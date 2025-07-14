import api from "./api";

class AdminService {
  async getStudents() {
    const response = await api.get("/alunos");
    return response.data;
  }

  async getStudentById(id) {
    const response = await api.get(`/aluno/${id}`);
    return response.data;
  }

  async getComplementarById(id) {
    const response = await api.get(`/complementar/${id}`);
    return response.data;
  }

  async getExtensaoById(id) {
    const response = await api.get(`/extensao/${id}`);
    return response.data;
  }

  async getComplementar() {
    const response = await api.get("/complementar");
    return response.data;
  }

  async getExtensao() {
    const response = await api.get("/extensao");
    return response.data;
  }

  async postComplementarById(id, data) {
    const response = await api.post(`/complementar/${id}`, data);
    return response.data;
  }

  async postExtensaoById(id, data) {
    const response = await api.post(`/extensao/${id}`, data);
    return response.data;
  }

  // // Cria novo usuário
  // async createUser(userData) {
  //   const response = await api.post("/admin/users", userData);
  //   return response.data;
  // }

  // // Atualiza usuário existente
  // async updateUser(userId, userData) {
  //   const response = await api.put(`/admin/users/${userId}`, userData);
  //   return response.data;
  // }

  // // Deleta usuário
  // async deleteUser(userId) {
  //   const response = await api.delete(`/admin/users/${userId}`);
  //   return response.data;
  // }

  // // Lista todos usuários
  // async listUsers() {
  //   const response = await api.get("/admin/users");
  //   return response.data;
  // }
}
export default new AdminService();
