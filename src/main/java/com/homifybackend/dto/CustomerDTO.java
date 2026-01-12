package com.homifybackend.dto;

import java.util.List;

public class CustomerDTO {
    private Long id;
    private String fullName;
    private String phoneNumber;
    private String email;
    private String pipelineStatus;
    private Integer interestScore;
    private Boolean isFavorite;
    private String demand;
    private List<AgentPropertyDTO> viewedHouses;

    public CustomerDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPipelineStatus() { return pipelineStatus; }
    public void setPipelineStatus(String pipelineStatus) { this.pipelineStatus = pipelineStatus; }

    public Integer getInterestScore() { return interestScore; }
    public void setInterestScore(Integer interestScore) { this.interestScore = interestScore; }

    public Boolean getIsFavorite() { return isFavorite; }
    public void setIsFavorite(Boolean isFavorite) { this.isFavorite = isFavorite; }

    public String getDemand() { return demand; }
    public void setDemand(String demand) { this.demand = demand; }

    public List<AgentPropertyDTO> getViewedHouses() { return viewedHouses; }
    public void setViewedHouses(List<AgentPropertyDTO> viewedHouses) {
        this.viewedHouses = viewedHouses;
    }
}
