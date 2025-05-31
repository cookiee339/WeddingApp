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
          <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
          </svg>
          <span>Odśwież</span>
        </button>
      </div>
    </div>

    <!-- Loading indicator -->
    <div v-if="isLoading" class="flex justify-center my-8">
      <div class="loading-spinner h-12 w-12"></div>
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
            <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7" />
            </svg>
          </button>
          
          <button 
            v-if="currentPhotoIndex < photos.length - 1"
            @click="nextPhoto"
            class="absolute right-4 top-1/2 transform -translate-y-1/2 bg-black/50 hover:bg-black/70 text-white rounded-full p-3 transition-all touch-manipulation hidden sm:block"
          >
            <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7" />
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
            <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
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
              <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8.684 13.342C8.886 12.938 9 12.482 9 12c0-.482-.114-.938-.316-1.342m0 2.684a3 3 0 110-2.684m0 2.684l6.632 3.316m-6.632-6l6.632-3.316m0 0a3 3 0 105.367-2.684 3 3 0 00-5.367 2.684zm0 9.316a3 3 0 105.367 2.684 3 3 0 00-5.367-2.684z" />
              </svg>
            </button>
            
            <!-- Download button -->
            <button 
              @click="downloadPhoto(selectedPhoto)"
              class="bg-white/20 hover:bg-white/30 backdrop-blur-sm text-white rounded-full p-4 transition-all transform active:scale-95 touch-manipulation"
            >
              <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 10v6m0 0l-3-3m3 3l3-3m2 8H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
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