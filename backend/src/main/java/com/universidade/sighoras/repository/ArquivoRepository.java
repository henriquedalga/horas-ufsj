package com.universidade.sighoras.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.universidade.sighoras.entity.Arquivo;

public interface ArquivoRepository  extends JpaRepository<Arquivo, Long> {
    
}

