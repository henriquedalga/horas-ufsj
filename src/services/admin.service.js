import api from "./api";

class AdminService {
  async getStudentById(id) {
    return await api(`/aluno/${id}`, { method: "GET" });
  }

  async getComplementarById(id) {
    return await api(`/complementar/${id}`, { method: "GET" });
  }

  async getExtensaoById(id) {
    return await api(`/extensao/${id}`, { method: "GET" });
  }

  async getComplementar() {
    return await api("/complementar", { method: "GET" });
  }

  async getExtensao() {
    return await api("/extensao", { method: "GET" });
  }

  async postComplementarById(id, data) {
    return await api(`/complementar/${id}`, {
      method: "POST",
      body: JSON.stringify(data),
      headers: {
        "Content-Type": "application/json",
      },
    });
  }

  async postExtensaoById(id, data) {
    return await api(`/extensao/${id}`, {
      method: "POST",
      body: JSON.stringify(data),
      headers: {
        "Content-Type": "application/json",
      },
    });
  }

  async getAdmins() {
    return await api("/admin", { method: "GET" });
  }

  async addAdmin(adminData) {
    return await api("/admin", {
      // ou a rota que você definir
      method: "POST",
      body: JSON.stringify(adminData),
    });
  }

  // --- FUNÇÃO PARA BUSCAR UM ADMIN POR ID ---
  /**
   * Busca os dados de um único administrador/moderador pelo seu ID.
   * @param {string} id - O ID do administrador.
   */
  async getAdminById(id) {
    // A rota é dinâmica para incluir o ID
    return await api(`/admin/${id}`, { method: "GET" });
  }

  // --- FUNÇÃO PARA EDITAR (ATUALIZAR) UM ADMIN ---
  /**
   * Envia os dados atualizados de um administrador.
   * @param {string} id - O ID do administrador a ser atualizado.
   * @param {object} adminData - O objeto com os novos dados (ex: { nome, email, role }).
   */
  async updateAdmin(id, adminData) {
    // Usamos o método 'PUT' para atualizar um recurso existente
    return await api(`/admin/${id}`, {
      method: "PUT",
      body: JSON.stringify(adminData),
    });
  }
}

export default new AdminService();

// import api from "./api";

// class AdminService {
//   async getStudents() {
//     const response = await api.get("/alunos");
//     return response.data;
//   }

//   async getStudentById(id) {
//     const response = await api.get(`/aluno/${id}`);
//     return response.data;
//   }

//   async getComplementarById(id) {
//     const response = await api.get(`/complementar/${id}`);
//     return response.data;
//   }

//   async getExtensaoById(id) {
//     const response = await api.get(`/extensao/${id}`);
//     return response.data;
//   }

//   async getComplementar() {
//     const response = await api.get("/complementar");
//     return response.data;
//   }

//   async getExtensao() {
//     const response = await api.get("/extensao");
//     return response.data;
//   }

//   async postComplementarById(id, data) {
//     const response = await api.post(`/complementar/${id}`, data);
//     return response.data;
//   }

//   async postExtensaoById(id, data) {
//     const response = await api.post(`/extensao/${id}`, data);
//     return response.data;
//   }
// }
// export default new AdminService();
