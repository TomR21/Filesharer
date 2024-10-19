import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;


/* class to upload files from Google Drive folder */
public class QuickUpload {

  /**
   * Uploads txt file to Google Drive FilesharerStorage Folder.
   * @param filepath absolute path to file which will be uploaded.
   * @throws IOException If the file at filepath cannot be found
   * @throws GeneralSecurityException If the connection has issues during uploading to Google Drive
   */
  public static void main(String filepath) throws IOException, GeneralSecurityException {
    // Build a new authorized API client service.
    Drive service = GoogleDrive.buildDrive();

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

    System.out.println("File ID: " + file.getId());
    Logger.log("Uploaded file ID: " + file.getId());
  }
}