/**
 * Objeto centralizado com todas as chaves usadas no Session Storage.
 * Usar este objeto em vez de strings hardcoded previne erros de digitação
 * e facilita a manutenção em todo o projeto.
 *
 * O prefixo 'meuapp_' ajuda a evitar colisões com chaves de outras bibliotecas ou extensões.
 */
export const STORAGE_KEYS = Object.freeze({
  AUTH_TOKEN: "meuapp_auth_token",
  USER_DATA: "meuapp_user_data",
  // Adicione outras chaves aqui conforme a necessidade
  // ex: THEME: 'meuapp_theme',
});
