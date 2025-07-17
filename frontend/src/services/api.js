import storageService from "./storage.service";

const API_URL = import.meta.env.VITE_API_URL;

const getAuthHeader = () => {
  const token = storageService.getAuthToken();
  return token ? { Authorization: token } : {};
};

const api = async (endpoint, options = {}) => {
  const headers = {
    "Content-Type": "application/json",
    ...getAuthHeader(),
    ...options.headers,
  };

  const response = await fetch(`${API_URL}${endpoint}`, {
    ...options,
    headers,
  });

  if (!response.ok) {
    const errorData = await response.json().catch(() => ({}));
    throw new Error(
      errorData.message || `Erro ${response.status} na requisição`
    );
  }

  return response.json(); // já retorna o body convertido
};

export default api;

// import axios from "axios";

// const api = axios.create({
//   baseURL: import.meta.env.VITE_API_URL,
// });

// api.interceptors.request.use((config) => {
//   const token = sessionStorage.getItem("authToken");
//   if (token) {
//     config.headers.Authorization = `Bearer ${token}`;
//   }
//   return config;
// });

// // export const getAlunos = async () => {
// //   console.log("Buscando alunos...");
// //   const response = await api.get("/alunos");
// //   console.log("Alunos no get alunos:", response.data);
// //   return response.data;
// // };

// // export const getAlunoById = async (id) => {
// //   const response = await api.get(`/aluno/${id}`);
// //   return response.data;
// // };

// export default api;
