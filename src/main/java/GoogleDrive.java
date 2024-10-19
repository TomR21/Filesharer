import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.ClientParametersAuthentication;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.StoredCredential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.DataStore;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
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


    public static Credential loadCredential(HttpTransport httpTransport, JsonFactory jsonFactory, String userId) throws IOException {
        // Specify the path to the credential file
        File dataStoreDir = new File(Settings.TOKENS_FOLDER__PATH); // Directory where the credential is stored

        // Create the DataStoreFactory using the FileDataStoreFactory
        FileDataStoreFactory dataStoreFactory = new FileDataStoreFactory(dataStoreDir);
        DataStore<StoredCredential> dataStore = dataStoreFactory.getDataStore("StoredCredential");

        // Load the stored credential for the specific user (userId)
        StoredCredential storedCredential = dataStore.get(userId);

        if (storedCredential == null) {
            return null;
        }

        // Load client secrets from the credentials.json file
        FileReader credentialsReader = new FileReader(Settings.CREDENTIALS_FILE_PATH);
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(jsonFactory, credentialsReader);

        // Build the Credential object using the stored access token and refresh token
        Credential credential = new Credential.Builder(BearerToken.authorizationHeaderAccessMethod())
            .setTransport(httpTransport)
            .setJsonFactory(jsonFactory)
            .setTokenServerEncodedUrl("https://oauth2.googleapis.com/token")
            .setClientAuthentication(new ClientParametersAuthentication(
                clientSecrets.getDetails().getClientId(),
                clientSecrets.getDetails().getClientSecret()
            ))
            .build();

        // Set the loaded access token, refresh token, and expiration time
        credential.setAccessToken(storedCredential.getAccessToken());
        credential.setRefreshToken(storedCredential.getRefreshToken());
        credential.setExpirationTimeMilliseconds(storedCredential.getExpirationTimeMilliseconds());

        return credential;
    }


    /**
     * Creates an authorized Credential object.
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) 
        throws IOException {
        
            String userId = "user";

        // Load previous credential
        Credential prevCredential = loadCredential(HTTP_TRANSPORT, JSON_FACTORY, userId);

        // If previous credentials were found and not close to expiring, use these
        if (prevCredential != null && prevCredential.getExpiresInSeconds() > 60) {
            Logger.log("Used Previous Credentials with " + prevCredential.getExpiresInSeconds() + " seconds remaining");
            return prevCredential;
        }
        // Else obtain new credentials via authorization flow
        
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
        Credential credential = new AuthorizationCodeInstalledApp(flow, receiver).authorize(userId);

        // Returns an authorized Credential object.
        return credential;
    }

    public static Drive buildDrive() {
        try {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Drive service = new Drive.Builder(HTTP_TRANSPORT, GoogleDrive.JSON_FACTORY, GoogleDrive.getCredentials(HTTP_TRANSPORT))
            .setApplicationName(GoogleDrive.APPLICATION_NAME)
            .build();

        Logger.log("Service has been build");
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
