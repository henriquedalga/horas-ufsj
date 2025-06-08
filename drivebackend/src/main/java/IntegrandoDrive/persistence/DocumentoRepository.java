package IntegrandoDrive.persistence;

import IntegrandoDrive.model.Documento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentoRepository extends JpaRepository<Documento, Long> {
    
    List<Documento> findBySolicitacaoId(Long solicitacaoId);
    
    Optional<Documento> findByDriveFileId(String driveFileId);
    
    List<Documento> findByNomeOriginalContainingIgnoreCase(String nomeOriginal);
}