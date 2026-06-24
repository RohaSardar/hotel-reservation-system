package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class DatabaseConnection {
    private static final String URL = "jdbc:sqlite:database/hotel.db";

    public static Connection getConnection() throws Exception {
        Class.forName("org.sqlite.JDBC");
        return DriverManager.getConnection(URL);
    }

    public static void initializeDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Create Users table
            stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "username TEXT UNIQUE," +
                    "password TEXT," +
                    "role TEXT," +
                    "name TEXT," +
                    "email TEXT," +
                    "phone TEXT," +
                    "address TEXT" +
                    ")");

            // Create Rooms table
            stmt.execute("CREATE TABLE IF NOT EXISTS rooms (" +
                    "room_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "room_number TEXT UNIQUE," +
                    "room_type TEXT," +
                    "price REAL," +
                    "status TEXT" +
                    ")");

            // Create Reservations table
            stmt.execute("CREATE TABLE IF NOT EXISTS reservations (" +
                    "reservation_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "customer_id INTEGER," +
                    "room_id INTEGER," +
                    "check_in TEXT," +
                    "check_out TEXT," +
                    "status TEXT," +
                    "FOREIGN KEY(customer_id) REFERENCES users(id)," +
                    "FOREIGN KEY(room_id) REFERENCES rooms(room_id)" +
                    ")");

            // Create Payments table
            stmt.execute("CREATE TABLE IF NOT EXISTS payments (" +
                    "payment_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "reservation_id INTEGER," +
                    "amount REAL," +
                    "payment_date TEXT," +
                    "FOREIGN KEY(reservation_id) REFERENCES reservations(reservation_id)" +
                    ")");

            // Insert default Admin if users table is empty
            String checkAdmin = "SELECT COUNT(*) FROM users WHERE role = 'ADMIN'";
            try (ResultSet rs = stmt.executeQuery(checkAdmin)) {
                if (rs.next() && rs.getInt(1) == 0) {
                    String insertAdmin = "INSERT INTO users (username, password, role, name, email, phone, address) VALUES (?, ?, ?, ?, ?, ?, ?)";
                    try (PreparedStatement pstmt = conn.prepareStatement(insertAdmin)) {
                        pstmt.setString(1, "admin");
                        pstmt.setString(2, "admin123");
                        pstmt.setString(3, "ADMIN");
                        pstmt.setString(4, "System Administrator");
                        pstmt.setString(5, "admin@hotel.com");
                        pstmt.setString(6, "03001234567");
                        pstmt.setString(7, "Hotel HQ Office");
                        pstmt.executeUpdate();
                        System.out.println("Default Admin account created (admin/admin123).");
                    }
                }
            }

            // Insert default Customer if users table is empty
            String checkCustomer = "SELECT COUNT(*) FROM users WHERE role = 'CUSTOMER'";
            try (ResultSet rs = stmt.executeQuery(checkCustomer)) {
                if (rs.next() && rs.getInt(1) == 0) {
                    String insertCustomer = "INSERT INTO users (username, password, role, name, email, phone, address) VALUES (?, ?, ?, ?, ?, ?, ?)";
                    try (PreparedStatement pstmt = conn.prepareStatement(insertCustomer)) {
                        pstmt.setString(1, "customer");
                        pstmt.setString(2, "customer123");
                        pstmt.setString(3, "CUSTOMER");
                        pstmt.setString(4, "Roha Sardar");
                        pstmt.setString(5, "roha@example.com");
                        pstmt.setString(6, "03129876543");
                        pstmt.setString(7, "Lahore, Pakistan");
                        pstmt.executeUpdate();
                        System.out.println("Default Customer account created (customer/customer123).");
                    }
                }
            }

            // Insert mock rooms if rooms table is empty
            String checkRooms = "SELECT COUNT(*) FROM rooms";
            try (ResultSet rs = stmt.executeQuery(checkRooms)) {
                if (rs.next() && rs.getInt(1) == 0) {
                    String insertRoom = "INSERT INTO rooms (room_number, room_type, price, status) VALUES (?, ?, ?, ?)";
                    try (PreparedStatement pstmt = conn.prepareStatement(insertRoom)) {
                        // Room 101
                        pstmt.setString(1, "101");
                        pstmt.setString(2, "Single");
                        pstmt.setDouble(3, 5000.0);
                        pstmt.setString(4, "Available");
                        pstmt.executeUpdate();

                        // Room 102
                        pstmt.setString(1, "102");
                        pstmt.setString(2, "Double");
                        pstmt.setDouble(3, 8000.0);
                        pstmt.setString(4, "Available");
                        pstmt.executeUpdate();

                        // Room 103
                        pstmt.setString(1, "103");
                        pstmt.setString(2, "Deluxe");
                        pstmt.setDouble(3, 12000.0);
                        pstmt.setString(4, "Available");
                        pstmt.executeUpdate();
                    }
                }
            }

        } catch (Exception e) {
            System.err.println("Error initializing database: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
