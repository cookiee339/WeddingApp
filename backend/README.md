# Wedding Photo Gallery - Backend

A Kotlin/Ktor backend for the Wedding Photo Gallery application that enables photo uploads, storage, and retrieval.

## Features

- RESTful API for photo management
- Integration with Cloudinary for cloud image storage
- PostgreSQL database for photo metadata
- Efficient image handling with optimization
- Support for filtering photos by uploader

## Tech Stack

- Kotlin 1.9+
- Ktor 2.3+ (Netty engine)
- Exposed SQL library for database operations
- PostgreSQL for persistence
- Cloudinary SDK for image storage
- HikariCP for connection pooling

## Project Setup

### Prerequisites

- JDK 11 or higher
- Kotlin 1.9+
- Docker and Docker Compose (for local development)
- PostgreSQL database (if not using Docker)
- Cloudinary account

### Installation

```bash
# Build the application
./gradlew build

# Run the application
./gradlew run
```

### Local Development with Docker

For local development, we provide Docker Compose configurations to simplify setup:

```bash
# Start only PostgreSQL database (for local development)
docker-compose up -d

# OR: Start the full stack (PostgreSQL + backend service)
docker-compose -f docker-compose.full.yml up -d

# View logs
docker-compose logs -f

# Stop containers
docker-compose down
```

When running just the PostgreSQL container, you can connect your locally running application to it by using the default configuration in `application.conf`.

## Environment Variables

The following environment variables should be set for the application:

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

## Database Setup

### Manual Setup

1. Create a PostgreSQL database:
```sql
CREATE DATABASE wedding_photos;
```

2. Run the schema creation script:
```bash
psql -U postgres -d wedding_photos -a -f src/main/resources/schema.sql
```

### Using Docker

The Docker Compose setup automatically initializes the database with the schema. To connect to the PostgreSQL instance running in Docker:

```bash
# Connect to the database
docker exec -it wedding-db-local psql -U postgres -d wedding_photos

# View tables
\dt

# Execute queries
SELECT * FROM photos;
```

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

## API Endpoints

### Photos

- `GET /photos` - Retrieve all photos
  - Query parameters:
    - `uploaderId` (optional): Filter by uploader
    - `page` (optional): Page number for pagination (default: 1)
    - `limit` (optional): Items per page (default: 20)
  - Response: Array of photo objects

- `GET /photos/{id}` - Retrieve a specific photo
  - Response: Photo object

- `POST /photos` - Upload a new photo
  - Content-Type: `multipart/form-data`
  - Form fields:
    - `image`: Image file
    - `uploader_id`: Identifier for the uploader
  - Response: Created photo object

- `DELETE /photos/{id}` - Delete a photo
  - Request body: `{ "uploader_id": "string" }`
  - Response: 204 No Content

### Utility

- `GET /health` - Health check endpoint
  - Response: "OK"

## Development Notes

- The application uses Cloudinary for image storage and optimization
- Images are automatically optimized when uploaded
- The database stores metadata about images, not the images themselves
- Connection pooling is used for efficient database connections

## Code Style

This project uses [ktlint](https://github.com/pinterest/ktlint) to enforce consistent code style based on the official Kotlin coding conventions.

### Running ktlint

```bash
# Check code style
./gradlew ktlintCheck

# Automatically fix code style issues
./gradlew ktlintFormat
```

The build process automatically includes ktlint checks. If you want to disable this behavior, you can use:

```bash
./gradlew build -x ktlintCheck
```

### IDE Integration

For the best development experience, consider installing ktlint plugins for your IDE:

- **IntelliJ IDEA**: Install the "Ktlint" plugin from the JetBrains Marketplace
- **VS Code**: Install the "Ktlint" extension

These plugins will highlight code style issues directly in your editor.

### Known Issues

The current codebase has several style violations, primarily related to wildcard imports. These need to be fixed manually by replacing wildcard imports (e.g., `io.ktor.http.*`) with specific imports for the classes that are actually used.

Example of fixing wildcard imports:

Before:
```
io.ktor.http.*
```

After:
```
io.ktor.http.HttpStatusCode
io.ktor.http.ContentType
```
