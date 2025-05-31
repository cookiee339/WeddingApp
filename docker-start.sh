#!/bin/bash

# Barbara & Mikołaj Wedding App - Docker Startup Script
# This script starts the entire wedding photo app using Docker Compose

set -e

# Color codes for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
PURPLE='\033[0;35m'
NC='\033[0m' # No Color

# Configuration
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ENV_FILE="$SCRIPT_DIR/.env"
ENV_EXAMPLE="$SCRIPT_DIR/.env.example"
COMPOSE_FILE="$SCRIPT_DIR/docker-compose.yml"
COMPOSE_OVERRIDE="$SCRIPT_DIR/docker-compose.override.yml"

# Default values
MODE="production"
DETACHED=true
BUILD=false
LOGS=false
STOP=false
REBUILD=false

# Function to print colored output
print_header() {
    echo -e "${PURPLE}================================================================================================${NC}"
    echo -e "${PURPLE}🌸 Barbara & Mikołaj Wedding Photo App - Docker Startup 🌸${NC}"
    echo -e "${PURPLE}================================================================================================${NC}"
    echo ""
}

print_step() {
    echo -e "${BLUE}[STEP]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

print_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

# Function to show usage
show_usage() {
    echo "Usage: $0 [OPTIONS]"
    echo ""
    echo "Options:"
    echo "  -h, --help              Show this help message"
    echo "  -d, --dev               Start in development mode"
    echo "  -p, --prod              Start in production mode (default)"
    echo "  -f, --foreground        Run in foreground (show logs)"
    echo "  -b, --build             Force rebuild of images"
    echo "  -r, --rebuild           Stop, rebuild, and start services"
    echo "  -s, --stop              Stop all services"
    echo "  -l, --logs              Show logs and exit"
    echo "  --status                Show service status"
    echo ""
    echo "Examples:"
    echo "  $0                      Start in production mode (detached)"
    echo "  $0 -d -f                Start in development mode with logs"
    echo "  $0 -b                   Rebuild and start production services"
    echo "  $0 -s                   Stop all services"
    echo "  $0 -l                   Show current logs"
}

# Function to check prerequisites
check_prerequisites() {
    print_step "Checking prerequisites..."
    
    # Check Docker
    if ! command -v docker &> /dev/null; then
        print_error "Docker is not installed. Please install Docker and try again."
        exit 1
    fi
    
    # Check Docker Compose
    if ! command -v docker-compose &> /dev/null && ! docker compose version &> /dev/null; then
        print_error "Docker Compose is not installed. Please install Docker Compose and try again."
        exit 1
    fi
    
    # Check if Docker daemon is running
    if ! docker info &> /dev/null; then
        print_error "Docker daemon is not running. Please start Docker and try again."
        exit 1
    fi
    
    print_success "All prerequisites are met"
}

# Function to setup environment
setup_environment() {
    print_step "Setting up environment..."
    
    if [ ! -f "$ENV_FILE" ]; then
        if [ -f "$ENV_EXAMPLE" ]; then
            print_warning "No .env file found. Copying from .env.example..."
            cp "$ENV_EXAMPLE" "$ENV_FILE"
            print_warning "Please edit .env file with your actual configuration before running again."
            print_warning "Especially update CLOUDINARY_* settings for photo uploads."
            exit 1
        else
            print_error "No .env or .env.example file found. Please create environment configuration."
            exit 1
        fi
    fi
    
    print_success "Environment configuration found"
}

# Function to check compose files
check_compose_files() {
    print_step "Checking Docker Compose files..."
    
    if [ ! -f "$COMPOSE_FILE" ]; then
        print_error "docker-compose.yml not found in $SCRIPT_DIR"
        exit 1
    fi
    
    if [ "$MODE" = "development" ] && [ ! -f "$COMPOSE_OVERRIDE" ]; then
        print_warning "docker-compose.override.yml not found. Development mode may not work properly."
    fi
    
    print_success "Compose files are ready"
}

# Function to build images
build_images() {
    if [ "$BUILD" = true ] || [ "$REBUILD" = true ]; then
        print_step "Building Docker images..."
        
        if [ "$MODE" = "development" ]; then
            docker-compose -f "$COMPOSE_FILE" -f "$COMPOSE_OVERRIDE" build --no-cache
        else
            docker-compose -f "$COMPOSE_FILE" build --no-cache
        fi
        
        print_success "Images built successfully"
    fi
}

# Function to stop services
stop_services() {
    print_step "Stopping all services..."
    
    if [ "$MODE" = "development" ]; then
        docker-compose -f "$COMPOSE_FILE" -f "$COMPOSE_OVERRIDE" down --remove-orphans
    else
        docker-compose -f "$COMPOSE_FILE" down --remove-orphans
    fi
    
    print_success "All services stopped"
}

# Function to start services
start_services() {
    print_step "Starting services in $MODE mode..."
    
    if [ "$MODE" = "development" ]; then
        if [ "$DETACHED" = true ]; then
            docker-compose -f "$COMPOSE_FILE" -f "$COMPOSE_OVERRIDE" up -d
        else
            docker-compose -f "$COMPOSE_FILE" -f "$COMPOSE_OVERRIDE" up
        fi
    else
        if [ "$DETACHED" = true ]; then
            docker-compose -f "$COMPOSE_FILE" up -d
        else
            docker-compose -f "$COMPOSE_FILE" up
        fi
    fi
    
    if [ "$DETACHED" = true ]; then
        print_success "Services started successfully in detached mode"
        show_service_info
    fi
}

# Function to show logs
show_logs() {
    print_step "Showing service logs..."
    
    if [ "$MODE" = "development" ]; then
        docker-compose -f "$COMPOSE_FILE" -f "$COMPOSE_OVERRIDE" logs -f
    else
        docker-compose -f "$COMPOSE_FILE" logs -f
    fi
}

# Function to show service status
show_status() {
    print_step "Service Status:"
    
    if [ "$MODE" = "development" ]; then
        docker-compose -f "$COMPOSE_FILE" -f "$COMPOSE_OVERRIDE" ps
    else
        docker-compose -f "$COMPOSE_FILE" ps
    fi
}

# Function to show service information
show_service_info() {
    echo ""
    print_success "Wedding Photo App is now running!"
    echo ""
    print_info "🌐 Service URLs:"
    echo "   • Frontend (Wedding App):     http://localhost"
    echo "   • QR Generator:               http://localhost:3000"
    echo "   • Backend API:                http://localhost:8080"
    
    if [ "$MODE" = "development" ]; then
        echo "   • Frontend Dev Server:        http://localhost:5173"
        echo "   • Database Admin (Adminer):   http://localhost:8081"
        echo "   • Database:                   localhost:5432"
    fi
    
    echo ""
    print_info "📋 Quick Commands:"
    echo "   • View logs:          docker-compose logs -f"
    echo "   • Stop services:      docker-compose down"
    echo "   • Restart service:    docker-compose restart <service_name>"
    echo "   • Check status:       docker-compose ps"
    echo ""
    print_info "🎯 Next Steps:"
    echo "   1. Generate QR codes: Open http://localhost:3000"
    echo "   2. Test the app: Open http://localhost"
    echo "   3. Place QR codes at your wedding venue"
    echo "   4. Guests scan QR codes to access the photo app"
    echo ""
    print_info "💡 Tips:"
    echo "   • QR codes are saved in ./qr-codes/ directory"
    echo "   • Photos are stored in Cloudinary (configure in .env)"
    echo "   • Database data persists in Docker volumes"
    echo ""
}

# Function to wait for services
wait_for_services() {
    print_step "Waiting for services to be ready..."
    
    local max_attempts=60
    local attempt=0
    
    while [ $attempt -lt $max_attempts ]; do
        if curl -s http://localhost:8080/api/access/codes > /dev/null 2>&1; then
            print_success "Backend is ready!"
            break
        fi
        
        attempt=$((attempt + 1))
        echo -n "."
        sleep 2
    done
    
    if [ $attempt -eq $max_attempts ]; then
        print_warning "Backend took longer than expected to start. Check logs with: docker-compose logs backend"
    fi
    
    echo ""
}

# Parse command line arguments
while [[ $# -gt 0 ]]; do
    case $1 in
        -h|--help)
            show_usage
            exit 0
            ;;
        -d|--dev)
            MODE="development"
            shift
            ;;
        -p|--prod)
            MODE="production"
            shift
            ;;
        -f|--foreground)
            DETACHED=false
            shift
            ;;
        -b|--build)
            BUILD=true
            shift
            ;;
        -r|--rebuild)
            REBUILD=true
            STOP=true
            BUILD=true
            shift
            ;;
        -s|--stop)
            STOP=true
            shift
            ;;
        -l|--logs)
            LOGS=true
            shift
            ;;
        --status)
            check_prerequisites
            show_status
            exit 0
            ;;
        *)
            print_error "Unknown option: $1"
            show_usage
            exit 1
            ;;
    esac
done

# Main execution
main() {
    print_header
    
    # Change to script directory
    cd "$SCRIPT_DIR"
    
    # Handle stop command
    if [ "$STOP" = true ]; then
        check_prerequisites
        stop_services
        if [ "$REBUILD" = false ]; then
            exit 0
        fi
    fi
    
    # Handle logs command
    if [ "$LOGS" = true ]; then
        check_prerequisites
        show_logs
        exit 0
    fi
    
    # Normal startup process
    check_prerequisites
    setup_environment
    check_compose_files
    
    if [ "$REBUILD" = true ]; then
        stop_services
    fi
    
    build_images
    start_services
    
    if [ "$DETACHED" = true ]; then
        wait_for_services
    fi
}

# Cleanup function for graceful exit
cleanup() {
    print_warning "Received interrupt signal. Stopping services..."
    stop_services
    exit 1
}

# Set up signal handlers
trap cleanup INT TERM

# Run main function
main