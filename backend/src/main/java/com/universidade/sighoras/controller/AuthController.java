package com.universidade.sighoras.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import java.util.Map;

import com.universidade.sighoras.entity.Funcionario;
import com.universidade.sighoras.service.AuthService;
import com.universidade.sighoras.service.FakeSigaa;
import com.universidade.sighoras.service.FuncionarioDuplicadoException;
import com.universidade.sighoras.service.FuncionarioService;

@RestController
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private FuncionarioService funcionarioService;

    @Autowired
    private FakeSigaa fakeSigaa;

    @GetMapping("/auth/funcionario")
    public ResponseEntity<?> auth(@RequestParam String nome, @RequestParam String senha) {

        // Aqui você pode chamar o serviço de autenticação
        String token = authService.authFuncionario(nome, senha);
        if (token != null) {
            return ResponseEntity.ok(Map.of("token", token));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciais inválidas");
        }
    }

    @PostMapping("/funcionarios")
    public ResponseEntity<?> cadastrarFuncionario(@RequestBody FuncionarioDTO funcionarioDTO) {
        try {
            Funcionario funcionario = funcionarioService.cadastrarFuncionario(
                funcionarioDTO.getNome(), 
                funcionarioDTO.getEmail(), 
                funcionarioDTO.getSenha()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(funcionario);
        } catch (FuncionarioDuplicadoException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao cadastrar funcionário");
        }
    }

    @PostMapping("/sigaa/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO loginDTO) {
        Map<String, String> resultado = fakeSigaa.autenticar(loginDTO.getCpf(), loginDTO.getSenha());
        
        if (resultado != null) {
            return ResponseEntity.ok(resultado);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciais inválidas");
        }
    }
    
    @PostMapping("/sigaa/logout")
    public ResponseEntity<?> logout(@RequestBody Map<String, String> body) {
        String token = body.get("token");
        
        if (token != null && fakeSigaa.validarToken(token)) {
            fakeSigaa.revogarToken(token);
            return ResponseEntity.ok().body("Logout realizado com sucesso");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token inválido");
        }
    }
    
}
