package com.homifybackend.dto.dashboard;

public record NoteDto(
    long id,
    String content,
    String createdAt
) {}