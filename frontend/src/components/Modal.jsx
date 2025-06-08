import { useEffect, useState } from "react";

export default function Modal({ isOpen, onClose, nome, arquivos }) {
  const [respostas, setRespostas] = useState([]);

  useEffect(() => {
    if (isOpen && arquivos.length > 0) {
      setRespostas(
        arquivos.map(() => ({
          status: "",
          comentario: "",
        }))
      );
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
                </div>

                {respostas[index]?.status === "REPROVADO" && (
                  <div className="br-textarea mt-2">
                    <label htmlFor={`comentario-${index}`}>Comentário:</label>
                    <textarea
                      id={`comentario-${index}`}
                      className="br-textarea w-full"
                      rows={3}
                      value={respostas[index].comentario}
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
          <button className="br-button secondary" onClick={onClose}>
            Cancelar
          </button>
          <button className="br-button primary mt-3 mt-sm-0 ml-sm-3">
            Retornar
          </button>
        </div>
      </div>
    </div>
  );
}
