module.exports = {
  content: [
    "./index.html",
    "./src/**/*.{vue,js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        'wedding-primary': '#4F6D7A',
        'wedding-secondary': '#DD6E42',
        'wedding-light': '#F4F4F9',
        'wedding-dark': '#2E2E2E',
        'wedding-accent': '#E8DAB2'
      },
      fontFamily: {
        'display': ['Playfair Display', 'serif'],
        'body': ['Lato', 'sans-serif'],
      },
      spacing: {
        '128': '32rem',
      },
    },
  },
  plugins: [],
}