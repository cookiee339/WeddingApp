<template>
  <teleport to="body">
    <div 
      v-if="accessStore.needsAccess || accessStore.isLoading" 
      class="fixed inset-0 bg-white flex items-center justify-center z-50"
    >
      <div class="max-w-md w-full mx-4">
        <!-- Loading State -->
        <div v-if="accessStore.isLoading" class="text-center">
          <div class="flex justify-center mb-6">
            <svg class="w-16 h-16 text-boho-dusty-rose animate-spin" viewBox="0 0 100 100" fill="currentColor">
              <path d="M20 50c0-16.568 13.432-30 30-30s30 13.432 30 30c0 8.284-3.358 15.784-8.787 21.213C66.784 76.642 59.284 80 51 80s-15.784-3.358-21.213-8.787C24.358 65.784 21 58.284 21 50z"/>
              <ellipse cx="35" cy="40" rx="8" ry="4" transform="rotate(-20 35 40)" opacity="0.6"/>
              <ellipse cx="65" cy="40" rx="8" ry="4" transform="rotate(20 65 40)" opacity="0.8"/>
              <ellipse cx="50" cy="35" rx="6" ry="3" opacity="0.7"/>
            </svg>
          </div>
          <h2 class="text-2xl font-semibold text-boho-brown mb-2">Sprawdzanie dostępu...</h2>
          <p class="text-boho-dusty-rose">Barbara & Mikołaj - 07.06.2025</p>
        </div>

        <!-- Access Denied State -->
        <div v-else class="text-center bg-boho-cream rounded-2xl p-8 shadow-lg">
          <div class="flex justify-center mb-6">
            <svg class="w-20 h-20 text-boho-dusty-rose" viewBox="0 0 100 100" fill="currentColor">
              <path d="M20 50c0-16.568 13.432-30 30-30s30 13.432 30 30c0 8.284-3.358 15.784-8.787 21.213C66.784 76.642 59.284 80 51 80s-15.784-3.358-21.213-8.787C24.358 65.784 21 58.284 21 50z"/>
              <ellipse cx="35" cy="40" rx="8" ry="4" transform="rotate(-20 35 40)"/>
              <ellipse cx="65" cy="40" rx="8" ry="4" transform="rotate(20 65 40)"/>
              <ellipse cx="50" cy="35" rx="6" ry="3"/>
              <ellipse cx="40" cy="55" rx="5" ry="2.5" transform="rotate(-30 40 55)"/>
              <ellipse cx="60" cy="55" rx="5" ry="2.5" transform="rotate(30 60 55)"/>
            </svg>
          </div>
          
          <h1 class="text-3xl font-bold text-boho-brown mb-2">Barbara & Mikołaj</h1>
          <p class="text-lg text-boho-dusty-rose mb-6">07.06.2025</p>
          
          <div class="bg-white rounded-lg p-6 mb-6">
            <h2 class="text-xl font-semibold text-boho-brown mb-3">Dostęp Ograniczony</h2>
            <p class="text-boho-brown mb-4">
              Ta aplikacja jest dostępna tylko dla gości weselnych. 
              Aby uzyskać dostęp, zeskanuj kod QR otrzymany od organizatorów wesela.
            </p>
            
            <div class="flex items-center justify-center space-x-2 text-sm text-gray-600">
              <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v1m6 11h2m-6 0h-2v4m0-11v3m0 0h.01M12 12h4.01M16 16h4.01M12 12V8m0 0V4m0 0H8m4 0h4"/>
              </svg>
              <span>Zeskanuj kod QR aby kontynuować</span>
            </div>
          </div>
          
          <!-- Decorative elements -->
          <div class="flex justify-center space-x-4 opacity-60">
            <svg class="w-8 h-8 text-boho-brown" viewBox="0 0 30 30" fill="currentColor">
              <circle cx="15" cy="15" r="2"/>
              <ellipse cx="15" cy="8" rx="2" ry="4"/>
              <ellipse cx="22" cy="15" rx="2" ry="4" transform="rotate(60 22 15)"/>
              <ellipse cx="22" cy="22" rx="2" ry="4" transform="rotate(120 22 22)"/>
              <ellipse cx="15" cy="22" rx="2" ry="4" transform="rotate(180 15 22)"/>
              <ellipse cx="8" cy="22" rx="2" ry="4" transform="rotate(240 8 22)"/>
              <ellipse cx="8" cy="15" rx="2" ry="4" transform="rotate(300 8 15)"/>
            </svg>
            <svg class="w-6 h-10 text-boho-brown" viewBox="0 0 20 40" fill="currentColor">
              <path d="M10 35v-15c0-2 1-3 2-4" stroke="currentColor" stroke-width="1" fill="none"/>
              <ellipse cx="12" cy="15" rx="3" ry="8" opacity="0.6"/>
              <ellipse cx="8" cy="18" rx="2" ry="6" opacity="0.4"/>
              <ellipse cx="14" cy="20" rx="2.5" ry="7" opacity="0.5"/>
            </svg>
            <svg class="w-8 h-8 text-boho-brown" viewBox="0 0 30 30" fill="currentColor">
              <circle cx="15" cy="15" r="2"/>
              <ellipse cx="15" cy="8" rx="2" ry="4"/>
              <ellipse cx="22" cy="15" rx="2" ry="4" transform="rotate(60 22 15)"/>
              <ellipse cx="22" cy="22" rx="2" ry="4" transform="rotate(120 22 22)"/>
              <ellipse cx="15" cy="22" rx="2" ry="4" transform="rotate(180 15 22)"/>
              <ellipse cx="8" cy="22" rx="2" ry="4" transform="rotate(240 8 22)"/>
              <ellipse cx="8" cy="15" rx="2" ry="4" transform="rotate(300 8 15)"/>
            </svg>
          </div>
        </div>
      </div>
    </div>
  </teleport>
</template>

<script setup>
import { useAccessStore } from '../stores/accessStore'

const accessStore = useAccessStore()
</script>

<style scoped>
/* Prevent body scroll when access control is active */
</style>