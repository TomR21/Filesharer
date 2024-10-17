import java.util.Scanner;

public class MainScanner {
    public static MainScanner instance = new MainScanner();
    
    private MainScanner() {
    }

    public static MainScanner getInstance() {
        return instance;
    }

    public String readInput() {
        Scanner scanner = new Scanner(System.in);
        String user_input = scanner.nextLine();
        scanner.close();
        return user_input;
    }
}
