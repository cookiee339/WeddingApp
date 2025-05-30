<template>
  <div class="photo-gallery">
    <div class="text-center mb-8">
      <h2 class="text-3xl font-display text-wedding-dark mb-3">My Photos</h2>
      <p class="text-lg text-gray-700">
        Photos you've shared during this celebration
      </p>
    </div>

    <!-- Loading indicator -->
    <div v-if="isLoading" class="flex justify-center my-8">
      <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-wedding-primary"></div>
    </div>

    <!-- Debug info (can be removed in production) -->
    <div class="bg-blue-100 text-blue-800 p-2 rounded text-xs mb-4">
      User ID: {{ currentUserId || 'Not set' }}
    </div>

    <!-- Error message -->
    <div v-if="error" class="bg-red-100 text-red-800 p-4 rounded-lg my-4">
      {{ error }}
      <button @click="fetchMyPhotos" class="underline ml-2">Try again</button>
    </div>

    <!-- No photos message -->
    <div v-if="!isLoading && !error && photos.length === 0" class="text-center my-12">
      <p class="text-xl text-gray-500 mb-4">You haven't shared any photos yet</p>
      <router-link to="/" class="btn-primary inline-block">
        Take your first photo
      </router-link>
    </div>

    <!-- Photos grid -->
    <div v-else class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-4">
      <div v-for="photo in photos" :key="photo.photoId" class="photo-card">
        <div class="relative pb-[100%] overflow-hidden bg-gray-100">
          <img 
            :src="photo.imageUrl" 
            :alt="'Photo ' + photo.photoId" 
            class="absolute inset-0 w-full h-full object-cover transition-transform duration-300 hover:scale-105"
            loading="lazy"
            @click="openPhotoModal(photo)"
          />
          <button 
            class="absolute top-2 right-2 bg-black bg-opacity-50 text-white rounded-full p-2"
            @click.stop="deletePhoto(photo.photoId)"
            title="Delete photo"
          >
            <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
            </svg>
          </button>
        </div>
        <div class="p-2 text-xs text-gray-500">
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
    <div v-if="selectedPhoto" class="fixed inset-0 bg-black bg-opacity-80 flex items-center justify-center z-50 p-4" @click="closePhotoModal">
      <div class="max-w-3xl max-h-[90vh] relative" @click.stop>
        <img 
          :src="selectedPhoto.imageUrl" 
          :alt="'Photo ' + selectedPhoto.photoId" 
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
          {{ formatDate(selectedPhoto.uploadedAt) }}
        </div>
      </div>
    </div>

    <!-- Confirmation Dialog -->
    <div v-if="showDeleteConfirm" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
      <div class="bg-white rounded-lg p-6 max-w-sm w-full">
        <h3 class="text-xl font-bold mb-4">Delete Photo?</h3>
        <p class="mb-6">Are you sure you want to delete this photo? This action cannot be undone.</p>
        <div class="flex justify-end space-x-3">
          <button @click="showDeleteConfirm = false" class="px-4 py-2 border rounded-lg">
            Cancel
          </button>
          <button 
            @click="confirmDelete" 
            class="px-4 py-2 bg-red-600 text-white rounded-lg"
            :disabled="isDeleting"
          >
            {{ isDeleting ? 'Deleting...' : 'Delete' }}
          </button>
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
const showDeleteConfirm = ref(false);
const isDeleting = ref(false);
const photoIdToDelete = ref(null);
const currentUserId = ref('');

// Fetch current user's photos from the backend
async function fetchMyPhotos(loadMore = false) {
  if (!loadMore) {
    isLoading.value = true;
    page.value = 1;
    photos.value = [];
  }
  
  const uploaderId = localStorage.getItem('uploader_id');
  if (!uploaderId) {
    error.value = 'User identifier not found. Please refresh the page.';
    isLoading.value = false;
    return;
  }
  
  try {
    const photoData = await api.getPhotos({
      uploaderId: uploaderId,
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
  fetchMyPhotos(true);
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

// Delete photo methods
function deletePhoto(photoId) {
  photoIdToDelete.value = photoId;
  showDeleteConfirm.value = true;
}

async function confirmDelete() {
  if (!photoIdToDelete.value) return;
  
  isDeleting.value = true;
  
  const uploaderId = localStorage.getItem('uploader_id');
  console.log('Attempting to delete photo:', {
    photoId: photoIdToDelete.value,
    uploaderId: uploaderId
  });
  
  try {
    const response = await api.deletePhoto(
      photoIdToDelete.value,
      uploaderId
    );
    
    console.log('Delete response:', response);
    
    // Remove photo from the list
    photos.value = photos.value.filter(photo => photo.photoId !== photoIdToDelete.value);
    
    // Close dialog
    showDeleteConfirm.value = false;
    photoIdToDelete.value = null;
    
    // If we were viewing this photo in the modal, close it
    if (selectedPhoto.value && selectedPhoto.value.photoId === photoIdToDelete.value) {
      closePhotoModal();
    }
    
    console.log('Photo deleted successfully');
  } catch (err) {
    console.error('Error deleting photo:', err);
    console.error('Error details:', {
      message: err.message,
      response: err.response?.data,
      status: err.response?.status
    });
    error.value = `Failed to delete photo: ${err.response?.data?.error || err.message}`;
  } finally {
    isDeleting.value = false;
  }
}

// Fetch photos when component mounts
onMounted(() => {
  currentUserId.value = localStorage.getItem('uploader_id') || '';
  fetchMyPhotos();
});
</script>