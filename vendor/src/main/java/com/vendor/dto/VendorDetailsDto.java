package com.vendor.dto;

public class VendorDetailsDto {
    private String name;
    private String email;
    private String vendorLicense;

    // Constructor
    public VendorDetailsDto(String name, String email, String vendorLicense) {
        this.name = name;
        this.email = email;
        this.vendorLicense = vendorLicense;
    }

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getVendorLicense() {
        return vendorLicense;
    }

    public void setVendorLicense(String vendorLicense) {
        this.vendorLicense = vendorLicense;
    }
}