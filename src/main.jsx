import "./index.css";

import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import { BrowserRouter } from "react-router-dom";

import App from "./App.jsx";

// //import { setupMockRoutes } from "./mocks/axios.mock";

// if (import.meta.env.MODE !== "production") {
//   console.log("Mock routes setup complete");
// }
// //setupMockRoutes();

async function enableMocking() {
  // 1. Garante que o mock só rode em ambiente de desenvolvimento
  if (import.meta.env.MODE !== "development") {
    // <--- MUDANÇA AQUI
    return;
  }

  // 2. Importa o worker que configuramos no passo anterior
  const { worker } = await import("./mocks/browser.js");

  // 3. Inicia o worker. `worker.start()` retorna uma Promise.
  // É importante que sua aplicação só renderize DEPOIS que o worker estiver pronto.
  return worker.start({
    onUnhandledRequest: "bypass", // Opcional: deixa passar requisições não mockadas
  });
}

enableMocking().then(() => {
  createRoot(document.getElementById("root")).render(
    <StrictMode>
      <BrowserRouter>
        <App />
      </BrowserRouter>
    </StrictMode>
  );
});
