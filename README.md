# Wedding Photo Gallery App

A full-stack web application that allows wedding guests to take photos during the event and share them in a communal gallery. Accessible via a QR code (linking to a web URL) with no app store downloads required.

## Core Technologies

- **Frontend**: Vue.js 3, Tailwind CSS
- **Backend**: Kotlin with Ktor
- **Photo Storage**: Multiple options (Cloudinary, AWS S3, Azure Storage)
- **Database**: PostgreSQL

## Project Structure

```
WeddingApp/
├── frontend/           # Vue.js frontend application
└── backend/            # Kotlin/Ktor backend application
```

## Setup Instructions

### Quick Start with Docker (Recommended)

The easiest way to run the application locally is using Docker:

1. Make sure you have Docker and Docker Compose installed
2. Clone the repository
3. Copy the example .env file: `cp .env.example .env`
4. Edit the .env file with your storage credentials
5. Start the application: `./docker-start.sh`

For detailed Docker setup instructions, see [DOCKER-SETUP.md](DOCKER-SETUP.md).

### Storage Backend Options

The application supports multiple storage backends:

1. **Cloudinary**: Default option, see setup below
2. **AWS S3**: For setup instructions, see the AWS S3 section in [DOCKER-SETUP.md](DOCKER-SETUP.md)
3. **Azure Storage**: For setup and testing instructions, see [AZURE-STORAGE-SETUP.md](AZURE-STORAGE-SETUP.md)

### Prerequisites for Manual Setup

- Node.js (v14+)
- Kotlin (1.9+)
- Gradle (7+)
- PostgreSQL (13+)
- Storage account (Cloudinary, AWS S3, or Azure Storage)

### Database Setup

1. Create a PostgreSQL database for the application:

```sql
CREATE DATABASE wedding_photos;
```

2. Run the schema creation script located at `backend/src/main/resources/schema.sql`:

```bash
psql -U your_username -d wedding_photos -a -f backend/src/main/resources/schema.sql
```

### Backend Setup

1. Configure environment variables for the backend:

```
# Database configuration
DB_USER=postgres
DB_PASSWORD=your_password
DATABASE_URL=jdbc:postgresql://localhost:5432/wedding_photos

# Cloudinary configuration
CLOUDINARY_CLOUD_NAME=your_cloud_name
CLOUDINARY_API_KEY=your_api_key
CLOUDINARY_API_SECRET=your_api_secret

# Optional: Port configuration
PORT=8080
```

2. Navigate to the backend directory and build the application:

```bash
cd WeddingApp/backend
./gradlew build
```

3. Run the backend server:

```bash
./gradlew run
```

The API will be available at `http://localhost:8080`.

### Frontend Setup

1. Navigate to the frontend directory:

```bash
cd WeddingApp/frontend
```

2. Install dependencies:

```bash
npm install
```

3. Configure environment variables (create a `.env.local` file):

```
VITE_API_BASE_URL=http://localhost:8080
```

4. Start the development server:

```bash
npm run dev
```

The frontend will be available at `http://localhost:3000`.

## Production Deployment

### Backend

1. Build the backend as a fat JAR:

```bash
cd WeddingApp/backend
./gradlew shadowJar
```

2. Run the JAR file:

```bash
java -jar build/libs/wedding-photo-app-backend-all.jar
```

### Frontend

1. Build the frontend for production:

```bash
cd WeddingApp/frontend
npm run build
```

2. Deploy the contents of the `dist` directory to your web server.

## QR Code Generation

Generate a QR code that links to your deployed frontend URL using any QR code generator, such as:

- [QR Code Generator](https://www.qr-code-generator.com/)
- [QRCode Monkey](https://www.qrcode-monkey.com/)

## API Endpoints

- `GET /photos` - Get all photos
- `GET /photos?uploaderId={id}` - Get photos by uploader ID
- `POST /photos` - Upload a new photo (multipart/form-data)
- `DELETE /photos/{id}` - Delete a photo

## Features

- Mobile-first, responsive design
- Real-time photo sharing
- View all photos or just your own
- No login required - uses device identifier for tracking uploads

## Security Considerations

- The app uses browser localStorage to store a unique identifier for each guest
- No personal information is collected or stored
- All storage credentials (Cloudinary, AWS S3, Azure) are only stored on the server side
- QR code access control provides basic security for the application
