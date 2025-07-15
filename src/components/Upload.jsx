import { useEffect, useRef, useState } from "react";

import StudentService from "../services/student.service";

export default function Upload({ tipo }) {
  const uploadRef = useRef(null); // referência local ao componente

  const [selectedFiles, setSelectedFiles] = useState([]);
  const [uploadStatus, setUploadStatus] = useState("idle"); // 'idle', 'uploading', 'success', 'error'
  const [error, setError] = useState(null);

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
        response = await StudentService.uploadFilesExtensao(formData);
      } else {
        response = await StudentService.uploadFilesComplementar(formData);
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
        <div className="upload-list"></div>
      </div>

      <p className="text-base mt-1">
        Clique ou arraste os arquivos para cima do componente Upload.
      </p>
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
