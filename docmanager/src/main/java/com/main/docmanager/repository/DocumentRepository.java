package com.main.docmanager.repository;




import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.main.docmanager.model.Document;

public interface DocumentRepository extends JpaRepository<Document, Long> {
  
	@Query("SELECT d FROM Document d WHERE LOWER(d.content) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(d.title) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Document> findByContentOrTitleContainingIgnoreCase(@Param("query") String query, Pageable pageable);

	@Query(value = "SELECT * FROM documents d WHERE " +
	           "(:author IS NULL OR d.author = :author) AND " +
	           "(:fileType IS NULL OR d.file_type = :fileType) ",
	           nativeQuery = true)
    Page<Document> findByFilters(
            @Param("author") String author,
            @Param("fileType") String fileType,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            Pageable pageable);
}