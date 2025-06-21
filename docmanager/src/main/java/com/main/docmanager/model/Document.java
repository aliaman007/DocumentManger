package com.main.docmanager.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "documents")
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String fileType;
    private String author;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime uploadedAt;

    @Column(columnDefinition = "TEXT")
    private String content;

    @ManyToOne
    @JoinColumn(name = "uploaded_by_id", referencedColumnName = "id")
    private User uploadedBy;

    // Constructors
    public Document() {}

    public Document(String title, String fileType, String author, String content, User uploadedBy) {
        this.title = title;
        this.fileType = fileType;
        this.author = author;
        this.content = content;
        this.uploadedBy = uploadedBy;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getFileType() { return fileType; }
    public void setFileType(String fileType) { this.fileType = fileType; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public LocalDateTime getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public User getUploadedBy() { return uploadedBy; }
    public void setUploadedBy(User uploadedBy) { this.uploadedBy = uploadedBy; }
}