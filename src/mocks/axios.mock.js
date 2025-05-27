import axios from "axios";
import MockAdapter from "axios-mock-adapter";

const mock = new MockAdapter(axios, { delayResponse: 500 }); // simula atraso 500ms

// Simula POST /api/auth/signin
mock.onPost("http://localhost:8080/api/auth/signin").reply((config) => {
  const { username, password } = JSON.parse(config.data);

  if (username === "aluno" && password === "1234") {
    return [
      200,
      {
        username: "aluno",
        authToken: "fake-token-abc123",
        role: "ALUNO",
      },
    ];
  }

  if (username === "admin" && password === "1234") {
    return [
      200,
      {
        username: "admin",
        authToken: "fake-token-xyz789",
        role: "FUNCIONARIO",
      },
    ];
  }

  return [401, { message: "Usuário ou senha inválidos" }];
});

// Opcional: simular outros endpoints se desejar
// mock.onPost("http://localhost:8080/api/auth/signup").reply(...);

export default mock;
