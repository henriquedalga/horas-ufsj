package com.universidade.sighoras.repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

import com.universidade.sighoras.entity.Arquivo;

public interface ArquivoRepository  extends JpaRepository<Arquivo, Long> {

    
    List<Arquivo> findByIdSolicitacao(Long idSolicitacao);
   Optional<Arquivo> findByUrl(String url);
    void deleteByUrl(String url);
}

