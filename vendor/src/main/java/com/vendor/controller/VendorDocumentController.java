package com.vendor.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vendor.dto.VendorDocumentDTO;
import com.vendor.entity.Vendor;
import com.vendor.entity.VendorDocument;
import com.vendor.exception.APIException;
import com.vendor.exception.ResourceNotFoundException;
import com.vendor.repository.VendorRepository;
import com.vendor.service.VendorDocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/vendor/documents")
public class VendorDocumentController {

    private static final Logger logger = LoggerFactory.getLogger(VendorDocumentController.class);

    @Autowired
    private VendorDocumentService documentService;

    @Autowired
    private VendorRepository vendorRepository;

    @PostMapping("/upload")
    public ResponseEntity<VendorDocument> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam("vendorId") Long vendorId,
            @RequestParam("documentTypeId") Long documentTypeId,
            @RequestParam("expiryDate") LocalDate expiryDate) {

        VendorDocument document = documentService.uploadDocument(file, vendorId,
                documentTypeId, expiryDate);
        return ResponseEntity.ok(document);
    }

    @GetMapping("/vendor/{vendorId}")
    public ResponseEntity<List<VendorDocument>> getVendorDocuments(@PathVariable Long vendorId) {
        List<VendorDocument> documents = documentService.getVendorDocuments(vendorId);
        return ResponseEntity.ok(documents);
    }

//    @GetMapping("/{documentId}")
//    public ResponseEntity<byte[]> getDocument(@PathVariable Long documentId) {
//        VendorDocument document = documentService.getVendorDocuments(documentId).get(0);
//        byte[] fileContent = documentService.getDocument(documentId);
//
//        return ResponseEntity.ok()
//                .header(HttpHeaders.CONTENT_DISPOSITION,
//                        "attachment; filename=\"" + document.getOriginalFilename() + "\"")
//                .contentType(MediaType.parseMediaType(document.getContentType()))
//                .body(fileContent);
//    }
@GetMapping("/{documentId}")
public ResponseEntity<byte[]> getDocument(@PathVariable Long documentId) {
    try {
        VendorDocument document = documentService.getDocument(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found with id: " + documentId));

        byte[] fileContent = documentService.getDocumentContent(documentId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + document.getOriginalFilename() + "\"")
                .contentType(MediaType.parseMediaType(document.getContentType()))
                .body(fileContent);
    } catch (ResourceNotFoundException e) {
        logger.error("Document not found: {}", documentId, e);
        return ResponseEntity.notFound().build();
    } catch (Exception e) {
        logger.error("Error downloading document: {}", documentId, e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}

    @GetMapping("/user/{username}")
    public ResponseEntity<?> getVendorDocumentsByUsername(@PathVariable String username) {
        try {
            Optional<Vendor> vendor = vendorRepository.findByUserUsername(username);
            if (!vendor.isPresent()) {
                throw new ResourceNotFoundException("Vendor not found for user: " + username);
            }
            List<VendorDocumentDTO> documents = documentService.getVendorDocumentsDTO(vendor.get().getId());
            return ResponseEntity.ok(documents);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new APIException(e.getMessage()));
        } catch (Exception e) {
            logger.error("Error fetching vendor documents: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new APIException(e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllDocuments(@RequestParam(required = false) String username) {
        try {
            List<VendorDocumentDTO> documents;

            if (username != null) {
                // Vendor-specific request
                documents = documentService.getVendorDocumentsByUsername(username);
            } else {
                // Admin request - get all documents
                documents = documentService.getAllDocumentsDTO();
            }

            return ResponseEntity.ok(documents);
        } catch (Exception e) {
            logger.error("Error fetching documents: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new APIException(e.getMessage()));
        }
    }

    @PostMapping("/update-with-file")
    public ResponseEntity<?> updateVendorDocuments(
            @RequestParam("username") String username,
            @RequestParam("file") MultipartFile file,
            @RequestParam("documents") String documentsJson) {
        try {
            logger.info("Received update request - Username: {}", username);
            logger.info("File details - Name: {}, Size: {}, Content Type: {}",
                    file.getOriginalFilename(), file.getSize(), file.getContentType());
            logger.info("Documents JSON: {}", documentsJson);

            // Parse JSON to verify format
            ObjectMapper mapper = new ObjectMapper();
            List<Map<String, Object>> documents = mapper.readValue(documentsJson,
                    new TypeReference<List<Map<String, Object>>>() {});
            logger.info("Parsed documents data: {}", documents);

            documentService.updateVendorDocuments(username, file, documentsJson);

            return ResponseEntity.ok()
                    .body(new HashMap<String, String>() {{
                        put("message", "Documents updated successfully");
                    }});

        } catch (Exception e) {
            logger.error("Error processing document update", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new HashMap<String, String>() {{
                        put("message", "Error updating documents: " + e.getMessage());
                        put("error", e.getClass().getSimpleName());
                    }});
        }
    }

}