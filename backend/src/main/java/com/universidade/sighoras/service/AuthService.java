package com.universidade.sighoras.service;

import com.universidade.sighoras.entity.Funcionario;
import com.universidade.sighoras.repository.FuncionarioRepository;

public class AuthService {
    
    private FuncionarioRepository funcionarioRepository;
    
    private Funcionario funcionario;

    public String authFuncionario(String nome, String senha) {
        // Aqui você pode implementar a lógica de autenticação do funcionário
        // Por exemplo, verificar se o email e a senha estão corretos
        funcionario = funcionarioRepository.findByNome(nome);
        if (senha.matches(funcionario.getSenha())) {
            return "Funcionário autenticado com sucesso!";

        }
        return "Falha na autenticação do funcionário!";
    }

}
