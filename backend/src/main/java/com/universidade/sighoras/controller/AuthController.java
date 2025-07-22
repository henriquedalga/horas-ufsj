package com.universidade.sighoras.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import java.util.Map;

import com.universidade.sighoras.dto.FuncionarioDTO;
import com.universidade.sighoras.dto.LoginDTO;
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

    @GetMapping("/check")
    public ResponseEntity<String> check() {
        return ResponseEntity.ok("API está funcionando corretamente!");
    }
    

    @PostMapping("/auth/signin-admin")
    public ResponseEntity<?> auth(@RequestBody FuncionarioDTO funcionarioDTO) {

        // Aqui você pode chamar o serviço de autenticação
        String token = authService.authFuncionario(funcionarioDTO.getEmail(), funcionarioDTO.getSenha());
        if (token != null) {
            Map<String, Object> response = Map.of(
            "authToken", token,
            "username", funcionarioDTO.getEmail(), // Usando email como username
            "role", "admin" // Definindo role padrão para funcionários
            );
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciais inválidas");
        }
    }

    @PostMapping("/cadastrar/funcionario")
    public ResponseEntity<?> cadastrarFuncionario(@RequestBody FuncionarioDTO funcionarioDTO) {
        try {
            Funcionario funcionario = funcionarioService.cadastrarFuncionario(
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

    //SIGAA Authentication Endpoints
    @PostMapping("/auth/signin-student")
    public ResponseEntity<?> login(@RequestBody LoginDTO loginDTO) {
        System.out.println(" Recebendo requisição de login: " + loginDTO);
    
        Map<String, String> resultado = fakeSigaa.autenticar(loginDTO.getCpf(), loginDTO.getSenha());
    
        System.out.println(" Resultado da autenticação: " + resultado);
        
        if (resultado != null) {
        // Adaptando a resposta para o formato esperado pelo frontend
        Map<String, Object> response = Map.of(
            "authToken", resultado.get("token"),
            "username", resultado.get("matricula"),
            "role", "STUDENT" // Definindo role padrão para alunos
        );
        return ResponseEntity.ok(response);
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