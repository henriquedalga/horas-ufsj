package IntegrandoDrive;

import IntegrandoDrive.controller.FileController;
import IntegrandoDrive.model.Student;
import IntegrandoDrive.persistence.DrivePersistence;
import com.google.api.services.drive.Drive;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class Experiment {
    public static void main(String[] args) {
        try {
            // 1) Inicializa o serviço Drive
            Drive drive = DrivePersistence.initDriveService();
            FileController controller = new FileController(new DrivePersistence(drive));

            // 2) Prepara dados
            Student aluno = new Student("João");
            String parentId = "1TIFxvdsCFWpB9xeXK6mx59Csp5MuDJlN"; 

            // 3) Cria/obtém pasta do aluno
            String pastaId = controller.createStudentFolder(aluno, parentId);
            System.out.println(">> Pasta criada ou existente: " + pastaId);

            // 4) Upload inicial
            java.io.File arquivo = new java.io.File("/home/naan/IC/ODS/teste.pdf");
            String firstFileId = controller.uploadFile(aluno, arquivo, pastaId);
            System.out.println(">> Arquivo enviado: " + firstFileId);

            // 5) Deleta e reenviá
            controller.deleteFile(aluno, firstFileId);
            System.out.println(">> Arquivo excluído: " + firstFileId);

            String newFileId = controller.uploadFile(aluno, arquivo, pastaId);
            System.out.println(">> Arquivo reenviado: " + newFileId);

            // 6) Finaliza submissão (tornar somente leitura, etc.)
            controller.finalizeSubmission(aluno, pastaId);
            System.out.println(">> Submissão finalizada para: " + aluno.getName());

            // 7) Tentativas após submissão (lançarão IllegalStateException)
            try {
                controller.uploadFile(aluno, arquivo, pastaId);
            } catch (IllegalStateException e) {
                System.out.println("⛔ " + e.getMessage());
            }
            try {
                controller.deleteFile(aluno, newFileId);
            } catch (IllegalStateException e) {
                System.out.println("⛔ " + e.getMessage());
            }

            // 8) Links
            System.out.println(">> Link da pasta: " + controller.getFolderLink(pastaId));
            System.out.println(">> Link do arquivo: " + controller.getFileLink(newFileId));

            // 9) Backup de BD (exemplo)
            java.io.File dump = new java.io.File("/home/naan/IC/ODS/db-dump.sql");
            controller.backupDatabase(dump, parentId);
            System.out.println(">> Backup de BD realizado.");

        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
        }
    }
}
