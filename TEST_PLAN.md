# AgriWealth - New Functionalities Test Plan

## Overview
This document outlines all the new functionalities added to the AgriWealth farm management system and provides a comprehensive test plan.

## New Functionalities Added

### 1. **Farm Management (OwnerFarmController)**
**Base URL:** `/api/owner/farms`

#### Endpoints:
- `POST /api/owner/farms` - Create a new farm
- `GET /api/owner/farms` - Get all farms owned by authenticated user
- `GET /api/owner/farms/{id}` - Get farm by ID
- `PUT /api/owner/farms/{id}` - Update a farm
- `DELETE /api/owner/farms/{id}` - Delete a farm
- `GET /api/owner/farms/{id}/remaining-area` - Get remaining available area

#### Test Cases:
1. **Create Farm**
   - Valid farm data (name, location, total area)
   - Invalid data (missing required fields)
   - Duplicate farm names
   - Unauthorized access (non-owner)

2. **List Farms**
   - Owner sees only their farms
   - Empty list for new owner
   - Proper pagination if implemented

3. **Get Farm Details**
   - Valid farm ID owned by user
   - Invalid farm ID
   - Farm ID owned by different user (should fail)

4. **Update Farm**
   - Valid updates
   - Invalid data
   - Unauthorized access

5. **Delete Farm**
   - Valid deletion
   - Farm with dependencies (polyhouses)
   - Unauthorized access

6. **Remaining Area Calculation**
   - Farm with no polyhouses (should equal total area)
   - Farm with polyhouses (should subtract polyhouse areas)
   - Invalid farm ID

### 2. **Polyhouse Management (OwnerPolyhouseController)**
**Base URL:** `/api/owner`

#### Endpoints:
- `POST /api/owner/farms/{farmId}/polyhouses` - Create polyhouse in farm
- `GET /api/owner/farms/{farmId}/polyhouses` - Get all polyhouses in farm
- `GET /api/owner/polyhouses/{id}` - Get polyhouse by ID
- `PUT /api/owner/polyhouses/{id}` - Update polyhouse
- `DELETE /api/owner/polyhouses/{id}` - Delete polyhouse
- `GET /api/owner/polyhouses/{id}/with-zones` - Get polyhouse with zones
- `GET /api/owner/polyhouses/{id}/zones` - Get zones in polyhouse

#### Test Cases:
1. **Create Polyhouse**
   - Valid polyhouse data
   - Area exceeds farm remaining area
   - Invalid farm ID
   - Unauthorized access

2. **List Polyhouses**
   - Valid farm ID
   - Empty farm
   - Invalid farm ID

3. **Polyhouse Operations**
   - CRUD operations
   - Zone relationships
   - Area calculations

### 3. **Zone Management (OwnerZoneController)**
**Base URL:** `/api/owner`

#### Endpoints:
- `POST /api/owner/polyhouses/{polyhouseId}/zones` - Create zone in polyhouse
- `GET /api/owner/polyhouses/{polyhouseId}/zones` - Get all zones in polyhouse
- `GET /api/owner/polyhouses/{polyhouseId}/zones/{zoneId}` - Get zone by ID
- `PUT /api/owner/zones/{id}` - Update zone
- `DELETE /api/owner/zones/{id}` - Delete zone

#### Test Cases:
1. **Create Zone**
   - Valid zone data
   - Area exceeds polyhouse remaining area
   - Invalid polyhouse ID
   - Reservoir assignment

2. **Zone Operations**
   - CRUD operations
   - Reservoir relationships
   - Area calculations

### 4. **Reservoir Management (OwnerReservoirController)**
**Base URL:** `/api/owner`

#### Endpoints:
- `POST /api/owner/farms/{farmId}/reservoirs` - Create reservoir in farm
- `GET /api/owner/farms/{farmId}/reservoirs` - Get all reservoirs in farm
- `GET /api/owner/reservoirs/{id}` - Get reservoir by ID
- `PUT /api/owner/reservoirs/{id}` - Update reservoir
- `DELETE /api/owner/reservoirs/{id}` - Delete reservoir

#### Test Cases:
1. **Create Reservoir**
   - Valid reservoir data
   - Capacity validation
   - Invalid farm ID

2. **Reservoir Operations**
   - CRUD operations
   - Zone assignments
   - Capacity tracking

### 5. **Manager Functionality (ManagerController)**
**Base URL:** `/api/manager`

#### Endpoints:
- `POST /api/manager/workers` - Create worker
- `GET /api/manager/workers` - View assigned workers
- `GET /api/manager/farms` - View assigned farms
- `GET /api/manager/polyhouses` - View assigned polyhouses
- `GET /api/manager/zones` - View assigned zones

#### Test Cases:
1. **Worker Management**
   - Create workers
   - View assigned workers
   - Unauthorized access

