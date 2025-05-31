<template>
  <div class="max-w-4xl mx-auto p-6 bg-white rounded-lg shadow-lg">
    <!-- Header -->
    <div class="text-center mb-8">
      <h1 class="text-3xl font-bold text-boho-brown mb-2">Generator Kodów QR</h1>
      <p class="text-boho-dusty-rose">Barbara & Mikołaj - 07.06.2025</p>
      <p class="text-sm text-gray-600 mt-2">
        Generuj kody QR dla gości weselnych aby umożliwić im dostęp do aplikacji
      </p>
    </div>

    <!-- Generator Form -->
    <div class="grid grid-cols-1 lg:grid-cols-2 gap-8">
      <!-- Left Column - Generator -->
      <div class="space-y-6">
        <div class="bg-boho-cream p-6 rounded-lg">
          <h2 class="text-xl font-semibold text-boho-brown mb-4">Nowy Kod QR</h2>
          
          <div class="space-y-4">
            <div>
              <label class="block text-sm font-medium text-boho-brown mb-2">
                Nazwa/Opis (opcjonalne)
              </label>
              <input
                v-model="codeDescription"
                type="text"
                placeholder="np. Stół 1, Rodzina pana młodego..."
                class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-boho-dusty-rose focus:border-transparent outline-none"
              />
            </div>

            <div>
              <label class="block text-sm font-medium text-boho-brown mb-2">
                Ważność (godziny)
              </label>
              <select
                v-model="validityHours"
                class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-boho-dusty-rose focus:border-transparent outline-none"
              >
                <option value="24">24 godziny (standardowe)</option>
                <option value="48">48 godzin</option>
                <option value="72">72 godziny</option>
                <option value="168">1 tydzień</option>
              </select>
            </div>

            <button
              @click="generateQRCode"
              :disabled="isGenerating"
              class="w-full bg-boho-dusty-rose text-white py-3 px-6 rounded-lg font-medium hover:bg-opacity-90 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
            >
              <span v-if="isGenerating" class="flex items-center justify-center">
                <svg class="animate-spin -ml-1 mr-3 h-5 w-5 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                  <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                  <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                </svg>
                Generowanie...
              </span>
              <span v-else>Wygeneruj Kod QR</span>
            </button>
          </div>
        </div>

        <!-- Generated Codes List -->
        <div v-if="generatedCodes.length > 0" class="bg-gray-50 p-6 rounded-lg">
          <h3 class="text-lg font-semibold text-boho-brown mb-4">Wygenerowane Kody</h3>
          <div class="space-y-3 max-h-64 overflow-y-auto">
            <div
              v-for="code in generatedCodes"
              :key="code.id"
              @click="selectCode(code)"
              :class="[
                'p-3 bg-white rounded-lg border cursor-pointer transition-all hover:shadow-md',
                selectedCode?.id === code.id ? 'border-boho-dusty-rose bg-boho-rose-light' : 'border-gray-200'
              ]"
            >
              <div class="flex justify-between items-start">
                <div>
                  <p class="font-medium text-boho-brown">
                    {{ code.description || `Kod #${code.id.slice(-6)}` }}
                  </p>
                  <p class="text-xs text-gray-500">
                    Utworzony: {{ formatDate(code.createdAt) }}
                  </p>
                  <p class="text-xs text-gray-500">
                    Ważny do: {{ formatDate(code.expiresAt) }}
                  </p>
                </div>
                <span :class="[
                  'px-2 py-1 rounded-full text-xs font-medium',
                  isCodeValid(code) ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'
                ]">
                  {{ isCodeValid(code) ? 'Aktywny' : 'Wygasły' }}
                </span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Right Column - QR Display -->
      <div class="space-y-6">
        <div v-if="selectedCode" class="bg-white border-2 border-boho-dusty-rose rounded-lg p-6">
          <div class="text-center space-y-4">
            <h3 class="text-lg font-semibold text-boho-brown">
              {{ selectedCode.description || `Kod QR #${selectedCode.id.slice(-6)}` }}
            </h3>
            
            <!-- QR Code Display -->
            <div class="flex justify-center">
              <div class="bg-white p-4 rounded-lg shadow-inner">
                <canvas
                  ref="qrCanvas"
                  class="border border-gray-200 rounded"
                ></canvas>
              </div>
            </div>

            <!-- Code Info -->
            <div class="text-sm text-gray-600 space-y-1">
              <p>Token: <code class="bg-gray-100 px-2 py-1 rounded text-xs">{{ selectedCode.token }}</code></p>
              <p>Ważny do: {{ formatDate(selectedCode.expiresAt) }}</p>
              <p>Status: 
                <span :class="isCodeValid(selectedCode) ? 'text-green-600' : 'text-red-600'">
                  {{ isCodeValid(selectedCode) ? 'Aktywny' : 'Wygasły' }}
                </span>
              </p>
            </div>

            <!-- Action Buttons -->
            <div class="flex flex-col sm:flex-row gap-3 pt-4">
              <button
                @click="downloadQR"
                class="flex-1 bg-boho-brown text-white py-2 px-4 rounded-lg font-medium hover:bg-opacity-90 transition-colors"
              >
                <svg class="w-4 h-4 inline mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 10v6m0 0l-3-3m3 3l3-3M3 17V7a2 2 0 012-2h6l2 2h6a2 2 0 012 2v10a2 2 0 01-2 2H5a2 2 0 01-2-2z"/>
                </svg>
                Pobierz PNG
              </button>
              <button
                @click="printQR"
                class="flex-1 bg-boho-dusty-rose text-white py-2 px-4 rounded-lg font-medium hover:bg-opacity-90 transition-colors"
              >
                <svg class="w-4 h-4 inline mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 17h2a2 2 0 002-2v-4a2 2 0 00-2-2H5a2 2 0 00-2 2v4a2 2 0 002 2h2m2 4h6a2 2 0 002-2v-4a2 2 0 00-2-2H9a2 2 0 00-2 2v4a2 2 0 002 2zm8-12V5a2 2 0 00-2-2H9a2 2 0 00-2 2v4h10z"/>
                </svg>
                Drukuj
              </button>
            </div>
          </div>
        </div>

        <div v-else class="bg-gray-50 border-2 border-dashed border-gray-300 rounded-lg p-12 text-center">
          <svg class="w-16 h-16 mx-auto text-gray-400 mb-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v1m6 11h2m-6 0h-2v4m0-11v3m0 0h.01M12 12h4.01M16 16h4.01M12 12V8m0 0V4m0 0H8m4 0h4"/>
          </svg>
          <p class="text-gray-500">Wygeneruj lub wybierz kod QR aby go wyświetlić</p>
        </div>
      </div>
    </div>

    <!-- Error/Success Messages -->
    <div v-if="message" :class="[
      'mt-6 p-4 rounded-lg text-sm',
      messageType === 'success' 
        ? 'bg-green-50 text-green-800 border border-green-200' 
        : 'bg-red-50 text-red-800 border border-red-200'
    ]">
      {{ message }}
    </div>
  </div>
