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
          horas: arquivo.horas || 0, // Adiciona o campo 'horas'
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

  const handleHorasChange = (index, valor) => {
    // Remove qualquer caractere que não seja um dígito
    const valorInteiro = valor.replace(/[^0-9]/g, "");

    const atualizadas = [...respostas];
    // Atualiza o valor. Se a string ficar vazia, guardamos 0 ou uma string vazia.
    atualizadas[index].horas =
      valorInteiro === "" ? 0 : parseInt(valorInteiro, 10);
    setRespostas(atualizadas);
  };

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
                <div className="row align-items-center flex-nowrap ">
                  <div className="col-auto p-0">
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

                  <div className="col-auto p-0">
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

                  <div className="col overflow-hidden">
                    <a
                      href={arquivo.url} // Usa a URL do objeto 'arquivo'
                      target="_blank" // Abre o link em uma nova aba
                      rel="noopener noreferrer" // Boas práticas de segurança para links externos
                      title={arquivo.nome} // Mostra o nome completo no hover (tooltip do navegador)
                      className="text-blue-600 hover:underline truncate" // Estilização do link
                    >
                      {arquivo.nome}
                    </a>
                  </div>

                  <div className="col max-w-18">
                    <div className="br-input">
                      <input
                        id={`horas-${index}`} // IDs devem ser únicos na página
                        type="number"
                        pattern="[0-9]*"
                        inputMode="numeric"
                        placeholder="Horas"
                        name={`horas-${index}`}
                        value={respostas[index]?.horas || ""}
                        onChange={(e) =>
                          handleHorasChange(index, e.target.value)
                        }
                        aria-label="Horas"
                      />
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
