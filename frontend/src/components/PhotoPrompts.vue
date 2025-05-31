<template>
  <div class="photo-prompts bg-white rounded-xl shadow-lg p-6 mb-6">
    <div class="text-center mb-6">
      <h3 class="text-xl font-semibold text-boho-brown-dark mb-2">📸 Pomysły na Zdjęcia</h3>
      <p class="text-sm text-boho-brown opacity-70">Potrzebujesz inspiracji? Spróbuj jednego z tych pomysłów!</p>
    </div>

    <!-- Current prompt display -->
    <div class="current-prompt bg-gradient-to-r from-boho-dusty-rose to-boho-terracotta text-white rounded-2xl p-6 mb-4 text-center relative overflow-hidden">
      <!-- Decorative elements -->
      <div class="absolute top-2 right-2 text-white/20 text-4xl">✨</div>
      <div class="absolute bottom-2 left-2 text-white/20 text-3xl">💕</div>
      
      <div class="relative z-10">
        <div class="text-3xl mb-3">{{ currentPrompt.emoji }}</div>
        <h4 class="text-lg font-bold mb-2">{{ currentPrompt.title }}</h4>
        <p class="text-sm opacity-90">{{ currentPrompt.description }}</p>
      </div>
    </div>

    <!-- Action buttons -->
    <div class="flex space-x-3 mb-4">
      <button 
        @click="nextPrompt" 
        class="flex-1 bg-boho-dusty-rose hover:bg-boho-dusty-rose-dark text-white py-3 px-4 rounded-xl font-semibold transition-all transform active:scale-95 touch-manipulation"
      >
        🎲 Nowy Pomysł
      </button>
      <button 
        @click="toggleFavorite" 
        class="bg-white border-2 border-boho-dusty-rose text-boho-dusty-rose hover:bg-boho-rose-light py-3 px-4 rounded-xl transition-all transform active:scale-95 touch-manipulation"
        :class="{ 'bg-boho-dusty-rose text-white border-boho-dusty-rose': isFavorited }"
      >
        {{ isFavorited ? '❤️' : '🤍' }}
      </button>
    </div>

    <!-- Category filters -->
    <div class="flex flex-wrap gap-2 mb-4">
      <button
        v-for="category in categories"
        :key="category.id"
        @click="selectCategory(category.id)"
        class="px-3 py-2 rounded-full text-sm font-medium transition-all transform active:scale-95 touch-manipulation"
        :class="selectedCategory === category.id 
          ? 'bg-boho-dusty-rose text-white' 
          : 'bg-boho-rose-light text-boho-brown hover:bg-boho-dusty-rose hover:text-white'"
      >
        {{ category.emoji }} {{ category.name }}
      </button>
    </div>

    <!-- Quick prompts grid -->
    <div class="grid grid-cols-2 gap-3" v-if="showQuickPrompts">
      <button
        v-for="prompt in filteredPrompts.slice(0, 4)"
        :key="prompt.id"
        @click="selectPrompt(prompt)"
        class="bg-boho-rose-light hover:bg-boho-dusty-rose hover:text-white text-boho-brown p-3 rounded-xl transition-all transform active:scale-95 touch-manipulation text-left"
      >
        <div class="text-2xl mb-1">{{ prompt.emoji }}</div>
        <div class="text-sm font-medium leading-tight">{{ prompt.title }}</div>
      </button>
    </div>

    <!-- Toggle quick prompts -->
    <div class="text-center mt-4">
      <button 
        @click="showQuickPrompts = !showQuickPrompts"
        class="text-boho-dusty-rose text-sm font-medium underline hover:opacity-80 transition-opacity touch-manipulation"
      >
        {{ showQuickPrompts ? 'Ukryj sugestie' : 'Pokaż więcej sugestii' }}
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';

// Reactive variables
const currentPromptIndex = ref(0);
const selectedCategory = ref('all');
const showQuickPrompts = ref(false);
const favoritePrompts = ref(new Set());

