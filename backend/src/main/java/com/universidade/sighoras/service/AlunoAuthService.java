package com.universidade.sighoras.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.universidade.sighoras.dto.AlunoDTO;
import com.universidade.sighoras.exception.ApiExternaException;

import jakarta.servlet.http.HttpSession;

@Service
public class AlunoAuthService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    
    @Value("${api.externa.aluno.url}")
    private String apiAlunoUrl;
    
    // Nome das chaves usadas na sessão
    private static final String SESSION_TOKEN_KEY = "alunoToken";
    private static final String SESSION_ALUNO_DATA_KEY = "alunoData";

    public AlunoAuthService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }
    
    /**
     * Valida token enviado pelo frontend e busca informações do aluno na API externa
     * 
     * @param token Token Bearer recebido do frontend
     * @param session Sessão HTTP atual
     * @return DTO com as informações do aluno
     */
    public AlunoDTO validateTokenAndFetchAlunoData(String token, HttpSession session) {
        try {
            // Verifica se já temos os dados do aluno em sessão para este token
            if (session.getAttribute(SESSION_TOKEN_KEY) != null && 
                token.equals(session.getAttribute(SESSION_TOKEN_KEY)) &&
                session.getAttribute(SESSION_ALUNO_DATA_KEY) != null) {
                
                // Retorna os dados já armazenados na sessão
                return (AlunoDTO) session.getAttribute(SESSION_ALUNO_DATA_KEY);
            }
            
            // Prepara cabeçalho da requisição com o token
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            // Faz a requisição para a API externa
            ResponseEntity<String> response = restTemplate.exchange(
                    apiAlunoUrl + "/alunos/me", 
                    HttpMethod.GET, 
                    entity, 
                    String.class);
            
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new ApiExternaException("Falha na comunicação com a API externa: " + response.getStatusCode());
            }
            
            // Converte a resposta JSON para um objeto AlunoDTO
            JsonNode root = objectMapper.readTree(response.getBody());
            AlunoDTO alunoDTO = new AlunoDTO();
            alunoDTO.setMatricula(root.path("matricula").asLong());
            alunoDTO.setNome(root.path("nome").asText());
            alunoDTO.setCurso(root.path("curso").asText());
            
            // Guarda o token e dados do aluno na sessão
            session.setAttribute(SESSION_TOKEN_KEY, token);
            session.setAttribute(SESSION_ALUNO_DATA_KEY, alunoDTO);
            
            return alunoDTO;
            
        } catch (Exception e) {
            throw new ApiExternaException("Erro ao processar dados do aluno: " + e.getMessage(), e);
        }
    }
    
    /**
     * Obtém os dados do aluno da sessão atual
     * 
     * @param session Sessão HTTP atual
     * @return DTO com as informações do aluno ou null se não encontrado
     */
    public AlunoDTO getAlunoFromSession(HttpSession session) {
        return (AlunoDTO) session.getAttribute(SESSION_ALUNO_DATA_KEY);
    }
    
    /**
     * Verifica se o token na sessão é válido
     * 
     * @param session Sessão HTTP atual
     * @return true se o token existir na sessão
     */
    public boolean hasValidSession(HttpSession session) {
        return session.getAttribute(SESSION_TOKEN_KEY) != null && 
               session.getAttribute(SESSION_ALUNO_DATA_KEY) != null;
    }
    
    /**
     * Invalida a sessão do aluno
     * 
     * @param session Sessão HTTP atual
     */
    public void invalidateSession(HttpSession session) {
        session.removeAttribute(SESSION_TOKEN_KEY);
        session.removeAttribute(SESSION_ALUNO_DATA_KEY);
    }
}