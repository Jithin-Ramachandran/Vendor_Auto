package com.vendor.service;

import com.vendor.entity.DocumentType;
import com.vendor.exception.ResourceNotFoundException;
import com.vendor.repository.DocumentTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DocumentTypeService {

    private final DocumentTypeRepository documentTypeRepository;

    @Autowired
    public DocumentTypeService(DocumentTypeRepository documentTypeRepository) {
        this.documentTypeRepository = documentTypeRepository;
    }

    public DocumentType createDocumentType(DocumentType documentType) {
        // Check if document type with same name already exists
        if (documentTypeRepository.findByTypeNameIgnoreCase(documentType.getTypeName()) != null) {
            throw new RuntimeException("Document type with name " + documentType.getTypeName() + " already exists");
        }
        return documentTypeRepository.save(documentType);
    }

    public DocumentType updateDocumentType(Long typeId, DocumentType documentType) {
        DocumentType existingDocType = documentTypeRepository.findById(typeId)
                .orElseThrow(() -> new ResourceNotFoundException("Document type not found with id: " + typeId));

        // Update fields if they are not null
        if (documentType.getTypeName() != null) {
            existingDocType.setTypeName(documentType.getTypeName());
        }
        if (documentType.getDescription() != null) {
            existingDocType.setDescription(documentType.getDescription());
        }
        existingDocType.setMandatory(documentType.isMandatory());

        return documentTypeRepository.save(existingDocType);
    }

    public void deleteDocumentType(Long typeId) {
        if (!documentTypeRepository.existsById(typeId)) {
            throw new ResourceNotFoundException("Document type not found with id: " + typeId);
        }
        documentTypeRepository.deleteById(typeId);
    }

    public DocumentType getDocumentType(Long typeId) {
        return documentTypeRepository.findById(typeId)
                .orElseThrow(() -> new ResourceNotFoundException("Document type not found with id: " + typeId));
    }

    public List<DocumentType> getAllDocumentTypes() {
        return documentTypeRepository.findAll();
    }

    public DocumentType getDocumentTypeByName(String typeName) {
        DocumentType documentType = documentTypeRepository.findByTypeNameIgnoreCase(typeName);
        if (documentType == null) {
            throw new ResourceNotFoundException("Document type not found with name: " + typeName);
        }
        return documentType;
    }
}