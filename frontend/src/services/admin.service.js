import api from "./api";

class AdminService {
  // Cria novo usuário
  async createUser(userData) {
    const response = await api.post("/admin/users", userData);
    return response.data;
  }

  // Atualiza usuário existente
  async updateUser(userId, userData) {
    const response = await api.put(`/admin/users/${userId}`, userData);
    return response.data;
  }

  // Deleta usuário
  async deleteUser(userId) {
    const response = await api.delete(`/admin/users/${userId}`);
    return response.data;
  }

  // Lista todos usuários
  async listUsers() {
    const response = await api.get("/admin/users");
    return response.data;
  }
}
export default new AdminService();