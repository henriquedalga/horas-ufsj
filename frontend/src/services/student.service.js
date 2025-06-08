import api from "./api";

class StudentService {
  // Cria novo aluno
  async createStudent(studentData) {
    const response = await api.post("/students", studentData);
    return response.data;
  }

  // Atualiza aluno existente
  async updateStudent(studentId, studentData) {
    const response = await api.put(`/students/${studentId}`, studentData);
    return response.data;
  }

  // Deleta aluno
  async deleteStudent(studentId) {
    const response = await api.delete(`/students/${studentId}`);
    return response.data;
  }

  // Lista todos alunos
  async listStudents() {
    const response = await api.get("/students");
    return response.data;
  }
}
export default new StudentService();
