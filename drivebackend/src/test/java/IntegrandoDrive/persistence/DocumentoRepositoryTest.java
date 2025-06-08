package IntegrandoDrive.persistence;

import IntegrandoDrive.model.Documento;
import IntegrandoDrive.model.Solicitacao;
import IntegrandoDrive.model.StatusSolicitacao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
// força o uso de um banco em memória (H2) mesmo havendo outros DataSources
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class DocumentoRepositoryTest {

    @Autowired
    private DocumentoRepository repo;

    @Autowired
    private SolicitacaoRepository solicitacaoRepo;

    @Test
    void findBySolicitacaoId_deveRetornarTodosDocumentosDaSolicitacao() {
        // cria solicitação
        Solicitacao sol = new Solicitacao();
        sol.setAlunoMatricula("X1");
        sol.setAlunoNome("Aluno X1");
        sol.setStatus(StatusSolicitacao.EM_PROCESSAMENTO);
        sol.setDataCriacao(LocalDateTime.now());
        sol = solicitacaoRepo.save(sol);

        // anexa dois documentos
        Documento d1 = new Documento();
        d1.setNomeOriginal("doc1.pdf");
        d1.setDriveFileId("id-doc1");
        d1.setSolicitacao(sol);
        repo.save(d1);

        Documento d2 = new Documento();
        d2.setNomeOriginal("doc2.pdf");
        d2.setDriveFileId("id-doc2");
        d2.setSolicitacao(sol);
        repo.save(d2);

        // executa
        List<Documento> docs = repo.findBySolicitacaoId(sol.getId());

        // valida
        assertThat(docs)
            .hasSize(2)
            .extracting(Documento::getNomeOriginal)
            .containsExactlyInAnyOrder("doc1.pdf", "doc2.pdf");
    }
}
