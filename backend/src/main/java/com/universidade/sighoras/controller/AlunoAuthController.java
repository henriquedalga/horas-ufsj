package com.universidade.sighoras.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.universidade.sighoras.dto.AlunoDTO;
import com.universidade.sighoras.dto.TokenRequestDTO;
import com.universidade.sighoras.exception.ApiExternaException;
import com.universidade.sighoras.exception.NaoAutorizadoException;
import com.universidade.sighoras.service.AlunoAuthService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/auth/aluno")
public class AlunoAuthController {

    private final AlunoAuthService alunoAuthService;
    
    @Autowired
    public AlunoAuthController(AlunoAuthService alunoAuthService) {
        this.alunoAuthService = alunoAuthService;
    }
    
    /**
     * Endpoint para validar o token enviado pelo frontend
     */
    @PostMapping("/validate-token")
    public ResponseEntity<AlunoDTO> validateToken(@RequestBody TokenRequestDTO tokenRequest, HttpSession session) {
        try {
            AlunoDTO alunoDTO = alunoAuthService.validateTokenAndFetchAlunoData(
                    tokenRequest.getToken(), 
                    session);
            
            return ResponseEntity.ok(alunoDTO);
        } catch (ApiExternaException e) {
            return ResponseEntity.status(502).build(); // Bad Gateway
        } catch (Exception e) {
            return ResponseEntity.status(401).build(); // Unauthorized
        }
    }
    
    /**
     * Endpoint para verificar se o aluno está autenticado
     */
    @GetMapping("/me")
    public ResponseEntity<AlunoDTO> getAlunoInfo(HttpSession session) {
        if (!alunoAuthService.hasValidSession(session)) {
            return ResponseEntity.status(401).build(); // Unauthorized
        }
        
        AlunoDTO alunoDTO = alunoAuthService.getAlunoFromSession(session);
        return ResponseEntity.ok(alunoDTO);
    }
    
    /**
     * Endpoint para fazer logout
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpSession session) {
        alunoAuthService.invalidateSession(session);
        return ResponseEntity.ok().build();
    }
}