package com.main.docmanager.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.main.docmanager.constants.ConstantsUtil;
import com.main.docmanager.dto.SearchResultDTO;
import com.main.docmanager.model.Document;
import com.main.docmanager.repository.DocumentRepository;
import com.main.docmanager.repository.UserRepository;

@Service
public class QaService {

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private UserRepository userRepository;

  
//    @Cacheable(value = "searchCache", key = "#query + #pageable.pageNumber")
	public List<SearchResultDTO> search(String query, Pageable pageable) {
		Page<Document> results = documentRepository.findByContentOrTitleContainingIgnoreCase(query, pageable);

		List<SearchResultDTO> searchResults = results
				.getContent().stream().map(doc -> new SearchResultDTO(doc.getId(),doc.getTitle(),
						generateSnippet(doc.getContent(), query, 50), doc.getUploadedAt(), doc.getAuthor()))
				.collect(Collectors.toList());

		return searchResults;
		}

    public Page<Document> filter(String author, String fileType, String fromDate, String toDate, Pageable pageable) {
    	LocalDateTime parsedFromDate = parseDateTime(fromDate, "fromDate");
        LocalDateTime parsedToDate = parseDateTime(toDate, "toDate");

         if (parsedFromDate != null && parsedToDate != null && parsedFromDate.isAfter(parsedToDate)) {
             throw new IllegalArgumentException("fromDate must be before or equal to toDate");
         }

    	return documentRepository.findByFilters(author, fileType, parsedFromDate, parsedToDate, pageable);
    }
    public String generateSnippet(String content, String keyword, int contextLength) {
        if (content == null || keyword == null || content.isEmpty() || keyword.isEmpty()) {
            return "";
        }

        String lowerContent = content.toLowerCase();
        String lowerKeyword = keyword.toLowerCase();
        int index = lowerContent.indexOf(lowerKeyword);

        if (index == -1) {
            return content.length() > contextLength * 2 ? content.substring(0, contextLength * 2) + "..." : content;
        }

        int start = Math.max(0, index - contextLength);
        int end = Math.min(content.length(), index + keyword.length() + contextLength);
        String snippet = (start > 0 ? "..." : "") + content.substring(start, end) + (end < content.length() ? "..." : "");
        return snippet;
    }
    public LocalDateTime parseDateTime(String dateTimeStr, String paramName) {
        if (dateTimeStr == null || dateTimeStr.isBlank()) {
//            logger.debug("{} is null or blank: {}", paramName, dateTimeStr);
            return null;
        }
        try {
            LocalDateTime parsed = LocalDateTime.parse(dateTimeStr,ConstantsUtil.DATE_TIME_FORMATTER);
//            logger.debug("Parsed {}: {} to {}", paramName, dateTimeStr, parsed);
            return parsed;
        } catch (DateTimeParseException e) {
//            logger.error("Failed to parse {}: {}", paramName, dateTimeStr, e);
            throw new IllegalArgumentException("Invalid date-time format for " + paramName + ": " + dateTimeStr + 
                    ". Use formats like 2024-01-01T00:00:00, 2024-01-01 00:00:00, or with milliseconds");
        }
    }}

