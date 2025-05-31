# Docker Setup Guide - Wedding Photo App

## Overview

This wedding photo app is fully containerized using Docker and Docker Compose. The setup includes:

- **Backend**: Kotlin/Ktor API server
- **Frontend**: Vue.js application with Nginx
- **Database**: PostgreSQL for data storage
- **QR Service**: Node.js service for generating QR codes
- **Redis**: Session management (optional)
- **Development Tools**: Adminer, Redis Commander, MailHog

## Prerequisites

- Docker 20.0+ installed
- Docker Compose 2.0+ installed
- 4GB+ RAM available
- 10GB+ disk space

### Install Docker

**Windows/Mac**: Download Docker Desktop from [docker.com](https://docker.com)

**Linux (Ubuntu/Debian)**:
```bash
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh
sudo usermod -aG docker $USER
```

## Quick Start

### 1. Clone and Setup
```bash
cd WeddingApp
cp .env.example .env
# Edit .env with your Cloudinary credentials
```

### 2. Start Everything
```bash
# Production mode (recommended)
./docker-start.sh

# Development mode
./docker-start.sh --dev

# With logs visible
./docker-start.sh --foreground
```

### 3. Access Services
- **Wedding App**: http://localhost
- **QR Generator**: http://localhost:3000
- **API**: http://localhost:8080

## Environment Configuration

### Required Settings (.env file)

```env
# Cloudinary (REQUIRED for photo uploads)
CLOUDINARY_CLOUD_NAME=your_cloud_name
CLOUDINARY_API_KEY=your_api_key
CLOUDINARY_API_SECRET=your_api_secret

# Database (auto-configured)
POSTGRES_DB=wedding_app
POSTGRES_USER=wedding_user
POSTGRES_PASSWORD=wedding_password_2025

# URLs (auto-configured for Docker)
FRONTEND_URL=http://localhost
BACKEND_URL=http://backend:8080
```

### Get Cloudinary Credentials
1. Sign up at https://cloudinary.com
2. Go to Dashboard
3. Copy Cloud name, API Key, and API Secret
4. Update .env file

## Service Architecture

### Core Services

#### Frontend (Port 80)
- Vue.js application
- Nginx web server
- Serves wedding photo app to guests
- Handles QR code access control

#### Backend (Port 8080)
- Kotlin/Ktor API server
- Handles photo uploads/downloads
- Manages access tokens
- Database operations

#### Database (Port 5432)
- PostgreSQL database
- Stores photos metadata and access tokens
- Persistent data storage

#### QR Service (Port 3000)
- Node.js web service
- Generates QR codes for guest access
- Web interface for organizers
- Batch QR generation

### Optional Services

#### Redis (Port 6379)
- Session management
- Caching layer
- Enable with: `--profile production`

#### Development Tools
- **Adminer** (Port 8081): Database admin interface
- **Redis Commander** (Port 8082): Redis management
- **MailHog** (Port 8025): Email testing

## Operating Modes

### Production Mode (Default)
```bash
./docker-start.sh
```

**Features**:
- Optimized for performance
- Minimal resource usage
- Production-ready configuration
- Auto-restart on failure

**Services**: frontend, backend, database, qr-service

### Development Mode
```bash
./docker-start.sh --dev
```

**Features**:
- Hot-reload for code changes
- Debug ports exposed
- Development tools included
- Verbose logging

**Additional Services**: adminer (database admin)

**Additional Ports**:
- Frontend Dev: 5173
- Backend Debug: 5005
- Node.js Debug: 9229

## Common Commands

### Service Management
```bash
# Start services
./docker-start.sh

# Start in development mode
./docker-start.sh --dev

# Stop all services
./docker-start.sh --stop

# Rebuild and restart
./docker-start.sh --rebuild

# Show logs
./docker-start.sh --logs

# Show service status
./docker-start.sh --status
```

### Manual Docker Compose
```bash
# Start production services
docker-compose up -d

# Start development services
docker-compose -f docker-compose.yml -f docker-compose.override.yml up -d

# View logs
docker-compose logs -f

# Stop services
docker-compose down

# Rebuild specific service
docker-compose build backend
docker-compose up -d backend
```

### Service-Specific Commands
```bash
# Restart single service
docker-compose restart backend

# View service logs
docker-compose logs -f frontend

# Execute command in container
docker-compose exec backend bash
docker-compose exec database psql -U wedding_user -d wedding_app

# Scale services (if needed)
docker-compose up -d --scale backend=2
```

## Data Management

### Volumes and Persistence

**Persistent Data**:
- `wedding_postgres_data`: Database files
- `wedding_qr_codes`: Generated QR codes
- `wedding_backup_data`: Database backups

**Logs**:
- `wedding_backend_logs`: Backend application logs
- `wedding_frontend_logs`: Nginx access/error logs
- `wedding_qr_logs`: QR service logs

### Backup and Restore
```bash
# Backup database
docker-compose exec database pg_dump -U wedding_user wedding_app > backup.sql

# Restore database
docker-compose exec -T database psql -U wedding_user -d wedding_app < backup.sql

# Backup QR codes
docker cp wedding_qr_service:/app/qr-codes ./qr-backup

# List all volumes
docker volume ls | grep wedding
```

## Networking

### Container Communication
- Services communicate via Docker network: `wedding_network`
- Internal DNS resolves service names: `backend`, `database`, `qr-service`
- Subnet: `172.20.0.0/16`

### Port Mapping

**Production**:
- 80: Frontend (main app)
- 3000: QR Generator
- 8080: Backend API
- 5432: Database (external access)

**Development** (additional):
- 5173: Frontend dev server
- 5005: Backend debug
- 9229: Node.js debug
- 8081: Adminer
- 8082: Redis Commander

## Security

### Default Security Measures
- Services isolated in Docker network
- Database not exposed in production
- Environment variables for secrets
- Health checks for all services
- Non-root users in containers

### Production Security
```bash
# Change default passwords in .env
POSTGRES_PASSWORD=your_secure_password
REDIS_PASSWORD=your_redis_password

# Use HTTPS in production
# Configure SSL certificates
# Update CORS settings
```

## Monitoring and Health

### Health Checks
All services include health checks:
- **Frontend**: HTTP GET /health
- **Backend**: HTTP GET /api/access/codes
- **Database**: pg_isready
- **QR Service**: HTTP GET /health

### View Service Health
```bash
docker-compose ps
docker inspect wedding_frontend | grep Health
```

### Logs and Debugging
```bash
# All logs
docker-compose logs

# Specific service
docker-compose logs backend

# Follow logs
docker-compose logs -f --tail=100

# Container inspection
docker stats
docker inspect wedding_backend
```

## Troubleshooting

### Common Issues

#### Services Won't Start
```bash
# Check Docker daemon
docker info

# Check compose file syntax
docker-compose config

# View service logs
docker-compose logs
```

#### Port Conflicts
```bash
# Check what's using ports
netstat -tulpn | grep :80
lsof -i :8080

# Change ports in docker-compose.yml
```

#### Database Connection Issues
```bash
# Check database logs
docker-compose logs database

# Test connection
docker-compose exec backend curl database:5432

# Reset database
docker-compose down
docker volume rm wedding_postgres_data
docker-compose up -d
```

#### Out of Disk Space
```bash
# Clean up Docker
docker system prune -a

# Remove unused volumes
docker volume prune

# Check volume sizes
docker system df
```

#### Memory Issues
```bash
# Check container memory usage
docker stats

# Increase Docker memory limit (Docker Desktop)
# Set memory limits in docker-compose.yml
```

### Service-Specific Issues

#### Frontend Not Loading
- Check if backend is healthy: `curl http://localhost:8080/api/access/codes`
- Check nginx logs: `docker-compose logs frontend`
- Verify .env configuration

#### QR Codes Not Generating
- Check backend connection: `docker-compose logs qr-service`
- Test API: `curl http://localhost:3000/health`
- Verify canvas dependencies in container

#### Photos Not Uploading
- Check Cloudinary configuration in .env
- Verify backend logs: `docker-compose logs backend`
- Test file permissions

## Development Workflow

### Code Changes

**Frontend Changes**:
1. Edit files in `./frontend/src/`
2. Changes auto-reload in dev mode
3. Rebuild for production: `docker-compose build frontend`

**Backend Changes**:
1. Edit files in `./backend/src/`
2. Gradle continuous build in dev mode
3. Rebuild: `docker-compose build backend`

**QR Service Changes**:
1. Edit files in `./qr-service/`
2. Nodemon auto-restart in dev mode
3. Rebuild: `docker-compose build qr-service`

### Database Schema Changes
```bash
# Access database
docker-compose exec database psql -U wedding_user -d wedding_app

# View tables
\dt

# Manual migration
# Update backend code
# Restart backend service
```

## Performance Optimization

### Production Optimizations
- Enable gzip compression (nginx)
- Use production Node.js images
- Optimize database connections
- Enable Redis caching
- Use CDN for static assets

### Resource Limits
```yaml
# In docker-compose.yml
services:
  backend:
    deploy:
      resources:
        limits:
          memory: 1G
          cpus: '0.5'
```

## Deployment

### Production Deployment
1. Update .env with production values
2. Configure domain/SSL certificates
3. Set up reverse proxy (optional)
4. Configure backups
5. Set up monitoring

### Cloud Deployment
- AWS: Use ECS or EC2 with Docker
- Google Cloud: Use Cloud Run or GKE
- Azure: Use Container Instances or AKS
- DigitalOcean: Use App Platform or Droplets

### Environment Variables for Production
```env
FRONTEND_URL=https://yourweddingdomain.com
BACKEND_URL=https://api.yourweddingdomain.com
NODE_ENV=production
CORS_ALLOWED_HOSTS=https://yourweddingdomain.com
```

## Backup Strategy

### Automated Backup Script
```bash
#!/bin/bash
DATE=$(date +%Y%m%d_%H%M%S)
docker-compose exec -T database pg_dump -U wedding_user wedding_app > "backup_${DATE}.sql"
docker cp wedding_qr_service:/app/qr-codes "qr_backup_${DATE}"
```

### Restore Process
```bash
# Stop services
docker-compose down

# Restore database
docker-compose up -d database
sleep 10
docker-compose exec -T database psql -U wedding_user -d wedding_app < backup.sql

# Restore QR codes
docker cp qr_backup_20250101/ wedding_qr_service:/app/qr-codes

# Start all services
docker-compose up -d
```

This Docker setup provides a complete, production-ready environment for your wedding photo app. The containerized approach ensures consistent deployment across different environments and simplifies the setup process for your special day! 🎉