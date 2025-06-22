
package com.main.docmanager.service;

import com.main.docmanager.constants.ConstantsUtil;
import com.main.docmanager.dto.SearchResultDTO;
import com.main.docmanager.model.Document;
import com.main.docmanager.model.User;
import com.main.docmanager.repository.DocumentRepository;
import com.main.docmanager.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class QaServiceTest {

    @Mock
    private DocumentRepository documentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Pageable pageable;

    @InjectMocks
    private QaService qaService;

    private User sampleUser;
    private Document sampleDocument;
    private LocalDateTime sampleDateTime;

    @BeforeEach
    void setUp() {
        sampleUser = new User("user", "password", "ROLE_USER");
        sampleUser.setId(1L);

        sampleDateTime = LocalDateTime.of(2024, 1, 1, 12, 0, 0);

        sampleDocument = new Document("Test Document", "application/pdf", "user", "This is a sample content with keyword.", sampleUser);
        sampleDocument.setId(1L);
        sampleDocument.setUploadedAt(sampleDateTime);
    }

   

    @Test
    void testSearchEmptyResults() {
        Page<Document> emptyPage = new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 10), 0);
        when(documentRepository.findByContentOrTitleContainingIgnoreCase("keyword", pageable)).thenReturn(emptyPage);

        List<SearchResultDTO> results = qaService.search("keyword", pageable);

        assertNotNull(results);
        assertTrue(results.isEmpty());
        verify(documentRepository).findByContentOrTitleContainingIgnoreCase("keyword", pageable);
    }

    @Test
    void testSearchNullQuery() {
        Page<Document> emptyPage = new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 10), 0);
        when(documentRepository.findByContentOrTitleContainingIgnoreCase(null, pageable)).thenReturn(emptyPage);

        List<SearchResultDTO> results = qaService.search(null, pageable);

        assertNotNull(results);
        assertTrue(results.isEmpty());
        verify(documentRepository).findByContentOrTitleContainingIgnoreCase(null, pageable);
    }

    // Tests for filter
    @Test
    void testFilterSuccessWithAllParams() {
        Page<Document> page = new PageImpl<>(List.of(sampleDocument), PageRequest.of(0, 10), 1);
        LocalDateTime fromDate = LocalDateTime.of(2024, 1, 1, 0, 0);
        LocalDateTime toDate = LocalDateTime.of(2024, 12, 31, 23, 59);
        when(documentRepository.findByFilters(eq("user"), eq("application/pdf"), eq(fromDate), eq(toDate), any(Pageable.class)))
            .thenReturn(page);

        Page<Document> result = qaService.filter("user", "application/pdf", "2024-01-01T00:00:00", "2024-12-31T23:59:00", pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(sampleDocument, result.getContent().get(0));
        verify(documentRepository).findByFilters(eq("user"), eq("application/pdf"), eq(fromDate), eq(toDate), any(Pageable.class));
    }

    @Test
    void testFilterNullDates() {
        Page<Document> page = new PageImpl<>(List.of(sampleDocument), PageRequest.of(0, 10), 1);
        when(documentRepository.findByFilters(eq("user"), eq("application/pdf"), isNull(), isNull(), any(Pageable.class)))
            .thenReturn(page);

        Page<Document> result = qaService.filter("user", "application/pdf", null, null, pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(documentRepository).findByFilters(eq("user"), eq("application/pdf"), isNull(), isNull(), any(Pageable.class));
    }

    @Test
    void testFilterFromDateAfterToDate() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            qaService.filter("user", "application/pdf", "2024-12-31T23:59:00", "2024-01-01T00:00:00", pageable);
        });
        assertEquals("fromDate must be before or equal to toDate", exception.getMessage());
    }

    @Test
    void testFilterInvalidDateFormat() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            qaService.filter("user", "application/pdf", "invalid-date", "2024-12-31T23:59:00", pageable);
        });
        assertTrue(exception.getMessage().contains("Invalid date-time format for fromDate: invalid-date"));
    }

    

    

    @Test
    void testGenerateSnippetNullContent() {
        String snippet = qaService.generateSnippet(null, "keyword", 10);
        assertEquals("", snippet);
    }

    @Test
    void testGenerateSnippetEmptyContent() {
        String snippet = qaService.generateSnippet("", "keyword", 10);
        assertEquals("", snippet);
    }

    @Test
    void testGenerateSnippetNullKeyword() {
        String content = "This is a sample content.";
        String snippet = qaService.generateSnippet(content, null, 10);
        assertEquals("", snippet);
    }

    @Test
    void testGenerateSnippetShortContent() {
        String content = "Short text.";
        String snippet = qaService.generateSnippet(content, "text", 10);
        assertEquals("Short text.", snippet);
    }

    // Tests for parseDateTime
    @Test
    void testParseDateTimeSuccess() {
        LocalDateTime result = qaService.parseDateTime("2024-01-01T12:00:00", "fromDate");
        assertEquals(LocalDateTime.of(2024, 1, 1, 12, 0, 0), result);
    }

    @Test
    void testParseDateTimeNull() {
        LocalDateTime result = qaService.parseDateTime(null, "fromDate");
        assertNull(result);
    }

    @Test
    void testParseDateTimeBlank() {
        LocalDateTime result = qaService.parseDateTime("", "fromDate");
        assertNull(result);
    }

    @Test
    void testParseDateTimeInvalidFormat() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            qaService.parseDateTime("invalid-date", "fromDate");
        });
        assertTrue(exception.getMessage().contains("Invalid date-time format for fromDate: invalid-date"));
    }
}
