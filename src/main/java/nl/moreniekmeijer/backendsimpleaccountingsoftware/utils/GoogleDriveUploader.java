package nl.moreniekmeijer.backendsimpleaccountingsoftware.utils;

import com.google.api.client.http.FileContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.services.drive.model.FileList;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.client.json.jackson2.JacksonFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.Collections;

@Component
public class GoogleDriveUploader {

    @Value("${google.service.account.keyfile}")
    private String serviceAccountKeyPath;

    private static final String UPLOAD_FOLDER_ID = "1BfiNAanatzpaEW4_PWz7qncFNQ563tZm";

    public String uploadFile(MultipartFile file) throws IOException {
        GoogleCredentials credentials;
        try (InputStream is = new ClassPathResource(serviceAccountKeyPath).getInputStream()) {
            credentials = GoogleCredentials.fromStream(is)
                    .createScoped(Collections.singleton("https://www.googleapis.com/auth/drive"));
        }

        Drive driveService = new Drive.Builder(
                new NetHttpTransport(),
                JacksonFactory.getDefaultInstance(),
                new HttpCredentialsAdapter(credentials))
                .setApplicationName("Boekhouding Niek Meijer")
                .build();

        // ✅ Huidig jaar
        String currentYear = String.valueOf(LocalDate.now().getYear());

        // ✅ Zoek of map voor huidig jaar al bestaat
        String query = String.format(
                "'%s' in parents and name = '%s' and mimeType = 'application/vnd.google-apps.folder' and trashed = false",
                UPLOAD_FOLDER_ID, currentYear
        );

        FileList result = driveService.files().list()
                .setQ(query)
                .setFields("files(id, name)")
                .execute();

        String yearFolderId;

        if (result.getFiles().isEmpty()) {
            // ❌ Bestaat nog niet → aanmaken
            File yearFolderMetadata = new File();
            yearFolderMetadata.setName(currentYear);
            yearFolderMetadata.setMimeType("application/vnd.google-apps.folder");
            yearFolderMetadata.setParents(Collections.singletonList(UPLOAD_FOLDER_ID));

            File createdFolder = driveService.files().create(yearFolderMetadata)
                    .setFields("id")
                    .execute();
            yearFolderId = createdFolder.getId();
        } else {
            // ✅ Bestaat al
            yearFolderId = result.getFiles().get(0).getId();
        }

        // 📎 Bestand uploaden naar jaarmap
        File fileMetadata = new File();
        fileMetadata.setName(file.getOriginalFilename());
        fileMetadata.setParents(Collections.singletonList(yearFolderId));

        FileContent mediaContent = new FileContent(file.getContentType(), convertToTempFile(file));

        File uploaded = driveService.files().create(fileMetadata, mediaContent)
                .setFields("id, webViewLink")
                .execute();

        return uploaded.getWebViewLink();
    }

    private java.io.File convertToTempFile(MultipartFile multipart) throws IOException {
        java.nio.file.Path tempPath = java.nio.file.Files.createTempFile("upload-", "-" + multipart.getOriginalFilename());
        java.io.File tempFile = tempPath.toFile();
        multipart.transferTo(tempFile);

        return tempFile;
    }
}
