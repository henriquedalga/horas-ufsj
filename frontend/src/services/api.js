import axios from "axios";


const api = axios.create({
  baseURL: "http://localhost:8080", // URL base da API
  timeout: 5000, // Define um timeout para requisições
  headers: {
    'Content-Type': 'application/json'
  }
});

api.interceptors.request.use((config) => {
  const token = sessionStorage.getItem("authToken");
  console.log('Requisição enviada para:', config.baseURL + config.url);
  console.log('Método:', config.method.toUpperCase());
  console.log('Dados:', config.data);
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
},
error => {
    console.error('Erro ao enviar requisição:', error);
    return Promise.reject(error);
  });
api.interceptors.response.use(
  response => {
    console.log('Resposta recebida de:', response.config.url);
    console.log('Status:', response.status);
    console.log('Dados:', response.data);
    return response;
  },
  error => {
    console.error('Erro na resposta:', error.response?.status, error.response?.data);
    return Promise.reject(error);
  }
);
export const getAlunos = async () => {
  console.log("Buscando alunos...");
  const response = await api.get("/alunos");
  console.log("Alunos no get alunos:", response.data);
  return response.data;
};

export const getAlunoById = async (id) => {
  const response = await api.get(`/aluno/${id}`);
  return response.data;
};

export default api;
