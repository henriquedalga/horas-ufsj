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

    public String authFuncionario(String email, String senha) {
        // Aqui você pode implementar a lógica de autenticação do funcionário
        // Por exemplo, verificar se o email e a senha estão corretos
        System.out.println("Autenticando funcionário com email: " + email + " e senha: " + senha);
        funcionario = funcionarioRepository.findByEmail(email);
        System.out.println("Funcionario: " + funcionario);
        if (funcionario != null && senha.matches(funcionario.getSenha())) {
            return UUID.randomUUID().toString();
        }
        return null; // Retorna null se as credenciais forem inválidas
    }

}
