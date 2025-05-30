<template>
  <div class="flex flex-col items-center justify-center pt-6">
    <div class="text-center mb-8">
      <h2 class="text-3xl font-display text-wedding-dark mb-3">Capture a Moment</h2>
      <p class="text-lg text-gray-700">
        Share your special moments from this wedding celebration
      </p>
    </div>

    <div class="photo-capture-area w-full max-w-md mb-8">
      <!-- Camera preview when active -->
      <div v-if="showPreview" class="relative rounded-lg overflow-hidden shadow-lg bg-black mb-4">
        <video ref="videoPreview" autoplay playsinline muted class="w-full h-auto"></video>
        <div class="absolute bottom-0 left-0 right-0 bg-black bg-opacity-50 p-3 flex justify-center">
          <button @click="capturePhoto" class="btn-secondary">
            <span class="flex items-center">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 9a2 2 0 012-2h.93a2 2 0 001.664-.89l.812-1.22A2 2 0 0110.07 4h3.86a2 2 0 011.664.89l.812 1.22A2 2 0 0018.07 7H19a2 2 0 012 2v9a2 2 0 01-2 2H5a2 2 0 01-2-2V9z" />
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 13a3 3 0 11-6 0 3 3 0 016 0z" />
              </svg>
              Take Photo
            </span>
          </button>
        </div>
      </div>

      <!-- Captured photo preview -->
      <div v-if="capturedImageUrl" class="relative rounded-lg overflow-hidden shadow-lg mb-4">
        <img :src="capturedImageUrl" alt="Captured photo" class="w-full h-auto" />
        <div class="absolute bottom-0 left-0 right-0 bg-black bg-opacity-50 p-3 flex justify-between">
          <button @click="discardPhoto" class="btn-secondary bg-gray-500">
            Discard
          </button>
          <button @click="uploadPhoto" class="btn-primary" :disabled="isUploading">
            {{ isUploading ? 'Uploading...' : 'Share Photo' }}
          </button>
        </div>
      </div>

      <!-- Photo capture button when preview not active -->
      <div v-if="!showPreview && !capturedImageUrl" class="flex flex-col items-center">
        <label for="photo-input" class="btn-primary text-center cursor-pointer mb-3 flex items-center">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 9a2 2 0 012-2h.93a2 2 0 001.664-.89l.812-1.22A2 2 0 0110.07 4h3.86a2 2 0 011.664.89l.812 1.22A2 2 0 0018.07 7H19a2 2 0 012 2v9a2 2 0 01-2 2H5a2 2 0 01-2-2V9z" />
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 13a3 3 0 11-6 0 3 3 0 016 0z" />
          </svg>
          Take a Photo
        </label>
        <input 
          id="photo-input" 
          type="file" 
          accept="image/*" 
          capture="environment" 
          class="hidden"
          @change="handleFileInput" 
        />
        <p class="text-sm text-gray-500">or</p>
        <button @click="startCameraPreview" class="mt-3 text-wedding-primary underline">
          Use camera preview
        </button>
      </div>
    </div>

    <!-- Status messages -->
    <div v-if="statusMessage" class="w-full max-w-md p-3 rounded-lg" :class="{
      'bg-green-100 text-green-800': statusType === 'success',
      'bg-red-100 text-red-800': statusType === 'error',
      'bg-blue-100 text-blue-800': statusType === 'info'
    }">
      {{ statusMessage }}
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick } from 'vue';
import api from '../api';

// State variables
const videoPreview = ref(null);
const showPreview = ref(false);
const capturedImageUrl = ref(null);
const isUploading = ref(false);
const statusMessage = ref('');
const statusType = ref('');
let mediaStream = null;
let canvas = null;

