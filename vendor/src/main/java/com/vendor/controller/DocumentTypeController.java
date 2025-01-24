package com.vendor.controller;

import com.vendor.entity.DocumentType;
import com.vendor.service.DocumentTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/document-types")
public class DocumentTypeController {

    private final DocumentTypeService documentTypeService;

    @Autowired
    public DocumentTypeController(DocumentTypeService documentTypeService) {
        this.documentTypeService = documentTypeService;
    }

    @PostMapping
    public ResponseEntity<?> createDocumentType(@RequestBody DocumentType documentType) {
        try {
            DocumentType createdType = documentTypeService.createDocumentType(documentType);
            return new ResponseEntity<>(createdType, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{typeId}")
    public ResponseEntity<?> updateDocumentType(
            @PathVariable Long typeId,
            @RequestBody DocumentType documentType) {
        try {
            DocumentType updatedType = documentTypeService.updateDocumentType(typeId, documentType);
            return ResponseEntity.ok(updatedType);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{typeId}")
    public ResponseEntity<?> deleteDocumentType(@PathVariable Long typeId) {
        try {
            documentTypeService.deleteDocumentType(typeId);
            return ResponseEntity.ok(Map.of("message", "Document type deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{typeId}")
    public ResponseEntity<?> getDocumentType(@PathVariable Long typeId) {
        try {
            DocumentType documentType = documentTypeService.getDocumentType(typeId);
            return ResponseEntity.ok(documentType);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<List<DocumentType>> getAllDocumentTypes() {
        List<DocumentType> documentTypes = documentTypeService.getAllDocumentTypes();
        return ResponseEntity.ok(documentTypes);
    }

    @GetMapping("/name/{typeName}")
    public ResponseEntity<?> getDocumentTypeByName(@PathVariable String typeName) {
        try {
            DocumentType documentType = documentTypeService.getDocumentTypeByName(typeName);
            return ResponseEntity.ok(documentType);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    //method for toggling the mandatory field
    @PatchMapping("/{typeId}/toggle-mandatory")
    public ResponseEntity<?> toggleMandatory(@PathVariable Long typeId) {
        try {
            // Fetch the document type
            DocumentType documentType = documentTypeService.getDocumentType(typeId);

            // Toggle the isMandatory field
            documentType.toggleMandatory();

            // Save the updated document type
            DocumentType updatedDocumentType = documentTypeService.updateDocumentType(typeId, documentType);

            return ResponseEntity.ok(updatedDocumentType);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}