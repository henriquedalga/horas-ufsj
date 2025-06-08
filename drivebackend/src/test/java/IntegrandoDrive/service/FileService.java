package IntegrandoDrive.service;

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

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileServiceTest {

    @Mock
    private DrivePersistence persistence;

    @InjectMocks
    private FileService service;

    private Student student;
    private java.io.File localFile;

    @BeforeEach
    void setUp() throws IOException {
        student   = new Student("Fulano", "2021001");
        localFile = java.io.File.createTempFile("tmp", ".txt");
    }

    @Test
    void createStudentFolder_delegatesToPersistence() throws IOException {
        when(persistence.createFolderIfNotExists("Fulano", "parentId"))
            .thenReturn("folder123");

        String pastaId = service.createStudentFolder(student, "parentId");
        assertEquals("folder123", pastaId);
        verify(persistence).createFolderIfNotExists("Fulano", "parentId");
    }

    @Test
    void upload_beforeSubmission_delegatesAndReturnsFileId() throws IOException {
        when(persistence.uploadFile(localFile, "fld")).thenReturn("fileABC");

        String id = service.uploadFile(student, localFile, "fld");
        assertEquals("fileABC", id);
        verify(persistence).uploadFile(localFile, "fld");
    }

    @Test
    void upload_afterSubmission_throws() {
        // simula que o aluno já submeteu
        student.setSubmitted(true);

        assertThrows(IllegalStateException.class,
            () -> service.uploadFile(student, localFile, "fld"));
    }

    @Test
    void delete_beforeSubmission_delegates() throws IOException {
        service.deleteFile(student, "fileXYZ");
        verify(persistence).deleteFile("fileXYZ");
    }

    @Test
    void delete_afterSubmission_throws() {
        student.setSubmitted(true);

        assertThrows(IllegalStateException.class,
            () -> service.deleteFile(student, "anyId"));
    }

    @Test
    void finalizeSubmission_marksSubmitted_andSetsReadOnlyOnAllFiles() throws IOException {
        File f1 = new File().setId("id1");
        File f2 = new File().setId("id2");
        FileList fl = new FileList().setFiles(List.of(f1, f2));

        when(persistence.listFilesInFolder("folderA")).thenReturn(fl);

        service.finalizeSubmission(student, "folderA");

        // depois de finalizeSubmission, student.hasSubmitted() deve ser true
        assertTrue(student.hasSubmitted());
        // todos os arquivos ficaram read-only
        verify(persistence).setFileReadOnly("id1", "Submissão finalizada");
        verify(persistence).setFileReadOnly("id2", "Submissão finalizada");
    }

    @Test
    void getFolderLink_delegates() throws IOException {
        when(persistence.getFolderLink("f1")).thenReturn("link1");

        assertEquals("link1", service.getFolderLink("f1"));
        verify(persistence).getFolderLink("f1");
    }

    @Test
    void getFileLink_delegates() throws IOException {
        when(persistence.getFileLink("f2")).thenReturn("link2");

        assertEquals("link2", service.getFileLink("f2"));
        verify(persistence).getFileLink("f2");
    }

    @Test
    void backupDatabase_delegates() throws IOException {
        java.io.File dump = java.io.File.createTempFile("dump", ".sql");
        service.backupDatabase(dump, "backupFolder");
        verify(persistence).backupDatabase(dump, "backupFolder");
    }
}
