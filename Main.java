import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {
        DataStore.seedIfNeeded();
        SwingUtilities.invokeLater(() -> new LoginPage());
    }
}
