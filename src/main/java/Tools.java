import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


/* Class with useful methods to be used all throughout the program to check input and do other conversions. */
public class Tools {

    /**
     * Calculates if string input is an integer or not
     * @param input Any string 
     * @return Boolean which indicates whether the string is an integer or not
     */
    public static boolean isInteger( String input ) {
        try {
            Integer.parseInt( input );
            return true;
        }
        catch( NumberFormatException e ) {
            return false;
        }
    }

    /**
     * Checks if there is a file at given filepath
     * @param filepath Path to location in file directory 
     * @return Boolean which indicates whether there is a file at path
     */
    public static boolean doesFileExist( String filepath ) {
        // Create java File object from path
        java.io.File f = new java.io.File(filepath);

        // Check if file exists at that path
        if (f.isFile()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Converts window (copied) paths ("C:\..") to UNIX paths (C:/) for compatability
     * @param string Any string 
     * @return String path that is stripped of quotes and UNIX based
     */
    public static String convertPath( String string ) {
        string = string.replace("\"", ""); // Remove quotes from copying paths
        return string.replace("\\", "/"); // Change slash direction 
    }
    
    /**
     * Calculates if string input is an integer or not
     * @param lineIndex Integer which indicates which row in the text file must be removed 
     * @throws FileNotFoundException If the file cannot be found at the path
     * @throws IOException If the are insufficient permissions to write, rename or delete the file
     */
    public static void removeLine( int lineIndex ) throws FileNotFoundException, IOException{
        File inputFile = new File(Settings.SAVEFILES_TXT_PATH);
        
        // Create temporary file in the same directory
        String tempfile_path = Settings.SAVEFILES_TXT_PATH.substring(0, Settings.SAVEFILES_TXT_PATH.lastIndexOf("/")) + "/temp.txt";
        File tempFile = new File(tempfile_path);

        BufferedReader reader = new BufferedReader(new FileReader(inputFile));
        BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

        String currentLine;

        // Go over each line and skip writing if index is equal to given lineIndex
        int index = 0;
        while((currentLine = reader.readLine()) != null) {
            if (index == lineIndex) {
                index += 1;
                continue;
            }
            writer.write(currentLine + System.getProperty("line.separator"));
            index += 1;
        }
        writer.close(); 
        reader.close(); 

        // Delete previous file and rename temp to old file name
        inputFile.delete();
        boolean successful = tempFile.renameTo(inputFile);
        System.out.println("Rewrite: " + successful);

    }
}