2. **Farm Visibility**
   - View assigned farms
   - Access restrictions

### 6. **Enhanced Admin Functionality (AdminController)**
**Base URL:** `/api/admin`

#### New Endpoints:
- `GET /api/admin/owners/{id}` - Get owner by ID
- `GET /api/admin/revenue` - Revenue tracking (placeholder)

#### Test Cases:
1. **Owner Management**
   - Enhanced owner operations
   - Revenue tracking

## Test Data Requirements

### Sample Farm Data:
```json
{
  "name": "Green Valley Farm",
  "location": "California, USA",
  "totalArea": 100.0,
  "description": "Organic vegetable farm"
}
```

### Sample Polyhouse Data:
```json
{
  "name": "Tomato House 1",
  "area": 25.0,
  "description": "Climate-controlled tomato cultivation"
}
```

### Sample Zone Data:
```json
{
  "name": "Zone A",
  "area": 10.0,
  "cropType": "Tomatoes",
  "description": "High-yield tomato zone"
}
```

### Sample Reservoir Data:
```json
{
  "name": "Main Water Tank",
  "capacity": 5000.0,
  "currentLevel": 3000.0,
  "description": "Primary water storage"
}
```

## Authentication Flow Testing

1. **Login as Admin**
   - Phone: +1234567890
   - Password: admin123

2. **Create Owner**
   - Use admin token to create owner

3. **Login as Owner**
   - Use owner credentials

4. **Create Manager**
   - Use owner token to create manager

5. **Login as Manager**
   - Use manager credentials

6. **Create Worker**
   - Use manager token to create worker

## Security Testing

1. **Role-Based Access Control**
   - Verify each role can only access authorized endpoints
   - Test unauthorized access attempts

2. **JWT Token Validation**
   - Valid tokens
   - Expired tokens
   - Invalid tokens
   - Missing tokens

3. **Data Isolation**
   - Owners can only see their own farms
   - Managers can only see assigned farms
   - Workers have limited access

## Performance Testing

1. **Load Testing**
   - Multiple concurrent requests
   - Large datasets
   - Database performance

2. **Area Calculations**
   - Complex farm hierarchies
   - Real-time area updates

## Integration Testing

1. **End-to-End Workflows**
   - Complete farm setup (Farm → Polyhouse → Zone → Reservoir)
   - User management flow
   - Data consistency

2. **Database Relationships**
   - Foreign key constraints
   - Cascade operations
   - Data integrity

## API Documentation Testing

1. **Swagger UI**
   - Access at http://localhost:8081/swagger-ui.html
   - Test all endpoints through UI
   - Verify request/response schemas

2. **OpenAPI Spec**
   - Access at http://localhost:8081/v3/api-docs
   - Validate API specification

## Test Execution Strategy

### Phase 1: Unit Tests
- Run existing test suite
- Verify all new service implementations
- Test repository methods

### Phase 2: Integration Tests
- Test controller endpoints
- Verify security configurations
- Test database operations

### Phase 3: End-to-End Tests
- Complete user workflows
- Cross-role functionality
- Data consistency

### Phase 4: Performance Tests
- Load testing
- Area calculation performance
- Database query optimization

## Expected Results

1. **All CRUD operations work correctly**
2. **Role-based access is enforced**
3. **Area calculations are accurate**
4. **Data relationships are maintained**
5. **API responses are consistent**
6. **Security is properly implemented**

### 7. **Twilio WhatsApp Integration**
**Base URL:** `/whatsapp`

#### Endpoints:
- `POST /whatsapp/webhook` - Receive WhatsApp messages from workers
- `GET /whatsapp/webhook` - Webhook verification endpoint

#### Features:
1. **OTP Authentication**
   - Send OTP via WhatsApp with SMS fallback
   - Verify OTP codes
   - Phone number validation

2. **Task Notifications**
   - Send task assignments to workers via WhatsApp
   - Include task details, deadline, and instructions
   - Support for emojis and special characters

3. **Webhook Processing**
   - Process "completed" text messages
   - Handle image uploads as task completion proof
   - Update task status automatically

#### Test Cases:

##### A. OTP Functionality Tests (TwilioServiceTest - 25 tests)
1. **Send WhatsApp OTP**
   - Valid phone number
   - Invalid phone format
   - Null/empty phone number
   - Multiple phone formats (US, India, UK, China)
   - Concurrent OTP requests
   - WhatsApp failure with SMS fallback

2. **Send SMS OTP**
   - Valid phone number
   - Invalid phone number
   - Null phone number

3. **Verify OTP**
   - Valid OTP code
   - Invalid OTP code
   - Wrong OTP code
   - Expired OTP
   - Null/empty OTP code
   - Multiple OTP formats (4, 6, 8 digits)
   - Multiple verification attempts

