
package com.main.docmanager.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.main.docmanager.model.Document;
import com.main.docmanager.model.User;
import com.main.docmanager.security.JwtUtil;
import com.main.docmanager.service.DocumentService;

@SpringBootTest
@AutoConfigureMockMvc
public class DocumentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DocumentService documentService;

    @Autowired
    private JwtUtil jwtUtil;

    private String adminToken;
    private String userToken;
    private Document sampleDocument;

    @BeforeEach
    void setUp() {
        // Create sample document
        sampleDocument = new Document();
        sampleDocument.setId(1L);
        sampleDocument.setTitle("Test.pdf");
        sampleDocument.setAuthor("John");
        sampleDocument.setUploadedAt(LocalDateTime.now());

        // Generate JWT tokens
        org.springframework.security.core.userdetails.User adminUser = 
            new org.springframework.security.core.userdetails.User(
                "admin", "password", 
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")));
        org.springframework.security.core.userdetails.User regularUser = 
            new org.springframework.security.core.userdetails.User(
                "user", "password", 
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        adminToken = jwtUtil.generateToken(adminUser);
        userToken = jwtUtil.generateToken(regularUser);
    }

    // POST /api/documents/upload
    @Test
    @WithMockUser(roles = "ADMIN")
    void testUploadDocumentSuccess() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
            "file", "test.pdf", "application/pdf", "Sample content".getBytes());
        when(documentService.upload(any(MockMultipartFile.class), eq("John")))
            .thenReturn(sampleDocument);

        mockMvc.perform(multipart("/api/documents/upload")
                .file(file)
                .param("author", "John")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Test.pdf"))
                .andExpect(jsonPath("$.author").value("John"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUploadDocumentException() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
            "file", "test.pdf", "application/pdf", "Sample content".getBytes());
        when(documentService.upload(any(MockMultipartFile.class), eq("John")))
            .thenThrow(new RuntimeException("Upload failed"));

        mockMvc.perform(multipart("/api/documents/upload")
                .file(file)
                .param("author", "John")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testUploadDocumentForbidden() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
            "file", "test.pdf", "application/pdf", "Sample content".getBytes());

        mockMvc.perform(multipart("/api/documents/upload")
                .file(file)
                .param("author", "John")
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void testUploadDocumentUnauthorized() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
            "file", "test.pdf", "application/pdf", "Sample content".getBytes());

        mockMvc.perform(multipart("/api/documents/upload")
                .file(file)
                .param("author", "John"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUploadDocumentMissingFile() throws Exception {
        mockMvc.perform(multipart("/api/documents/upload")
                .param("author", "John")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUploadDocumentMissingAuthor() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
            "file", "test.pdf", "application/pdf", "Sample content".getBytes());

        mockMvc.perform(multipart("/api/documents/upload")
                .file(file)
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isInternalServerError());
    }

    // DELETE /api/documents/delete
    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteDocumentSuccess() throws Exception {
        when(documentService.delete(1L)).thenReturn(sampleDocument);

        mockMvc.perform(delete("/api/documents/delete")
                .param("id", "1")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Test.pdf"))
                .andExpect(jsonPath("$.author").value("John"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteDocumentException() throws Exception {
        when(documentService.delete(1L)).thenThrow(new RuntimeException("Delete failed"));

        mockMvc.perform(delete("/api/documents/delete")
                .param("id", "1")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testDeleteDocumentForbidden() throws Exception {
        mockMvc.perform(delete("/api/documents/delete")
                .param("id", "1")
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void testDeleteDocumentUnauthorized() throws Exception {
        mockMvc.perform(delete("/api/documents/delete")
                .param("id", "1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteDocumentMissingId() throws Exception {
        mockMvc.perform(delete("/api/documents/delete")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isInternalServerError());
    }
    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void testUploadFailure() throws Exception {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "test.pdf",
            "application/pdf",
            "Sample PDF content".getBytes()
        );
        String author = "admin";
        User uploadedBy = new User("admin", "password", "ROLE_ADMIN");
        Document document = new Document("Test Document", "application/pdf", author, "Sample content", uploadedBy);
        document.setId(1L);
        document.setUploadedAt(LocalDateTime.now());

        when(documentService.upload(any(MockMultipartFile.class), eq(author))).thenReturn(document);

        // Act & Assert
        mockMvc.perform(multipart("/upload")
                .file(file)
                .param("author", author)
                .contentType("multipart/form-data"))
            .andExpect(status().isInternalServerError());

    }
}
