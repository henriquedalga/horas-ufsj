package com.universidade.sighoras.controller;

import com.universidade.sighoras.dto.SolicitacaoRequestDTO;
import com.universidade.sighoras.entity.Solicitacao;
import com.universidade.sighoras.service.AlunoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
public class AlunoRestController {

    private final AlunoService alunoService;

    public AlunoRestController(AlunoService alunoService) {
        this.alunoService = alunoService;
    }

    /**
     * RF1.1 Criar nova solicitação
     */
    @PostMapping("/aluno/solicitacao/extensao")
    public ResponseEntity<Solicitacao> verificarSolicitacao(@RequestBody SolicitacaoRequestDTO solicitacaoDTO) {
    // Chama o serviço para criar a solicitação
    Solicitacao sol = alunoService.verificarOuCriarSolicitacao(
            solicitacaoDTO.getMatricula(), 
            solicitacaoDTO.getNome(), 
            solicitacaoDTO.getHoraTipo()
    );
    return ResponseEntity.ok(sol);
}

    /**
     * RF1.2 Atualizar status da solicitação
     */
    @PutMapping("/solicitacao/{matricula}/status")
    public ResponseEntity<Void> atualizarStatus(
            @PathVariable Long matricula,
            @RequestParam String status
    ) {
        return alunoService.atualizarStatus(matricula, status);
    }

    /**
     * RF2.1 Adicionar arquivo (chama AlunoService.verificarPermissaoModificacao)
     */
    @PostMapping("/solicitacao/{id}/arquivo")
    public ResponseEntity<Void> adicionarArquivo(@PathVariable Long idSolicitacao,
                                                @RequestParam("arquivo") MultipartFile arquivo) {
        alunoService.adicionarArquivo(idSolicitacao, arquivo);
        return ResponseEntity.ok().build();
    }

    /**
     * RF2.2 Remover arquivo (chama AlunoService.verificarPermissaoModificacao)
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
     * RF3.1 Listar todas as solicitações
     */
    @GetMapping("/solicitacoes")
    public ResponseEntity<List<Solicitacao>> listarTodas() {
        return alunoService.listarTodas();
    }

    /**
     * RF3.2 Buscar solicitação por ID
     */
    @GetMapping("/solicitacao/{id}")
    public ResponseEntity<Solicitacao> buscarPorId(
            @PathVariable Long id
    ) {
        return alunoService.buscarPorId(id);
    }

    /**
     * RF3.3 Listar solicitações por nome do aluno
     */
    @GetMapping("/solicitacoes/por-nome")
    public ResponseEntity<List<Solicitacao>> listarPorNome(
            @RequestParam String nome
    ) {
        return alunoService.listarPorNome(nome);
    }


    /////////
    /// Métodos de testes para utilizar no postman 
    //////// 
    
    @PostMapping("/solicitacao/add/arquivo/{idSolicitacao}") 
    public ResponseEntity<Void> adicionarArquivoPost(@PathVariable("idSolicitacao")  Long idSolicitacao,
                                                @RequestParam("arquivo") MultipartFile arquivo) {
        System.out.println("==================================");
        System.out.println("Recebendo requisição para adicionar arquivo");
        System.out.println("ID da Solicitação: " + idSolicitacao);
        System.out.println("Nome do arquivo: " + (arquivo != null ? arquivo.getOriginalFilename() : "null"));
        System.out.println("Tamanho do arquivo: " + (arquivo != null ? arquivo.getSize() + " bytes" : "null"));
        System.out.println("==================================");
         try {
         alunoService.adicionarArquivo(idSolicitacao, arquivo);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            System.err.println("Erro ao processar arquivo: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }
}
