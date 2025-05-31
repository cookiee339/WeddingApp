FROM node:18-alpine

WORKDIR /app

# Install dependencies for canvas and image processing
RUN apk add --no-cache \
    cairo-dev \
    jpeg-dev \
    pango-dev \
    musl-dev \
    giflib-dev \
    pixman-dev \
    pangomm-dev \
    libjpeg-turbo-dev \
    freetype-dev

# Copy package files
COPY package*.json ./

# Install npm dependencies
RUN npm install --only=production

# Copy application code
COPY . .

# Create output directory for QR codes
RUN mkdir -p /app/qr-codes

# Expose port for web service
EXPOSE 3000

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:3000/health || exit 1

# Run the QR generator service
CMD ["node", "qr-web-service.js"]