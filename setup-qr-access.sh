#!/bin/bash

# Barbara & Mikołaj Wedding App - QR Access Control Setup Script
# This script sets up the QR code-based access control system

set -e

echo "🌸 Barbara & Mikołaj Wedding App - QR Access Control Setup 🌸"
echo "============================================================="
echo ""

# Color codes for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
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

# Check if Node.js is installed
check_node() {
    print_step "Checking Node.js installation..."
    if ! command -v node &> /dev/null; then
        print_error "Node.js is not installed. Please install Node.js 16+ and try again."
        exit 1
    fi
    
    NODE_VERSION=$(node --version | cut -d'v' -f2 | cut -d'.' -f1)
    if [ "$NODE_VERSION" -lt 16 ]; then
        print_error "Node.js version $NODE_VERSION is too old. Please install Node.js 16+ and try again."
        exit 1
    fi
    
    print_success "Node.js $(node --version) is installed"
}

# Check if Java is installed (for backend)
check_java() {
    print_step "Checking Java installation..."
    if ! command -v java &> /dev/null; then
        print_warning "Java is not installed. Backend will not work without Java 21+."
        return 1
    fi
    
    print_success "Java is installed"
    return 0
}

# Install QR generator dependencies
install_qr_dependencies() {
    print_step "Installing QR generator dependencies..."
    if [ ! -f "package.json" ]; then
        print_error "package.json not found. Please run this script from the WeddingApp directory."
        exit 1
    fi
    
    npm install
    print_success "QR generator dependencies installed"
}

# Install frontend dependencies
install_frontend_dependencies() {
    print_step "Installing frontend dependencies..."
    if [ ! -d "frontend" ]; then
        print_error "Frontend directory not found."
        exit 1
    fi
    
    cd frontend
    npm install
    cd ..
    print_success "Frontend dependencies installed"
}

# Setup backend dependencies
setup_backend() {
    print_step "Setting up backend..."
    if [ ! -d "backend" ]; then
        print_error "Backend directory not found."
        exit 1
    fi
    
    cd backend
    if [ -f "gradlew" ]; then
        chmod +x gradlew
        print_success "Backend setup completed"
    else
        print_error "Gradle wrapper not found in backend directory"
        exit 1
    fi
    cd ..
}

# Create QR codes output directory
create_directories() {
    print_step "Creating output directories..."
    mkdir -p qr-codes
    print_success "Output directories created"
}

# Start backend server in background
start_backend() {
    print_step "Starting backend server..."
    cd backend
    
    # Check if backend is already running
    if curl -s http://localhost:8080/api/access/codes &> /dev/null; then
        print_warning "Backend appears to be already running on port 8080"
        cd ..
        return 0
    fi
    
    # Start backend
    ./gradlew run &
    BACKEND_PID=$!
    cd ..
    
    # Wait for backend to start
    echo "Waiting for backend to start..."
    for i in {1..30}; do
        if curl -s http://localhost:8080/api/access/codes &> /dev/null; then
            print_success "Backend started successfully (PID: $BACKEND_PID)"
            return 0
        fi
        sleep 1
        echo -n "."
    done
    
    print_error "Backend failed to start within 30 seconds"
    return 1
}

# Test the access control system
test_system() {
    print_step "Testing access control system..."
    
    if npm run test; then
        print_success "All tests passed! System is working correctly."
        return 0
    else
        print_error "Tests failed. Please check the error messages above."
        return 1
    fi
}

# Display usage instructions
show_instructions() {
    echo ""
    echo "🎉 Setup completed successfully!"
    echo ""
    echo "Next steps:"
    echo "==========="
    echo ""
    echo "1. Generate QR codes for your wedding guests:"
    echo "   npm run generate"
    echo ""
    echo "2. Start the frontend development server:"
    echo "   cd frontend && npm run dev"
    echo ""
    echo "3. Your QR codes will be saved in the 'qr-codes' directory"
    echo ""
    echo "4. Print the QR codes and place them at wedding tables"
    echo ""
    echo "5. Guests can scan QR codes to access the photo app"
    echo ""
    echo "URLs:"
    echo "-----"
    echo "Frontend: http://localhost:5173"
    echo "Backend API: http://localhost:8080"
    echo ""
    echo "Files:"
    echo "------"
    echo "QR Codes: ./qr-codes/"
    echo "Documentation: ./QR-ACCESS-SETUP.md"
    echo ""
    echo "Have a wonderful wedding! 💕"
}

# Main setup process
main() {
    # Check prerequisites
    check_node
    JAVA_OK=0
    check_java && JAVA_OK=1
    
    # Install dependencies
    install_qr_dependencies
    install_frontend_dependencies
    
    # Setup backend if Java is available
    if [ $JAVA_OK -eq 1 ]; then
        setup_backend
    fi
    
    # Create directories
    create_directories
    
    # Start and test backend if Java is available
    if [ $JAVA_OK -eq 1 ]; then
        if start_backend; then
            sleep 2
            if test_system; then
                show_instructions
            else
                print_error "System tests failed. Please check the backend logs."
                exit 1
            fi
        else
            print_error "Failed to start backend. Please check the logs and try manually."
            exit 1
        fi
    else
        print_warning "Skipping backend setup - Java not found"
        print_warning "Please install Java 21+ and start the backend manually:"
        print_warning "cd backend && ./gradlew run"
        echo ""
        show_instructions
    fi
}

# Cleanup function for graceful exit
cleanup() {
    if [ ! -z "$BACKEND_PID" ]; then
        print_step "Stopping backend server..."
        kill $BACKEND_PID 2>/dev/null || true
    fi
}

# Set up signal handlers
trap cleanup EXIT INT TERM

# Run main setup
main