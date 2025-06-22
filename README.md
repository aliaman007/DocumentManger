# Document Management System
**Contains Instructions for local setup and deployement(Docker)**

[![Build Status](https://img.shields.io/badge/build-passing-brightgreen)](https://github.com/your-username/docmanager/actions)
[![Coverage](https://img.shields.io/badge/coverage-75%25-brightgreen)](https://github.com/your-username/docmanager)


A Spring Boot REST API for secure document management with JWT authentication, role-based access, and PostgreSQL storage. Administrators can upload and list documents, with interactive API documentation via Swagger UI.

---

## Getting Started

### Local Setup

1. **Clone the Repository**
   ```bash
   git clone https://github.com/your-username/docmanager.git
   cd docmanager


Set Up PostgreSQL

Install PostgreSQL 15+.
Create a database:psql -U postgres
CREATE DATABASE postgres;
\q


Verify connection:psql -h localhost -U postgres -d postgres

Default password: 1835 (or set via environment variable).


**Configure Environment**

Copy sample configuration:cp src/main/resources/application.yml src/main/resources/application-local.yml


Update application-local.yml (if needed):spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/postgres
    username: postgres
    password: 1835


Set environment variables (recommended):export DB_USERNAME=postgres
export DB_PASSWORD=1835
export JWT_SECRET=8f4a3b9c2d6e8f1a4b7c9d2e6f8a3b9c2d6e8f1a4b7c9d2e6f8a3b9c2d6e8f1a




**##Build and Run**

Install dependencies:mvn clean install


Run the application:mvn spring-boot:run -Dspring.profiles.active=local


Access:
API: http://localhost:8080
Swagger UI: http://localhost:8080/swagger-ui.html




Test APIs

Login:
Method: POST
URL: http://localhost:8080/api/auth/login
Body:{
  "username": "testuser",
  "password": "password"
}


Response: JWT token


Upload Document (ADMIN only):
Method: POST
URL: http://localhost:8080/api/document/upload
Headers: Authorization: Bearer <jwt-token>
Body: Form-data with file (e.g., PDF) and author (e.g., admin)


List Documents (ADMIN only):
Method: GET
URL: http://localhost:8080/api/document/list?sort=title
Headers: Authorization: Bearer <jwt-token>






**Production Deployment**
Option 1: Docker (Recommended)

Build the Application
mvn clean package -Dspring.profiles.active=prod

Output: target/docmanager-0.0.1-SNAPSHOT.jar

Set Up PostgreSQL

Provision a PostgreSQL instance (e.g., AWS RDS, Heroku Postgres).
Create a database and user:CREATE DATABASE docmanager_prod;
CREATE USER docmanager_user WITH PASSWORD 'secure_password';
GRANT ALL PRIVILEGES ON DATABASE docmanager_prod TO docmanager_user;




**Configure Docker**

Ensure Dockerfile exists (see Dockerfile):FROM openjdk:21-jdk-slim
WORKDIR /app
COPY target/docmanager-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENV SPRING_PROFILES_ACTIVE=prod
ENTRYPOINT ["java", "-jar", "app.jar"]


Ensure docker-compose.yml exists (see docker-compose.yml):version: '3.8'
services:
  app:
    build: .
    ports:
      - "8080:8080"
    depends_on:
      - db
    environment:
      - SPRING_PROFILES_ACTIVE=prod
      - SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/docmanager_prod
      - SPRING_DATASOURCE_USERNAME=docmanager_user
      - SPRING_DATASOURCE_PASSWORD=secure_password
      - JWT_SECRET=your_jwt_secret
  db:
    image: postgres:15
    environment:
      - POSTGRES_DB=docmanager_prod
      - POSTGRES_USER=docmanager_user
      - POSTGRES_PASSWORD=secure_password
    volumes:
      - postgres_data:/var/lib/postgresql/data
    ports:
      - "5432:5432"
volumes:
  postgres_data:


Update environment variables in docker-compose.yml for your production database.


Deploy
docker-compose up -d


Access:
API: http://<server-ip>:8080
Swagger UI: http://<server-ip>:8080/swagger-ui.html





Option 2: Manual Deployment

Build the Application
mvn clean package -Dspring.profiles.active=prod


Configure Production

Copy target/docmanager-0.0.1-SNAPSHOT.jar to your server.
Create application-prod.yml (see application-prod.yml):spring:
  datasource:
    url: ${SPRING_DATASOURCE_URL}
    username: ${SPRING_DATASOURCE_USERNAME}
    password: ${SPRING_DATASOURCE_PASSWORD}
  jpa:
    hibernate:
      ddl-auto: validate
jwt:
  secret: ${JWT_SECRET}




Set Environment Variables
export SPRING_PROFILES_ACTIVE=prod
export SPRING_DATASOURCE_URL=jdbc:postgresql://<prod-host>:5432/docmanager_prod
export SPRING_DATASOURCE_USERNAME=docmanager_user
export SPRING_DATASOURCE_PASSWORD=secure_password
export JWT_SECRET=your_jwt_secret


Run
java -jar docmanager-0.0.1-SNAPSHOT.jar


Monitor Logs
tail -f application.log




Project Overview
A secure, scalable API for managing documents, built with Spring Boot. Key functionalities include document upload, listing, and user authentication, with comprehensive testing and API documentation.
Features

Document Management: Upload (up to 50MB) and list documents with sorting.
Authentication: JWT-based login, registration, and logout.
Authorization: ADMIN role required for document operations.
API Docs: Swagger UI for interactive endpoint testing.
Database: PostgreSQL with Hibernate ORM.
Security: BCrypt passwords, stateless JWT sessions.
Testing: 100% coverage with JUnit, Mockito, and JaCoCo.

Tech Stack

Spring Boot 3.5.3
Spring Security, Hibernate 6.6.18, JJWT 0.12.6
PostgreSQL 15
springdoc-openapi 2.8.9
Maven, JUnit 5, Mockito 5.14.2
SLF4J with Logback


Project Structure
├── src
│   ├── main
│   │   ├── java/com/main/docmanager
│   │   │   ├── config          # Security, Swagger, exception handling
│   │   │   ├── controller      # REST endpoints
│   │   │   ├── model           # Document, User entities
│   │   │   ├── repository      # JPA repositories
│   │   │   ├── security        # JWT and security configs
│   │   │   ├── service         # Business logic
│   │   ├── resources
│   │       ├── application.yml # Config for DB, JWT, Swagger
│   ├── test                    # Unit and integration tests
├── pom.xml                     # Maven dependencies
├── Dockerfile                  # Docker build
├── docker-compose.yml          # Docker compose
├── README.md                   # Documentation


API Documentation

Swagger UI: http://localhost:8080/swagger-ui.html (local) or http://<server-ip>:8080/swagger-ui.html (prod)
Key Endpoints:
POST /api/auth/login: Authenticate and get JWT
POST /api/document/upload: Upload document (ADMIN)
GET /api/document/list: List documents with sorting (ADMIN)


Authorization: Use Bearer <jwt-token> in Swagger’s “Authorize” button.


Testing

Run tests:mvn clean test


Generate coverage report:mvn jacoco:report

View at target/site/jacoco/index.html.
Key tests:
DocumentControllerTest: Upload and list endpoints
SwaggerIntegrationTest: Swagger UI access
JwtUtilTest: JWT generation and validation






Additional Files

Dockerfile: Docker build configuration
docker-compose.yml: Docker Compose for app and DB
application-prod.yml: Production config



