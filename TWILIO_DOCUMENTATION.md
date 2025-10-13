# Twilio WhatsApp Integration Documentation

## Overview

This document provides comprehensive documentation for the Twilio integration in the AgriWealth application. The system uses Twilio for:

1. **OTP Authentication** - Sending and verifying one-time passwords via WhatsApp (with SMS fallback)
2. **Task Management Communication** - Sending task notifications to workers via WhatsApp
3. **WhatsApp Webhook Handling** - Processing responses from workers, including text and image messages

## Setup Requirements

### 1. Twilio Account Setup

1. Create a Twilio account at [https://www.twilio.com/](https://www.twilio.com/)
2. Navigate to the Twilio Console to get your Account SID and Auth Token
3. Set up a WhatsApp Sandbox or Business Profile
4. Create a Verify Service for OTP functionality

### 2. Environment Configuration

The following environment variables should be configured:

```
# Main Twilio configuration
TWILIO_ACCOUNT_SID=your_account_sid
TWILIO_AUTH_TOKEN=your_auth_token
TWILIO_WHATSAPP_NUMBER=+14155238886  # Your Twilio WhatsApp number

# Twilio Verify service for OTP
TWILIO_VERIFY_SERVICE_SID=your_verify_service_sid
```

These variables can be set in your environment or in the `application.yml` file. Current configuration allows fallback to default values for development purposes.

## Core Components

### 1. Twilio Configuration

`TwilioConfig` class initializes the Twilio SDK with your credentials and provides access to configuration values.

```java
@Configuration
public class TwilioConfig {
    // Handles Twilio initialization with Account SID and Auth Token
    // Provides access to WhatsApp number and credentials
}
```

### 2. OTP Authentication Service

`TwilioService` handles OTP operations for authentication:

- `sendWhatsAppOTP(phoneNumber)` - Sends OTP via WhatsApp with SMS fallback
- `sendSMSOTP(phoneNumber)` - Sends OTP via SMS only
- `verifyOTP(phoneNumber, otpCode)` - Verifies the OTP code entered by user

Example usage:
```java
// Send OTP to user's phone
twilioService.sendWhatsAppOTP("+911234567890");

// Verify OTP entered by user
VerificationCheck result = twilioService.verifyOTP("+911234567890", "123456");
if (result.getValid()) {
    // OTP is valid, proceed with authentication
}
```

### 3. WhatsApp Communication Service

`WhatsAppService` manages sending messages to workers:

- `sendTaskAssignmentMessage(task)` - Sends task notification to worker
- `downloadAndSaveMedia(mediaUrl, authToken)` - Downloads and saves media from WhatsApp
- `sendTaskCompletionConfirmation(phoneNumber, taskTitle)` - Sends confirmation when task is completed

Example usage:
```java
// Notify worker about new task
whatsAppService.sendTaskAssignmentMessage(newTask);
```

### 4. WhatsApp Webhook Controller

`WhatsAppWebhookController` processes incoming WhatsApp messages:

- Handles text messages with "completed" to mark tasks as done
- Processes image uploads as task completion proof
- Updates task status based on worker responses

## Workflow Diagrams

### OTP Authentication Flow

1. User requests password reset with phone number
2. System sends OTP via WhatsApp (falls back to SMS if WhatsApp fails)
3. User enters OTP code
4. System verifies code using Twilio Verify API
5. If valid, user can reset password

### Task Assignment Flow

1. Manager assigns task to worker
2. System sends WhatsApp notification to worker with task details
3. Worker completes task and replies "completed" or sends photo
4. Webhook controller processes reply and updates task status
5. System sends confirmation message to worker

## Setting Up Twilio Verify for WhatsApp

1. In Twilio Console, go to Verify > Services
2. Create a new Verify Service
3. Enable WhatsApp channel
4. Configure service to use your WhatsApp Sender
5. Set up webhook URL for status callbacks (optional)

## Setting Up WhatsApp Webhook

1. Create a publicly accessible endpoint (use ngrok for local development)
2. Configure your webhook URL in Twilio Console under WhatsApp Sandbox settings
3. Set the webhook URL to: `https://your-domain.com/whatsapp/webhook`
4. Ensure your server accepts POST requests with form-encoded data

## Testing the Integration

### Testing OTP Functionality

1. Use the `/auth/forgot-password` endpoint with a valid phone number
2. Check the phone receives an OTP via WhatsApp or SMS
3. Use the `/auth/verify-otp` endpoint with the phone number and OTP
4. Verify successful validation

### Testing WhatsApp Communication

1. Assign a task to a worker with a valid WhatsApp-enabled number
2. Verify the worker receives the notification
3. Reply "completed" or send an image from the worker's phone
4. Verify the task status is updated in the system

## Troubleshooting

### Common Issues

1. **OTP not received**: 
   - Verify the phone number is in E.164 format (+911234567890)
   - Check Twilio console for error messages
   - Verify the WhatsApp number is properly set up

2. **WhatsApp webhook not working**:
   - Ensure URL is publicly accessible
   - Check webhook logs in Twilio Console
   - Verify the server is properly parsing form data

3. **Media download failing**:
   - Check that the uploads directory exists and is writable
   - Verify authentication parameters are correctly passed

4. **Twilio errors in logs**:
   - Check account balance
   - Verify service SIDs are correct
   - Ensure phone numbers are in the correct format

### Debugging

Enable debug logging by adding this to `application.yml`:

```yaml
logging:
  level:
    com.nitroxen.demo.service: DEBUG
    com.twilio: DEBUG
```

## References

- [Twilio WhatsApp API Documentation](https://www.twilio.com/docs/whatsapp/api)
- [Twilio Verify API Documentation](https://www.twilio.com/docs/verify/api)
- [Twilio Java SDK Documentation](https://www.twilio.com/docs/libraries/java)
