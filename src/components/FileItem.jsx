import React from "react";

function formatBytes(bytes, decimals = 2) {
  if (!+bytes) return "0 Bytes";
  const k = 1024;
  const dm = decimals < 0 ? 0 : decimals;
  const sizes = ["Bytes", "KB", "MB", "GB", "TB"];
  const i = Math.floor(Math.log(bytes) / Math.log(k));
  return `${parseFloat((bytes / Math.pow(k, i)).toFixed(dm))} ${sizes[i]}`;
}

// Este componente agora é totalmente independente de bibliotecas externas de JS.
// Não precisa mais de useEffect ou useRef para o tooltip.
export default function FileItem({ file, onDelete }) {
  const isDeleteDisabled =
    file.status === "PENDENTE" || file.status === "APROVADO";

  const itemClass = `br-item d-flex align-items-center p-2 mb-2 rounded ${
    file.status === "APROVADO" ? "item-aprovado" : ""
  }`;

  // As classes da tag de status agora são calculadas aqui para maior clareza
  const tagClass =
    file.status === "APROVADO"
      ? "success"
      : file.status === "REPROVADO"
      ? "danger"
      : "warning"; // Usamos 'warning' para PENDENTE

  // O objeto de atributos do tooltip antigo não é mais necessário para a visibilidade
  // mas ainda podemos usá-lo para as cores
  const tooltipColorClass =
    file.status === "APROVADO"
      ? "bg-green-600"
      : file.status === "REPROVADO"
      ? "bg-red-600"
      : "bg-yellow-400";

  return (
    <div className={itemClass} key={file.id}>
      <div className="content text-primary-default mr-auto">{file.nome}</div>

      <div className="support d-flex align-items-center">
        {/* PASSO 1: Adicione a classe 'group' ao elemento gatilho */}
        <span className={`br-tag relative group ${tagClass}`}>
          {file.status}

          {/* PASSO 2: Estilize o tooltip para começar escondido e depois aparecer com 'group-hover' */}
          <div
            className={`
              absolute bottom-full left-1/2 -translate-x-1/2 mb-2 px-3 py-1 rounded-md text-white text-sm whitespace-nowrap
              ${tooltipColorClass}
              opacity-0 invisible group-hover:visible group-hover:opacity-100 group-hover:z-10
              transition-opacity duration-300
            `}
            role="tooltip"
          >
            {file.comments || file.status}
            {/* Adiciona uma seta/triângulo para o tooltip */}
            <div
              className={`absolute top-full left-1/2 -translate-x-1/2 w-0 h-0 border-x-4 border-x-transparent border-t-4 ${tooltipColorClass.replace(
                "bg",
                "border-t"
              )}`}
            ></div>
          </div>
        </span>

        {file.size && <span className="mx-3">{formatBytes(file.size)}</span>}
        <button
          className="br-button circle small"
          type="button"
          aria-label={`Excluir o arquivo ${file.nome}`}
          onClick={() => onDelete(file.id)}
          disabled={isDeleteDisabled}
        >
          <i className="fas fa-trash" aria-hidden="true"></i>
        </button>
      </div>
    </div>
  );
}
