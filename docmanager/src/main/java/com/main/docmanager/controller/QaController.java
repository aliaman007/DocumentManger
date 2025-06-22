package com.main.docmanager.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.main.docmanager.dto.SearchResultDTO;
import com.main.docmanager.model.Document;
import com.main.docmanager.service.QaService;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/api/documents")
public class QaController {

	 @Autowired
	    private QaService qaService;
	 
	 @Operation(summary = "Search documents by query")
	    @GetMapping("/search")
	    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
	    public ResponseEntity<List<SearchResultDTO>> search(
	            @RequestParam String query, Pageable pageable) {
	        return ResponseEntity.ok(qaService.search(query, pageable));
	    }

	    @Operation(summary = "Filter documents by metadata")
	    @GetMapping("/filter")
	    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
	    public ResponseEntity<Page<Document>> filter(@RequestParam(required = false) String author,
	            @RequestParam(required = false, name = "fileType") String fileType,
	            @RequestParam(required = false)  String fromDate,
	            @RequestParam(required = false)  String toDate,
	            Pageable pageable) {
	        return ResponseEntity.ok(qaService.filter(author, fileType,fromDate,toDate, pageable));
	    }
}
