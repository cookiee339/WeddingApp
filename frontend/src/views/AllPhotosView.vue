<template>
  <div class="photo-gallery">
    <div class="text-center mb-6 md:mb-8">
      <h2 class="text-2xl md:text-3xl font-semibold mb-4 text-boho-brown-dark">💕 Wspólne Wspomnienia</h2>
      <p class="text-sm md:text-base opacity-70 text-boho-brown mb-4">
        Piękne chwile z uroczystości Barbary i Mikołaja
      </p>
      <!-- Photo count and refresh button -->
      <div class="flex items-center justify-center space-x-4 text-sm text-boho-brown">
        <div class="flex items-center space-x-1">
          <span>📸</span>
          <span>{{ photos.length }} {{ photos.length === 1 ? 'zdjęcie' : 'zdjęć' }}</span>
        </div>
        <button @click="refreshPhotos" class="flex items-center space-x-1 text-boho-dusty-rose hover:opacity-80 transition-opacity touch-manipulation">
          <svg class="h-4 w-4" viewBox="0 0 20 20" fill="currentColor">
            <!-- Organic refresh with flowing petals -->
            <path d="M10 2c-2 0-3.5 0.8-4.5 2s-1.5 2.8-1.5 4.5c0 1.7 0.5 3.5 1.5 4.5s2.5 2 4.5 2c2 0 3.5-0.8 4.5-2" stroke="currentColor" stroke-width="1.2" fill="none" stroke-linecap="round"/>
            <path d="M14 6c0.5-0.5 1-0.8 1.5-0.8s1 0.3 1.5 0.8" stroke="currentColor" stroke-width="1.2" fill="none" stroke-linecap="round"/>
            <!-- Small organic arrow -->
            <ellipse cx="15.5" cy="5" rx="0.8" ry="1.2" transform="rotate(45 15.5 5)" fill="currentColor" opacity="0.8"/>
            <!-- Center dot -->
            <circle cx="10" cy="10" r="1" fill="currentColor" opacity="0.6"/>
          </svg>
          <span>Odśwież</span>
        </button>
      </div>
    </div>

    <!-- Loading indicator -->
    <div v-if="isLoading" class="flex justify-center my-8">
      <div class="boho-gallery-spinner h-12 w-12">
        <svg class="w-full h-full" viewBox="0 0 40 40">
          <!-- Organic flower-like spinner -->
          <g class="boho-spinner-petals" transform-origin="20 20">
            <ellipse cx="20" cy="6" rx="1.5" ry="4" fill="#D8A0A0" opacity="0.9"/>
            <ellipse cx="20" cy="6" rx="1.5" ry="4" fill="#D8A0A0" opacity="0.7" transform="rotate(45 20 20)"/>
            <ellipse cx="20" cy="6" rx="1.5" ry="4" fill="#D8A0A0" opacity="0.5" transform="rotate(90 20 20)"/>
            <ellipse cx="20" cy="6" rx="1.5" ry="4" fill="#D8A0A0" opacity="0.3" transform="rotate(135 20 20)"/>
            <ellipse cx="20" cy="6" rx="1.5" ry="4" fill="#D8A0A0" opacity="0.2" transform="rotate(180 20 20)"/>
            <ellipse cx="20" cy="6" rx="1.5" ry="4" fill="#D8A0A0" opacity="0.1" transform="rotate(225 20 20)"/>
            <ellipse cx="20" cy="6" rx="1.5" ry="4" fill="#D8A0A0" opacity="0.1" transform="rotate(270 20 20)"/>
            <ellipse cx="20" cy="6" rx="1.5" ry="4" fill="#D8A0A0" opacity="0.2" transform="rotate(315 20 20)"/>
          </g>
          <circle cx="20" cy="20" r="2" fill="#D8A0A0" opacity="0.6"/>
        </svg>
      </div>
    </div>

    <!-- Error message -->
    <div v-else-if="error" class="status-error my-4">
      {{ error }}
      <button @click="fetchPhotos" class="underline ml-2 hover:opacity-80">Spróbuj ponownie</button>
    </div>

    <!-- No photos message -->
    <div v-else-if="photos.length === 0" class="text-center my-16 px-4">
      <div class="mb-6">
        <div class="text-6xl mb-4">📷</div>
        <p class="text-xl text-boho-brown opacity-70 mb-2">Nie ma jeszcze wspólnych wspomnień</p>
        <p class="text-sm text-boho-brown opacity-60 mb-6">Bądź pierwszy, który uwieczni ten wyjątkowy moment!</p>
      </div>
      <router-link to="/" class="btn-primary inline-block py-4 px-8 text-lg rounded-2xl shadow-lg transform active:scale-95 transition-all touch-manipulation">
        📸 Udostępnij Pierwsze Zdjęcie
      </router-link>
    </div>

    <!-- Photos grid -->
    <div v-else class="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-4 gap-2 sm:gap-4">
      <div v-for="photo in photos" :key="photo.photoId" class="photo-card bg-white rounded-xl shadow-md overflow-hidden transform transition-all duration-300 hover:scale-105 active:scale-95 touch-manipulation">
        <div class="aspect-square relative overflow-hidden cursor-pointer group" @click="openPhotoModal(photo)">
          <img 
            :src="photo.imageUrl" 
            :alt="'Zdjęcie ślubne ' + photo.photoId"
            class="w-full h-full object-cover transition-transform duration-500 group-hover:scale-110"
            loading="lazy"
          />
          <!-- Gradient overlay for better text readability -->
          <div class="absolute inset-0 bg-gradient-to-t from-black/50 via-transparent to-transparent opacity-0 group-hover:opacity-100 transition-opacity duration-300"></div>
          
          <!-- Upload time -->
          <div class="absolute bottom-2 left-2 right-2 text-center">
            <div class="bg-white/80 backdrop-blur-sm rounded-full px-2 py-1 text-xs text-gray-700 inline-block">
              {{ formatDate(photo.uploadedAt) }}
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Load more button -->
    <div v-if="hasMorePhotos && !isLoading" class="flex justify-center mt-8">
      <button @click="loadMorePhotos" class="bg-white border-2 border-boho-dusty-rose text-boho-dusty-rose py-3 px-8 rounded-xl font-semibold transition-all transform active:scale-95 touch-manipulation hover:bg-boho-dusty-rose hover:text-white">
        📸 Załaduj Więcej Wspomnień
      </button>
    </div>

    <!-- Enhanced Photo Modal -->
    <div v-if="selectedPhoto" class="modal-overlay" @click="closePhotoModal">
      <div 
        class="w-full h-full relative flex items-center justify-center" 
        @click.stop
        @touchstart="handleTouchStart"
        @touchend="handleTouchEnd"
      >
        <!-- Photo display -->
        <div class="relative max-w-full max-h-full flex items-center justify-center">
          <img 
            :src="selectedPhoto.imageUrl" 
            :alt="'Zdjęcie ślubne ' + selectedPhoto.photoId"
            class="max-h-[85vh] max-w-[95vw] object-contain rounded-lg shadow-2xl"
          />
          
          <!-- Navigation arrows for desktop -->
          <button 
            v-if="currentPhotoIndex > 0"
            @click="previousPhoto"
            class="absolute left-4 top-1/2 transform -translate-y-1/2 bg-black/50 hover:bg-black/70 text-white rounded-full p-3 transition-all touch-manipulation hidden sm:block"
          >
            <svg class="h-6 w-6" viewBox="0 0 24 24" fill="currentColor">
              <!-- Organic left arrow with flowing curves -->
              <path d="M16 6c-1.5 1.5-3 3-4.5 4.5s-3 3-4.5 4.5" stroke="currentColor" stroke-width="1.8" fill="none" stroke-linecap="round"/>
              <path d="M9 10c-0.8 0.8-1.2 1.8-1.2 3s0.4 2.2 1.2 3" stroke="currentColor" stroke-width="1.8" fill="none" stroke-linecap="round"/>
              <!-- Organic arrow head -->
              <ellipse cx="8" cy="12" rx="1.2" ry="1.8" transform="rotate(-45 8 12)" fill="currentColor" opacity="0.9"/>
            </svg>
          </button>
          
          <button 
            v-if="currentPhotoIndex < photos.length - 1"
            @click="nextPhoto"
            class="absolute right-4 top-1/2 transform -translate-y-1/2 bg-black/50 hover:bg-black/70 text-white rounded-full p-3 transition-all touch-manipulation hidden sm:block"
          >
            <svg class="h-6 w-6" viewBox="0 0 24 24" fill="currentColor">
              <!-- Organic right arrow with flowing curves -->
              <path d="M8 6c1.5 1.5 3 3 4.5 4.5s3 3 4.5 4.5" stroke="currentColor" stroke-width="1.8" fill="none" stroke-linecap="round"/>
              <path d="M15 10c0.8 0.8 1.2 1.8 1.2 3s-0.4 2.2-1.2 3" stroke="currentColor" stroke-width="1.8" fill="none" stroke-linecap="round"/>
              <!-- Organic arrow head -->
              <ellipse cx="16" cy="12" rx="1.2" ry="1.8" transform="rotate(45 16 12)" fill="currentColor" opacity="0.9"/>
            </svg>
          </button>
        </div>
        
        <!-- Top controls -->
        <div class="absolute top-4 left-4 right-4 flex justify-between items-start z-10">
          <!-- Photo counter -->
          <div class="bg-black/50 backdrop-blur-sm text-white px-3 py-2 rounded-full text-sm font-medium">
            {{ currentPhotoIndex + 1 }} z {{ photos.length }}
          </div>
          
          <!-- Close button -->
          <button 
            @click="closePhotoModal"
            class="bg-black/50 hover:bg-black/70 text-white rounded-full p-3 transition-all touch-manipulation"
          >
            <svg class="h-6 w-6" viewBox="0 0 24 24" fill="currentColor">
              <path d="M12 2c-1.5 0-2.8 0.3-4 1.2-1.2 0.9-2 2.1-2.5 3.8-0.3 1-0.3 2 0 3 0.5 1.7 1.3 2.9 2.5 3.8 1.2 0.9 2.5 1.2 4 1.2s2.8-0.3 4-1.2c1.2-0.9 2-2.1 2.5-3.8 0.3-1 0.3-2 0-3-0.5-1.7-1.3-2.9-2.5-3.8-1.2-0.9-2.5-1.2-4-1.2z" opacity="0.1"/>
              <path d="M8.5 8.5c0.3-0.3 0.7-0.3 1 0l2.5 2.5 2.5-2.5c0.3-0.3 0.7-0.3 1 0s0.3 0.7 0 1l-2.5 2.5 2.5 2.5c0.3 0.3 0.3 0.7 0 1s-0.7 0.3-1 0l-2.5-2.5-2.5 2.5c-0.3 0.3-0.7 0.3-1 0s-0.3-0.7 0-1l2.5-2.5-2.5-2.5c-0.3-0.3-0.3-0.7 0-1z" stroke="currentColor" stroke-width="1.5" fill="none" stroke-linecap="round"/>
            </svg>
          </button>
        </div>
        
        <!-- Actions -->
        <div class="absolute bottom-4 left-4 right-4 z-10">
          <!-- Action buttons -->
          <div class="flex justify-center space-x-3">
            <!-- Share button -->
            <button 
              @click="sharePhoto(selectedPhoto)"
              class="bg-white/20 hover:bg-white/30 backdrop-blur-sm text-white rounded-full p-4 transition-all transform active:scale-95 touch-manipulation"
            >
              <svg class="h-6 w-6" viewBox="0 0 24 24" fill="currentColor">
                <!-- Organic share with flowing connections -->
                <circle cx="6" cy="12" r="2.5" stroke="currentColor" stroke-width="1.2" fill="none" opacity="0.8"/>
                <circle cx="18" cy="8" r="2.5" stroke="currentColor" stroke-width="1.2" fill="none" opacity="0.8"/>
                <circle cx="18" cy="16" r="2.5" stroke="currentColor" stroke-width="1.2" fill="none" opacity="0.8"/>
                <!-- Flowing connection lines -->
                <path d="M8 11c2 0 4-1 6-2s4-1 6-1" stroke="currentColor" stroke-width="1.3" fill="none" stroke-linecap="round" opacity="0.9"/>
                <path d="M8 13c2 0 4 1 6 2s4 1 6 1" stroke="currentColor" stroke-width="1.3" fill="none" stroke-linecap="round" opacity="0.9"/>
                <!-- Small heart accents -->
                <ellipse cx="12" cy="9" rx="0.8" ry="1" fill="currentColor" opacity="0.6"/>
                <ellipse cx="12" cy="15" rx="0.8" ry="1" fill="currentColor" opacity="0.6"/>
              </svg>
            </button>
            
            <!-- Download button -->
            <button 
              @click="downloadPhoto(selectedPhoto)"
              class="bg-white/20 hover:bg-white/30 backdrop-blur-sm text-white rounded-full p-4 transition-all transform active:scale-95 touch-manipulation"
            >
              <svg class="h-6 w-6" viewBox="0 0 24 24" fill="currentColor">
                <!-- Organic document with flowing download arrow -->
                <path d="M7 4c0-1 0.5-1.8 1.2-2.3C8.9 1.2 9.7 1 10.5 1h3c0.8 0 1.6 0.2 2.3 0.7s1.2 1.3 1.2 2.3v15c0 1-0.5 1.8-1.2 2.3s-1.5 0.7-2.3 0.7h-6c-0.8 0-1.6-0.2-2.3-0.7S5 19.8 5 19V6c0-1 0.5-1.8 1.2-2.3S7.2 3 8 3" stroke="currentColor" stroke-width="1.3" fill="none" stroke-linecap="round"/>
                <!-- Flowing downward arrow -->
                <path d="M12 7c0 1.5 0 3 0 4.5" stroke="currentColor" stroke-width="1.5" fill="none" stroke-linecap="round"/>
                <path d="M10 10c0.5 0.8 1.2 1.2 2 1.2s1.5-0.4 2-1.2" stroke="currentColor" stroke-width="1.5" fill="none" stroke-linecap="round"/>
                <!-- Organic heart at arrow tip -->
                <ellipse cx="12" cy="12.5" rx="1" ry="1.2" fill="currentColor" opacity="0.8"/>
                <!-- Document fold -->
                <path d="M14 3c0.5 0.5 1 1 1.5 1.5S16.5 5.5 17 6" stroke="currentColor" stroke-width="1.2" fill="none" stroke-linecap="round" opacity="0.7"/>
              </svg>
            </button>
          </div>
          
          <!-- Mobile navigation hints -->
          <div class="text-center mt-3 sm:hidden">
            <div class="text-white/70 text-xs">
              👈 Przesuń, aby przechodzić między zdjęciami 👉
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import api from '../api';

