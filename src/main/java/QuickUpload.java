import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;

import java.io.FileReader;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.Scanner;


/* class to upload files from Google Drive folder */
public class QuickUpload {

    /**
      * Uploads files stored in filesSaved.txt to Google Drive FilesharerStorage Folder.
      * @throws IOException If the file at filepath cannot be found
      * @throws GeneralSecurityException If the connection has issues during uploading to Google Drive
      */
    public static void main() throws IOException, GeneralSecurityException {
        // Build a new authorized API client service.
        Drive service = GoogleDrive.buildDrive();

        // Scanner read line for line from filesSaved.txt
        try (Scanner scanner = new Scanner(new FileReader(Settings.SAVEFILES_TXT_PATH))) {
        
            // Read the file until all lines have been read
            while (scanner.hasNextLine()) {
                String filepath = scanner.nextLine(); // we read one line

                // Check if file is recognized by OS
                if (!(Tools.doesFileExist(filepath))) {
                    System.out.println("File at "+ filepath +" could not be found, wrong path or file does not exist");
                    continue;
                }

                // Create metadata for file to be uploaded
                File fileMetadata = new File();
                fileMetadata.setName(filepath);    // Set Google Drive File Name
                String driveFolderId = Settings.DRIVE_FOLDER_ID; // Google Drive Folder id
                fileMetadata.setParents(Collections.singletonList(driveFolderId)); // Set Google Drive storage place 

                // File's content.
                java.io.File filePath = new java.io.File(filepath);
                
                // Specify media type (null uses standard type) and file-path for file.
                FileContent mediaContent = new FileContent(null, filePath);
                File file = service.files().create(fileMetadata, mediaContent)
                  .setFields("id, parents")
                  .execute();

                System.out.println("Uploaded file ID: " + file.getId());
                Logger.log("Uploaded file ID: " + file.getId());
            }
                
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}