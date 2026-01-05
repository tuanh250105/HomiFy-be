package com.homifybackend.mapper;

import com.homifybackend.dto.CustomerDTO;
import com.homifybackend.dto.AgentPropertyDTO;
import com.homifybackend.model.Customer;
import com.homifybackend.model.Property;

import java.util.stream.Collectors;

public class CustomerMapper {

    public static CustomerDTO toDTO(Customer c) {
        if (c == null) return null;

        CustomerDTO dto = new CustomerDTO();

        dto.setId(c.getUserId());
        dto.setFullName(c.getFullName());
        dto.setPhoneNumber(c.getPhoneNumber());
        dto.setPipelineStatus(c.getPipelineStatus());
        dto.setInterestScore(c.getInterestScore());
        dto.setIsFavorite(c.getIsFavorite());
        dto.setDemand(c.getDemand());
        if (c.getViewedHouses() != null) {
            dto.setViewedHouses(
                    c.getViewedHouses()
                            .stream()
                            .map(CustomerMapper::toPropertyDTO)
                            .collect(Collectors.toList())
            );
        }

        return dto;
    }

    public static AgentPropertyDTO toPropertyDTO(Property p) {
        if (p == null) return null;

        AgentPropertyDTO dto = new AgentPropertyDTO();

        dto.setId(p.getPropertyId());
        dto.setArea(p.getArea());

        if (p.getAddress() != null) {
            dto.setCity(p.getAddress().getCity());
            dto.setStreet(p.getAddress().getStreet());
        }

        if (p.getSaleListing() != null && p.getSaleListing().getCurrentPrice() != null) {
            dto.setPrice(p.getSaleListing().getCurrentPrice().doubleValue());
        } else {
            dto.setPrice(null);
        }

        return dto;
    }
}