const photos = ref([]);
const isLoading = ref(false);
const error = ref('');
const page = ref(1);
const limit = 12; // Photos per page
const hasMorePhotos = ref(true);
const selectedPhoto = ref(null);
const currentPhotoIndex = ref(0);
const touchStartX = ref(0);
const touchEndX = ref(0);

// Fetch photos from the backend
async function fetchPhotos(loadMore = false) {
  if (!loadMore) {
    isLoading.value = true;
    page.value = 1;
    photos.value = [];
  }
  
  try {
    const photoData = await api.getPhotos({
      page: page.value,
      limit: limit
    });
    
    // Add wedding-specific properties to each photo
    const enhancedPhotos = photoData.map(photo => ({
      ...photo,
      isLiked: false, // This would come from user's liked photos in a real app
      likeCount: Math.floor(Math.random() * 15) + 1 // Simulate likes for demo
    }));
    
    if (loadMore) {
      photos.value = [...photos.value, ...enhancedPhotos];
    } else {
      photos.value = enhancedPhotos;
    }
    
    // Check if we've reached the end
    hasMorePhotos.value = photoData.length === limit;
    error.value = '';
  } catch (err) {
    console.error('Error fetching photos:', err);
    error.value = 'Nie udało się załadować zdjęć. Proszę spróbować ponownie później.';
  } finally {
    isLoading.value = false;
  }
}

