import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/* Contains all methods related to the filesSaved.txt file */
public class SaveLog {

    private static String saveFilePath = Settings.SAVEFILES_TXT_PATH;

    /**
     * Asks user for filepath. If filepath links to a file it is added to filesSaved.txt
     */
    public static void addFile( Scanner scanner ) {
        System.out.println("Enter path to file that needs to be saved:");

        String filePath = scanner.nextLine();
        filePath = Tools.removeQuotes(filePath);  // Remove quotes to deal with copied windows paths

        // Check if files exists, if not return to regular program
        if (!(Tools.doesFileExist(filePath))) {
            System.out.println("File not found");
            return;
        }

        // Try to append user filepath to current saved filepaths
        try (FileWriter myWriter = new FileWriter(saveFilePath, true)) {
            myWriter.write(filePath + "\n");
            myWriter.close();
            System.out.println("Added new file to saved files");
          } catch (IOException e) {
            System.out.println("An error occurred during saving.");
            e.printStackTrace();
          }
    }

    /**
     * Prints out all saved filepaths and removes filepath at given user index
     */
    public static void removeFile( Scanner scanner ) {
        System.out.println("Currently saved files:");
        
        // Print all filePaths and obtain index of last file 
        int index = printFilePaths();

        // Ask user to specify index to remove file 
        System.out.println("Which file do you want to remove? (Enter integer, enter any other character to go back)");
        //Scanner remove_scanner = new Scanner(System.in);
        String answer = scanner.nextLine();

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
    } 

    /**
     * Print all filepaths present in filesSaved.txt 
     * @return Integer index of final row of the file, number of saved files equals index + 1 
     */
    public static Integer printFilePaths() {
        List<String> filePathList = getFilePaths();

        // Print out each file stored in filesSaved.txt with corresponding index
        int index = 0;
        for (String filePath : filePathList) {
            String fileName = filePath.substring(filePath.lastIndexOf("\\") + 1);
            System.out.println(String.valueOf(index) + ". " + fileName + " (Path: " + filePath + ")");
            index += 1;
        }

        return index;
    }

    /**
     * Puts all filepaths from the filesSaved.txt file into a List
     * @return List<String> containing each path to a saved file 
     */
    public static List<String> getFilePaths() {
        List<String> filePathList = new ArrayList<String>();

        // Print out each file stored in filesSaved.txt with corresponding index
        try (Scanner reader = new Scanner(new FileReader(saveFilePath))) {
            while (reader.hasNextLine()) {
                String row = reader.nextLine();
                filePathList.add(row);
            }
        } catch (Exception e) {
            System.out.println("Could not read save file, error: " + e.getMessage());
        }

        return filePathList;
    }

    /**
      * Puts all the file names (excluding parent directories) from the filesSaved.txt file into a List
      * @return List<String> containing each name including extension of a saved file 
      */
    public static List<String> getFileNames() {
        List<String> fileNameList = new ArrayList<String>();

        // Print out each file stored in filesSaved.txt with corresponding index
        try (Scanner reader = new Scanner(new FileReader(saveFilePath))) {
            while (reader.hasNextLine()) {
                String row = reader.nextLine();
                row = row.substring(row.lastIndexOf("\\") + 1);
                fileNameList.add(row);
            }
        } catch (Exception e) {
            System.out.println("Could not read save file, error: " + e.getMessage());
        }

        return fileNameList;
    }
}
