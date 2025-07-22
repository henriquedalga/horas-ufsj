package IntegrandoDrive.service;

import IntegrandoDrive.persistence.DrivePersistence;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class FileService {

    private final DrivePersistence persComp;
    private final DrivePersistence persExt;

    public FileService(
        @Qualifier("persistenceComplementary") DrivePersistence persComp,
        @Qualifier("persistenceExtension")     DrivePersistence persExt
    ) {
        this.persComp = persComp;
        this.persExt  = persExt;
    }
    private DrivePersistence selectPersistence(int hourType) {
        // 0 = complementar, 1 = extensão
        return hourType == 1 ? persExt : persComp;
    }

     /**
     * Cria uma pasta no Drive, se não existir.
     */
    public String createFolder(String nome, int hourType) throws IOException {
        DrivePersistence persistence = selectPersistence(hourType);
        return persistence.createFolderIfNotExists(nome, persistence.getDefaultParentFolderId());
    }

    /**
     * Faz upload de arquivo, desde que a solicitação não esteja finalizada.
     */
    public String uploadFile(java.io.File localFile, String folderId, int hourType) throws IOException {
        return selectPersistence(hourType)
               .uploadFile(localFile, folderId);
    }

    /**
     * Exclui arquivo, desde que a solicitação não esteja finalizada.
     */
    public void deleteFile(String fileId, int hourType) throws IOException {
        selectPersistence(hourType)
            .deleteFile(fileId);
    }

    /**
     * Finaliza submissão: marca todos os arquivos como read-only.
     */
    public void finalizeSubmission(String folderId, int hourType) throws IOException {
        selectPersistence(hourType)
            .setFolderReadOnly(folderId);
    }

    /**
     * Rejeita submissão: torna todos os arquivos editáveis de novo.
     */
    public void rejectSubmission(String folderId, int hourType) throws IOException {
        selectPersistence(hourType)
            .setFolderWritable(folderId);
    }

    /**
     * Retorna o link de visualização da pasta.
     */
    public String getFolderLink(String folderId, int hourType) throws IOException {
        return selectPersistence(hourType)
               .getFolderLink(folderId);
    }

    /**
     * Retorna o link de visualização do arquivo.
     */
    public String getFileLink(String fileId, int hourType) throws IOException {
        return selectPersistence(hourType)
               .getFileLink(fileId);
    }
    /**
     * Verifica se a pasta está marcada como somente leitura.
     */
    public boolean isFolderReadOnly(String folderId, int hourType) throws IOException {
        return selectPersistence(hourType)
               .isFolderReadOnly(folderId);
    }

    /**
     * Lista todos os links de arquivos dentro de uma pasta.
     */
    public List<String> listFileLinks(String folderId, int hourType) throws IOException {
        return selectPersistence(hourType)
               .listFileLinks(folderId);
    }
    /**
     * Faz backup do banco de dados e faz upload para a pasta.
     */
    public String backupDatabase(java.io.File dumpFile, String folderId, int hourType) throws IOException {
        return selectPersistence(hourType)
               .uploadFile(dumpFile, folderId);
    }
}
