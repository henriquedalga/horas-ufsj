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
  status: faker.helpers.arrayElement(["ABERTO", "FECHADO"]),
});

const createRandomAdmin = () => ({
  id: faker.number.int({ min: 1, max: 10000 }),
  nome: faker.person.fullName(),
  email: faker.internet.email(),
  role: faker.helpers.arrayElement(["admin", "mod"]),
});

// Gerador para um único arquivo. Agora ele pode receber um status específico.
const createFakeFile = (statusDefinido) => {
  // Se um status for passado, usa ele. Senão, escolhe aleatoriamente.
  const status =
    statusDefinido ||
    faker.helpers.arrayElement(["APROVADO", "REPROVADO", "PENDENTE"]);
  let feedback = null;

  if (status === "REPROVADO") {
    feedback = faker.lorem.sentence();
  }

  return {
    id: `arq-${faker.string.alphanumeric(10)}`,
    nome: faker.system.commonFileName("pdf"),
    status: status,
    feedback: feedback,
    // ... outros dados do arquivo
  };
};

// GERADOR PRINCIPAL: Agora ele constrói a resposta com base nas suas regras de negócio.
const createFakeActivityResponse = (id, tipo, statusPedidoDesejado) => {
  let arquivos = [];

  switch (statusPedidoDesejado) {
    case "FECHADO":
      // Regra: Todos os arquivos devem estar APROVADOS.
      arquivos = faker.helpers.multiple(() => createFakeFile("APROVADO"), {
        count: { min: 1, max: 3 },
      });
      break;

    case "ABERTO": {
      // Regra: Ou não tem arquivos, ou tem pelo menos um REPROVADO.
      // Vamos sortear um dos dois cenários.
      const comArquivosReprovados = faker.datatype.boolean();
      if (comArquivosReprovados) {
        arquivos.push(createFakeFile("REPROVADO")); // Garante pelo menos um reprovado
        arquivos.push(createFakeFile("APROVADO"));
        arquivos.push(createFakeFile("PENDENTE"));
      }
      // Se não, o array 'arquivos' fica vazio, o que também significa ABERTO.
      break;
    }
  }

  return {
    id: id,
    tipo: tipo,
    statusPedido: statusPedidoDesejado,
    arquivos: arquivos,
    // ... outros dados como aluno e dataEnvio
  };
};

const handleSolicitacaoAluno = ({ request, params }) => {
  // 1. A única verificação é a existência do token
  const authHeader = request.headers.get("Authorization");
  if (!authHeader) {
    return HttpResponse.json(
      { message: "Token não fornecido." },
      { status: 401 }
    );
  }

  // 2. SORTEIA ALEATORIAMENTE um dos três status de pedido possíveis
  const statusPossiveis = ["FECHADO", "ABERTO"];
  const statusSorteado = faker.helpers.arrayElement(statusPossiveis);

  console.log(
    `[MSW] Token verificado. Gerando uma resposta aleatória com status: ${statusSorteado}`
  );

  // 3. Usa o gerador para criar uma resposta COMPLETA e CONSISTENTE com o status sorteado
  // O 'params.tipo' virá da rota que foi chamada ('extensao' ou 'complementar')
  const responseData = createFakeActivityResponse(
    faker.string.uuid(), // Gera um ID de atividade aleatório
    params.tipo,
    statusSorteado
  );

  return HttpResponse.json(responseData);
};
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

  http.post(`${API_URL}/auth/sso-validate`, async ({ request }) => {
    const { uuid } = await request.json();

    if (uuid) {
      console.log(`[MSW] Validando o UUID (falso) ${uuid}`);

      // SUCESSO! Agora usamos o Faker para criar os dados do usuário logado.
      return HttpResponse.json({
        user: {
          id: faker.string.uuid(),
          name: faker.person.fullName(),
          email: faker.internet.email(),
          role: "student", // ou 'mod', etc.
        },
        token: `fake-session-token-${faker.string.uuid()}`, // Token da nossa aplicação
      });
    }

    return HttpResponse.json({ message: "UUID inválido" }, { status: 400 });
  }),

  // As duas rotas agora usam a mesma função de handler dinâmico
  http.get(`${API_URL}/aluno/solicitacao/extensao`, handleSolicitacaoAluno),
  http.get(`${API_URL}/aluno/solicitacao/complementar`, handleSolicitacaoAluno),

  // Adicionamos um :tipo na rota para que o handler saiba qual tipo de atividade gerar
  // Esta é uma forma mais limpa de reutilizar a lógica
  http.get(`${API_URL}/aluno/solicitacao/:tipo`, handleSolicitacaoAluno),
];
