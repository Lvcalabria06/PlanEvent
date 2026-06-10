/** @type {import('tailwindcss').Config} */
export default {
  content: ["./index.html", "./src/**/*.{ts,tsx}"],
  // Preflight desligado para NÃO resetar o CSS próprio do restante do app
  // (dashboard/equipe/agenda). Os utilitários do Tailwind continuam disponíveis
  // para os componentes de tarefa; o box-sizing é reescopado em tailwind.css.
  corePlugins: {
    preflight: false,
  },
  theme: {
    extend: {},
  },
  plugins: [],
};
