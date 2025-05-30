module.exports = {
  content: [
    "./index.html",
    "./src/**/*.{vue,js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        'boho': {
          cream: '#FFFBF0',      // Light warm cream background
          offwhite: '#FFFDF7',   // Card backgrounds
          brown: '#5D5550',      // Main text color
          'brown-dark': '#4A403A', // Darker brown for headings
          'dusty-rose': '#D8A0A0', // Primary button color
          'dusty-rose-dark': '#C08080', // Hover button color
          'rose-light': '#F5E8E8', // Light rose for placeholders
          'rose-muted': '#E0B0B0', // Muted rose for icons
          'rose-border': '#F0D0D0', // Light borders
        },
        // Keep existing wedding colors for compatibility
        'wedding-primary': '#D8A0A0',
        'wedding-secondary': '#C08080',
        'wedding-light': '#FFFBF0',
        'wedding-dark': '#4A403A',
        'wedding-accent': '#F5E8E8'
      },
      fontFamily: {
        'script': ['Great Vibes', 'cursive'],
        'display': ['Great Vibes', 'cursive'],
        'body': ['Inter', 'sans-serif'],
        'sans': ['Inter', 'sans-serif'],
      },
      spacing: {
        '128': '32rem',
      },
      aspectRatio: {
        '4/5': '4 / 5',
      },
      animation: {
        'scale-up': 'scale-up 0.3s ease-in-out',
      },
      keyframes: {
        'scale-up': {
          '0%': { transform: 'scale(1)' },
          '100%': { transform: 'scale(1.05)' },
        }
      },
      backdropBlur: {
        'xs': '2px',
      }
    },
  },
  plugins: [],
}