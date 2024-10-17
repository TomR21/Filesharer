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
import java.io.OutputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.io.FileOutputStream;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;


/* Class to download all files from Google Drive Folder. */
public class QuickDownload {
    private static final String APPLICATION_NAME = "Google Drive API Java Quickstart"; // Application name
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance(); // Global instance of JSON factory
    private static final String TOKENS_DIRECTORY_PATH = "tokens\\"; // Directory to store authorization tokens for application

    // Global instance of the scopes required by this quickstart. If modifying these scopes, delete your previously saved tokens/ folder.
    private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE);
    private static final String CREDENTIALS_FILE_PATH = "credentials.json";

    /**
    * Creates an authorized Credential object.
    * @param HTTP_TRANSPORT The network HTTP Transport
    * @return An authorized Credential object.
    * @throws IOException If the credentials.json file cannot be found.
    */
    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT)
      throws IOException {
        // Load client secrets
        InputStream in = QuickDownload.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
        throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets =
            GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
            HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
            .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
            .setAccessType("offline")
            .build();

        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        Credential credential = new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
 
        //returns an authorized Credential object
        return credential;
    }

    /**
     * Downloads all files from Google Drive FilesharerStorage Folder.
     * @throws IOException If the file at filepath cannot be found
     * @throws GeneralSecurityException If the connection has issues during uploading to Google Drive
     */
    public static void main() throws IOException, GeneralSecurityException {
        // Set HTTP transport and build drive service to download files with
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Drive service = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
            .setApplicationName(APPLICATION_NAME)
            .build();
        
        try {
            // Folder to download all documents to
            String downloadFolderPath = Settings.DOWNLOAD_FOLDER_PATH;
            List<File> files = new ArrayList<File>();

            String pageToken = null;
            do {
            FileList result = service.files().list()
                .setQ("parents in '1XwTpo-p6YyDmgDOXu0OlGHUDfIUMdHxD'") // Searches for files in folder 
                //.setSpaces("drive")
                //.setFields("nextPageToken, items(id, title)")
                .setPageToken(pageToken)
                .execute();

            for (File file : result.getFiles()) {
                String downloadFilePathName = file.getName();
                String downloadFileName = downloadFilePathName.substring(downloadFilePathName.lastIndexOf("\\") + 1);
    
                // Create path to download folder location with file name
                String downloadFolderLoc = downloadFolderPath + downloadFileName;
    
                // Create Outputstream to save file locally
                OutputStream outputStream = new FileOutputStream(downloadFolderLoc);
                
                try {
                    service.files().get(file.getId()).executeMediaAndDownloadTo(outputStream);
                    System.out.printf("Downloaded file: %s (%s)\n",
                        file.getName(), file.getId());
                    Logger.log("Downloaded File:" + file.getName());
                } catch(IOException e) {
                    System.out.println("Could not download " + downloadFilePathName);
                }
                outputStream.flush();
                outputStream.close();
            }

            files.addAll(result.getFiles());

            pageToken = result.getNextPageToken();
            } while (pageToken != null);

            return;

        } catch (GoogleJsonResponseException e) {
            System.err.println("Unable to move file: " + e.getDetails());
            throw e;
        }
    }
}