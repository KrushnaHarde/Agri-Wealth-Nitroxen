package com.nitroxen.demo.service;

import com.twilio.Twilio;
import com.twilio.rest.verify.v2.service.Verification;
import com.twilio.rest.verify.v2.service.VerificationCheck;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

@Service
@Slf4j
public class TwilioService {

    @Value("${application.twilio.account-sid}")
    private String accountSid;

    @Value("${application.twilio.auth-token}")
    private String authToken;

    @Value("${application.twilio.verify-service-sid}")
    private String verifyServiceSid;

    @PostConstruct
    public void initTwilio() {
        Twilio.init(accountSid, authToken);
        log.info("Twilio initialized successfully");
    }

    /**
     * Send OTP via WhatsApp using Twilio Verify API with SMS fallback
     * @param phoneNumber Phone number in E.164 format
     * @return Verification status
     */
    public Verification sendWhatsAppOTP(String phoneNumber) {
        try {
            log.info("Attempting to send WhatsApp OTP to: {}", maskPhoneNumber(phoneNumber));

            // First try WhatsApp
            Verification verification = Verification.creator(
                    verifyServiceSid,
                    phoneNumber,
                    "whatsapp"
            ).create();

            log.info("WhatsApp OTP sent successfully. Status: {}, SID: {}", verification.getStatus(), verification.getSid());
            return verification;

        } catch (Exception whatsappError) {
            log.warn("WhatsApp OTP failed for {}: {}. Falling back to SMS.",
                    maskPhoneNumber(phoneNumber), whatsappError.getMessage());

            try {
                // Fallback to SMS
                Verification verification = Verification.creator(
                        verifyServiceSid,
                        phoneNumber,
                        "sms"
                ).create();

                log.info("SMS OTP sent successfully as fallback. Status: {}, SID: {}",
                        verification.getStatus(), verification.getSid());
                return verification;

            } catch (Exception smsError) {
                log.error("Both WhatsApp and SMS OTP failed for {}: WhatsApp: {}, SMS: {}",
                        maskPhoneNumber(phoneNumber), whatsappError.getMessage(), smsError.getMessage());
                throw new RuntimeException("Failed to send OTP via WhatsApp or SMS: " + smsError.getMessage(), smsError);
            }
        }
    }

    /**
     * Verify OTP code using Twilio Verify API
     * @param phoneNumber Phone number in E.164 format
     * @param otpCode OTP code to verify
     * @return Verification check result
     */
    public VerificationCheck verifyOTP(String phoneNumber, String otpCode) {
        try {
            log.info("Verifying OTP for: {}", maskPhoneNumber(phoneNumber));

            VerificationCheck verificationCheck = VerificationCheck.creator(verifyServiceSid)
                    .setTo(phoneNumber)
                    .setCode(otpCode)
                    .create();

            log.info("OTP verification result. Status: {}, Valid: {}",
                    verificationCheck.getStatus(), verificationCheck.getValid());
            return verificationCheck;

        } catch (Exception e) {
            log.error("Failed to verify OTP for {}: {}", maskPhoneNumber(phoneNumber), e.getMessage());
            throw new RuntimeException("Failed to verify OTP: " + e.getMessage(), e);
        }
    }

    /**
     * Send OTP via SMS only (alternative method)
     * @param phoneNumber Phone number in E.164 format
     * @return Verification status
     */
    public Verification sendSMSOTP(String phoneNumber) {
        try {
            log.info("Sending SMS OTP to: {}", maskPhoneNumber(phoneNumber));

            Verification verification = Verification.creator(
                    verifyServiceSid,
                    phoneNumber,
                    "sms"
            ).create();

            log.info("SMS OTP sent successfully. Status: {}, SID: {}", verification.getStatus(), verification.getSid());
            return verification;

        } catch (Exception e) {
            log.error("Failed to send SMS OTP to {}: {}", maskPhoneNumber(phoneNumber), e.getMessage());
            throw new RuntimeException("Failed to send OTP via SMS: " + e.getMessage(), e);
        }
    }

    /**
     * Mask phone number for logging security
     * @param phoneNumber Original phone number
     * @return Masked phone number
     */
    private String maskPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.length() < 4) {
            return "****";
        }
        return phoneNumber.substring(0, 3) + "****" + phoneNumber.substring(phoneNumber.length() - 4);
    }
}
