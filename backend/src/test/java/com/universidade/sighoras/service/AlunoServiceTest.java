package com.universidade.sighoras.service;

import com.universidade.sighoras.HorasUfsjApplication;
import com.universidade.sighoras.service.AlunoService;
import com.universidade.sighoras.service.SolicitacaoService;
import com.universidade.sighoras.entity.Solicitacao;
import IntegrandoDrive.service.FileService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.List;

public class AlunoServiceTest {

    private static final String TEST_PARENT_ID = "1TIFxvdsCFWpB9xeXK6mx59Csp5MuDJlN";

    public static void main(String[] args) {
        // 1) Inicia contexto Spring Boot com profile "test"
        SpringApplication app = new SpringApplication(HorasUfsjApplication.class);
        app.setWebApplicationType(WebApplicationType.NONE); // sem servidor web
        //app.setAdditionalProfiles("test");
        try (ConfigurableApplicationContext ctx = app.run()) {

            AlunoService alunoService = ctx.getBean(AlunoService.class);
            SolicitacaoService solicitacaoService = ctx.getBean(SolicitacaoService.class);
            FileService fileService = ctx.getBean(FileService.class);

            // TESTE 1: criar solicitação
            System.out.println("=== TESTE 1: criarSolicitacao ===");
            Long matricula1 = 777L;
            String nome1 = "Aluno1";
            String tipo1 = "EXTENSAO";
            //String pasta1 = fileService.createFolder(nome1, TEST_PARENT_ID);
            alunoService.criarSolicitacao(matricula1, nome1, tipo1);

            List<Solicitacao> achadas1 = solicitacaoService.listarSolicitacoesPorNome(nome1);
            if (achadas1.isEmpty()) {
                System.err.println("ERRO: nenhuma solicitação encontrada para " + nome1);
            } else {
                Solicitacao sol1 = solicitacaoService.obterSolicitacaoPorId(achadas1.get(0).getId());
                assertEquals(matricula1, sol1.getMatricula(), "matricula");
                assertEquals(nome1, sol1.getNome(), "nome");
                //assertEquals(email1, sol1.getEmail(), "email");
                assertEquals("Aberta", sol1.getStatus(), "status");
                //assertEquals(pasta1, sol1.getLinkPasta(), "linkPasta");
            }

            // TESTE 2: atualizar status
            System.out.println("=== TESTE 2: atualizarStatus ===");
            Long matricula2 = 888L;
            String nome2 = "Aluno2";
            String pasta2 = fileService.createFolder(nome2, TEST_PARENT_ID);
            //alunoService.criarSolicitacao(matricula2, nome2, "status2@uni.br", "EXTENSAO", pasta2);
            alunoService.atualizarStatus(matricula2, "Aberto");

            Solicitacao sol2 = solicitacaoService.listarSolicitacoesPorNome(nome2).get(0);
            assertEquals("Aberto", sol2.getStatus(), "status após atualização");

            // TESTE 3: upload de arquivo
            System.out.println("=== TESTE 3: adicionarArquivo ===");
            Long matricula3 = 999L;
            String nome3 = "Aluno3";
            String pasta3 = fileService.createFolder(nome3, TEST_PARENT_ID);
            alunoService.criarSolicitacao(matricula3, nome3, "EXTENSAO");
            Solicitacao sol3 = solicitacaoService.listarSolicitacoesPorNome(nome3).get(0);

            File tmp = new File("teste.pdf");
            Files.writeString(tmp.toPath(), "Testando");
            org.springframework.mock.web.MockMultipartFile mp = new org.springframework.mock.web.MockMultipartFile(
                "file", "teste.pdf", "aplication/pdf", Files.readAllBytes(tmp.toPath()));
            alunoService.adicionarArquivo(sol3.getId(), mp);
            List<String> links = fileService.listFileLinks(pasta3);
            if (links.isEmpty()) {
                System.err.println("ERRO: nenhum link de arquivo retornado");
            } else {
                System.out.println("OK: encontrou " + links.size() + " arquivo(s) no Drive");
            }
            tmp.delete();

            // TESTE 4: listar arquivos (implemente conforme sua lógica)
            System.out.println("=== TESTE 4: listarArquivosDaSolicitacao ===");
            // aqui você chama alunoService.listarArquivos(sol3.getId()) ou similar
            // e imprime/verifica resultado

            System.out.println("=== TODOS TESTES EXECUTADOS ===");

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static void assertEquals(Object esperado, Object atual, String campo) {
        if (esperado == null ? atual != null : !esperado.equals(atual)) {
            System.err.printf("ERRO no campo '%s': esperado [%s] mas foi [%s]%n",
                              campo, esperado, atual);
        } else {
            System.out.printf("OK em '%s': %s%n", campo, atual);
        }
    }
}
