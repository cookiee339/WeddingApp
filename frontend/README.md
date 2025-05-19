# Wedding Photo Gallery - Frontend

A Vue.js 3 frontend for the Wedding Photo Gallery application that allows wedding guests to take and share photos in real-time.

## Features

- Mobile-first, responsive design
- Take photos using device camera
- View all photos in a shared gallery
- View just your own photos
- Delete your own photos
- No login required - uses device identifier

## Tech Stack

- Vue.js 3
- Tailwind CSS for styling
- Axios for API requests
- Vue Router for navigation
- Vite for fast development

## Project Setup

### Prerequisites

- Node.js (v14 or later)
- npm or yarn

### Installation

```bash
# Install dependencies
npm install

# Start development server
npm run dev

# Build for production
npm run build

# Preview production build
npm run preview
```

## Environment Variables

Create a `.env.local` file in the project root with the following variables:

```
VITE_API_BASE_URL=http://your-backend-url
```

For local development, this will typically be `http://localhost:8080`.

## Project Structure

```
frontend/
├── public/             # Static assets
├── src/
│   ├── api/            # API client and requests
│   ├── assets/         # Images, fonts, etc.
│   ├── components/     # Reusable Vue components
│   ├── router/         # Vue Router configuration
│   ├── views/          # Page components
│   ├── App.vue         # Root component
│   └── main.js         # Application entry point
├── index.html          # HTML template
├── vite.config.js      # Vite configuration
└── tailwind.config.js  # Tailwind CSS configuration
```

## API Integration

The frontend communicates with the backend Ktor server through RESTful API endpoints:

- `GET /photos` - Get all photos
- `GET /photos?uploaderId={id}` - Get photos by uploader ID
- `POST /photos` - Upload a new photo
- `DELETE /photos/{id}` - Delete a photo

## User Identification

The app generates a unique identifier for each user on their first visit, storing it in localStorage. This ID is sent with each photo upload to track which photos belong to which user.

## Browser Compatibility

The application is designed to work on modern browsers that support the following features:

- WebRTC API (for camera access)
- Fetch API
- ES6 features
- localStorage

## Development Notes

- The application uses the device's camera when available
- Fallback to file input when camera access is denied or unavailable
- Images are optimized by Cloudinary on the backend