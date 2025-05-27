import { Routes, Route, Navigate } from "react-router-dom";
import PrivateRoute from "./PrivateRoute";
import { StudentRoutes } from "./StudentRoutes";
import { AdminRoutes } from "./AdminRoutes";
import Initial from "../pages/Initial";
// import Unauthorized from "../pages/Unauthorized";
// import NotFound from "../pages/NotFound";

export default function AppRoutes() {
  return (
    <Routes>
      <Route path="/main" element={<Initial />} />
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
