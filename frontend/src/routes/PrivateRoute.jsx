import { Navigate } from "react-router-dom";

export default function PrivateRoute({ children }) {
  const user = JSON.parse(sessionStorage.getItem("user"));
  const isAuth = !!(user && user.authToken);
  console.log("user", user);
  console.log("user.authToken", user?.authToken);
  console.log("isAuth", isAuth);
  return isAuth ? children : <Navigate to="/login" />;
}
