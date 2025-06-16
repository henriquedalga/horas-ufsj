package IntegrandoDrive.service;

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

    private java.io.File tempFile;

    @BeforeEach
    void setUp() throws IOException {
        tempFile = java.io.File.createTempFile("test", ".txt");
    }

    @Test
    void createFolder_delegatesToPersistence() throws IOException {
        when(persistence.createFolderIfNotExists("nome", "parentId"))
            .thenReturn("folder123");

        String result = service.createFolder("nome", "parentId");

        assertEquals("folder123", result);
        verify(persistence).createFolderIfNotExists("nome", "parentId");
    }

    @Test
    void uploadFile_beforeFinalized_delegatesAndReturnsId() throws IOException {
        when(persistence.uploadFile(tempFile, "fld")).thenReturn("fileABC");

        String id = service.uploadFile(tempFile, "fld", "EM_PROCESSAMENTO");

        assertEquals("fileABC", id);
        verify(persistence).uploadFile(tempFile, "fld");
    }

    @Test
    void uploadFile_afterFinalized_throwsException() {
        assertThrows(IllegalStateException.class,
            () -> service.uploadFile(tempFile, "fld", "FINALIZADA"));
    }

    @Test
    void deleteFile_beforeFinalized_delegates() throws IOException {
        service.deleteFile("fileId", "EM_PROCESSAMENTO");
        verify(persistence).deleteFile("fileId");
    }

    @Test
    void deleteFile_afterFinalized_throwsException() {
        assertThrows(IllegalStateException.class,
            () -> service.deleteFile("fileId", "FINALIZADA"));
    }

    @Test
    void finalizeSubmission_marksAllFilesReadOnly() throws IOException {
        File f1 = new File().setId("id1");
        File f2 = new File().setId("id2");
        FileList fl = new FileList().setFiles(List.of(f1, f2));

        when(persistence.listFilesInFolder("folderA")).thenReturn(fl);

        // call
        service.finalizeSubmission("folderA");

        verify(persistence).setFileReadOnly("id1", "Submissão finalizada");
        verify(persistence).setFileReadOnly("id2", "Submissão finalizada");
    }

    @Test
    void getFolderLink_delegates() throws IOException {
        when(persistence.getFolderLink("f1")).thenReturn("link1");
        String link = service.getFolderLink("f1");
        assertEquals("link1", link);
        verify(persistence).getFolderLink("f1");
    }

    @Test
    void getFileLink_delegates() throws IOException {
        when(persistence.getFileLink("f2")).thenReturn("link2");
        String link = service.getFileLink("f2");
        assertEquals("link2", link);
        verify(persistence).getFileLink("f2");
    }

    @Test
    void backupDatabase_delegatesToUpload() throws IOException {
        java.io.File dump = java.io.File.createTempFile("dump", ".sql");
        when(persistence.uploadFile(dump, "backupFolder")).thenReturn("backupId");

        String id = service.backupDatabase(dump, "backupFolder");

        assertEquals("backupId", id);
        verify(persistence).uploadFile(dump, "backupFolder");
    }
}