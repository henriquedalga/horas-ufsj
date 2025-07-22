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
            // 1) Inicializa as duas contas do Google Drive
            Drive driveComp = DrivePersistence.initDriveService("/credentials.json");
            Drive driveExt  = DrivePersistence.initDriveService("/credentials.json");

            // 2) Cria os dois persistence
            DrivePersistence persistenceComp = new DrivePersistence(driveComp, "1kr-It-ec_y9gCz62gF5TevDm3q-9knkl");
            DrivePersistence persistenceExt  = new DrivePersistence(driveExt, "1ouyNWdAy0SlDKrZN_ixPYityxQ1UtVdv");

            // 3) Cria FileService passando os dois persistence
            FileService fileService = new FileService(persistenceComp, persistenceExt);

            // 4) Escolhe tipo de hora a testar (0=complementar, 1=extensão)
            int hourType = 1;

            // 5) Define nome do aluno e pasta pai correspondente
            String nomeAluno = "TesteHoje";

            // 6) Cria ou recupera a pasta do aluno
            String pastaId = fileService.createFolder(nomeAluno, hourType);
            System.out.println(">> Pasta criada ou existente: " + pastaId);

            // 7) Faz upload de um arquivo
            File arquivo = new File("teste.pdf");
            String firstFileId = fileService.uploadFile(arquivo, pastaId, hourType);
            System.out.println(">> Arquivo enviado: " + firstFileId);

            // 8) Exclui e reenvia (ainda EM_PROCESSAMENTO)
            fileService.deleteFile(firstFileId, hourType);
            System.out.println(">> Arquivo excluído: " + firstFileId);

            String newFileId = fileService.uploadFile(arquivo, pastaId, hourType);
            System.out.println(">> Arquivo reenviado: " + newFileId);

            // 9) Finaliza submissão (torna read-only)
            fileService.finalizeSubmission(pastaId, hourType);
            System.out.println(">> Submissão finalizada para: " + nomeAluno);

            if (fileService.isFolderReadOnly(pastaId, hourType)) {
                System.out.println(" Pasta está em modo somente leitura (submetido)");
            } else {
                System.err.println("ERRO: Pasta deveria estar em modo somente leitura!");
            }

            // 10) Rejeita submissão (torna editável novamente)
            fileService.rejectSubmission(pastaId, hourType);
            System.out.println(">> Submissão rejeitada para: " + nomeAluno);

            if (!fileService.isFolderReadOnly(pastaId, hourType)) {
                System.out.println("Pasta voltou a ser editável após rejeição");
            } else {
                System.err.println("ERRO: Pasta ainda está em modo somente leitura!");
            }

            // 11) Imprime links
            System.out.println(">> Link da pasta: " + fileService.getFolderLink(pastaId, hourType));
            System.out.println(">> Link do arquivo: " + fileService.getFileLink(newFileId, hourType));

        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
        }
    }
}
