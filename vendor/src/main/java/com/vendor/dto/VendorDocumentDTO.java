package com.vendor.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VendorDocumentDTO {
    private Long id;
    private String vendorName;
    private String documentType;
    private String fileName;
    private LocalDateTime uploadDate;
    private LocalDate expiryDate;
    private String status;
    private String contentType;
    private Long fileSize;
}