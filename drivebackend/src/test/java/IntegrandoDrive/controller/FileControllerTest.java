package IntegrandoDrive.controller;

import IntegrandoDrive.model.Student;
import IntegrandoDrive.persistence.DrivePersistence;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.io.IOException;

import static org.mockito.Mockito.verify;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileControllerTest {

    @Mock
    private DrivePersistence persistence;

    @InjectMocks
    private FileController controller;

    private Student student;
    private java.io.File localFile;

    @BeforeEach
    void setUp() throws IOException {
        student   = new Student("Naan");
        localFile = java.io.File.createTempFile("tmp", ".pdf");
    }

    @Test
    void createStudentFolder_delegatesToPersistence() throws IOException {
        when(persistence.createFolderIfNotExists("Naan", "parentId"))
            .thenReturn("folder123");

        String pastaId = controller.createStudentFolder(student, "parentId");
        assertEquals("folder123", pastaId);
        verify(persistence).createFolderIfNotExists("Naan", "parentId");
    }

    @Test
    void upload_beforeSubmission_delegatesAndReturnsFileId() throws IOException {
        when(persistence.uploadFile(localFile, "fld")).thenReturn("file123");

        String fileId = controller.uploadFile(student, localFile, "fld");
        assertEquals("file123", fileId);
        verify(persistence).uploadFile(localFile, "fld");
    }

    @Test
    void upload_afterSubmission_throws() {
        student.setSubmitted(true);
        assertThrows(IllegalStateException.class,
            () -> controller.uploadFile(student, localFile, "fld"));
    }

    @Test
    void delete_beforeSubmission_delegates() throws IOException {
        controller.deleteFile(student, "fId");
        verify(persistence).deleteFile("fId");
    }

    @Test
    void delete_afterSubmission_throws() {
        student.setSubmitted(true);
        assertThrows(IllegalStateException.class,
            () -> controller.deleteFile(student, "fId"));
    }

    @Test
    void finalizeSubmission_marksSubmitted_andSetsReadOnlyOnAllFiles() throws IOException {
        // prepara lista simulada de arquivos na pasta
        File f1 = new File().setId("id1");
        File f2 = new File().setId("id2");
        FileList fl = new FileList().setFiles(List.of(f1, f2));

        when(persistence.listFilesInFolder("pastaId")).thenReturn(fl);

        // executa
        controller.finalizeSubmission(student, "pastaId");

        // valida que o aluno foi marcado
        assertTrue(student.hasSubmitted());

        // valida chamadas de setFileReadOnly
        verify(persistence).setFileReadOnly("id1", "Submissão finalizada");
        verify(persistence).setFileReadOnly("id2", "Submissão finalizada");
    }

    @Test
    void getFolderLink_delegates() throws IOException {
        when(persistence.getFolderLink("f1")).thenReturn("linkF");
        assertEquals("linkF", controller.getFolderLink("f1"));
        verify(persistence).getFolderLink("f1");
    }

    @Test
    void getFileLink_delegates() throws IOException {
        when(persistence.getFileLink("f2")).thenReturn("linkA");
        assertEquals("linkA", controller.getFileLink("f2"));
        verify(persistence).getFileLink("f2");
    }

    @Test
    void backupDatabase_delegates() throws IOException {
        java.io.File dump = java.io.File.createTempFile("dum", ".sql");
        controller.backupDatabase(dump, "root");
        verify(persistence).backupDatabase(dump, "root");
    }
}