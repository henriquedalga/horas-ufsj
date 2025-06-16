package com.universidade.sighoras.service;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.universidade.sighoras.entity.Funcionario;
import com.universidade.sighoras.repository.FuncionarioRepository;
import com.universidade.sighoras.service.FuncionarioDuplicadoException;

@Service
public class FuncionarioService {

    private FuncionarioRepository funcionarioRepository;

    public FuncionarioService(FuncionarioRepository funcionarioRepository) {
        this.funcionarioRepository = funcionarioRepository;
    }

    public Funcionario cadastrarFuncionario(String nome, String senha, String email) throws FuncionarioDuplicadoException {
        // Verificar se já existe funcionário com este nome
        if (funcionarioRepository.findByNome(nome) != null) {
            throw new FuncionarioDuplicadoException("Já existe um funcionário cadastrado com o nome: " + nome);
        }
        
        // Verificar email apenas se foi fornecido
        if (email != null && !email.trim().isEmpty()) {
            if (funcionarioRepository.findByEmail(email) != null) {
                throw new FuncionarioDuplicadoException("Já existe um funcionário cadastrado com o email: " + email);
            }
        }
        
        Funcionario funcionario = new Funcionario();
        funcionario.setNome(nome);
        funcionario.setSenha(senha);
        funcionario.setEmail(email); // Pode ser null
        
        try {
            return funcionarioRepository.save(funcionario);
        } catch (DataIntegrityViolationException e) {
            throw new FuncionarioDuplicadoException("Erro ao cadastrar funcionário: possível duplicação de dados");
        }
    }
}
