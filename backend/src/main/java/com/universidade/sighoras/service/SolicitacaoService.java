package com.universidade.sighoras.service;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.universidade.sighoras.entity.HoraTipo;
import com.universidade.sighoras.entity.Solicitacao;
import com.universidade.sighoras.repository.SolicitacaoRepository;

import IntegrandoDrive.service.FileService;

import org.springframework.stereotype.Service;

@Service
public class SolicitacaoService {

    @Autowired
    private SolicitacaoRepository solicitacaoRepository;

    private final FileService fileService;

    public SolicitacaoService(FileService fileService) {
        this.fileService = fileService;
    }

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

        String folderId = fileService.createFolder(matricula.toString(), "root");
        solicitacao.setLinkPasta(fileService.getFolderLink(folderId));
        
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

    // Método de conveniência específico para EXTENSAO
    public List<Solicitacao> listarSolicitacoesExtensao() {
        return solicitacaoRepository.findByHoraTipo(HoraTipo.EXTENSAO);
    }

     // Método de conveniência específico para EXTENSAO
    public List<Solicitacao> listarSolicitacoesComplementar() {
        return solicitacaoRepository.findByHoraTipo(HoraTipo.COMPLEMENTAR);
    }

}
