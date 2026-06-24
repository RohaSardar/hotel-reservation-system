package gui;

import database.DatabaseConnection;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class CustomerDashboard extends JFrame {

    private int customerId;
    private String customerName;

    private JButton btnBrowseRooms;
    private JButton btnMyReservations;
    private JButton btnProfile;
    private JButton btnLogout;

    public CustomerDashboard(int customerId, String customerName) {
        this.customerId = customerId;
        this.customerName = customerName;

        setTitle("Customer Dashboard - " + customerName);
        setSize(600, 500);
        setLayout(null);
        getContentPane().setBackground(new Color(240, 248, 255));

        JLabel title = new JLabel("CUSTOMER DASHBOARD");
        title.setBounds(100, 30, 400, 40);
        title.setFont(new Font("Verdana", Font.BOLD, 22));
        title.setForeground(new Color(0, 51, 153));
        title.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel welcome = new JLabel("Welcome, " + customerName + "!");
        welcome.setBounds(100, 80, 400, 25);
        welcome.setFont(new Font("Arial", Font.ITALIC, 14));
        welcome.setHorizontalAlignment(SwingConstants.CENTER);

        btnBrowseRooms = new JButton("Browse & Book Rooms");
        btnBrowseRooms.setBounds(180, 130, 220, 40);
        btnBrowseRooms.setBackground(new Color(0, 102, 204));
        btnBrowseRooms.setForeground(Color.WHITE);
        btnBrowseRooms.setFont(new Font("Arial", Font.BOLD, 14));

        btnMyReservations = new JButton("My Reservations");
        btnMyReservations.setBounds(180, 195, 220, 40);
        btnMyReservations.setBackground(new Color(255, 153, 0));
        btnMyReservations.setForeground(Color.WHITE);
        btnMyReservations.setFont(new Font("Arial", Font.BOLD, 14));

        btnProfile = new JButton("My Profile");
        btnProfile.setBounds(180, 260, 220, 40);
        btnProfile.setBackground(new Color(102, 0, 204));
        btnProfile.setForeground(Color.WHITE);
        btnProfile.setFont(new Font("Arial", Font.BOLD, 14));

        btnLogout = new JButton("Logout");
        btnLogout.setBounds(180, 325, 220, 40);
        btnLogout.setBackground(new Color(204, 0, 0));
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFont(new Font("Arial", Font.BOLD, 14));

        add(title);
        add(welcome);
        add(btnBrowseRooms);
        add(btnMyReservations);
        add(btnProfile);
        add(btnLogout);

        // Action listeners
        btnBrowseRooms.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new RoomBrowsing(customerId, customerName).setVisible(true);
                dispose();
            }
        });

        btnMyReservations.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showMyReservations();
            }
        });

        btnProfile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showProfileDialog();
            }
        });

        btnLogout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new LoginForm().setVisible(true);
                dispose();
            }
        });

        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void showMyReservations() {
        JFrame frame = new JFrame("My Reservations");
        frame.setSize(700, 400);
        frame.setLayout(new BorderLayout());
        
        String[] columns = {"Res ID", "Room No", "Room Type", "Price/Night", "Check In", "Check Out", "Status"};
        java.util.List<String[]> dataList = new java.util.ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT r.reservation_id, rm.room_number, rm.room_type, rm.price, r.check_in, r.check_out, r.status " +
                     "FROM reservations r " +
                     "JOIN rooms rm ON r.room_id = rm.room_id " +
                     "WHERE r.customer_id = ?")) {
            
            pstmt.setInt(1, customerId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    dataList.add(new String[]{
                            String.valueOf(rs.getInt("reservation_id")),
                            rs.getString("room_number"),
                            rs.getString("room_type"),
                            String.valueOf(rs.getDouble("price")),
                            rs.getString("check_in"),
                            rs.getString("check_out"),
                            rs.getString("status")
                    });
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error fetching reservations: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

        String[][] data = dataList.toArray(new String[0][]);
        JTable table = new JTable(new javax.swing.table.DefaultTableModel(data, columns));
        JScrollPane scrollPane = new JScrollPane(table);
        frame.add(scrollPane, BorderLayout.CENTER);
        
        frame.setLocationRelativeTo(this);
        frame.setVisible(true);
    }

    private void showProfileDialog() {
        JTextField txtEmail = new JTextField(20);
        JTextField txtPhone = new JTextField(20);
        JTextField txtAddress = new JTextField(20);

        // Fetch current details
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT email, phone, address FROM users WHERE id = ?")) {
            pstmt.setInt(1, customerId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    txtEmail.setText(rs.getString("email"));
                    txtPhone.setText(rs.getString("phone"));
                    txtAddress.setText(rs.getString("address"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
        panel.add(new JLabel("Email:"));
        panel.add(txtEmail);
        panel.add(new JLabel("Phone:"));
        panel.add(txtPhone);
        panel.add(new JLabel("Address:"));
        panel.add(txtAddress);

        int result = JOptionPane.showConfirmDialog(this, panel, "My Profile", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            String email = txtEmail.getText().trim();
            String phone = txtPhone.getText().trim();
            String address = txtAddress.getText().trim();

            if (email.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Email is mandatory.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement("UPDATE users SET email = ?, phone = ?, address = ? WHERE id = ?")) {
                pstmt.setString(1, email);
                pstmt.setString(2, phone);
                pstmt.setString(3, address);
                pstmt.setInt(4, customerId);

                int updated = pstmt.executeUpdate();
                if (updated > 0) {
                    JOptionPane.showMessageDialog(this, "Profile updated successfully!");
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
