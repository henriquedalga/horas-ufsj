import { Navigate } from "react-router-dom";

import storageService from "../services/storage.service";

export default function PrivateRoute({ children }) {
  const isAuth = !!storageService.getAuthToken();
  console.log("isAuth", isAuth);
  return isAuth ? children : <Navigate to="/login" />;
}
