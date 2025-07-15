package IntegrandoDrive.service;

import IntegrandoDrive.persistence.DrivePersistence;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

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
    public String uploadFile(java.io.File localFile, String folderId) throws IOException {
        return persistence.uploadFile(localFile, folderId);
    }

    /**
     * Exclui arquivo, desde que a solicitação não esteja finalizada.
     */
    public void deleteFile(String fileId) throws IOException {
        persistence.deleteFile(fileId);
    }

    /**
     * Finaliza submissão: marca todos os arquivos como read-only.
     */
    public void finalizeSubmission(String folderId) throws IOException {
        persistence.setFolderReadOnly(folderId);
    }

    public void rejectSubmission(String folderId) throws IOException {
        persistence.setFolderWritable(folderId);
    }

    public String getFolderLink(String folderId) throws IOException {
        return persistence.getFolderLink(folderId);
    }

    public String getFileLink(String fileId) throws IOException {
        return persistence.getFileLink(fileId);
    }
    public boolean isFolderReadOnly(String folderId) throws IOException {
        return persistence.isFolderReadOnly(folderId);
    }

    public List<String> listFileLinks(String folderId) throws IOException {
        return persistence.listFileLinks(folderId);
    }
        /**
     * Cria backup do banco de dados e faz upload para a pasta.
     */
    public String backupDatabase(java.io.File dumpFile, String folderId) throws IOException {
        // Faz upload do dump para a pasta, servindo como backup
        return persistence.uploadFile(dumpFile, folderId);
    }
}
