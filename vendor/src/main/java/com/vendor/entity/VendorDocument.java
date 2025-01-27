package com.vendor.entity;

import jakarta.persistence.*;
import org.w3c.dom.DocumentType;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "vendor_document")
public class VendorDocument {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long documentId;

    @ManyToOne
    @JoinColumn(name = "vendor_id")
    private Vendor vendor;

//    @ManyToOne
//    @JoinColumn(name = "document_type_id")
//    private DocumentType documentType;

    private String originalFilename;
    private String storedFilename;
    private String filePath;
    private LocalDateTime uploadDate;
    private LocalDate expiryDate;
    private Long fileSize;
    private String contentType;
    private String status;

    // Getters and Setters


    public VendorDocument(Long documentId, Vendor vendor, DocumentType documentType, String originalFilename, String storedFilename, String filePath, LocalDateTime uploadDate, LocalDate expiryDate, Long fileSize, String contentType, String status) {
        this.documentId = documentId;
        this.vendor = vendor;
//        this.documentType = documentType;
        this.originalFilename = originalFilename;
        this.storedFilename = storedFilename;
        this.filePath = filePath;
        this.uploadDate = uploadDate;
        this.expiryDate = expiryDate;
        this.fileSize = fileSize;
        this.contentType = contentType;
        this.status = status;
    }

    public VendorDocument() {}

    public Long getDocumentId() {
        return documentId;
    }

    public void setDocumentId(Long documentId) {
        this.documentId = documentId;
    }

    public Vendor getVendor() {
        return vendor;
    }

    public void setVendor(Vendor vendor) {
        this.vendor = vendor;
    }

    public DocumentType getDocumentType() {
        return documentType;
    }

    public void setDocumentType(DocumentType documentType) {
        this.documentType = documentType;
    }

    public String getOriginalFilename() {
        return originalFilename;
    }

    public void setOriginalFilename(String originalFilename) {
        this.originalFilename = originalFilename;
    }

    public String getStoredFilename() {
        return storedFilename;
    }

    public void setStoredFilename(String storedFilename) {
        this.storedFilename = storedFilename;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public LocalDateTime getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(LocalDateTime uploadDate) {
        this.uploadDate = uploadDate;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}