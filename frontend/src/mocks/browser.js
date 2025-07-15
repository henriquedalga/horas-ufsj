// src/mocks/browser.js
import { setupWorker } from "msw/browser";

// Importe seus handlers do arquivo mock.faker.js
// O caminho pode precisar de ajuste dependendo de onde você colocou o arquivo.
import { handlers } from "./faker.mock.js";

// Configura o worker com os handlers importados
export const worker = setupWorker(...handlers);