// Refresh photos
async function refreshPhotos() {
  await fetchPhotos();
}

// Load more photos
function loadMorePhotos() {
  page.value++;
  fetchPhotos(true);
}

// Format the date for display
function formatDate(dateString) {
  if (!dateString) return '';
  
  const date = new Date(dateString);
  const now = new Date();
  const diffInMinutes = Math.floor((now - date) / (1000 * 60));
  
  if (diffInMinutes < 1) return 'teraz';
  if (diffInMinutes < 60) return `${diffInMinutes}m temu`;
  if (diffInMinutes < 1440) return `${Math.floor(diffInMinutes / 60)}h temu`;
  
  return new Intl.DateTimeFormat('pl-PL', {
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  }).format(date);
}

// Format the date for modal display
function formatDateLong(dateString) {
  if (!dateString) return '';
  
  const date = new Date(dateString);
  return new Intl.DateTimeFormat('pl-PL', {
    weekday: 'long',
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  }).format(date);
}

// Photo modal methods
function openPhotoModal(photo) {
  selectedPhoto.value = photo;
  currentPhotoIndex.value = photos.value.findIndex(p => p.photoId === photo.photoId);
  // Prevent body scrolling when modal is open
  document.body.style.overflow = 'hidden';
}

function closePhotoModal() {
  selectedPhoto.value = null;
  // Restore body scrolling
  document.body.style.overflow = '';
}

