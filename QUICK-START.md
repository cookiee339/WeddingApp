# Quick Start Guide - QR Access Control

## Prerequisites

- Node.js 16+ installed
- Java 21+ installed (for backend)
- Git (to clone the project)

## 5-Minute Setup

### 1. Install Dependencies
```bash
cd WeddingApp
npm install
cd frontend && npm install && cd ..
```

### 2. Start Backend
```bash
cd backend
./gradlew run &
cd ..
```

### 3. Test System
```bash
npm run test
```

### 4. Generate Your First QR Code
```bash
npm run generate
```
- Choose option 1 (single QR)
- Enter description: "Test Table"
- Press Enter for 48 hours validity
- QR code saved to `./qr-codes/`

### 5. Start Frontend
```bash
cd frontend
npm run dev
```

## Test the Flow

1. Open the QR code PNG file in `./qr-codes/`
2. Scan with your phone OR copy the URL from terminal
3. Open the URL - you should see the wedding app
4. Try opening just `http://localhost:5173` - you should see "Access Denied"

## Production Checklist

- [ ] Update `frontend/.env` with your production API URL
- [ ] Update QR generator environment variables:
  ```bash
  export API_URL="https://your-api.com"
  export FRONTEND_URL="https://your-wedding-app.com"
  ```
- [ ] Generate QR codes for your tables/guests
- [ ] Print QR codes and place at wedding venue
- [ ] Test with real phones before the wedding

## Common Commands

```bash
# Generate QR codes
npm run generate

# Test API
npm run test

# Auto-setup everything
./setup-qr-access.sh

# Start frontend dev server
cd frontend && npm run dev

# Start backend
cd backend && ./gradlew run
```

## How Guests Use It

1. Guest scans QR code at wedding table
2. Phone opens wedding photo app automatically
3. Guest can immediately start taking/sharing photos
4. Access lasts 48 hours (configurable)

## Troubleshooting

- **QR not working?** Check backend is running on port 8080
- **Access denied?** Generate new QR code and try again
- **Can't generate QR?** Run `npm install` and check API connection

That's it! Your wedding app now has QR code access control. 🎉

For detailed documentation, see `QR-ACCESS-SETUP.md`.