// Importa apenas outros serviços se necessário, não precisamos do axios
// import api from "./api"; - não é mais necessário

class AuthService {
  // URL base para todas as requisições
  baseURL = "http://localhost:8080";
  
  // Login: envia username e password, recebe token e dados do usuário
  async login(username, password, tipo) {
    console.log(`Tentando login com: ${username}`);
    let loginRoute;
    if (tipo === "student") {
      console.log("Tipo de usuário: estudante");
      loginRoute = "/auth/signin-student";

          try {
      // Configuração da requisição fetch
      const response = await fetch(`${this.baseURL}${loginRoute}`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Accept': 'application/json'
        },
        body: JSON.stringify({ cpf: username, senha: password })
      });

      // Log para debug
      console.log('Status da resposta:', response.status);
      
      // Verifica se a resposta foi bem-sucedida
      if (!response.ok) {
        throw new Error(`Erro HTTP: ${response.status}`);
      }
      
      // Parse da resposta JSON
      const data = await response.json();
      console.log("Resposta da API:", data);
      
      const { authToken, username: name, role } = data;
      
      // Salva dados no sessionStorage
      sessionStorage.setItem(
        "user",
        JSON.stringify({ authToken, username: name, role })
      );
      
      return data;
    } catch (error) {
      console.error("Erro na autenticação:", error.message);
      throw error;
    }
      
    } else {
      console.log("Tipo de usuário: administrador");
      loginRoute = "/auth/signin-admin";

          try {
      // Configuração da requisição fetch
      const response = await fetch(`${this.baseURL}${loginRoute}`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Accept': 'application/json'
        },
        body: JSON.stringify({ email: username, senha: password })
      });

      // Log para debug
      console.log('Status da resposta:', response.status);
      
      // Verifica se a resposta foi bem-sucedida
      if (!response.ok) {
        throw new Error(`Erro HTTP: ${response.status}`);
      }
      
      // Parse da resposta JSON
      const data = await response.json();
      console.log("Resposta da API:", data);
      
      const { authToken, username: name, role } = data;
      
      // Salva dados no sessionStorage
      sessionStorage.setItem(
        "user",
        JSON.stringify({ authToken, username: name, role })
      );
      
      return data;
    } catch (error) {
      console.error("Erro na autenticação:", error.message);
      throw error;
    }
    }

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