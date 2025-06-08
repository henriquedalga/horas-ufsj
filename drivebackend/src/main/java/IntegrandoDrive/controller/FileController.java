package IntegrandoDrive.controller;

import IntegrandoDrive.model.Student;
import IntegrandoDrive.service.FileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/files")
public class FileController {

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/folder")
    public ResponseEntity<String> createStudentFolder(
            @RequestBody Student student,
            @RequestParam String parentFolderId) throws IOException {
        return ResponseEntity.ok(
            fileService.createStudentFolder(student, parentFolderId)
        );
    }

    // demais endpoints que delegam para fileService…
}
