package com.universidade.sighoras.controller;

import com.universidade.sighoras.entity.Solicitacao;
import com.universidade.sighoras.service.AlunoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/aluno")
@CrossOrigin(origins = "*")
public class AlunoRestController {

    private final AlunoService alunoService;

    public AlunoRestController(AlunoService alunoService) {
        this.alunoService = alunoService;
    }

    /**
     * Criar nova solicitação
     */
    @PostMapping("/solicitacao")
    public ResponseEntity<Void> criarSolicitacao(
            @RequestParam Long matricula,
            @RequestParam String nome,
            @RequestParam String email,
            @RequestParam String horaTipo,
            @RequestParam String linkPasta
    ) {
        return alunoService.criarSolicitacao(matricula, nome, email, horaTipo, linkPasta);
    }

    /**
     * Atualizar status da solicitação
     */
    @PutMapping("/solicitacao/{matricula}/status")
    public ResponseEntity<Void> atualizarStatus(
            @PathVariable Long matricula,
            @RequestParam String status
    ) {
        return alunoService.atualizarStatus(matricula, status);
    }

    /**
     * Adicionar arquivo (chama AlunoService.verificarPermissaoModificacao)
     */
    @PostMapping("/solicitacao/{id}/arquivo")
    public ResponseEntity<Void> adicionarArquivo(@PathVariable Long idSolicitacao,
                                                @RequestParam("arquivo") MultipartFile arquivo) {
        alunoService.adicionarArquivo(idSolicitacao, arquivo);
        return ResponseEntity.ok().build();
    }

    /**
     * Remover arquivo (chama AlunoService.verificarPermissaoModificacao)
     */
    @DeleteMapping("/solicitacao/{id}/arquivo")
    public ResponseEntity<Void> removerArquivo(
            @PathVariable("id") Long idSolicitacao,
            @RequestParam("link") String linkArquivo
    ) {
        alunoService.removerArquivo(idSolicitacao, linkArquivo);
        return ResponseEntity.ok().build();
    }

    /**
     * Finalizar submissão de solicitação: 
     * marca pasta como read‑only, envia e‑mail e atualiza status
     */
    @PostMapping("/solicitacao/{id}/finalizar")
    public ResponseEntity<Void> finalizarSolicitacao(
            @PathVariable("id") Long idSolicitacao
    ) {
        return alunoService.finalizarSolicitacao(idSolicitacao);
    }

    /**
     * Listar todas as solicitações
     */
    @GetMapping("/solicitacoes")
    public ResponseEntity<List<Solicitacao>> listarTodas() {
        return alunoService.listarTodas();
    }

    /**
     * Buscar solicitação por ID
     */
    @GetMapping("/solicitacao/{id}")
    public ResponseEntity<Solicitacao> buscarPorId(
            @PathVariable Long id
    ) {
        return alunoService.buscarPorId(id);
    }

    /**
     * Listar solicitações por nome do aluno
     */
    @GetMapping("/solicitacoes/por-nome")
    public ResponseEntity<List<Solicitacao>> listarPorNome(
            @RequestParam String nome
    ) {
        return alunoService.listarPorNome(nome);
    }
    /**
     * Lista todos os arquivos de uma solicitação
     */
    @GetMapping("/solicitacao/{id}/arquivos")
    public ResponseEntity<List<String>> listarArquivos(
            @PathVariable("id") Long idSolicitacao
    ) {
        return alunoService.listarArquivos(idSolicitacao);
    }
}
