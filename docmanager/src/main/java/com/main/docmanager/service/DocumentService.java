
package com.main.docmanager.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Optional;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaCoreProperties;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

import com.main.docmanager.constants.ConstantsUtil;
import com.main.docmanager.model.Document;
import com.main.docmanager.model.User;
import com.main.docmanager.repository.DocumentRepository;
import com.main.docmanager.repository.UserRepository;

@Service
public class DocumentService {
    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private UserRepository userRepository;

    public Document upload(MultipartFile file, String author) throws IOException, TikaException, SAXException {
        validateFile(file);

        File tempFile = null;
        try {
            // Create temporary file
            tempFile = createTempFile(file);

            // Parse file with Tika
            AutoDetectParser parser = new AutoDetectParser();
            BodyContentHandler handler = new BodyContentHandler(-1);
            Metadata metadata = new Metadata();
            metadata.set(TikaCoreProperties.RESOURCE_NAME_KEY, file.getOriginalFilename());

            try (FileInputStream inputStream = new FileInputStream(tempFile)) {
                parser.parse(inputStream, handler, metadata, new org.apache.tika.parser.ParseContext());
            }

            // Extract content and metadata
            String content = handler.toString();
            String title = metadata.get(TikaCoreProperties.TITLE) != null ? metadata.get(TikaCoreProperties.TITLE) : file.getOriginalFilename();
            String fileType = metadata.get(Metadata.CONTENT_TYPE);

            // Fetch User
            User uploadedBy = userRepository.findByUsername(author)
                    .orElseThrow(() -> new IllegalArgumentException("User not found: " + author));

            // Create and save document
            Document document = new Document(title, fileType, author, content, uploadedBy);
            return documentRepository.save(document);
        } finally {
            // Clean up temporary file
            if (tempFile != null && tempFile.exists()) {
                if (!tempFile.delete()) {
                    System.err.println("Failed to delete temporary file: " + tempFile.getAbsolutePath());
                }
            }
        }
    }

    public void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is empty or not provided");
        }
        if (file.getSize() >ConstantsUtil.MAX_FILE_SIZE) {
            throw new IllegalArgumentException("File size exceeds 10MB limit");
        }
        if (!ConstantsUtil.ALLOWED_FILE_TYPES.contains(file.getContentType())) {
            throw new IllegalArgumentException("Unsupported file type: " + file.getContentType());
        }
    }

    public File createTempFile(MultipartFile multipartFile) throws IOException {
        File tempFile = File.createTempFile("tika-", multipartFile.getOriginalFilename());
        multipartFile.transferTo(tempFile);
        return tempFile;
    }

	public Document delete(Long id) throws FileNotFoundException {
		// TODO Auto-generated method stub
		Optional<Document> d=documentRepository.findById(id);
		if(d.isEmpty()) {
			throw new FileNotFoundException();
		}
		 documentRepository.deleteById(id);
		 return d.get();
	}
}