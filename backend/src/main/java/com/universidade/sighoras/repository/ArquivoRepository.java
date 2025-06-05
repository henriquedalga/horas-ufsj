package com.universidade.sighoras.repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.universidade.sighoras.entity.Arquivo;

public interface ArquivoRepository  extends JpaRepository<Arquivo, Long> {

    
    List<Arquivo> findByIdSolicitacao(Long idSolicitacao);
}

