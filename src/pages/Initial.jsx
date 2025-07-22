// import { faker } from "@faker-js/faker";
import { GoogleLogin } from "@react-oauth/google";
import { jwtDecode } from "jwt-decode";
import { useEffect } from "react";
import { useNavigate } from "react-router-dom";

// import Login from "../components/Login";
import AuthService from "../services/auth.service";
import storageService from "../services/storage.service";

export default function Initial() {
  const navigate = useNavigate();

  // const ssoLoginUrl = `${
  //   import.meta.env.VITE_SSO_BASE_URL
  // }/auth/oauth/sso/login?clientId=${import.meta.env.VITE_SSO_CLIENT_ID}`;

  // // A função de simulação para desenvolvimento
  // const handleMockSsoLogin = () => {
  //   const fakeUuid = faker.string.uuid();
  //   console.log(`[MOCK SSO] Simulando redirect com uuid: ${fakeUuid}`);
  //   navigate(`/login?uuid=${fakeUuid}`);
  // };

  // --- FUNÇÃO DE LOGIN PARA ALUNOS ---
  const handleStudentLogin = async (credentialResponse) => {
    const idToken = credentialResponse.credential;
    const userObject = jwtDecode(idToken);

    // 1. Regra de negócio para ALUNOS
    if (!userObject.email.endsWith("@aluno.ufsj.edu.br")) {
      alert(
        "Acesso negado. Por favor, use um e-mail de aluno (@aluno.ufsj.edu.br)."
      );
      return;
    }

    try {
      const sessionData = await AuthService.loginWithGoogle(idToken);
      storageService.saveAuthToken(sessionData.token);
      storageService.saveUserData(sessionData.user);
      navigate("/student"); // Redireciona para a área do aluno
    } catch (error) {
      alert("Ocorreu um erro ao tentar logar no sistema.");
      console.error("Falha no login do aluno com o backend:", error);
    }
  };

  // --- NOVA FUNÇÃO DE LOGIN PARA ADMINS ---
  const handleAdminLogin = async (credentialResponse) => {
    const idToken = credentialResponse.credential;
    const userObject = jwtDecode(idToken);

    // 2. Regra de negócio para ADMINISTRADORES
    if (
      !userObject.email.endsWith("@ufsj.edu.br") ||
      !userObject.email.endsWith("@aluno.ufsj.edu.br")
    ) {
      alert(
        "Acesso negado. Apenas e-mails de funcionários (@ufsj.edu.br) são permitidos."
      );
      return;
    }

    try {
      const sessionData = await AuthService.loginWithGoogle(idToken);
      storageService.saveAuthToken(sessionData.token);
      storageService.saveUserData(sessionData.user);
      navigate("/admin"); // Redireciona para a área do admin
    } catch (error) {
      alert("Ocorreu um erro ao tentar logar no sistema.");
      console.error("Falha no login do admin com o backend:", error);
    }
  };

  const handleGoogleLoginError = () => {
    console.error("Login com Google falhou");
    alert("Não foi possível autenticar com o Google.");
  };

  useEffect(() => {
    if (window.core?.BRTab) {
      const abasList = [];
      for (const brTab of window.document.querySelectorAll(".br-tab")) {
        abasList.push(new window.core.BRTab("br-tab", brTab));
      }
    }
  }, []);
  return (
    <div className="initial-wrapper min-h-screen w-screen flex items-center justify-center flex-col ">
      <div className="flex flex-row align-items-center ">
        <div className="col-auto ">
          <img
            src="/UFSJ.png"
            alt="logo"
            style={{ height: "100px", width: "auto" }}
          />
        </div>
        <div className="col-auto">
          <span
            className="br-divider vertical"
            style={{
              height: "100px",
              display: "inline-block",
              marginLeft: "8px",
              marginRight: "8px",
            }}
          ></span>
        </div>
        <div className="col-auto">
          <img
            src="/Ccomp.png"
            alt="logo"
            style={{ height: "100px", width: "auto" }}
          />
        </div>
      </div>

      <div className="br-tab pt-6">
        <nav className="tab-nav">
          <ul>
            <li className="tab-item active">
              <button type="button" data-panel="panel-1-icon">
                <span className="name">
                  <span className="d-flex flex-column flex-sm-row">
                    <span className="icon mb-1 mb-sm-0 mr-sm-1">
                      <i className="fas fa-user" aria-hidden="true"></i>
                    </span>
                    <span className="name">Aluno</span>
                  </span>
                </span>
              </button>
            </li>
            <li className="tab-item">
              <button type="button" data-panel="panel-2-icon">
                <span className="name">
                  <span className="d-flex flex-column flex-sm-row">
                    <span className="icon mb-1 mb-sm-0 mr-sm-1">
                      <i className="fas fa-user-tie" aria-hidden="true"></i>
                    </span>
                    <span className="name">Administrador</span>
                  </span>
                </span>
              </button>
            </li>
          </ul>
        </nav>
        <div className="tab-content">
          <div className="tab-panel active" id="panel-1-icon">
            <div className="login-wrapper flex items-center justify-center shadow-md rounded-b-lg">
              <GoogleLogin
                onSuccess={handleStudentLogin}
                onError={handleGoogleLoginError}
                useOneTap // Opcional: habilita o login com um clique se o usuário já estiver logado no Google
              />
              {/* {import.meta.env.MODE === "production" ? (
                // EM PRODUÇÃO: Renderiza um link <a> para o SSO real
                <a href={ssoLoginUrl} className="br-button primary">
                  <i className="fas fa-city" aria-hidden="true"></i>
                  <span className="ml-1">Login com SSO</span>
                </a>
              ) : (
                // EM DESENVOLVIMENTO: Renderiza um botão <button> com o onClick da simulação
                <button
                  type="button"
                  className="br-button primary"
                  onClick={handleMockSsoLogin}
                >
                  <i className="fas fa-city" aria-hidden="true"></i>
                  <span className="ml-1">Login com SSO</span>
                </button>
              )} */}
              {/* <a
                href={`${
                  import.meta.env.VITE_SSO_BASE_URL
                }/auth/oauth/sso/login?clientId=${
                  import.meta.env.VITE_SSO_CLIENT_ID
                }`}
                className="br-button primary"
              >
                <i className="fas fa-city" aria-hidden="true"></i>
                <span className="ml-1">Login com SSO</span>
              </a> */}
            </div>

            {/* 
            <Login tipo="student" /> */}
          </div>
          <div className="tab-panel" id="panel-2-icon">
            <div className="login-wrapper flex items-center justify-center shadow-md rounded-b-lg p-6">
              <GoogleLogin
                onSuccess={handleAdminLogin} // Usa a nova função de admin
                onError={handleGoogleLoginError}
              />
            </div>
            {/* <Login tipo="admin" /> */}
          </div>
        </div>
      </div>
    </div>
  );
}
