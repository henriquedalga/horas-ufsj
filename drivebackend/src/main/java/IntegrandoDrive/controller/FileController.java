package IntegrandoDrive.controller;

import IntegrandoDrive.model.Student;
import IntegrandoDrive.persistence.DrivePersistence;
import com.google.api.services.drive.model.FileList;
import java.io.IOException;
import java.security.GeneralSecurityException;

public class FileController {
    private DrivePersistence persistence;

    public FileController(DrivePersistence persistence) {
        this.persistence = persistence;
    }

    /** Cria (ou obtém) a pasta do aluno e retorna o ID dela */
    public String createStudentFolder(Student student, String parentFolderId) throws IOException {
        return persistence.createFolderIfNotExists(student.getName(), parentFolderId);
    }

    /** Faz upload de arquivo local para a pasta do aluno */
    public String uploadFile(Student student, java.io.File localFile, String folderId) throws IOException {
        if (student.hasSubmitted()) {
            throw new IllegalStateException("Aluno já submeteu: não pode adicionar arquivos.");
        }
        return persistence.uploadFile(localFile, folderId);
    }

    /** Exclui um arquivo pelo ID, se ainda não tiver submetido */
    public void deleteFile(Student student, String fileId) throws IOException {
        if (student.hasSubmitted()) {
            throw new IllegalStateException("Aluno já submeteu: não pode excluir arquivos.");
        }
        persistence.deleteFile(fileId);
    }

    /** Marca o aluno como submetido e, opcionalmente, torna todos os arquivos da pasta somente leitura */
    public void finalizeSubmission(Student student, String folderId) throws IOException {
        student.setSubmitted(true);

        // opcional: torna todos os arquivos da pasta somente leitura
        FileList files = persistence.listFilesInFolder(folderId);
        for (com.google.api.services.drive.model.File f : files.getFiles()) {
            persistence.setFileReadOnly(f.getId(), "Submissão finalizada");
        }
    }

    /** Pega o link de visualização da pasta */
    public String getFolderLink(String folderId) throws IOException {
        return persistence.getFolderLink(folderId);
    }

    /** Pega o link de visualização de um arquivo */
    public String getFileLink(String fileId) throws IOException {
        return persistence.getFileLink(fileId);
    }

    /** Faz backup de um dump de banco dentro de uma subpasta "db-backups" */
    public void backupDatabase(java.io.File dbDump, String backupFolderId) throws IOException {
        persistence.backupDatabase(dbDump, backupFolderId);
    }
}
