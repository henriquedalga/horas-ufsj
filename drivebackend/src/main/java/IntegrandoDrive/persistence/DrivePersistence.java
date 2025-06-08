package IntegrandoDrive.persistence;

import com.google.auth.oauth2.GoogleCredentials;  
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.ContentRestriction;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Collections;

@Component
public class DrivePersistence {
private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String APPLICATION_NAME = "Drive Submission App";
    private Drive driveService;

    public DrivePersistence(Drive driveService) {
        this.driveService = driveService;
    }
    public String createFolderIfNotExists(String folderName, String parentFolderId) throws IOException {
        String query = "mimeType = 'application/vnd.google-apps.folder' and name = '" + folderName + "' and '" + parentFolderId + "' in parents";
        FileList result = driveService.files().list()
                .setQ(query)
                .setFields("files(id, name)")
                .execute();
        if (!result.getFiles().isEmpty()) {
            return result.getFiles().get(0).getId();
        }
        File metadata = new File();
        metadata.setName(folderName);
        metadata.setMimeType("application/vnd.google-apps.folder");
        metadata.setParents(Collections.singletonList(parentFolderId));
        File created = driveService.files().create(metadata).setFields("id").execute();
        return created.getId();
    }
    public String getFolderLink(String folderId) throws IOException {
        File f = driveService.files()
            .get(folderId)
            .setFields("id, webViewLink")
            .execute();
        return f.getWebViewLink();
    }
    public String getFileLink(String fileId) throws IOException {
        File f = driveService.files()
            .get(fileId)
            .setFields("id, webViewLink, webContentLink")
            .execute();
        return f.getWebViewLink();
    }
    public String uploadFile(java.io.File filePath, String folderId) throws IOException {
        File fileMetadata = new File();
        fileMetadata.setName(filePath.getName());
        fileMetadata.setParents(Collections.singletonList(folderId));
    
        FileContent mediaContent = new FileContent("application/pdf", filePath);
    
        File uploadedFile = driveService.files().create(fileMetadata, mediaContent)
                .setFields("id")
                .execute();
    
        return uploadedFile.getId();
    }
    public String findFileIdByName(String fileName, String folderId) throws IOException {
        // Monta a query: nome exato, dentro dos parents, não enviado para lixeira
        String q = String.format(
            "name = '%s' and '%s' in parents and trashed = false",
            fileName.replace("'", "\\'"),  // escapa possíveis aspas simples
            folderId
        );
        FileList result = driveService.files().list()
            .setQ(q)
            .setFields("files(id)")
            .execute();
    
        if (result.getFiles().isEmpty()) {
            return null;
        }
        return result.getFiles().get(0).getId();
    }
    public FileList listFilesInFolder(String folderId) throws IOException {
        String q = String.format("'%s' in parents and trashed = false", folderId);
        return driveService.files().list()
                .setQ(q)
                .setFields("files(id)")
                .execute();
    }

    public void deleteFile(String fileId) throws IOException {
        driveService.files().delete(fileId).execute();
    }
    public void setFileReadOnly(String fileId, String reason) throws IOException {
        ContentRestriction restriction = new ContentRestriction().setReadOnly(true).setReason(reason);
        File update = new File().setContentRestrictions(Collections.singletonList(restriction));
        driveService.files().update(fileId, update).setFields("contentRestrictions").execute();
    }
    public void backupDatabase(java.io.File dbDump, String backupFolderId) throws IOException {
        String backupRoot = createFolderIfNotExists("db-backups", backupFolderId);
        uploadFile(dbDump, backupRoot);
    }
    public static Drive initDriveService() throws IOException, GeneralSecurityException {
        // Carrega credenciais do arquivo JSON no classpath
        InputStream in = DrivePersistence.class.getResourceAsStream("/credentials.json");
        GoogleCredentials creds = GoogleCredentials.fromStream(in)
            .createScoped(Collections.singletonList("https://www.googleapis.com/auth/drive"));

        HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        return new Drive.Builder(httpTransport, JSON_FACTORY, new HttpCredentialsAdapter(creds))
                .setApplicationName(APPLICATION_NAME)
                .build();
    }
}