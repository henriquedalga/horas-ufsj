package IntegrandoDrive;

import IntegrandoDrive.model.Student;
import IntegrandoDrive.persistence.DrivePersistence;
import IntegrandoDrive.service.FileService;
import com.google.api.services.drive.Drive;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class Experiment {
    public static void main(String[] args) {
        try {
            // inicializa o HTTP client do Drive
            Drive drive = DrivePersistence.initDriveService();

            // monta o persistence e o service
            DrivePersistence persistence = new DrivePersistence(drive);
            FileService fileService = new FileService(persistence);

            Student aluno = new Student("João");
            String parentId = "1TIFxvdsCFWpB9xeXK6mx59Csp5MuDJlN";

            String pastaId = fileService.createStudentFolder(aluno, parentId);
            System.out.println(">> Pasta criada ou existente: " + pastaId);

            java.io.File arquivo = new java.io.File("/home/naan/IC/ODS/teste.pdf");
            String firstFileId = fileService.uploadFile(aluno, arquivo, pastaId);
            System.out.println(">> Arquivo enviado: " + firstFileId);

            // delete e re-upload
            fileService.deleteFile(aluno, firstFileId);
            System.out.println(">> Arquivo excluído: " + firstFileId);
            String newFileId = fileService.uploadFile(aluno, arquivo, pastaId);
            System.out.println(">> Arquivo reenviado: " + newFileId);

            // finaliza
            fileService.finalizeSubmission(aluno, pastaId);
            System.out.println(">> Submissão finalizada para: " + aluno.getName());

            try { fileService.uploadFile(aluno, arquivo, pastaId);}
            catch(IllegalStateException e){ System.out.println("⛔ "+e.getMessage()); }
            try { fileService.deleteFile(aluno, newFileId); }
            catch(IllegalStateException e){ System.out.println("⛔ "+e.getMessage()); }

            System.out.println(">> Link da pasta: " + fileService.getFolderLink(pastaId));
            System.out.println(">> Link do arquivo: " + fileService.getFileLink(newFileId));

            java.io.File dump = new java.io.File("/home/naan/IC/ODS/db-dump.sql");
            fileService.backupDatabase(dump, parentId);
            System.out.println(">> Backup de BD realizado.");

        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
        }
    }
}
