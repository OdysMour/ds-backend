# DS Backend

## Security Features

- JWT Authentication with enhanced security
- Rate limiting protection
- CORS configuration
- CSRF protection
- Security headers
- Environment-based configuration
- Database security measures
- Role-Based Access Control

## Security Configuration

1. Create a `.env` file based on `.env.example`:
```bash
cp .env.example .env
```

2. Generate a secure JWT secret:
```bash
openssl rand -hex 64
```

3. Configure environment variables in `.env`:
- Generate strong passwords for database
- Set appropriate CORS origins
- Configure rate limiting
- Set JWT expiration time
- Configure SSL in production

### Role-Based Access Control
The application implements role-based access control (RBAC) to restrict access to resources based on the user's role. The following roles are defined:
- `ROLE_ADMIN`: Has full access to all resources.
- `ROLE_MANAGER`: Can manage animals and users.
- `ROLE_VETERINARIAN`: Can view and update animal health records.
- `ROLE_STAFF`: Can create animals.
- `ROLE_USER`: Can view animals.

## API Testing with Postman

### Setting Up the Postman Collection

1. Import the collection and environment files:
   ```bash
   # Using Postman Desktop App
   Import the files from ds-backend/postman/:
   - collection.json
   - environment.json

   # Using Postman CLI
   postman login
   cd ds-backend/postman
   postman collection import collection.json
   postman environment import environment.json
   ```

2. Configure the environment:
   - Select the "DS Backend Environment"
   - Update the following variables if needed:
     - `baseUrl`: Default is http://localhost:9090
     - `adminUsername` and `adminPassword`
     - `vetUsername` and `vetPassword`
     - `userUsername` and `userPassword`

3. Authentication Flow:
   - First run the signup requests for each role (Admin, Vet, User)
   - Then run the signin requests to get JWT tokens
   - Tokens are automatically stored in environment variables

4. Testing Endpoints:
   - Animals API supports full CRUD operations
   - Different roles have different access levels:
     - Admin: Full access including deletion
     - Vet: Can create, read, update, and manage health status
     - User: Read-only access

5. Running Tests with Postman CLI:
   ```bash
   # Run all tests
   postman collection run ds-backend/postman/collection.json -e ds-backend/postman/environment.json

   # Run specific folder
   postman collection run ds-backend/postman/collection.json -e ds-backend/postman/environment.json --folder "Animals"
   ```

### Available Endpoints

1. Authentication:
   - POST /api/auth/signup - Register new user
   - POST /api/auth/signin - Login and get JWT token

2. Animals:
   - GET /api/animals - List all animals
   - GET /api/animals/{id} - Get single animal
   - POST /api/animals - Create new animal
   - PUT /api/animals/{id} - Update animal
   - PATCH /api/animals/{id}/health-status - Update health status
   - DELETE /api/animals/{id} - Delete animal

## Production Deployment Checklist

### 1. Environment Configuration
- [ ] Set strong database passwords
- [ ] Configure secure JWT secret
- [ ] Restrict CORS to specific origins
- [ ] Enable SSL/TLS
- [ ] Set appropriate rate limits
- [ ] Disable debug features

### 2. Database Security
- [ ] Use connection pooling
- [ ] Enable SSL for database connection
- [ ] Restrict database user permissions
- [ ] Disable public access to database port
- [ ] Regular backup configuration

### 3. Application Security
- [ ] Enable HTTPS only
- [ ] Set secure cookie configurations
- [ ] Configure proper logging levels
- [ ] Set up monitoring and alerts
- [ ] Regular security updates
- [ ] Implement audit logging

### 4. Infrastructure Security
- [ ] Configure firewall rules
- [ ] Set up DDoS protection
- [ ] Enable security headers
- [ ] Configure secure reverse proxy
- [ ] Regular security scanning

## Development Setup

```bash
# Start postgres db as container
docker run --name ds-backend-pg --rm \
-e POSTGRES_PASSWORD=${DB_PASSWORD} \
-e POSTGRES_USER=${DB_USER} \
-e POSTGRES_DB=${DB_NAME} \
-d -p ${DB_PORT}:5432 \
-v ds-backend-vol:/var/lib/postgresql/data \
postgres:14

# Stop the container
docker kill ds-backend-pg

# Delete volume
docker volume rm ds-backend-vol
```

## Security Best Practices

1. JWT Token Security
- Short expiration times
- Secure token storage
- Token rotation
- Claims validation

2. Password Security
- Strong password policy
- Password hashing with bcrypt
- Account lockout policy
- Password reset security

3. API Security
- Rate limiting
- Input validation
- Output encoding
- Error handling
- API versioning

4. Data Security
- Data encryption
- Secure file handling
- PII protection
- Audit logging
- Data backups

## Monitoring and Maintenance

1. Security Monitoring
- Failed login attempts
- Rate limit breaches
- Suspicious activities
- System resource usage

2. Regular Updates
- Dependency updates
- Security patches
- Configuration reviews
- Security testing

3. Backup Strategy
- Regular backups
- Backup encryption
- Recovery testing
- Retention policy

4. Incident Response
- Security incident plan
- Contact information
- Recovery procedures
- Documentation

## Additional Resources

- [Spring Security Documentation](https://docs.spring.io/spring-security/reference/index.html)
- [OWASP Security Cheat Sheet](https://cheatsheetseries.owasp.org/)
- [JWT Best Practices](https://datatracker.ietf.org/doc/html/draft-ietf-oauth-jwt-bcp-07)