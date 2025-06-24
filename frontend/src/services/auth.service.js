import api from "./api";

class AuthService {
  // Login: envia username e password, recebe token e dados do usuário
  async login(username, password) {
    const response = await api.post("/auth/signin", { username, password });
    console.log("Login response data:", response.data);
    const { username: usern, name, role, authToken } = response.data;

    // Salva dados no sessionStorage para manter sessão
    sessionStorage.setItem(
      "user",
      JSON.stringify({ username: usern, name, role, authToken })
    );

    return response.data;
  }

  // Logout: remove dados da sessão local
  logout() {
    sessionStorage.removeItem("user");
  }

  // Retorna usuário logado (se existir)
  getCurrentUser() {
    const user = sessionStorage.getItem("user");
    return user ? JSON.parse(user) : null;
  }

  // Verifica se usuário está autenticado
  isAuthenticated() {
    const user = this.getCurrentUser();
    return !!(user && user.authToken);
  }
}

export default new AuthService();
