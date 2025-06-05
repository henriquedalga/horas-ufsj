package com.universidade.sighoras.controller;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO para transferência de dados de funcionário
 * O campo email é opcional
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FuncionarioDTO {
    
    private String nome;
    private String senha;
    private String email;
    
    /**
     * Construtor para criar um FuncionarioDTO apenas com nome e senha
     * @param nome Nome do funcionário
     * @param senha Senha do funcionário
     */
    public FuncionarioDTO(String nome, String senha) {
        this.nome = nome;
        this.senha = senha;
        this.email = null; // Email é opcional
    }
    
    /**
     * Verifica se o email foi fornecido
     * @return true se o email não for nulo nem vazio
     */
    public boolean hasEmail() {
        return email != null && !email.trim().isEmpty();
    }
}