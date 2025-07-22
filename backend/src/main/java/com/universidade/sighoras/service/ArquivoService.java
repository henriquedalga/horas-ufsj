package com.universidade.sighoras.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import com.universidade.sighoras.entity.Arquivo;
import com.universidade.sighoras.repository.ArquivoRepository;

@Service
public class ArquivoService {

    private final ArquivoRepository arquivoRepository;
    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Autowired
    public ArquivoService(ArquivoRepository arquivoRepository) {
        this.arquivoRepository = arquivoRepository;
    }

    /**
     * Salva um registro de Arquivo no banco.
     */
    public Arquivo salvarArquivo(Long idSolicitacao,
                                 String nomeArquivo,
                                 String drivelink,
                                 String comentario) {
        Arquivo arq = new Arquivo();
        arq.setIdSolicitacao(idSolicitacao);
        arq.setNomeArquivo(nomeArquivo);
        arq.setUrl(drivelink);
        arq.setComent(comentario);
        arq.setData(LocalDateTime.now().format(ISO_FORMATTER));
        return arquivoRepository.save(arq);
    }

    /**
     * Lista todos os arquivos vinculados a uma solicitação.
     */
    public List<Arquivo> listarPorSolicitacao(Long idSolicitacao) {
        return arquivoRepository.findByIdSolicitacao(idSolicitacao);
    }

    /**
     * Exclui o metadado de um arquivo pelo seu DriveLink.
     */
    public void excluirPorDriveLink(String drivelink) {
        Optional<Arquivo> arq = arquivoRepository.findByUrl(drivelink);
        if (arq.isPresent()) {
            arquivoRepository.delete(arq.get());
        } else {
            throw new RuntimeException("Arquivo não encontrado no banco com o link fornecido.");
        }
    }

        /**
     * Atualiza o comentário de um Arquivo existente identificado pelo driveLink.
     */
    @Transactional
    public Arquivo atualizarComentario(String drivelink, String comentario) {
        Optional<Arquivo> opt = arquivoRepository.findByUrl(drivelink);
        if (opt.isEmpty()) {
            throw new RuntimeException("Arquivo não encontrado: " + drivelink);
        }
        Arquivo arq = opt.get();
        arq.setComent(comentario);
        arq.setData(LocalDateTime.now().format(ISO_FORMATTER));
        return arquivoRepository.save(arq);
    }
}