package com.homifybackend.model;

import jakarta.persistence.*;
import java.util.List;

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

    // Trường bổ sung để chứa danh sách nhà đã xem từ TourRequest
    // JPA bỏ qua trường này khi tạo bảng trong Database
    @Transient
    private List<Property> viewedHouses;

    @Transient
    private String email;

    public Customer() {}
    public List<Property> getViewedHouses() {
        return viewedHouses;
    }
    public void setViewedHouses(List<Property> viewedHouses) {
        this.viewedHouses = viewedHouses;
    }
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

    public String getEmail() {
        if (this.email == null && this.getAccount() != null) {
            return this.getAccount().getEmail();
        }
        return this.email;
    }
    public void setEmail(String email) { this.email = email; }
}