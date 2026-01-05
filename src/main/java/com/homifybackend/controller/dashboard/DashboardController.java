package com.homifybackend.controller.dashboard;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.homifybackend.dto.dashboard.AddNoteRequest;
import com.homifybackend.dto.dashboard.AgentProfileDto;
import com.homifybackend.dto.dashboard.ErrorResponse;
import com.homifybackend.dto.dashboard.NoteDto;
import com.homifybackend.dto.dashboard.OverviewDto;
import com.homifybackend.dto.dashboard.Performance7dDto;
import com.homifybackend.dto.dashboard.TodayDto;
import com.homifybackend.dto.dashboard.UpdateNoteRequest;
import com.homifybackend.service.agentDashboard.DashboardService;

@RestController
@RequestMapping("/api")
public class DashboardController {

    private final DashboardService service;

    public DashboardController(DashboardService service) {
        this.service = service;
    }

    @GetMapping("/agent/profile")
    public ResponseEntity<?> getProfile(@RequestParam long agentId) {
        AgentProfileDto profile = service.getAgentProfile(agentId);
        if (profile == null) return ResponseEntity.status(404).body(new ErrorResponse("Agent not found"));
        return ResponseEntity.ok(profile);
    }

    @GetMapping("/dashboard/overview")
    public ResponseEntity<OverviewDto> getOverview(@RequestParam long agentId) {
        return ResponseEntity.ok(service.getOverview(agentId));
    }

    @GetMapping("/dashboard/performance-7d")
    public ResponseEntity<Performance7dDto> getPerformance(@RequestParam long agentId) {
        return ResponseEntity.ok(service.getPerformance7d(agentId));
    }

    @GetMapping("/dashboard/today")
    public ResponseEntity<TodayDto> getToday(@RequestParam long agentId) {
        return ResponseEntity.ok(service.getToday(agentId));
    }

    // ==================================================================
    // LEAD NOTES (Ghi chú khách hàng)
    // ==================================================================
    @GetMapping("/person/{personId}/notes")
    public ResponseEntity<List<NoteDto>> getNotes(@PathVariable long personId, @RequestParam long agentId) {
        return ResponseEntity.ok(service.getNotes(agentId, personId));
    }

    @PostMapping("/person/{personId}/notes")
    public ResponseEntity<?> addNote(@PathVariable long personId, @RequestBody AddNoteRequest body) {
        if (body.agentId() == null) return ResponseEntity.badRequest().body(new ErrorResponse("agentId is required"));
        String content = body.content() == null ? "" : body.content().trim();
        if (content.isEmpty()) return ResponseEntity.badRequest().body(new ErrorResponse("Content cannot be empty"));
        return ResponseEntity.ok(service.addNote(body.agentId(), personId, content));
    }

    @PutMapping("/notes/{noteId}")
    public ResponseEntity<?> updateNote(@PathVariable long noteId, @RequestBody UpdateNoteRequest body) {
        if (body.content() == null || body.content().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Content cannot be empty"));
        }
        return ResponseEntity.ok(service.updateNote(noteId, body.content()));
    }

    @DeleteMapping("/notes/{noteId}")
    public ResponseEntity<?> deleteNote(@PathVariable long noteId) {
        service.deleteNote(noteId);
        return ResponseEntity.ok().build();
    }

    // ==================================================================
    // PERSONAL NOTES (Ghi chú cá nhân)
    // ==================================================================
    @GetMapping("/agent/notes")
    public ResponseEntity<List<NoteDto>> getPersonalNotes(@RequestParam long agentId) {
        return ResponseEntity.ok(service.getPersonalNotes(agentId));
    }

    @PostMapping("/agent/notes")
    public ResponseEntity<?> addPersonalNote(@RequestBody AddNoteRequest body) {
        if (body.agentId() == null) return ResponseEntity.badRequest().body(new ErrorResponse("agentId is required"));
        String content = body.content() == null ? "" : body.content().trim();
        if (content.isEmpty()) return ResponseEntity.badRequest().body(new ErrorResponse("Content cannot be empty"));
        return ResponseEntity.ok(service.addPersonalNote(body.agentId(), content));
    }

    @PutMapping("/agent/notes/{noteId}")
    public ResponseEntity<?> updatePersonalNote(@PathVariable long noteId, @RequestBody UpdateNoteRequest body) {
        if (body.content() == null || body.content().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Content cannot be empty"));
        }
        return ResponseEntity.ok(service.updatePersonalNote(noteId, body.content()));
    }

    @DeleteMapping("/agent/notes/{noteId}")
    public ResponseEntity<?> deletePersonalNote(@PathVariable long noteId) {
        service.deletePersonalNote(noteId);
        return ResponseEntity.ok().build();
    }
}