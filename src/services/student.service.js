import api from "./api";

class StudentService {
  async getFilesByStudentId(studentId) {
    return await api(`/${studentId}/files`, {
      method: "GET",
    });
  }

  async postFileToStudent(studentId, fileData) {
    return await api(`/${studentId}/files`, {
      method: "POST",
      body: JSON.stringify(fileData),
      headers: {
        "Content-Type": "application/json",
      },
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
