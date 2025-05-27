package IntegrandoDrive.persistence;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.*;
import com.google.api.client.http.FileContent; 
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import org.mockito.Mockito;  


@ExtendWith(MockitoExtension.class)
class DrivePersistenceTest {

    @Mock
    private Drive driveService;

    @InjectMocks
    private DrivePersistence persistence;

    @BeforeEach
    void setUp() {
        // nothing
    }

    @Test
    void createFolderIfNotExists_whenExists_returnsExistingId() throws IOException {
        Drive.Files filesApi   = mock(Drive.Files.class);
        Drive.Files.List listC  = mock(Drive.Files.List.class);
        FileList result         = new FileList()
                                      .setFiles(List.of(new com.google.api.services.drive.model.File().setId("123")));

        when(driveService.files()).thenReturn(filesApi);
        when(filesApi.list()).thenReturn(listC);
        when(listC.setQ(anyString())).thenReturn(listC);
        when(listC.setFields(anyString())).thenReturn(listC);
        when(listC.execute()).thenReturn(result);

        String id = persistence.createFolderIfNotExists("X", "root");
        assertEquals("123", id);
    }
    @Test
    void createFolderIfNotExists_whenNotExists_createsAndReturnsNewId() throws IOException {
        Drive.Files filesApi    = mock(Drive.Files.class);
        Drive.Files.List listC  = mock(Drive.Files.List.class);
        Drive.Files.Create createC = mock(Drive.Files.Create.class);

        FileList emptyList = new FileList().setFiles(Collections.emptyList());
        com.google.api.services.drive.model.File newMeta =
            new com.google.api.services.drive.model.File().setId("NEWID");

        when(driveService.files()).thenReturn(filesApi);
        when(filesApi.list()).thenReturn(listC);
        when(listC.setQ(anyString())).thenReturn(listC);
        when(listC.setFields(anyString())).thenReturn(listC);
        when(listC.execute()).thenReturn(emptyList);

        doReturn(createC)
        .when(filesApi)
        .create(any(com.google.api.services.drive.model.File.class));
        when(createC.setFields("id")).thenReturn(createC);
        when(createC.execute()).thenReturn(newMeta);

        String id = persistence.createFolderIfNotExists("Y", "root");
        assertEquals("NEWID", id);

        verify(filesApi).create(any(com.google.api.services.drive.model.File.class));
    }

    @Test
    void uploadFile_delegatesToApi_andReturnsFileId() throws IOException {
        Drive.Files filesApi      = mock(Drive.Files.class);
        Drive.Files.Create create = mock(Drive.Files.Create.class);
        com.google.api.services.drive.model.File meta = new com.google.api.services.drive.model.File().setId("FID");

        when(driveService.files()).thenReturn(filesApi);
        when(filesApi.create(any(), any(FileContent.class))).thenReturn(create);
        when(create.setFields("id")).thenReturn(create);
        when(create.execute()).thenReturn(meta);

        String fid = persistence.uploadFile(java.io.File.createTempFile("tmp", ".pdf"), "fld");
        assertEquals("FID", fid);
    }

    @Test
    void deleteFile_callsApiDelete() throws IOException {
        Drive.Files filesApi   = mock(Drive.Files.class);
        Drive.Files.Delete del = mock(Drive.Files.Delete.class);

        when(driveService.files()).thenReturn(filesApi);
        when(filesApi.delete("fId")).thenReturn(del);
        // execute() não retorna nada
        persistence.deleteFile("fId");
        verify(del).execute();
    }

    @Test
    void getFolderLink_returnsWebViewLink() throws IOException {
        Drive.Files filesApi = mock(Drive.Files.class);
        Drive.Files.Get getC = mock(Drive.Files.Get.class);
        com.google.api.services.drive.model.File meta =
            new com.google.api.services.drive.model.File()
                .setId("fid")
                .setWebViewLink("LINKF");

        when(driveService.files()).thenReturn(filesApi);
        when(filesApi.get("fid")).thenReturn(getC);
        when(getC.setFields("id, webViewLink")).thenReturn(getC);
        when(getC.execute()).thenReturn(meta);

        assertEquals("LINKF", persistence.getFolderLink("fid"));
    }

