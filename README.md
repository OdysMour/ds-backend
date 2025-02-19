# Pet Insurance Management System Backend

## Overview

This Spring Boot backend application provides a RESTful API for managing pet insurance services, including functionalities for user management, animal registration, health checks, and citizen information management.

## Technologies

- Java 17
- Spring Boot
- Spring Security with JWT Authentication
- Spring Data JPA
- PostgreSQL
- Maven
- Docker

## Prerequisites

- Java 17 or higher
- Docker and Docker Compose
- Maven
- PostgreSQL

## Project Structure

```
src/main/java/gr/odys/ds_backend/
├── api/                  # API interfaces
│   ├── AnimalApi
│   ├── AnimalHealthCheckApi
│   ├── CitizenApi
│   ├── UserApi
│   └── UserProfileApi
├── config/              # Configuration classes
│   ├── Security configs
│   ├── JWT utils
│   └── Rate limiting
├── controller/          # REST controllers
├── entity/              # JPA entities
├── payload/             # Request/Response DTOs
│   ├── request/        # Request objects
│   └── response/       # Response objects
├── repository/          # Spring Data repositories
└── service/             # Business logic services
```

## Key Features

- JWT-based Authentication
- Role-based Access Control (ADMIN, VET, USER)
- Rate Limiting
- REST API for:
  - User Management
  - Animal Registration
  - Health Check Records
  - Citizen Information

## Getting Started

1. Clone the repository
2. Configure environment variables:
   ```bash
   cp .env.example .env
   # Edit .env with your configuration
   ```

3. Start the database using Docker:
   ```bash
   docker-compose up -d
   ```

4. Build and run the application:
   ```bash
   export $(grep -v '^#' .env | xargs) && ./mvnw spring-boot:run
   ```

The server will start on `http://localhost:8080`

## API Documentation

### Authentication Endpoints

- `POST /api/auth/signin` - User login
- `POST /api/auth/signup` - User registration

### Animal Management

- `GET /api/animals` - List all animals
- `POST /api/animals` - Register new animal
- `GET /api/animals/{id}` - Get animal details
- `PUT /api/animals/{id}` - Update animal information
- `DELETE /api/animals/{id}` - Delete animal record

### Health Checks

- `GET /api/animals/{id}/health-checks` - List animal health checks
- `POST /api/animals/{id}/health-checks` - Add new health check
- `GET /api/health-checks/{id}` - Get health check details

### User Management

- `GET /api/users` - List all users (Admin only)
- `GET /api/users/{id}` - Get user details
- `PUT /api/users/{id}` - Update user information
- `DELETE /api/users/{id}` - Delete user (Admin only)

### User Profile Management

- `GET /api/users/{id}/profile` - Get user profile
- `PUT /api/users/{id}/profile` - Update user profile
- `DELETE /api/users/{id}/profile` - Delete user profile

### Citizen Management

- `GET /api/citizens` - List all citizens
- `POST /api/citizens` - Create new citizen
- `GET /api/citizens/{id}` - Get citizen details
- `PUT /api/citizens/{id}` - Update citizen information
- `DELETE /api/citizens/{id}` - Delete citizen

## Security

The application implements several security measures:

- JWT-based authentication
- Role-based access control
- Rate limiting for API endpoints
- Password encryption using BCrypt
- CORS configuration
- Custom error handling

## Development

### Running Tests

```bash
export $(grep -v '^#' .env | xargs) && ./mvnw test
```

### Building for Production

```bash
export $(grep -v '^#' .env | xargs) && ./mvnw clean package
```

### API Testing

A Postman collection is included in the `postman` directory for testing the APIs.

```bash
newman run collection.json -e environment.json
```

## Environment Variables

Key environment variables required:

- `SPRING_DATASOURCE_URL`: Database connection URL
- `SPRING_DATASOURCE_USERNAME`: Database username
- `SPRING_DATASOURCE_PASSWORD`: Database password
- `JWT_SECRET`: Secret key for JWT token generation
- `JWT_EXPIRATION_MS`: JWT token expiration time

## Error Handling

The application includes comprehensive error handling for:

- Validation errors
- Authentication failures
- Resource not found
- Authorization errors
- Rate limit exceeded

## Contributing

1. Create a new branch for your feature
2. Write tests for new functionality
3. Ensure all tests pass
4. Submit a pull request

## License

This project is licensed under the MIT License.