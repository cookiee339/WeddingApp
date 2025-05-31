import { createRouter, createWebHistory } from 'vue-router'

// Import view components
import HomeView from '../views/HomeView.vue'
import AllPhotosView from '../views/AllPhotosView.vue'
import MyPhotosView from '../views/MyPhotosView.vue'

// Define routes
const routes = [
  {
    path: '/',
    name: 'home',
    component: HomeView,
    meta: { title: 'Zrób Zdjęcie' }
  },
  {
    path: '/all-photos',
    name: 'all-photos',
    component: AllPhotosView,
    meta: { title: 'Wspólne Wspomnienia' }
  },
  {
    path: '/my-photos',
    name: 'my-photos',
    component: MyPhotosView,
    meta: { title: 'Moje Zdjęcia' }
  }
]

// Create router instance
const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
})

// Update document title based on route
router.beforeEach((to, from, next) => {
  document.title = `${to.meta.title || 'Barbara & Mikołaj'} - Ślubna Księga Fotograficzna`
  next()
})

export default router