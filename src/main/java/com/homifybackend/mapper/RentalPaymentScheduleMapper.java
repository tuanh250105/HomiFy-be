package com.homifybackend.manageRentalPayments.mapper;

import com.homifybackend.manageRentalPayments.dto.RentalPaymentScheduleDTO;
import com.homifybackend.model.Address;
import com.homifybackend.model.Property;
import com.homifybackend.model.RentalContract;
import com.homifybackend.model.RentalPaymentSchedule;
import org.springframework.stereotype.Component;

@Component
public class RentalPaymentScheduleMapper {

  public RentalPaymentScheduleDTO toDTO(RentalPaymentSchedule entity) {
    RentalPaymentScheduleDTO dto = new RentalPaymentScheduleDTO();

    dto.setScheduleId(entity.getScheduleId());
    dto.setRentalContractId(entity.getRentalContractId());
    dto.setPeriodMonth(entity.getPeriodMonth());
    dto.setDueDate(entity.getDueDate());
    dto.setAmountDue(entity.getAmountDue());
    dto.setStatus(entity.getStatus() != null ? entity.getStatus().name() : null);
    dto.setMatchedTxnId(entity.getMatchedTxnId());
    dto.setMatchedAt(entity.getMatchedAt());
    dto.setNote(entity.getNote());

    // ✅ map UI fields từ DB qua quan hệ
    RentalContract c = entity.getRentalContract();
    if (c != null) {
      // tenantName: tùy Customer class của bạn, thường là getFullName()
      if (c.getTenant() != null) {
        dto.setTenantName(c.getTenant().getFullName());
      }

      if (c.getRentalListing() != null && c.getRentalListing().getProperty() != null) {
        Property p = c.getRentalListing().getProperty();
        dto.setPropertyId(p.getPropertyId()); // Property của bạn là propertyId

        Address a = p.getAddress();
        if (a != null) {
          // ghép địa chỉ: street, city, province (tùy Address của bạn có field gì)
          StringBuilder sb = new StringBuilder();
          if (a.getStreet() != null) sb.append(a.getStreet());
          if (a.getCity() != null) sb.append(sb.length() > 0 ? ", " : "").append(a.getCity());
          if (a.getProvince() != null) sb.append(sb.length() > 0 ? ", " : "").append(a.getProvince());
          dto.setPropertyAddress(sb.toString());
        }
      }
    }

    return dto;
  }
}
