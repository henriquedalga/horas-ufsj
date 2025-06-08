package IntegrandoDrive.controller;

import IntegrandoDrive.controller.AlunoRestController;
import IntegrandoDrive.dto.SolicitacaoResponseDTO;
import IntegrandoDrive.service.AlunoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AlunoRestController.class)
class AlunoRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AlunoService alunoService;

    @Autowired
    private ObjectMapper mapper;

    @Test
    void uploadDocumentos_retorna200eDTO() throws Exception {
        SolicitacaoResponseDTO dto = new SolicitacaoResponseDTO();
        dto.setId(3L);
        when(alunoService.processarDocumentos(any(), anyString(), anyString()))
            .thenReturn(dto);

        MockMultipartFile file = new MockMultipartFile(
            "files", "a.txt",
            MediaType.TEXT_PLAIN_VALUE,
            "conteudo".getBytes());

        mockMvc.perform(multipart("/api/aluno/upload-documentos")
                .file(file)
                .param("alunoMatricula", "123")
                .param("alunoNome", "Fulano"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(3));
    }

    @Test
    void buscarSolicitacao_porId_retornaDTO() throws Exception {
        SolicitacaoResponseDTO dto = new SolicitacaoResponseDTO();
        dto.setId(5L);
        when(alunoService.buscarSolicitacao(5L)).thenReturn(dto);

        mockMvc.perform(get("/api/aluno/solicitacao/5"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(5));
    }

    @Test
    void buscarSolicitacoesPorAluno_retornaLista() throws Exception {
        SolicitacaoResponseDTO dto = new SolicitacaoResponseDTO();
        dto.setId(7L);
        when(alunoService.buscarSolicitacoesPorAluno("2021"))
            .thenReturn(List.of(dto));

        mockMvc.perform(get("/api/aluno/solicitacoes/aluno/2021"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(7));
    }

    @Test
    void finalizarSubmissao_retornaDTO() throws Exception {
        SolicitacaoResponseDTO dto = new SolicitacaoResponseDTO();
        dto.setId(9L);
        when(alunoService.finalizarSubmissao(9L, "Obs"))
            .thenReturn(dto);

        mockMvc.perform(post("/api/aluno/finalizar-submissao/9")
                .param("comentario", "Obs"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(9));
    }

    @Test
    void obterLinkPasta_retornaStringSimples() throws Exception {
        when(alunoService.obterLinkPastaAluno(4L))
            .thenReturn("link123");

        mockMvc.perform(get("/api/aluno/pasta-link/4"))
            .andExpect(status().isOk())
            .andExpect(content().string("link123"));
    }

    @Test
    void excluirDocumento_retorna204() throws Exception {
        doNothing().when(alunoService).excluirDocumento(15L);

        mockMvc.perform(delete("/api/aluno/documento/15"))
            .andExpect(status().isNoContent());
    }
}