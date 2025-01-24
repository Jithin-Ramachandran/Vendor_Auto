package com.vendor.service;

import com.vendor.dto.VendorDocumentDTO;
import com.vendor.entity.DocumentType;
import com.vendor.entity.User;
import com.vendor.entity.Vendor;
import com.vendor.entity.VendorDocument;
import com.vendor.repository.UserRepository;
import com.vendor.repository.VendorDocumentRepository;
import com.vendor.repository.VendorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
public class VendorDocumentService {
    public VendorDocumentService(VendorDocumentRepository documentRepository, VendorDocumentRepository vendorDocumentRepository, com.vendor.repository.VendorRepository vendorRepository, com.vendor.repository.UserRepository userRepository, UserRepository userRepository1) {
        this.documentRepository = documentRepository;
        VendorRepository = vendorRepository;
        this.userRepository = userRepository1;
    }
    private static final Logger logger = LoggerFactory.getLogger(VendorDocumentService.class);

    @Autowired
    private VendorDocumentRepository documentRepository;

    @Autowired
    private DocumentStorageService storageService;

    @Autowired
    private DocumentTypeService documentTypeService;

    @Autowired
    private VendorService vendorService; // You'll need to inject VendorService

    private final VendorRepository VendorRepository;
    private final UserRepository userRepository;

    public VendorDocument uploadDocument(MultipartFile file, Long vendorId,
                                         Long documentTypeId, LocalDate expiryDate) {
        try {
            // Get vendor details
            Vendor vendor = vendorService.getVendorById(vendorId);
            if (vendor == null) {
                throw new RuntimeException("Vendor not found");
            }

            // Get document type details
            DocumentType docType = documentTypeService.getDocumentType(documentTypeId);
            if (docType == null) {
                throw new RuntimeException("Document type not found");
            }

            // Store file with vendor name and document type
            String filePath = storageService.storeFile(
                    file,
                    vendorId,
                    vendor.getName(),
                    docType.getTypeName()  // assuming document type has a getName() method
            );

            VendorDocument document = new VendorDocument();
            document.setVendor(vendor);
            document.setDocumentType(docType);
            document.setOriginalFilename(file.getOriginalFilename());
            document.setStoredFilename(filePath.substring(filePath.lastIndexOf("/") + 1));
            document.setFilePath(filePath);
            document.setUploadDate(LocalDateTime.now());
            document.setExpiryDate(expiryDate);
            document.setFileSize(file.getSize());
            document.setContentType(file.getContentType());
            document.setStatus("Active");

            return documentRepository.save(document);
        } catch (Exception e) {
            throw new RuntimeException("Could not store the file. Error: " + e.getMessage());
        }
    }

    public List<VendorDocument> getVendorDocuments(Long vendorId) {
        return documentRepository.findByVendorId(vendorId);
    }

