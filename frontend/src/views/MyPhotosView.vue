<template>
  <div class="photo-gallery">
    <div class="text-center mb-8 md:mb-12">
      <h2 class="text-2xl md:text-3xl font-semibold mb-6 text-boho-brown-dark">Moje Zdjęcia</h2>
      <p class="text-sm md:text-base opacity-70 text-boho-brown">
        Zdjęcia, które udostępniłeś podczas ślubu Barbary i Mikołaja
      </p>
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
    <div v-if="error" class="status-error my-4">
      {{ error }}
      <button @click="fetchMyPhotos" class="underline ml-2 hover:opacity-80">Spróbuj ponownie</button>
    </div>

    <!-- No photos message -->
    <div v-if="!isLoading && !error && photos.length === 0" class="text-center my-12">
      <p class="text-xl text-boho-brown opacity-70 mb-4">Nie udostępniłeś jeszcze żadnych zdjęć</p>
      <router-link to="/" class="btn-primary inline-block">
        Zrób swoje pierwsze zdjęcie
      </router-link>
    </div>

    <!-- Photos grid -->
    <div v-else class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4 md:gap-6">
      <div v-for="photo in photos" :key="photo.photoId" class="photo-card p-3 md:p-4">
        <div class="w-full h-4/5 gallery-placeholder rounded-lg mb-3 overflow-hidden cursor-pointer relative">
          <img 
            :src="photo.imageUrl" 
            :alt="'Zdjęcie ' + photo.photoId"
            class="w-full h-full object-cover transition-transform duration-300 hover:scale-105"
            loading="lazy"
            @click="openPhotoModal(photo)"
          />
          <button 
            class="absolute top-2 right-2 bg-black bg-opacity-50 text-white rounded-full p-2 hover:bg-opacity-70 transition-all"
            @click.stop="deletePhoto(photo.photoId)"
            title="Usuń zdjęcie"
          >
            <svg class="h-4 w-4" viewBox="0 0 20 20" fill="currentColor">
              <!-- Organic basket/container -->
              <path d="M4 6.5c0-0.4 0.2-0.8 0.6-1C5 5.3 5.4 5.2 6 5.2h8c0.6 0 1 0.1 1.4 0.3s0.6 0.6 0.6 1l-0.8 9c-0.1 0.8-0.5 1.4-1 1.8s-1.2 0.6-1.8 0.6H8.6c-0.6 0-1.3-0.2-1.8-0.6s-0.9-1-1-1.8L4 6.5z" stroke="currentColor" stroke-width="1.1" fill="none" stroke-linecap="round"/>
              <!-- Organic lid/top -->
              <path d="M3.5 6c0-0.2 0.1-0.4 0.4-0.4h12.2c0.3 0 0.4 0.2 0.4 0.4s-0.1 0.4-0.4 0.4H3.9c-0.3 0-0.4-0.2-0.4-0.4z" stroke="currentColor" stroke-width="1.2" fill="none"/>
              <!-- Handle/grip - flowing lines -->
              <path d="M8 4.2c0-0.8 0.4-1.4 1-1.8C9.5 2 10 1.8 10.6 1.8h1.8c0.6 0 1.1 0.2 1.6 0.6s0.8 1 0.8 1.8" stroke="currentColor" stroke-width="1.1" fill="none" stroke-linecap="round"/>
              <!-- Content lines - organic -->
              <path d="M8.5 9c0 0.4 0 2 0 3.2s0 2 0 2.4" stroke="currentColor" stroke-width="1" fill="none" stroke-linecap="round" opacity="0.7"/>
              <path d="M11.5 9c0 0.4 0 2 0 3.2s0 2 0 2.4" stroke="currentColor" stroke-width="1" fill="none" stroke-linecap="round" opacity="0.7"/>
            </svg>
          </button>
        </div>
        <div class="text-sm text-center opacity-70 text-boho-brown truncate">
          {{ formatDate(photo.uploadedAt) }}
        </div>
      </div>
    </div>

    <!-- Load more button -->
    <div v-if="hasMorePhotos && !isLoading" class="flex justify-center mt-8">
      <button @click="loadMorePhotos" class="btn-secondary">
        Załaduj Więcej Zdjęć
      </button>
    </div>

    <!-- Photo Modal -->
    <div v-if="selectedPhoto" class="modal-overlay" @click="closePhotoModal">
      <div class="max-w-3xl max-h-[90vh] relative" @click.stop>
        <img 
          :src="selectedPhoto.imageUrl" 
          :alt="'Zdjęcie ' + selectedPhoto.photoId"
          class="max-h-[90vh] max-w-full object-contain rounded-xl"
        />
        <button 
          class="absolute top-2 right-2 bg-black bg-opacity-50 text-white rounded-full p-2 hover:bg-opacity-70 transition-all"
          @click="closePhotoModal"
        >
          <svg class="h-6 w-6" viewBox="0 0 24 24" fill="currentColor">
            <path d="M12 2c-1.5 0-2.8 0.3-4 1.2-1.2 0.9-2 2.1-2.5 3.8-0.3 1-0.3 2 0 3 0.5 1.7 1.3 2.9 2.5 3.8 1.2 0.9 2.5 1.2 4 1.2s2.8-0.3 4-1.2c1.2-0.9 2-2.1 2.5-3.8 0.3-1 0.3-2 0-3-0.5-1.7-1.3-2.9-2.5-3.8-1.2-0.9-2.5-1.2-4-1.2z" opacity="0.1"/>
            <path d="M8.5 8.5c0.3-0.3 0.7-0.3 1 0l2.5 2.5 2.5-2.5c0.3-0.3 0.7-0.3 1 0s0.3 0.7 0 1l-2.5 2.5 2.5 2.5c0.3 0.3 0.3 0.7 0 1s-0.7 0.3-1 0l-2.5-2.5-2.5 2.5c-0.3 0.3-0.7 0.3-1 0s-0.3-0.7 0-1l2.5-2.5-2.5-2.5c-0.3-0.3-0.3-0.7 0-1z" stroke="currentColor" stroke-width="1.5" fill="none" stroke-linecap="round"/>
          </svg>
        </button>
        <div class="absolute bottom-2 left-2 right-2 bg-black bg-opacity-50 text-white p-2 rounded">
          {{ formatDate(selectedPhoto.uploadedAt) }}
        </div>
      </div>
    </div>

    <!-- Confirmation Dialog -->
    <div v-if="showDeleteConfirm" class="modal-overlay">
      <div class="bg-boho-offwhite rounded-xl p-6 max-w-sm w-full shadow-xl">
        <h3 class="text-xl font-bold mb-4 text-boho-brown-dark">Usunąć Zdjęcie?</h3>
        <p class="mb-6 text-boho-brown">Czy na pewno chcesz usunąć to zdjęcie? Ta czynność nie może zostać cofnięta.</p>
        <div class="flex justify-end space-x-3">
          <button @click="showDeleteConfirm = false" class="px-4 py-2 border border-boho-rose-border rounded-lg text-boho-brown hover:bg-boho-rose-light transition-colors">
            Anuluj
          </button>
          <button 
            @click="confirmDelete" 
            class="px-4 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700 transition-colors"
            :disabled="isDeleting"
          >
            {{ isDeleting ? 'Usuwanie...' : 'Usuń' }}
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
    error.value = 'Identyfikator użytkownika nie został znaleziony. Proszę odświeżyć stronę.';
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
    error.value = 'Nie udało się załadować zdjęć. Proszę spróbować ponownie później.';
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
  return new Intl.DateTimeFormat('pl-PL', {
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
    error.value = `Nie udało się usunąć zdjęcia: ${err.response?.data?.error || err.message}`;
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

<style scoped>
/* Boho-style spinner animations */
.boho-gallery-spinner .boho-spinner-petals {
  animation: boho-spin 2s linear infinite;
}

@keyframes boho-spin {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

/* Organic transitions for buttons */
.touch-manipulation {
  -webkit-tap-highlight-color: transparent;
  touch-action: manipulation;
}

/* Enhanced organic hover effects */
.photo-card {
  transition: transform 0.3s cubic-bezier(0.4, 0, 0.2, 1),
              box-shadow 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.photo-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(139, 117, 102, 0.15);
}

/* Organic modal transitions */
.modal-overlay {
  backdrop-filter: blur(4px);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

/* Soft organic shadows */
.shadow-xl {
  box-shadow: 0 12px 30px rgba(139, 117, 102, 0.2), 
              0 6px 15px rgba(139, 117, 102, 0.1);
}

/* Button hover effects */
.btn-primary:hover,
.btn-secondary:hover {
  transform: translateY(-1px);
  box-shadow: 0 6px 20px rgba(216, 160, 160, 0.3);
}

/* Organic delete button styling */
.bg-red-600:hover {
  background-color: #dc2626;
  transform: translateY(-1px);
  box-shadow: 0 4px 15px rgba(220, 38, 38, 0.3);
}

/* Smooth organic transitions */
* {
  transition: transform 0.2s cubic-bezier(0.4, 0, 0.2, 1),
              box-shadow 0.2s cubic-bezier(0.4, 0, 0.2, 1),
              background-color 0.2s cubic-bezier(0.4, 0, 0.2, 1);
}
</style>