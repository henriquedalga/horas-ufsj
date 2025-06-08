package IntegrandoDrive.persistence;

import IntegrandoDrive.model.Solicitacao;
import IntegrandoDrive.model.StatusSolicitacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SolicitacaoRepository extends JpaRepository<Solicitacao, Long> {
    
    List<Solicitacao> findByAlunoMatriculaOrderByDataCriacaoDesc(String matricula);
    
    List<Solicitacao> findByStatus(StatusSolicitacao status);
    
    @Query("SELECT s FROM Solicitacao s WHERE s.alunoMatricula = :matricula AND s.status = :status")
    List<Solicitacao> findByAlunoAndStatus(@Param("matricula") String matricula, 
                                          @Param("status") StatusSolicitacao status);
    
    List<Solicitacao> findByAlunoNomeContainingIgnoreCase(String nome);
}