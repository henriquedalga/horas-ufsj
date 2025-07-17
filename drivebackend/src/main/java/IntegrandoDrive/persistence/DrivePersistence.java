package IntegrandoDrive.persistence;

import java.util.List;
import java.util.stream.Collectors;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.Permission;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.auth.http.HttpCredentialsAdapter;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.Objects;

public class DrivePersistence {
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String APPLICATION_NAME = "Drive Submission App";

    private final Drive driveService;
    private String defaultParentFolderId;

    public DrivePersistence(Drive driveService, String defaultParentFolderId) {
        this.driveService = driveService;
        this.defaultParentFolderId = defaultParentFolderId;
    }
    /**
     * Inicializa o DriveService a partir de um arquivo JSON de credenciais.
     */   
    public static Drive initDriveService(String credentialsPath)
            throws IOException, GeneralSecurityException {
        InputStream in = DrivePersistence.class.getResourceAsStream(credentialsPath);
        GoogleCredentials creds = GoogleCredentials.fromStream(in)
            .createScoped(Collections.singletonList("https://www.googleapis.com/auth/drive"));

        HttpTransport transport = GoogleNetHttpTransport.newTrustedTransport();
        return new Drive.Builder(transport, JSON_FACTORY, new HttpCredentialsAdapter(creds))
            .setApplicationName(APPLICATION_NAME)
            .build();
    }

    /**
     * Define ou atualiza a pasta pai padrão para operações.
     */
    public void setDefaultParentFolderId(String defaultParentFolderId) {
        this.defaultParentFolderId = defaultParentFolderId;
    }

    public String getDefaultParentFolderId() {
        return defaultParentFolderId;
    }

    public String createFolderIfNotExists(String folderName, String parentFolderId) throws IOException {
        String query = "mimeType = 'application/vnd.google-apps.folder' and name = '" + folderName +
                       "' and '" + parentFolderId + "' in parents";
        FileList result = driveService.files().list()
            .setQ(query)
            .setFields("files(id, name)")
            .execute();

        if (!result.getFiles().isEmpty()) {
            return result.getFiles().get(0).getId();
        }

        File metadata = new File()
            .setName(folderName)
            .setMimeType("application/vnd.google-apps.folder")
            .setParents(Collections.singletonList(parentFolderId));
        File created = driveService.files()
            .create(metadata)
            .setFields("id")
            .execute();
        return created.getId();
    }

    public String getFolderLink(String folderId) throws IOException {
        File f = driveService.files()
            .get(folderId)
            .setFields("webViewLink")
            .execute();
        return f.getWebViewLink();
    }

    public String getFileLink(String fileId) throws IOException {
        File f = driveService.files()
            .get(fileId)
            .setFields("webViewLink")
            .execute();
        return f.getWebViewLink();
    }

    public String uploadFile(java.io.File filePath, String folderId) throws IOException {
        File fileMeta = new File()
            .setName(filePath.getName())
            .setParents(Collections.singletonList(folderId));
        FileContent media = new FileContent("application/octet-stream", filePath);

        File uploaded = driveService.files()
            .create(fileMeta, media)
            .setFields("id")
            .execute();
        return uploaded.getId();
    }

    public List<String> listFileLinks(String folderId) throws IOException {
        String q = String.format("'%s' in parents and trashed = false", folderId);
        // já traz id e webViewLink em uma única chamada
        FileList list = driveService.files()
            .list()
            .setQ(q)
            .setFields("files(id, webViewLink)")
            .execute();

        if (list.getFiles() == null) {
            return Collections.emptyList();
        }
        return list.getFiles().stream()
                .map(f -> f.getWebViewLink())
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public void deleteFile(String fileId) throws IOException {
        driveService.files().delete(fileId).execute();
    }

    public void setFolderReadOnly(String folderId) throws IOException {
        Permission p = new Permission()
            .setType("anyone")
            .setRole("reader");
        driveService.permissions()
            .create(folderId, p)
            .setFields("id")
            .execute();
    }

    public void setFolderWritable(String folderId) throws IOException {
        List<Permission> perms = driveService.permissions()
            .list(folderId)
            .setFields("permissions(id,type,role)")
            .execute()
            .getPermissions();

        for (Permission p : perms) {
            if ("anyone".equalsIgnoreCase(p.getType())) {
                driveService.permissions()
                    .delete(folderId, p.getId())
                    .execute();
            }
        }

        Permission writer = new Permission()
            .setType("anyone")
            .setRole("writer");
        driveService.permissions()
            .create(folderId, writer)
            .setFields("id")
            .execute();
    }
    public boolean isFolderReadOnly(String folderId) throws IOException {
        List<Permission> perms = driveService.permissions()
            .list(folderId)
            .setFields("permissions(id,type,role)")
            .execute()
            .getPermissions();

        for (Permission p : perms) {
            if ("anyone".equalsIgnoreCase(p.getType()) && "reader".equalsIgnoreCase(p.getRole())) {
                return true;
            }
        }
        return false;
    }
    public static Drive initDriveService() throws IOException, GeneralSecurityException {
        InputStream in = DrivePersistence.class.getResourceAsStream("/credentials.json");
        GoogleCredentials creds = GoogleCredentials.fromStream(in)
            .createScoped(Collections.singletonList("https://www.googleapis.com/auth/drive"));

        HttpTransport transport = GoogleNetHttpTransport.newTrustedTransport();
        return new Drive.Builder(transport, JSON_FACTORY, new HttpCredentialsAdapter(creds))
            .setApplicationName(APPLICATION_NAME)
            .build();
    }
}