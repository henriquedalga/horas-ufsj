package com.universidade.sighoras.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.universidade.sighoras.entity.Funcionario;
import com.universidade.sighoras.repository.FuncionarioRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;

@Service
public class AuthService {
    @Autowired
    private FuncionarioRepository funcionarioRepository;
    
    private Funcionario funcionario;
    
    // Chave secreta para assinar o JWT
    private final SecretKey jwtSecretKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);
    
    // Duração do token em milissegundos (3 horas)
    private final long JWT_EXPIRATION = 3 * 60 * 60 * 1000;

    public String authFuncionario(String email, String senha) {
        System.out.println("Autenticando funcionário com email: " + email);
        funcionario = funcionarioRepository.findByEmail(email);
        
        // Verifica se o funcionário existe e a senha está correta
        // Nota: a comparação com equals é mais adequada que matches() se não estiver usando regex
        if (funcionario != null && senha.matches(funcionario.getSenha())) {
            return generateJwtToken(funcionario);
        }
        
        return null; // Retorna null se as credenciais forem inválidas
    }
    
    /**
     * Gera um token JWT com informações do funcionário
     */
    private String generateJwtToken(Funcionario funcionario) {
        // Data atual
        Date now = new Date();
        
        // Data de expiração
        Date expiryDate = new Date(now.getTime() + JWT_EXPIRATION);
        
        // Claims (dados) que serão incluídos no token
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", funcionario.getId());
        claims.put("email", funcionario.getEmail());
        claims.put("role", "ROLE_ADMIN");
        
        // Gera o token JWT
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(funcionario.getEmail())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(jwtSecretKey, SignatureAlgorithm.HS512)
                .compact();
    }
    
    /**
     * Método para validar um token JWT
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(jwtSecretKey)
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Método para extrair o email do token
     */
    public String getEmailFromToken(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(jwtSecretKey)
            .build()
            .parseClaimsJws(token)
            .getBody()
            .getSubject();
    }

    
}