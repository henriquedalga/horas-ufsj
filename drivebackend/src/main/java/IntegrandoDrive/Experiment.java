package IntegrandoDrive;

import IntegrandoDrive.persistence.DrivePersistence;
import IntegrandoDrive.service.FileService;
import com.google.api.services.drive.Drive;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;

public class Experiment {
    public static void main(String[] args) {
        try {
            // 1) inicializa o HTTP client do Drive
            Drive drive = DrivePersistence.initDriveService();

            // 2) cria persistence e service
            DrivePersistence persistence = new DrivePersistence(drive);
            FileService fileService = new FileService(persistence);

            String nomeAluno   = "João";
            String parentId    = "1TIFxvdsCFWpB9xeXK6mx59Csp5MuDJlN";

            // 3) cria (ou obtém) a pasta do aluno
            String pastaId = fileService.createFolder(nomeAluno, parentId);
            System.out.println(">> Pasta criada ou existente: " + pastaId);

            // 4) faz upload de um arquivo qualquer
            File arquivo = new File("/home/naan/IC/ODS/teste.pdf");
            String firstFileId = fileService.uploadFile(arquivo, pastaId, "EM_PROCESSAMENTO");
            System.out.println(">> Arquivo enviado: " + firstFileId);

            // 5) exclui e re‑envia
            fileService.deleteFile(firstFileId, "EM_PROCESSAMENTO");
            System.out.println(">> Arquivo excluído: " + firstFileId);

            String newFileId = fileService.uploadFile(arquivo, pastaId, "EM_PROCESSAMENTO");
            System.out.println(">> Arquivo reenviado: " + newFileId);

            // 6) finaliza submissão
            fileService.finalizeSubmission(pastaId);
            System.out.println(">> Submissão finalizada para: " + nomeAluno);

            // 7) tenta operações proibidas após finalização
            try {
                fileService.uploadFile(arquivo, pastaId, "FINALIZADA");
            } catch (IllegalStateException e) {
                System.out.println("⛔ " + e.getMessage());
            }
            try {
                fileService.deleteFile(newFileId, "FINALIZADA");
            } catch (IllegalStateException e) {
                System.out.println("⛔ " + e.getMessage());
            }

            // 8) imprime links
            System.out.println(">> Link da pasta: " + fileService.getFolderLink(pastaId));
            System.out.println(">> Link do arquivo: " + fileService.getFileLink(newFileId));

        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
        }
    }
}
