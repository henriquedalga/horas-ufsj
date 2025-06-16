package IntegrandoDrive.controller;

import IntegrandoDrive.service.FileService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/api/files")
public class FileController {

    private final FileService fileService;
    private static final String TMP_DIR = System.getProperty("java.io.tmpdir");

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    /**
     * Faz upload de um único arquivo, desde que a solicitação ainda não esteja finalizada.
     */
    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(
            @RequestParam MultipartFile file,
            @RequestParam String folderId,
            @RequestParam String status
    ) throws IOException {
        // converte MultipartFile em File temporário
        File temp = new File(TMP_DIR, file.getOriginalFilename());
        file.transferTo(temp);
        try {
            String fileId = fileService.uploadFile(temp, folderId, status);
            return ResponseEntity.ok(fileId);
        } catch (IllegalStateException ex) {
            // mapeia erro de submissão finalizada para 500
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ex.getMessage());
        } finally {
            temp.delete();
        }
    }

    /**
     * Exclui um arquivo do Drive, desde que a solicitação não esteja finalizada.
     */
    @DeleteMapping("/{fileId}")
    public ResponseEntity<Void> deleteFile(
            @PathVariable String fileId,
            @RequestParam String status
    ) throws IOException {
        try {
            fileService.deleteFile(fileId, status);
            return ResponseEntity.noContent().build();
        } catch (IllegalStateException ex) {
            // mapeia erro de submissão finalizada para 400
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        }
    }

    /**
     * Marca todos os arquivos de uma pasta como read‑only (finaliza submissão).
     */
    @PostMapping("/finalize")
    public ResponseEntity<Void> finalizeSubmission(
            @RequestParam String folderId
    ) throws IOException {
        fileService.finalizeSubmission(folderId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Retorna o link público de uma pasta no Drive.
     */
    @GetMapping("/folder/{folderId}/link")
    public ResponseEntity<String> getFolderLink(
            @PathVariable String folderId
    ) throws IOException {
        return ResponseEntity.ok(fileService.getFolderLink(folderId));
    }

    /**
     * Retorna o link público de um arquivo no Drive.
     */
    @GetMapping("/file/{fileId}/link")
    public ResponseEntity<String> getFileLink(
            @PathVariable String fileId
    ) throws IOException {
        return ResponseEntity.ok(fileService.getFileLink(fileId));
    }
}