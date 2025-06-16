package IntegrandoDrive.service;

import IntegrandoDrive.persistence.DrivePersistence;
import com.google.api.services.drive.model.FileList;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class FileService {

    private final DrivePersistence persistence;

    public FileService(DrivePersistence persistence) {
        this.persistence = persistence;
    }

    /**
     * Cria uma pasta no Drive, se não existir.
     */
    public String createFolder(String nome, String parentFolderId) throws IOException {
        return persistence.createFolderIfNotExists(nome, parentFolderId);
    }

    /**
     * Faz upload de arquivo, desde que a solicitação não esteja finalizada.
     */
    public String uploadFile(java.io.File localFile, String folderId, String status) throws IOException {
        if ("FINALIZADA".equals(status)) {
            throw new IllegalStateException("Não é possível adicionar arquivos a submissão finalizada.");
        }
        return persistence.uploadFile(localFile, folderId);
    }

    /**
     * Exclui arquivo, desde que a solicitação não esteja finalizada.
     */
    public void deleteFile(String fileId, String status) throws IOException {
        if ("FINALIZADA".equals(status)) {
            throw new IllegalStateException("Não é possível excluir arquivos de submissão finalizada.");
        }
        persistence.deleteFile(fileId);
    }

    /**
     * Finaliza submissão: marca todos os arquivos como read-only.
     */
    public void finalizeSubmission(String folderId) throws IOException {
        FileList files = persistence.listFilesInFolder(folderId);
        if (files != null && files.getFiles() != null) {
            for (com.google.api.services.drive.model.File f : files.getFiles()) {
                persistence.setFileReadOnly(f.getId(), "Submissão finalizada");
            }
        }
    }

    public String getFolderLink(String folderId) throws IOException {
        return persistence.getFolderLink(folderId);
    }

    public String getFileLink(String fileId) throws IOException {
        return persistence.getFileLink(fileId);
    }
        /**
     * Cria backup do banco de dados e faz upload para a pasta.
     */
    public String backupDatabase(java.io.File dumpFile, String folderId) throws IOException {
        // Faz upload do dump para a pasta, servindo como backup
        return persistence.uploadFile(dumpFile, folderId);
    }
}
