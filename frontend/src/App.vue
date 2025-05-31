<template>
    <header class="wedding-header bg-boho-cream relative overflow-hidden">
        <!-- Decorative floral elements -->
        <div class="absolute top-4 left-4 opacity-40">
            <svg
                class="w-24 h-24 text-boho-dusty-rose"
                viewBox="0 0 100 100"
                fill="currentColor"
            >
                <path
                    d="M20 50c0-16.568 13.432-30 30-30s30 13.432 30 30c0 8.284-3.358 15.784-8.787 21.213C66.784 76.642 59.284 80 51 80s-15.784-3.358-21.213-8.787C24.358 65.784 21 58.284 21 50z"
                />
                <ellipse
                    cx="35"
                    cy="40"
                    rx="8"
                    ry="4"
                    transform="rotate(-20 35 40)"
                />
                <ellipse
                    cx="65"
                    cy="40"
                    rx="8"
                    ry="4"
                    transform="rotate(20 65 40)"
                />
                <ellipse cx="50" cy="35" rx="6" ry="3" />
                <ellipse
                    cx="40"
                    cy="55"
                    rx="5"
                    ry="2.5"
                    transform="rotate(-30 40 55)"
                />
                <ellipse
                    cx="60"
                    cy="55"
                    rx="5"
                    ry="2.5"
                    transform="rotate(30 60 55)"
                />
            </svg>
        </div>

        <div class="absolute top-4 right-4 opacity-40">
            <svg
                class="w-18 h-18 text-boho-dusty-rose"
                viewBox="0 0 60 60"
                fill="currentColor"
            >
                <path d="M30 10c3 0 6 2 8 5l-8 5-8-5c2-3 5-5 8-5z" />
                <path d="M30 20l8-5c2 3 2 7 0 10l-8-5z" />
                <path d="M30 20l-8-5c-2 3-2 7 0 10l8-5z" />
                <path d="M30 20l8 5c-2 3-5 5-8 5s-6-2-8-5l8-5z" />
                <path d="M22 25c-3 2-5 5-5 8s2 6 5 8l5-8-5-8z" />
                <path d="M38 25c3 2 5 5 5 8s-2 6-5 8l-5-8 5-8z" />
            </svg>
        </div>

        <!-- Pampas grass silhouettes -->
        <div class="absolute bottom-0 left-8 opacity-30">
            <svg
                class="w-12 h-32 text-boho-brown"
                viewBox="0 0 20 60"
                fill="currentColor"
            >
                <path
                    d="M10 60v-25c0-2 1-4 3-5l4-2c1-1 1-2 0-3l-4-3c-2-1-3-3-3-5V0"
                />
                <ellipse cx="13" cy="15" rx="3" ry="8" opacity="0.6" />
                <ellipse cx="7" cy="18" rx="2" ry="6" opacity="0.4" />
                <ellipse cx="15" cy="22" rx="2.5" ry="7" opacity="0.5" />
            </svg>
        </div>

        <div class="absolute bottom-0 right-8 opacity-30">
            <svg
                class="w-10 h-28 text-boho-brown"
                viewBox="0 0 15 50"
                fill="currentColor"
            >
                <path
                    d="M7 50v-20c0-2 1-3 2-4l3-2c1-1 1-2 0-2l-3-2c-1-1-2-2-2-4V0"
                />
                <ellipse cx="9" cy="12" rx="2" ry="6" opacity="0.6" />
                <ellipse cx="5" cy="14" rx="1.5" ry="4" opacity="0.4" />
                <ellipse cx="11" cy="17" rx="2" ry="5" opacity="0.5" />
            </svg>
        </div>

        <div class="container mx-auto py-4 px-4 relative z-10">
            <h1 class="couple-names">Barbara & Mikołaj</h1>
            <p class="wedding-date">07.06.2025</p>
            <p class="page-title">Ślubna Księga Fotograficzna</p>

            <!-- Small floral accent under the title -->
            <div class="flex justify-center mt-4">
                <svg
                    class="w-24 h-6 text-boho-dusty-rose opacity-70"
                    viewBox="0 0 80 20"
                    fill="currentColor"
                >
                    <circle cx="15" cy="10" r="2" />
                    <circle cx="25" cy="8" r="1.5" />
                    <circle cx="35" cy="12" r="1" />
                    <circle cx="40" cy="10" r="3" />
                    <circle cx="45" cy="12" r="1" />
                    <circle cx="55" cy="8" r="1.5" />
                    <circle cx="65" cy="10" r="2" />
                    <path
                        d="M10 10c2-1 4-1 6 0M20 8c2-1 4-1 6 0M30 12c2-1 4-1 6 0M50 8c2-1 4-1 6 0M60 10c2-1 4-1 6 0"
                        stroke="currentColor"
                        stroke-width="0.5"
                        fill="none"
                    />
                </svg>
            </div>
        </div>
    </header>

    <!-- Mobile Bottom Navigation -->
    <nav
        class="fixed bottom-0 left-0 right-0 bg-white border-t border-boho-rose-border z-40 safe-area-inset"
    >
        <div class="flex justify-around items-center py-2">
            <router-link to="/" class="mobile-nav-item">
                <div
                    class="flex flex-col items-center space-y-1 py-2 px-3 rounded-xl transition-all touch-manipulation"
                >
                    <svg
                        xmlns="http://www.w3.org/2000/svg"
                        class="h-6 w-6"
                        fill="none"
                        viewBox="0 0 24 24"
                        stroke="currentColor"
                    >
                        <path
                            stroke-linecap="round"
                            stroke-linejoin="round"
                            stroke-width="2"
                            d="M3 9a2 2 0 012-2h.93a2 2 0 001.664-.89l.812-1.22A2 2 0 0110.07 4h3.86a2 2 0 011.664.89l.812 1.22A2 2 0 0018.07 7H19a2 2 0 012 2v9a2 2 0 01-2 2H5a2 2 0 01-2-2V9z"
                        />
                        <path
                            stroke-linecap="round"
                            stroke-linejoin="round"
                            stroke-width="2"
                            d="M15 13a3 3 0 11-6 0 3 3 0 016 0z"
                        />
                    </svg>
                    <span class="text-xs font-medium">Zrób zdjęcie</span>
                </div>
            </router-link>

            <router-link to="/all-photos" class="mobile-nav-item">
                <div
                    class="flex flex-col items-center space-y-1 py-2 px-3 rounded-xl transition-all touch-manipulation"
                >
                    <svg
                        xmlns="http://www.w3.org/2000/svg"
                        class="h-6 w-6"
                        fill="none"
                        viewBox="0 0 24 24"
                        stroke="currentColor"
                    >
                        <path
                            stroke-linecap="round"
                            stroke-linejoin="round"
                            stroke-width="2"
                            d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z"
                        />
                    </svg>
                    <span class="text-xs font-medium">Galeria</span>
                </div>
            </router-link>

            <router-link to="/my-photos" class="mobile-nav-item">
                <div
                    class="flex flex-col items-center space-y-1 py-2 px-3 rounded-xl transition-all touch-manipulation"
                >
                    <svg
                        xmlns="http://www.w3.org/2000/svg"
                        class="h-6 w-6"
                        fill="none"
                        viewBox="0 0 24 24"
                        stroke="currentColor"
                    >
                        <path
                            stroke-linecap="round"
                            stroke-linejoin="round"
                            stroke-width="2"
                            d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"
                        />
                    </svg>
                    <span class="text-xs font-medium">Moje Zdjęcia</span>
                </div>
            </router-link>
        </div>
    </nav>

    <main class="container mx-auto py-6 px-4 sm:px-6 lg:px-8 pb-24">
        <router-view v-slot="{ Component }">
            <transition name="fade" mode="out-in">
                <component :is="Component" />
            </transition>
        </router-view>
    </main>

    <footer
        class="bg-boho-cream py-10 md:py-16 mt-8 md:mt-12 border-t footer-border text-center"
    >
        <div class="container mx-auto px-4">
            <p class="opacity-70 text-boho-brown">
                Z miłością i najlepszymi życzeniami
            </p>
            <div class="decorative-icons text-boho-brown">
                <!-- Eucalyptus branch -->
                <svg
                    class="w-12 h-9 mx-3"
                    viewBox="0 0 40 30"
                    fill="currentColor"
                    xmlns="http://www.w3.org/2000/svg"
                >
                    <path
                        d="M5 25c3-2 6-4 8-7s3-6 4-9c1-3 2-6 3-9"
                        stroke="currentColor"
                        stroke-width="1.5"
                        fill="none"
                    />
                    <ellipse cx="8" cy="20" rx="2" ry="3" opacity="0.7" />
                    <ellipse cx="12" cy="16" rx="2.5" ry="3.5" opacity="0.6" />
                    <ellipse cx="16" cy="12" rx="2" ry="3" opacity="0.7" />
                    <ellipse cx="19" cy="8" rx="1.5" ry="2.5" opacity="0.6" />
                    <ellipse cx="21" cy="5" rx="1" ry="2" opacity="0.5" />
                </svg>

                <!-- Wildflower -->
                <svg
                    class="w-9 h-9 mx-3"
                    viewBox="0 0 30 30"
                    fill="currentColor"
                    xmlns="http://www.w3.org/2000/svg"
                >
                    <circle cx="15" cy="15" r="2" />
                    <ellipse cx="15" cy="8" rx="2" ry="4" />
                    <ellipse
                        cx="22"
                        cy="15"
                        rx="2"
                        ry="4"
                        transform="rotate(60 22 15)"
                    />
                    <ellipse
                        cx="22"
                        cy="22"
                        rx="2"
                        ry="4"
                        transform="rotate(120 22 22)"
                    />
                    <ellipse
                        cx="15"
                        cy="22"
                        rx="2"
                        ry="4"
                        transform="rotate(180 15 22)"
                    />
                    <ellipse
                        cx="8"
                        cy="22"
                        rx="2"
                        ry="4"
                        transform="rotate(240 8 22)"
                    />
                    <ellipse
                        cx="8"
                        cy="15"
                        rx="2"
                        ry="4"
                        transform="rotate(300 8 15)"
                    />
                </svg>

                <!-- Pampas grass -->
                <svg
                    class="w-8 h-12 mx-3"
                    viewBox="0 0 20 40"
                    fill="currentColor"
                    xmlns="http://www.w3.org/2000/svg"
                >
                    <path
                        d="M10 35v-15c0-2 1-3 2-4"
                        stroke="currentColor"
                        stroke-width="1"
                        fill="none"
                    />
                    <ellipse cx="12" cy="15" rx="3" ry="8" opacity="0.6" />
                    <ellipse cx="8" cy="18" rx="2" ry="6" opacity="0.4" />
                    <ellipse cx="14" cy="20" rx="2.5" ry="7" opacity="0.5" />
                </svg>

                <!-- Small botanical sprig -->
                <svg
                    class="w-9 h-7 mx-3"
                    viewBox="0 0 25 20"
                    fill="currentColor"
                    xmlns="http://www.w3.org/2000/svg"
                >
                    <path
                        d="M5 15c2-1 4-2 6-4s3-4 4-6"
                        stroke="currentColor"
                        stroke-width="1"
                        fill="none"
                    />
                    <circle cx="7" cy="13" r="1" opacity="0.7" />
                    <circle cx="10" cy="10" r="1.2" opacity="0.6" />
                    <circle cx="13" cy="7" r="1" opacity="0.7" />
                    <circle cx="15" cy="5" r="0.8" opacity="0.5" />
                </svg>

                <!-- Another eucalyptus branch (mirrored) -->
                <svg
                    class="w-12 h-9 mx-3"
                    viewBox="0 0 40 30"
                    fill="currentColor"
                    xmlns="http://www.w3.org/2000/svg"
                    style="transform: scaleX(-1)"
                >
                    <path
                        d="M5 25c3-2 6-4 8-7s3-6 4-9c1-3 2-6 3-9"
                        stroke="currentColor"
                        stroke-width="1.5"
                        fill="none"
                    />
                    <ellipse cx="8" cy="20" rx="2" ry="3" opacity="0.7" />
                    <ellipse cx="12" cy="16" rx="2.5" ry="3.5" opacity="0.6" />
                    <ellipse cx="16" cy="12" rx="2" ry="3" opacity="0.7" />
                    <ellipse cx="19" cy="8" rx="1.5" ry="2.5" opacity="0.6" />
                    <ellipse cx="21" cy="5" rx="1" ry="2" opacity="0.5" />
                </svg>
            </div>
        </div>
    </footer>

    <!-- Access Control Component -->
    <AccessControl />
</template>

<script setup>
import { onMounted } from "vue";
import { v4 as uuidv4 } from "uuid";
import AccessControl from "./components/AccessControl.vue";
import { useAccessStore } from "./stores/accessStore";

const accessStore = useAccessStore();

// Initialize unique user identifier if it doesn't exist
onMounted(() => {
    if (!localStorage.getItem("uploader_id")) {
        localStorage.setItem("uploader_id", uuidv4());
    }
    
    // Check URL for access token and initialize access control
    accessStore.checkURLAccess();
});
</script>

<style>
#app {
    display: flex;
    flex-direction: column;
    min-height: 100vh;
    background-color: #fffbf0;
    color: #5d5550;
}

.mobile-nav-item {
    text-boho-brown: true;
    opacity: 0.6;
    transition: all 0.2s;
    touch-action: manipulation;
    min-height: 44px;
    min-width: 44px;
}

.mobile-nav-item:hover {
    opacity: 0.8;
}

.mobile-nav-item.router-link-active {
    @apply text-boho-dusty-rose opacity-100;
}

.mobile-nav-item.router-link-active div {
    @apply bg-boho-rose-light;
}

.mobile-nav-item.router-link-active svg {
    @apply text-boho-dusty-rose;
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
