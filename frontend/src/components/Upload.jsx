import { useEffect, useRef, useState } from "react";

import StudentService from "../services/student.service";
import FileItem from "./FilesItem";

export default function Upload({ tipo }) {
  const uploadRef = useRef(null); // referência local ao componente

  const [selectedFiles, setSelectedFiles] = useState([]);
  const [uploadStatus, setUploadStatus] = useState("idle"); // 'idle', 'uploading', 'success', 'error'
  const [error, setError] = useState(null);
  const [existingFiles, setExistingFiles] = useState([]);
  const [isLoading, setIsLoading] = useState(true);

  const handleFileChange = (event) => {
    // event.target.files é uma FileList, convertemos para um array
    if (event.target.files) {
      setSelectedFiles(Array.from(event.target.files));
      setUploadStatus("idle"); // Reseta o status se novos arquivos forem selecionados
    }
  };

  const handleUpload = async () => {
    if (selectedFiles.length === 0) {
      alert("Por favor, selecione ao menos um arquivo.");
      return;
    }

    setUploadStatus("uploading");
    setError(null);

    // FormData é o formato correto para enviar arquivos
    const formData = new FormData();
    selectedFiles.forEach((file) => {
      // A chave 'files' deve ser a que seu backend espera receber
      formData.append("files", file);
    });

    try {
      // Supondo que seu serviço tenha um método 'uploadFiles'
      let response;
      if (tipo == "extensao") {
        response = await StudentService.postFilesExtensao(formData);
      } else {
        response = await StudentService.postFilesComplementar(formData);
      }

      console.log("Upload bem-sucedido:", response);
      setUploadStatus("success");
      setSelectedFiles([]); // Limpa a seleção após o sucesso
    } catch (err) {
      console.error("Erro no upload:", err);
      setError(err.message);
      setUploadStatus("error");
    }
  };

  // Função para lidar com a exclusão de um arquivo existente
  const handleDeleteExistingFile = async (fileId) => {
    if (!window.confirm("Tem certeza que deseja excluir este arquivo?")) return;

    try {
      // Supondo que exista um método no serviço para deletar
      // await StudentService.deleteFile(tipo, id, fileId);

      // Remove o arquivo da lista na interface
      setExistingFiles((prevFiles) => prevFiles.filter((f) => f.id !== fileId));

      alert("Arquivo excluído com sucesso!");
    } catch (err) {
      console.error("Erro ao excluir arquivo:", err);
      alert("Não foi possível excluir o arquivo.");
    }
  };

  // ... seu useEffect de verificação inicial ...

  const renderContent = () => {
    if (isLoading) {
      return <div className="p-4 text-center">Verificando...</div>;
    }

    if (existingFiles.length > 0) {
      // Agora ele renderizará o componente com o visual exato da biblioteca
      return (
        <>
          {existingFiles.map((file) => (
            <FileItem
              key={file.id} // A 'key' é sempre definida aqui, no loop
              file={file} // Passamos o objeto 'file' individualmente
              onDelete={handleDeleteExistingFile}
            />
          ))}
        </>
      );
    }

    // ... resto do código
  };

  useEffect(() => {
    if (window.core?.BRUpload && uploadRef.current) {
      function uploadTimeout() {
        return new Promise((resolve) => setTimeout(resolve, 3000));
      }

      const brUpload = uploadRef.current.querySelector(".br-upload");
      if (brUpload && !brUpload.dataset.initialized) {
        console.log("brUpload", brUpload);
        new window.core.BRUpload("br-upload", brUpload, uploadTimeout);
        brUpload.dataset.initialized = "true"; // evita reprocessar o mesmo
      }
    }
  }, []);

  useEffect(() => {
    // Cria uma função async para buscar os dados
    const fetchExistingFiles = async () => {
      // Se não recebeu o tipo, não faz nada
      if (!tipo) return;

      setIsLoading(true);
      setExistingFiles([]); // Limpa a lista antiga antes de buscar a nova

      try {
        let data;
        // Decide qual método do serviço chamar com base na prop 'tipo'
        if (tipo === "extensao") {
          data = await StudentService.getSolicitacaoExtensao();
        } else if (tipo === "complementar") {
          data = await StudentService.getSolicitacaoComplementar();
        }

        // Se a resposta tiver um array de arquivos, atualiza o estado
        if (data && data.arquivos) {
          setExistingFiles(data.arquivos);
        }
      } catch (err) {
        console.error(`Erro ao buscar arquivos de ${tipo}:`, err);
        setError(`Não foi possível carregar os arquivos de ${tipo}.`);
      } finally {
        setIsLoading(false); // Para de carregar, independentemente do resultado
      }
    };

    fetchExistingFiles();
  }, [tipo]);

  return (
    <div ref={uploadRef} className="flex flex-col">
      <div className="br-upload">
        <label className="upload-label" htmlFor="multiple-files">
          <span>Envio de arquivos</span>
        </label>
        <input
          className="upload-input"
          id="multiple-files"
          type="file"
          multiple
          aria-label="enviar arquivo"
          onChange={handleFileChange}
        />
        <div className="upload-list">{renderContent()}</div>
      </div>

      {/* --- BOTÃO DE ENVIO E INDICADORES DE STATUS --- */}
      <div className="pt-2 flex w-full max-w-lg">
        <button
          type="button"
          className="br-button primary ml-auto"
          // Desabilita o botão se não houver arquivos ou se estiver enviando
          disabled={selectedFiles.length === 0 || uploadStatus === "uploading"}
          onClick={handleUpload}
        >
          {uploadStatus === "uploading" ? "Enviando..." : "Enviar"}
        </button>
      </div>

      {/* Mensagens de feedback para o usuário */}
      {uploadStatus === "success" && (
        <div className="br-message success mt-4" role="alert">
          <div className="icon">
            <i className="fas fa-check-circle fa-lg" aria-hidden="true"></i>
          </div>
          <div className="content">Arquivos enviados com sucesso!</div>
        </div>
      )}

      {uploadStatus === "error" && (
        <div className="br-message danger mt-4" role="alert">
          <div className="icon">
            <i className="fas fa-times-circle fa-lg" aria-hidden="true"></i>
          </div>
          <div className="content">Falha no envio: {error}</div>
        </div>
      )}
    </div>
  );
}
