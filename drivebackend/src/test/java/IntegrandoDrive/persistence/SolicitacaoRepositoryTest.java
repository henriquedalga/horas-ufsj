package IntegrandoDrive.persistence;

import IntegrandoDrive.model.Solicitacao;
import IntegrandoDrive.model.StatusSolicitacao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class SolicitacaoRepositoryTest {

    @Autowired
    private SolicitacaoRepository repo;

    @Test
    void findByAlunoMatriculaOrderByDataCriacaoDesc_deveRetornarOrdenado() {
        // prepara duas solicitações com datas diferentes
        Solicitacao antiga = new Solicitacao();
        antiga.setAlunoMatricula("A1");
        antiga.setAlunoNome("Aluno A1");
        antiga.setStatus(StatusSolicitacao.EM_PROCESSAMENTO);
        antiga.setDataCriacao(LocalDateTime.now().minusDays(2));
        repo.save(antiga);

        Solicitacao recente = new Solicitacao();
        recente.setAlunoMatricula("A1");
        recente.setAlunoNome("Aluno A1");
        recente.setStatus(StatusSolicitacao.EM_PROCESSAMENTO);
        recente.setDataCriacao(LocalDateTime.now());
        repo.save(recente);

        // executa
        List<Solicitacao> result = repo.findByAlunoMatriculaOrderByDataCriacaoDesc("A1");

        // verifica que está em ordem decrescente de data
        assertThat(result)
            .hasSize(2)
            .extracting(Solicitacao::getDataCriacao)
            .isSortedAccordingTo((d1, d2) -> d2.compareTo(d1));
    }

    @Test
    void findByAlunoAndStatus_queryCustomizada() {
        // prepara
        Solicitacao sol = new Solicitacao();
        sol.setAlunoMatricula("M2");
        sol.setAlunoNome("Aluno M2");
        sol.setStatus(StatusSolicitacao.APROVADA);
        sol.setDataCriacao(LocalDateTime.now());
        repo.save(sol);

        // executa
        List<Solicitacao> result = repo.findByAlunoAndStatus("M2", StatusSolicitacao.APROVADA);

        // valida
        assertThat(result)
            .hasSize(1)
            .allMatch(s -> s.getAlunoMatricula().equals("M2")
                       && s.getStatus() == StatusSolicitacao.APROVADA);
    }
}