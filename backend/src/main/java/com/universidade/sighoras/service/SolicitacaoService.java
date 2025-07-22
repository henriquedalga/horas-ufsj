package com.universidade.sighoras.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.beans.factory.annotation.Autowired;

import com.universidade.sighoras.entity.HoraTipo;
import com.universidade.sighoras.entity.Solicitacao;
import com.universidade.sighoras.repository.SolicitacaoRepository;
import IntegrandoDrive.service.FileService;

import com.universidade.sighoras.entity.HoraTipo;

import org.springframework.stereotype.Service;

@Service
public class SolicitacaoService {

    @Autowired
    private SolicitacaoRepository solicitacaoRepository;
    
    @Autowired
    private ArquivoService arquivoService;

    @Autowired
    private FileService fileService;


    // Métodos de CRUD
    
    public Solicitacao criarSolicitacao(Long matricula, String nome, HoraTipo horaTipo) throws IOException {
        // Verifica se já existe uma solicitação com a mesma matrícula e tipo de hora
        Solicitacao solicitacaoExistente = solicitacaoRepository
                .findByMatriculaAndHoraTipo(matricula, horaTipo);
        
        // Se já existir, retorna a solicitação existente sem alterações
        if (solicitacaoExistente != null) {
            System.out.println("Solicitação já existe para matrícula " + matricula + 
                    " e tipo de hora " + horaTipo);
            return solicitacaoExistente;
        }
        
        // Se não existir, cria uma nova solicitação
        System.out.println("Criando nova solicitação para matrícula " + matricula + 
                " e tipo de hora " + horaTipo);
        
        Solicitacao solicitacao = new Solicitacao();
        solicitacao.setMatricula(matricula);
        solicitacao.setNome(nome);
        solicitacao.setHoraTipo(horaTipo);
        solicitacao.setStatus("Aberta"); // Status inicial
        solicitacao.setDataSolicitacao(new java.util.Date().toString()); // Data atual como string
         int hourTypeInt = (horaTipo == HoraTipo.EXTENSAO) ? 1 : 0;
        String folderId = fileService.createFolder(matricula.toString(), hourTypeInt);
        solicitacao.setLinkPasta(fileService.getFolderLink(folderId, hourTypeInt));
        
        // Salva e retorna a nova solicitação
        return solicitacaoRepository.save(solicitacao);
    }
    
    public void atualizarStatus(Long matricula, String status) {
        // Lógica para atualizar uma solicitação existente
        Solicitacao solicitacao = solicitacaoRepository.findByMatricula(matricula);
        solicitacao.setStatus(status);
        solicitacaoRepository.save(solicitacao);
    }

    public List<Solicitacao> listarSolicitacoes() {
        // Lógica para listar todas as solicitações
        return solicitacaoRepository.findAll();
    }
    public Solicitacao obterSolicitacaoPorId(Long id) {
        // Lógica para obter uma solicitação por ID
        return solicitacaoRepository.findById(id).orElse(null);
    }
    public List<Solicitacao> listarSolicitacoesPorStatus(String status) {
        // Lógica para listar solicitações por status
        return solicitacaoRepository.findByStatus(status);
    }

    public List<Solicitacao> listarSolicitacoesPorNome(String nome) {
        // Lógica para listar solicitações por matrícula
        return solicitacaoRepository.findByNome(nome);
    }

    public List<Solicitacao> listarSolicitacoesPendentes() {
        // Lógica para listar solicitações pendentes
        return solicitacaoRepository.findByStatus("Pendente");
    }

    public List<Solicitacao> listarSolicitacoesAprovadas() {
        // Lógica para listar solicitações aprovadas
        return solicitacaoRepository.findByStatus("Aprovada");
    }
    public List<Solicitacao> listarSolicitacoesRejeitadas() {
        // Lógica para listar solicitações rejeitadas
        return solicitacaoRepository.findByStatus("Rejeitada");
    }

    @Transactional
    public void aprovarSolicitacao(Long solicitacaoId) throws IOException {
        Solicitacao sol = solicitacaoRepository.findById(solicitacaoId)
            .orElseThrow(() -> new RuntimeException("Solicitação não encontrada: " + solicitacaoId));
        sol.setStatus("Aprovada");
        solicitacaoRepository.save(sol);

        // Marca a pasta no Drive como somente leitura
        int hourType = sol.getHoraTipo() == HoraTipo.EXTENSAO ? 1 : 0;
        String folderId = extractFolderIdFromUrl(sol.getLinkPasta());
        fileService.finalizeSubmission(folderId, hourType);
    }

    @Transactional
    public void reprovarSolicitacao(Long solicitacaoId,
                                    Map<String /*driveLink*/, String /*comentário*/> comentarios)
                                    throws IOException {
        Solicitacao sol = solicitacaoRepository.findById(solicitacaoId)
            .orElseThrow(() -> new RuntimeException("Solicitação não encontrada: " + solicitacaoId));
        sol.setStatus("Rejeitada");
        solicitacaoRepository.save(sol);

        // Atualiza comentário para cada arquivo
        comentarios.forEach((driveLink, comentario) -> {
            arquivoService.atualizarComentario(driveLink, comentario);
        });

        // Torna a pasta editável novamente no Drive
        int hourType = sol.getHoraTipo() == HoraTipo.EXTENSAO ? 1 : 0;
        String folderId = extractFolderIdFromUrl(sol.getLinkPasta());
        fileService.rejectSubmission(folderId, hourType);
    }
    
    // Método de conveniência específico para EXTENSAO
    public List<Solicitacao> listarSolicitacoesExtensao() {
        return solicitacaoRepository.findByHoraTipo(HoraTipo.EXTENSAO);
    }

     // Método de conveniência específico para EXTENSAO
    public List<Solicitacao> listarSolicitacoesComplementar() {
        return solicitacaoRepository.findByHoraTipo(HoraTipo.COMPLEMENTAR);
    }
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

}
