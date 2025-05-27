import { Routes, Route } from "react-router-dom";

// Importa as páginas que o aluno pode acessar

import Student from "../pages/student";
// ... outras páginas específicas

export function StudentRoutes() {
  return (
    <Routes>
      <Route path="/" element={<Student />} />

      {/* outras rotas, como tarefas, histórico, etc */}
    </Routes>
  );
}
