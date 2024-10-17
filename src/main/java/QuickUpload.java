import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;


/* class to upload files from Google Drive folder */
public class QuickUpload {

  private static final String APPLICATION_NAME = "Google Drive API Java Upload"; // Application name
  private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance(); // Global instance of JSON factory
  private static final java.io.File CREDENTIAL_TOKEN_FOLDER = new java.io.File(Settings.TOKENS_FOLDER__PATH);

  // Global instance of the scopes required by this quickstart. Saved tokens/ folder must be deleted when changing scopes
  private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE); 

  /**
   * Creates an authorized Credential object.
   * @param HTTP_TRANSPORT The network HTTP Transport.
   * @return An authorized Credential object.
   * @throws IOException If the credentials.json file cannot be found.
   */
  private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT)
      throws IOException {
    // Load client secrets.
    InputStream in = new FileInputStream(Settings.CREDENTIALS_FILE_PATH);
    GoogleClientSecrets clientSecrets =
        GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

    // Build flow and trigger user authorization request.
    GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
        HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)        // new java.io.File(TOKENS_DIRECTORY_PATH)
        .setDataStoreFactory(new FileDataStoreFactory(CREDENTIAL_TOKEN_FOLDER))
        .setAccessType("offline")
        .build();

    LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
    Credential credential = new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");

    // Returns an authorized Credential object.
    return credential;
  }

  /**
   * Uploads txt file to Google Drive FilesharerStorage Folder.
   * @param filepath absolute path to file which will be uploaded.
   * @throws IOException If the file at filepath cannot be found
   * @throws GeneralSecurityException If the connection has issues during uploading to Google Drive
   */
  public static void main(String filepath) throws IOException, GeneralSecurityException {
    // Build a new authorized API client service.
    final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
    Drive service = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
        .setApplicationName(APPLICATION_NAME)
        .build();

    // Create metadata for file to be uploaded
    File fileMetadata = new File();
    fileMetadata.setName(filepath);    // Set Google Drive File Name
    String driveFolderId = Settings.DRIVE_FOLDER_ID; // Google Drive Folder id
    fileMetadata.setParents(Collections.singletonList(driveFolderId)); // Set Google Drive storage place 

    // File's content.
    java.io.File filePath = new java.io.File(filepath);
    
    // Specify media type and file-path for file.
    FileContent mediaContent = new FileContent("text/plain", filePath);
    File file = service.files().create(fileMetadata, mediaContent)
      .setFields("id, parents")
      .execute();

    System.out.println("File ID: " + file.getId());
    Logger.log("Uploaded file ID: " + file.getId());
  }
}