import { defineConfig } from "vitest/config";
import react from "@vitejs/plugin-react";

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  // En desarrollo, Vite reparte /api entre los tres microservicios.
  server: {
    // Cada ruta va a su microservicio. Vite es el único punto que ve el navegador.
    proxy: {
      "/api/productos": { target: "http://localhost:8081", changeOrigin: true },
      "/api/auth": { target: "http://localhost:8082", changeOrigin: true },
      "/api/usuarios": { target: "http://localhost:8082", changeOrigin: true },
      "/api/carrito": { target: "http://localhost:8083", changeOrigin: true },
      "/api/pedidos": { target: "http://localhost:8083", changeOrigin: true },
    },
  },
  // Configuración de Vitest (pruebas unitarias)
  test: {
    environment: "jsdom", // simula un navegador para poder renderizar componentes
    globals: true, // permite que React Testing Library limpie el DOM entre pruebas
    setupFiles: "./src/pruebas/setup.ts",
    css: false,
    // Cobertura con v8. El resultado sale en texto; no se exige un porcentaje.
    coverage: {
      provider: "v8",
      reporter: ["text"],
      include: ["src/**/*.{ts,tsx}"],
      exclude: ["src/pruebas/**", "src/main.tsx"],
    },
  },
});
