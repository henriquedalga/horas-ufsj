package com.universidade.sighoras.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

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
    
    public void criarSolicitacao(Long matricula, String nome, String email, String horaTipo, String linkPasta) {
        // Lógica para criar uma nova solicitação
        Solicitacao solicitacao = new Solicitacao();
        solicitacao.setMatricula(matricula);
        solicitacao.setNome(nome);
        solicitacao.setEmail(email);
        solicitacao.setHoraTipoStr(horaTipo);
        solicitacao.setLinkPasta(linkPasta);
        solicitacao.setStatus("Aberta"); // Definindo status inicial como Pendente
        solicitacaoRepository.save(solicitacao);
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
        fileService.finalizeSubmission(sol.getLinkPasta(), hourType);
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
        fileService.rejectSubmission(sol.getLinkPasta(), hourType);
    }

}
