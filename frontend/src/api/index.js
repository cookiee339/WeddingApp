import axios from 'axios';

// Create axios instance with base URL from environment variables
const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080',
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 30000, // 30 seconds timeout
});

// Add request interceptor to handle common request tasks
apiClient.interceptors.request.use(
  (config) => {
    // Add any auth tokens here if needed in the future
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Add response interceptor to handle common errors
apiClient.interceptors.response.use(
  (response) => {
    return response;
  },
  (error) => {
    const { response } = error;
    
    if (!response) {
      console.error('Network error: Could not connect to the server');
      // Handle offline state or server down
    } else {
      // Log specific error codes
      console.error(`API Error (${response.status}): ${response.data?.message || 'Unknown error'}`);
    }
    
    return Promise.reject(error);
  }
);

/**
 * API functions for interacting with the backend
 */
export default {
  /**
   * Get all photos with optional filtering
   * 
   * @param {Object} options - Query options
   * @param {string} [options.uploaderId] - Optional uploader ID to filter photos
   * @param {number} [options.page=1] - Page number for pagination
   * @param {number} [options.limit=20] - Number of photos per page
   * @returns {Promise<Array>} Array of photo objects
   */
  getPhotos(options = {}) {
    return apiClient.get('/photos', { params: options })
      .then(response => response.data.data);
  },
  
  /**
   * Get a specific photo by ID
   * 
   * @param {number} photoId - The ID of the photo to fetch
   * @returns {Promise<Object>} Photo object
   */
  getPhotoById(photoId) {
    return apiClient.get(`/photos/${photoId}`)
      .then(response => response.data);
  },
  
  /**
   * Upload a new photo
   * 
   * @param {File} file - The image file to upload
   * @param {string} uploaderId - The ID of the uploader
   * @param {Function} [onProgress] - Optional progress callback function
   * @returns {Promise<Object>} Uploaded photo object
   */
  uploadPhoto(file, uploaderId, onProgress = null) {
    const formData = new FormData();
    formData.append('image', file);
    formData.append('uploader_id', uploaderId);
    
    const config = {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    };
    
    if (onProgress) {
      config.onUploadProgress = (progressEvent) => {
        const percentage = Math.round((progressEvent.loaded * 100) / progressEvent.total);
        onProgress(percentage);
      };
    }
    
    return apiClient.post('/photos', formData, config).then(response => response.data);
  },
  
  /**
   * Delete a photo
   * 
   * @param {number} photoId - The ID of the photo to delete
   * @param {string} uploaderId - The ID of the uploader for verification
   * @returns {Promise<void>}
   */
  deletePhoto(photoId, uploaderId) {
    return apiClient.delete(`/photos/${photoId}`, {
      data: { uploader_id: uploaderId }
    });
  }
};