    public List<VendorDocumentDTO> getVendorDocumentsDTO(Long vendorId) {
        List<VendorDocument> documents = documentRepository.findByVendorId(vendorId);
        return documents.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

public Optional<VendorDocument> getDocument(Long documentId) {
    return documentRepository.findById(documentId);
}

    // Add new method for getting document content
    public byte[] getDocumentContent(Long documentId) {
        VendorDocument document = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found with id: " + documentId));
        try {
            byte[] content = storageService.retrieveFile(document.getFilePath());
            if (content == null || content.length == 0) {
                throw new RuntimeException("Document content is empty for id: " + documentId);
            }
            return content;
        } catch (Exception e) {
            logger.error("Error retrieving document content for id {}: {}", documentId, e.getMessage());
            throw new RuntimeException("Error retrieving document content: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public List<VendorDocument> getAllDocuments() {
        try {
            logger.debug("Fetching all vendor documents");
            List<VendorDocument> documents = documentRepository.findAll();

            // Validate and ensure all necessary relationships are loaded
            List<VendorDocument> validatedDocuments = new ArrayList<>();
            for (VendorDocument doc : documents) {
                try {
                    // Validate vendor information
                    if (doc.getVendor() == null) {
                        logger.warn("Document ID {} has no associated vendor", doc.getDocumentId());
                        continue;
                    }

                    // Validate document type information
                    if (doc.getDocumentType() == null) {
                        logger.warn("Document ID {} has no associated document type", doc.getDocumentId());
                        continue;
                    }

                    // Validate file path
                    if (doc.getFilePath() == null || doc.getFilePath().isEmpty()) {
                        logger.warn("Document ID {} has no file path", doc.getDocumentId());
                        continue;
                    }

                    // Add only valid documents to the result list
                    validatedDocuments.add(doc);
                } catch (Exception e) {
                    logger.error("Error processing document ID {}: {}", doc.getDocumentId(), e.getMessage());
                }
            }

            logger.info("Successfully fetched {} valid documents out of {} total documents",
                    validatedDocuments.size(), documents.size());

            return validatedDocuments;

        } catch (Exception e) {
            logger.error("Error fetching all documents: ", e);
            throw new RuntimeException("Failed to fetch vendor documents", e);
        }
    }

    // Optional: Add a method to return DTOs instead of entities
    @Transactional(readOnly = true)
    public List<VendorDocumentDTO> getAllDocumentsDTO() {
        // This method should only be called by admin users
        List<VendorDocument> documents = getAllDocuments();
        return documents.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private VendorDocumentDTO convertToDTO(VendorDocument doc) {
        return VendorDocumentDTO.builder()
                .id(doc.getDocumentId())
                .vendorName(doc.getVendor().getName())
                .documentType(doc.getDocumentType().getTypeName())
                .fileName(doc.getOriginalFilename())
                .uploadDate(doc.getUploadDate())
                .expiryDate(doc.getExpiryDate())
                .status(doc.getStatus())
                .contentType(doc.getContentType())
                .fileSize(doc.getFileSize())
                .build();
    }

    @Transactional
    public void updateVendorDocuments(String username, MultipartFile file, String documentsJson) {
        try {
            logger.info("Starting document update process for user: {}", username);

            // Get user and create vendor if doesn't exist
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found: " + username));

            Vendor vendor = user.getVendor();
            if (vendor == null) {
                vendor = new Vendor();
                vendor.setName(user.getName());
                vendor.setEmail(user.getEmail());
                vendor.setUser(user);
                vendor = VendorRepository.save(vendor);
                user.setVendor(vendor);
                userRepository.save(user);
                logger.info("Created new vendor for user: {}", username);
            }
            logger.info("Found/Created vendor: {}", vendor.getName());

            // Parse documents JSON
            ObjectMapper mapper = new ObjectMapper();
            List<Map<String, Object>> documents = mapper.readValue(
                    documentsJson,
                    new TypeReference<List<Map<String, Object>>>() {}
            );

            for (Map<String, Object> doc : documents) {
                Long documentTypeId = Long.parseLong(doc.get("documentTypeId").toString());
                LocalDate expiryDate = LocalDate.parse(doc.get("expiryDate").toString());

                logger.info("Processing document - Type: {}, Expiry: {}", documentTypeId, expiryDate);

                // Get document type
                DocumentType docType = documentTypeService.getDocumentType(documentTypeId);
                if (docType == null) {
                    throw new RuntimeException("Document type not found: " + documentTypeId);
                }

                // Store file
                String filePath = storageService.storeFile(
                        file,
                        vendor.getId(),
                        vendor.getName(),
                        docType.getTypeName()
                );
                logger.info("File stored at: {}", filePath);

                // Create document record
                VendorDocument document = new VendorDocument();
                document.setVendor(vendor);
                document.setDocumentType(docType);
                document.setOriginalFilename(file.getOriginalFilename());
                document.setStoredFilename(filePath.substring(filePath.lastIndexOf("/") + 1));
                document.setFilePath(filePath);
                document.setUploadDate(LocalDateTime.now());
                document.setExpiryDate(expiryDate);
                document.setFileSize(file.getSize());
                document.setContentType(file.getContentType());
                document.setStatus("Active");

                documentRepository.save(document);
                logger.info("Document record saved with ID: {}", document.getDocumentId());

                // Update vendor's expiry date if this document's expiry is earlier
                if (vendor.getExpiryDate() == null ||
                        expiryDate.isBefore(vendor.getExpiryDate())) {
                    vendor.setExpiryDate(expiryDate);
                    VendorRepository.save(vendor);
                    logger.info("Updated vendor expiry date to: {}", expiryDate);
                }
            }

        } catch (Exception e) {
            logger.error("Error in updateVendorDocuments", e);
            throw new RuntimeException("Failed to update vendor documents: " + e.getMessage(), e);
        }
    }

    public Vendor getVendorByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));
        return user.getVendor();
    }

    @Transactional(readOnly = true)
    public List<VendorDocumentDTO> getVendorDocumentsByUsername(String username) {
        try {
            logger.debug("Fetching documents for vendor with username: {}", username);

            // Get vendor by username
            Vendor vendor = getVendorByUsername(username);
            if (vendor == null) {
                logger.warn("No vendor found for username: {}", username);
                return new ArrayList<>();
            }

            // Get documents for this specific vendor
            List<VendorDocument> documents = documentRepository.findByVendorId(vendor.getId());

            // Convert to DTOs
            List<VendorDocumentDTO> dtos = documents.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());

            logger.info("Found {} documents for vendor: {}", dtos.size(), vendor.getName());
            return dtos;

        } catch (Exception e) {
            logger.error("Error fetching vendor documents for username {}: {}", username, e.getMessage());
            throw new RuntimeException("Failed to fetch vendor documents", e);
        }
    }


}