import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
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

        // Loop over each filepath stored in filesSaved.txt 
        List<String> filePathList = SaveLog.getFilePaths();

        for (String filePath : filePathList) {

            // Check if file is recognized by OS
            if (!(Tools.doesFileExist(filePath))) {
                System.out.println("File at "+ filePath +" could not be found, wrong path or file does not exist");
                continue;
            }

            // Get name of the file with extension excluding its directory path 
            String fileName = filePath.substring(filePath.lastIndexOf("\\") + 1);
            
            // Create metadata for file to be uploaded
            File fileMetadata = new File();
            fileMetadata.setName(fileName);    // Set Google Drive File Name
            String driveFolderId = Settings.DRIVE_FOLDER_ID; // Google Drive Folder id
            fileMetadata.setParents(Collections.singletonList(driveFolderId)); // Set Google Drive storage place 

            // File's content.
            java.io.File saveFile = new java.io.File(filePath);
            FileContent mediaContent = new FileContent(null, saveFile); // null type uses default media type
                
            // Catches Error status codes in HTTP response error due to bad JSON requests, 
            try {
                String pageToken = null;
                
                // Query to check if there exists a file in the Drive Folder with the same name
                String qString = String.format("parents in '%s' and name = '%s'", Settings.DRIVE_FOLDER_ID, fileName);

                // Collect results and replace/upload file
                do {
                FileList foundFiles = service.files().list()
                    .setQ(qString) 
                    .setPageToken(pageToken)
                    .execute();

                List<File> foundFilesList = foundFiles.getFiles();

                if (foundFilesList.size() == 0) {
                    // If no similar named file was found, do regular upload
                    System.out.printf("Uploading file: %s\n", fileName);
                    service.files().create(fileMetadata, mediaContent)
                    .setFields("id, parents")
                    .execute();

                } else if (foundFilesList.size() == 1) {
                    // Update the file at existing file id with a new file with new mediacontent
                    String oldFileId = foundFilesList.get(0).getId(); 
                    File newfile = new File();

                    System.out.printf("Replacing file: %s (%s)\n", foundFilesList.get(0).getName(), oldFileId);
                    service.files().update(oldFileId, newfile, mediaContent).execute();
                    
                } else {
                    // Two or more files have the same name, do not overwrite
                    System.out.println("Two or more files found with name: " + fileName);
                }
    
                // Go to next page if there exists multiple pages
                pageToken = foundFiles.getNextPageToken();
                } while (pageToken != null);
    
            } catch (GoogleJsonResponseException e) {
                System.err.println("Google JSON Response Exception: " + e.getDetails());
                throw e;
            }                
        }

    }
}