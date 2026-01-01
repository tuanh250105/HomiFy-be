package com.homifybackend.explore_options.service;

import com.homifybackend.explore_options.dto.SellRequestCreateDTO;
import com.homifybackend.explore_options.dto.SellRequestResponseDTO;
import com.homifybackend.explore_options.repository.AddressRepository;
import com.homifybackend.explore_options.repository.SellRequestRepository;
import com.homifybackend.model.Address;
import com.homifybackend.model.SellRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class ExploreOptionsService {

    private final SellRequestRepository sellRequestRepository;
    private final AddressRepository addressRepository;

    public ExploreOptionsService(
            SellRequestRepository sellRequestRepository,
            AddressRepository addressRepository
    ) {
        this.sellRequestRepository = sellRequestRepository;
        this.addressRepository = addressRepository;
    }

    @Transactional
    public SellRequestResponseDTO createSellRequest(SellRequestCreateDTO dto) {
        if (dto.getOwnerId() == null) {
            throw new IllegalArgumentException("ownerId is required");
        }

        Address address = null;
        if (dto.getAddress() != null && !dto.getAddress().trim().isEmpty()) {
            address = new Address();
            address.setStreet(dto.getAddress().trim());
            address = addressRepository.save(address);
        }

        SellRequest sr = new SellRequest();
        sr.setOwnerId(dto.getOwnerId());
        sr.setAddress(address);

        sr.setEstBeds(nvl(dto.getBedrooms()));

        // FIX: estBaths = full + half*0.5 + threeQuarter*0.75, rounded
        int full = nvl(dto.getBathroomsFull());
        int half = nvl(dto.getBathroomsHalf());
        int tq = nvl(dto.getBathroomsThreeQuarter());
        double baths = full + 0.5 * half + 0.75 * tq;
        sr.setEstBaths((int) Math.round(baths));

        sr.setFloors(nvl(dto.getFloors()));
        sr.setHasBasement("yes".equalsIgnoreCase(dto.getBasementHas()));

        sr.setLivingRoomCondition(dto.getLivingRoomQuality());
        sr.setKitchenCondition(dto.getKitchenQuality());
        sr.setInteriorCondition(dto.getMainBathroomQuality());
        sr.setExteriorCondition(dto.getExteriorQuality());

        sr.setEstimatedArea(dto.getLivingArea());

        // FIX: chỉ dùng buildNotes, không gọi buildNotesJson
        sr.setNeededRepairNotes(buildNotes(dto));

        sr = sellRequestRepository.save(sr);

        return toResponse(sr, dto.getAddress());
    }

    public SellRequestResponseDTO getSellRequest(Long id) {
        SellRequest sr = sellRequestRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("SellRequest not found: " + id));
        String addrText = (sr.getAddress() != null) ? sr.getAddress().getStreet() : null;
        return toResponse(sr, addrText);
    }

    public List<SellRequestResponseDTO> listByOwner(Long ownerId) {
        List<SellRequest> list = sellRequestRepository.findByOwnerIdOrderByCreatedAtDesc(ownerId);
        List<SellRequestResponseDTO> out = new ArrayList<>();
        for (SellRequest sr : list) {
            String addrText = (sr.getAddress() != null) ? sr.getAddress().getStreet() : null;
            out.add(toResponse(sr, addrText));
        }
        return out;
    }

    // Notes: KHÔNG LẶP basement
    private String buildNotes(SellRequestCreateDTO dto) {
        StringBuilder sb = new StringBuilder();

        append(sb, "homeType", dto.getHomeType());
        append(sb, "bedrooms", dto.getBedrooms());
        append(sb, "bathroomsFull", dto.getBathroomsFull());
        append(sb, "bathroomsHalf", dto.getBathroomsHalf());
        append(sb, "bathroomsThreeQuarter", dto.getBathroomsThreeQuarter());
        append(sb, "livingArea", dto.getLivingArea());
        append(sb, "floors", dto.getFloors());

        append(sb, "countertops", dto.getCountertops());
        append(sb, "countertopsOther", dto.getCountertopsOther());

        // Basement (ONLY ONCE)
        append(sb, "basementHas", dto.getBasementHas());
        append(sb, "basementKnowSqft", dto.getBasementKnowSqft());
        append(sb, "basementFinishedSqft", dto.getBasementFinishedSqft());
        append(sb, "basementUnfinishedSqft", dto.getBasementUnfinishedSqft());

        append(sb, "exteriorQuality", dto.getExteriorQuality());
        append(sb, "livingRoomQuality", dto.getLivingRoomQuality());
        append(sb, "kitchenQuality", dto.getKitchenQuality());
        append(sb, "mainBathroomQuality", dto.getMainBathroomQuality());

        if (dto.getIssues() != null && !dto.getIssues().isEmpty()) {
            sb.append("issues=").append(dto.getIssues()).append("; ");
        }

        if (dto.getContact() != null) {
            SellRequestCreateDTO.ContactDTO c = dto.getContact();
            sb.append("contact={")
                    .append("firstName=").append(nullToEmpty(c.getFirstName())).append(", ")
                    .append("lastName=").append(nullToEmpty(c.getLastName())).append(", ")
                    .append("email=").append(nullToEmpty(c.getEmail())).append(", ")
                    .append("phoneNumber=").append(nullToEmpty(c.getPhoneNumber())).append(", ")
                    .append("accepted=").append(c.getAccepted())
                    .append("}; ");
        }

        return sb.toString();
    }

    private void append(StringBuilder sb, String key, Object val) {
        sb.append(key).append("=").append(val == null ? "null" : val).append("; ");
    }

    private String nullToEmpty(String s) {
        return s == null ? "" : s;
    }

    private SellRequestResponseDTO toResponse(SellRequest sr, String addressText) {
        SellRequestResponseDTO r = new SellRequestResponseDTO();
        r.setId(sr.getId());
        r.setOwnerId(sr.getOwnerId());
        r.setAddressId(sr.getAddress() != null ? sr.getAddress().getAddressId() : null);
        r.setAddressText(addressText);
        r.setEstBeds(sr.getEstBeds());
        r.setEstBaths(sr.getEstBaths());
        r.setFloors(sr.getFloors());
        r.setHasBasement(sr.getHasBasement());
        r.setEstimatedArea(sr.getEstimatedArea());
        r.setStatus(sr.getStatus());
        r.setCreatedAt(sr.getCreatedAt());

        // nếu DTO bạn đã thêm các field này thì giữ lại
        r.setNeededRepairNotes(sr.getNeededRepairNotes());
        r.setExteriorCondition(sr.getExteriorCondition());
        r.setLivingRoomCondition(sr.getLivingRoomCondition());
        r.setKitchenCondition(sr.getKitchenCondition());
        r.setInteriorCondition(sr.getInteriorCondition());

        return r;
    }

    private Integer nvl(Integer v) { return v == null ? 0 : v; }
}
