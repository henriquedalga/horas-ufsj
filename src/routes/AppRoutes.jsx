import { Navigate, Route, Routes } from "react-router-dom";

import AuthCallback from "../pages/AuthCallback";
import Initial from "../pages/Initial";
import { AdminRoutes } from "./AdminRoutes";
import PrivateRoute from "./PrivateRoute";
import { StudentRoutes } from "./StudentRoutes";
// import Unauthorized from "../pages/Unauthorized";
// import NotFound from "../pages/NotFound";

export default function AppRoutes() {
  return (
    <Routes>
      <Route path="/main" element={<Initial />} />
      <Route path="/login" element={<AuthCallback />} />
      {/* <Route path="/unauthorized" element={<Unauthorized />} />
        <Route path="*" element={<NotFound />} /> */}
      <Route
        path="/student/*"
        element={
          <PrivateRoute>
            <StudentRoutes />
          </PrivateRoute>
        }
      />

      <Route
        path="/admin/*"
        element={
          <PrivateRoute>
            <AdminRoutes />
          </PrivateRoute>
        }
      />

      <Route path="*" element={<Navigate to="/main" />} />
    </Routes>
  );
}
