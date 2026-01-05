package com.homifybackend.dto;


import java.util.List;

public class CustomerToursResponseDTO {
    private List<CustomerTourItemDTO> upcoming;
    private List<CustomerTourItemDTO> past;

    public CustomerToursResponseDTO(List<CustomerTourItemDTO> upcoming, List<CustomerTourItemDTO> past) {
        this.upcoming = upcoming;
        this.past = past;
    }

    public List<CustomerTourItemDTO> getUpcoming() { return upcoming; }
    public List<CustomerTourItemDTO> getPast() { return past; }
}
