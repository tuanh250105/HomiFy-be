package com.homifybackend.accountSetting_myListings.dto.account;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChangePasswordRequest {
    private String oldPassword;
    private String newPassword;
}
