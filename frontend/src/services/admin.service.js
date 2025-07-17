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
    return await api("solicitacoes/complementar", { method: "GET" });
  }

  async getExtensao() {
    return await api("/solicitacoes/extensao", { method: "GET" });
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
    return await api("/admins", {
      // ou a rota que você definir
      method: "POST",
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
