package com.vendor.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "document_type")
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long typeId;
    private String typeName;
    private String description;
    private boolean isMandatory = true;


}