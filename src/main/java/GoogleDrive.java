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
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

public class GoogleDrive {

    static final String APPLICATION_NAME = "Google Drive API Java Upload"; // Application name
    static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance(); // Global instance of JSON factory
    private static final java.io.File CREDENTIAL_TOKEN_FOLDER = new java.io.File(Settings.TOKENS_FOLDER__PATH);
  
    // Global instance of the scopes required by this quickstart. Saved tokens/ folder must be deleted when changing scopes
    private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE); 

    /**
     * Creates an authorized Credential object.
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) 
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

    public static Drive buildDrive() {
        try {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Drive service = new Drive.Builder(HTTP_TRANSPORT, GoogleDrive.JSON_FACTORY, GoogleDrive.getCredentials(HTTP_TRANSPORT))
            .setApplicationName(GoogleDrive.APPLICATION_NAME)
            .build();
        return service;
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
            return null;
        } catch (GeneralSecurityException e) {
            System.out.println("GeneralSecurityException: " + e.getMessage());
            return null;
        }
        
    }

}
