package com.homifybackend.service.ExploreOptionsService;

import com.homifybackend.dto.SellRequestCreateDTO;
import com.homifybackend.dto.SellRequestResponseDTO;
import com.homifybackend.repository.AddressRepository;
import com.homifybackend.repository.SellRequestRepository;
import com.homifybackend.model.Address;
import com.homifybackend.model.Customer;
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

        // FIX: SellRequest có owner là Customer (FK sell_requests.owner_id -> customers.user_id)
        // Tạo "reference" customer chỉ set id (miễn là customer id tồn tại trong DB)
        Customer owner = new Customer();
        // Nếu Customer class của bạn dùng field khác tên userId thì đổi dòng này cho đúng.
        owner.setUserId(dto.getOwnerId());
        sr.setOwner(owner);

        sr.setAddress(address);

        sr.setEstBeds(nvl(dto.getBedrooms()));

        // estBaths = full + half*0.5 + threeQuarter*0.75, rounded -> int
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

        // notes
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
        List<SellRequest> list = sellRequestRepository.findByOwner_UserIdOrderByCreatedAtDesc(ownerId);
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

        // FIX: ownerId lấy từ sr.getOwner().getUserId()
        r.setOwnerId(sr.getOwner() != null ? sr.getOwner().getUserId() : null);

        r.setAddressId(sr.getAddress() != null ? sr.getAddress().getAddressId() : null);
        r.setAddressText(addressText);
        r.setEstBeds(sr.getEstBeds());
        r.setEstBaths(sr.getEstBaths());
        r.setFloors(sr.getFloors());
        r.setHasBasement(sr.getHasBasement());
        r.setEstimatedArea(sr.getEstimatedArea());

        // FIX: status là enum -> trả string
        r.setStatus(sr.getStatus() != null ? sr.getStatus().name() : null);

        r.setCreatedAt(sr.getCreatedAt());

        r.setNeededRepairNotes(sr.getNeededRepairNotes());
        r.setExteriorCondition(sr.getExteriorCondition());
        r.setLivingRoomCondition(sr.getLivingRoomCondition());
        r.setKitchenCondition(sr.getKitchenCondition());
        r.setInteriorCondition(sr.getInteriorCondition());

        return r;
    }

    private Integer nvl(Integer v) { return v == null ? 0 : v; }
}
