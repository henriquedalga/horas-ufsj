package com.universidade.sighoras.controller;

import com.universidade.sighoras.entity.HoraTipo;
import com.universidade.sighoras.service.AlunoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
    controllers = AlunoRestController.class,
    excludeAutoConfiguration = SecurityAutoConfiguration.class
)
@AutoConfigureMockMvc(addFilters = false)
class AlunoRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AlunoService alunoService;

    @Test
    void uploadDocumentos_fluxoFeliz_retornaDTO() throws Exception {
        SolicitacaoResponseDTO dto = new SolicitacaoResponseDTO();
        dto.setId(33L);
        dto.setStatus("DOCUMENTOS_ENVIADOS");

        when(alunoService.processarDocumentos(
                any(MultipartFile[].class),
                anyLong(),
                anyString(),
                any(HoraTipo.class))
        ).thenReturn(dto);

        MockMultipartFile file = new MockMultipartFile(
            "files", "b.pdf",
            MediaType.APPLICATION_PDF_VALUE,
            "pdfconteudo".getBytes()
        );

        mockMvc.perform(multipart("/api/aluno/upload-documentos")
                .file(file)
                .param("matricula", "2021")
                .param("nome", "Ciclano")
                .param("horaTipo", "EXTENSAO"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(33))
            .andExpect(jsonPath("$.status").value("DOCUMENTOS_ENVIADOS"));
    }

    @Test
    void uploadDocumentos_quandoStatusFinalizada_retorna500() throws Exception {
        when(alunoService.processarDocumentos(
                any(MultipartFile[].class),
                anyLong(),
                anyString(),
                any(HoraTipo.class))
        ).thenThrow(new IllegalStateException(
            "Não é possível adicionar arquivos a submissão finalizada"
        ));

        MockMultipartFile file = new MockMultipartFile(
            "files", "doc.txt",
            MediaType.TEXT_PLAIN_VALUE,
            "conteúdo".getBytes()
        );

        mockMvc.perform(multipart("/api/aluno/upload-documentos")
                .file(file)
                .param("matricula", "2021")
                .param("nome", "Fulano")
                .param("horaTipo", "COMPLEMENTAR"))
            .andExpect(status().is5xxServerError())
            .andExpect(jsonPath("$.message").value("Não é possível adicionar arquivos a submissão finalizada"));
    }

    @Test
    void buscarSolicitacao_fluxoFeliz_retornaDTO() throws Exception {
        SolicitacaoResponseDTO dto = new SolicitacaoResponseDTO();
        dto.setId(5L);

        when(alunoService.buscarSolicitacao(5L)).thenReturn(dto);

        mockMvc.perform(get("/api/aluno/solicitacao/5"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(5));
    }

    @Test
    void buscarSolicitacao_quandoNaoExistir_retorna404() throws Exception {
        when(alunoService.buscarSolicitacao(99L))
            .thenThrow(new RuntimeException("Solicitação não encontrada ID: 99"));

        mockMvc.perform(get("/api/aluno/solicitacao/99"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("Solicitação não encontrada ID: 99"));
    }

    @Test
    void buscarSolicitacoesPorAluno_fluxoFeliz_retornaLista() throws Exception {
        SolicitacaoResponseDTO dto1 = new SolicitacaoResponseDTO(); dto1.setId(11L);
        SolicitacaoResponseDTO dto2 = new SolicitacaoResponseDTO(); dto2.setId(22L);

        when(alunoService.buscarSolicitacoesPorAluno(2021L))
            .thenReturn(List.of(dto1, dto2));

        mockMvc.perform(get("/api/aluno/solicitacoes/aluno/2021"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].id").value(11))
            .andExpect(jsonPath("$[1].id").value(22));
    }

    @Test
    void buscarSolicitacoesPorAluno_semRegistros_retorna200ListaVazia() throws Exception {
        when(alunoService.buscarSolicitacoesPorAluno(0L))
            .thenReturn(List.of());

        mockMvc.perform(get("/api/aluno/solicitacoes/aluno/0"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void finalizarSubmissao_fluxoFeliz_retornaDTO() throws Exception {
        SolicitacaoResponseDTO dto = new SolicitacaoResponseDTO(); dto.setId(9L);

        when(alunoService.finalizarSubmissao(9L, "Obs")).thenReturn(dto);

        mockMvc.perform(post("/api/aluno/finalizar-submissao/9")
                .param("comentario", "Obs"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(9));
    }

    @Test
    void finalizeSubmissao_quandoServiceLancaIOException_retorna500() throws Exception {
        when(alunoService.finalizarSubmissao(5L, "Obs"))
            .thenThrow(new RuntimeException("Erro no Drive"));

        mockMvc.perform(post("/api/aluno/finalizar-submissao/5")
                .param("comentario", "Obs"))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.message").value("Erro no Drive"));
    }

    @Test
    void obterLinkPasta_fluxoFeliz_retornaString() throws Exception {
        when(alunoService.obterLinkPastaAluno(4L)).thenReturn("link123");

        mockMvc.perform(get("/api/aluno/pasta-link/4"))
            .andExpect(status().isOk())
            .andExpect(content().string("link123"));
    }

    @Test
    void excluirDocumento_fluxoFeliz_retorna204() throws Exception {
        doNothing().when(alunoService).excluirDocumento(15L);

        mockMvc.perform(delete("/api/aluno/documento/15"))
            .andExpect(status().isNoContent());
    }

    @Test
    void excluirDocumento_quandoServiceLancaIllegalState_retorna400() throws Exception {
        doThrow(new IllegalStateException("Não permitido"))
            .when(alunoService).excluirDocumento(77L);

        mockMvc.perform(delete("/api/aluno/documento/77"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Não permitido"));
    }
}
