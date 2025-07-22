package com.universidade.sighoras.service;

import com.universidade.sighoras.HorasUfsjApplication;
import com.universidade.sighoras.entity.HoraTipo;
import com.universidade.sighoras.entity.Arquivo;
import com.universidade.sighoras.entity.Solicitacao;
import com.universidade.sighoras.service.ArquivoService;
import com.universidade.sighoras.service.SolicitacaoService;
import com.universidade.sighoras.service.AlunoService;
import IntegrandoDrive.service.FileService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FuncionarioEdicaoTest {

    private static final String PARENT_ID_COMP = "1kr-It-ec_y9gCz62gF5TevDm3q-9knkl";
    private static final String PARENT_ID_EXT = "1ouyNWdAy0SlDKrZN_ixPYityxQ1UtVdv";

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(HorasUfsjApplication.class);
        app.setWebApplicationType(WebApplicationType.NONE);
        try (ConfigurableApplicationContext ctx = app.run(args)) {

            SolicitacaoService solicitacaoService = ctx.getBean(SolicitacaoService.class);
            ArquivoService arquivoService = ctx.getBean(ArquivoService.class);
            AlunoService alunoService = ctx.getBean(AlunoService.class);
            FileService fileService = ctx.getBean(FileService.class);

            // 0) Criar 3 solicitações e adicionar 2 arquivos em cada
            System.out.println("\n=== SETUP: criando 3 solicitações de aluno com arquivos ===");
            for (int i = 1; i <= 3; i++) {
                String nome = "Aluno" + i;
                // escolhe o enum — só EXTENSAO ou COMPLEMENTAR
                HoraTipo tipoEnum = (i % 2 == 0)
                    ? HoraTipo.EXTENSAO
                    : HoraTipo.COMPLEMENTAR;

                // cria a solicitacao (retorno void)
                Long matricula = 1000L + i;
                solicitacaoService.criarSolicitacao(
                    matricula,
                    nome,
                    tipoEnum
                );
                // recupera a solicitacao criada
                List<Solicitacao> list = solicitacaoService.listarSolicitacoesPorNome(nome);
                if (list.isEmpty()) {
                    System.err.println("Falha ao criar solicitação para " + nome);
                    continue;
                }
                Solicitacao nova = list.get(0);
                // Corrigir ordem de argumentos em printf: incluir nome e tipo
                System.out.printf(
                    "Solicitação criada: ID=%d, Nome=%s, Tipo=%s, Pasta=%s%n",
                    nova.getId(), nova.getNome(), nova.getHoraTipo(), nova.getLinkPasta()
                );

                // adicionar 2 arquivos via AlunoService
                for (int j = 1; j <= 2; j++) {
                    String filename = "arquivo_" + i + "_" + j + ".txt";
                    MockMultipartFile file = new MockMultipartFile(
                        "file", filename,
                        "text/plain",
                        ("conteudo do arquivo " + i + "-" + j).getBytes(StandardCharsets.UTF_8)
                    );
                    alunoService.adicionarArquivo(nova.getId(), file);
                    System.out.printf("  + Arquivo enviado: %s%n", filename);
                }
            }

            // 1) Buscar todas as solicitações para edição
            System.out.println("\n=== Setup: buscar solicitações criadas ===");
            List<Solicitacao> todas = solicitacaoService.listarSolicitacoes();
            if (todas.isEmpty()) {
                System.err.println("Nenhuma solicitação encontrada.");
                System.exit(1);
            }

            // 2) Aprovar a primeira
            Solicitacao sol1 = todas.get(0);
            System.out.println("=== TESTE: Aprovar Solicitação ID=" + sol1.getId() + " ===");
            solicitacaoService.aprovarSolicitacao(sol1.getId());
            Solicitacao solAprov = solicitacaoService.obterSolicitacaoPorId(sol1.getId());
            assertEqual("Aprovada", solAprov.getStatus(), "Status Aprovada");

            // 3) Reprovar a segunda com comentários (map link->comentário)
            if (todas.size() < 2) {
                System.err.println("Não há segunda solicitação para reprovação.");
                System.exit(1);
            }
            Solicitacao sol2 = todas.get(1);
            System.out.println("=== TESTE: Reprovar Solicitação ID=" + sol2.getId() + " ===");
            Map<String, String> comentarios = new HashMap<>();
            List<Arquivo> arquivosSol2 = arquivoService.listarPorSolicitacao(sol2.getId());
            for (Arquivo arq : arquivosSol2) {
                String nomeArq = arq.getNomeArquivo();
                String comentario = nomeArq.contains("_1") ? "Sem assinatura" : "Revisão necessária";
                comentarios.put(arq.getUrl(), comentario);
            }
            solicitacaoService.reprovarSolicitacao(sol2.getId(), comentarios);
            Solicitacao solRej = solicitacaoService.obterSolicitacaoPorId(sol2.getId());
            assertEqual("Rejeitada", solRej.getStatus(), "Status Rejeitada");

            // 4) Verificar comentários adicionados
            List<Arquivo> verif = arquivoService.listarPorSolicitacao(sol2.getId());
            for (Arquivo arq : verif) {
                String nomeArq = arq.getNomeArquivo();
                String esperado = nomeArq.contains("_1") ? "Sem assinatura" : "Revisão necessária";
                assertEqual(esperado, arq.getComent(), "Comentário atualizado para " + nomeArq);
            }

            System.out.println("\n=== TODOS TESTES DO FUNCIONARIO EXECUTADOS ===");

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static void assertEqual(Object expected, Object actual, String field) {
        if (expected == null ? actual != null : !expected.equals(actual)) {
            System.err.printf("ERRO [%s]: esperado [%s] mas foi [%s]%n", field, expected, actual);
        } else {
            System.out.printf("OK [%s]: %s%n", field, actual);
        }
    }
}
