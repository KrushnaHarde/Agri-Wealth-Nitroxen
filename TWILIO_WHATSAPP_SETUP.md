# Twilio WhatsApp OTP Setup Guide

## 🚨 Current Issue Resolution
Your application now has **automatic SMS fallback** when WhatsApp fails. The OTP system will:
1. First attempt to send via WhatsApp
2. If WhatsApp fails, automatically fallback to SMS
3. Inform you which channel was used in the response

## 📱 Twilio WhatsApp Configuration Steps

### Step 1: Create Twilio Verify Service
1. Go to [Twilio Console](https://console.twilio.com/)
2. Navigate to **Verify > Services**
3. Click **"Create new Service"**
4. Enter service name (e.g., "AgriWealth OTP")
5. Copy the **Service SID** (starts with VA...)

### Step 2: Configure WhatsApp Channel
1. In your Verify Service, go to **"Messaging"** tab
2. Find **"WhatsApp"** section
3. **Enable WhatsApp channel**
4. **Important**: You need to complete WhatsApp Business verification

### Step 3: WhatsApp Business Account Setup
⚠️ **Critical Steps for Production:**

#### For Testing (Sandbox):
1. Go to **WhatsApp > Sandbox** in Twilio Console
2. Add your phone number to sandbox
3. Send the join code from your WhatsApp to the sandbox number

#### For Production:
1. **WhatsApp Business Account**: Create at [business.whatsapp.com](https://business.whatsapp.com/)
2. **Business Verification**: Complete Facebook Business verification
3. **Template Approval**: Submit OTP message templates for approval
4. **Phone Number Verification**: Verify your business phone number

### Step 4: Environment Variables
```bash
TWILIO_ACCOUNT_SID=ACxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
TWILIO_AUTH_TOKEN=your_auth_token_here
TWILIO_VERIFY_SERVICE_SID=VAxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
```

## 🔧 Current Workaround (SMS Fallback)
Your application now works with SMS automatically when WhatsApp is not configured:

### Test the OTP System:
```bash
# Request OTP (will try WhatsApp, fallback to SMS)
POST /api/auth/request-otp
{
  "phoneNumber": "+911234567890"
}

# Response will indicate which channel was used:
{
  "status": "pending",
  "message": "OTP sent successfully via SMS (WhatsApp unavailable)",
  "phoneNumber": "+91****7890",
  "success": true
}
```

## 📋 WhatsApp Setup Checklist
- [ ] Twilio Account created
- [ ] Verify Service created
- [ ] WhatsApp channel enabled in service
- [ ] WhatsApp Business Account created
- [ ] Business verification completed
- [ ] Phone number verified
- [ ] Message templates approved
- [ ] Sandbox testing completed

## 🚀 Production Requirements
For production WhatsApp OTP:
1. **Facebook Business Manager** account
2. **WhatsApp Business API** access
3. **Message template approval** (can take 24-48 hours)
4. **Business verification** (can take several days)

## 💡 Immediate Solution
Your application now automatically uses SMS when WhatsApp fails, so you can:
1. ✅ Test OTP functionality immediately with SMS
2. ✅ Complete WhatsApp setup in parallel
3. ✅ Switch to WhatsApp automatically once configured

The system is production-ready with SMS fallback!
