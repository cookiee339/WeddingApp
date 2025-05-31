# QR Code Access Control Implementation Summary

## Overview

This document summarizes the QR code-based access control system implemented for the Barbara & Mikołaj Wedding Photo App. The system restricts app access to only users who have scanned a valid QR code or accessed the app through a URL containing a valid access token.

## What Was Implemented

### 1. Frontend Access Control
- **Access Gate**: Users without valid tokens see an access denied screen
- **Token Validation**: Automatic validation of tokens from URLs
- **Persistent Access**: Valid access persists for 48 hours in localStorage
- **Seamless Integration**: No disruption to existing photo sharing functionality

### 2. Backend API Endpoints
- `POST /api/access/validate` - Validates access tokens
- `POST /api/access/generate` - Generates new access tokens (for organizers)
- `GET /api/access/codes` - Lists all generated tokens
- `DELETE /api/access/codes/{id}` - Deactivates specific tokens
- `POST /api/access/cleanup` - Removes expired tokens

### 3. Database Integration
- New `access_tokens` table with secure token storage
- Automatic schema creation and migration
- Configurable token expiration (default: 48 hours)

### 4. QR Code Generator
- Interactive command-line tool for generating QR codes
- Single QR generation with custom descriptions
- Batch QR generation for multiple guests
- PNG output with wedding theme colors
- Automatic report generation

### 5. Testing & Setup Tools
- Automated test suite for API endpoints
- Setup script for easy deployment
- Comprehensive documentation

## How It Works

### User Flow
1. **Wedding organizers** generate QR codes using the command-line tool
2. **QR codes contain URLs** with unique access tokens (e.g., `https://app.com?token=ABC123`)
3. **Guests scan QR codes** or click shared URLs
4. **Frontend validates tokens** with the backend API
5. **Valid tokens grant 48-hour access** to the photo sharing app
6. **Invalid/expired tokens** show access denied screen

### Security Features
- **Cryptographically secure tokens** (32-byte random, Base64URL encoded)
- **Configurable expiration** (1 hour to 1 week)
- **Token deactivation** capability
- **Automatic cleanup** of expired tokens
- **No personal data** stored in tokens

## Files Created/Modified

### Frontend Files
```
frontend/src/stores/accessStore.js          # Access control state management
frontend/src/components/AccessControl.vue   # Access gate component
frontend/src/App.vue                        # Updated to include access control
frontend/package.json                       # Added QR dependencies
```

### Backend Files
```
backend/src/main/kotlin/com/wedding/models/AccessToken.kt              # Token data models
backend/src/main/kotlin/com/wedding/services/AccessControlService.kt   # Token business logic
backend/src/main/kotlin/com/wedding/plugins/AccessRoutes.kt             # API endpoints
backend/src/main/kotlin/com/wedding/database/DatabaseFactory.kt        # Updated schema
backend/src/main/kotlin/com/wedding/Application.kt                      # Updated configuration
```

### Root Directory Files
```
WeddingApp/qr-generator.js           # Interactive QR code generator
WeddingApp/test-access.js            # API testing script
WeddingApp/setup-qr-access.sh        # Automated setup script
WeddingApp/package.json              # QR generator dependencies
WeddingApp/QR-ACCESS-SETUP.md        # Detailed setup guide
```

## Key Features

### For Wedding Organizers
- **Easy QR Generation**: Interactive command-line tool
- **Flexible Options**: Custom descriptions, validity periods
- **Batch Processing**: Generate multiple QR codes at once
- **Print-Ready Output**: High-quality PNG files with wedding branding
- **Management**: View, deactivate, and cleanup tokens

### For Wedding Guests
- **Simple Access**: Just scan QR code or click link
- **No Registration**: No accounts or sign-ups required
- **Persistent Access**: Works for 48 hours after scanning
- **Seamless Experience**: Direct access to photo sharing

### For Developers
- **RESTful API**: Standard HTTP endpoints
- **Database Integration**: Exposed tables with proper relationships
- **Error Handling**: Comprehensive error responses
- **Testing Tools**: Automated test suite
- **Documentation**: Complete setup and usage guides

## Usage Instructions

### Quick Start
```bash
# 1. Install dependencies
npm install

# 2. Start backend (requires Java 21+)
cd backend && ./gradlew run

# 3. Generate QR codes
npm run generate

# 4. Start frontend
cd frontend && npm run dev

# 5. Test the system
npm run test
```

### QR Code Generation Examples
```bash
# Single QR code
node qr-generator.js
# Choose option 1, enter "Table 5", set 48 hours

# Batch QR codes
node qr-generator.js  
# Choose option 2, enter 10 codes, "Guest Access", 48 hours
```

### Output Example
```
qr-codes/
├── qr-Table_5-1234567890.png
├── qr-Guest_Access_1-1234567891.png
├── qr-Guest_Access_2-1234567892.png
└── batch-report-1234567893.txt
```

## Configuration

### Environment Variables
```bash
# QR Generator
export API_URL="http://localhost:8080"
export FRONTEND_URL="http://localhost:5173"

# Frontend
VITE_API_BASE_URL=http://localhost:8080
```

### Token Settings
- **Default Validity**: 48 hours
- **Minimum Validity**: 1 hour
- **Maximum Validity**: 168 hours (1 week)
- **Token Length**: 32 bytes (Base64URL encoded)

## Deployment Considerations

### Production Setup
1. Update environment variables with production URLs
2. Ensure HTTPS for security
3. Configure database connection
4. Set up automated token cleanup
5. Monitor token usage and expiration

### Scaling
- Tokens are stateless and can be validated by any backend instance
- Database queries are optimized with indexes
- No session storage required
- Cleanup can be automated with cron jobs

## Benefits

### Security
- **Access Control**: Only invited guests can use the app
- **Time-Limited**: Automatic expiration prevents long-term unauthorized access
- **Revocable**: Tokens can be deactivated if needed
- **No Registration**: Reduces data privacy concerns

### User Experience
- **Frictionless**: Single scan provides immediate access
- **Mobile-Friendly**: QR codes work perfectly on phones
- **Offline Capable**: Tokens work without constant internet
- **Wedding-Themed**: Branded QR codes match wedding aesthetics

### Management
- **Easy Distribution**: Print QR codes, share links, or send digitally
- **Flexible Placement**: Table cards, invitations, welcome bags
- **Real-Time Control**: Generate, monitor, and deactivate as needed
- **Batch Operations**: Handle large guest lists efficiently

## Future Enhancements

### Potential Additions
- Usage analytics (when/where tokens are used)
- Geographic restrictions (venue-only access)
- Time-based restrictions (ceremony vs reception)
- Guest grouping (family vs friends)
- Integration with wedding planning tools

This implementation provides a complete, secure, and user-friendly access control system that enhances your wedding photo sharing app while maintaining the intimate, private atmosphere you want for your special day.