// Start camera preview
async function startCameraPreview() {
  try {
    // Clear any previous status messages
    statusMessage.value = '';
    
    // Check if camera is supported
    if (!navigator.mediaDevices || !navigator.mediaDevices.getUserMedia) {
      setStatus('Camera not supported in this browser. Please use the file upload option.', 'error');
      return;
    }

    // Check if we're on HTTPS (required for camera access)
    if (location.protocol !== 'https:' && location.hostname !== 'localhost' && location.hostname !== '127.0.0.1') {
      setStatus('Camera requires HTTPS. Please use the file upload option or access via HTTPS.', 'error');
      return;
    }

    console.log('Starting camera preview...');
    setStatus('Requesting camera permission...', 'info');

    // First try with ideal constraints
    try {
      console.log('Trying with ideal constraints...');
      mediaStream = await navigator.mediaDevices.getUserMedia({ 
        audio: false, // Explicitly state we don't need audio
        video: { 
          facingMode: 'environment',
          width: { ideal: 1280 },
          height: { ideal: 720 }
        } 
      });
      console.log('Camera stream obtained with ideal constraints');
    } catch (constraintError) {
      console.warn('Failed with ideal constraints, trying with basic constraints:', constraintError);
      setStatus('Trying basic camera settings...', 'info');
      
      // If that fails, try with minimal constraints
      mediaStream = await navigator.mediaDevices.getUserMedia({ 
        audio: false,
        video: true 
      });
      console.log('Camera stream obtained with basic constraints');
    }

    // Show preview first to make sure video element exists
    showPreview.value = true;
    
    // Wait for next tick to ensure DOM is updated
    await new Promise(resolve => setTimeout(resolve, 100));
    
    if (videoPreview.value) {
      console.log('Setting video source...');
      videoPreview.value.srcObject = mediaStream;
      
      // Wait for video element to be ready
      videoPreview.value.onloadedmetadata = async () => {
        console.log('Video metadata loaded, attempting to play...');
        try {
          await videoPreview.value.play();
          setStatus('Camera ready! You can now take a photo.', 'success');
          console.log('Camera preview started successfully');
        } catch (playError) {
          console.error('Error playing video:', playError);
          setStatus('Could not start video preview. Browser may have blocked autoplay.', 'error');
          stopCameraPreview();
        }
      };

      // Handle video errors
      videoPreview.value.onerror = (error) => {
        console.error('Video element error:', error);
        setStatus('Video playback error. Please try again.', 'error');
        stopCameraPreview();
      };
    } else {
      console.error('Video preview element not found after DOM update');
      setStatus('Video preview not available. Please refresh the page.', 'error');
      stopCameraPreview();
    }
  } catch (error) {
    console.error('Error accessing camera:', error);
    
    // Provide specific error messages based on error type
    if (error.name === 'NotAllowedError') {
      setStatus('Camera permission denied. Please allow camera access and try again.', 'error');
    } else if (error.name === 'NotFoundError') {
      setStatus('No camera found. Please use the file upload option.', 'error');
    } else if (error.name === 'NotSupportedError') {
      setStatus('Camera not supported. Please use the file upload option.', 'error');
    } else if (error.name === 'NotReadableError') {
      setStatus('Camera is being used by another application. Please close other camera apps and try again.', 'error');
    } else {
      setStatus(`Camera error: ${error.message}. Please use the file upload option.`, 'error');
    }
    
    stopCameraPreview();
  }
}

// Stop camera preview
function stopCameraPreview() {
  if (mediaStream) {
    mediaStream.getTracks().forEach(track => track.stop());
    mediaStream = null;
  }
  showPreview.value = false;
}

// Capture photo from video stream
function capturePhoto() {
  if (!videoPreview.value) return;

  if (!canvas) {
    canvas = document.createElement('canvas');
  }

  const video = videoPreview.value;
  canvas.width = video.videoWidth;
  canvas.height = video.videoHeight;

  const ctx = canvas.getContext('2d');
  ctx.drawImage(video, 0, 0, canvas.width, canvas.height);

  capturedImageUrl.value = canvas.toDataURL('image/jpeg');
  stopCameraPreview();
}

// Handle file input from the native camera/gallery
function handleFileInput(event) {
  const file = event.target.files[0];
  if (!file) return;

  const reader = new FileReader();
  reader.onload = (e) => {
    capturedImageUrl.value = e.target.result;
  };
  reader.readAsDataURL(file);
}

// Discard captured photo
function discardPhoto() {
  capturedImageUrl.value = null;
  statusMessage.value = '';
}

// Upload photo to the backend
async function uploadPhoto() {
  if (!capturedImageUrl.value) return;

  // Get uploader ID from localStorage
  const uploaderId = localStorage.getItem('uploader_id');
  if (!uploaderId) {
    setStatus('Error: Could not identify user. Please refresh the page.', 'error');
    return;
  }

  isUploading.value = true;

  try {
    // Convert base64 to blob
    const blob = await fetch(capturedImageUrl.value).then(res => res.blob());

    // Send to backend using API client
    await api.uploadPhoto(blob, uploaderId);

    setStatus('Photo uploaded successfully!', 'success');
    // Clear the captured image after short delay
    setTimeout(() => {
      capturedImageUrl.value = null;
      statusMessage.value = '';
    }, 2000);
  } catch (error) {
    console.error('Error uploading photo:', error);
    setStatus('Error uploading photo. Please try again.', 'error');
  } finally {
    isUploading.value = false;
  }
}

// Helper for setting status messages
function setStatus(message, type) {
  statusMessage.value = message;
  statusType.value = type;
  console.log(`Status: ${type} - ${message}`);
}

// Check camera capabilities on mount
onMounted(async () => {
  // Check if getUserMedia is supported
  if (!navigator.mediaDevices || !navigator.mediaDevices.getUserMedia) {
    console.warn('getUserMedia is not supported in this browser');
    // We don't show an error message here as the user hasn't tried to use the camera yet
  } else {
    // Log camera debugging information
    console.log('=== Camera Debug Info ===');
    console.log('Protocol:', location.protocol);
    console.log('Hostname:', location.hostname);
    console.log('getUserMedia supported:', !!navigator.mediaDevices.getUserMedia);
    
    try {
      const devices = await navigator.mediaDevices.enumerateDevices();
      const videoDevices = devices.filter(device => device.kind === 'videoinput');
      console.log('Video devices found:', videoDevices.length);
      videoDevices.forEach((device, index) => {
        console.log(`Camera ${index + 1}:`, device.label || 'Unknown camera');
      });
    } catch (error) {
      console.log('Could not enumerate devices:', error);
    }
    console.log('========================');
  }
});

// Clean up on component unmount
onUnmounted(() => {
  stopCameraPreview();
});
</script>
