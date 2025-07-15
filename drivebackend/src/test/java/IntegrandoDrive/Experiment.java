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

            String nomeAluno = "João";
            String parentId  = "1TIFxvdsCFWpB9xeXK6mx59Csp5MuDJlN";

            // 3) cria (ou obtém) a pasta do aluno
            String pastaId = fileService.createFolder(nomeAluno, parentId);
            System.out.println(">> Pasta criada ou existente: " + pastaId);

            // 4) faz upload de um arquivo qualquer
            File arquivo = new File("teste.pdf");
            String firstFileId = fileService.uploadFile(arquivo, pastaId);
            System.out.println(">> Arquivo enviado: " + firstFileId);

            // 5) exclui e re‑envia (ainda EM_PROCESSAMENTO)
            fileService.deleteFile(firstFileId);
            System.out.println(">> Arquivo excluído: " + firstFileId);

            String newFileId = fileService.uploadFile(arquivo, pastaId);
            System.out.println(">> Arquivo reenviado: " + newFileId);

            // 6) finaliza submissão (torna todos os arquivos read-only)
            fileService.finalizeSubmission(pastaId);
            System.out.println(">> Submissão finalizada para: " + nomeAluno);

            // 7) tenta operações proibidas após finalização (status FINALIZADA)
            if (fileService.isFolderReadOnly(pastaId)) {
                System.out.println(" Pasta está em modo somente leitura (submetido)");
            } else {
                System.err.println("ERRO: Pasta deveria estar em modo somente leitura!");
            }
            // 8) rejeita submissão (torna todos editáveis de novo)
            fileService.rejectSubmission(pastaId);
            System.out.println(">> Submissão rejeitada para: " + nomeAluno);
            if (!fileService.isFolderReadOnly(pastaId)) {
                System.out.println("Pasta voltou a ser editável após rejeição");
            } else {
                System.err.println("ERRO: Pasta ainda está em modo somente leitura!");
            }

            // 9) imprime links
            System.out.println(">> Link da pasta: " + fileService.getFolderLink(pastaId));
            System.out.println(">> Link do arquivo existente: " + fileService.getFileLink(newFileId));

        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
        }
    }
}