// Navigation methods
function previousPhoto() {
  if (currentPhotoIndex.value > 0) {
    currentPhotoIndex.value--;
    selectedPhoto.value = photos.value[currentPhotoIndex.value];
  }
}

function nextPhoto() {
  if (currentPhotoIndex.value < photos.value.length - 1) {
    currentPhotoIndex.value++;
    selectedPhoto.value = photos.value[currentPhotoIndex.value];
  }
}

// Touch handlers for swipe navigation
function handleTouchStart(event) {
  touchStartX.value = event.touches[0].clientX;
}

function handleTouchEnd(event) {
  touchEndX.value = event.changedTouches[0].clientX;
  handleSwipe();
}

function handleSwipe() {
  const swipeThreshold = 50;
  const swipeDistance = touchStartX.value - touchEndX.value;
  
  if (Math.abs(swipeDistance) > swipeThreshold) {
    if (swipeDistance > 0) {
      // Swipe left - next photo
      nextPhoto();
    } else {
      // Swipe right - previous photo
      previousPhoto();
    }
  }
}

async function sharePhoto(photo) {
  if (navigator.share) {
    try {
      await navigator.share({
        title: 'Zdjęcie Ślubne - Barbara i Mikołaj',
        text: 'Spójrz na ten piękny moment ze ślubu!',
        url: photo.imageUrl
      });
    } catch (error) {
      console.log('Udostępnianie anulowane lub nieudane:', error);
    }
  } else {
    // Fallback: copy to clipboard
    try {
      await navigator.clipboard.writeText(photo.imageUrl);
      alert('Link do zdjęcia skopiowany do schowka! 📋');
    } catch (error) {
      console.error('Nie udało się skopiować linku:', error);
      alert('Udostępnianie nie jest obsługiwane na tym urządzeniu 😔');
    }
  }
}

async function downloadPhoto(photo) {
  try {
    const response = await fetch(photo.imageUrl);
    const blob = await response.blob();
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = `wedding-photo-${photo.photoId}.jpg`;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    window.URL.revokeObjectURL(url);
    
    // Provide feedback
    if ('vibrate' in navigator) {
      navigator.vibrate([100, 50, 100]);
    }
  } catch (error) {
    console.error('Pobieranie nieudane:', error);
    alert('Pobieranie nieudane. Proszę spróbować ponownie.');
  }
}

// Fetch photos when component mounts
onMounted(() => {
  fetchPhotos();
});
</script>