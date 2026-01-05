package com.homifybackend.service.agentSurveyTask;

import com.homifybackend.dto.survey.MySurveyTaskDto;
import com.homifybackend.dto.survey.OpportunityPoolDto;
import com.homifybackend.model.Address;
import com.homifybackend.model.Customer;
import com.homifybackend.model.SellRequest;
import com.homifybackend.model.SellRequestStatus;
import com.homifybackend.model.SurveyTask;
import com.homifybackend.repository.surveyTaskRepository.SellRequestRepository;
import com.homifybackend.repository.surveyTaskRepository.SurveyTaskRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class SurveyTaskService {

    private final SurveyTaskRepository surveyTaskRepository;
    private final SellRequestRepository sellRequestRepository;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public SurveyTaskService(SurveyTaskRepository surveyTaskRepository,
                             SellRequestRepository sellRequestRepository) {
        this.surveyTaskRepository = surveyTaskRepository;
        this.sellRequestRepository = sellRequestRepository;
    }

    // =========================
// 1) OPPORTUNITY POOL
// =========================
    @Transactional(readOnly = true)
    public List<OpportunityPoolDto> getOpportunityPool(String district) {

        List<SellRequest> pending;

        if (district == null || district.isBlank() || district.equalsIgnoreCase("ALL")) {
            pending = sellRequestRepository
                    .findOpportunityPoolByStatus(SellRequestStatus.PENDING);
        } else {
            // ⚠️ yêu cầu repo có method này (bên dưới mình nói)
            pending = sellRequestRepository
                    .findOpportunityPoolByStatusAndDistrict(
                            SellRequestStatus.PENDING,
                            district
                    );
        }

        return pending.stream().map(sr -> {
            Long addressId = null;
            String fullAddress = "Chưa cập nhật";
            String city = "N/A";
            String province = "N/A";

            Address addr = sr.getAddress();
            if (addr != null) {
                addressId = addr.getAddressId();
                String street = addr.getStreet() != null ? addr.getStreet() : "";
                city = addr.getCity() != null ? addr.getCity() : "N/A";
                province = addr.getProvince() != null ? addr.getProvince() : "N/A";
                fullAddress = street.isBlank() ? city : (street + ", " + city);
            }

            String ownerName = "Khách ẩn danh";
            Customer owner = sr.getOwner();
            if (owner != null && owner.getFullName() != null && !owner.getFullName().isBlank()) {
                ownerName = owner.getFullName();
            }

            String createdAt = sr.getCreatedAt() != null
                    ? sr.getCreatedAt().format(FMT)
                    : null;

            String statusStr = sr.getStatus() != null
                    ? sr.getStatus().name()
                    : "PENDING";

            return new OpportunityPoolDto(
                    sr.getId(),
                    statusStr,
                    createdAt,
                    addressId,
                    sr.getEstBeds() != null ? sr.getEstBeds() : 0,
                    sr.getEstBaths() != null ? sr.getEstBaths() : 0,
                    sr.getEstimatedArea() != null ? sr.getEstimatedArea() : 0.0,
                    fullAddress,
                    city,
                    province,
                    ownerName
            );
        }).toList();
    }


    // =========================
    // 2) MY TASKS
    // =========================
    @Transactional(readOnly = true)
    public List<MySurveyTaskDto> getMyTasks(Long agentId) {
        List<SurveyTask> tasks = surveyTaskRepository.findByAgentId(agentId);

        return tasks.stream().map(task -> {
            SellRequest sr = task.getSellRequest();

            Long addressId = null;
            Integer beds = 0;
            Integer baths = 0;
            Double area = 0.0;

            if (sr != null) {
                beds = sr.getEstBeds() != null ? sr.getEstBeds() : 0;
                baths = sr.getEstBaths() != null ? sr.getEstBaths() : 0;
                area = sr.getEstimatedArea() != null ? sr.getEstimatedArea() : 0.0;

                if (sr.getAddress() != null) {
                    addressId = sr.getAddress().getAddressId();
                }
            }

            String createdAt = task.getCreatedAt() != null ? task.getCreatedAt().format(FMT) : null;
            String scheduledAt = task.getScheduledAt() != null ? task.getScheduledAt().format(FMT) : null;

            return new MySurveyTaskDto(
                    task.getId(),
                    sr != null ? sr.getId() : null,
                    task.getTaskStatus(),
                    createdAt,
                    scheduledAt,
                    addressId,
                    beds,
                    baths,
                    area,
                    task.getNote()
            );
        }).toList();
    }

    // =========================
    // 3) CLAIM ONE
    // =========================
    @Transactional
    public void claimSurvey(Long sellRequestId, Long agentId) {

        SellRequest sr = sellRequestRepository.findById(sellRequestId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy Sell Request"));

        // Fix: Compare Enum directly
        if (sr.getStatus() != SellRequestStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Request không còn khả dụng để nhận");
        }

        try {
            SurveyTask task = new SurveyTask();
            task.setSellRequest(sr);
            task.setAgentId(agentId);
            task.setTaskStatus("CLAIMED");
            surveyTaskRepository.saveAndFlush(task);

        } catch (DataIntegrityViolationException ex) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Yêu cầu này đã có người nhận!");
        }
    }

    // =========================
    // 4) CLAIM ALL
    // =========================
    @Transactional
    public int claimAll(Long agentId) {
        // Fix: Pass Enum directly
        List<SellRequest> pool = sellRequestRepository.findOpportunityPoolByStatus(SellRequestStatus.PENDING);

        int count = 0;
        for (SellRequest sr : pool) {
            try {
                SurveyTask task = new SurveyTask();
                task.setSellRequest(sr);
                task.setAgentId(agentId);
                task.setTaskStatus("CLAIMED");
                surveyTaskRepository.save(task);
                count++;
            } catch (DataIntegrityViolationException ex) {
                // skip
            }
        }
        return count;
    }

    // =========================
    // 5) SCHEDULE
    // =========================
    @Transactional
    public void schedule(Long taskId, LocalDateTime scheduledAt) {
        SurveyTask task = surveyTaskRepository.findById(taskId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy Task"));

        task.setScheduledAt(scheduledAt);
        task.setTaskStatus("SCHEDULED");

        surveyTaskRepository.save(task);
    }

    // =========================
    // 6) UPDATE NOTE
    // =========================
    @Transactional
    public void updateNote(Long taskId, String note) {
        SurveyTask task = surveyTaskRepository.findById(taskId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy Task"));

        task.setNote(note);
        // Có thể update thêm status nếu cần, ví dụ:
        // task.setTaskStatus("COMPLETED");
        
        surveyTaskRepository.save(task);
    }
}