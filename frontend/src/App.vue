<template>
  <header class="wedding-header bg-boho-cream relative overflow-hidden">
    <!-- Decorative floral elements -->
    <div class="absolute top-4 left-4 opacity-40">
      <svg class="w-24 h-24 text-boho-dusty-rose" viewBox="0 0 100 100" fill="currentColor">
        <path d="M20 50c0-16.568 13.432-30 30-30s30 13.432 30 30c0 8.284-3.358 15.784-8.787 21.213C66.784 76.642 59.284 80 51 80s-15.784-3.358-21.213-8.787C24.358 65.784 21 58.284 21 50z"/>
        <ellipse cx="35" cy="40" rx="8" ry="4" transform="rotate(-20 35 40)"/>
        <ellipse cx="65" cy="40" rx="8" ry="4" transform="rotate(20 65 40)"/>
        <ellipse cx="50" cy="35" rx="6" ry="3"/>
        <ellipse cx="40" cy="55" rx="5" ry="2.5" transform="rotate(-30 40 55)"/>
        <ellipse cx="60" cy="55" rx="5" ry="2.5" transform="rotate(30 60 55)"/>
      </svg>
    </div>
    
    <div class="absolute top-4 right-4 opacity-40">
      <svg class="w-18 h-18 text-boho-dusty-rose" viewBox="0 0 60 60" fill="currentColor">
        <path d="M30 10c3 0 6 2 8 5l-8 5-8-5c2-3 5-5 8-5z"/>
        <path d="M30 20l8-5c2 3 2 7 0 10l-8-5z"/>
        <path d="M30 20l-8-5c-2 3-2 7 0 10l8-5z"/>
        <path d="M30 20l8 5c-2 3-5 5-8 5s-6-2-8-5l8-5z"/>
        <path d="M22 25c-3 2-5 5-5 8s2 6 5 8l5-8-5-8z"/>
        <path d="M38 25c3 2 5 5 5 8s-2 6-5 8l-5-8 5-8z"/>
      </svg>
    </div>

    <!-- Pampas grass silhouettes -->
    <div class="absolute bottom-0 left-8 opacity-30">
      <svg class="w-12 h-32 text-boho-brown" viewBox="0 0 20 60" fill="currentColor">
        <path d="M10 60v-25c0-2 1-4 3-5l4-2c1-1 1-2 0-3l-4-3c-2-1-3-3-3-5V0"/>
        <ellipse cx="13" cy="15" rx="3" ry="8" opacity="0.6"/>
        <ellipse cx="7" cy="18" rx="2" ry="6" opacity="0.4"/>
        <ellipse cx="15" cy="22" rx="2.5" ry="7" opacity="0.5"/>
      </svg>
    </div>

    <div class="absolute bottom-0 right-8 opacity-30">
      <svg class="w-10 h-28 text-boho-brown" viewBox="0 0 15 50" fill="currentColor">
        <path d="M7 50v-20c0-2 1-3 2-4l3-2c1-1 1-2 0-2l-3-2c-1-1-2-2-2-4V0"/>
        <ellipse cx="9" cy="12" rx="2" ry="6" opacity="0.6"/>
        <ellipse cx="5" cy="14" rx="1.5" ry="4" opacity="0.4"/>
        <ellipse cx="11" cy="17" rx="2" ry="5" opacity="0.5"/>
      </svg>
    </div>

    <div class="container mx-auto py-4 px-4 relative z-10">
      <h1 class="couple-names">Barbara & Mikołaj</h1>
      <p class="wedding-date">07.06.2025</p>
      <p class="page-title">Wedding Photo Guestbook</p>
      
      <!-- Small floral accent under the title -->
      <div class="flex justify-center mt-4">
        <svg class="w-24 h-6 text-boho-dusty-rose opacity-70" viewBox="0 0 80 20" fill="currentColor">
          <circle cx="15" cy="10" r="2"/>
          <circle cx="25" cy="8" r="1.5"/>
          <circle cx="35" cy="12" r="1"/>
          <circle cx="40" cy="10" r="3"/>
          <circle cx="45" cy="12" r="1"/>
          <circle cx="55" cy="8" r="1.5"/>
          <circle cx="65" cy="10" r="2"/>
          <path d="M10 10c2-1 4-1 6 0M20 8c2-1 4-1 6 0M30 12c2-1 4-1 6 0M50 8c2-1 4-1 6 0M60 10c2-1 4-1 6 0" stroke="currentColor" stroke-width="0.5" fill="none"/>
        </svg>
      </div>
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
        <!-- Eucalyptus branch -->
        <svg class="w-12 h-9 mx-3" viewBox="0 0 40 30" fill="currentColor" xmlns="http://www.w3.org/2000/svg">
          <path d="M5 25c3-2 6-4 8-7s3-6 4-9c1-3 2-6 3-9" stroke="currentColor" stroke-width="1.5" fill="none"/>
          <ellipse cx="8" cy="20" rx="2" ry="3" opacity="0.7"/>
          <ellipse cx="12" cy="16" rx="2.5" ry="3.5" opacity="0.6"/>
          <ellipse cx="16" cy="12" rx="2" ry="3" opacity="0.7"/>
          <ellipse cx="19" cy="8" rx="1.5" ry="2.5" opacity="0.6"/>
          <ellipse cx="21" cy="5" rx="1" ry="2" opacity="0.5"/>
        </svg>
        
        <!-- Wildflower -->
        <svg class="w-9 h-9 mx-3" viewBox="0 0 30 30" fill="currentColor" xmlns="http://www.w3.org/2000/svg">
          <circle cx="15" cy="15" r="2"/>
          <ellipse cx="15" cy="8" rx="2" ry="4"/>
          <ellipse cx="22" cy="15" rx="2" ry="4" transform="rotate(60 22 15)"/>
          <ellipse cx="22" cy="22" rx="2" ry="4" transform="rotate(120 22 22)"/>
          <ellipse cx="15" cy="22" rx="2" ry="4" transform="rotate(180 15 22)"/>
          <ellipse cx="8" cy="22" rx="2" ry="4" transform="rotate(240 8 22)"/>
          <ellipse cx="8" cy="15" rx="2" ry="4" transform="rotate(300 8 15)"/>
        </svg>
        
        <!-- Pampas grass -->
        <svg class="w-8 h-12 mx-3" viewBox="0 0 20 40" fill="currentColor" xmlns="http://www.w3.org/2000/svg">
          <path d="M10 35v-15c0-2 1-3 2-4" stroke="currentColor" stroke-width="1" fill="none"/>
          <ellipse cx="12" cy="15" rx="3" ry="8" opacity="0.6"/>
          <ellipse cx="8" cy="18" rx="2" ry="6" opacity="0.4"/>
          <ellipse cx="14" cy="20" rx="2.5" ry="7" opacity="0.5"/>
        </svg>
        
        <!-- Small botanical sprig -->
        <svg class="w-9 h-7 mx-3" viewBox="0 0 25 20" fill="currentColor" xmlns="http://www.w3.org/2000/svg">
          <path d="M5 15c2-1 4-2 6-4s3-4 4-6" stroke="currentColor" stroke-width="1" fill="none"/>
          <circle cx="7" cy="13" r="1" opacity="0.7"/>
          <circle cx="10" cy="10" r="1.2" opacity="0.6"/>
          <circle cx="13" cy="7" r="1" opacity="0.7"/>
          <circle cx="15" cy="5" r="0.8" opacity="0.5"/>
        </svg>
        
        <!-- Another eucalyptus branch (mirrored) -->
        <svg class="w-12 h-9 mx-3" viewBox="0 0 40 30" fill="currentColor" xmlns="http://www.w3.org/2000/svg" style="transform: scaleX(-1)">
          <path d="M5 25c3-2 6-4 8-7s3-6 4-9c1-3 2-6 3-9" stroke="currentColor" stroke-width="1.5" fill="none"/>
          <ellipse cx="8" cy="20" rx="2" ry="3" opacity="0.7"/>
          <ellipse cx="12" cy="16" rx="2.5" ry="3.5" opacity="0.6"/>
          <ellipse cx="16" cy="12" rx="2" ry="3" opacity="0.7"/>
          <ellipse cx="19" cy="8" rx="1.5" ry="2.5" opacity="0.6"/>
          <ellipse cx="21" cy="5" rx="1" ry="2" opacity="0.5"/>
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