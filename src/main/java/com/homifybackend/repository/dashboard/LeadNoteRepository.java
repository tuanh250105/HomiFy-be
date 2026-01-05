package com.homifybackend.repository.dashboard;

import com.homifybackend.model.LeadNote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LeadNoteRepository extends JpaRepository<LeadNote, Long> {
    List<LeadNote> findByLeadIdOrderByCreatedAtDesc(Long leadId);
}