import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

import AuthService from "../services/auth.service";
import storageService from "../services/storage.service";

export default function AuthCallback() {
  const navigate = useNavigate();
  const [error, setError] = useState(null);

  useEffect(() => {
    // 1. Extrai o `uuid` dos parâmetros da URL
    const params = new URLSearchParams(window.location.search);
    const uuid = params.get("uuid");

    const validateUuid = async (ssoUuid) => {
      try {
        // 2. Envia o uuid para o nosso backend (mock) para validação
        const sessionData = await AuthService.validateSsoUuid(ssoUuid);

        // 3. Salva o token de sessão retornado pelo nosso backend
        storageService.saveAuthToken(sessionData.token);
        storageService.saveUserData(sessionData.user);

        // 4. Redireciona para a página principal do admin
        navigate("/student");
      } catch (err) {
        setError(err.message || "Falha na validação do SSO.");
      }
    };

    if (uuid) {
      validateUuid(uuid);
    } else {
      setError("Nenhum 'uuid' de autenticação encontrado na URL.");
    }
  }, [navigate]);

  if (error) {
    return (
      <div>
        <h1>Erro na Autenticação</h1>
        <p style={{ color: "red" }}>{error}</p>
      </div>
    );
  }

  return <div>Autenticando, por favor aguarde...</div>;
}
