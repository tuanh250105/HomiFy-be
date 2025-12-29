package com.homify.repository;

import com.homify.model.Otp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.Optional;

@Repository
public interface OtpRepository extends JpaRepository<Otp, Long> {
    Optional<Otp> findByEmailAndOtpCodeAndOtpTypeAndIsUsedFalse(String email, String otpCode, String otpType);
    
    @Modifying
    @Query("UPDATE Otp o SET o.isUsed = true WHERE o.email = :email AND o.otpType = :otpType")
    void markAllAsUsedByEmailAndType(@Param("email") String email, @Param("otpType") String otpType);
    
    @Modifying
    @Query("DELETE FROM Otp o WHERE o.expiresAt < :now")
    void deleteExpiredOtps(@Param("now") Timestamp now);
}

