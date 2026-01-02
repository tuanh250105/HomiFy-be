package com.homifybackend.agentsforcustomer.dto;

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

    @Pattern(regexp = "^\\+?[0-9]{10,20}$|^$", message = "Invalid phone number format")
    @Size(max = 20, message = "Phone number must not exceed 20 characters")
    private String senderPhone;

    @NotBlank(message = "Message is required")
    @Size(min = 10, max = 1000, message = "Message must be between 10 and 1000 characters")
    private String message;

    private Long propertyId;

    @Pattern(regexp = "SALE|RENTAL|^$", message = "Listing type must be SALE or RENTAL")
    private String listingType;
}