package com.universidade.sighoras.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
    // aprovar solicitação
    @PutMapping("/{id}/aprovar")
    public ResponseEntity<Void> aprovarSolicitacao(@PathVariable Long id) {
        try {
            service.aprovarSolicitacao(id);
            return ResponseEntity.ok().build();
        } catch (IOException e) {
            // você pode customizar o tratamento de erro aqui
            return ResponseEntity.status(500).build();
        }
    }

    // reprovar solicitação, recebendo map de comentários (driveLink → comentário)
    @PutMapping("/{id}/reprovar")
    public ResponseEntity<Void> reprovarSolicitacao(
            @PathVariable Long id,
            @RequestBody Map<String, String> comentarios
    ) {
        try {
            service.reprovarSolicitacao(id, comentarios);
            return ResponseEntity.ok().build();
        } catch (IOException e) {
            return ResponseEntity.status(500).build();
        }
    }
   
}
