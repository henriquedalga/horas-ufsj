package com.universidade.sighoras.service;

import com.universidade.sighoras.entity.HoraTipo;
import com.universidade.sighoras.entity.Solicitacao;
import com.universidade.sighoras.entity.Arquivo;
import com.universidade.sighoras.service.ArquivoService;
//import com.universidade.sighoras.service.EmailService;
import IntegrandoDrive.service.FileService;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AlunoService {

    private final SolicitacaoService solicitacaoService;
    private final FileService fileService;
    //private final EmailService emailService;
    private final ArquivoService arquivoService;

    public AlunoService(SolicitacaoService solicitacaoService, FileService fileService, ArquivoService arquivoService) {
        this.solicitacaoService = solicitacaoService;
        this.fileService = fileService;
        //this.emailService = emailService;
        this.arquivoService = arquivoService;
    }

    /**
     * Cria uma nova solicitação de aluno.
     */
    public ResponseEntity<Void> criarSolicitacao(Long matricula,
                                                 String nome,
                                                 String horaTipo) {
        // Sempre pode criar enquanto não houver pendente
        HoraTipo tipo = HoraTipo.valueOf(horaTipo.toUpperCase());
        try {
            solicitacaoService.criarSolicitacao(matricula, nome, tipo);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return ResponseEntity.ok().build();
    }
    /**
     * Verifica se uma solicitação existe, se não, cria uma nova.
     * @return A solicitação existente ou recém-criada
     */
    public Solicitacao verificarOuCriarSolicitacao(Long matricula, String nome, HoraTipo horaTipo) {
        try {
            return solicitacaoService.criarSolicitacao(matricula, nome, horaTipo);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Atualiza o status de uma solicitação existente.
     */
    public ResponseEntity<Void> atualizarStatus(Long matricula, String status) {
        // Permite qualquer transição registrada no service
        solicitacaoService.atualizarStatus(matricula, status);
        return ResponseEntity.ok().build();
    }
    /**
     * Finaliza a submissão: marca pasta como read‑only, envia e‑mail e atualiza status.
     */
    public ResponseEntity<Void> finalizarSolicitacao(Long idSolicitacao) {
        Solicitacao sol = solicitacaoService.obterSolicitacaoPorId(idSolicitacao);
        verificarPermissaoModificacao(sol);

        int hourType = sol.getHoraTipo() == HoraTipo.EXTENSAO ? 1 : 0;
        try {
            String folderId = extractFolderIdFromUrl(sol.getLinkPasta());
            fileService.finalizeSubmission(folderId, hourType);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao finalizar submissão no Drive", e);
        }

        //emailService.sendSubmissionEmail(sol);

        solicitacaoService.atualizarStatus(sol.getMatricula(), "Pendente");

        return ResponseEntity.ok().build();
    }    

    /**
     * Adiciona um arquivo a uma solicitação existente, se permitido.
     */
    public void adicionarArquivo(Long idSolicitacao, MultipartFile arquivo) {
        Solicitacao sol = solicitacaoService.obterSolicitacaoPorId(idSolicitacao);
        verificarPermissaoModificacao(sol);
        try {
            // Converte MultipartFile para File temporário
            java.io.File tempFile = java.io.File.createTempFile("upload-", arquivo.getOriginalFilename());
            arquivo.transferTo(tempFile);
            int hourType = sol.getHoraTipo() == HoraTipo.EXTENSAO ? 1 : 0;
            String fileID = extractFolderIdFromUrl(sol.getLinkPasta());
            String drivelink = fileService.uploadFile(tempFile, fileID, hourType);
            tempFile.delete(); // limpa depois

            //salvando metadados
            arquivoService.salvarArquivo(sol.getId(), arquivo.getOriginalFilename(), drivelink, null);

        } catch (IOException e) {
            throw new RuntimeException("Erro ao fazer upload para o Drive", e);
        }
    }

    /**
     * Remove um arquivo de uma solicitação existente, se permitido.
     */
    public void removerArquivo(Long idSolicitacao, String linkArquivo) {
        Solicitacao sol = solicitacaoService.obterSolicitacaoPorId(idSolicitacao);
        verificarPermissaoModificacao(sol);

        try {
            String fileId = extrairFileIdDoLink(linkArquivo);
            int hourType = sol.getHoraTipo() == HoraTipo.EXTENSAO ? 1 : 0;
            fileService.deleteFile(fileId, hourType);

            arquivoService.excluirPorDriveLink(fileId);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao remover arquivo do Drive", e);
        }
    }

    /**
     * Lista todas as solicitações.
     */
    public ResponseEntity<List<Solicitacao>> listarTodas() {
        List<Solicitacao> lista = solicitacaoService.listarSolicitacoes();
        return ResponseEntity.ok(lista);
    }

    /**
     * Busca uma solicitação pelo seu ID.
     */
    public ResponseEntity<Solicitacao> buscarPorId(Long id) {
        Solicitacao sol = solicitacaoService.obterSolicitacaoPorId(id);
        return sol != null
                ? ResponseEntity.ok(sol)
                : ResponseEntity.notFound().build();
    }

    /**
     * Lista solicitações por nome do aluno.
     */
    public ResponseEntity<List<Solicitacao>> listarPorNome(String nome) {
        List<Solicitacao> lista = solicitacaoService.listarSolicitacoesPorNome(nome);
        return ResponseEntity.ok(lista);
    }

    /**
     * Lista todos os arquivos de uma solicitação.
     */
    public ResponseEntity<List<String>> listarArquivos(Long idSolicitacao) {
        Solicitacao sol = solicitacaoService.obterSolicitacaoPorId(idSolicitacao);
        int hourType = sol.getHoraTipo() == HoraTipo.EXTENSAO ? 1 : 0;
        try {
            String folderId = extractFolderIdFromUrl(sol.getLinkPasta());
            List<String> links = fileService.listFileLinks(folderId, hourType);
            return ResponseEntity.ok(links);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao listar arquivos do Drive", e);
        }
    }

    /**
     * Recebe o link do arquivo e extrai o id dele.
     */
    private String extrairFileIdDoLink(String link) {
        // Suporta links no formato: https://drive.google.com/file/d/FILE_ID/view
        // ou: https://drive.google.com/open?id=FILE_ID
        if (link.contains("/d/")) {
            return link.split("/d/")[1].split("/")[0];
        } else if (link.contains("id=")) {
            return link.split("id=")[1].split("&")[0];
        } else {
            throw new IllegalArgumentException("Link do arquivo inválido: " + link);
        }
    }

    /**
     * Extrai o ID da pasta de uma URL do Google Drive
     */
    private String extractFolderIdFromUrl(String url) {
        if (url == null) {
            throw new IllegalArgumentException("URL da pasta é nula");
        }
        
        // Verifica se é uma URL completa ou apenas o ID
        if (url.contains("/")) {
            // Formato: https://drive.google.com/drive/folders/1_xwa2akI1qjznKflfSqj3tdMcq-qvERp
            String[] parts = url.split("/");
            return parts[parts.length - 1].replace(".", ""); // Remove o ponto no final, se existir
        } else {
            // Já é apenas o ID
            return url;
        }
    }

    /**
     * Valida se o status da solicitação permite adicionar/excluir arquivos.
     */
    private void verificarPermissaoModificacao(Solicitacao sol) {
        String st = sol.getStatus();
        // Permite apenas quando Aberta ou Rejeitada
        if (!"Aberta".equalsIgnoreCase(st) && !"Rejeitada".equalsIgnoreCase(st)) {
            throw new IllegalStateException(
                "Não é permitido modificar arquivos na solicitação com status: " + st
            );
        }
    }
}
