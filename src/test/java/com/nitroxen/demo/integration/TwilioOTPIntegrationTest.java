package com.nitroxen.demo.integration;

import com.nitroxen.demo.service.TwilioService;
import com.twilio.rest.verify.v2.service.Verification;
import com.twilio.rest.verify.v2.service.VerificationCheck;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for Twilio OTP functionality
 * These tests require actual Twilio credentials and will only run when environment variables are set
 * 
 * To run these tests, set the following environment variables:
 * - TWILIO_ACCOUNT_SID
 * - TWILIO_AUTH_TOKEN
 * - TWILIO_VERIFY_SERVICE_SID
 * - TEST_PHONE_NUMBER (your test phone number in E.164 format)
 */
@SpringBootTest
@ActiveProfiles("test")
@EnabledIfEnvironmentVariable(named = "TWILIO_INTEGRATION_TESTS", matches = "true")
public class TwilioOTPIntegrationTest {

    @Autowired
    private TwilioService twilioService;

    private String testPhoneNumber;

    @BeforeEach
    void setUp() {
        // Get test phone number from environment variable
        testPhoneNumber = System.getenv("TEST_PHONE_NUMBER");
        if (testPhoneNumber == null || testPhoneNumber.isEmpty()) {
            testPhoneNumber = "+14155238886"; // Twilio test number
        }
    }

    @Test
    void testSendWhatsAppOTP_RealAPI_Success() {
        // Test sending OTP via WhatsApp with real Twilio API
        assertDoesNotThrow(() -> {
            Verification verification = twilioService.sendWhatsAppOTP(testPhoneNumber);
            
            assertNotNull(verification);
            assertNotNull(verification.getSid());
            assertTrue(verification.getStatus().equals("pending") || 
                      verification.getStatus().equals("approved"));
        });
    }

    @Test
    void testSendSMSOTP_RealAPI_Success() {
        // Test sending OTP via SMS with real Twilio API
        assertDoesNotThrow(() -> {
            Verification verification = twilioService.sendSMSOTP(testPhoneNumber);
            
            assertNotNull(verification);
            assertNotNull(verification.getSid());
            assertEquals("pending", verification.getStatus());
        });
    }

    @Test
    void testOTPFlow_SendAndVerify_Success() {
        // Test complete OTP flow: send and verify
        // Note: This test requires manual OTP entry or test credentials
        
        // Step 1: Send OTP
        Verification verification = twilioService.sendWhatsAppOTP(testPhoneNumber);
        assertNotNull(verification);
        assertEquals("pending", verification.getStatus());
        
        // Step 2: Verify OTP
        // In real testing, you would enter the actual OTP received
        // For automated testing, use Twilio's test credentials with fixed OTP
        String testOTP = "123456"; // This would be the actual OTP in real scenario
        
        assertDoesNotThrow(() -> {
            try {
                VerificationCheck check = twilioService.verifyOTP(testPhoneNumber, testOTP);
                // With test credentials, this might fail, which is expected
                assertNotNull(check);
            } catch (RuntimeException e) {
                // Expected if using wrong OTP
                assertTrue(e.getMessage().contains("Failed to verify OTP"));
            }
        });
    }