</template>

<script setup>
import { ref, nextTick, onMounted } from 'vue'
import QRCode from 'qrcode'

// State
const codeDescription = ref('')
const validityHours = ref('24')
const isGenerating = ref(false)
const generatedCodes = ref([])
const selectedCode = ref(null)
const qrCanvas = ref(null)
const message = ref('')
const messageType = ref('error')

// Methods
async function generateQRCode() {
  isGenerating.value = true
  clearMessage()
  
  try {
    const response = await fetch(`${import.meta.env.VITE_API_URL}/api/access/generate`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        description: codeDescription.value.trim() || null,
        validityHours: parseInt(validityHours.value)
      })
    })

    if (!response.ok) {
      throw new Error('Failed to generate access code')
    }

    const newCode = await response.json()
    generatedCodes.value.unshift(newCode)
    selectedCode.value = newCode
    
    // Clear form
    codeDescription.value = ''
    
    // Generate QR code display
    await nextTick()
    await renderQRCode(newCode)
    
    showMessage('Kod QR został wygenerowany pomyślnie!', 'success')
  } catch (error) {
    console.error('Error generating QR code:', error)
    showMessage('Błąd podczas generowania kodu QR. Spróbuj ponownie.', 'error')
  } finally {
    isGenerating.value = false
  }
}

