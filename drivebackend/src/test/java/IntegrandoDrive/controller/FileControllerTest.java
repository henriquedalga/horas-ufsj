package IntegrandoDrive.controller;

import IntegrandoDrive.service.FileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FileController.class)
class FileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FileService fileService;

    private MockMultipartFile multipartFile;

    @BeforeEach
    void setUp() {
        multipartFile = new MockMultipartFile(
            "file", "test.txt",
            MediaType.TEXT_PLAIN_VALUE,
            "Hello".getBytes()
        );
    }

    @Test
    void uploadFile_beforeFinalized_returnsFileId() throws Exception {
        when(fileService.uploadFile(any(), eq("fld"), eq("EM_PROCESSAMENTO")))
            .thenReturn("file123");

        mockMvc.perform(multipart("/api/files/upload")
                .file(multipartFile)
                .param("folderId", "fld")
                .param("status", "EM_PROCESSAMENTO")
            )
            .andExpect(status().isOk())
            .andExpect(content().string("file123"));
    }

    @Test
    void uploadFile_afterFinalized_returns500() throws Exception {
        when(fileService.uploadFile(any(), anyString(), eq("FINALIZADA")))
            .thenThrow(new IllegalStateException("Não é possível adicionar arquivos a submissão finalizada."));

        mockMvc.perform(multipart("/api/files/upload")
                .file(multipartFile)
                .param("folderId", "fld")
                .param("status", "FINALIZADA")
            )
            .andExpect(status().isInternalServerError());
    }

    @Test
    void deleteFile_beforeFinalized_returns204() throws Exception {
        doNothing().when(fileService).deleteFile("fileX", "EM_PROCESSAMENTO");

        mockMvc.perform(delete("/api/files/fileX")
                .param("status", "EM_PROCESSAMENTO")
            )
            .andExpect(status().isNoContent());
    }

    @Test
    void deleteFile_afterFinalized_returns400() throws Exception {
        doThrow(new IllegalStateException("Não é possível excluir arquivos de submissão finalizada."))
            .when(fileService).deleteFile("fileY", "FINALIZADA");

        mockMvc.perform(delete("/api/files/fileY")
                .param("status", "FINALIZADA")
            )
            .andExpect(status().isBadRequest());
    }

    @Test
    void finalizeSubmission_returns204() throws Exception {
        doNothing().when(fileService).finalizeSubmission("fld");

        mockMvc.perform(post("/api/files/finalize")
                .param("folderId", "fld")
            )
            .andExpect(status().isNoContent());
    }

    @Test
    void getFolderLink_returnsLink() throws Exception {
        when(fileService.getFolderLink("f1")).thenReturn("link1");

        mockMvc.perform(get("/api/files/folder/f1/link"))
            .andExpect(status().isOk())
            .andExpect(content().string("link1"));
    }

    @Test
    void getFileLink_returnsLink() throws Exception {
        when(fileService.getFileLink("f2")).thenReturn("link2");

        mockMvc.perform(get("/api/files/file/f2/link"))
            .andExpect(status().isOk())
            .andExpect(content().string("link2"));
    }
}
