package com.universidade.sighoras.controller;

import com.universidade.sighoras.controller.SolicitacaoResponseDTO;
import com.universidade.sighoras.entity.HoraTipo;
import com.universidade.sighoras.service.AlunoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/aluno")
@CrossOrigin(origins = "*")
public class AlunoRestController {
    private final AlunoService alunoService;

    public AlunoRestController(AlunoService alunoService) {
        this.alunoService = alunoService;
    }

    // RF2.1: Receber documentos do front
    @PostMapping("/upload-documentos")
    public ResponseEntity<?> uploadDocumentos(
            @RequestParam("files") MultipartFile[] files,
            @RequestParam("matricula") Long matricula,
            @RequestParam("nome") String nome,
            @RequestParam("horaTipo") HoraTipo horaTipo) {
        try {
            SolicitacaoResponseDTO dto = alunoService.processarDocumentos(files, matricula, nome, horaTipo);
            return ResponseEntity.ok(dto);
        } catch (IllegalStateException e) {
            // submissão finalizada
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    // RF2.3: Enviar dados da solicitação para o front
    @GetMapping("/solicitacao/{id}")
    public ResponseEntity<?> buscarSolicitacao(@PathVariable Long id) {
        try {
            SolicitacaoResponseDTO response = alunoService.buscarSolicitacao(id);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/solicitacoes/aluno/{matricula}")
    public ResponseEntity<List<SolicitacaoResponseDTO>> buscarSolicitacoesPorAluno(
            @PathVariable Long matricula) {
        List<SolicitacaoResponseDTO> lista = alunoService.buscarSolicitacoesPorAluno(matricula);
        return ResponseEntity.ok(lista);
    }

    @PostMapping("/finalizar-submissao/{id}")
    public ResponseEntity<?> finalizarSubmissao(
            @PathVariable Long id,
            @RequestParam(required = false) String comentario) {
        try {
            SolicitacaoResponseDTO response = alunoService.finalizarSubmissao(id, comentario);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/pasta-link/{id}")
    public ResponseEntity<String> obterLinkPasta(@PathVariable Long id) {
        String link = alunoService.obterLinkPastaAluno(id);
        return ResponseEntity.ok(link);
    }

    @DeleteMapping("/documento/{id}")
    public ResponseEntity<?> excluirDocumento(@PathVariable Long id) {
        try {
            alunoService.excluirDocumento(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));
        }
    }
}
