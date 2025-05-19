import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'
import './assets/main.css'

// Create and configure the Vue application
const app = createApp(App)

// Add global store management with Pinia
app.use(createPinia())

// Add Vue Router for navigation
app.use(router)

// Mount the application to the DOM
app.mount('#app')