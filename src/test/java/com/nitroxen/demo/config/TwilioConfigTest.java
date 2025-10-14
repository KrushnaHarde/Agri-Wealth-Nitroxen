package com.nitroxen.demo.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for TwilioConfig
 * Tests configuration initialization and getter methods
 */
@ExtendWith(MockitoExtension.class)
public class TwilioConfigTest {

    private TwilioConfig twilioConfig;

    private static final String TEST_ACCOUNT_SID = "ACxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx";
    private static final String TEST_AUTH_TOKEN = "test_auth_token_12345";
    private static final String TEST_WHATSAPP_NUMBER = "+14155238886";

    @BeforeEach
    void setUp() {
        twilioConfig = new TwilioConfig();
        
        // Set private fields using ReflectionTestUtils
        ReflectionTestUtils.setField(twilioConfig, "accountSid", TEST_ACCOUNT_SID);
        ReflectionTestUtils.setField(twilioConfig, "authToken", TEST_AUTH_TOKEN);
        ReflectionTestUtils.setField(twilioConfig, "whatsappNumber", TEST_WHATSAPP_NUMBER);
    }

    @Test
    void testGetWhatsappNumber_Success() {
        // Test that WhatsApp number is correctly retrieved
        String whatsappNumber = twilioConfig.getWhatsappNumber();
        
        assertNotNull(whatsappNumber);
        assertEquals(TEST_WHATSAPP_NUMBER, whatsappNumber);
    }

    @Test
    void testGetAccountSid_Success() {
        // Test that Account SID is correctly retrieved
        String accountSid = twilioConfig.getAccountSid();
        
        assertNotNull(accountSid);
        assertEquals(TEST_ACCOUNT_SID, accountSid);
    }

    @Test
    void testGetAuthToken_Success() {
        // Test that Auth Token is correctly retrieved
        String authToken = twilioConfig.getAuthToken();
        
        assertNotNull(authToken);
        assertEquals(TEST_AUTH_TOKEN, authToken);
    }

    @Test
    void testInitTwilio_Success() {
        // Test that Twilio initialization doesn't throw exceptions
        assertDoesNotThrow(() -> {
            twilioConfig.initTwilio();
        });
    }

    @Test
    void testInitTwilio_WithValidCredentials() {
        // Test initialization with valid format credentials
        ReflectionTestUtils.setField(twilioConfig, "accountSid", "ACxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
        ReflectionTestUtils.setField(twilioConfig, "authToken", "valid_token_123");
        
        assertDoesNotThrow(() -> {
            twilioConfig.initTwilio();
        });
    }

    @Test
    void testWhatsappNumber_E164Format() {
        // Test that WhatsApp number is in E.164 format
        String whatsappNumber = twilioConfig.getWhatsappNumber();
        
        assertTrue(whatsappNumber.startsWith("+"), "WhatsApp number should start with +");
        assertTrue(whatsappNumber.length() >= 10, "WhatsApp number should have at least 10 digits");
    }

    @Test
    void testAccountSid_Format() {
        // Test that Account SID has correct format
        String accountSid = twilioConfig.getAccountSid();
        
        assertTrue(accountSid.startsWith("AC"), "Account SID should start with AC");
        assertTrue(accountSid.length() == 34, "Account SID should be 34 characters long");
    }

    @Test
    void testAuthToken_NotEmpty() {
        // Test that Auth Token is not empty
        String authToken = twilioConfig.getAuthToken();
        
        assertNotNull(authToken);
        assertFalse(authToken.isEmpty(), "Auth Token should not be empty");
        assertTrue(authToken.length() > 0, "Auth Token should have length > 0");
    }

    @Test
    void testConfigurationValues_NotNull() {
        // Test that all configuration values are not null
        assertNotNull(twilioConfig.getAccountSid(), "Account SID should not be null");
        assertNotNull(twilioConfig.getAuthToken(), "Auth Token should not be null");
        assertNotNull(twilioConfig.getWhatsappNumber(), "WhatsApp number should not be null");
    }

    @Test
    void testWhatsappNumber_DifferentCountries() {
        // Test WhatsApp numbers from different countries
        String[] validNumbers = {
            "+14155238886",  // US
            "+911234567890", // India
            "+447911123456", // UK
            "+861234567890"  // China
        };

        for (String number : validNumbers) {
            ReflectionTestUtils.setField(twilioConfig, "whatsappNumber", number);
            String retrievedNumber = twilioConfig.getWhatsappNumber();
            
            assertEquals(number, retrievedNumber);
            assertTrue(retrievedNumber.startsWith("+"));
        }
    }

    @Test
    void testInitTwilio_MultipleInvocations() {
        // Test that multiple initializations don't cause issues
        assertDoesNotThrow(() -> {
            twilioConfig.initTwilio();
            twilioConfig.initTwilio();
            twilioConfig.initTwilio();
        });
    }

    @Test
    void testGetters_Consistency() {
        // Test that getters return consistent values
        String accountSid1 = twilioConfig.getAccountSid();
        String accountSid2 = twilioConfig.getAccountSid();
        
        assertEquals(accountSid1, accountSid2, "Account SID should be consistent");
        
        String authToken1 = twilioConfig.getAuthToken();
        String authToken2 = twilioConfig.getAuthToken();
        
        assertEquals(authToken1, authToken2, "Auth Token should be consistent");
        
        String whatsappNumber1 = twilioConfig.getWhatsappNumber();
        String whatsappNumber2 = twilioConfig.getWhatsappNumber();
        
        assertEquals(whatsappNumber1, whatsappNumber2, "WhatsApp number should be consistent");
    }

    @Test
    void testConfiguration_Immutability() {
        // Test that configuration values don't change after initialization
        String originalAccountSid = twilioConfig.getAccountSid();
        String originalAuthToken = twilioConfig.getAuthToken();
        String originalWhatsappNumber = twilioConfig.getWhatsappNumber();
        
        // Perform some operations
        twilioConfig.initTwilio();
        
        // Verify values haven't changed
        assertEquals(originalAccountSid, twilioConfig.getAccountSid());
        assertEquals(originalAuthToken, twilioConfig.getAuthToken());
        assertEquals(originalWhatsappNumber, twilioConfig.getWhatsappNumber());
    }

    @Test
    void testWhatsappNumber_NoSpaces() {
        // Test that WhatsApp number doesn't contain spaces
        String whatsappNumber = twilioConfig.getWhatsappNumber();
        
        assertFalse(whatsappNumber.contains(" "), "WhatsApp number should not contain spaces");
    }

    @Test
    void testAccountSid_AlphanumericOnly() {
        // Test that Account SID contains only alphanumeric characters
        String accountSid = twilioConfig.getAccountSid();
        
        assertTrue(accountSid.matches("^[a-zA-Z0-9]+$"), 
            "Account SID should contain only alphanumeric characters");
    }

    @Test
    void testAuthToken_MinimumLength() {
        // Test that Auth Token has minimum required length
        String authToken = twilioConfig.getAuthToken();
        
        assertTrue(authToken.length() >= 8, 
            "Auth Token should have at least 8 characters for security");
    }
}
