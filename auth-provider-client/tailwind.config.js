/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    "./app/**/*.{js,ts,jsx,tsx}",
    "./pages/**/*.{js,ts,jsx,tsx}",
    "./components/**/*.{js,ts,jsx,tsx}",

    // Or if using `src` directory:
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        primary: "#FFC700",
        "secondary-700": "#363636",
        "secondary-800": "#212121",
        "secondary-300": "#D4D4D4",
        "transparent-dark": "rgba(0,0,0,0.3)",
      },
    },
  },
  plugins: [],
};