4. **Utility Functions**
   - Phone number masking for security
   - Twilio initialization

##### B. Configuration Tests (TwilioConfigTest - 16 tests)
1. **Configuration Getters**
   - WhatsApp number retrieval
   - Account SID retrieval
   - Auth Token retrieval
   - Getter consistency

2. **Initialization**
   - Twilio SDK initialization
   - Multiple initialization calls
   - Valid credentials format

3. **Validation**
   - E.164 phone format
   - Account SID format (AC prefix, 34 chars)
   - Auth Token minimum length
   - No spaces in phone numbers
   - Alphanumeric SID validation
   - Configuration immutability
   - International phone numbers

##### C. WhatsApp Service Tests (WhatsAppServiceTest - 33 tests)
1. **Task Assignment Messages**
   - Send task notification successfully
   - Worker not found
   - Null task object
   - Null worker ID
   - Null/empty deadline
   - Empty task title
   - Long description (1000 chars)
   - Special characters and emojis
   - Invalid worker phone number
   - Null worker phone number

2. **Media Download**
   - Successful media download
   - No data returned
   - Network errors
   - Null/empty media URL
   - Null auth token
   - Invalid URL format
   - Large files (10MB)
   - Empty files
   - Concurrent downloads
   - Network timeout

3. **Task Completion Confirmation**
   - Send confirmation message
   - Null/empty phone number
   - Invalid phone number
   - Null/empty task title
   - Long task title (500 chars)

##### D. Webhook Controller Tests (WhatsAppWebhookControllerTest - 7 tests)
1. **Message Processing**
   - Task completion via text message
   - Task completion via image upload
   - Worker not found
   - No pending tasks
   - Invalid phone number
   - Webhook verification with challenge
   - Webhook verification without challenge

##### E. Integration Tests (TwilioOTPIntegrationTest - 15 tests)
**Note:** These tests require real Twilio credentials and are enabled via environment variable `TWILIO_INTEGRATION_TESTS=true`

1. **Real API Tests**
   - Send WhatsApp OTP via real API
   - Send SMS OTP via real API
   - Complete OTP flow (send and verify)

2. **Error Handling**
   - Invalid phone number with real API
   - Invalid OTP code
   - Expired OTP code
   - Verify without sending first

3. **Advanced Scenarios**
   - Send OTP multiple times
   - WhatsApp fallback to SMS
   - Different phone formats
   - SMS after WhatsApp failure
   - OTP case sensitivity
   - Rate limiting
   - Multiple wrong verification attempts

#### Test Data:

##### Environment Variables:
```bash
TWILIO_ACCOUNT_SID=ACxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
TWILIO_AUTH_TOKEN=your_auth_token
TWILIO_VERIFY_SERVICE_SID=VAxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
TWILIO_WHATSAPP_NUMBER=+14155238886
```

##### Sample Phone Numbers:
- US: +14155238886
- India: +911234567890
- UK: +447911123456
- China: +861234567890

##### Sample OTP Codes:
- 6-digit: 123456
- 4-digit: 1234
- 8-digit: 12345678

#### Running Twilio Tests:

```bash
# Run all Twilio tests
mvn test -Dtest=*Twilio*,*WhatsApp*

# Run unit tests only
mvn test -Dtest=TwilioServiceTest,TwilioConfigTest,WhatsAppServiceTest,WhatsAppWebhookControllerTest

# Run integration tests (requires credentials)
export TWILIO_INTEGRATION_TESTS=true
mvn test -Dtest=TwilioOTPIntegrationTest

# Run specific test
mvn test -Dtest=TwilioServiceTest#testSendWhatsAppOTP_Success
```

#### Test Coverage Summary:
- **Total Twilio Tests:** 96+
- **Unit Tests:** 81
- **Integration Tests:** 15
- **Controller Tests:** 7
- **Coverage:** Excellent (all major scenarios covered)

#### Security Considerations:
1. Phone numbers are masked in logs
2. OTP codes are never logged
3. Auth tokens are stored securely
4. Webhook endpoints validate Twilio signatures
5. Rate limiting prevents abuse

#### Documentation:
- **Detailed Test Documentation:** See `TWILIO_TEST_DOCUMENTATION.md`
- **Setup Guide:** See `TWILIO_WHATSAPP_SETUP.md`
- **Feature Documentation:** See `TWILIO_DOCUMENTATION.md`

## Notes

- The application runs on port 8081 by default
- Default admin credentials: +1234567890 / admin123
- All new endpoints require JWT authentication
- Owner-scoped endpoints ensure data isolation
- Area calculations prevent over-allocation
- Comprehensive test coverage for all new features
- **Twilio integration includes 96+ test cases covering OTP, WhatsApp messaging, and webhooks**
- Integration tests require real Twilio credentials (set via environment variables)