    @Test
    void getFileLink_returnsWebViewLink() throws IOException {
        Drive.Files filesApi = mock(Drive.Files.class);
        Drive.Files.Get getC = mock(Drive.Files.Get.class);
        com.google.api.services.drive.model.File meta =
            new com.google.api.services.drive.model.File()
                .setId("fid")
                .setWebViewLink("VIEW")
                .setWebContentLink("CONTENT");

        when(driveService.files()).thenReturn(filesApi);
        when(filesApi.get("fid")).thenReturn(getC);
        when(getC.setFields("id, webViewLink, webContentLink")).thenReturn(getC);
        when(getC.execute()).thenReturn(meta);

        assertEquals("VIEW", persistence.getFileLink("fid"));
    }

    @Test
    void findFileIdByName_whenFound_returnsId() throws IOException {
        Drive.Files filesApi   = mock(Drive.Files.class);
        Drive.Files.List listC = mock(Drive.Files.List.class);
        FileList result        = new FileList()
                                     .setFiles(List.of(new com.google.api.services.drive.model.File().setId("ABC")));

        when(driveService.files()).thenReturn(filesApi);
        when(filesApi.list()).thenReturn(listC);
        when(listC.setQ(anyString())).thenReturn(listC);
        when(listC.setFields("files(id)")).thenReturn(listC);
        when(listC.execute()).thenReturn(result);

        assertEquals("ABC", persistence.findFileIdByName("nome.pdf", "fld"));
    }

    @Test
    void findFileIdByName_whenNotFound_returnsNull() throws IOException {
        Drive.Files filesApi   = mock(Drive.Files.class);
        Drive.Files.List listC = mock(Drive.Files.List.class);
        FileList empty = new FileList().setFiles(Collections.emptyList());

        when(driveService.files()).thenReturn(filesApi);
        when(filesApi.list()).thenReturn(listC);
        when(listC.setQ(anyString())).thenReturn(listC);
        when(listC.setFields("files(id)")).thenReturn(listC);
        when(listC.execute()).thenReturn(empty);

        assertNull(persistence.findFileIdByName("x.pdf", "fld"));
    }

    @Test
    void setFileReadOnly_callsUpdateWithRestriction() throws IOException {
        Drive.Files filesApi   = mock(Drive.Files.class);
        Drive.Files.Update upd = mock(Drive.Files.Update.class);
        com.google.api.services.drive.model.File dummy = new com.google.api.services.drive.model.File();

        when(driveService.files()).thenReturn(filesApi);
        when(filesApi.update(eq("fid"), any(com.google.api.services.drive.model.File.class)))
            .thenReturn(upd);
        when(upd.setFields("contentRestrictions")).thenReturn(upd);

        persistence.setFileReadOnly("fid", "motivo");
        verify(upd).execute();
    }

    @Test
    void listFilesInFolder_returnsFileList() throws IOException {
        // precisa do método que listamos no DrivePersistence
        Drive.Files filesApi    = mock(Drive.Files.class);
        Drive.Files.List listC  = mock(Drive.Files.List.class);
        FileList result         = new FileList()
                                      .setFiles(List.of(new com.google.api.services.drive.model.File()));

        when(driveService.files()).thenReturn(filesApi);
        when(filesApi.list()).thenReturn(listC);
        when(listC.setQ(anyString())).thenReturn(listC);
        when(listC.setFields("files(id)")).thenReturn(listC);
        when(listC.execute()).thenReturn(result);

        FileList out = persistence.listFilesInFolder("fld");
        assertEquals(1, out.getFiles().size());
    }

    @Test
    void backupDatabase_createsFolderAndUploads() throws IOException {
        DrivePersistence spy = Mockito.spy(persistence);
        java.io.File dump    = java.io.File.createTempFile("dum", ".sql");

        doReturn("bkpId").when(spy).createFolderIfNotExists("db-backups", "root");
        doReturn("upId").when(spy).uploadFile(dump, "bkpId");

        spy.backupDatabase(dump, "root");

        verify(spy).createFolderIfNotExists("db-backups", "root");
        verify(spy).uploadFile(dump, "bkpId");
    }
}
