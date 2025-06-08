package IntegrandoDrive.service;

import IntegrandoDrive.dto.SolicitacaoResponseDTO;
import IntegrandoDrive.dto.DocumentoDTO;
import IntegrandoDrive.model.Student;
import IntegrandoDrive.model.Documento;
import IntegrandoDrive.model.Solicitacao;
import IntegrandoDrive.model.StatusSolicitacao;
import IntegrandoDrive.persistence.DocumentoRepository;
import IntegrandoDrive.persistence.SolicitacaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class AlunoService {
    private final FileService fileService;
    private final DocumentoRepository documentoRepository;
    private final SolicitacaoRepository solicitacaoRepository;

    private static final String PASTA_RAIZ_DRIVE = "1ABC123XYZ_FOLDER_ID_AQUI";

    public AlunoService(FileService fileService,
                        DocumentoRepository documentoRepository,
                        SolicitacaoRepository solicitacaoRepository) {
        this.fileService = fileService;
        this.documentoRepository = documentoRepository;
        this.solicitacaoRepository = solicitacaoRepository;
    }

    public SolicitacaoResponseDTO processarDocumentos(MultipartFile[] files,
                                                     String alunoMatricula,
                                                     String alunoNome) {
        try {
            // Inicia solicitação com status EM_PROCESSAMENTO e sem documentos
            Solicitacao solicitacao = new Solicitacao();
            solicitacao.setAlunoMatricula(alunoMatricula);
            solicitacao.setAlunoNome(alunoNome);
            solicitacao.setStatus(StatusSolicitacao.EM_PROCESSAMENTO);
            solicitacao.setDataCriacao(LocalDateTime.now());
            solicitacao.setDocumentos(new ArrayList<>());

            // Cria pasta no Drive
            Student student = new Student(alunoNome, alunoMatricula);
            String pastaAlunoId = fileService.createStudentFolder(student, PASTA_RAIZ_DRIVE);
            solicitacao.setPastaGoogleDriveId(pastaAlunoId);

            // Persiste solicitação inicial
            solicitacao = solicitacaoRepository.save(solicitacao);

            // Bloqueia caso já FINALIZADA
            if (solicitacao.getStatus() == StatusSolicitacao.FINALIZADA) {
                throw new IllegalStateException("Não é possível adicionar documentos a uma submissão finalizada.");
            }

            // Upload de cada arquivo e persistência de metadados
            for (MultipartFile mf : files) {
                if (mf.isEmpty()) continue;
                File temp = convertMultipartToFile(mf);
                try {
                    String fileId = fileService.uploadFile(student, temp, pastaAlunoId);
                    String fileLink = fileService.getFileLink(fileId);

                    Documento doc = new Documento();
                    doc.setNomeOriginal(mf.getOriginalFilename());
                    doc.setDriveFileId(fileId);
                    doc.setDriveUrl(fileLink);
                    doc.setTamanho(mf.getSize());
                    doc.setMimeType(mf.getContentType());
                    doc.setDataUpload(LocalDateTime.now());
                    doc.setSolicitacao(solicitacao);

                    Documento salvo = documentoRepository.save(doc);
                    solicitacao.getDocumentos().add(salvo);
                } finally {
                    if (temp.exists()) temp.delete();
                }
            }

            // Atualiza status para DOCUMENTOS_ENVIADOS
            solicitacao.setStatus(StatusSolicitacao.DOCUMENTOS_ENVIADOS);
            solicitacao.setDataAtualizacao(LocalDateTime.now());
            solicitacao = solicitacaoRepository.save(solicitacao);

            return converterParaResponseDTO(solicitacao);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao processar documentos: " + e.getMessage(), e);
        }
    }

    public SolicitacaoResponseDTO buscarSolicitacao(Long id) {
        Solicitacao sol = solicitacaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Solicitação não encontrada ID: " + id));
        return converterParaResponseDTO(sol);
    }

    public List<SolicitacaoResponseDTO> buscarSolicitacoesPorAluno(String matricula) {
        List<Solicitacao> lista = solicitacaoRepository.findByAlunoMatriculaOrderByDataCriacaoDesc(matricula);
        List<SolicitacaoResponseDTO> dtos = new ArrayList<>();
        for (Solicitacao s : lista) {
            dtos.add(converterParaResponseDTO(s));
        }
        return dtos;
    }

    public SolicitacaoResponseDTO finalizarSubmissao(Long id, String comentario) {
        try {
            Solicitacao sol = solicitacaoRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Solicitação não encontrada"));
            // Marca arquivos no Drive
            Student student = new Student(sol.getAlunoNome(), sol.getAlunoMatricula());
            String pastaId = sol.getPastaGoogleDriveId() == null ? "" : sol.getPastaGoogleDriveId();
            fileService.finalizeSubmission(student, pastaId);

            // Persiste FINALIZADA e comentário
            sol.setStatus(StatusSolicitacao.FINALIZADA);
            sol.setComentarios(comentario);
            sol.setDataAtualizacao(LocalDateTime.now());
            sol = solicitacaoRepository.save(sol);

            return converterParaResponseDTO(sol);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao finalizar submissão: " + e.getMessage(), e);
        }
    }

    public String obterLinkPastaAluno(Long id) {
        try {
            Solicitacao sol = solicitacaoRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Solicitação não encontrada"));
            return fileService.getFolderLink(sol.getPastaGoogleDriveId());
        } catch (IOException e) {
            throw new RuntimeException("Erro ao obter link da pasta: " + e.getMessage(), e);
        }
    }

    public void excluirDocumento(Long docId) {
        try {
            Documento doc = documentoRepository.findById(docId)
                    .orElseThrow(() -> new RuntimeException("Documento não encontrado"));
            Solicitacao sol = doc.getSolicitacao();
            if (sol.getStatus() == StatusSolicitacao.FINALIZADA) {
                throw new IllegalStateException("Não é possível excluir documento de submissão finalizada");
            }
            Student student = new Student(sol.getAlunoNome(), sol.getAlunoMatricula());
            fileService.deleteFile(student, doc.getDriveFileId());
            documentoRepository.delete(doc);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao excluir documento: " + e.getMessage(), e);
        }
    }

    private File convertMultipartToFile(MultipartFile mf) throws IOException {
        File f = new File(System.getProperty("java.io.tmpdir") + "/" + mf.getOriginalFilename());
        mf.transferTo(f);
        return f;
    }

    private SolicitacaoResponseDTO converterParaResponseDTO(Solicitacao sol) {
        SolicitacaoResponseDTO dto = new SolicitacaoResponseDTO();
        dto.setId(sol.getId());
        dto.setAlunoMatricula(sol.getAlunoMatricula());
        dto.setAlunoNome(sol.getAlunoNome());
        dto.setStatus(sol.getStatus().toString());
        dto.setComentarios(sol.getComentarios());
        dto.setDataCriacao(sol.getDataCriacao());
        dto.setDataAtualizacao(sol.getDataAtualizacao());

        List<DocumentoDTO> docs = new ArrayList<>();
        for (Documento d : sol.getDocumentos()) {
            DocumentoDTO dd = new DocumentoDTO();
            dd.setId(d.getId());
            dd.setNomeOriginal(d.getNomeOriginal());
            dd.setDriveUrl(d.getDriveUrl());
            dd.setTamanho(d.getTamanho());
            dd.setDataUpload(d.getDataUpload());
            docs.add(dd);
        }
        dto.setDocumentos(docs);
        return dto;
    }
}
