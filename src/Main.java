import database.DatabaseConnection;
import gui.LoginForm;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // Initialize the SQLite database and seed default values
        DatabaseConnection.initializeDatabase();

        // Launch the Login Form GUI on the Swing Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            new LoginForm().setVisible(true);
        });
    }
}
