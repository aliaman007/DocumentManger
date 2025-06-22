
package com.main.docmanager.exception;

import com.main.docmanager.dto.ErrorResponse;
import org.apache.tika.exception.TikaException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.xml.sax.SAXException;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();

    @Test
    void testHandleTikaException() {
        TikaException ex = new TikaException("Tika parsing error");
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleTikaException(ex);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ErrorResponse error = response.getBody();
        assertNotNull(error);
        assertEquals("File parsing error", error.getError());
        assertEquals("Tika parsing error", error.getMessage());
    }

    @Test
    void testHandleTikaExceptionWithNullMessage() {
        TikaException ex = new TikaException(null);
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleTikaException(ex);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ErrorResponse error = response.getBody();
        assertNotNull(error);
        assertEquals("File parsing error", error.getError());
        assertNull(error.getMessage());
    }

    @Test
    void testHandleSAXException() {
        SAXException ex = new SAXException("Invalid XML content");
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleSAXException(ex);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ErrorResponse error = response.getBody();
        assertNotNull(error);
        assertEquals("File parsing error", error.getError());
        assertEquals("Invalid file content: Invalid XML content", error.getMessage());
    }

    @Test
    void testHandleSAXExceptionWithNullMessage() {
        SAXException ex = new SAXException((String) null);
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleSAXException(ex);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ErrorResponse error = response.getBody();
        assertNotNull(error);
        assertEquals("File parsing error", error.getError());
        assertEquals("Invalid file content: null", error.getMessage());
    }

    @Test
    void testHandleIOException() {
        IOException ex = new IOException("File read error");
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleIOException(ex);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        ErrorResponse error = response.getBody();
        assertNotNull(error);
        assertEquals("File processing error", error.getError());
        assertEquals("Failed to process file: File read error", error.getMessage());
    }

    @Test
    void testHandleIOExceptionWithNullMessage() {
        IOException ex = new IOException((String) null);
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleIOException(ex);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        ErrorResponse error = response.getBody();
        assertNotNull(error);
        assertEquals("File processing error", error.getError());
        assertEquals("Failed to process file: null", error.getMessage());
    }

    @Test
    void testHandleMaxUploadSizeExceeded() {
        MaxUploadSizeExceededException ex = new MaxUploadSizeExceededException(1024);
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleMaxUploadSizeExceeded(ex);

        assertNotNull(response);
        assertEquals(HttpStatus.PAYLOAD_TOO_LARGE, response.getStatusCode());
        ErrorResponse error = response.getBody();
        assertNotNull(error);
        assertEquals("File too large", error.getError());
        assertEquals("File size exceeds the maximum limit", error.getMessage());
    }

    @Test
    void testHandleIllegalArgumentException() {
        IllegalArgumentException ex = new IllegalArgumentException("Invalid input");
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleIllegalArgumentException(ex);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ErrorResponse error = response.getBody();
        assertNotNull(error);
        assertEquals("Invalid request", error.getError());
        assertEquals("Invalid input", error.getMessage());
    }

    @Test
    void testHandleIllegalArgumentExceptionWithNullMessage() {
        IllegalArgumentException ex = new IllegalArgumentException((String) null);
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleIllegalArgumentException(ex);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ErrorResponse error = response.getBody();
        assertNotNull(error);
        assertEquals("Invalid request", error.getError());
        assertNull(error.getMessage());
    }

    @Test
    void testHandleGenericException() {
        Exception ex = new Exception("Unexpected error");
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleGenericException(ex);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        ErrorResponse error = response.getBody();
        assertNotNull(error);
        assertEquals("Unexpected error", error.getError());
        assertEquals("An unexpected error occurred: Unexpected error", error.getMessage());
    }

    @Test
    void testHandleGenericExceptionWithNullMessage() {
        Exception ex = new Exception((String) null);
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleGenericException(ex);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        ErrorResponse error = response.getBody();
        assertNotNull(error);
        assertEquals("Unexpected error", error.getError());
        assertEquals("An unexpected error occurred: null", error.getMessage());
    }
}
