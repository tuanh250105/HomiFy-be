package com.homifybackend.mapper;

import com.homifybackend.dto.AgentDTO;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
public class AgentMapper {

    public AgentDTO mapToDTO(Object[] row) {
        if (row == null) {
            System.err.println("ERROR: NULL row in mapToDTO");
            return null;
        }

        if (row.length != 19) {
            System.err.println("ERROR: Expected 19 columns, got " + row.length);
            return null;
        }

        try {
            String street = row[9] != null ? row[9].toString() : "";
            String city = row[10] != null ? row[10].toString() : "";
            String province = row[11] != null ? row[11].toString() : "";
            String zipCode = row[12] != null ? row[12].toString() : "";

            String fullAddress = buildFullAddress(street, city, province, zipCode);
            String[] specialties = extractSpecialties(row[17]);

            return AgentDTO.builder()
                    .agentId(getLongValue(row[0]))
                    .licenseId(getStringValue(row[1]))
                    .bio(getStringValue(row[2]))
                    .rate(getBigDecimalValue(row[3]))
                    .fullName(row[4] != null ? row[4].toString() : "Unknown Agent")
                    .phoneNumber(getStringValue(row[5]))
                    .avatarUrl(getStringValue(row[6]))
                    .gender(null)
                    .email(getStringValue(row[8]))
                    .street(street)
                    .city(city)
                    .province(province)
                    .zipCode(zipCode)
                    .nation(row[13] != null ? row[13].toString() : "USA")
                    .fullAddress(fullAddress)
                    .reviewCount(getIntegerValue(row[14]))
                    .averageRating(getDoubleValue(row[18]))
                    .saleListingsCount(getIntegerValue(row[15]))
                    .rentalListingsCount(getIntegerValue(row[16]))
                    .specialties(specialties)
                    .build();

        } catch (Exception e) {
            System.err.println("ERROR in mapToDTO: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private String buildFullAddress(String street, String city, String province, String zipCode) {
        StringBuilder address = new StringBuilder();
        if (!street.isEmpty()) address.append(street);
        if (!city.isEmpty()) {
            if (address.length() > 0) address.append(", ");
            address.append(city);
        }
        if (!province.isEmpty()) {
            if (address.length() > 0) address.append(", ");
            address.append(province);
        }
        if (!zipCode.isEmpty()) {
            if (address.length() > 0) address.append(" ");
            address.append(zipCode);
        }
        return address.toString();
    }

    private String[] extractSpecialties(Object specialtiesObj) {
        if (specialtiesObj == null) {
            return new String[0];
        }

        try {
            if (specialtiesObj instanceof String[]) {
                return (String[]) specialtiesObj;
            }

            if (specialtiesObj instanceof java.sql.Array) {
                Object arrayData = ((java.sql.Array) specialtiesObj).getArray();
                if (arrayData instanceof String[]) {
                    return (String[]) arrayData;
                }
                if (arrayData instanceof Object[]) {
                    Object[] objArray = (Object[]) arrayData;
                    String[] result = new String[objArray.length];
                    for (int i = 0; i < objArray.length; i++) {
                        result[i] = objArray[i] != null ? objArray[i].toString() : "";
                    }
                    return result;
                }
            }
        } catch (Exception e) {
            System.err.println("ERROR extracting specialties: " + e.getMessage());
        }

        return new String[0];
    }

    private Long getLongValue(Object obj) {
        if (obj == null) return null;
        if (obj instanceof Number) return ((Number) obj).longValue();
        return null;
    }

    private Integer getIntegerValue(Object obj) {
        if (obj == null) return 0;
        if (obj instanceof Number) return ((Number) obj).intValue();
        return 0;
    }

    private Double getDoubleValue(Object obj) {
        if (obj == null) return 0.0;
        if (obj instanceof Number) return ((Number) obj).doubleValue();
        return 0.0;
    }

    private BigDecimal getBigDecimalValue(Object obj) {
        if (obj == null) return BigDecimal.ZERO;
        return new BigDecimal(obj.toString());
    }

    private String getStringValue(Object obj) {
        return obj != null ? obj.toString() : null;
    }
}