package com.universidade.sighoras.service;

import com.universidade.sighoras.controller.SolicitacaoResponseDTO;
import com.universidade.sighoras.entity.Arquivo;
import com.universidade.sighoras.entity.HoraTipo;
import com.universidade.sighoras.entity.Solicitacao;
import com.universidade.sighoras.repository.ArquivoRepository;
import com.universidade.sighoras.repository.SolicitacaoRepository;
import IntegrandoDrive.service.FileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.*;

@ExtendWith(MockitoExtension.class)
class AlunoServiceTest {

    @Mock private FileService fileService;
    @Mock private SolicitacaoRepository solicitacaoRepo;
    @Mock private ArquivoRepository arquivoRepo;

    @InjectMocks private AlunoService service;

    private MockMultipartFile mp;

    @BeforeEach
    void setUp() {
        mp = new MockMultipartFile("files", "teste.txt", "text/plain", "conteudo".getBytes());
    }

    @Test
    void processarDocumentos_fluxoPrincipal() throws IOException {
        // Drive
        when(fileService.createFolder(eq("Fulano"), anyString())).thenReturn("folder123");
        when(fileService.getFolderLink("folder123")).thenReturn("https://drive/folder123");
        when(fileService.uploadFile(any(File.class), eq("folder123"), eq("EM_PROCESSAMENTO"))).thenReturn("file123");
        when(fileService.getFileLink("file123")).thenReturn("https://drive/link/file123");

        // Repositórios
        when(solicitacaoRepo.findByMatriculaOrderByDataSolicitacaoDesc(anyLong()))
            .thenReturn(Collections.emptyList());                     // << aqui!
        when(solicitacaoRepo.save(any(Solicitacao.class))).thenAnswer(inv -> {
            Solicitacao s = inv.getArgument(0);
            if (s.getId() == null) s.setId(1L);
            return s;
        });
        when(arquivoRepo.save(any(Arquivo.class))).thenAnswer(inv -> inv.getArgument(0));
        when(arquivoRepo.findByIdSolicitacao(anyLong()))
            .thenReturn(List.of(new Arquivo()));                      // garante lista não vazia

        SolicitacaoResponseDTO dto = service.processarDocumentos(
            new MultipartFile[]{ mp }, 2021001L, "Fulano", HoraTipo.COMPLEMENTAR);

        assertNotNull(dto);
        assertEquals("DOCUMENTOS_ENVIADOS", dto.getStatus());
        assertFalse(dto.getArquivos().isEmpty());

        verify(fileService).createFolder(eq("Fulano"), anyString());
        verify(fileService).uploadFile(any(File.class), eq("folder123"), eq("EM_PROCESSAMENTO"));
        verify(arquivoRepo).save(any(Arquivo.class));
        verify(solicitacaoRepo, times(3)).save(any(Solicitacao.class));
    }

    @Test
    void processarDocumentos_quandoIOException_lanca500() throws IOException {
        // força a lista de submissões anteriores vazia
        when(solicitacaoRepo.findByMatriculaOrderByDataSolicitacaoDesc(anyLong()))
            .thenReturn(Collections.emptyList());
        // exceção de drive
        when(fileService.createFolder(anyString(), anyString()))
            .thenThrow(new IOException("Drive indisponível"));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
            service.processarDocumentos(new MultipartFile[]{ mp }, 1L, "X", HoraTipo.COMPLEMENTAR));

