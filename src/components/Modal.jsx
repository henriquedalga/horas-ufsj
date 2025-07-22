import { useEffect, useRef, useState } from "react";

import AdminService from "../services/admin.service";

export default function Modal({ isOpen, onClose, nome, id, arquivos, tipo }) {
  const [respostas, setRespostas] = useState([]);
  const modalBodyRef = useRef(null);

  useEffect(() => {
    console.log("Modal aberto:", isOpen, "Arquivos:", arquivos);
    if (isOpen && arquivos.length > 0) {
      setRespostas(
        arquivos.map((arquivo) => ({
          status: arquivo.status || "",
          comments: "",
        }))
      );
    }
  }, [isOpen, arquivos]);

  useEffect(() => {
    // Só executa se o modal estiver aberto e a biblioteca existir
    if (isOpen && window.core?.BRSelect && modalBodyRef.current) {
      const selectInstances = [];
      // Busca os selects APENAS DENTRO do corpo do modal
      const brSelects = modalBodyRef.current.querySelectorAll(".br-select");

      for (const brSelect of brSelects) {
        // A inicialização agora é feita de forma segura
        selectInstances.push(new window.core.BRSelect("br-select", brSelect));
      }

      // // Função de limpeza para evitar memory leaks
      // return () => {
      //   selectInstances.forEach(
      //     (inst) => inst && inst.destroy && inst.destroy()
      //   );
      // };
    }
  }, [isOpen, arquivos]);

  const handleRadioChange = (index, status) => {
    const atualizadas = [...respostas];
    atualizadas[index].status = status;
    setRespostas(atualizadas);
  };

  const handleComentarioChange = (index, comentario) => {
    const atualizadas = [...respostas];
    atualizadas[index].comentario = comentario;
    setRespostas(atualizadas);
  };

  async function handleSubmit() {
    const dataAtualizada = {
      arquivos: respostas,
    };

    try {
      if (tipo === "complementar") {
        await AdminService.postComplementarById(id, dataAtualizada);
      } else {
        await AdminService.postExtensaoById(id, dataAtualizada);
      }
    } catch (error) {
      console.error("Erro ao enviar respostas:", error);
    }
  }

  if (!isOpen) return null;

  return (
    <div className="br-scrim-util foco active" data-scrim="true">
      <div className="br-modal p-4" aria-labelledby="modal-title">
        <div className="br-modal-header" id="modal-title">
          {nome}
        </div>
        <div className="br-modal-body">
          <span className="br-divider"></span>

          {arquivos.map((arquivo, index) => (
            <div key={arquivo.id}>
              <div className="br-item">
                <div className="row align-items-center">
                  <div className="col-auto">
                    <div className="br-radio valid">
                      <input
                        id={`radio-aprovar-${index}`}
                        type="radio"
                        name={`radio-${index}`}
                        value="APROVADO"
                        checked={respostas[index]?.status === "APROVADO"}
                        onChange={() => handleRadioChange(index, "APROVADO")}
                        aria-label="Aprovar"
                      />
                      <label htmlFor={`radio-aprovar-${index}`}></label>
                    </div>
                  </div>

                  <div className="col-auto">
                    <div className="br-radio invalid">
                      <input
                        id={`radio-reprovar-${index}`}
                        type="radio"
                        name={`radio-${index}`}
                        value="REPROVADO"
                        checked={respostas[index]?.status === "REPROVADO"}
                        onChange={() => handleRadioChange(index, "REPROVADO")}
                        aria-label="Reprovar"
                      />
                      <label htmlFor={`radio-reprovar-${index}`}></label>
                    </div>
                  </div>

                  <div className="col">
                    <span>{arquivo.nome}</span>
                  </div>

                  <div className="col">
                    <div className="br-select" ref={modalBodyRef}>
                      <div className="br-input">
                        <input
                          id="select-simple"
                          type="text"
                          placeholder="Selecione o item"
                        />
                        <button
                          className="br-button"
                          type="button"
                          aria-label="Exibir lista"
                          tabIndex="-1"
                          data-trigger="data-trigger"
                        >
                          <i class="fas fa-angle-down" aria-hidden="true"></i>
                        </button>
                      </div>
                      <div className="br-list" tabIndex="0">
                        <div className="br-item" tabIndex="-1">
                          <div className="br-radio">
                            <input
                              id="rb0"
                              type="radio"
                              name="estados-simples"
                              value="rb0"
                            />
                            <label for="rb0">1</label>
                          </div>
                        </div>
                        <div className="br-item" tabIndex="-1">
                          <div className="br-radio">
                            <input
                              id="rb1"
                              type="radio"
                              name="estados-simples"
                              value="rb1"
                            />
                            <label for="rb1">2</label>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>

                {respostas[index]?.status === "REPROVADO" && (
                  <div className="br-textarea mt-2">
                    <label htmlFor={`comentario-${index}`}>Comentário:</label>
                    <textarea
                      id={`comentario-${index}`}
                      className="br-textarea w-full"
                      rows={3}
                      value={respostas[index].comments}
                      onChange={(e) =>
                        handleComentarioChange(index, e.target.value)
                      }
                      placeholder="Explique o motivo da reprovação"
                    />
                  </div>
                )}
              </div>
              <span className="br-divider"></span>
            </div>
          ))}
        </div>

        <div className="br-modal-footer justify-content-center">
          <button className="br-button secondary min-w-35" onClick={onClose}>
            Cancelar
          </button>
          <button
            className="br-button primary mt-3 mt-sm-0 ml-sm-3 min-w-35"
            type="button"
            onClick={handleSubmit}
          >
            Aceitar
          </button>
        </div>
      </div>
    </div>
  );
}
