import api from "./api";
import storageService from "./storage.service";

class AuthService {
  async login(email, password) {
    const data = await api("/auth/signin-admin", {
      method: "POST",
      body: JSON.stringify({ 
      email: email, 
      senha: password // Alterado para "senha" para combinar com o backend
    }),
    });

    const { token, ...userData } = data;
    storageService.saveUserData(userData);
    storageService.saveAuthToken(token);

    return data;
  }

  async validateSsoUuid(uuid) {
    // Nova rota para validação do uuid
    return await api("/auth/sso-validate", {
      method: "POST",
      body: JSON.stringify({ uuid: uuid }),
    });
  }
  // Login: envia email e password, recebe token e dados do usuário
  // async login(email, password) {
  //   const response = await api.post("/auth/signin", { email, password });
  //   console.log("Login response data:", response.data);
  //   const { email: usern, name, role, authToken } = response.data;

  //   // Salva dados no sessionStorage para manter sessão
  //   sessionStorage.setItem(
  //     "user",
  //     JSON.stringify({ email: usern, name, role, authToken })
  //   );

  //   return response.data;
  // }

  // Logout: remove dados da sessão local
  logout() {
    storageService.clearAll();
  }

  // Retorna usuário logado (se existir)
  getCurrentUser() {
    return storageService.getUserData();
  }

  // Verifica se usuário está autenticado
  isAuthenticated() {
    return !!storageService.getAuthToken();
  }

  // Método auxiliar para facilitar requisições futuras
  async fetchWithAuth(url, options = {}) {
    const user = this.getCurrentUser();
    const headers = options.headers || {};

    if (user && user.authToken) {
      headers.Authorization = `Bearer ${user.authToken}`;
    }

    return fetch(`${this.baseURL}${url}`, {
      ...options,
      headers: {
        'Content-Type': 'application/json',
        ...headers
      }
    });
  }
}

export default new AuthService();
