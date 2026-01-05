package com.homifybackend.dto.dashboard;

public record AddNoteRequest(
    Long agentId,
    String content
) {}