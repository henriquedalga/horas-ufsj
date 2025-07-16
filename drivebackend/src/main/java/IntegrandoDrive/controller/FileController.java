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
     * Faz upload de arquivo, desde que a solicitação não esteja finalizada.
     */
    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(
        @RequestParam MultipartFile file,
        @RequestParam String folderId,
        @RequestParam int hourtype // 0=complementar, 1=extensao
    ) throws IOException {
        File temp = new File(TMP_DIR, file.getOriginalFilename());
        file.transferTo(temp);
        try {
            String fileId = fileService.uploadFile(temp, folderId, hourtype);
            return ResponseEntity.ok(fileId);
        } catch (IllegalStateException ex) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
        } finally {
            temp.delete();
        }
    }

    /** 
     * Exclui um arquivo, desde que a solicitação não esteja finalizada.
     */
    @DeleteMapping("/{fileId}")
    public ResponseEntity<Void> deleteFile(
        @PathVariable String fileId,
        @RequestParam String status,
        @RequestParam int hourType  // 0=compl, 1=ext
    ) throws IOException {
        try {
            fileService.deleteFile(fileId, hourType);
            return ResponseEntity.noContent().build();
        } catch (IllegalStateException ex) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .build();
        }
    }

    /** Marca todos os arquivos como read‑only (finaliza submissão). */
    @PostMapping("/finalize")
    public ResponseEntity<Void> finalizeSubmission(
        @RequestParam String folderId,
        @RequestParam int hourType  // 0=compl, 1=ext
    ) throws IOException {
        fileService.finalizeSubmission(folderId, hourType);
        return ResponseEntity.noContent().build();
    }

    /** Rejeita submissão (torna todos os arquivos editáveis de novo). */
    @PostMapping("/reject")
    public ResponseEntity<Void> rejectSubmission(
        @RequestParam String folderId,
        @RequestParam int hourType  // 0=compl, 1=ext
    ) throws IOException {
        fileService.rejectSubmission(folderId, hourType);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/folder/{folderId}/link")
    public ResponseEntity<String> getFolderLink(
        @PathVariable String folderId,
        @RequestParam int hourType  // 0=compl, 1=ext
        ) throws IOException {
        return ResponseEntity.ok(fileService.getFolderLink(folderId, hourType));
    }

    @GetMapping("/file/{fileId}/link")
    public ResponseEntity<String> getFileLink(
        @PathVariable String fileId,
        @RequestParam int hourType  // 0=compl, 1=ext
        ) throws IOException {
        return ResponseEntity.ok(fileService.getFileLink(fileId, hourType));
    }
}
