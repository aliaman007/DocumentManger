package com.main.docmanager.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.main.docmanager.model.Document;
import com.main.docmanager.service.DocumentService;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    @Autowired
    private DocumentService documentService;

    @PostMapping("/upload")
    public ResponseEntity<Document> upload(@RequestParam("file") MultipartFile file, @RequestParam String author) throws Exception {
        try {
            Document document = documentService.upload(file, author);
            return ResponseEntity.ok(document);
        } catch (Exception ex) {
            throw ex;
        }
    
    }
    @DeleteMapping("/delete")
    public ResponseEntity<Document> delete(@RequestParam("id") Long id) throws Exception {
        try {
            Document document = documentService.delete(id);
            return ResponseEntity.ok(document);
        } catch (Exception ex) {
            throw ex;
        }
    }
}