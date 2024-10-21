import java.security.GeneralSecurityException;
import java.util.Scanner;
import java.io.IOException;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;


/**
   * Class running the main program loop and all callable methods from the CLI
   */
public class main {
    
    /**
      * Checks if file exists at the given path
      * @param filepath String path to file including file itself
      * @return Boolean which returns the result
      */
    public static void checkFile( Scanner scanner ) {    
        // Get path from user
        System.out.println("Give a path");
        String filepath = scanner.nextLine();
        filepath = Tools.removeQuotes(filepath);  // Remove quotes to deal with copied windows paths
    
        if (Tools.doesFileExist(filepath)) {
            System.out.println("This file exists");
        } else {
            System.out.println("This file does not exist");
        }
} 

    /**
      * Method to add path to saved files list stored in savedFiles.txt
      * @param scanner Scanner object used in the main program loop to ask for path
      */                        
    public static void saveFile( Scanner scanner ) {
        // Get path from user
        System.out.println("Give path to a file");
        String filepath = scanner.nextLine();
        filepath = Tools.removeQuotes(filepath);  // Remove quotes to deal with copied windows paths

        // Check if files exists, if not return to regular program
        if (!(Tools.doesFileExist(filepath))) {
            System.out.println("File not found");
            return;
        }

        // Path of saved files text file
        String savefiles_path = Settings.SAVEFILES_TXT_PATH;

        // Try to append user filepath to current saved filepaths
        try (FileWriter myWriter = new FileWriter(savefiles_path, true)) {
            myWriter.write(filepath + "\n");
            myWriter.close();
            System.out.println("Added new file to saved files");
          } catch (IOException e) {
            System.out.println("An error occurred during saving.");
            e.printStackTrace();
          }
    } 

    /**
      * Method to remove a file path stored in filesSaved.txt
      */   
    public static void removeFile() {
        System.out.println("Currently saved files:");

        // Print out each file stored in filesSaved.txt with corresponding index
        int index = 0;
        try (Scanner reader = new Scanner(new FileReader(Settings.SAVEFILES_TXT_PATH))) {
            while (reader.hasNextLine()) {
                String row = reader.nextLine();
                System.out.println(String.valueOf(index) + ". " + row);
                index += 1;
            }
        } catch (Exception e) {
            System.out.println("Could not read save file, error: " + e.getMessage());
        }

        // Ask user to specify index to remove file 
        System.out.println("Which file do you want to remove? (Enter integer, enter any other character to go back)");
        Scanner remove_scanner = new Scanner(System.in);
        String answer = remove_scanner.nextLine();

        // Remove file if given a valid index
        if (Tools.isInteger(answer) && Integer.parseInt(answer) <= index) {
            try {
                Tools.removeLine(Integer.parseInt(answer));
            } catch (FileNotFoundException e) {
                System.out.println("FileNotFoundException: " + e.getMessage());
            } catch (IOException e) {
                System.out.println("IOException: " + e.getMessage());
            }
        }

        //remove_scanner.close();  // closes the application, fix later
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
    public static void deleteFiles() {
        try {
            QuickDelete.main();
        } catch (IOException e) {
            System.out.println("IOException: " + e);
        } catch (GeneralSecurityException e) {
            System.out.println("GeneralSecurityException: " + e);
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
                deleteFiles();
                uploadFiles();
                return;
            }
        }

         
        // Main program loop
        System.out.println("Hello there, what do you want to do?");
        while (true) {
            System.out.println("'check' to see if file exists, 'add' to add file to saved files \n" 
                + "'remove' to remove file from saved files, 'upload' to upload all stored files \n" 
                + "'download' to download all files, 'delete' to delete all drive files\n"
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

                case "add":
                    saveFile(scanner);
                    break;

                case "remove":
                    removeFile();
                    break;

                case "upload":
                    uploadFiles();
                    break;

                case "download":
                    downloadFiles();
                    break;

                case "delete":
                    deleteFiles();
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
