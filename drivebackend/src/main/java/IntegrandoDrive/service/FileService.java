package IntegrandoDrive.service;

import IntegrandoDrive.model.Student;
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

    public String createStudentFolder(Student student, String parentFolderId) throws IOException {
        return persistence.createFolderIfNotExists(student.getName(), parentFolderId);
    }

    public String uploadFile(Student student, java.io.File localFile, String folderId) throws IOException {
        if (student.hasSubmitted()) {
            throw new IllegalStateException("Aluno já submeteu: não pode adicionar arquivos.");
        }
        return persistence.uploadFile(localFile, folderId);
    }

    public void deleteFile(Student student, String fileId) throws IOException {
        if (student.hasSubmitted()) {
            throw new IllegalStateException("Aluno já submeteu: não pode excluir arquivos.");
        }
        persistence.deleteFile(fileId);
    }

    public void finalizeSubmission(Student student, String folderId) throws IOException {
        // Marca o estudante como submetido e aplica read-only em todos os arquivos
        student.setSubmitted(true);
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

    public void backupDatabase(java.io.File dbDump, String backupFolderId) throws IOException {
        persistence.backupDatabase(dbDump, backupFolderId);
    }
}