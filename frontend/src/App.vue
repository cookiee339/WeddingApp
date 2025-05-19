<template>
  <header class="bg-wedding-primary text-white shadow-md">
    <div class="container mx-auto py-4 px-4">
      <h1 class="text-2xl font-display text-center">Wedding Photo Gallery</h1>
    </div>
  </header>

  <nav class="bg-wedding-dark text-white">
    <div class="container mx-auto py-2 px-4">
      <div class="flex justify-around">
        <router-link to="/" class="nav-link">Capture</router-link>
        <router-link to="/all-photos" class="nav-link">All Photos</router-link>
        <router-link to="/my-photos" class="nav-link">My Photos</router-link>
      </div>
    </div>
  </nav>

  <main class="container mx-auto py-6 px-4">
    <router-view v-slot="{ Component }">
      <transition name="fade" mode="out-in">
        <component :is="Component" />
      </transition>
    </router-view>
  </main>

  <footer class="bg-wedding-dark text-white py-4 mt-auto">
    <div class="container mx-auto px-4 text-center text-sm">
      <p>Thank you for sharing your memorable moments!</p>
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
}

.nav-link {
  @apply py-2 px-4 font-medium;
}

.router-link-active {
  @apply border-b-2 border-wedding-secondary font-bold;
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