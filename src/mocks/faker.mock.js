import { faker } from "@faker-js/faker/locale/pt_BR";
import { http, HttpResponse } from "msw";

const API_URL = import.meta.env.VITE_API_URL;

// --- 1. Configuração do Mock ---

// Credenciais fixas para o login do administrador
const ADMIN_USER = {
  email: "admin@plataforma.com",
  password: "admin123",
  name: "Admin da Plataforma", // <-- Adicionado
  role: "mod", // <-- Adicionado
};
const FAKE_AUTH_TOKEN = "Bearer fake-super-secret-admin-token-12345";

// --- 2. Geradores de Dados Aleatórios (usados pelo dashboard) ---

/** Gera um usuário aleatório completo */
const createRandomHoras = () => ({
  id: faker.number.int({ min: 1, max: 10000 }),
  nome: faker.person.fullName(),
  status: faker.helpers.arrayElement(["PENDENTE", "APROVADO", "REPROVADO"]),
});

const createRandomAdmin = () => ({
  id: faker.number.int({ min: 1, max: 10000 }),
  nome: faker.person.fullName(),
  email: faker.internet.email(),
  role: faker.helpers.arrayElement(["admin", "mod"]),
});

// --- 3. Definição dos Handlers da API ---

/** Handler compartilhado para as rotas /admin e /mod */
const adminModHandler = ({ request }) => {
  const authHeader = request.headers.get("Authorization");

  if (authHeader !== FAKE_AUTH_TOKEN) {
    return HttpResponse.json(
      { message: "Acesso não autorizado. Token inválido ou ausente." },
      { status: 401 }
    );
  }

  const newAdmins = faker.helpers.multiple(createRandomAdmin, {
    count: faker.number.int({ min: 50, max: 500 }),
  });

  return HttpResponse.json(newAdmins);
};

/** Handler compartilhado para as rotas /complementar e /extensao */
const horasHandler = ({ request }) => {
  const authHeader = request.headers.get("Authorization");

  if (authHeader !== FAKE_AUTH_TOKEN) {
    return HttpResponse.json(
      { message: "Acesso não autorizado. Token inválido ou ausente." },
      { status: 401 }
    );
  }
  const newHoras = faker.helpers.multiple(createRandomHoras, {
    count: faker.number.int({ min: 50, max: 500 }),
  });

  return HttpResponse.json(newHoras);
};

export const handlers = [
  /**
   * ROTA DE LOGIN: Verifica credenciais fixas.
   */
  http.post(`${API_URL}/auth/signin`, async ({ request }) => {
    const { email, password } = await request.json();

    if (email === ADMIN_USER.email && password === ADMIN_USER.password) {
      // Sucesso: retorna o objeto EXATO que o frontend espera
      return HttpResponse.json({
        email: ADMIN_USER.email,
        name: ADMIN_USER.name,
        role: ADMIN_USER.role,
        token: FAKE_AUTH_TOKEN, // O token continua o mesmo
      });
    }

    // Falha: retorna erro de não autorizado
    return HttpResponse.json(
      { message: "Credenciais de administrador inválidas." },
      { status: 401 }
    );
  }),

  // ROTAS DE HORAS (complementar e extensao)
  // O aviso seria o mesmo aqui, então aplicamos a mesma correção.
  http.get(`${API_URL}/complementar`, horasHandler),
  http.get(`${API_URL}/extensao`, horasHandler),

  // ROTAS DE ADMINS (admin e mod) - CORRIGIDO
  // Agora temos um handler explícito para cada rota, ambos usando a mesma lógica.
  http.get(`${API_URL}/admin`, adminModHandler),
  http.get(`${API_URL}/mod`, adminModHandler),
];
