import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useAccessStore = defineStore('access', () => {
  // State
  const accessToken = ref(localStorage.getItem('wedding_access_token') || null)
  const accessGrantedAt = ref(localStorage.getItem('wedding_access_granted_at') || null)
  const isLoading = ref(false)
  const hasInitialized = ref(false)

  // Computed
  const hasAccess = computed(() => {
    if (!accessToken.value || !accessGrantedAt.value) return false
    
    // Check if access is still valid (48 hours)
    const grantedTime = new Date(accessGrantedAt.value)
    const now = new Date()
    const hoursSinceGranted = (now - grantedTime) / (1000 * 60 * 60)
    
    return hoursSinceGranted < 48
  })

  const needsAccess = computed(() => !hasAccess.value && hasInitialized.value)

  // Actions
  function grantAccess(token) {
    const now = new Date().toISOString()
    accessToken.value = token
    accessGrantedAt.value = now
    
    // Persist to localStorage
    localStorage.setItem('wedding_access_token', token)
    localStorage.setItem('wedding_access_granted_at', now)
  }

  function revokeAccess() {
    accessToken.value = null
    accessGrantedAt.value = null
    
    // Clear from localStorage
    localStorage.removeItem('wedding_access_token')
    localStorage.removeItem('wedding_access_granted_at')
  }

  async function validateToken(token) {
    try {
      const response = await fetch(`${import.meta.env.VITE_API_BASE_URL}/access/validate`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ token })
      })

      if (!response.ok) {
        return false
      }

      const result = await response.json()
      return result.valid === true
    } catch (error) {
      console.error('Token validation error:', error)
      return false
    }
  }

  async function checkURLAccess() {
    if (hasAccess.value) {
      hasInitialized.value = true
      return true
    }

    isLoading.value = true
    
    try {
      // Check URL parameters for access token
      const urlParams = new URLSearchParams(window.location.search)
      const urlToken = urlParams.get('token')
      
      if (urlToken) {
        const isValid = await validateToken(urlToken)
        if (isValid) {
          grantAccess(urlToken)
          // Clean URL without reloading
          const newUrl = window.location.pathname
          window.history.replaceState({}, document.title, newUrl)
          hasInitialized.value = true
          return true
        }
      }
      
      // If no valid token found, revoke any existing access
      if (accessToken.value) {
        const isValid = await validateToken(accessToken.value)
        if (!isValid) {
          revokeAccess()
        }
      }
      
      hasInitialized.value = true
      return hasAccess.value
    } catch (error) {
      console.error('Access check error:', error)
      hasInitialized.value = true
      return false
    } finally {
      isLoading.value = false
    }
  }

  return {
    // State
    accessToken,
    accessGrantedAt,
    isLoading,
    hasInitialized,
    
    // Computed
    hasAccess,
    needsAccess,
    
    // Actions
    grantAccess,
    revokeAccess,
    validateToken,
    checkURLAccess
  }
})
