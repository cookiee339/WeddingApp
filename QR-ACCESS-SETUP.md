# QR Code Access Control Setup Guide

## Overview

This wedding photo app now includes QR code-based access control. Only users who scan a valid QR code or access the app through a URL with a valid token can use the application.

## How It Works

1. **Wedding organizers** generate QR codes with access tokens
2. **Guests** scan QR codes or click URLs to gain access  
3. **Access is temporary** - tokens expire after 48 hours by default
4. **No registration required** - simple one-time access via QR code

## Quick Start for Wedding Organizers

### 1. Install QR Generator Dependencies

```bash
cd WeddingApp
npm install
```

### 2. Start Your Backend Server

Make sure your backend is running with the new access control routes:

```bash
cd backend
./gradlew run
```

### 3. Generate QR Codes

```bash
cd WeddingApp
npm run generate
# or
node qr-generator.js
```

Follow the interactive prompts to:
- Generate single QR codes for specific guests/tables
- Generate batch QR codes for multiple guests
- Set custom expiration times

### 4. Distribute QR Codes

- **Print QR codes** and place them at tables
- **Send QR codes** via messaging apps
- **Email links** with access tokens
- **Share on social media** for your wedding hashtag

## Generated Files

QR codes and reports are saved in the `./qr-codes/` directory:

```
qr-codes/
├── qr-Table_1-1234567890.png
├── qr-Family_Smith-1234567891.png
├── batch-report-1234567892.txt
└── ...
```

## Frontend Configuration

### Environment Variables

Update your frontend `.env` file:

```env
VITE_API_URL=http://localhost:8080
```

For production, use your actual backend URL.

### Access Flow

1. **Guest scans QR code** or opens URL with token
2. **App validates token** with backend API
3. **If valid**: Guest gets access for 48 hours
4. **If invalid**: Guest sees access denied screen

## Backend API Endpoints

The backend now includes these access control endpoints:

- `POST /api/access/validate` - Validate an access token
- `POST /api/access/generate` - Generate new access token (for organizers)
- `GET /api/access/codes` - List all generated tokens
- `DELETE /api/access/codes/{id}` - Deactivate a token
- `POST /api/access/cleanup` - Remove expired tokens

## QR Code Generator Options

### Single QR Code
```bash
node qr-generator.js
# Select option 1
# Enter description: "Table 5"
# Enter validity: 48 hours
```

### Batch QR Codes
```bash
node qr-generator.js
# Select option 2  
# Count: 10
# Base description: "Guest Access"
# Validity: 48 hours
```

This creates:
- Guest Access #1
- Guest Access #2
- Guest Access #3
- ... etc

## Configuration Options

### QR Generator Environment Variables

```bash
export API_URL="http://localhost:8080"           # Backend API URL
export FRONTEND_URL="http://localhost:5173"     # Frontend URL for QR codes
```

### Token Validity Period

Default: 48 hours
Range: 1 hour to 168 hours (1 week)

Adjust when generating tokens:
```bash
# 24 hours
validityHours: 24

# 1 week for VIP guests
validityHours: 168
```

## Deployment Considerations

### Production URLs

Update the QR generator script for production:

```bash
export API_URL="https://your-backend.com"
export FRONTEND_URL="https://your-wedding-app.com"
```

### Database

The system automatically creates an `access_tokens` table with:
- `token` (unique access key)
- `description` (optional label)
- `created_at` (timestamp)
- `expires_at` (expiration time)
- `is_active` (can be deactivated)

### Security

- Tokens are cryptographically secure (32-byte random)
- Automatic cleanup of expired tokens
- No personal data stored in tokens
- HTTPS recommended for production

## Usage Scenarios

### Table Cards
```
┌─────────────────────────────┐
│  Barbara & Mikołaj          │
│  07.06.2025                 │
│                             │
│  [QR CODE]                  │
│                             │
│  Scan to share photos from  │
│  our special day!           │
│                             │
│  Table 5                    │
└─────────────────────────────┘
```

### Wedding Invitations
Include QR codes in digital or printed invitations.

### Social Media
Share QR codes on wedding hashtags for extended family/friends.

### Welcome Bags
Include QR code cards in guest welcome bags.

## Troubleshooting

### QR Code Not Working
1. Check if backend is running
2. Verify API_URL is correct
3. Check token hasn't expired
4. Test the direct URL from QR code

### Cannot Generate QR Codes
1. Install dependencies: `npm install`
2. Check backend is accessible
3. Verify API endpoints are working

### Access Denied for Guests
1. Check token expiration
2. Verify QR code scans correctly
3. Test with browser developer tools

### Backend Issues
1. Check database connection
2. Verify CORS settings for frontend domain
3. Check API routes are properly configured

## Support

For technical issues during your wedding setup, check:

1. **Backend logs** for API errors
2. **Frontend console** for JavaScript errors  
3. **Network tab** for failed requests
4. **Database tables** for token data

## Example Wedding Setup

1. **1 week before**: Generate QR codes for all tables
2. **3 days before**: Print and prepare table cards
3. **Day of wedding**: Place QR codes at tables
4. **During reception**: Guests scan and start sharing photos
5. **After wedding**: Download all shared photos

Enjoy your special day! 🎉💕