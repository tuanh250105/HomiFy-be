package com.homifybackend.accountSetting_myListings.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "customers")
@PrimaryKeyJoinColumn(name = "user_id")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer extends User {

    @Column(name = "pipeline_status")
    private String pipelineStatus;

    @Column(name = "interest_score")
    private Integer interestScore;

    @Column(name = "is_hot_lead")
    private Boolean isHotLead;

    @Column(name = "demand")
    private String demand;
}
