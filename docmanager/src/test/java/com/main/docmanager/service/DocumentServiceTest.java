
package com.main.docmanager.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Optional;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaCoreProperties;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.BodyContentHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

import com.main.docmanager.constants.ConstantsUtil;
import com.main.docmanager.model.Document;
import com.main.docmanager.model.User;
import com.main.docmanager.repository.DocumentRepository;
import com.main.docmanager.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class DocumentServiceTest {

    @Mock
    private DocumentRepository documentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MultipartFile multipartFile;

    @Mock
    private AutoDetectParser parser;

    @Mock
    private BodyContentHandler handler;

    @Mock
    private Metadata metadata;

    @InjectMocks
    private DocumentService documentService;

    @TempDir
    Path tempDir;

    private User sampleUser;
    private Document sampleDocument;
    private File tempFile;

    @BeforeEach
    void setUp() throws IOException {
        sampleUser = new User("user", "password", "ROLE_USER");
        sampleUser.setId(1L);

        sampleDocument = new Document("Test.pdf", "application/pdf", "user", "Sample content", sampleUser);
        sampleDocument.setId(1L);
        sampleDocument.setUploadedAt(LocalDateTime.now());

        tempFile = tempDir.resolve("tika-Test.pdf").toFile();
        tempFile.createNewFile();
    }

  



    // Tests for validateFile
    @Test
    void testValidateFileSuccess() {
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getSize()).thenReturn(1024L);
        when(multipartFile.getContentType()).thenReturn("application/pdf");

        assertDoesNotThrow(() -> documentService.validateFile(multipartFile));
    }

    @Test
    void testValidateFileNull() {
        assertThrows(IllegalArgumentException.class, () -> documentService.validateFile(null));
    }

    @Test
    void testValidateFileEmpty() {
        when(multipartFile.isEmpty()).thenReturn(true);
        assertThrows(IllegalArgumentException.class, () -> documentService.validateFile(multipartFile));
    }

    @Test
    void testValidateFileOversized() {
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getSize()).thenReturn(ConstantsUtil.MAX_FILE_SIZE + 1);
        assertThrows(IllegalArgumentException.class, () -> documentService.validateFile(multipartFile));
    }

    @Test
    void testValidateFileUnsupportedType() {
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getSize()).thenReturn(1024L);
        when(multipartFile.getContentType()).thenReturn("image/jpeg");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            documentService.validateFile(multipartFile);
        });
        assertEquals("Unsupported file type: image/jpeg", exception.getMessage());
    }

    // Tests for createTempFile
    @Test
    void testCreateTempFileSuccess() throws IOException {
        when(multipartFile.getOriginalFilename()).thenReturn("Test.pdf");
//        when(multipartFile.transferTo(any(File.class))).thenAnswer(invocation -> {
//            File file = invocation.getArgument(0);
//            file.createNewFile();
//            return null;
//        });

        File result = documentService.createTempFile(multipartFile);
        assertTrue(result.exists());
        assertTrue(result.delete());
    }



    // Tests for delete
    @Test
    void testDeleteSuccess() throws FileNotFoundException {
        when(documentRepository.findById(1L)).thenReturn(Optional.of(sampleDocument));
        doNothing().when(documentRepository).deleteById(1L);

        Document result = documentService.delete(1L);

        assertNotNull(result);
        assertEquals(sampleDocument, result);
        verify(documentRepository).deleteById(1L);
    }

    @Test
    void testDeleteNotFound() {
        when(documentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(FileNotFoundException.class, () -> documentService.delete(1L));
    }
}