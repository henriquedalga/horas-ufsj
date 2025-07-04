package com.universidade.sighoras.service;

import com.universidade.sighoras.controller.DocumentoDTO;
import com.universidade.sighoras.controller.SolicitacaoResponseDTO;
import com.universidade.sighoras.entity.Arquivo;
import com.universidade.sighoras.entity.Solicitacao;
import com.universidade.sighoras.entity.HoraTipo;
import com.universidade.sighoras.repository.ArquivoRepository;
import com.universidade.sighoras.repository.SolicitacaoRepository;
import IntegrandoDrive.service.FileService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Transactional
public class AlunoService {
    private final FileService fileService;
    private final SolicitacaoRepository solicitacaoRepo;
    private final ArquivoRepository arquivoRepo;

    private static final String PASTA_RAIZ_DRIVE = "1TIFxvdsCFWpB9xeXK6mx59Csp5MuDJlN";

    public AlunoService(FileService fileService,
                        SolicitacaoRepository solicitacaoRepo,
                        ArquivoRepository arquivoRepo) {
        this.fileService = fileService;
        this.solicitacaoRepo = solicitacaoRepo;
        this.arquivoRepo = arquivoRepo;
    }

    public SolicitacaoResponseDTO processarDocumentos(MultipartFile[] files,
                                                     Long matricula,
                                                     String nome,
                                                     HoraTipo horaTipo) {
        List<Solicitacao> anteriores = solicitacaoRepo.findByMatriculaOrderByDataSolicitacaoDesc(matricula);
        if (anteriores != null && !anteriores.isEmpty()
                && "FINALIZADA".equals(anteriores.get(0).getStatus())) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Não é possível adicionar arquivos a submissão finalizada"
            );
        }
        try {
            Solicitacao sol = new Solicitacao();
            sol.setMatricula(matricula);
            sol.setNome(nome);
            sol.setHoraTipo(horaTipo);
            sol.setStatus("EM_PROCESSAMENTO");
            sol.setDataSolicitacao(LocalDateTime.now().toString());
            sol.setResposta("");
            sol.setLinkPasta("");
            sol = solicitacaoRepo.save(sol);

            String pastaId = fileService.createFolder(nome, PASTA_RAIZ_DRIVE);
            String pastaLink = fileService.getFolderLink(pastaId);
            sol.setLinkPasta(pastaLink);
            sol = solicitacaoRepo.save(sol);

            for (MultipartFile mf : files) {
                if (mf.isEmpty()) continue;
                File temp = convertMultipartToFile(mf);
                try {
                    String fileId = fileService.uploadFile(temp, pastaId, sol.getStatus());
                    String fileLink = fileService.getFileLink(fileId);

                    Arquivo arq = new Arquivo();
                    arq.setIdSolicitacao(sol.getId());
                    arq.setNomeArquivo(mf.getOriginalFilename());
                    arq.setDrivelink(fileLink);
                    arq.setComentario("");
                    arq.setData(LocalDateTime.now().toString());
                    arquivoRepo.save(arq);
                } finally {
                    temp.delete();
                }
            }

            sol.setStatus("DOCUMENTOS_ENVIADOS");
            sol.setResposta(LocalDateTime.now().toString());
            sol = solicitacaoRepo.save(sol);
            return converterParaResponseDTO(sol);
        } catch (IOException e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Erro no Drive",
                    e
            );
        }
    }

    public SolicitacaoResponseDTO buscarSolicitacao(Long id) {
        Solicitacao sol = solicitacaoRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Solicitação não encontrada ID: " + id
                ));
        return converterParaResponseDTO(sol);
    }

    public List<SolicitacaoResponseDTO> buscarSolicitacoesPorAluno(Long matricula) {
        List<Solicitacao> lista = solicitacaoRepo.findByMatriculaOrderByDataSolicitacaoDesc(matricula);
        if (lista == null) lista = Collections.emptyList();
        List<SolicitacaoResponseDTO> dtos = new ArrayList<>();
        for (Solicitacao s : lista) dtos.add(converterParaResponseDTO(s));
        return dtos;
    }

    public SolicitacaoResponseDTO finalizarSubmissao(Long id, String comentario) {
        try {
            Solicitacao sol = solicitacaoRepo.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Solicitação não encontrada ID: " + id
                    ));
            String pastaLink = sol.getLinkPasta(); 
            String pastaId   = pastaLink.substring(pastaLink.lastIndexOf('/') + 1);
            fileService.finalizeSubmission(pastaId);
            sol.setStatus("FINALIZADA");
            sol.setResposta(comentario);
            sol = solicitacaoRepo.save(sol);
            return converterParaResponseDTO(sol);
        } catch (IOException e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Erro no Drive",
                    e
            );
        }
    }

    public String obterLinkPastaAluno(Long id) {
        Solicitacao sol = solicitacaoRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Solicitação não encontrada ID: " + id
                ));
        return sol.getLinkPasta();
    }

    public void excluirDocumento(Long docId) {
        Arquivo doc = arquivoRepo.findById(docId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Documento não encontrado ID: " + docId
                ));
        Solicitacao sol = solicitacaoRepo.findById(doc.getIdSolicitacao())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Solicitação não encontrada ID: " + doc.getIdSolicitacao()
                ));
        if ("FINALIZADA".equals(sol.getStatus())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Não permitido"
            );
        }
        try {
            fileService.deleteFile(doc.getDrivelink(), sol.getStatus());
            arquivoRepo.delete(doc);
        } catch (IOException e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Erro ao excluir documento",
                    e
            );
        }
    }

    private File convertMultipartToFile(MultipartFile mf) throws IOException {
        File file = new File(System.getProperty("java.io.tmpdir"), mf.getOriginalFilename());
        mf.transferTo(file);
        return file;
    }

    private SolicitacaoResponseDTO converterParaResponseDTO(Solicitacao sol) {
        SolicitacaoResponseDTO dto = new SolicitacaoResponseDTO();
        dto.setId(sol.getId());
        dto.setMatricula(sol.getMatricula());
        dto.setNome(sol.getNome());
        dto.setHoraTipo(sol.getHoraTipo());
        dto.setStatus(sol.getStatus());
        dto.setDataSolicitacao(sol.getDataSolicitacao());
        dto.setResposta(sol.getResposta());
        dto.setLinkPasta(sol.getLinkPasta());

        List<DocumentoDTO> docs = new ArrayList<>();
        List<Arquivo> arquivos = arquivoRepo.findByIdSolicitacao(sol.getId());
        if (arquivos == null) arquivos = Collections.emptyList();
        for (Arquivo a : arquivos) {
            DocumentoDTO d = new DocumentoDTO();
            d.setId(a.getId());
            d.setNomeArquivo(a.getNomeArquivo());
            d.setDriveUrl(a.getDrivelink());
            d.setComentario(a.getComentario());
            d.setData(a.getData());
            docs.add(d);
        }
        dto.setArquivos(docs);
        return dto;
    }
}
