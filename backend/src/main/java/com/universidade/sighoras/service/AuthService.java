package com.universidade.sighoras.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.universidade.sighoras.entity.Funcionario;
import com.universidade.sighoras.repository.FuncionarioRepository;
import IntegrandoDrive.controller.FileController;

@Service
public class AuthService {
    @Autowired
    private FuncionarioRepository funcionarioRepository;
    
    private Funcionario funcionario;

    @Autowired
    private FileController fileController;

    public String authFuncionario(String nome, String senha) {
        // Aqui você pode implementar a lógica de autenticação do funcionário
        // Por exemplo, verificar se o email e a senha estão corretos
        funcionario = funcionarioRepository.findByNome(nome);
        if (senha.matches(funcionario.getSenha())) {
            return UUID.randomUUID().toString();
        }
        return "Falha na autenticação do funcionário!";
    }

}
