package com.universidade.sighoras.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.universidade.sighoras.entity.Solicitacao;
import com.universidade.sighoras.service.SolicitacaoService;


@RestController
public class SolicitacaoController {
    
    @Autowired
    private SolicitacaoService service;

    @GetMapping("/solicitacoes")
    public ResponseEntity<?> solicitacoes() {
        List<Solicitacao> solicitacoes = service.listarSolicitacoes();
        return ResponseEntity.ok(solicitacoes);
    }

    @GetMapping("/solicitacoes/pendentes")
    public ResponseEntity<?> solicitacoesPendentes() {
        List<Solicitacao> solicitacoesPendentes = service.listarSolicitacoesPendentes();
        return ResponseEntity.ok(solicitacoesPendentes);
    }

    @GetMapping("/solicitacoes/aprovadas")
    public ResponseEntity<?> solicitacoesAprovadas() {
        List<Solicitacao> solicitacoesAprovadas = service.listarSolicitacoesAprovadas();
        return ResponseEntity.ok(solicitacoesAprovadas);
    }
    
}
