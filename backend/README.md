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
- PostgreSQL database
- Cloudinary account

### Installation

```bash
# Build the application
./gradlew build

# Run the application
./gradlew run
```

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

1. Create a PostgreSQL database:
```sql
CREATE DATABASE wedding_photos;
```

2. Run the schema creation script:
```bash
psql -U postgres -d wedding_photos -a -f src/main/resources/schema.sql
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