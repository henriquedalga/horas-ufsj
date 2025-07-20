package com.universidade.sighoras.service;

import com.universidade.sighoras.HorasUfsjApplication;
import com.universidade.sighoras.service.AlunoService;
import com.universidade.sighoras.service.SolicitacaoService;
import com.universidade.sighoras.entity.HoraTipo;
import com.universidade.sighoras.entity.Solicitacao;
import IntegrandoDrive.service.FileService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

public class AlunoServiceTest {

    private static final String PARENT_ID_COMP = "1kr-It-ec_y9gCz62gF5TevDm3q-9knkl";
    private static final String PARENT_ID_EXT = "1ouyNWdAy0SlDKrZN_ixPYityxQ1UtVdv";

    public static void main(String[] args) {
        // Inicia o contexto da aplicação sem servidor web, usando o application.properties padrão
        SpringApplication app = new SpringApplication(HorasUfsjApplication.class);
        app.setWebApplicationType(WebApplicationType.NONE);
        try (ConfigurableApplicationContext ctx = app.run(args)) {

            AlunoService alunoService = ctx.getBean(AlunoService.class);
            SolicitacaoService solicitacaoService = ctx.getBean(SolicitacaoService.class);
            FileService fileService = ctx.getBean(FileService.class);

            // TESTE 1: criar solicitação
            System.out.println("=== TESTE 1: criarSolicitacao ===");
            Long matricula1 = 777L;
            String nome1 = "Aluno1";
            String email1 = "naan.vasconcelos2@gmail.com";
            String tipo1 = "EXTENSAO";
            String pasta1 = fileService.createFolder(nome1, PARENT_ID_EXT, 1);
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
                assertEquals(pasta1, sol1.getLinkPasta(), "linkPasta");
                File tmp = new File("teste.pdf");
                Files.writeString(tmp.toPath(), "Testando");
                var mp = new org.springframework.mock.web.MockMultipartFile(
                    "file", "teste1.pdf", "aplication/pdf", Files.readAllBytes(tmp.toPath()));
                alunoService.adicionarArquivo(sol1.getId(), mp);
                tmp.delete();
                System.out.println("=== FinalizarSolicitacao ===");
                alunoService.finalizarSolicitacao(sol1.getId());
            }


            // TESTE 2: atualizar status
            System.out.println("=== TESTE 2: atualizarStatus ===");
            Long matricula2 = 888L;
            String nome2 = "Aluno2";
            String pasta2 = fileService.createFolder(nome2, PARENT_ID_EXT, 1);
            alunoService.criarSolicitacao(matricula2, nome2, "EXTENSAO");
            Solicitacao sol2 = solicitacaoService.listarSolicitacoesPorNome(nome2).get(0);
            alunoService.finalizarSolicitacao(sol2.getId());

            alunoService.atualizarStatus(matricula2, "Rejeitada");
            sol2 = solicitacaoService.listarSolicitacoesPorNome(nome2).get(0);
            assertEquals("Rejeitada", sol2.getStatus(), "status após atualização");

            // TESTE 3: upload de arquivo
            System.out.println("=== TESTE 3: adicionarArquivo ===");
            Long matricula3 = 999L;
            String nome3 = "Aluno3";
            String pasta3 = fileService.createFolder(nome3, PARENT_ID_COMP, 0);
            alunoService.criarSolicitacao(matricula3, nome3, "COMPLEMENTAR");
            Solicitacao sol3 = solicitacaoService.listarSolicitacoesPorNome(nome3).get(0);

            File tmp = new File("teste.pdf");
            Files.writeString(tmp.toPath(), "Testando");
            org.springframework.mock.web.MockMultipartFile mp = new org.springframework.mock.web.MockMultipartFile(
                "file", "teste.pdf", "aplication/pdf", Files.readAllBytes(tmp.toPath()));
            alunoService.adicionarArquivo(sol3.getId(), mp);
            List<String> links = fileService.listFileLinks(pasta3, 0);
            if (links.isEmpty()) {
                System.err.println("ERRO: nenhum link de arquivo retornado");
            } else {
                System.out.println("OK: encontrou " + links.size() + " arquivo(s) no Drive");
            }
            tmp.delete();


            // // TESTE 4: listarArquivosDaSolicitacao
            // System.out.println("=== TESTE 4: listarArquivosDaSolicitacao ===");
            // List<String> arquivos = alunoService.listarArquivos(sol3.getId()).getBody();
            // if (arquivos == null || arquivos.isEmpty()) {
            //     System.err.println("ERRO: nenhum arquivo listado para a solicitação " + sol3.getId());
            // } else {
            //     System.out.println("OK: arquivos encontrados na solicitação " + sol3.getId() + ":");
            //     arquivos.forEach(link -> System.out.println(" - " + link));
            // }

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
