package com.vendor.repository;

import com.vendor.entity.DocumentType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentTypeRepository extends JpaRepository<DocumentType, Long> {
    DocumentType findByTypeNameIgnoreCase(String typeName);
}