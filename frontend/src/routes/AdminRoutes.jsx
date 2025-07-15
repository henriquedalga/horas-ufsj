import { Route, Routes } from "react-router-dom";

import Admin from "../pages/Admin";

export function AdminRoutes() {
  return (
    <Routes>
      <Route path="/" element={<Admin />} />
      {/* outras rotas, como tarefas, histórico, etc */}
    </Routes>
  );
}
