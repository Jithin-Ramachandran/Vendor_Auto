package com.vendor.controller;

import com.vendor.dto.UserDetailsDto;
import com.vendor.dto.VendorDetailsDto;
import com.vendor.entity.Vendor;
import com.vendor.exception.ResourceNotFoundException;
import com.vendor.service.VendorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/vendor")
public class VendorController {

    public VendorController(VendorService vendorService) {
        this.vendorService = vendorService;
    }

    @Autowired
    private VendorService vendorService;

//    @PostMapping
//    public ResponseEntity<?> createVendor(@RequestBody Vendor vendor) {
//        try {
//            Vendor savedVendor = vendorService.createVendor(vendor);
//            return new ResponseEntity<>(savedVendor, HttpStatus.CREATED);
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body("Error occurred: " + e.getMessage());
//        }
//    }

    @PostMapping
    public ResponseEntity<?> createVendor(@RequestBody Vendor vendor) {
        try {
            // Log the incoming request
            System.out.println("Received vendor creation request: " + vendor);

            Vendor savedVendor = vendorService.createVendor(vendor);

            // Log the saved vendor
            System.out.println("Vendor created successfully: " + savedVendor);

            return new ResponseEntity<>(savedVendor, HttpStatus.CREATED);
        } catch (Exception e) {
            // Log the error
            System.err.println("Error creating vendor: " + e.getMessage());
            e.printStackTrace();

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error occurred: " + e.getMessage());
        }
    }

    @GetMapping("/details/{username}")
    public ResponseEntity<UserDetailsDto> getVendorDetailsByUsername(@PathVariable String username) {
        try {
            UserDetailsDto userDetails = vendorService.getVendorDetailsByUsername(username);
            return ResponseEntity.ok(userDetails);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/documents/update")
    public ResponseEntity<?> updateVendorDocuments(
            @RequestParam("username") String username,
            @RequestParam("documents") String documentsJson,
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam("fileIndices") List<Integer> fileIndices) {
        try {
            vendorService.updateVendorDocuments(username, documentsJson, files, fileIndices);
            return ResponseEntity.ok().body("Documents updated successfully");
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating documents: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Vendor> getVendorById(@PathVariable Long id) {
        Vendor getVendor = vendorService.getVendorById(id);
        return new ResponseEntity<>(getVendor, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Vendor> updatedVendor(@PathVariable Long id, @RequestBody Vendor vendor) {
        Vendor updatedVendor = vendorService.updateVendor(id, vendor);
        return new ResponseEntity<>(updatedVendor, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteVendor(@PathVariable Long id) {
        vendorService.deleteVendorById(id);
        return ResponseEntity.ok("Vendor Deleted Successfully");
    }

    @GetMapping
    public ResponseEntity<List<Vendor>> getAllVendors() {
        List<Vendor> vendors = vendorService.getAllVendors();
        return new ResponseEntity<>(vendors, HttpStatus.OK);
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<Vendor> getVendorByName( @PathVariable String name) {
        Vendor vendor = vendorService.getVendorByName(name);
        return new ResponseEntity<>(vendor, HttpStatus.OK);
    }

    @GetMapping("/test-scheduler")
    public void testScheduler() {
        vendorService.checkAndSendExpiryNotifications();
    }

}
