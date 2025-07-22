import api from "./api";

class StudentService {
  async getFilesByStudentId(studentId) {
    return await api(`/${studentId}/files`, {
      method: "GET",
    });
  }

  async postFilesExtensao(fileData) {
    return await api(`/extensao/files`, {
      method: "POST",
      body: JSON.stringify(fileData),
      headers: {
        "Content-Type": "application/json",
      },
    });
  }

  async postFilesComplementar(fileData) {
    return await api(`/complementa/files`, {
      method: "POST",
      body: JSON.stringify(fileData),
      headers: {
        "Content-Type": "application/json",
      },
    });
  }

  getSolicitacaoExtensao() {
    // O wrapper 'api.js' adiciona o token de autorização automaticamente
    return api("/aluno/solicitacao/extensao");
  }

  getSolicitacaoComplementar() {
    return api("/aluno/solicitacao/complementar");
  }

  // --- NOVO MÉTODO PARA EXCLUIR ARQUIVO ---
  /**
   * Envia uma requisição para excluir um arquivo específico pelo seu ID.
   * @param {string} fileId - O ID do arquivo a ser excluído.
   */
  async deleteFileById(fileId) {
    // Usamos um template literal para construir a URL dinâmica com o ID do arquivo.
    // O método HTTP correto para exclusão é o 'DELETE'.
    return await api(`/excluir/${fileId}`, {
      method: "DELETE",
    });
  }
}

export default new StudentService();

// import api from "./api";

// class StudentService {
//   async getFilesByStudentId(studentId) {
//     const response = await api.get(`/${studentId}/files`);
//     return response.data;
//   }

//   async postFileToStudent(studentId, fileData) {
//     const response = await api.post(`/${studentId}/files`, fileData);
//     return response.data;
//   }
//   // // Cria novo aluno
//   // async createStudent(studentData) {
//   //   const response = await api.post("/students", studentData);
//   //   return response.data;
//   // }

//   // // Atualiza aluno existente
//   // async updateStudent(studentId, studentData) {
//   //   const response = await api.put(`/students/${studentId}`, studentData);
//   //   return response.data;
//   // }

//   // // Deleta aluno
//   // async deleteStudent(studentId) {
//   //   const response = await api.delete(`/students/${studentId}`);
//   //   return response.data;
//   // }

//   // // Lista todos alunos
//   // async listStudents() {
//   //   const response = await api.get("/students");
//   //   return response.data;
//   // }
// }
// export default new StudentService();
