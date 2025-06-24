import MockAdapter from "axios-mock-adapter";

import api from "../services/api";

const mock = new MockAdapter(api, { delayResponse: 500 });

// Dados mockados para autenticação
const mockUsers = [
  {
    username: "aluno",
    name: "João da Silva",
    password: "1234",
    role: "ALUNO",
    authToken: "fake-token-abc123",
  },
  {
    username: "admin",
    name: "Administrador",
    password: "1234",
    role: "FUNCIONARIO",
    authToken: "fake-token-xyz789",
  },
];

// Estrutura comum usada para ambas as categorias
const mockComplementar = [
  { id: 1, nome: "João Silva", status: "PENDENTE" },
  { id: 2, nome: "Maria Oliveira", status: "REPROVADO" },
];

const mockExtensao = [
  { id: 3, nome: "Carlos Souza", status: "APROVADO" },
  { id: 4, nome: "Ana Costa", status: "APROVADO" },
];

const mockDetalhes = {
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
        comentario: "Arquivo incompleto.",
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
//   // POST /api/auth/signin
//   mock.onPost("/auth/signin").reply((config) => {
//     const { username, password } = JSON.parse(config.data);
//     const user = mockUsers.find(
//       (u) => u.username === username && u.password === password
//     );

//     if (user) {
//       return [
//         200,
//         {
//           username: user.username,
//           role: user.role,
//           authToken: user.authToken,
//         },
//       ];
//     }

//     return [401, { message: "Usuário ou senha inválidos" }];
//   });

  // GET /complementar
  mock.onGet("/complementar").reply(200, mockComplementar);

  // GET /extensao
  mock.onGet("/extensao").reply(200, mockExtensao);

  // GET /complementar/:id
  mock.onGet(new RegExp("/complementar/\\d+")).reply((config) => {
    const id = config.url.split("/").pop();
    const data = mockDetalhes[id];
    return data
      ? [200, data]
      : [404, { message: "Complementar não encontrado" }];
  });

  // GET /extensao/:id
  mock.onGet(new RegExp("/extensao/\\d+")).reply((config) => {
    const id = config.url.split("/").pop();
    const data = mockDetalhes[id];
    return data ? [200, data] : [404, { message: "Extensão não encontrada" }];
  });

  // POST /complementar/:id
  mock.onPost(new RegExp("/complementar/\\d+")).reply((config) => {
    const id = config.url.split("/").pop();
    const payload = JSON.parse(config.data);
    console.log(`Recebido POST complementar/${id}`, payload);

    // Simula atualização no mockDetalhes[id]
    if (mockDetalhes[id]) {
      mockDetalhes[id].arquivos = payload.respostas;
      return [200, { message: "Complementar atualizado com sucesso" }];
    }
    return [404, { message: "Complementar não encontrado" }];
  });

  // POST /extensao/:id
  mock.onPost(new RegExp("/extensao/\\d+")).reply((config) => {
    const id = config.url.split("/").pop();
    const payload = JSON.parse(config.data);
    console.log(`Recebido POST extensao/${id}`, payload);

    if (mockDetalhes[id]) {
      mockDetalhes[id].arquivos = payload.respostas;
      return [200, { message: "Extensão atualizada com sucesso" }];
    }
    return [404, { message: "Extensão não encontrada" }];
  });
}

export default mock;
