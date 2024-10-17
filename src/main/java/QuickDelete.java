import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.model.File;

import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.util.Collections;
import java.util.List;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;


/* Class to demonstrate use-case of drive's download file. */
public class QuickDelete {

    private static final String APPLICATION_NAME = "Google Drive API Java Delete"; // Application name
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance(); // Global instance of JSON factory
    private static final java.io.File CREDENTIAL_TOKEN_FOLDER = new java.io.File(Settings.TOKENS_FOLDER__PATH);

    // Global instance of the scopes required by this quickstart. Saved tokens/ folder must be deleted when changing scopes
    private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE); 

    /**
    * Creates an authorized Credential object.
    * @param HTTP_TRANSPORT The network HTTP Transport
    * @return An authorized Credential object.
    * @throws IOException If the credentials.json file cannot be found.
    */
    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT)
      throws IOException {
        // Load client secrets
        InputStream in = new FileInputStream(Settings.CREDENTIALS_FILE_PATH);
        GoogleClientSecrets clientSecrets =
            GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
            HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
            .setDataStoreFactory(new FileDataStoreFactory(CREDENTIAL_TOKEN_FOLDER))
            .setAccessType("offline")
            .build();

        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        Credential credential = new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");

        // Returns an authorized Credential object
        return credential;
        }

    /**
     * Deletes all files from Google Drive FilesharerStorage Folder
     * @throws IOException If the file to be deleted cannot be found
     * @throws GeneralSecurityException If the connection has issues during deletion of files in Google Drive
     */
    public static void main() throws IOException, GeneralSecurityException {
        // Set HTTP transport and build drive service to download files with
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Drive service = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
            .setApplicationName(APPLICATION_NAME)
            .build();

        try {
            String pageToken = null;
            
            // Prevent deletion of files in main directory of Google Drive
            if (Settings.DRIVE_FOLDER_ID.equals("")) {
                System.out.println("Cannot remove files from Google Drive main directory");
                return;
            }
            String qString = String.format("parents in '%s'", Settings.DRIVE_FOLDER_ID);
            
            do {
            // Collect all files from Filesharer Folder
            FileList result = service.files().list()
                .setQ(qString) // Searches for files in folder 
                .setPageToken(pageToken)
                .execute();

            // Delete each file in the folder
            for (File file : result.getFiles()) {
                service.files().delete(file.getId()).execute();
                System.out.printf("Deleted file: %s (%s)\n",
                    file.getName(), file.getId());
                Logger.log("Deleted file: " + file.getName());
            }

            // Go to next page if there exists multiple pages
            pageToken = result.getNextPageToken();
            } while (pageToken != null);

        } catch (GoogleJsonResponseException e) {
            System.err.println("Google JSON Response Exception: " + e.getDetails());
            throw e;
        }
        
    }
}