import { useState } from "react";

import AdminService from "../services/admin.service";

export default function ModalAdmin({ onClose, onSuccess }) {
  const [formData, setFormData] = useState({
    nome: "",
    email: "",
    password: "",
  });
  const [error, setError] = useState(null);
  const [isSubmitting, setIsSubmitting] = useState(false);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prevData) => ({
      ...prevData,
      [name]: value,
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);
    setIsSubmitting(true);

    try {
      await AdminService.addAdmin(formData);
      alert("Administrador adicionado com sucesso!");
      onSuccess();
      onClose();
    } catch (err) {
      console.error("Erro ao adicionar admin:", err);
      setError(err.message);
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="br-scrim-util foco active" data-scrim="true">
      {/* AQUI AGORA É UM FORM com onSubmit */}
      <form className="br-modal p-4" onSubmit={handleSubmit}>
        <div className="br-modal-header" id="modal-title">
          Adicionar novo administrador :
        </div>
        <div className="br-modal-body w-full">
          {/* Cada input agora é controlado pelo estado */}
          <div className="flex flex-col align-items-center w-full">
            <div className="br-input mb-2">
              <input
                type="text"
                name="nome" // O 'name' deve corresponder à chave no estado
                value={formData.nome}
                onChange={handleChange}
                placeholder="Nome"
                required
              />
            </div>
            <div className="br-input mb-2">
              <input
                type="email" // Use o tipo 'email' para validação do navegador
                name="email"
                value={formData.email}
                onChange={handleChange}
                placeholder="Email"
                required
              />
            </div>
            <div className="br-input">
              <input
                type="password"
                name="password"
                value={formData.password}
                onChange={handleChange}
                placeholder="Senha"
                required
              />
            </div>
          </div>
        </div>
        <div className="br-modal-footer justify-content-center">
          {/* O botão de cancelar apenas fecha o modal */}
          <button
            type="button"
            className="br-button secondary"
            onClick={onClose}
          >
            Cancelar
          </button>
          {/* O botão principal agora é do tipo 'submit' */}
          <button
            className="br-button primary mt-3 mt-sm-0 ml-sm-3"
            type="submit"
            disabled={isSubmitting} // Desabilita o botão durante o envio
          >
            {isSubmitting ? "Adicionando..." : "Adicionar"}
          </button>
        </div>
        {error && <div className="text-red-600 text-center mt-2">{error}</div>}
      </form>
    </div>
  );
}
