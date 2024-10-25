import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import java.io.FileReader;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.Scanner;
import java.util.List;


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

                // Get name of the file with extension excluding its directory path 
                String fileName = filepath.substring(filepath.lastIndexOf("\\") + 1);
                
                // Create metadata for file to be uploaded
                File fileMetadata = new File();
                fileMetadata.setName(fileName);    // Set Google Drive File Name
                String driveFolderId = Settings.DRIVE_FOLDER_ID; // Google Drive Folder id
                fileMetadata.setParents(Collections.singletonList(driveFolderId)); // Set Google Drive storage place 

                // File's content.
                java.io.File filePath = new java.io.File(filepath);
                FileContent mediaContent = new FileContent(null, filePath); // null type uses default media type

                 
                try {
                    String pageToken = null;
                    
                    // Check if there exists a file in the Drive Folder with the same name
                    String qString = String.format("parents in '%s' and name = '%s'", Settings.DRIVE_FOLDER_ID, fileName);

                    // Collect results and replace/upload file
                    do {
                    FileList fileList = service.files().list()
                        .setQ(qString) 
                        .setPageToken(pageToken)
                        .execute();

                    List<File> newFileList = fileList.getFiles();

                    if (newFileList.size() == 0) {
                        // If no similar named file was found, do regular upload
                        System.out.printf("Uploading file: %s\n", fileName);
                        service.files().create(fileMetadata, mediaContent)
                        .setFields("id, parents")
                        .execute();

                    } else if (newFileList.size() == 1) {
                        // Update the file at existing file id with a new file with new mediacontent
                        String oldFileId = newFileList.get(0).getId(); 
                        File newfile = new File();

                        System.out.printf("Replacing file: %s (%s)\n", newFileList.get(0).getName(), oldFileId);
                        service.files().update(oldFileId, newfile, mediaContent).execute();
                        
                    } else {
                        // Two or more files have the same name, do not overwrite
                        System.out.println("Two or more files found with name: " + fileName);
                    }
        
                    // Go to next page if there exists multiple pages
                    pageToken = fileList.getNextPageToken();
                    } while (pageToken != null);
        
                } catch (GoogleJsonResponseException e) {
                    System.err.println("Google JSON Response Exception: " + e.getDetails());
                    throw e;
                }                
            }
                
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}