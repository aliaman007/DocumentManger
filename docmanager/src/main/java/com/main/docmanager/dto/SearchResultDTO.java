package com.main.docmanager.dto;

import java.time.LocalDateTime;

public record SearchResultDTO(Long id,String title, String snippet, LocalDateTime uploadedAt, String author) {}
