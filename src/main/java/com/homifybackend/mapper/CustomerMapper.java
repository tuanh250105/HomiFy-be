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

        if (c.getAccount() != null) {
            dto.setEmail(c.getAccount().getEmail());
        }

        dto.setPipelineStatus(c.getPipelineStatus());
        dto.setInterestScore(c.getInterestScore());
        dto.setIsFavorite(c.getIsFavorite());
        dto.setDemand(c.getDemand());

        // Chuyển đổi danh sách nhà đã xem
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
        dto.setBeds(p.getBeds());
        dto.setBaths(p.getBaths());
        dto.setFloors(p.getFloors());
        dto.setDescription(p.getDescription());
        dto.setYearBuilt(p.getYearBuilt());
        dto.setPropertyType(p.getPropertyType());

        // Xử lý địa chỉ
        if (p.getAddress() != null) {
            String street = p.getAddress().getStreet() != null ? p.getAddress().getStreet() : "";
            String city = p.getAddress().getCity() != null ? p.getAddress().getCity() : "";
            dto.setStreet(street);
            dto.setCity(city);

            if (!street.isEmpty() && !city.isEmpty()) {
                dto.setAddress(street + ", " + city);
            } else {
                dto.setAddress(street + city);
            }
        } else {
            dto.setAddress("No address");
        }

        if (p.getSaleListing() != null && p.getSaleListing().getCurrentPrice() != null) {
            dto.setPrice(p.getSaleListing().getCurrentPrice().doubleValue());
        } else {
            dto.setPrice(null);
        }

        return dto;
    }
}