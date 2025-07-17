import { STORAGE_KEYS } from "../config/storageKeys";

/**
 * Serviço que abstrai a interação com o sessionStorage.
 * Centraliza a lógica de salvar, obter, remover e fazer o parse de JSON,
 * garantindo consistência e tratamento de erros.
 */
const storageService = {
  // --- Métodos específicos para Autenticação ---

  /**
   * Salva o token de autenticação na sessão.
   * @param {string} token - O token a ser salvo.
   */
  saveAuthToken(token) {
    sessionStorage.setItem(STORAGE_KEYS.AUTH_TOKEN, token);
  },

  /**
   * Obtém o token de autenticação da sessão.
   * @returns {string | null} O token salvo ou null se não existir.
   */
  getAuthToken() {
    return sessionStorage.getItem(STORAGE_KEYS.AUTH_TOKEN);
  },

  /**
   * Salva os dados do usuário logado na sessão.
   * @param {object} userData - O objeto com os dados do usuário.
   */
  saveUserData(userData) {
    // Sempre usamos JSON.stringify para salvar objetos
    sessionStorage.setItem(STORAGE_KEYS.USER_DATA, JSON.stringify(userData));
  },

  /**
   * Obtém os dados do usuário da sessão.
   * @returns {object | null} O objeto do usuário ou null se não existir ou for inválido.
   */
  getUserData() {
    const data = sessionStorage.getItem(STORAGE_KEYS.USER_DATA);
    if (!data) {
      return null;
    }
    try {
      // Usamos um try-catch para o caso de o JSON salvo ser inválido
      return JSON.parse(data);
    } catch (error) {
      console.error(
        "Erro ao fazer parse dos dados do usuário no sessionStorage:",
        error
      );
      this.clearAll(); // Limpa tudo em caso de dado corrompido
      return null;
    }
  },

  /**
   * Limpa todos os dados relacionados à sessão do usuário.
   */
  clearAll() {
    sessionStorage.removeItem(STORAGE_KEYS.AUTH_TOKEN);
    sessionStorage.removeItem(STORAGE_KEYS.USER_DATA);
  },
};

// Exportamos o serviço como um objeto para ser usado como um singleton
export default Object.freeze(storageService);
