package com.vendor;

import com.vendor.entity.Vendor;
import com.vendor.service.VendorService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.LocalDate;

import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@SpringBootTest
class VendorApplicationTests {

	@Mock
	private JavaMailSender javaMailSender; // Mock the JavaMailSender dependency

	@InjectMocks
	private VendorService vendorService; // Inject the mock into the service


	@Test
	void contextLoads() {
	}
//
//	@Test
//	void testEmailNotification() throws MessagingException {
//		// Creating a test vendor
//		Vendor vendor = new Vendor();
//		vendor.setName("Test User");
//		vendor.setEmail("arjun@resemblesystems.com");
//		vendor.setExpiryDate(LocalDate.now().plusDays(30)); // Set expiry date 30 days from now
//
//		// Mock the MimeMessage and MimeMessageHelper
//		MimeMessage mockMimeMessage = mock(MimeMessage.class);
//		when(javaMailSender.createMimeMessage()).thenReturn(mockMimeMessage);
//
//		// Send the email notification
//		vendorService.checkAndSendExpiryNotifications();
//
//		// Verify that the send method of JavaMailSender was called
//		verify(javaMailSender, times(1)).send(mockMimeMessage); // Verifying that send() was called exactly once
//
//		System.out.println("Test email notification sent for: " + vendor.getName());
//	}
	
}
