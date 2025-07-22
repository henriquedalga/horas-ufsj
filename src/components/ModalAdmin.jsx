import { useEffect, useState } from "react";

import AdminService from "../services/admin.service";

export default function ModalAdmin({ onClose, onSuccess, adminToEdit = null }) {
  const isEditMode = Boolean(adminToEdit);

  const [formData, setFormData] = useState({ nome: "", email: "" });
  const [error, setError] = useState(null);
  const [isSubmitting, setIsSubmitting] = useState(false);

  useEffect(() => {
    if (isEditMode) {
      setFormData({ nome: adminToEdit.nome, email: adminToEdit.email });
    } else {
      setFormData({ nome: "", email: "" });
    }
  }, [adminToEdit, isEditMode]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);
    setIsSubmitting(true);
    try {
      if (isEditMode) {
        await AdminService.updateAdmin(adminToEdit.id, formData);
        alert("Administrador atualizado com sucesso!");
      } else {
        await AdminService.addAdmin(formData);
        alert("Administrador adicionado com sucesso!");
      }
      onSuccess();
    } catch (err) {
      setError(err.message);
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="br-scrim-util foco active" data-scrim="true">
      <form className="br-modal p-4" onSubmit={handleSubmit}>
        <div className="br-modal-header" id="modal-title">
          {/* Título dinâmico */}
          {isEditMode ? "Editar Administrador" : "Adicionar Novo Administrador"}
        </div>
        <div className="br-modal-body w-full">
          <div className="flex flex-col items-center w-full">
            <div className="br-input mb-2">
              <input
                name="nome"
                value={formData.nome}
                onChange={handleChange}
                placeholder="Nome"
                required
              />
            </div>
            <div className="br-input mb-2">
              <input
                type="email"
                name="email"
                value={formData.email}
                onChange={handleChange}
                placeholder="Email"
                required
              />
            </div>
            {/* O campo de senha só aparece no modo de adição */}
            {!isEditMode && (
              <div className="br-input mb-2">
                <input
                  type="password"
                  name="password"
                  onChange={handleChange}
                  placeholder="Senha"
                  required
                />
              </div>
            )}
          </div>
        </div>
        <div className="br-modal-footer justify-content-center">
          <button
            type="button"
            className="br-button secondary"
            onClick={onClose}
          >
            Cancelar
          </button>
          <button
            className="br-button primary mt-3 mt-sm-0 ml-sm-3"
            type="submit"
            disabled={isSubmitting}
          >
            {/* Texto do botão dinâmico */}
            {isSubmitting
              ? isEditMode
                ? "Salvando..."
                : "Adicionando..."
              : isEditMode
              ? "Salvar"
              : "Adicionar"}
          </button>
        </div>
        {error && <div className="text-red-600 text-center mt-2">{error}</div>}
      </form>
    </div>
  );
}
