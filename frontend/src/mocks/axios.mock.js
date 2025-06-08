import MockAdapter from "axios-mock-adapter";

import api from "../services/api";

const mock = new MockAdapter(api, { delayResponse: 500 });

// Dados mockados para autenticação
const mockUsers = [
  {
    username: "aluno",
    password: "1234",
    role: "ALUNO",
    authToken: "fake-token-abc123",
  },
  {
    username: "admin",
    password: "1234",
    role: "FUNCIONARIO",
    authToken: "fake-token-xyz789",
  },
];

const mockAlunos = [
  { id: 1, nome: "João Silva", status: "PENDENTE" },
  { id: 2, nome: "Maria Oliveira", status: "REPROVADO" },
  { id: 3, nome: "Carlos Souza", status: "APROVADO" },
];

const mockAlunoDetails = {
  1: {
    id: 1,
    nome: "João Silva",
    curso: "Engenharia Civil",
    email: "joao@exemplo.com",
    arquivos: [
      {
        id: "arq1",
        nome: "relatorio_estagio.pdf",
        status: "PENDENTE",
        comentario: "",
        dataEnvio: "2024-05-01",
      },
      {
        id: "arq2",
        nome: "certificado_curso.pdf",
        status: "APROVADO",
        comentario: "Documento válido e legível.",
        dataEnvio: "2024-04-15",
      },
    ],
  },
  2: {
    id: 2,
    nome: "Maria Oliveira",
    curso: "Design Gráfico",
    email: "maria@exemplo.com",
    arquivos: [
      {
        id: "arq3",
        nome: "portfolio_design.pdf",
        status: "REJEITADO",
        comentario: "O arquivo está incompleto. Favor reenviar.",
        dataEnvio: "2024-05-10",
      },
    ],
  },
  3: {
    id: 3,
    nome: "Carlos Souza",
    curso: "Ciência da Computação",
    email: "carlos@exemplo.com",
    arquivos: [
      {
        id: "arq4",
        nome: "projeto_open_source.zip",
        status: "PENDENTE",
        comentario: "",
        dataEnvio: "2024-05-20",
      },
      {
        id: "arq5",
        nome: "comprovante_evento.pdf",
        status: "PENDENTE",
        comentario: "",
        dataEnvio: "2024-05-22",
      },
    ],
  },
  4: {
    id: 4,
    nome: "Ana Costa",
    curso: "Administração",
    email: "ana@exemplo.com",
    arquivos: [
      {
        id: "arq6",
        nome: "certificado_voluntariado.pdf",
        status: "APROVADO",
        comentario: "Parabéns pela iniciativa!",
        dataEnvio: "2024-04-28",
      },
    ],
  },
};

// ✅ Setup de todas as rotas mockadas
export function setupMockRoutes() {
  // POST /api/auth/signin
  mock.onPost("/auth/signin").reply((config) => {
    const { username, password } = JSON.parse(config.data);
    const user = mockUsers.find(
      (u) => u.username === username && u.password === password
    );

    if (user) {
      return [
        200,
        {
          username: user.username,
          role: user.role,
          authToken: user.authToken,
        },
      ];
    }

    return [401, { message: "Usuário ou senha inválidos" }];
  });

  // GET /api/alunos
  mock.onGet("/alunos").reply(200, mockAlunos);

  // GET /api/aluno/:id
  mock.onGet(new RegExp("/aluno/\\d+")).reply((config) => {
    const id = config.url.split("/").pop();
    const aluno = mockAlunoDetails[id];

    if (aluno) {
      return [200, aluno];
    }

    return [404, { message: "Aluno não encontrado" }];
  });
}

export default mock;
