package com.universidade.sighoras.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.universidade.sighoras.entity.Funcionario;

public interface FuncionarioRepository extends JpaRepository<Funcionario, Long> {
    Funcionario findByEmail(String email);
    
}