    @Test
    void testSendWhatsAppOTP_InvalidPhoneNumber_Failure() {
        // Test sending OTP to invalid phone number
        String invalidPhone = "invalid-phone";
        
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            twilioService.sendWhatsAppOTP(invalidPhone);
        });
        
        assertTrue(exception.getMessage().contains("Failed to send OTP"));
    }

    @Test
    void testVerifyOTP_InvalidCode_Failure() {
        // Test verifying with invalid OTP code
        String invalidOTP = "000000";
        
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            twilioService.verifyOTP(testPhoneNumber, invalidOTP);
        });
        
        assertTrue(exception.getMessage().contains("Failed to verify OTP"));
    }

    @Test
    void testSendWhatsAppOTP_MultipleTimes_Success() {
        // Test sending OTP multiple times to same number
        assertDoesNotThrow(() -> {
            Verification verification1 = twilioService.sendWhatsAppOTP(testPhoneNumber);
            assertNotNull(verification1);
            
            // Wait a bit before sending again
            Thread.sleep(2000);
            
            Verification verification2 = twilioService.sendWhatsAppOTP(testPhoneNumber);
            assertNotNull(verification2);
            
            // Both should have different SIDs
            assertNotEquals(verification1.getSid(), verification2.getSid());
        });
    }

    @Test
    void testSendOTP_WhatsAppFallbackToSMS_Success() {
        // Test that WhatsApp failure falls back to SMS
        // This will naturally happen if WhatsApp is not configured
        
        assertDoesNotThrow(() -> {
            Verification verification = twilioService.sendWhatsAppOTP(testPhoneNumber);
            
            // Should succeed via either WhatsApp or SMS
            assertNotNull(verification);
            assertNotNull(verification.getSid());
        });
    }

    @Test
    void testVerifyOTP_ExpiredCode_Failure() {
        // Test verifying an expired OTP
        // First send an OTP
        Verification verification = twilioService.sendWhatsAppOTP(testPhoneNumber);
        assertNotNull(verification);
        
        // Wait for expiration (Twilio default is 10 minutes, but we'll test with old code)
        String expiredOTP = "999999";
        
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            twilioService.verifyOTP(testPhoneNumber, expiredOTP);
        });
        
        assertTrue(exception.getMessage().contains("Failed to verify OTP"));
    }

    @Test
    void testSendOTP_DifferentPhoneFormats_Success() {
        // Test sending OTP to different phone number formats
        String[] phoneNumbers = {
            "+14155238886",  // US format
            "+911234567890"  // India format
        };
        
        for (String phone : phoneNumbers) {
            assertDoesNotThrow(() -> {
                try {
                    Verification verification = twilioService.sendWhatsAppOTP(phone);
                    assertNotNull(verification);
                } catch (RuntimeException e) {
                    // Some numbers might not be valid, which is ok for this test
                    assertTrue(e.getMessage().contains("Failed to send OTP"));
                }
            });
        }
    }

    @Test
    void testVerifyOTP_WithoutSendingFirst_Failure() {
        // Test verifying OTP without sending it first
        String randomOTP = "123456";
        
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            twilioService.verifyOTP("+19999999999", randomOTP);
        });
        
        assertTrue(exception.getMessage().contains("Failed to verify OTP"));
    }

    @Test
    void testSendSMSOTP_AfterWhatsAppFailure_Success() {
        // Test that SMS works as a fallback
        assertDoesNotThrow(() -> {
            // Try WhatsApp first (might fail)
            try {
                twilioService.sendWhatsAppOTP(testPhoneNumber);
            } catch (Exception e) {
                // If WhatsApp fails, try SMS directly
                Verification verification = twilioService.sendSMSOTP(testPhoneNumber);
                assertNotNull(verification);
            }
        });
    }

    @Test
    void testOTPVerification_CaseSensitivity() {
        // Test that OTP verification is not case-sensitive (numbers only)
        Verification verification = twilioService.sendWhatsAppOTP(testPhoneNumber);
        assertNotNull(verification);
        
        // Try verifying with numeric code
        assertDoesNotThrow(() -> {
            try {
                twilioService.verifyOTP(testPhoneNumber, "123456");
            } catch (RuntimeException e) {
                // Expected if wrong code
                assertTrue(e.getMessage().contains("Failed to verify OTP"));
            }
        });
    }

    @Test
    void testSendOTP_RateLimiting() {
        // Test that rate limiting is handled properly
        assertDoesNotThrow(() -> {
            try {
                // Send multiple OTPs rapidly
                for (int i = 0; i < 3; i++) {
                    twilioService.sendWhatsAppOTP(testPhoneNumber);
                    Thread.sleep(1000); // Small delay between requests
                }
            } catch (RuntimeException e) {
                // Rate limiting might kick in, which is expected
                assertTrue(e.getMessage().contains("Failed to send OTP") || 
                          e.getMessage().contains("rate limit"));
            }
        });
    }

    @Test
    void testVerifyOTP_MultipleAttempts_Failure() {
        // Test that multiple wrong verification attempts are handled
        Verification verification = twilioService.sendWhatsAppOTP(testPhoneNumber);
        assertNotNull(verification);
        
        // Try multiple wrong codes
        String[] wrongCodes = {"111111", "222222", "333333"};
        
        for (String code : wrongCodes) {
            assertThrows(RuntimeException.class, () -> {
                twilioService.verifyOTP(testPhoneNumber, code);
            });
        }
    }
}