        assertEquals(INTERNAL_SERVER_ERROR, ex.getStatusCode());
        assertEquals("Erro no Drive", ex.getReason());
    }

    @Test
    void buscarSolicitacao_quandoExistir_retornaDTO() {
        when(solicitacaoRepo.findById(7L)).thenReturn(Optional.of(new Solicitacao(){{
            setId(7L); setMatricula(2021001L);
            setNome("Fulano"); setStatus("EM_PROCESSAMENTO");
            setDataSolicitacao(LocalDateTime.now().toString());
        }}));

        SolicitacaoResponseDTO dto = service.buscarSolicitacao(7L);
        assertEquals(7L, dto.getId());
        assertEquals(2021001L, dto.getMatricula());
    }

    @Test
    void buscarSolicitacao_quandoNaoExistir_lanca404() {
        when(solicitacaoRepo.findById(42L)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
            service.buscarSolicitacao(42L));

        assertEquals(NOT_FOUND, ex.getStatusCode());
        assertEquals("Solicitação não encontrada ID: 42", ex.getReason());
    }

    @Test
    void buscarSolicitacoesPorAluno_fluxoPrincipal() {
        Solicitacao s1 = new Solicitacao(); s1.setId(1L); s1.setMatricula(2021L); s1.setStatus("EM_PROCESSAMENTO");
        Solicitacao s2 = new Solicitacao(); s2.setId(2L); s2.setMatricula(2021L); s2.setStatus("DOCUMENTOS_ENVIADOS");

        when(solicitacaoRepo.findByMatriculaOrderByDataSolicitacaoDesc(2021L))
            .thenReturn(List.of(s1, s2));

        List<SolicitacaoResponseDTO> list = service.buscarSolicitacoesPorAluno(2021L);
        assertEquals(2, list.size());
        assertEquals("EM_PROCESSAMENTO", list.get(0).getStatus());
        assertEquals("DOCUMENTOS_ENVIADOS", list.get(1).getStatus());
    }

    @Test
    void finalizarSubmissao_fluxoPrincipal() throws IOException {
        Solicitacao sol = new Solicitacao(); sol.setId(5L); sol.setStatus("DOCUMENTOS_ENVIADOS"); sol.setLinkPasta("folder");
        when(solicitacaoRepo.findById(5L)).thenReturn(Optional.of(sol));
        when(solicitacaoRepo.save(any(Solicitacao.class))).thenAnswer(inv -> inv.getArgument(0));
        doNothing().when(fileService).finalizeSubmission("folder");

        SolicitacaoResponseDTO dto = service.finalizarSubmissao(5L, "Obs");
        assertEquals("FINALIZADA", dto.getStatus());
        assertEquals("Obs", dto.getResposta());
        verify(fileService).finalizeSubmission("folder");
    }

    @Test
    void finalizarSubmissao_quandoIOException_lanca500() throws IOException {
        when(solicitacaoRepo.findById(5L)).thenReturn(Optional.of(new Solicitacao(){{
            setId(5L); setLinkPasta("x");
        }}));
        doThrow(new IOException("falha")).when(fileService).finalizeSubmission("x");

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
            service.finalizarSubmissao(5L, "Obs"));

        assertEquals(INTERNAL_SERVER_ERROR, ex.getStatusCode());
        assertEquals("Erro no Drive", ex.getReason());
    }

    @Test
    void excluirDocumento_fluxoPrincipal() throws IOException {
        Arquivo doc = new Arquivo(); doc.setId(50L); doc.setIdSolicitacao(10L); doc.setDrivelink("fileXYZ");
        Solicitacao sol = new Solicitacao(); sol.setId(10L); sol.setStatus("EM_PROCESSAMENTO");

        when(arquivoRepo.findById(50L)).thenReturn(Optional.of(doc));
        when(solicitacaoRepo.findById(10L)).thenReturn(Optional.of(sol));
        doNothing().when(fileService).deleteFile("fileXYZ", "EM_PROCESSAMENTO");

        service.excluirDocumento(50L);

        verify(fileService).deleteFile("fileXYZ", "EM_PROCESSAMENTO");
        verify(arquivoRepo).delete(doc);
    }

    @Test
    void excluirDocumento_quandoStatusFinalizada_lanca400() {
        Arquivo doc = new Arquivo(); doc.setId(777L); doc.setIdSolicitacao(123L); doc.setDrivelink("abc");
        Solicitacao sol = new Solicitacao(); sol.setId(123L); sol.setStatus("FINALIZADA");

        when(arquivoRepo.findById(777L)).thenReturn(Optional.of(doc));
        when(solicitacaoRepo.findById(123L)).thenReturn(Optional.of(sol));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
            () -> service.excluirDocumento(777L));

        assertEquals(BAD_REQUEST, ex.getStatusCode());
        assertEquals("Não permitido", ex.getReason());
    }

    @Test
    void obterLinkPastaAluno_fluxoPrincipal() {
        Solicitacao sol = new Solicitacao(); sol.setId(99L); sol.setLinkPasta("https://drive/f/99");
        when(solicitacaoRepo.findById(99L)).thenReturn(Optional.of(sol));

        String link = service.obterLinkPastaAluno(99L);
        assertEquals("https://drive/f/99", link);
        verifyNoInteractions(fileService);
    }

    @Test
    void obterLinkPastaAluno_quandoNaoExistir_lanca404() {
        when(solicitacaoRepo.findById(999L)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
            () -> service.obterLinkPastaAluno(999L));

        assertEquals(NOT_FOUND, ex.getStatusCode());
        assertEquals("Solicitação não encontrada ID: 999", ex.getReason());
    }
}
