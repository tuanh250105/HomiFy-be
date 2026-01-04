package com.homifybackend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "customers")
@PrimaryKeyJoinColumn(name = "user_id")
public class Customer extends User {

    @Column(name = "pipeline_status")
    private String pipelineStatus;

    @Column(name = "interest_score")
    private Integer interestScore;

    @Column(name = "is_favorite")
    private Boolean isFavorite;

    private String demand;

    public Customer() {}
    public String getPipelineStatus() { return pipelineStatus; }
    public void setPipelineStatus(String pipelineStatus) { this.pipelineStatus = pipelineStatus; }
    public Integer getInterestScore() { return interestScore; }
    public void setInterestScore(Integer interestScore) { this.interestScore = interestScore; }
    public Boolean getIsFavorite() { return isFavorite; }
    public void setIsFavorite(Boolean isFavorite) { this.isFavorite = isFavorite; }
    public String getDemand() { return demand; }
    public void setDemand(String demand) { this.demand = demand; }
    public String getStatus() { return this.pipelineStatus; }
    public String getName() { return getFullName(); }
    public String getPhone() { return getPhoneNumber(); }
}
