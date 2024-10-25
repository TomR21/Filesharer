import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.model.File;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.security.GeneralSecurityException;
import java.io.FileOutputStream;


/* Class to download all files from Google Drive Folder. */
public class QuickDownload {

    /**
     * Downloads all files from Google Drive FilesharerStorage Folder.
     * @throws IOException If the file at filepath cannot be found
     * @throws GeneralSecurityException If the connection has issues during uploading to Google Drive
     */
    public static void main() throws IOException, GeneralSecurityException {
        // Set HTTP transport and build drive service to download files with
        Drive service = GoogleDrive.buildDrive();
        
        try {
            // Folder to download all documents to
            String downloadFolderPath = Settings.DOWNLOAD_FOLDER_PATH;
            List<File> files = new ArrayList<File>();

            String pageToken = null;
            String qString = String.format("parents in '%s'", Settings.DRIVE_FOLDER_ID);
            do {
            FileList result = service.files().list()
                .setQ(qString) // Searches for files in folder 
                .setSpaces("drive")
                .setFields("nextPageToken, files(id, name)")
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