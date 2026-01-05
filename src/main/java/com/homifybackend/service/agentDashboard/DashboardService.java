package com.homifybackend.service.agentDashboard;

import com.homifybackend.dto.dashboard.*;
import com.homifybackend.model.*;
import com.homifybackend.repository.dashboard.*; 
import com.homifybackend.repository.surveyTaskRepository.AgentRepository;
import com.homifybackend.repository.surveyTaskRepository.SurveyTaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DashboardService {

    private final AgentRepository agentRepository;
    private final SaleListingRepository saleListingRepository;
    private final SaleContractRepository saleContractRepository;
    private final AgentLeadRepository agentLeadRepository;
    private final LeadNoteRepository leadNoteRepository;
    private final TourRequestRepository tourRequestRepository;
    private final SaleListingDailyStatsRepository statsRepository;
    private final SurveyTaskRepository surveyTaskRepository;
    private final AgentNoteRepository agentNoteRepository;

    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public DashboardService(AgentRepository agentRepository,
                            SaleListingRepository saleListingRepository,
                            SaleContractRepository saleContractRepository,
                            AgentLeadRepository agentLeadRepository,
                            LeadNoteRepository leadNoteRepository,
                            TourRequestRepository tourRequestRepository,
                            SaleListingDailyStatsRepository statsRepository,
                            SurveyTaskRepository surveyTaskRepository,
                            AgentNoteRepository agentNoteRepository) {
        this.agentRepository = agentRepository;
        this.saleListingRepository = saleListingRepository;
        this.saleContractRepository = saleContractRepository;
        this.agentLeadRepository = agentLeadRepository;
        this.leadNoteRepository = leadNoteRepository;
        this.tourRequestRepository = tourRequestRepository;
        this.statsRepository = statsRepository;
        this.surveyTaskRepository = surveyTaskRepository;
        this.agentNoteRepository = agentNoteRepository;
    }

    // ==================================================================
    // 1. AGENT PROFILE
    // ==================================================================
    @Transactional(readOnly = true)
    public AgentProfileDto getAgentProfile(long agentId) {
        Agent agent = agentRepository.findById(agentId).orElse(null);
        if (agent == null) return null;

        long totalListings = saleListingRepository.countByAgent_UserIdAndSaleStatus(agentId, SaleListingStatus.ACTIVE); 
        long allListings = saleListingRepository.findByAgent_UserId(agentId).size();
        long soldDeals = saleContractRepository.countByAgentId(agentId);

        String addressStr = "";
        if (agent.getAddress() != null) {
            String street = agent.getAddress().getStreet() != null ? agent.getAddress().getStreet() : "";
            String city = agent.getAddress().getCity() != null ? agent.getAddress().getCity() : "";
            addressStr = street + (street.isEmpty() || city.isEmpty() ? "" : ", ") + city;
        }

        Double rateVal = agent.getRate() != null ? agent.getRate().doubleValue() : 0.0;
        
        String email = (agent.getAccount() != null) ? agent.getAccount().getEmail() : "";

        return new AgentProfileDto(
                agent.getUserId(),
                agent.getFullName(),
                email,
                agent.getPhoneNumber(),
                agent.getAvatarUrl(),
                agent.getRegistrationDate() != null ? agent.getRegistrationDate().toString() : "",
                agent.getLicenseId(),
                agent.getBio(),
                rateVal,
                addressStr,
                (int) allListings,
                (int) soldDeals
        );
    }

    // ==================================================================
    // 2. OVERVIEW
    // ==================================================================
    @Transactional(readOnly = true)
    public OverviewDto getOverview(long agentId) {
        long activeListings = saleListingRepository.countByAgent_UserIdAndSaleStatus(agentId, SaleListingStatus.ACTIVE);
        long soldDeals = saleContractRepository.countByAgentId(agentId);
        
        Double avgDays = saleContractRepository.getAvgDaysToSell(agentId);
        int avgDaysInt = avgDays != null ? (int) Math.round(avgDays) : 0;

        long newLeads = agentLeadRepository.countByAgentIdAndLeadStatus(agentId, "NEW");

        LocalDate today = LocalDate.now();
        long todayTours = tourRequestRepository.countTodayTours(agentId, today.toString());
        
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.plusDays(1).atStartOfDay();
        long todaySurveys = surveyTaskRepository.findTasksByAgentIdAndDate(agentId, startOfDay, endOfDay).size();

        return new OverviewDto(
                (int) activeListings,
                (int) soldDeals,
                avgDaysInt,
                (int) newLeads,
                (int) (todayTours + todaySurveys)
        );
    }

    // ==================================================================
    // 3. PERFORMANCE 7 DAYS
    // ==================================================================
    @Transactional(readOnly = true)
    public Performance7dDto getPerformance7d(long agentId) {
        List<String> labels = new ArrayList<>();
        LocalDate today = LocalDate.now();
        LocalDate startDate = today.minusDays(6);

        for (int i = 6; i >= 0; i--) {
            labels.add(today.minusDays(i).toString());
        }

        List<Object[]> newListingsData = saleListingRepository.countNewListingsByDate(agentId, startDate.atStartOfDay());
        Map<String, Integer> nlMap = new HashMap<>();
        for (Object[] row : newListingsData) {
            LocalDateTime dt = (LocalDateTime) row[0];
            nlMap.put(dt.toLocalDate().toString(), ((Number) row[1]).intValue());
        }

        List<Object[]> viewsData = statsRepository.sumViewsByDate(agentId, startDate);
        Map<String, Integer> vMap = new HashMap<>();
        for (Object[] row : viewsData) {
            LocalDate d = (LocalDate) row[0];
            vMap.put(d.toString(), ((Number) row[1]).intValue());
        }

        List<Integer> nlSeries = new ArrayList<>();
        List<Integer> vSeries = new ArrayList<>();

        for (String date : labels) {
            nlSeries.add(nlMap.getOrDefault(date, 0));
            vSeries.add(vMap.getOrDefault(date, 0));
        }

        return new Performance7dDto(labels, nlSeries, vSeries);
    }

    // ==================================================================
    // 4. TODAY TASKS (Tours + Surveys)
    // ==================================================================
    @Transactional(readOnly = true)
    public TodayDto getToday(long agentId) {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.plusDays(1).atStartOfDay();

        // 1. Tours
        List<Tour> tours = tourRequestRepository.findTodayTours(agentId, today.toString());
        List<TodayTaskDto> tourDtos = tours.stream().map(tr -> {
            String addr = "No Address";
            String district = "";
            if (tr.getSaleListing() != null && tr.getSaleListing().getProperty() != null) {
                Address a = tr.getSaleListing().getProperty().getAddress();
                if (a != null) {
                    addr = (a.getStreet() != null ? a.getStreet() + ", " : "") + a.getCity();
                    district = a.getProvince();
                }
            }
            return new TodayTaskDto(
                    tr.getId(), "TOUR", tr.getStatus(), tr.getDate(), tr.getTime(),
                    null, tr.getSaleListing().getId(), null, addr, district
            );
        }).toList();

        // 2. Surveys
        List<SurveyTask> surveys = surveyTaskRepository.findTasksByAgentIdAndDate(agentId, startOfDay, endOfDay);
        List<TodayTaskDto> surveyDtos = surveys.stream().map(st -> {
            String addr = "No Address";
            String district = "";
            if (st.getSellRequest() != null && st.getSellRequest().getAddress() != null) {
                Address a = st.getSellRequest().getAddress();
                addr = (a.getStreet() != null ? a.getStreet() + ", " : "") + a.getCity();
                district = a.getProvince();
            }
            return new TodayTaskDto(
                    st.getId(), "SURVEY", st.getTaskStatus(), null, null,
                    st.getScheduledAt() != null ? st.getScheduledAt().format(TIME_FMT) : "",
                    null, st.getSellRequest().getId(), addr, district
            );
        }).toList();

        return new TodayDto(tourDtos, surveyDtos);
    }

    // ==================================================================
    // 5. LEAD NOTES (Ghi chú khách hàng)
    // ==================================================================
    @Transactional(readOnly = true)
    public List<NoteDto> getNotes(long agentId, long personId) {
        AgentLead lead = agentLeadRepository.findByAgentIdAndCustomerId(agentId, personId).orElse(null);
        if (lead == null) return new ArrayList<>();

        List<LeadNote> notes = leadNoteRepository.findByLeadIdOrderByCreatedAtDesc(lead.getId());
        return notes.stream().map(n -> new NoteDto(
                n.getId(),
                n.getContent(),
                n.getCreatedAt() != null ? n.getCreatedAt().format(TIME_FMT) : ""
        )).toList();
    }

    @Transactional
    public NoteDto addNote(long agentId, long personId, String content) {
        AgentLead lead = agentLeadRepository.findByAgentIdAndCustomerId(agentId, personId)
                .orElseGet(() -> {
                    AgentLead newLead = AgentLead.builder()
                            .agentId(agentId)
                            .customerId(personId)
                            .leadStatus("NEW")
                            .build();
                    return agentLeadRepository.save(newLead);
                });

        LeadNote note = LeadNote.builder()
                .leadId(lead.getId())
                .content(content)
                .build();
        
        note = leadNoteRepository.save(note);

        return new NoteDto(
                note.getId(),
                note.getContent(),
                note.getCreatedAt() != null ? note.getCreatedAt().format(TIME_FMT) : LocalDateTime.now().format(TIME_FMT)
        );
    }

    @Transactional
    public NoteDto updateNote(long noteId, String content) {
        LeadNote note = leadNoteRepository.findById(noteId)
                .orElseThrow(() -> new RuntimeException("Note not found"));
        note.setContent(content);
        note = leadNoteRepository.save(note);
        return new NoteDto(
                note.getId(),
                note.getContent(),
                note.getCreatedAt() != null ? note.getCreatedAt().format(TIME_FMT) : ""
        );
    }

    @Transactional
    public void deleteNote(long noteId) {
        leadNoteRepository.deleteById(noteId);
    }

    // ==================================================================
    // 6. PERSONAL NOTES (Ghi chú cá nhân - Bảng agent_notes)
    // ==================================================================
    @Transactional(readOnly = true)
    public List<NoteDto> getPersonalNotes(long agentId) {
        List<AgentNote> notes = agentNoteRepository.findByAgentIdOrderByCreatedAtDesc(agentId);
        return notes.stream().map(n -> new NoteDto(
                n.getId(),
                n.getContent(),
                n.getCreatedAt() != null ? n.getCreatedAt().format(TIME_FMT) : ""
        )).toList();
    }

    @Transactional
    public NoteDto addPersonalNote(long agentId, String content) {
        AgentNote note = AgentNote.builder()
                .agentId(agentId)
                .content(content)
                .build();
        note = agentNoteRepository.save(note);
        return new NoteDto(
                note.getId(),
                note.getContent(),
                note.getCreatedAt() != null ? note.getCreatedAt().format(TIME_FMT) : LocalDateTime.now().format(TIME_FMT)
        );
    }

    @Transactional
    public NoteDto updatePersonalNote(long noteId, String content) {
        AgentNote note = agentNoteRepository.findById(noteId)
                .orElseThrow(() -> new RuntimeException("Personal note not found"));
        note.setContent(content);
        note = agentNoteRepository.save(note);
        return new NoteDto(
                note.getId(),
                note.getContent(),
                note.getCreatedAt() != null ? note.getCreatedAt().format(TIME_FMT) : ""
        );
    }

    @Transactional
    public void deletePersonalNote(long noteId) {
        agentNoteRepository.deleteById(noteId);
    }
}