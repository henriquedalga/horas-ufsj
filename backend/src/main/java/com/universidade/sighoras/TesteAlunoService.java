package com.universidade.sighoras;

import com.universidade.sighoras.controller.SolicitacaoResponseDTO;
import com.universidade.sighoras.entity.HoraTipo;
import com.universidade.sighoras.entity.Solicitacao;
import com.universidade.sighoras.entity.Arquivo;
import com.universidade.sighoras.service.AlunoService;
import IntegrandoDrive.service.FileService;
import IntegrandoDrive.persistence.DrivePersistence;
import com.universidade.sighoras.repository.SolicitacaoRepository;
import com.universidade.sighoras.repository.ArquivoRepository;
import org.mockito.Mockito;
import org.springframework.web.multipart.MultipartFile;

import com.google.api.services.drive.Drive;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.*;

/**
 * Teste de integração "linear" do AlunoService usando o FileService real (Google Drive)
 * sem necessidade de JUnit, via um método main.
 */
public class TesteAlunoService{
       private static MultipartFile createStub(String originalFilename) {
              return new MultipartFile() {
                     @Override public String getName()                { return ""; }
                     @Override public String getOriginalFilename()   { return originalFilename; }
                     @Override public String getContentType()        { return "application/pdf"; }
                     @Override public boolean isEmpty()              { return false; }
                     @Override public long getSize()                 { return 1L; }
                     @Override public byte[] getBytes()              { return new byte[0]; }
                     @Override public java.io.InputStream getInputStream() { return null; }
                     @Override public void transferTo(File dest) throws IOException {
                            dest.createNewFile();
                     }
              };
       }
       public static void main(String[] args) throws IOException, GeneralSecurityException {
              // 1) Inicializa cliente do Drive
              Drive drive = DrivePersistence.initDriveService();
              // 2) Cria persistence e FileService reais
              DrivePersistence persistence = new DrivePersistence(drive);
              FileService fileService = new FileService(persistence);

              // 3) Repositórios podem ser mocks ou stubs (não tocam no BD)
              SolicitacaoRepository solicitacaoRepo = Mockito.mock(SolicitacaoRepository.class);
              ArquivoRepository arquivoRepo       = Mockito.mock(ArquivoRepository.class);

              // 4) Instancia AlunoService com FileService real
              AlunoService alunoService = new AlunoService(fileService, solicitacaoRepo, arquivoRepo);

              long matriculaTeste = 111L;
              String nomeAluno   = "ajda Teste";
              HoraTipo tipoHora  = HoraTipo.MONITORIA;

              // ==== 1) Teste processarDocumentos com Drive real ====
              System.out.println("==> Teste: processarDocumentos (Drive real)");
              Mockito.when(solicitacaoRepo.findByMatriculaOrderByDataSolicitacaoDesc(matriculaTeste))
                     .thenReturn(Collections.emptyList());
              Mockito.when(solicitacaoRepo.save(Mockito.any(Solicitacao.class)))
                     .thenAnswer(inv -> {
                            Solicitacao s = inv.getArgument(0);
                            if (s.getId() == null) s.setId(100L);
                            return s;
                     });

              MultipartFile f1 = createStub("doc1.pdf");
              MultipartFile f2 = createStub("doc2.pdf");
              MultipartFile f3 = createStub("doc3.pdf");

              // Executa envio e captura DTO com link da pasta
              SolicitacaoResponseDTO resp = alunoService.processarDocumentos(
                     new MultipartFile[]{f1},
                     matriculaTeste,
                     nomeAluno,
                     tipoHora
              );

              System.out.println("Status: " + resp.getStatus());
              System.out.println("Link pasta: " + resp.getLinkPasta());
              resp.getArquivos().forEach(d ->
                     System.out.println("Doc: " + d.getNomeArquivo() + " -> " + d.getDriveUrl())
              );

              // ==== 2) Teste buscarSolicitacoesPorAluno ====
              System.out.println("==> Teste: buscarSolicitacoesPorAluno");
              Mockito.when(solicitacaoRepo.findByMatriculaOrderByDataSolicitacaoDesc(matriculaTeste))
                     .thenReturn(List.of(
                            new Solicitacao(100L, matriculaTeste, nomeAluno, null, tipoHora,
                                          "DOCUMENTOS_ENVIADOS", "", "", resp.getLinkPasta())
                     ));
              alunoService.buscarSolicitacoesPorAluno(matriculaTeste)
                     .forEach(System.out::println);

              // ==== 3) Teste buscarSolicitacao(id) ====
              System.out.println("==> Teste: buscarSolicitacao(id)");
              Mockito.when(solicitacaoRepo.findById(100L))
                     .thenReturn(Optional.of(
                            new Solicitacao(100L, matriculaTeste, nomeAluno, null, tipoHora,
                                          "DOCUMENTOS_ENVIADOS", "", "", resp.getLinkPasta())
                     ));
              System.out.println(alunoService.buscarSolicitacao(100L));

              // ==== 4) Teste finalizarSubmissao usando ID puro extraído ====
              System.out.println("==> Teste: finalizarSubmissao");
              alunoService.finalizarSubmissao(100L, "Coment OK");
              System.out.println("Finalização executada com sucesso para folder: " + resp.getLinkPasta());

              // ==== 5) Verifica que, após finalização, não é permitido adicionar nem excluir ====
              System.out.println("==> Teste pós-finalização: upload proibido");
              try {
                     alunoService.processarDocumentos(new MultipartFile[]{f1}, matriculaTeste, nomeAluno, tipoHora);
              } catch (Exception e) {
                     System.out.println("⛔ Exceção esperada ao tentar upload: " + e.getMessage());
              }

              System.out.println("==> Teste pós-finalização: exclusão proibida");
              Arquivo arq = new Arquivo(); arq.setId(200L); arq.setIdSolicitacao(100L);
              Mockito.when(arquivoRepo.findById(200L)).thenReturn(Optional.of(arq));
              Mockito.when(solicitacaoRepo.findById(100L))
                     .thenReturn(Optional.of(
                            new Solicitacao(100L, matriculaTeste, nomeAluno, null, tipoHora,
                                          "FINALIZADA", "", "", resp.getLinkPasta())
                     ));
              try {
                     alunoService.excluirDocumento(200L);
              } catch (Exception e) {
                     System.out.println("⛔ Exceção esperada ao tentar excluir: " + e.getMessage());
              }
       }
}
