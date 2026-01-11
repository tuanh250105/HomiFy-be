package com.homifybackend.auth.service;

import java.security.SecureRandom;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.homifybackend.auth.config.AuthConstants;
import com.homifybackend.auth.repository.OtpRepository;
import com.homifybackend.model.Otp;

@Service
public class OtpService {

    private static final Logger logger = LoggerFactory.getLogger(OtpService.class);

    @Autowired
    private OtpRepository otpRepository;

    @Autowired
    private EmailService emailService;

    @Value("${app.otp.expiration-minutes:10}")
    private Integer expirationMinutes;

    @Value("${app.otp.length:6}")
    private Integer otpLength;

    private static final SecureRandom random = new SecureRandom();
    private static final String OTP_CHARACTERS = "0123456789";

    public String generateOtp() {
        StringBuilder otp = new StringBuilder(otpLength);
        for (int i = 0; i < otpLength; i++) {
            otp.append(OTP_CHARACTERS.charAt(random.nextInt(OTP_CHARACTERS.length())));
        }
        return otp.toString();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Otp createAndSendOtp(String email, String otpType) {
        // Mark old OTPs as used
        otpRepository.markAllAsUsedByEmailAndType(email, otpType);

        // Generate new OTP
        String otpCode = generateOtp();
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(expirationMinutes);

        // Save OTP to database
        Otp otp = Otp.builder()
                .email(email)
                .otpCode(otpCode)
                .otpType(otpType)
                .expiresAt(Timestamp.valueOf(expiresAt))
                .isUsed(false)
                .build();

        Otp savedOtp = otpRepository.save(otp);

        // Send OTP via email
        logger.debug("Sending OTP email to: {}, type: {}", email, otpType);
        
        try {
            emailService.sendOtpEmail(email, otpCode, otpType);
            logger.debug("OTP email sent successfully to: {}", email);
        } catch (Exception e) {
            // Log email error
            logger.error("Failed to send OTP email to: {}, type: {}", email, otpType, e);
            throw new RuntimeException(AuthConstants.ErrorMessage.OTP_SEND_FAILED, e);
        }

        return savedOtp;
    }

    @Transactional
    public boolean verifyOtp(String email, String otpCode, String otpType) {
        Optional<Otp> otpOptional = otpRepository.findByEmailAndOtpCodeAndOtpTypeAndIsUsedFalse(
                email, otpCode, otpType);

        if (otpOptional.isEmpty()) {
            return false;
        }

        Otp otp = otpOptional.get();

        // Check if OTP is expired
        if (otp.getExpiresAt().before(Timestamp.valueOf(LocalDateTime.now()))) {
            return false;
        }

        // Mark OTP as used
        otp.setIsUsed(true);
        otpRepository.save(otp);

        return true;
    }
}

