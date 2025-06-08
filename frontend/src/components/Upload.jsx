import { useEffect, useRef } from "react";

export default function Upload() {
  const uploadRef = useRef(null); // referência local ao componente

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
        />
        <div className="upload-list"></div>
      </div>

      <p className="text-base mt-1">
        Clique ou arraste os arquivos para cima do componente Upload.
      </p>
    </div>
  );
}
