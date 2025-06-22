
package com.main.docmanager.dto;

import java.time.LocalDateTime;

public class SearchResultDTO {
    private Long id;
    private String title;
    private String snippet;
    private LocalDateTime uploadedAt;
    private String author;

    public SearchResultDTO(Long id, String title, String snippet, LocalDateTime uploadedAt, String author) {
        this.id = id;
        this.title = title;
        this.snippet = snippet;
        this.uploadedAt = uploadedAt;
        this.author = author;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getSnippet() {
        return snippet;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    public String getAuthor() {
        return author;
    }
}
