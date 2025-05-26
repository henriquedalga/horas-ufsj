package com.universidade.sighoras.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.universidade.sighoras.service.AuthService;

@RestController
public class AuthController {

    @Autowired
    private AuthService authService;

    @GetMapping("/auth/funcionario")
    public String auth(@RequestParam String nome, @RequestParam String senha) {
        // Aqui você pode chamar o serviço de autenticação
        authService.authFuncionario(nome, senha);
        return "Auth endpoint";
    }

    @GetMapping("/auth/Aluno")
    public String authAluno() {
        return "Auth Aluno endpoint";
    }

    @GetMapping("/auth/login")
    public String login() {
        return "Login endpoint";
    }
    
}
