import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

import AuthService from "../services/auth.service";

export default function Header() {
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [userName, setUserName] = useState("");
  const navigate = useNavigate();

  const handleAuthAction = () => {
    if (isAuthenticated) {
      AuthService.logout(); // limpa token/localStorage
      setIsAuthenticated(false);
      navigate("/login"); // ou página inicial
    } else {
      navigate("/login");
    }
  };

  useEffect(() => {
    const user = AuthService.getCurrentUser(); // ou checar token/localStorage
    console.log(user.name);
    if (user) {
      setIsAuthenticated(true);
      setUserName(user.name); // substitua "nome" pela chave correta se for diferente
    }
    if (window.core?.BRHeader) {
      const headerList = [];
      for (const brHeader of window.document.querySelectorAll(".br-header")) {
        headerList.push(new window.core.BRHeader("br-header", brHeader));
      }
    }
  }, []);

  return (
    <header className="br-header compact fixed top-0 w-full">
      <div className="container-lg">
        <div className="header-top">
          <div className="header-logo">
            <img src="/Ccomp.png" alt="logo" />
            <span className="br-divider vertical"></span>
            <div className="header-sign">Assinatura</div>
          </div>
          <div className="header-actions">
            <div className="br-item">
              <button
                className="br-button circle small"
                type="button"
                aria-label="Funcionalidade 4"
              >
                <i className="fas fa-adjust" aria-hidden="true"></i>
              </button>
            </div>

            <div className="header-login">
              <div className="header-sign-in">
                <button
                  className="br-sign-in small"
                  type="button"
                  onClick={handleAuthAction}
                >
                  <i className="fas fa-user" aria-hidden="true"></i>
                  <span className="d-sm-inline">
                    {isAuthenticated ? "Sair" : "Entrar"}
                  </span>
                </button>
              </div>
              <div className="header-avatar"></div>
            </div>
          </div>
        </div>
        <div className="header-bottom">
          <div className="header-menu">
            <div className="header-menu-trigger">
              <button
                className="br-button small circle"
                type="button"
                aria-label="Menu"
                data-toggle="menu"
                data-target="#main-navigation"
                id="menu-compact"
              >
                <i className="fas fa-bars" aria-hidden="true"></i>
              </button>
            </div>
            <div className="header-info">
              <div className="header-title">Entrega de Horas UFSJ</div>
              <div className="header-subtitle">
                Olá, {userName || "Usuário não identificado"}
              </div>
            </div>
          </div>
        </div>
      </div>
    </header>
  );
}
