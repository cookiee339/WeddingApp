<template>
  <div class="flex flex-col items-center justify-center pt-6 relative">
    <!-- Decorative floral elements around the main content -->
    <div class="absolute top-0 left-4 opacity-35 hidden md:block">
      <svg class="w-20 h-24 text-boho-dusty-rose" viewBox="0 0 30 40" fill="currentColor">
        <path d="M15 35v-20c0-2 1-3 2-4" stroke="currentColor" stroke-width="1" fill="none"/>
        <ellipse cx="17" cy="10" rx="2.5" ry="6" opacity="0.6"/>
        <ellipse cx="13" cy="12" rx="2" ry="5" opacity="0.4"/>
        <ellipse cx="19" cy="15" rx="2" ry="5" opacity="0.5"/>
      </svg>
    </div>

    <div class="absolute top-8 right-4 opacity-35 hidden md:block">
      <svg class="w-16 h-16 text-boho-dusty-rose" viewBox="0 0 25 25" fill="currentColor">
        <circle cx="12.5" cy="12.5" r="2"/>
        <ellipse cx="12.5" cy="6" rx="1.5" ry="3"/>
        <ellipse cx="19" cy="12.5" rx="1.5" ry="3" transform="rotate(60 19 12.5)"/>
        <ellipse cx="19" cy="19" rx="1.5" ry="3" transform="rotate(120 19 19)"/>
        <ellipse cx="12.5" cy="19" rx="1.5" ry="3" transform="rotate(180 12.5 19)"/>
        <ellipse cx="6" cy="19" rx="1.5" ry="3" transform="rotate(240 6 19)"/>
        <ellipse cx="6" cy="12.5" rx="1.5" ry="3" transform="rotate(300 6 12.5)"/>
      </svg>
    </div>

    <div class="text-center mb-8 md:mb-16 relative z-10">
      <h2 class="text-2xl md:text-3xl font-semibold mb-6 text-boho-brown-dark">Zrób Zdjęcie</h2>
      <p class="text-sm md:text-base opacity-70 text-boho-brown">
        Podziel się radością! Zrób zdjęcie i dodaj je do naszej wspólnej księgi wspomnień.
      </p>
      
      <!-- Small floral accent under subtitle -->
      <div class="flex justify-center mt-4">
        <svg class="w-20 h-5 text-boho-dusty-rose opacity-60" viewBox="0 0 60 15" fill="currentColor">
          <circle cx="10" cy="7.5" r="1"/>
          <circle cx="20" cy="6" r="1"/>
          <circle cx="30" cy="9" r="1.5"/>
          <circle cx="40" cy="6" r="1"/>
          <circle cx="50" cy="7.5" r="1"/>
          <path d="M5 7.5c2-0.5 4-0.5 6 0M15 6c2-0.5 4-0.5 6 0M35 6c2-0.5 4-0.5 6 0M45 7.5c2-0.5 4-0.5 6 0" stroke="currentColor" stroke-width="0.5" fill="none"/>
        </svg>
      </div>
    </div>

    <section class="capture-section w-full mb-12 md:mb-16 relative">
      <!-- Decorative corner florals for the capture section -->
      <div class="absolute -top-2 -left-2 opacity-40 z-0">
        <svg class="w-14 h-14 text-boho-dusty-rose" viewBox="0 0 20 20" fill="currentColor">
          <path d="M2 18c2-1 4-2 6-4s3-4 4-6" stroke="currentColor" stroke-width="1" fill="none"/>
          <circle cx="4" cy="16" r="1" opacity="0.7"/>
          <circle cx="7" cy="13" r="1.2" opacity="0.6"/>
          <circle cx="10" cy="10" r="1" opacity="0.7"/>
          <circle cx="12" cy="8" r="0.8" opacity="0.5"/>
        </svg>
      </div>
      
      <div class="absolute -top-2 -right-2 opacity-40 z-0" style="transform: scaleX(-1)">
        <svg class="w-14 h-14 text-boho-dusty-rose" viewBox="0 0 20 20" fill="currentColor">
          <path d="M2 18c2-1 4-2 6-4s3-4 4-6" stroke="currentColor" stroke-width="1" fill="none"/>
          <circle cx="4" cy="16" r="1" opacity="0.7"/>
          <circle cx="7" cy="13" r="1.2" opacity="0.6"/>
          <circle cx="10" cy="10" r="1" opacity="0.7"/>
          <circle cx="12" cy="8" r="0.8" opacity="0.5"/>
        </svg>
      </div>
      <!-- Camera preview when active -->
      <div v-if="showPreview" class="relative rounded-xl overflow-hidden shadow-lg bg-black mb-4 max-h-[70vh]">
        <video ref="videoPreview" autoplay playsinline muted class="w-full h-full object-cover"></video>
        <!-- Camera controls overlay -->
        <div class="absolute inset-0 pointer-events-none">
          <!-- Grid lines for composition -->
          <div class="absolute inset-4 border border-white opacity-20 grid grid-cols-3 grid-rows-3">
            <div class="border-r border-b border-white"></div>
            <div class="border-r border-b border-white"></div>
            <div class="border-b border-white"></div>
            <div class="border-r border-b border-white"></div>
            <div class="border-r border-b border-white"></div>
            <div class="border-b border-white"></div>
            <div class="border-r border-white"></div>
            <div class="border-r border-white"></div>
            <div></div>
          </div>
        </div>
        <!-- Control buttons -->
        <div class="absolute bottom-0 left-0 right-0 bg-gradient-to-t from-black to-transparent p-4 pointer-events-auto">
          <div class="flex justify-between items-center">
            <button @click="stopCameraPreview" class="bg-white bg-opacity-20 hover:bg-opacity-30 text-white rounded-full p-3 transition-all touch-manipulation">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
              </svg>
            </button>
            <!-- Large capture button -->
            <button @click="capturePhoto" class="bg-white hover:bg-gray-100 text-gray-800 rounded-full p-6 shadow-lg transition-all transform active:scale-95 touch-manipulation">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-8 w-8" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 9a2 2 0 012-2h.93a2 2 0 001.664-.89l.812-1.22A2 2 0 0110.07 4h3.86a2 2 0 011.664.89l.812 1.22A2 2 0 0018.07 7H19a2 2 0 012 2v9a2 2 0 01-2 2H5a2 2 0 01-2-2V9z" />
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 13a3 3 0 11-6 0 3 3 0 016 0z" />
              </svg>
            </button>
            <!-- Switch camera button (for devices with multiple cameras) -->
            <button @click="switchCamera" class="bg-white bg-opacity-20 hover:bg-opacity-30 text-white rounded-full p-3 transition-all touch-manipulation">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
              </svg>
            </button>
          </div>
        </div>
      </div>

      <!-- Captured photo preview -->
      <div v-if="capturedImageUrl" class="space-y-4">
        <div class="relative rounded-xl overflow-hidden shadow-lg max-h-[50vh]">
          <img :src="capturedImageUrl" alt="Przechwycone zdjęcie" class="w-full h-full object-cover" />
          <!-- Upload progress overlay -->
          <div v-if="isUploading" class="absolute inset-0 bg-black bg-opacity-80 flex items-center justify-center">
            <div class="text-center text-white max-w-xs px-4">
              <div class="loading-spinner h-16 w-16 mx-auto mb-6"></div>
              <p class="text-xl font-medium mb-4">{{ uploadMessage }}</p>
              <!-- Progress bar -->
              <div class="w-full bg-gray-600 rounded-full h-3 mb-3">
                <div 
                  class="bg-white h-3 rounded-full transition-all duration-300 ease-out" 
                  :style="{ width: uploadProgress + '%' }"
                ></div>
              </div>
              <p class="text-lg opacity-75">{{ uploadProgress }}% ukończone</p>
            </div>
          </div>
        </div>
        
        <!-- Caption input -->
        <div class="space-y-3">
          <label class="block text-boho-brown-dark font-medium text-lg">
            💭 Dodaj wiadomość (opcjonalnie)
          </label>
          <textarea 
            v-model="photoCaption"
            placeholder="Podziel się swoimi myślami o tym momencie... 💕"
            class="w-full p-4 rounded-xl border-2 border-boho-rose-border focus:border-boho-dusty-rose focus:outline-none resize-none text-lg bg-white transition-colors touch-manipulation"
            rows="3"
            maxlength="200"
            :disabled="isUploading"
          ></textarea>
          <div class="flex justify-between items-center text-sm text-boho-brown opacity-70">
            <span>{{ photoCaption.length }}/200 znaków</span>
            <span v-if="photoCaption.length > 0" class="text-boho-dusty-rose">✨ Idealne!</span>
          </div>
        </div>

        <!-- Action buttons -->
        <div class="flex gap-4">
          <button @click="discardPhoto" class="flex-1 bg-gray-100 hover:bg-gray-200 text-gray-700 py-4 px-6 rounded-xl font-semibold transition-all transform active:scale-95 touch-manipulation">
            <span class="flex items-center justify-center">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
              </svg>
              Odrzuć
            </span>
          </button>
          <button @click="uploadPhoto" class="flex-2 bg-boho-dusty-rose hover:bg-boho-dusty-rose-dark text-white py-4 px-8 rounded-xl font-bold transition-all transform active:scale-95 touch-manipulation shadow-lg" :disabled="isUploading">
            <span v-if="isUploading" class="flex items-center justify-center">
              <div class="animate-spin rounded-full h-5 w-5 border-b-2 border-white mr-2"></div>
              Przesyłanie...
            </span>
            <span v-else class="flex items-center justify-center">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 16a4 4 0 01-.88-7.903A5 5 0 1115.9 6L16 6a5 5 0 011 9.9M15 13l-3-3m0 0l-3 3m3-3v12" />
              </svg>
              🎉 Udostępnij Zdjęcie
            </span>
          </button>
        </div>
      </div>

      <!-- Photo capture button when preview not active -->
      <div v-if="!showPreview && !capturedImageUrl" class="flex flex-col items-center space-y-6">
        <!-- Primary action: Take photo directly -->
        <label for="photo-input" class="w-full bg-boho-dusty-rose hover:bg-boho-dusty-rose-dark text-white py-6 px-8 rounded-2xl text-center cursor-pointer font-bold text-xl transition-all transform active:scale-95 touch-manipulation shadow-lg">
          <span class="flex items-center justify-center">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-8 w-8 mr-3" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 9a2 2 0 012-2h.93a2 2 0 001.664-.89l.812-1.22A2 2 0 0110.07 4h3.86a2 2 0 011.664.89l.812 1.22A2 2 0 0118.07 7H19a2 2 0 012 2v9a2 2 0 01-2 2H5a2 2 0 01-2-2V9z" />
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 13a3 3 0 11-6 0 3 3 0 016 0z" />
            </svg>
            📸 Zrób Zdjęcie
          </span>
        </label>
        <input 
          id="photo-input" 
          type="file" 
          accept="image/*" 
          capture="environment" 
          class="hidden"
          @change="handleFileInput" 
        />
        
        <!-- Divider -->
        <div class="flex items-center w-full">
          <div class="flex-1 h-px bg-boho-brown opacity-20"></div>
          <span class="px-4 text-boho-brown opacity-70 text-sm">lub</span>
          <div class="flex-1 h-px bg-boho-brown opacity-20"></div>
        </div>
        
        <!-- Secondary action: Camera preview -->
        <button @click="startCameraPreview" class="w-full bg-white border-2 border-boho-dusty-rose text-boho-dusty-rose py-4 px-6 rounded-xl font-semibold transition-all transform active:scale-95 touch-manipulation">
          <span class="flex items-center justify-center">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 10l4.553-2.276A1 1 0 0121 8.618v6.764a1 1 0 01-1.447.894L15 14M5 18h8a2 2 0 002-2V8a2 2 0 00-2-2H5a2 2 0 00-2 2v8a2 2 0 002 2z" />
            </svg>
            Użyj Podglądu Kamery
          </span>
        </button>
      </div>
    </section>

    <!-- Photo Prompts Section -->
    <PhotoPrompts ref="photoPromptsRef" class="max-w-lg mx-auto mb-8" />


    <!-- Decorative bottom florals -->
    <div class="absolute bottom-16 left-8 opacity-25 hidden lg:block">
      <svg class="w-24 h-18 text-boho-brown" viewBox="0 0 40 30" fill="currentColor">
        <path d="M5 25c3-2 6-4 8-7s3-6 4-9c1-3 2-6 3-9" stroke="currentColor" stroke-width="1.5" fill="none"/>
        <ellipse cx="8" cy="20" rx="2" ry="3" opacity="0.7"/>
        <ellipse cx="12" cy="16" rx="2.5" ry="3.5" opacity="0.6"/>
        <ellipse cx="16" cy="12" rx="2" ry="3" opacity="0.7"/>
        <ellipse cx="19" cy="8" rx="1.5" ry="2.5" opacity="0.6"/>
        <ellipse cx="21" cy="5" rx="1" ry="2" opacity="0.5"/>
      </svg>
    </div>

    <div class="absolute bottom-16 right-8 opacity-25 hidden lg:block">
      <svg class="w-20 h-15 text-boho-brown" viewBox="0 0 35 25" fill="currentColor" style="transform: scaleX(-1)">
        <path d="M5 20c2-1 4-2 6-4s3-4 4-6c1-2 2-4 3-6" stroke="currentColor" stroke-width="1.5" fill="none"/>
        <ellipse cx="7" cy="16" rx="1.5" ry="2.5" opacity="0.7"/>
        <ellipse cx="10" cy="13" rx="2" ry="3" opacity="0.6"/>
        <ellipse cx="13" cy="10" rx="1.5" ry="2.5" opacity="0.7"/>
        <ellipse cx="16" cy="7" rx="1" ry="2" opacity="0.6"/>
        <ellipse cx="18" cy="4" rx="0.8" ry="1.5" opacity="0.5"/>
      </svg>
    </div>

    <!-- Status messages -->
    <div v-if="statusMessage" class="w-full max-w-lg" :class="{
      'status-success': statusType === 'success',
      'status-error': statusType === 'error',
      'status-info': statusType === 'info'
    }">
      {{ statusMessage }}
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick } from 'vue';
import api from '../api';
import PhotoPrompts from '../components/PhotoPrompts.vue';

