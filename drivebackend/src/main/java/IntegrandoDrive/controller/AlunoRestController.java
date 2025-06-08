package IntegrandoDrive.controller;

import IntegrandoDrive.service.AlunoService;
import IntegrandoDrive.dto.SolicitacaoResponseDTO;

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
    
    // RF2.1: Receber documentos do front
    @PostMapping("/upload-documentos")
    public ResponseEntity<SolicitacaoResponseDTO> uploadDocumentos(
            @RequestParam("files") MultipartFile[] files,
            @RequestParam("alunoMatricula") String alunoMatricula,
            @RequestParam("alunoNome") String alunoNome) {
        
        SolicitacaoResponseDTO response = alunoService.processarDocumentos(files, alunoMatricula, alunoNome);
        return ResponseEntity.ok(response);
    }
    
    // RF2.3: Enviar dados da solicitação para o front
    @GetMapping("/solicitacao/{id}")
    public ResponseEntity<SolicitacaoResponseDTO> buscarSolicitacao(@PathVariable Long id) {
        SolicitacaoResponseDTO response = alunoService.buscarSolicitacao(id);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/solicitacoes/aluno/{matricula}")
    public ResponseEntity<List<SolicitacaoResponseDTO>> buscarSolicitacoesPorAluno(@PathVariable String matricula) {
        List<SolicitacaoResponseDTO> solicitacoes = alunoService.buscarSolicitacoesPorAluno(matricula);
        return ResponseEntity.ok(solicitacoes);
    }
    
    @PostMapping("/finalizar-submissao/{id}")
    public ResponseEntity<SolicitacaoResponseDTO> finalizarSubmissao(
            @PathVariable Long id,
            @RequestParam(required = false) String comentario) {
        SolicitacaoResponseDTO response = alunoService.finalizarSubmissao(id, comentario);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/pasta-link/{id}")
    public ResponseEntity<String> obterLinkPasta(@PathVariable Long id) {
        String link = alunoService.obterLinkPastaAluno(id);
        return ResponseEntity.ok(link);
    }
    
    @DeleteMapping("/documento/{id}")
    public ResponseEntity<Void> excluirDocumento(@PathVariable Long id) {
        alunoService.excluirDocumento(id);
        return ResponseEntity.noContent().build();
    }
}