// Categories
const categories = [
  { id: 'all', name: 'Wszystkie', emoji: '🎭' },
  { id: 'group', name: 'Grupa', emoji: '👥' },
  { id: 'candid', name: 'Naturalne', emoji: '😄' },
  { id: 'romantic', name: 'Miłość', emoji: '💕' },
  { id: 'details', name: 'Detale', emoji: '✨' },
  { id: 'fun', name: 'Zabawa', emoji: '🎉' }
];

// Photo prompts database
const prompts = [
  // Group photos
  { id: 1, category: 'group', emoji: '👰‍♀️', title: 'Z Panną Młodą', description: 'Zrób zdjęcie z piękną Barbarą!' },
  { id: 2, category: 'group', emoji: '🤵‍♂️', title: 'Z Panem Młodym', description: 'Zrób zdjęcie z przystojnym Mikołajem!' },
  { id: 3, category: 'group', emoji: '👥', title: 'Ekipa ze Stołu', description: 'Zdjęcie z wszystkimi przy Waszym stole!' },
  { id: 4, category: 'group', emoji: '🕺', title: 'Ekipa z Parkietu', description: 'Pokażcie razem te taneczne ruchy!' },
  { id: 5, category: 'group', emoji: '👨‍👩‍👧‍👦', title: 'Portret Rodzinny', description: 'Zbierzcie rodzinę na wyjątkowe zdjęcie' },
  
  // Candid moments
  { id: 6, category: 'candid', emoji: '😂', title: 'Szczery Śmiech', description: 'Złapcie kogoś w momencie czystej radości' },
  { id: 7, category: 'candid', emoji: '🥂', title: 'Na Zdrowie!', description: 'Toast za szczęśliwą parę!' },
  { id: 8, category: 'candid', emoji: '🍰', title: 'Reakcja na Tort', description: 'Uchwćcie moment krojenia tortu' },
  { id: 9, category: 'candid', emoji: '👏', title: 'Oklaski', description: 'Wszyscy świętujący razem' },
  { id: 10, category: 'candid', emoji: '🎤', title: 'Czas na Przemowy', description: 'Uchwćcie szczere przemowy' },
  
  // Romantic moments
  { id: 11, category: 'romantic', emoji: '💕', title: 'Szczęśliwa Para', description: 'Barbara i Mikołaj będący uroczi' },
  { id: 12, category: 'romantic', emoji: '💐', title: 'Piękny Bukiet', description: 'Pokażcie te wspaniałe kwiaty' },
  { id: 13, category: 'romantic', emoji: '💍', title: 'Błyszczące Obrączki', description: 'Te błyszczące obrączki ślubne!' },
  { id: 14, category: 'romantic', emoji: '🤗', title: 'Słodki Uścisk', description: 'Uchwćcie czuły moment' },
  { id: 15, category: 'romantic', emoji: '👫', title: 'Inne Pary', description: 'Miłość wisi w powietrzu dla wszystkich!' },
  
  // Details and decorations
  { id: 16, category: 'details', emoji: '🕯️', title: 'Dekoracje Stołów', description: 'Te piękne dekoracje stołów' },
  { id: 17, category: 'details', emoji: '🌸', title: 'Kompozycje Kwiatowe', description: 'Wspaniałe kwiaty wszędzie' },
  { id: 18, category: 'details', emoji: '✨', title: 'Magia Miejsca', description: 'Oszałamiające detale miejsca' },
  { id: 19, category: 'details', emoji: '🍽️', title: 'Sztuka Kulinarna', description: 'Te smakowicie wyglądające potrawy' },
  { id: 20, category: 'details', emoji: '🎂', title: 'Tort Weselny', description: 'Ten niesamowity tort zasługuje na własne zdjęcie!' },
  
  // Fun and creative
  { id: 21, category: 'fun', emoji: '📱', title: 'Czas na Selfie', description: 'Idealne selfie z uroczystym tłem' },
  { id: 22, category: 'fun', emoji: '🕶️', title: 'W Stylu Fotokabiny', description: 'Zapozujcie z rekwizytami!' },
  { id: 23, category: 'fun', emoji: '🙌', title: 'Ręce do Góry!', description: 'Wszyscy świętujący z rękami w górze' },
  { id: 24, category: 'fun', emoji: '🎭', title: 'Śmieszne Miny', description: 'Wyluzujcie się i bądźcie śmieszni!' },
  { id: 25, category: 'fun', emoji: '🌟', title: 'Za Kulisami', description: 'Uchwćcie momenty przygotowań' },
  { id: 26, category: 'fun', emoji: '👞', title: 'Gra Butów', description: 'Wszystkie eleganckie buty dzisiaj!' },
  { id: 27, category: 'fun', emoji: '🎨', title: 'Kreatywny Kąt', description: 'Spróbujcie artystycznej lub niezwykłej perspektywy' },
  { id: 28, category: 'fun', emoji: '🌅', title: 'Złota Godzina', description: 'Uchwćcie piękne oświetlenie' },
  { id: 29, category: 'fun', emoji: '🎪', title: 'Nieoczekiwane', description: 'Znajdźcie coś zaskakującego do sfotografowania' },
  { id: 30, category: 'fun', emoji: '🎈', title: 'Imprezowy Klimat', description: 'Pokażcie energię uroczystości' }
];

