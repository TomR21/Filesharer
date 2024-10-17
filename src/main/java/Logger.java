import java.io.IOException;

public class Logger {
    static void log(String write_string) {
        // Path of saved files text file
        String logtext_path = "C:\\Users\\tomva\\VSCode Projects\\Filesharer\\operation_log.txt";

        // Try catch will ensure writer is closed if error occurs
        try (java.io.FileWriter myWriter = new java.io.FileWriter(logtext_path, true)) {
            myWriter.write(write_string + "\n");
            myWriter.close();
        } catch (IOException e) {
            System.out.println("An error occurred during logging.");
            e.printStackTrace();
        }
    }
}
