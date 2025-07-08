import { useEffect, useRef, useState } from "react";
import { useNavigate } from "react-router-dom";

import AuthService from "../services/auth.service";

export default function Login({ tipo }) {
  const [usuario, setUsuario] = useState("");
  const [senha, setSenha] = useState("");
  const navigate = useNavigate();
  const [erro, setErro] = useState(null);

  const loginInputRef = useRef(null);
  const senhaInputRef = useRef(null);

  // const [erro, setErro] = useState("");
  const getPlaceholder = (tipo) => {
    if (tipo === "admin") {
      return "Digite seu e-mail";
    } else if (tipo === "student") {
      return "Digite seu CPF";
    }
  };

  useEffect(() => {
    if (window.core?.BRInput) {
      const loginEl = loginInputRef.current;
      const senhaEl = senhaInputRef.current;
      console.log("senhaEl", senhaEl);

      if (loginEl && !loginEl.dataset.initialized) {
        new window.core.BRInput("br-input", loginEl);
        loginEl.dataset.initialized = "true";
      }

      if (senhaEl && !senhaEl.dataset.initialized) {
        console.log("senhaEl", senhaEl);
        new window.core.BRInput("br-input", senhaEl);
        senhaEl.dataset.initialized = "true";
      }
    }
  }, []);

  const handleLogin = async (e) => {
    e.preventDefault();
    try {
      await AuthService.login(usuario, senha);

      // Verificação do tipo (ex: vindo do backend: userData.role === 'ALUNO' ou 'FUNCIONARIO')
      if (tipo === "student") {
        console.log("Login realizado com sucesso");
        navigate("/student");
      } else if (tipo === "admin") {
        navigate("/admin");
      } else {
        setErro("Tipo de usuário desconhecido");
      }
    } catch {
      setErro("Usuário ou senha inválidos");
    }
  };

  return (
    <div className="login-wrapper flex items-center justify-center shadow-md rounded-b-lg">
      <form onSubmit={handleLogin} className=" ">
        <div className="br-input" ref={loginInputRef}>
          <label htmlFor={`input-icon-${tipo}`}>Login</label>
          <div className="input-group">
            <div className="input-icon">
              <i className="fas fa-user-tie" aria-hidden="true"></i>
            </div>
            <input
              id={`input-icon-${tipo}`}
              type="text"
              value={usuario}
              onChange={(e) => setUsuario(e.target.value)}
              placeholder={getPlaceholder(tipo)}
              required
            />
          </div>
        </div>

        <div className="br-input input-button pt-2" ref={senhaInputRef}>
          <label htmlFor={`input-password-${tipo}`}>Senha</label>
          <input
            id={`input-password-${tipo}`}
            type="password"
            value={senha}
            onChange={(e) => setSenha(e.target.value)}
            placeholder="Digite sua senha"
            // required
          />
          <button
            className="br-button"
            type="button"
            aria-label="Exibir senha"
            role="switch"
            aria-checked="false"
          >
            <i className="fas fa-eye" aria-hidden="true"></i>
          </button>
        </div>
        {erro && <div className="erro">{erro}</div>}
        <div className="pt-4 flex justify-end">
          <button className="br-sign-in primary" type="submit">
            <i className="fas fa-sign-in-alt mr-0.5" aria-hidden="true"></i>
            Entrar
          </button>
        </div>
      </form>
    </div>
  );
}
