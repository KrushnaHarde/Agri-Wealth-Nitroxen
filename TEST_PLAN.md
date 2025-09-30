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

## Notes

- The application runs on port 8081 by default
- Default admin credentials: +1234567890 / admin123
- All new endpoints require JWT authentication
- Owner-scoped endpoints ensure data isolation
- Area calculations prevent over-allocation
- Comprehensive test coverage for all new features



