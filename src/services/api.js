import axios from "axios";

const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL,
});

api.interceptors.request.use((config) => {
  const token = sessionStorage.getItem("authToken");
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// export const getAlunos = async () => {
//   console.log("Buscando alunos...");
//   const response = await api.get("/alunos");
//   console.log("Alunos no get alunos:", response.data);
//   return response.data;
// };

// export const getAlunoById = async (id) => {
//   const response = await api.get(`/aluno/${id}`);
//   return response.data;
// };

export default api;
