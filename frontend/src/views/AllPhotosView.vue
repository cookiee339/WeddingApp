<template>
  <div class="photo-gallery">
    <div class="text-center mb-8">
      <h2 class="text-3xl font-display text-wedding-dark mb-3">All Wedding Photos</h2>
      <p class="text-lg text-gray-700">
        Explore all the beautiful moments captured during this celebration
      </p>
    </div>

    <!-- Loading indicator -->
    <div v-if="isLoading" class="flex justify-center my-8">
      <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-wedding-primary"></div>
    </div>

    <!-- Error message -->
    <div v-else-if="error" class="bg-red-100 text-red-800 p-4 rounded-lg my-4">
      {{ error }}
      <button @click="fetchPhotos" class="underline ml-2">Try again</button>
    </div>

    <!-- No photos message -->
    <div v-else-if="photos.length === 0" class="text-center my-12">
      <p class="text-xl text-gray-500 mb-4">No photos have been shared yet</p>
      <router-link to="/" class="btn-primary inline-block">
        Be the first to share a photo
      </router-link>
    </div>

    <!-- Photos grid -->
    <div v-else class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-4">
      <div v-for="photo in photos" :key="photo.photo_id" class="photo-card">
        <div class="relative pb-[100%] overflow-hidden bg-gray-100">
          <img 
            :src="photo.image_url" 
            :alt="'Photo ' + photo.photo_id" 
            class="absolute inset-0 w-full h-full object-cover transition-transform duration-300 hover:scale-105"
            loading="lazy"
            @click="openPhotoModal(photo)"
          />
        </div>
        <div class="p-2 text-xs text-gray-500">
          {{ formatDate(photo.uploaded_at) }}
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
    <div v-if="selectedPhoto" class="fixed inset-0 bg-black bg-opacity-80 flex items-center justify-center z-50 p-4" @click="closePhotoModal">
      <div class="max-w-3xl max-h-[90vh] relative" @click.stop>
        <img 
          :src="selectedPhoto.image_url" 
          :alt="'Photo ' + selectedPhoto.photo_id" 
          class="max-h-[90vh] max-w-full object-contain rounded-lg"
        />
        <button 
          class="absolute top-2 right-2 bg-black bg-opacity-50 text-white rounded-full p-2"
          @click="closePhotoModal"
        >
          <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
          </svg>
        </button>
        <div class="absolute bottom-2 left-2 right-2 bg-black bg-opacity-50 text-white p-2 rounded">
          {{ formatDate(selectedPhoto.uploaded_at) }}
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