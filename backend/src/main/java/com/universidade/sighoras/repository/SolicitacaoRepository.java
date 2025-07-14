package com.universidade.sighoras.repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.universidade.sighoras.entity.Solicitacao;

public interface SolicitacaoRepository  extends JpaRepository<Solicitacao, Long> {

    List<Solicitacao> findByStatus(String status);

    Solicitacao findByMatricula(Long matricula);

    Solicitacao findByMatriculaAndHoraTipo(Long matricula, String tipoHora);

    List<Solicitacao> findByNome(String nome);
    
}