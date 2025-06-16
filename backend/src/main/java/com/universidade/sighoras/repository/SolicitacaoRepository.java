package com.universidade.sighoras.repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.universidade.sighoras.entity.Solicitacao;

public interface SolicitacaoRepository extends JpaRepository<Solicitacao, Long> {

    // Busca por status (já existente)
    List<Solicitacao> findByStatus(String status);

    // Busca todas as solicitações de uma matrícula, da mais recente para a mais antiga
    List<Solicitacao> findByMatriculaOrderByDataSolicitacaoDesc(Long matricula);
}
