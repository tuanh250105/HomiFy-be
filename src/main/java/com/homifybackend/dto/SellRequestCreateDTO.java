package com.homifybackend.dto;

import java.util.Map;

public class SellRequestCreateDTO {
    private Long ownerId;

    // FE dùng 1 string address; ta nhét vào street nếu không có breakdown
    private String address;

    private Integer bedrooms;
    private Integer bathroomsFull;
    private Integer bathroomsThreeQuarter;
    private Integer bathroomsHalf;

    private Double livingArea;
    private String homeType;
    private Integer floors;

    private String basementHas;      // "yes" | "no" | null
    private String basementKnowSqft; // "yes" | "no" | null

    private Double basementFinishedSqft;
    private Double basementUnfinishedSqft;

    private String exteriorQuality;
    private String livingRoomQuality;
    private String kitchenQuality;
    private String mainBathroomQuality;

    private String countertops;
    private String countertopsOther;

    private Map<String, Boolean> issues; // flooding/foundation/electrical/plumbing/roof

    private ContactDTO contact;

    public static class ContactDTO {
        private String firstName;
        private String lastName;
        private String email;
        private String phoneNumber;
        private Boolean accepted;

        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPhoneNumber() { return phoneNumber; }
        public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
        public Boolean getAccepted() { return accepted; }
        public void setAccepted(Boolean accepted) { this.accepted = accepted; }
    }

    // getters/setters

    public Long getOwnerId() { return ownerId; }
    public void setOwnerId(Long ownerId) { this.ownerId = ownerId; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public Integer getBedrooms() { return bedrooms; }
    public void setBedrooms(Integer bedrooms) { this.bedrooms = bedrooms; }

    public Integer getBathroomsFull() { return bathroomsFull; }
    public void setBathroomsFull(Integer bathroomsFull) { this.bathroomsFull = bathroomsFull; }

    public Integer getBathroomsThreeQuarter() { return bathroomsThreeQuarter; }
    public void setBathroomsThreeQuarter(Integer bathroomsThreeQuarter) { this.bathroomsThreeQuarter = bathroomsThreeQuarter; }

    public Integer getBathroomsHalf() { return bathroomsHalf; }
    public void setBathroomsHalf(Integer bathroomsHalf) { this.bathroomsHalf = bathroomsHalf; }

    public Double getLivingArea() { return livingArea; }
    public void setLivingArea(Double livingArea) { this.livingArea = livingArea; }

    public String getHomeType() { return homeType; }
    public void setHomeType(String homeType) { this.homeType = homeType; }

    public Integer getFloors() { return floors; }
    public void setFloors(Integer floors) { this.floors = floors; }

    public String getBasementHas() { return basementHas; }
    public void setBasementHas(String basementHas) { this.basementHas = basementHas; }

    public String getBasementKnowSqft() { return basementKnowSqft; }
    public void setBasementKnowSqft(String basementKnowSqft) { this.basementKnowSqft = basementKnowSqft; }

    public Double getBasementFinishedSqft() { return basementFinishedSqft; }
    public void setBasementFinishedSqft(Double basementFinishedSqft) { this.basementFinishedSqft = basementFinishedSqft; }

    public Double getBasementUnfinishedSqft() { return basementUnfinishedSqft; }
    public void setBasementUnfinishedSqft(Double basementUnfinishedSqft) { this.basementUnfinishedSqft = basementUnfinishedSqft; }

    public String getExteriorQuality() { return exteriorQuality; }
    public void setExteriorQuality(String exteriorQuality) { this.exteriorQuality = exteriorQuality; }

    public String getLivingRoomQuality() { return livingRoomQuality; }
    public void setLivingRoomQuality(String livingRoomQuality) { this.livingRoomQuality = livingRoomQuality; }

    public String getKitchenQuality() { return kitchenQuality; }
    public void setKitchenQuality(String kitchenQuality) { this.kitchenQuality = kitchenQuality; }

    public String getMainBathroomQuality() { return mainBathroomQuality; }
    public void setMainBathroomQuality(String mainBathroomQuality) { this.mainBathroomQuality = mainBathroomQuality; }

    public String getCountertops() { return countertops; }
    public void setCountertops(String countertops) { this.countertops = countertops; }

    public String getCountertopsOther() { return countertopsOther; }
    public void setCountertopsOther(String countertopsOther) { this.countertopsOther = countertopsOther; }

    public Map<String, Boolean> getIssues() { return issues; }
    public void setIssues(Map<String, Boolean> issues) { this.issues = issues; }

    public ContactDTO getContact() { return contact; }
    public void setContact(ContactDTO contact) { this.contact = contact; }
}
