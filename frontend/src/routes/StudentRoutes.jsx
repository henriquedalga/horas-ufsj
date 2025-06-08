import { Route, Routes } from "react-router-dom";

import Student from "../pages/Student";
// ... outras páginas específicas

export function StudentRoutes() {
  return (
    <Routes>
      <Route path="/" element={<Student />} />

      {/* outras rotas, como tarefas, histórico, etc */}
    </Routes>
  );
}