// Computed properties
const currentPrompt = computed(() => prompts[currentPromptIndex.value]);

const filteredPrompts = computed(() => {
  if (selectedCategory.value === 'all') {
    return prompts;
  }
  return prompts.filter(prompt => prompt.category === selectedCategory.value);
});

const isFavorited = computed(() => 
  favoritePrompts.value.has(currentPrompt.value.id)
);

// Methods
function nextPrompt() {
  const filtered = filteredPrompts.value;
  currentPromptIndex.value = (currentPromptIndex.value + 1) % filtered.length;
  
  // Update index to match the actual prompt in the full array
  const selectedPrompt = filtered[currentPromptIndex.value % filtered.length];
  currentPromptIndex.value = prompts.findIndex(p => p.id === selectedPrompt.id);
  
  // Provide haptic feedback
  if ('vibrate' in navigator) {
    navigator.vibrate(50);
  }
}

function selectCategory(categoryId) {
  selectedCategory.value = categoryId;
  // Reset to first prompt of the category
  const filtered = filteredPrompts.value;
  if (filtered.length > 0) {
    currentPromptIndex.value = prompts.findIndex(p => p.id === filtered[0].id);
  }
  
  // Provide haptic feedback
  if ('vibrate' in navigator) {
    navigator.vibrate(30);
  }
}

function selectPrompt(prompt) {
  currentPromptIndex.value = prompts.findIndex(p => p.id === prompt.id);
  showQuickPrompts.value = false;
  
  // Provide haptic feedback
  if ('vibrate' in navigator) {
    navigator.vibrate(50);
  }
}

function toggleFavorite() {
  const promptId = currentPrompt.value.id;
  if (favoritePrompts.value.has(promptId)) {
    favoritePrompts.value.delete(promptId);
  } else {
    favoritePrompts.value.add(promptId);
  }
  
  // Save to localStorage
  localStorage.setItem('favoritePrompts', JSON.stringify([...favoritePrompts.value]));
  
  // Provide haptic feedback
  if ('vibrate' in navigator) {
    navigator.vibrate(isFavorited.value ? [50, 50, 50] : 100);
  }
}

// Initialize component
onMounted(() => {
  // Load favorites from localStorage
  const saved = localStorage.getItem('favoritePrompts');
  if (saved) {
    try {
      const favorites = JSON.parse(saved);
      favoritePrompts.value = new Set(favorites);
    } catch (error) {
      console.warn('Failed to load favorite prompts:', error);
    }
  }
  
  // Start with a random prompt
  currentPromptIndex.value = Math.floor(Math.random() * prompts.length);
});

// Expose methods for parent component
defineExpose({
  nextPrompt,
  getCurrentPrompt: () => currentPrompt.value
});
</script>

<style scoped>
/* Add any component-specific styles here */
.photo-prompts {
  user-select: none; /* Prevent text selection on touch devices */
}

/* Smooth transitions for category buttons */
.transition-all {
  transition: all 0.2s ease-in-out;
}
</style>