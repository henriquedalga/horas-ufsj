import tailwindcss from "@tailwindcss/vite";
import react from "@vitejs/plugin-react-swc";
import { defineConfig, loadEnv } from "vite";

export default defineConfig(({ mode }) => {
  // Carrega variáveis de ambiente com base no modo (development/production)
  const env = loadEnv(mode, process.cwd(), 'VITE_');
  
  return {
    plugins: [react(), tailwindcss()],
    envPrefix: 'VITE_',
    define: {
      // Torna as variáveis de ambiente disponíveis globalmente
      'import.meta.env.VITE_API_URL': JSON.stringify(env.VITE_API_URL),
      'import.meta.env.VITE_SSO_BASE_URL': JSON.stringify(env.VITE_SSO_BASE_URL),
      'import.meta.env.VITE_SSO_CLIENT_ID': JSON.stringify(env.VITE_SSO_CLIENT_ID),
    }
  };
});
