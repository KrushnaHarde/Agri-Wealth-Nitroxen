# AgriWealth - Polyhouse Farm Management System

A comprehensive Spring Boot application for managing polyhouse farms with role-based access control and JWT authentication.

## Features

- **JWT Authentication**: Secure token-based authentication
- **Role-Based Access Control**: ADMIN, OWNER, MANAGER, WORKER roles
- **User Management**: Create and manage users across different roles
- **Password Management**: Secure password change functionality
- **API Documentation**: Comprehensive Swagger/OpenAPI documentation
- **Global Exception Handling**: Centralized error handling

## Technology Stack

- **Backend**: Spring Boot 3.5.6
- **Security**: Spring Security with JWT
- **Database**: MySQL 8.0+
- **Documentation**: OpenAPI/Swagger
- **Build Tool**: Maven
- **Java Version**: 17

## Quick Start

### Prerequisites

- Java 17+
- Maven 3.6+
- MySQL 8.0+

### Database Setup

1. Create a MySQL database:
```sql
CREATE DATABASE agriwealth_db;
```

2. Update database credentials in `application.properties` if needed.

### Running the Application

1. Clone the repository
2. Navigate to the project directory
3. Run the application:
```bash
mvn spring-boot:run
```

The application will start on port 8080.

## Default Admin User

The system creates a default admin user on startup:
- **Phone**: +1234567890
- **Password**: admin123

## API Documentation

Once the application is running, you can access:
- **Swagger UI**: http://localhost:8081/swagger-ui.html
- **API Docs**: http://localhost:8081/v3/api-docs

## API Endpoints

### Authentication
- `POST /api/auth/login` - User login
- `POST /api/auth/change-password` - Change password

### Admin Endpoints
- `POST /api/admin/owners` - Create farm owner
- `GET /api/admin/owners` - Get all farm owners

### Owner Endpoints
- `POST /api/owner/managers` - Create manager
- `POST /api/owner/workers` - Create worker
- `GET /api/owner/managers` - Get all managers
- `GET /api/owner/workers` - Get all workers

### Manager Endpoints
- `POST /api/manager/workers` - Create worker
- `GET /api/manager/workers` - View assigned workers

## Role Hierarchy

```
ADMIN
├── Can create OWNER accounts
└── Can view all owners

OWNER
├── Can create MANAGER accounts
├── Can create WORKER accounts
├── Can view their managers
└── Can view their workers

MANAGER
├── Can create WORKER accounts
└── Can view assigned workers

WORKER
└── Basic access (extensible for future features)
```

## Sample API Usage

### 1. Login
```bash
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "phoneNumber": "+1234567890",
    "password": "admin123"
  }'
```

### 2. Create Owner (Admin only)
```bash
curl -X POST http://localhost:8081/api/admin/owners \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "name": "John Doe",
    "email": "john@example.com",
    "phoneNumber": "+1234567891",
    "password": "password123",
    "role": "OWNER"
  }'
```

### 3. Change Password
```bash
curl -X POST http://localhost:8081/api/auth/change-password \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "currentPassword": "admin123",
    "newPassword": "newPassword123"
  }'
```

## Security Features

- **JWT Token Authentication**: Secure stateless authentication
- **Password Encryption**: BCrypt password hashing
- **Role-Based Authorization**: Method-level security
- **Token Expiration**: Configurable token expiration (24 hours default)
- **CORS Configuration**: Cross-origin resource sharing support

## Error Handling

The application includes comprehensive error handling with:
- Global exception handler
- Validation error responses
- Custom error messages
- HTTP status code mapping

## Configuration

Key configuration properties in `application.properties`:

```properties
# JWT Configuration
application.security.jwt.secret-key=YOUR_SECRET_KEY
application.security.jwt.expiration=86400000

# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/agriwealth_db
spring.datasource.username=root
spring.datasource.password=password
```

## Future Enhancements

- Farm and Polyhouse management
- Zone and Crop management
- Task assignment and tracking
- Weather integration
- Reporting and analytics
- Mobile application support

## License

This project is licensed under the MIT License.
