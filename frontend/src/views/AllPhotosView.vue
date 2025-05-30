<template>
  <div class="photo-gallery">
    <div class="text-center mb-8 md:mb-12">
      <h2 class="text-2xl md:text-3xl font-semibold mb-6 text-boho-brown-dark">Shared Memories</h2>
      <p class="text-sm md:text-base opacity-70 text-boho-brown">
        Explore all the beautiful moments captured during this celebration
      </p>
    </div>

    <!-- Loading indicator -->
    <div v-if="isLoading" class="flex justify-center my-8">
      <div class="loading-spinner h-12 w-12"></div>
    </div>

    <!-- Error message -->
    <div v-else-if="error" class="status-error my-4">
      {{ error }}
      <button @click="fetchPhotos" class="underline ml-2 hover:opacity-80">Try again</button>
    </div>

    <!-- No photos message -->
    <div v-else-if="photos.length === 0" class="text-center my-12">
      <p class="text-xl text-boho-brown opacity-70 mb-4">No photos have been shared yet</p>
      <router-link to="/" class="btn-primary inline-block">
        Be the first to share a photo
      </router-link>
    </div>

    <!-- Photos grid -->
    <div v-else class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4 md:gap-6">
      <div v-for="photo in photos" :key="photo.photoId" class="photo-card p-3 md:p-4">
        <div class="w-full h-4/5 gallery-placeholder rounded-lg mb-3 overflow-hidden cursor-pointer">
          <img 
            :src="photo.imageUrl" 
            :alt="'Photo ' + photo.photoId" 
            class="w-full h-full object-cover transition-transform duration-300 hover:scale-105"
            loading="lazy"
            @click="openPhotoModal(photo)"
          />
        </div>
        <div class="text-sm text-center opacity-70 text-boho-brown truncate">
          {{ formatDate(photo.uploadedAt) }}
        </div>
      </div>
    </div>

    <!-- Load more button -->
    <div v-if="hasMorePhotos && !isLoading" class="flex justify-center mt-8">
      <button @click="loadMorePhotos" class="btn-secondary">
        Load More Photos
      </button>
    </div>

    <!-- Photo Modal -->
    <div v-if="selectedPhoto" class="modal-overlay" @click="closePhotoModal">
      <div class="max-w-3xl max-h-[90vh] relative" @click.stop>
        <img 
          :src="selectedPhoto.imageUrl" 
          :alt="'Photo ' + selectedPhoto.photoId" 
          class="max-h-[90vh] max-w-full object-contain rounded-xl"
        />
        <button 
          class="absolute top-2 right-2 bg-black bg-opacity-50 text-white rounded-full p-2 hover:bg-opacity-70 transition-all"
          @click="closePhotoModal"
        >
          <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
          </svg>
        </button>
        <div class="absolute bottom-2 left-2 right-2 bg-black bg-opacity-50 text-white p-2 rounded">
          {{ formatDate(selectedPhoto.uploadedAt) }}
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
    
    if (loadMore) {
      photos.value = [...photos.value, ...photoData];
    } else {
      photos.value = photoData;
    }
    
    // Check if we've reached the end
    hasMorePhotos.value = photoData.length === limit;
    error.value = '';
  } catch (err) {
    console.error('Error fetching photos:', err);
    error.value = 'Failed to load photos. Please try again later.';
  } finally {
    isLoading.value = false;
  }
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
  return new Intl.DateTimeFormat('en-US', {
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  }).format(date);
}

// Photo modal methods
function openPhotoModal(photo) {
  selectedPhoto.value = photo;
  // Prevent body scrolling when modal is open
  document.body.style.overflow = 'hidden';
}

function closePhotoModal() {
  selectedPhoto.value = null;
  // Restore body scrolling
  document.body.style.overflow = '';
}

// Fetch photos when component mounts
onMounted(() => {
  fetchPhotos();
});
</script>