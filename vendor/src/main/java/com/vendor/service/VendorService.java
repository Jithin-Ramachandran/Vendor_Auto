package com.vendor.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vendor.dto.DocumentDTO;
import com.vendor.dto.UserDetailsDto;
import com.vendor.dto.VendorDetailsDto;
import com.vendor.entity.Document;
import com.vendor.entity.User;
import com.vendor.entity.Vendor;
import com.vendor.exception.ResourceNotFoundException;
import com.vendor.repository.DocumentRepository;
import com.vendor.repository.UserRepository;
import com.vendor.repository.VendorRepository;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
public class VendorService {

    private final VendorRepository vendorRepository;
    private final JavaMailSender javaMailSender;
    private final UserRepository userRepository;
    private final DocumentRepository documentRepository;

    @Autowired
    public VendorService(VendorRepository vendorRepository, JavaMailSender javaMailSender, UserRepository userRepository, DocumentRepository documentRepository) {
        this.vendorRepository = vendorRepository;
        this.javaMailSender = javaMailSender;
        this.userRepository = userRepository;
        this.documentRepository = documentRepository;
    }

    public Vendor createVendor(Vendor vendor) {
        System.out.println("Employee Created: " + vendor);
        return vendorRepository.save(vendor);
    }

    public Vendor getVendorByUsername(String username) {
        return vendorRepository.findByUserUsername(username)
                .orElseThrow(() -> new RuntimeException("Vendor not found for user: " + username));
    }

   public Vendor getVendorById(Long id) {
        return vendorRepository.getVendorById(id);
   }

    public Vendor getVendorByName(String vendorName) {
        Vendor vendor = vendorRepository.findByNameIgnoreCase(vendorName);
        if(vendor == null) {
            throw new RuntimeException("Vendor not found with name: " + vendorName);
        }
        return vendor;
    }

//    public VendorDetailsDto getVendorDetailsByUsername(String username) {
//        User user = userRepository.findByUsername(username)
//                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
//
//        Vendor vendor = user.getVendor();
//        if (vendor == null) {
//            throw new ResourceNotFoundException("Vendor details not found for this user");
//        }
//
//        return new VendorDetailsDto(
//                vendor.getName(),
//                vendor.getEmail(),
//                vendor.getVendorLicense()
//        );
//    }

    public UserDetailsDto getVendorDetailsByUsername(String username) {
        System.out.println("Attempting to find user with username: " + username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return new UserDetailsDto(
                user.getName(),
                user.getUsername(),
                user.getEmail()
        );
    }

    public Vendor updateVendor(Long id, Vendor updatedVendor) {

        // Retrieve the existing vendor
        Vendor existingVendor = vendorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found with id: " + id));

        // Update only the fields that are not null in the incoming object
        if (updatedVendor.getName() != null) {
            existingVendor.setName(updatedVendor.getName());
        }
        if (updatedVendor.getEmail() != null) {
            existingVendor.setEmail(updatedVendor.getEmail());
        }
        if (updatedVendor.getVendorLicense() != null) {
            existingVendor.setVendorLicense(updatedVendor.getVendorLicense());
        }
        if (updatedVendor.getExpiryDate() != null) {
            existingVendor.setExpiryDate(updatedVendor.getExpiryDate());
        }
        return vendorRepository.save(existingVendor);
    }

    public void deleteVendorById(Long id) {
        vendorRepository.deleteById(id);
    }


    public List<Vendor> getAllVendors() throws ResourceNotFoundException {
        try {
            List<Vendor> allVendors = vendorRepository.findAll();
            return allVendors;
        } catch (Exception e) {
            throw new ResourceNotFoundException("Couldn't fetch all Vendors");
        }

    }


    //@Scheduled(cron = "0 * * * * ?") // Runs every minute for testing;
    public void checkAndSendExpiryNotifications() {
        System.out.println("Check Expiry Notifications");
        List<Vendor> vendors = vendorRepository.findAll();
        LocalDate currentDate = LocalDate.now();

        // Collect vendors with expired license or licenses expiring within 30 days
        StringBuilder expiringVendorsList = new StringBuilder();
        for (Vendor vendor : vendors) {
            if (vendor.getExpiryDate() != null) {
                long daysDifference = ChronoUnit.DAYS.between(currentDate, vendor.getExpiryDate());
                if (daysDifference <= 30) {
                    expiringVendorsList.append(formatVendorDetails(vendor, daysDifference));
                }
            }
        }

        // If there are vendors to notify about, send the email
        if (!expiringVendorsList.isEmpty()) {
            sendLicenseExpiryNotification(expiringVendorsList.toString());
        }
    }

    private void sendLicenseExpiryNotification(String vendorDetails) {
        System.out.println("Sending license expiry notification");

        String notificationRecipient = "jithin.ramachandran@resemblesystems.com";
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(notificationRecipient);
        message.setSubject("License Expiry Notifications for Vendors");
        message.setText("The following vendors have licenses that are expired or will expire within 30 days:\n\n"
                + vendorDetails + "\n\n"
                + "Please take necessary actions to renew these licenses.\n\n"
                + "Thank you,\n"
                + "Vendor Management System");
        javaMailSender.send(message);

        System.out.println("License expiry notification sent to " + notificationRecipient);
    }

    private String formatVendorDetails(Vendor vendor, long daysDifference) {
        String status = daysDifference < 0 ? "Expired" : "Expiring in " + daysDifference + " days";
        return String.format("""
            Vendor Name: %s
            Vendor License: %s
            Email: %s
            License Expiry Date: %s
            Status: %s
            
            """,
                vendor.getName(),
                vendor.getVendorLicense(),
                vendor.getEmail(),
                vendor.getExpiryDate(),
                status);
    }

    @Transactional
    public void updateVendorDocuments(String username, String documentsJson, List<MultipartFile> files, List<Integer> fileIndices) {
        try {
            // Get vendor details from username
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));

            Vendor vendor = user.getVendor();
            if (vendor == null) {
                throw new ResourceNotFoundException("Vendor not found for user: " + username);
            }

            // Parse documents JSON
            ObjectMapper mapper = new ObjectMapper();
            List<DocumentDTO> documents = mapper.readValue(
                    documentsJson,
                    mapper.getTypeFactory().constructCollectionType(List.class, DocumentDTO.class)
            );

            // Process each file and its corresponding document data
            for (int i = 0; i < files.size(); i++) {
                MultipartFile file = files.get(i);
                int documentIndex = fileIndices.get(i);
                DocumentDTO documentDTO = documents.get(documentIndex);

                Document document = new Document();
                document.setVendor(vendor);
                document.setDocumentTypeId(documentDTO.getDocumentTypeId());
                document.setExpiryDate(LocalDate.parse(documentDTO.getExpiryDate()));
                document.setFileName(file.getOriginalFilename());

                // Save file and get path
                String filePath = saveFile(file);
                document.setFilePath(filePath);

                // Save document to database
                documentRepository.save(document);

                // Update vendor's expiry date if this document's expiry is earlier
                if (vendor.getExpiryDate() == null ||
                        document.getExpiryDate().isBefore(vendor.getExpiryDate())) {
                    vendor.setExpiryDate(document.getExpiryDate());
                    vendorRepository.save(vendor);
                }
            }

        } catch (IOException e) {
            throw new RuntimeException("Error processing document upload: " + e.getMessage(), e);
        }
    }

    private String saveFile(MultipartFile file) throws IOException {
        String uploadDir = "uploads/vendor-documents/";
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Path path = Paths.get(uploadDir + fileName);
        Files.createDirectories(path.getParent());
        Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
        return fileName;
    }

}
