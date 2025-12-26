package com.homifybackend.dto;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddressDTO {

    private String city;
    private String province;
    private String street;
    private Double latitude;
    private Double longitude;


    // constructor + getter/setter

}
