
package com.main.docmanager.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.main.docmanager.dto.SearchResultDTO;
import com.main.docmanager.model.Document;
import com.main.docmanager.security.JwtUtil;
import com.main.docmanager.service.QaService;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
public class QaControllerTest {

    private static final Logger logger = LoggerFactory.getLogger(QaControllerTest.class);

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private QaService qaService;

    @Autowired
    private JwtUtil jwtUtil;

    @InjectMocks
    private QaController qaController;

    private String adminToken;
    private String userToken;
    private String guestToken;
    private Document sampleDocument;
    private SearchResultDTO sampleSearchResult;
    private Page<Document> sampleDocumentPage;

    @BeforeEach
    void setUp() {
        // Create sample document
        sampleDocument = new Document();
        sampleDocument.setId(1L);
        sampleDocument.setTitle("Test.pdf");
        sampleDocument.setAuthor("John");
        sampleDocument.setUploadedAt(LocalDateTime.of(2025, 6, 22, 11, 0));

        // Create sample SearchResultDTO
        sampleSearchResult = new SearchResultDTO(1L, "Test.pdf", "Snippet", LocalDateTime.of(2025, 6, 22, 11, 0), "John");

        // Create sample Page<Document>
        sampleDocumentPage = new PageImpl<>(Collections.singletonList(sampleDocument), PageRequest.of(0, 10), 1);

        // Generate JWT tokens
        UserDetails adminUser = new User(
            "admin", "password", Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")));
        UserDetails regularUser = new User(
            "user", "password", Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        UserDetails guestUser = new User(
            "guest", "password", Collections.singletonList(new SimpleGrantedAuthority("ROLE_GUEST")));
        adminToken = jwtUtil.generateToken(adminUser);
        userToken = jwtUtil.generateToken(regularUser);
        guestToken = jwtUtil.generateToken(guestUser);

        // Clear SecurityContext
        SecurityContextHolder.clearContext();
    }

    private void setSecurityContext(UserDetails userDetails) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
            userDetails, null, userDetails.getAuthorities());
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authToken);
        SecurityContextHolder.setContext(securityContext);
    }

 

   

    @Test
    void testSearchForbidden() throws Exception {
        setSecurityContext(new User(
            "guest", "password", Collections.singletonList(new SimpleGrantedAuthority("ROLE_GUEST"))));

        mockMvc.perform(get("/api/documents/search")
                .param("query", "report")
                .param("page", "0")
                .param("size", "10")
                .header("Authorization", "Bearer " + guestToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void testSearchUnauthorized() throws Exception {
        mockMvc.perform(get("/api/documents/search")
                .param("query", "report")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isForbidden());
    }

    @Test
    void testSearchMissingQuery() throws Exception {
        setSecurityContext(new User(
            "admin", "password", Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"))));

        mockMvc.perform(get("/api/documents/search")
                .param("page", "0")
                .param("size", "10")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isInternalServerError());
    }


   

    

    @Test
    void testFilterForbidden() throws Exception {
        setSecurityContext(new User(
            "guest", "password", Collections.singletonList(new SimpleGrantedAuthority("ROLE_GUEST"))));

        mockMvc.perform(get("/api/documents/filter")
                .param("author", "John")
                .param("page", "0")
                .param("size", "10")
                .header("Authorization", "Bearer " + guestToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void testFilterUnauthorized() throws Exception {
        mockMvc.perform(get("/api/documents/filter")
                .param("author", "John")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isForbidden());
    }

    @Test
    void testFilterInvalidDateFormat() throws Exception {
        setSecurityContext(new User(
            "admin", "password", Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"))));

        mockMvc.perform(get("/api/documents/filter")
                .param("fromDate", "invalid-date")
                .param("page", "0")
                .param("size", "10")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isBadRequest());
    }
   
}
