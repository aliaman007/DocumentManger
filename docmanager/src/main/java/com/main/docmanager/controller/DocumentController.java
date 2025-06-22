package com.main.docmanager.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.main.docmanager.model.Document;
import com.main.docmanager.service.DocumentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {
    private static final Logger logger = LoggerFactory.getLogger(DocumentController.class);
    @Autowired
    private DocumentService documentService;
    @Operation(
            summary = "Upload a document",
            description = "Uploads a document file with an associated author. Requires ADMIN role and JWT authentication.",
            security = @SecurityRequirement(name = "bearerAuth")
        )
        @ApiResponses({
            @ApiResponse(
                responseCode = "200",
                description = "Document uploaded successfully",
                content = @Content(schema = @Schema(implementation = Document.class))
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Invalid file or parameters",
                content = @Content
            ),
            @ApiResponse(
                responseCode = "403",
                description = "Forbidden: ADMIN role required",
                content = @Content
            ),
            @ApiResponse(
                responseCode = "500",
                description = "Server error during upload",
                content = @Content
            )
        })
    @PostMapping("/upload")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Document> upload(
            @Parameter(description = "File to upload", required = true) @RequestParam("file") MultipartFile file,
            @Parameter(description = "Author of the document", required = true) @RequestParam String author
        ) throws Exception {
        try {
            Document document = documentService.upload(file, author);
            return ResponseEntity.ok(document);
        } catch (Exception ex) {
        	logger.error("Exception occured while uploading the document ",ex.getMessage());
        	throw ex;
        }
    
    }
    @DeleteMapping("/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Document> delete(@RequestParam("id") Long id) throws Exception {
        try {
            Document document = documentService.delete(id);
            return ResponseEntity.ok(document);
        } catch (Exception ex) {
        	logger.error("Exception occured while deleting the document with id"+id+"-->",ex.getMessage());
            throw ex;
        }
    }
}