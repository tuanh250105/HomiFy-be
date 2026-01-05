package com.homifybackend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Entity
@SuperBuilder
@Table(name = "customers")
@PrimaryKeyJoinColumn(name = "user_id")
@Getter
@Setter
@AllArgsConstructor
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
}