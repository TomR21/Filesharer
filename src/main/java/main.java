import java.security.GeneralSecurityException;
import java.util.Scanner;
import java.io.IOException;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;


/**
   * Class running the main program loop
   */
public class main {
    
    /**
      * Checks if file exists at the given path
      * @param filepath String path to file including file itself
      * @return Boolean which returns the result
      */
    public static void checkFile(Scanner scanner) {    
        // Get path from user
        System.out.println("Give a path");
        String filepath = scanner.nextLine();
        filepath = Tools.removeQuotes(filepath);
    
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
    public static void saveFile(Scanner scanner) {
        System.out.println("Give path to a file");
        String filepath = scanner.nextLine();
        filepath = Tools.removeQuotes(filepath);

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
      * Method to remove a file path stored in savedFiles.txt
      */   
    public static void removeFile() {
        System.out.println("Currently saved files:");

        // Print out each file stored in savedFiles.txt with corresponding index
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

        //remove_scanner.close();
    }

    /**
      * Uploads all files whose path is saved in filesSaved.txt
      */
    public static void uploadFiles() {
        String savefile_path = Settings.SAVEFILES_TXT_PATH;

        // Scanner read line for line from filesSaved.txt
        try (Scanner scanner = new Scanner(new FileReader(savefile_path))) {
    
            // Read the file until all lines have been read
            while (scanner.hasNextLine()) {
                String row = scanner.nextLine(); // we read one line

                // Check if file is recognized by OS
                if (!(Tools.doesFileExist(row))) {
                    System.out.println("File at "+ row +" could not be found, wrong path or file does not exist");
                    continue;
                }

                // Try to upload file using Drive API
                try {
                    QuickUpload.main(row);
                    System.out.println(row + " has been uploaded");     // we print the line that we read
                } catch (IOException e) {
                    System.out.println("IOException: " + e);
                } catch (GeneralSecurityException e) {
                    System.out.println("GeneralSecurityException: " + e);
                }
            }
            
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
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
    public static void main(String args []) {
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
            System.out.println("'check' to see if file exists, 'upload' to upload all stored files, " 
                + "'add' to add file to saved files, 'download' to download all files, 'delete' to delete all drive files");

            // Read user input
            Scanner scanner = new Scanner(System.in);
            String answer = scanner.nextLine(); 
            
            // Checking the given command for each possible action
            if (answer.equals("check")) {
                checkFile(scanner);
            } else if (answer.equals("add")) {
                saveFile(scanner);
            } else if (answer.equals("delete")) {
                deleteFiles();
            } else if (answer.equals("remove")) {
                removeFile();
            } else if (answer.equals("upload")) {
                uploadFiles();
            } else if (answer.equals("download")) {
                downloadFiles();
            } else if (answer.equals("quit")) {
                scanner.close();
                return;
            } else {
                System.out.println("unknown command, try again");
            }
        }

    }

}
