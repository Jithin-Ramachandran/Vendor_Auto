package com.vendor.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;

import java.time.LocalDate;

@MappedSuperclass
public class BaseDocument {

    public BaseDocument() {
    }

    @Column(name="DOCUMENTID", nullable=false)
    private String documentId;

    @Column(name="EXPIRYDATE", nullable=false)
    private LocalDate expiryDate;

    @Column(name="FILENAME")
    private String fileName;

    @Column(name="FILEPATH")
    private String filePath;

    public BaseDocument(String documentId, LocalDate expiryDate, String fileName, String filePath) {
        this.documentId = documentId;
        this.expiryDate = expiryDate;
        this.fileName = fileName;
        this.filePath = filePath;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
