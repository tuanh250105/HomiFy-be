package com.homifybackend.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateContactRequest {

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String senderName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String senderEmail;

    // FIXED: Relaxed phone validation to accept common formats like (123) 456-7890, +1-234-567-8900, etc.
    // Accepts: digits, spaces, dashes, parentheses, plus sign
    // Optional field (can be empty)
    @Pattern(
            regexp = "^[\\+]?[(]?[0-9]{1,4}[)]?[-\\s\\.]?[(]?[0-9]{1,4}[)]?[-\\s\\.]?[0-9]{1,9}[-\\s\\.]?[0-9]{0,9}$|^$",
            message = "Invalid phone number format"
    )
    @Size(max = 30, message = "Phone number must not exceed 30 characters")
    private String senderPhone;

    @NotBlank(message = "Message is required")
    @Size(min = 10, max = 1000, message = "Message must be between 10 and 1000 characters")
    private String message;

    private Long propertyId;

    @Pattern(regexp = "SALE|RENTAL|^$", message = "Listing type must be SALE or RENTAL")
    private String listingType;
}