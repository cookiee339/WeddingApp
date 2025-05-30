<template>
  <header class="wedding-header bg-boho-cream">
    <div class="container mx-auto py-4 px-4">
      <h1 class="couple-names">Barbara & Mikołaj</h1>
      <p class="wedding-date">07.06.2025</p>
      <p class="page-title">Wedding Photo Guestbook</p>
    </div>
  </header>

  <nav class="bg-boho-brown-dark text-white">
    <div class="container mx-auto py-2 px-4">
      <div class="flex justify-around">
        <router-link to="/" class="nav-link">Capture</router-link>
        <router-link to="/all-photos" class="nav-link">All Photos</router-link>
        <router-link to="/my-photos" class="nav-link">My Photos</router-link>
      </div>
    </div>
  </nav>

  <main class="container mx-auto py-6 px-4 sm:px-6 lg:px-8">
    <router-view v-slot="{ Component }">
      <transition name="fade" mode="out-in">
        <component :is="Component" />
      </transition>
    </router-view>
  </main>

  <footer class="bg-boho-cream py-10 md:py-16 mt-8 md:mt-12 border-t footer-border text-center">
    <div class="container mx-auto px-4">
      <p class="opacity-70 text-boho-brown">With love & best wishes</p>
      <div class="decorative-icons text-boho-brown">
        <svg class="w-5 h-5 mx-1" viewBox="0 0 24 24" fill="currentColor" xmlns="http://www.w3.org/2000/svg">
          <path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z"/>
        </svg>
        <svg class="w-5 h-5 mx-1" viewBox="0 0 24 24" fill="currentColor" xmlns="http://www.w3.org/2000/svg">
          <path d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z"/>
        </svg>
        <svg class="w-5 h-5 mx-1" viewBox="0 0 24 24" fill="currentColor" xmlns="http://www.w3.org/2000/svg">
          <path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z"/>
        </svg>
      </div>
    </div>
  </footer>
</template>

<script setup>
import { onMounted } from 'vue';
import { v4 as uuidv4 } from 'uuid';

// Initialize unique user identifier if it doesn't exist
onMounted(() => {
  if (!localStorage.getItem('uploader_id')) {
    localStorage.setItem('uploader_id', uuidv4());
  }
});
</script>

<style>
#app {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  background-color: #FFFBF0;
  color: #5D5550;
}

.nav-link {
  @apply py-2 px-4 font-medium transition-colors duration-200;
}

.nav-link:hover {
  @apply opacity-80;
}

.router-link-active {
  @apply border-b-2 font-bold;
  border-color: #D8A0A0;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>