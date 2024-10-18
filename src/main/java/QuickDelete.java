import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.model.File;

import java.io.IOException;
import java.security.GeneralSecurityException;


/* Class to demonstrate use-case of drive's download file. */
public class QuickDelete {

    /**
     * Deletes all files from Google Drive FilesharerStorage Folder
     * @throws IOException If the file to be deleted cannot be found
     * @throws GeneralSecurityException If the connection has issues during deletion of files in Google Drive
     */
    public static void main() throws IOException, GeneralSecurityException {
        // Set HTTP transport and build drive service to download files with
        Drive service = GoogleDrive.buildDrive();

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