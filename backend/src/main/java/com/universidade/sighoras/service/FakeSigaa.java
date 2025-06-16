package com.universidade.sighoras.service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;

/**
 * Serviço que simula a API externa do SIGAA
 */
@Service
public class FakeSigaa {
    
    // Mapa que simula o banco de dados do SIGAA (CPF -> [senha, matrícula, email])
    private final Map<String, String[]> alunosDb = new HashMap<>();
    
    // Mapa para armazenar tokens ativos (token -> cpf)
    private final Map<String, String> tokensAtivos = new HashMap<>();
    
    public FakeSigaa() {
        // Pré-cadastro de alguns alunos para teste
        alunosDb.put("12345678900", new String[]{"senha123", "20210001", "aluno1@universidade.edu.br"});
        alunosDb.put("98765432100", new String[]{"senha456", "20210002", "aluno2@universidade.edu.br"});
        alunosDb.put("11122233344", new String[]{"senha789", "20210003", "aluno3@universidade.edu.br"});
    }
    
    /**
     * Realiza autenticação do aluno
     * 
     * @param cpf CPF do aluno
     * @param senha Senha do aluno
     * @return Map contendo token, matricula e email se autenticação for bem-sucedida, null caso contrário
     */
    public Map<String, String> autenticar(String cpf, String senha) {
        // Verifica se o CPF existe e a senha está correta
        if (alunosDb.containsKey(cpf) && alunosDb.get(cpf)[0].equals(senha)) {
            String token = gerarToken();
            String matricula = alunosDb.get(cpf)[1];
            String email = alunosDb.get(cpf)[2];
            
            // Armazena o token gerado
            tokensAtivos.put(token, cpf);
            
            Map<String, String> resultado = new HashMap<>();
            resultado.put("token", token);
            resultado.put("matricula", matricula);
            resultado.put("email", email);
            return resultado;
        }
        
        return null;  // Autenticação falhou
    }
    
    /**
     * Verifica se um token é válido
     * 
     * @param token Token a ser verificado
     * @return true se o token for válido, false caso contrário
     */
    public boolean validarToken(String token) {
        return tokensAtivos.containsKey(token);
    }
    
    /**
     * Revoga um token (logout)
     * 
     * @param token Token a ser revogado
     */
    public void revogarToken(String token) {
        tokensAtivos.remove(token);
    }
    
    /**
     * Adiciona um novo aluno ao sistema simulado
     * 
     * @param cpf CPF do aluno
     * @param senha Senha do aluno
     * @param matricula Número de matrícula do aluno
     * @param email Email do aluno
     * @return true se o aluno foi adicionado, false se o CPF já existir
     */
    public boolean adicionarAluno(String cpf, String senha, String matricula, String email) {
        if (alunosDb.containsKey(cpf)) {
            return false;
        }
        
        alunosDb.put(cpf, new String[]{senha, matricula, email});
        return true;
    }
    
    /**
     * Gera um token UUID aleatório
     * 
     * @return Token gerado
     */
    private String gerarToken() {
        return UUID.randomUUID().toString();
    }
    
    /**
     * Recupera informações do aluno a partir do CPF
     * 
     * @param cpf CPF do aluno
     * @return Map contendo matricula e email, ou null se o CPF não existir
     */
    public Map<String, String> getInfoAluno(String cpf) {
        if (alunosDb.containsKey(cpf)) {
            Map<String, String> info = new HashMap<>();
            info.put("matricula", alunosDb.get(cpf)[1]);
            info.put("email", alunosDb.get(cpf)[2]);
            return info;
        }
        return null;
    }
}