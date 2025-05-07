package nl.moreniekmeijer.backendsimpleaccountingsoftware.utils;

import com.google.api.client.http.FileContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.services.drive.model.FileList;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.client.json.jackson2.JacksonFactory;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;

@Component
public class GoogleDriveUploader {

    @Value("${google.service.account.keyfile}")
    private String serviceAccountKeyPath;

    @Value("${google.drive.root.folder}")
    private String rootFolderId;

    private Drive drive;

    @PostConstruct
    public void init() throws IOException {
        try (InputStream is = new ClassPathResource(serviceAccountKeyPath).getInputStream()) {
            GoogleCredentials credentials = GoogleCredentials.fromStream(is)
                    .createScoped(Collections.singleton("https://www.googleapis.com/auth/drive"));

            this.drive = new Drive.Builder(
                    new NetHttpTransport(),
                    JacksonFactory.getDefaultInstance(),
                    new HttpCredentialsAdapter(credentials))
                    .setApplicationName("Boekhouding Niek Meijer")
                    .build();
        }
    }

    public String uploadToYearFolder(MultipartFile file, int year) throws IOException {
        String folderId = getOrCreateYearFolder(String.valueOf(year));
        return uploadFileToFolder(file, folderId);
    }

    private String getOrCreateYearFolder(String year) throws IOException {
        String query = String.format(
                "'%s' in parents and name = '%s' and mimeType = 'application/vnd.google-apps.folder' and trashed = false",
                rootFolderId, year
        );

        FileList result = drive.files().list()
                .setQ(query)
                .setFields("files(id)")
                .execute();

        if (!result.getFiles().isEmpty()) {
            return result.getFiles().get(0).getId();
        }

        File folderMetadata = new File();
        folderMetadata.setName(year);
        folderMetadata.setMimeType("application/vnd.google-apps.folder");
        folderMetadata.setParents(Collections.singletonList(rootFolderId));

        File folder = drive.files().create(folderMetadata)
                .setFields("id")
                .execute();

        return folder.getId();
    }

    private String uploadFileToFolder(MultipartFile file, String folderId) throws IOException {
        File fileMetadata = new File();
        fileMetadata.setName(file.getOriginalFilename());
        fileMetadata.setParents(Collections.singletonList(folderId));

        java.io.File tempFile = convertToTempFile(file);
        FileContent mediaContent = new FileContent(file.getContentType(), tempFile);

        try {
            File uploaded = drive.files().create(fileMetadata, mediaContent)
                    .setFields("id, webViewLink")
                    .execute();

            return uploaded.getWebViewLink();
        } finally {
            Files.deleteIfExists(tempFile.toPath());
        }
    }

    private java.io.File convertToTempFile(MultipartFile multipart) throws IOException {
        Path tempPath = Files.createTempFile("upload-", "-" + multipart.getOriginalFilename());
        java.io.File tempFile = tempPath.toFile();
        multipart.transferTo(tempFile);
        return tempFile;
    }

    public void deleteFileFromFolder(String fileId) throws IOException {
        try {
            drive.files().delete(fileId).execute();
        } catch (IOException e) {
            throw new IOException("Fout bij het verwijderen van het bestand met ID: " + fileId, e);
        }
    }

    public void deleteFileById(String driveUrl) throws IOException {
        if (driveUrl != null && !driveUrl.isBlank()) {
            String fileId = extractFileIdFromDriveUrl(driveUrl);
            deleteFileFromFolder(fileId);
            System.out.println("Bestand succesvol verwijderd van Drive.");
        }
    }

    public String extractFileIdFromDriveUrl(String driveUrl) {
        String[] parts = driveUrl.split("/d/");
        if (parts.length > 1) {
            return parts[1].split("/")[0];
        }
        throw new IllegalArgumentException("Ongeldige Drive URL");
    }
}