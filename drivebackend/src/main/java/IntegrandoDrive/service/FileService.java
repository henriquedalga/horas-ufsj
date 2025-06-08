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
        // Upload só bloqueia via status da Solicitação no serviço
        return persistence.uploadFile(localFile, folderId);
    }

    public void deleteFile(Student student, String fileId) throws IOException {
        // Exclusão só bloqueia via status da Solicitação no serviço
        persistence.deleteFile(fileId);
    }

    public void finalizeSubmission(Student student, String folderId) throws IOException {
        // Marca arquivos como somente leitura ao finalizar
        FileList files = persistence.listFilesInFolder(folderId);
        for (com.google.api.services.drive.model.File f : files.getFiles()) {
            persistence.setFileReadOnly(f.getId(), "Submissão finalizada");
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
