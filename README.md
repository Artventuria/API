# Artventuria API

API backend for the Artventuria application, enabling the management of artworks, badges, points, and social interactions.

## 🚀 Features

- Authentication and authorization with JWT
- Artwork and collection management
- Points system and ranking
- NFC tag validation
- Firebase notification system
- API documentation with Swagger

## 🛠️ Technologies

- Java 21
- Spring Boot 3.x
- PostgreSQL 16
- MongoDB 5
- Kafka & Zookeeper
- Firebase
- Spring Security
- Spring Data JPA/MongoDB
- Logback
- Swagger/OpenAPI
- Terraform
- Docker
- AWS

## 📋 Prerequisites

- Java 21
- Maven 4.0.0 or higher
- Docker and Docker Compose
- AWS CLI (for deployment)

## 🔧 Installation

1. Clone the repository:

```bash
git clone https://github.com/Artventuria/API.git
cd API
```

2. Install dependencies:

```bash
make deps
# or manually: ./mvnw dependency:resolve
```

3. Set up development environment:

```bash
# Start required services (PostgreSQL, MongoDB)
docker-compose up -d
```

4. Configure environment variables in `.env` file (for local development)

## 🚀 Running the Application

To run the application in development mode:

```bash
make dev
# or manually: ./mvnw spring-boot:run -Pdev
```

To run the application in production mode:

```bash
make run
# or manually: ./mvnw spring-boot:run -Pprod
```

## 🏗️ Build

To build the application:

```bash
make build
# or manually: ./mvnw package
```

To build for Linux (for deployment):

```bash
make linux64
```

## 🧪 Tests

Run tests:

```bash
make test
# or manually: ./mvnw test
```

With code coverage:

```bash
make test_coverage
# or manually: ./mvnw verify
```

## 📚 API Documentation

Swagger documentation is automatically generated and available at:

`http://localhost:8081/swagger-ui.html`

To generate OpenAPI docs:

```bash
make openapi
```

## 🔍 Code Quality

To run code quality checks:

```bash
make lint
# or manually: ./mvnw checkstyle:check
```

## 🗄️ Database Management

Run migrations:

```bash
make migrate
# or manually: ./mvnw flyway:migrate
```

View migration info:

```bash
make migrate_info
```

Clean and reapply migrations:

```bash
make migrate_clean
```

## 🐳 Docker

Build and run with Docker:

```bash
# Development environment
docker-compose up -d

# Production environment
docker-compose -f docker-compose.prod.yml up -d
```

## 🚢 Deployment

The project uses GitHub Actions for CI/CD. To deploy:

1. Create a tag with version number:

```bash
git tag v1.0.0
git push origin v1.0.0
```

2. GitHub Actions will automatically:
   - Build the application
   - Create a Docker image
   - Push to Docker Hub
   - Deploy to the production server

## 📁 Project Structure

```
.
├── .deploy/                # Deployment configuration
├── .github/                # GitHub workflows (CI/CD)
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── com/artventuria/api/
│   │   │   │   ├── config/        # Application configuration
│   │   │   │   ├── controller/    # REST controllers
│   │   │   │   ├── dto/           # Data Transfer Objects
│   │   │   │   ├── exception/     # Custom exceptions
│   │   │   │   ├── domain/        # Domain models
│   │   │   │   ├── repository/    # Data access layer
│   │   │   │   ├── security/      # Security configuration
│   │   │   │   └── service/       # Business logic
│   │   ├── resources/             # Application resources
│   └── test/                      # Test files
├── terraform/                     # Infrastructure as Code
├── docker-compose.yml             # Development services
├── docker-compose.prod.yml        # Production services
├── Makefile                       # Development commands
└── pom.xml                        # Project dependencies
```

## 📝 License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.
