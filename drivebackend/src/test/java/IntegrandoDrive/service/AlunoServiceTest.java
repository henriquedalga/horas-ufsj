package IntegrandoDrive.service;

import IntegrandoDrive.dto.SolicitacaoResponseDTO;
import IntegrandoDrive.model.Documento;
import IntegrandoDrive.model.Solicitacao;
import IntegrandoDrive.model.StatusSolicitacao;
import IntegrandoDrive.persistence.DocumentoRepository;
import IntegrandoDrive.persistence.SolicitacaoRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.mock.web.MockMultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlunoServiceTest {

    @Mock private FileService fileService;
    @Mock private DocumentoRepository documentoRepository;
    @Mock private SolicitacaoRepository solicitacaoRepository;
    @InjectMocks private AlunoService service;

    private final String MATRICULA = "2021001";
    private final String NOME = "Fulano";
    private MockMultipartFile mp;

    @BeforeEach
    void setUp() {
        mp = new MockMultipartFile(
            "files", "teste.txt",
            "text/plain", "conteudo".getBytes());
    }

    @Test
    void processarDocumentos_fluxoPrincipal() throws IOException {
        // mocks do FileService
        when(fileService.createStudentFolder(any(), anyString()))
            .thenReturn("folder123");
        when(fileService.uploadFile(any(), any(File.class), eq("folder123")))
            .thenReturn("file123");
        when(fileService.getFileLink("file123"))
            .thenReturn("https://drive/link/file123");

        // mocks de save
        when(solicitacaoRepository.save(any(Solicitacao.class)))
            .thenAnswer(inv -> inv.getArgument(0));
        when(documentoRepository.save(any(Documento.class)))
            .thenAnswer(inv -> inv.getArgument(0));

        SolicitacaoResponseDTO dto = service.processarDocumentos(
            new MultipartFile[]{mp}, MATRICULA, NOME);

        assertNotNull(dto);
        assertEquals(StatusSolicitacao.DOCUMENTOS_ENVIADOS.toString(), dto.getStatus());
        assertFalse(dto.getDocumentos().isEmpty());

        verify(fileService).createStudentFolder(any(), anyString());
        verify(fileService).uploadFile(any(), any(File.class), eq("folder123"));
        verify(documentoRepository).save(any(Documento.class));
        verify(solicitacaoRepository, times(2)).save(any(Solicitacao.class));
    }

    @Test
    void buscarSolicitacao_quandoExistir_retornaDTO() {
        Solicitacao sol = new Solicitacao();
        sol.setId(7L);
        sol.setAlunoMatricula(MATRICULA);
        sol.setAlunoNome(NOME);
        sol.setStatus(StatusSolicitacao.EM_PROCESSAMENTO);
        sol.setDataCriacao(LocalDateTime.now());

        when(solicitacaoRepository.findById(7L))
            .thenReturn(Optional.of(sol));

        var dto = service.buscarSolicitacao(7L);
        assertEquals(7L, dto.getId());
        assertEquals(MATRICULA, dto.getAlunoMatricula());
    }

    @Test
    void finalizarSubmissao_mudaStatusParaFinalizada() throws IOException {
        Solicitacao sol = new Solicitacao();
        sol.setId(5L);
        sol.setAlunoMatricula(MATRICULA);
        sol.setAlunoNome(NOME);
        sol.setStatus(StatusSolicitacao.DOCUMENTOS_ENVIADOS);

        when(solicitacaoRepository.findById(5L))
            .thenReturn(Optional.of(sol));
        when(solicitacaoRepository.save(any(Solicitacao.class)))
            .thenAnswer(inv -> inv.getArgument(0));
        doNothing().when(fileService)
                .finalizeSubmission(any(IntegrandoDrive.model.Student.class), any());

        var dto = service.finalizarSubmissao(5L, "Comentário final");
        assertEquals(StatusSolicitacao.FINALIZADA.toString(), dto.getStatus());
        assertEquals("Comentário final", dto.getComentarios());
        verify(fileService).finalizeSubmission(any(), anyString());
    }

    @Test
    void excluirDocumento_quandoStatusEmProcessamento_deleta() throws IOException {
        Solicitacao sol = new Solicitacao();
        sol.setStatus(StatusSolicitacao.DOCUMENTOS_ENVIADOS);
        sol.setAlunoMatricula(MATRICULA);
        sol.setAlunoNome(NOME);
        Documento doc = new Documento();
        doc.setDriveFileId("drive99");
        doc.setSolicitacao(sol);

        when(documentoRepository.findById(99L))
            .thenReturn(Optional.of(doc));

        service.excluirDocumento(99L);

        verify(fileService).deleteFile(any(), eq("drive99"));
        verify(documentoRepository).delete(doc);
    }

    @Test
    void excluirDocumento_quandoFinalizada_lancaExcecao() {
        Solicitacao sol = new Solicitacao();
        sol.setStatus(StatusSolicitacao.FINALIZADA);
        Documento doc = new Documento();
        doc.setSolicitacao(sol);

        when(documentoRepository.findById(100L))
            .thenReturn(Optional.of(doc));

        assertThrows(IllegalStateException.class,
            () -> service.excluirDocumento(100L));
    }
}