// State variables
const videoPreview = ref(null);
const showPreview = ref(false);
const capturedImageUrl = ref(null);
const photoCaption = ref('');
const isUploading = ref(false);
const uploadProgress = ref(0);
const uploadMessage = ref('Przesyłanie zdjęcia...');
const statusMessage = ref('');
const statusType = ref('');
const photoPromptsRef = ref(null);
let mediaStream = null;
let canvas = null;
let currentFacingMode = 'environment';

// Start camera preview
async function startCameraPreview() {
  try {
    // Clear any previous status messages
    statusMessage.value = '';
    
    // Check if camera is supported
    if (!navigator.mediaDevices || !navigator.mediaDevices.getUserMedia) {
      setStatus('Kamera nie jest obsługiwana w tej przeglądarce. Proszę użyć opcji przesyłania pliku.', 'error');
      return;
    }

    // Check if we're on HTTPS (required for camera access)
    if (location.protocol !== 'https:' && location.hostname !== 'localhost' && location.hostname !== '127.0.0.1') {
      setStatus('Kamera wymaga HTTPS. Proszę użyć opcji przesyłania pliku lub dostępu przez HTTPS.', 'error');
      return;
    }

    console.log('Starting camera preview...');
    setStatus('Proszę o pozwolenie na dostęp do kamery...', 'info');

    // First try with mobile-optimized constraints
    try {
      console.log('Trying with mobile-optimized constraints...');
      mediaStream = await navigator.mediaDevices.getUserMedia({ 
        audio: false,
        video: { 
          facingMode: { ideal: 'environment' },
          width: { ideal: 1920, max: 1920 },
          height: { ideal: 1080, max: 1080 },
          aspectRatio: { ideal: 16/9 }
        } 
      });
      console.log('Camera stream obtained with mobile-optimized constraints');
    } catch (constraintError) {
      console.warn('Failed with mobile constraints, trying with fallback:', constraintError);
      setStatus('Dostosowywanie ustawień kamery dla Twojego urządzenia...', 'info');
      
      try {
        // Try with just facingMode
        mediaStream = await navigator.mediaDevices.getUserMedia({ 
          audio: false,
          video: { facingMode: 'environment' }
        });
        console.log('Camera stream obtained with environment facing mode');
      } catch (facingError) {
        console.warn('Environment facing mode failed, trying any camera:', facingError);
        // If that fails, try with minimal constraints
        mediaStream = await navigator.mediaDevices.getUserMedia({ 
          audio: false,
          video: true 
        });
        console.log('Camera stream obtained with basic constraints');
      }
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
          setStatus('Kamera gotowa! Możesz teraz zrobić zdjęcie.', 'success');
          console.log('Camera preview started successfully');
        } catch (playError) {
          console.error('Error playing video:', playError);
          setStatus('Nie można uruchomić podglądu wideo. Przeglądarka mogła zablokować automatyczne odtwarzanie.', 'error');
          stopCameraPreview();
        }
      };

      // Handle video errors
      videoPreview.value.onerror = (error) => {
        console.error('Video element error:', error);
        setStatus('Błąd odtwarzania wideo. Proszę spróbować ponownie.', 'error');
        stopCameraPreview();
      };
    } else {
      console.error('Video preview element not found after DOM update');
      setStatus('Podgląd wideo niedostępny. Proszę odświeżyć stronę.', 'error');
      stopCameraPreview();
    }
  } catch (error) {
    console.error('Error accessing camera:', error);
    
    // Provide specific error messages based on error type
    if (error.name === 'NotAllowedError') {
      setStatus('Odmówiono dostępu do kamery. Proszę zezwolić na dostęp do kamery i spróbować ponownie.', 'error');
    } else if (error.name === 'NotFoundError') {
      setStatus('Nie znaleziono kamery. Proszę użyć opcji przesyłania pliku.', 'error');
    } else if (error.name === 'NotSupportedError') {
      setStatus('Kamera nie jest obsługiwana. Proszę użyć opcji przesyłania pliku.', 'error');
    } else if (error.name === 'NotReadableError') {
      setStatus('Kamera jest używana przez inną aplikację. Proszę zamknąć inne aplikacje kamery i spróbować ponownie.', 'error');
    } else {
      setStatus(`Błąd kamery: ${error.message}. Proszę użyć opcji przesyłania pliku.`, 'error');
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

  // Convert to high-quality JPEG
  capturedImageUrl.value = canvas.toDataURL('image/jpeg', 0.9);
  stopCameraPreview();
  
  // Provide haptic feedback if available
  if ('vibrate' in navigator) {
    navigator.vibrate(50);
  }
}

// Switch between front and back camera
async function switchCamera() {
  currentFacingMode = currentFacingMode === 'environment' ? 'user' : 'environment';
  
  // Stop current stream
  if (mediaStream) {
    mediaStream.getTracks().forEach(track => track.stop());
  }
  
  try {
    setStatus('Przełączanie kamery...', 'info');
    mediaStream = await navigator.mediaDevices.getUserMedia({
      audio: false,
      video: {
        facingMode: { ideal: currentFacingMode },
        width: { ideal: 1920, max: 1920 },
        height: { ideal: 1080, max: 1080 }
      }
    });
    
    if (videoPreview.value) {
      videoPreview.value.srcObject = mediaStream;
      await videoPreview.value.play();
      setStatus('Kamera przełączona pomyślnie!', 'success');
      setTimeout(() => setStatus('', ''), 2000);
    }
  } catch (error) {
    console.error('Error switching camera:', error);
    setStatus('Nie można przełączyć kamery. Używam bieżącej kamery.', 'error');
    // Restart with original facing mode
    currentFacingMode = currentFacingMode === 'environment' ? 'user' : 'environment';
    setTimeout(() => setStatus('', ''), 3000);
  }
}

// Handle file input from the native camera/gallery
function handleFileInput(event) {
  const file = event.target.files[0];
  if (!file) return;

  // Validate file type
  if (!file.type.startsWith('image/')) {
    setStatus('Proszę wybrać plik obrazu.', 'error');
    return;
  }

  // Validate file size (max 10MB)
  if (file.size > 10 * 1024 * 1024) {
    setStatus('Obraz jest za duży. Proszę wybrać obraz mniejszy niż 10MB.', 'error');
    return;
  }

  setStatus('Przetwarzanie zdjęcia...', 'info');
  
  const reader = new FileReader();
  reader.onload = (e) => {
    capturedImageUrl.value = e.target.result;
    setStatus('Zdjęcie gotowe do udostępnienia! 📸✨', 'success');
    setTimeout(() => setStatus('', ''), 2000);
    
    // Provide haptic feedback
    if ('vibrate' in navigator) {
      navigator.vibrate(50);
    }
    
    // Clear the input so the same file can be selected again
    event.target.value = '';
  };
  reader.onerror = () => {
    setStatus('Błąd odczytu pliku obrazu. Proszę spróbować ponownie.', 'error');
  };
  reader.readAsDataURL(file);
}

// Discard captured photo
function discardPhoto() {
  capturedImageUrl.value = null;
  photoCaption.value = '';
  statusMessage.value = '';
}

// Upload photo to the backend
async function uploadPhoto() {
  if (!capturedImageUrl.value) return;

  // Get uploader ID from localStorage
  const uploaderId = localStorage.getItem('uploader_id');
  if (!uploaderId) {
    setStatus('Błąd: Nie można zidentyfikować użytkownika. Proszę odświeżyć stronę.', 'error');
    return;
  }

  isUploading.value = true;
  uploadProgress.value = 0;
  setStatus('Przygotowywanie Twojego wspaniałego zdjęcia...', 'info');

  try {
    // Convert base64 to blob
    const blob = await fetch(capturedImageUrl.value).then(res => res.blob());
    
    // Create a new file with caption metadata if provided
    let fileToUpload = blob;
    if (photoCaption.value.trim()) {
      // For now, we'll handle caption separately since the current API doesn't support it
      // In a real implementation, you might want to modify the API to accept captions
      console.log('Photo caption:', photoCaption.value.trim());
    }
    
    setStatus('Dzielenie się wspomnieniem ze wszystkimi...', 'info');
    
    // Send to backend using API client with progress tracking
    await api.uploadPhoto(fileToUpload, uploaderId, (progress) => {
      uploadProgress.value = progress;
      
      // Update message based on progress with more engaging language
      if (progress < 20) {
        uploadMessage.value = 'Przygotowywanie Twojego pięknego zdjęcia...';
      } else if (progress < 40) {
        uploadMessage.value = 'Dodawanie do albumu ślubnego...';
      } else if (progress < 60) {
        uploadMessage.value = 'Prawie gotowe do udostępnienia...';
      } else if (progress < 80) {
        uploadMessage.value = 'Dodawanie ostatecznych poprawek...';
      } else if (progress < 95) {
        uploadMessage.value = 'Prawie gotowe...';
      } else {
        uploadMessage.value = 'Idealne! Wszystko gotowe! ✨';
      }
    });

    // Provide haptic feedback on success
    if ('vibrate' in navigator) {
      navigator.vibrate([100, 50, 100]);
    }

    setStatus('Twoje zdjęcie zostało udostępnione! 🎉✨ Dziękujemy za dodanie do naszych wspomnień!', 'success');
    
    // Clear the captured image and caption after delay
    setTimeout(() => {
      capturedImageUrl.value = null;
      photoCaption.value = '';
      statusMessage.value = '';
    }, 4000);
  } catch (error) {
    console.error('Error uploading photo:', error);
    setStatus('Nie udało się przesłać zdjęcia. Proszę sprawdzić połączenie i spróbować ponownie.', 'error');
  } finally {
    isUploading.value = false;
    uploadProgress.value = 0;
    uploadMessage.value = 'Przesyłanie zdjęcia...';
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
