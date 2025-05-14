package com.universidade.sighoras.service;

import java.util.List;

import com.universidade.sighoras.entity.Solicitacao;
import com.universidade.sighoras.repository.SolicitacaoRepository;

public class SolicitacaoService {

    private final SolicitacaoRepository solicitacaoRepository;

    public SolicitacaoService(SolicitacaoRepository solicitacaoRepository) {
        this.solicitacaoRepository = solicitacaoRepository;
    }
    // Métodos de CRUD
    // Outros métodos relacionados a solicitações podem ser adicionados aqui
    // Exemplo de método para criar uma nova solicitação
    public void criarSolicitacao(Solicitacao solicitacao) {
        // Lógica para criar uma nova solicitação
        solicitacaoRepository.save(solicitacao);
    }
    
    public void atualizarSolicitacao(Solicitacao solicitacao) {
        // Lógica para atualizar uma solicitação existente
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

    
}
