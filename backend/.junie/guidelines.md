
# Project Guidelines for Junie

## Project Overview
This is a Wedding Photo Gallery backend application built with Kotlin and Ktor. The application provides a RESTful API for managing wedding photos, allowing users to upload, retrieve, and delete photos.

### Key Features
- RESTful API for photo management
- Integration with Cloudinary for cloud image storage
- PostgreSQL database for photo metadata
- Efficient image handling with optimization
- Support for filtering photos by uploader

### Tech Stack
- Kotlin 1.9+
- Ktor 2.3+ (Netty engine)
- Exposed SQL library for database operations
- PostgreSQL for persistence
- Cloudinary SDK for image storage
- HikariCP for connection pooling

## Project Structure
```
backend/
├── src/
│   ├── main/
│   │   ├── kotlin/com/wedding/
│   │   │   ├── config/         # Configuration classes
│   │   │   ├── database/       # Database setup and tables
│   │   │   ├── models/         # Data models
│   │   │   ├── plugins/        # Ktor plugins
│   │   │   ├── services/       # Business logic
│   │   │   └── Application.kt  # Entry point
│   │   └── resources/
│   │       ├── application.conf  # Application configuration
│   │       └── schema.sql        # Database schema
│   └── test/                     # Unit and integration tests
└── build.gradle.kts              # Build configuration
```

## Testing Guidelines
- Run tests using: `./gradlew test`
- Tests are located in `src/test/kotlin/com/wedding/`
- The current test suite includes basic endpoint tests
- When implementing new features, add corresponding tests
- For comprehensive testing, mock the database and Cloudinary service

## Build Instructions
- Build the application: `./gradlew build`
- Run the application: `./gradlew run`
- The build process includes ktlint checks for code style
- To skip ktlint checks: `./gradlew build -x ktlintCheck`

## Docker Usage
- The project includes Docker and Docker Compose configurations
- Start only PostgreSQL database: `docker-compose up -d`
- Start the full stack (PostgreSQL + backend): `docker-compose -f docker-compose.full.yml up -d`
- View logs: `docker-compose logs -f`
- Stop containers: `docker-compose down`
- The Dockerfile uses a multi-stage build process:
    1. Build stage with Gradle
    2. Runtime stage with JDK 17

## Code Style Guidelines
- The project uses ktlint for code style enforcement
- Run code style check: `./gradlew ktlintCheck`
- Automatically fix code style issues: `./gradlew ktlintFormat`
- Follow the official Kotlin coding conventions
- Avoid wildcard imports (e.g., `io.ktor.http.*`), use specific imports instead
- IDE plugins for ktlint are recommended:
    - IntelliJ IDEA: "Ktlint" plugin
    - VS Code: "Ktlint" extension

## API Endpoints
- `GET /photos` - Retrieve all photos (supports pagination and filtering)
- `GET /photos/{id}` - Retrieve a specific photo
- `POST /photos` - Upload a new photo
- `DELETE /photos/{id}` - Delete a photo
- `GET /health` - Health check endpoint

## Environment Configuration
The application requires the following environment variables:
```
# Database configuration
DB_USER=postgres
DB_PASSWORD=your_password
DATABASE_URL=jdbc:postgresql://localhost:5432/wedding_photos

# Cloudinary configuration
CLOUDINARY_CLOUD_NAME=your_cloud_name
CLOUDINARY_API_KEY=your_api_key
CLOUDINARY_API_SECRET=your_api_secret

# Server configuration
PORT=8080
```

## Development Workflow
1. Set up the environment variables
2. Start the database (using Docker or local PostgreSQL)
3. Run the application
4. Make changes and ensure tests pass
5. Run ktlint to ensure code style compliance
6. Build the application
7. Submit changes

## Troubleshooting
- If database connection issues occur, verify PostgreSQL is running and credentials are correct
- For Cloudinary issues, check API keys and network connectivity
- For build failures, check the Gradle output for specific errors