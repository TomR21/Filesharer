import java.security.GeneralSecurityException;
import java.util.Scanner;
import java.io.IOException;

/**
 * Class running the main program loop and all callable methods from the CLI
 */
public class main {
    
    /**
     * Prints out whether file exists at path given by user
     * @param scanner Main system scanner that asks for path to a file
     */
    public static void checkFile( Scanner scanner ) {    
        // Get path from user
        System.out.println("Give a path");
        String filepath = scanner.nextLine();
        filepath = Tools.convertPath(filepath);  // Convert to UNIX path
    
        if (Tools.doesFileExist(filepath)) {
            System.out.println("This file exists");
        } else {
            System.out.println("This file does not exist");
        }
    } 

    /**  
     * Prints all the files saved in filesSaved.txt
     */
    public static void listSavedFiles() {
        SaveLog.printFilePaths();
    }

    /**
     * Method to add path to saved files list stored in savedFiles.txt
     * @param scanner Scanner object used in the main program loop to ask for path
     */                        
    public static void saveFile( Scanner scanner ) {
        SaveLog.addFile(scanner);
    }

    /**
     * Method to remove a file path stored in filesSaved.txt
     */   
    public static void removeFile( Scanner scanner ) {
        SaveLog.removeFile(scanner);
    }

    /**
     * Uploads all files whose path is saved in filesSaved.txt
     */
    public static void uploadFiles() {
        try {
            QuickUpload.main();
        } catch (IOException e) {
            System.out.println("IOException: " + e);
        } catch (GeneralSecurityException e) {
            System.out.println("GeneralSecurityException: " + e);
        }
    }

    /**
     * Downloads all files stored in the drive to downloads folder
     */
    public static void downloadFiles() {
        try {
            QuickDownload.main();
        } catch (IOException e) {
            System.out.println("IOException: " + e);
        } catch (GeneralSecurityException e) {
            System.out.println("GeneralSecurityException: " + e);
        }
    }

    /**
     * Deletes all files stored in the drive
     */
    public static void deleteFiles( Scanner scanner ) {
        System.out.println("This will delete ALL files in the Google Drive Folder (also non-saved), are you sure? "
            + "Enter 'DELETE' to continue");
        String answer = scanner.nextLine();
        if (answer.equals("DELETE")) {
            try {
                QuickDelete.main();
            } catch (IOException e) {
                System.out.println("IOException: " + e);
            } catch (GeneralSecurityException e) {
                System.out.println("GeneralSecurityException: " + e);
            }
        }
    }

    /**
     * Main program loop
     * @param arguments options are 'shutdown' and 'boot', which circumvent user interaction and upload / download files respectively 
     */
    public static void main( String args [] ) {
        // Arguments circumventing user interaction via CLI, only applied on boot and shutdown
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("boot")) {
                downloadFiles();
                return;
            } else if (args[i].equals("shutdown")) {
                uploadFiles();
                return;
            }
        }

         
        // Main program loop
        System.out.println("Hello there, what do you want to do?");
        while (true) {
            System.out.println("'check' to see if file exists, 'list' to list all currently saved files, 'add' to add file to saved files \n" 
                + "'remove' to remove file from saved files, 'upload' to upload all saved files \n" 
                + "'download' to download all saved files, 'delete' to delete ALL drive files\n"
                + "'quit' to quit program");

            // Read user input
            Scanner scanner = new Scanner(System.in);
            String answer = scanner.nextLine(); 
            
            // Checking the given command for each possible action
            switch (answer) 
            {
                case "check":
                    checkFile(scanner);
                    break;

                case "list":
                    listSavedFiles();
                    break;

                case "add":
                    saveFile(scanner);
                    break;

                case "remove":
                    removeFile(scanner);
                    break;

                case "upload":
                    uploadFiles();
                    break;

                case "download":
                    downloadFiles();
                    break;

                case "delete":
                    deleteFiles(scanner);
                    break;

                case "quit":
                    scanner.close();
                    Logger.log("Quitting Program");
                    return;

                default:
                    System.out.println("Unknown command, try again");
                    break;
                }
        }

    }

}
