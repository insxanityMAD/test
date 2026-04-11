package malinaoproject;

import java.sql.*;
import javax.swing.JOptionPane;

/**
 * Corrected DB_connect Class
 * @author Christian Dedil
 */
public class DB_connect {
    // FIX: Removed the extra space after the database name
    private static final String URL = "jdbc:mysql://localhost:3306/library_inventory_system";
    private static final String USER = "root";
    private static final String PASSWORD = "";
    
    public static java.sql.Connection getConnection() {
        java.sql.Connection cn = null;
        
        try {
            // Ensure the driver is loaded
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Get the connection directly from DriverManager
            cn = DriverManager.getConnection(URL, USER, PASSWORD);
            
        } catch (ClassNotFoundException | SQLException err) {
            JOptionPane.showMessageDialog(null, "Database Connection Error: " + err.getMessage());
        }
        return cn;
    }
}