async function selectCode(code) {
  selectedCode.value = code
  await nextTick()
  await renderQRCode(code)
}

async function renderQRCode(code) {
  if (!qrCanvas.value || !code) return
  
  try {
    const qrUrl = `${window.location.origin}?token=${encodeURIComponent(code.token)}`
    
    await QRCode.toCanvas(qrCanvas.value, qrUrl, {
      width: 200,
      margin: 2,
      color: {
        dark: '#5d5550', // boho-brown
        light: '#FFFFFF'
      }
    })
  } catch (error) {
    console.error('Error rendering QR code:', error)
  }
}

function downloadQR() {
  if (!qrCanvas.value || !selectedCode.value) return
  
  try {
    const link = document.createElement('a')
    link.download = `qr-code-${selectedCode.value.description || selectedCode.value.id.slice(-6)}.png`
    link.href = qrCanvas.value.toDataURL()
    link.click()
    
    showMessage('Kod QR został pobrany!', 'success')
  } catch (error) {
    console.error('Error downloading QR code:', error)
    showMessage('Błąd podczas pobierania kodu QR', 'error')
  }
}

function printQR() {
  if (!selectedCode.value) return
  
  try {
    const printWindow = window.open('', '_blank')
    const qrDataUrl = qrCanvas.value.toDataURL()
    
    printWindow.document.write(`
      <html>
        <head>
          <title>Kod QR - ${selectedCode.value.description || selectedCode.value.id.slice(-6)}</title>
          <style>
            body { 
              font-family: Arial, sans-serif; 
              text-align: center; 
              padding: 20px;
              background: white;
            }
            .qr-container {
              border: 2px solid #d4906c;
              border-radius: 12px;
              padding: 20px;
              margin: 20px auto;
              max-width: 300px;
              background: #fffbf0;
            }
            .couple-names {
              font-size: 24px;
              font-weight: bold;
              color: #5d5550;
              margin-bottom: 5px;
            }
            .wedding-date {
              font-size: 16px;
              color: #d4906c;
              margin-bottom: 15px;
            }
            .qr-code {
              margin: 20px 0;
            }
            .instructions {
              font-size: 12px;
              color: #5d5550;
              margin-top: 15px;
              line-height: 1.4;
            }
            @media print {
              body { margin: 0; }
              .qr-container { border: 2px solid #000; }
            }
          </style>
        </head>
        <body>
          <div class="qr-container">
            <div class="couple-names">Barbara & Mikołaj</div>
            <div class="wedding-date">07.06.2025</div>
            <div class="qr-code">
              <img src="${qrDataUrl}" alt="QR Code" style="width: 200px; height: 200px;">
            </div>
            <div class="instructions">
              Zeskanuj kod aby uzyskać dostęp<br>
              do ślubnej księgi fotograficznej
            </div>
          </div>
        </body>
      </html>
    `)
    
    printWindow.document.close()
    printWindow.focus()
    
    setTimeout(() => {
      printWindow.print()
      printWindow.close()
    }, 250)
    
  } catch (error) {
    console.error('Error printing QR code:', error)
    showMessage('Błąd podczas drukowania kodu QR', 'error')
  }
}

function isCodeValid(code) {
  return new Date(code.expiresAt) > new Date()
}

function formatDate(dateString) {
  return new Date(dateString).toLocaleString('pl-PL', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

function showMessage(text, type = 'error') {
  message.value = text
  messageType.value = type
  
  if (type === 'success') {
    setTimeout(() => {
      clearMessage()
    }, 3000)
  }
}

function clearMessage() {
  message.value = ''
  messageType.value = 'error'
}

async function loadGeneratedCodes() {
  try {
    const response = await fetch(`${import.meta.env.VITE_API_URL}/api/access/codes`)
    if (response.ok) {
      generatedCodes.value = await response.json()
    }
  } catch (error) {
    console.error('Error loading generated codes:', error)
  }
}

// Initialize
onMounted(() => {
  loadGeneratedCodes()
})
</script>