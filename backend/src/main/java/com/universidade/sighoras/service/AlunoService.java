package com.universidade.sighoras.service;

import com.universidade.sighoras.entity.Arquivo;
import com.universidade.sighoras.entity.HoraTipo;
import com.universidade.sighoras.entity.Solicitacao;
import com.universidade.sighoras.service.EmailService;
import IntegrandoDrive.service.FileService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class AlunoService {

    private final SolicitacaoService solicitacaoService;
    private final FileService fileService;
    private final com.universidade.sighoras.repository.SolicitacaoRepository solicitacaoRepository;
     private final EmailService emailService;

    public AlunoService(SolicitacaoService solicitacaoService, FileService fileService, com.universidade.sighoras.repository.SolicitacaoRepository solicitacaoRepository, EmailService emailService) {
        this.solicitacaoService = solicitacaoService;
        this.fileService = fileService;
        this.solicitacaoRepository = solicitacaoRepository;
        this.emailService = emailService;
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
            fileService.finalizeSubmission(sol.getLinkPasta(), hourType);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao finalizar submissão no Drive", e);
        }

        emailService.sendSubmissionEmail(sol);

        solicitacaoService.atualizarStatus(sol.getMatricula(), "Pendente");

        return ResponseEntity.ok().build();
    }    

    /**
     * Adiciona um arquivo a uma solicitação existente, se permitido.
     */
    public void adicionarArquivo(Long idSolicitacao, MultipartFile arquivo) {
        Solicitacao sol = solicitacaoService.obterSolicitacaoPorId(idSolicitacao);
        verificarPermissaoModificacao(sol);
        String driveUrl = null;
        String fileId = null;  
        try {
            // Converte MultipartFile para File temporário
            java.io.File tempFile = java.io.File.createTempFile("upload-", arquivo.getOriginalFilename());
            arquivo.transferTo(tempFile);
            
            int hourType = sol.getHoraTipo() == HoraTipo.EXTENSAO ? 1 : 0;
            // Extrai o ID da pasta do link completo
            String folderId = extractFolderIdFromUrl(sol.getLinkPasta());
            // Faz upload e captura o ID do arquivo retornado
            fileId = fileService.uploadFile(tempFile, folderId, hourType);
            // Obtém o link do arquivo usando o ID
            driveUrl = fileService.getFileLink(fileId, hourType);
            
            tempFile.delete(); // limpa depois
        } catch (IOException e) {
            throw new RuntimeException("Erro ao fazer upload para o Drive", e);
        }
        Arquivo arquivoObj = new Arquivo(arquivo.getOriginalFilename(), idSolicitacao, arquivo.getSize(), driveUrl);
        sol.adicionarArquivo(arquivoObj);
        solicitacaoRepository.save(sol); // Salva a solicitação atualizada
    }

    /**
     * Remove um arquivo de uma solicitação existente, se permitido.
     */
    public void removerArquivo(Long idSolicitacao, String linkArquivo) {
        Solicitacao sol = solicitacaoService.obterSolicitacaoPorId(idSolicitacao);
        verificarPermissaoModificacao(sol);

        try {
            // Extrair o ID do arquivo a partir do link
            String fileId = extrairFileIdDoLink(linkArquivo);
            
            // Encontrar e remover o arquivo da lista de documentos da solicitação
            Arquivo arquivoParaRemover = null;
            for (Arquivo doc : sol.getDocumentos()) {
                if (doc.getUrl() != null && doc.getUrl().equals(linkArquivo)) {
                    arquivoParaRemover = doc;
                    break;
                }
            }
            
            // Se encontrou o arquivo, remove-o da lista
            if (arquivoParaRemover != null) {
                sol.getDocumentos().remove(arquivoParaRemover);
                System.out.println("Arquivo removido da solicitação: " + arquivoParaRemover.getNomeArquivo());
            } else {
                System.out.println("Aviso: Arquivo com link " + linkArquivo + " não encontrado na solicitação " + idSolicitacao);
            }
            
            // Salva a solicitação atualizada
            solicitacaoRepository.save(sol);
            
            int hourType = sol.getHoraTipo() == HoraTipo.EXTENSAO ? 1 : 0;
            fileService.deleteFile(fileId, hourType);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao remover arquivo do Drive: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao processar remoção do arquivo: " + e.getMessage(), e);
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
    public ResponseEntity<List<Arquivo>> listarArquivos(Long idSolicitacao) {
            System.out.println("==================================");
            System.out.println("Listando arquivos da solicitação: " + idSolicitacao);
            Solicitacao sol = solicitacaoService.obterSolicitacaoPorId(idSolicitacao);
            System.out.println("==================================");
            System.out.println("Passou!!!!: " + idSolicitacao);

            if (sol == null) {
                return ResponseEntity.notFound().build();
            }
            
            try {
                List<Arquivo> links = sol.getDocumentos();

                System.out.println("==================================");
                System.out.println("Listando arquivos da solicitação: " + idSolicitacao);
                System.out.println("Total de arquivos: " + links.size());
                for (Arquivo arquivo : links) {
                    System.out.println("Arquivo: " + arquivo.getNomeArquivo() + " - Link: " + arquivo.getUrl());
                }
                System.out.println("==================================");
                return ResponseEntity.ok(links);
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.status(500).build();
            }
        // try {
        //     List<String> links = fileService.listFileLinks(sol.getLinkPasta(), hourType);
        //     return ResponseEntity.ok(links);
        // } catch (IOException e) {
        //     throw new RuntimeException("Erro ao listar arquivos do Drive", e);
        // }
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
