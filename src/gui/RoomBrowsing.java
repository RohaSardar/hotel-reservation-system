package gui;

import database.DatabaseConnection;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RoomBrowsing extends JFrame {

    private int customerId;
    private String customerName;

    private JTable roomTable;
    private DefaultTableModel tableModel;
    private JTextField txtCheckIn, txtCheckOut, txtSearch;
    private JButton btnBook, btnBack, btnSearch;

    public RoomBrowsing(int customerId, String customerName) {
        this.customerId = customerId;
        this.customerName = customerName;

        setTitle("Browse Rooms - " + customerName);
        setSize(850, 520);
        setLayout(null);
        getContentPane().setBackground(new Color(240, 248, 255));

        JLabel title = new JLabel("AVAILABLE ROOMS");
        title.setBounds(280, 15, 300, 40);
        title.setFont(new Font("Verdana", Font.BOLD, 22));
        title.setForeground(new Color(0, 102, 204));
        title.setHorizontalAlignment(SwingConstants.CENTER);

        // Search panel
        JLabel lblSearch = new JLabel("Search by Type:");
        lblSearch.setBounds(50, 75, 120, 25);
        lblSearch.setFont(new Font("Arial", Font.BOLD, 12));

        txtSearch = new JTextField();
        txtSearch.setBounds(160, 75, 150, 25);

        btnSearch = new JButton("Search");
        btnSearch.setBounds(320, 75, 90, 25);
        btnSearch.setBackground(new Color(0, 102, 204));
        btnSearch.setForeground(Color.WHITE);

        // Table
        String[] columns = {"Room ID", "Room Number", "Room Type", "Price", "Status"};
        tableModel = new DefaultTableModel(columns, 0);
        roomTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(roomTable);
        scrollPane.setBounds(50, 120, 730, 220);

        // Date input fields for booking
        JLabel lblCheckIn = new JLabel("Check-In Date *");
        lblCheckIn.setBounds(50, 360, 110, 25);
        
        txtCheckIn = new JTextField("YYYY-MM-DD");
        txtCheckIn.setBounds(160, 360, 120, 25);

        JLabel lblCheckOut = new JLabel("Check-Out Date *");
        lblCheckOut.setBounds(310, 360, 110, 25);
        
        txtCheckOut = new JTextField("YYYY-MM-DD");
        txtCheckOut.setBounds(430, 360, 120, 25);

        btnBook = new JButton("Book Selected Room");
        btnBook.setBounds(180, 415, 180, 40);
        btnBook.setBackground(new Color(0, 153, 76));
        btnBook.setForeground(Color.WHITE);
        btnBook.setFont(new Font("Arial", Font.BOLD, 14));

        btnBack = new JButton("Back to Dashboard");
        btnBack.setBounds(400, 415, 180, 40);
        btnBack.setBackground(new Color(204, 0, 0));
        btnBack.setForeground(Color.WHITE);
        btnBack.setFont(new Font("Arial", Font.BOLD, 14));

        add(title);
        add(lblSearch); add(txtSearch); add(btnSearch);
        add(scrollPane);
        add(lblCheckIn); add(txtCheckIn);
        add(lblCheckOut); add(txtCheckOut);
        add(btnBook);
        add(btnBack);

        // Action Listeners
        btnSearch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performSearch();
            }
        });

        btnBook.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                bookRoom();
            }
        });

        btnBack.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new CustomerDashboard(customerId, customerName).setVisible(true);
                dispose();
            }
        });

        refreshTable();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM rooms WHERE status = 'Available'")) {
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("room_id"),
                        rs.getString("room_number"),
                        rs.getString("room_type"),
                        rs.getDouble("price"),
                        rs.getString("status")
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading rooms: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void performSearch() {
        String type = txtSearch.getText().trim();
        if (type.isEmpty()) {
            refreshTable();
            return;
        }

        tableModel.setRowCount(0);
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM rooms WHERE status = 'Available' AND room_type LIKE ?")) {
            pstmt.setString(1, "%" + type + "%");
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    tableModel.addRow(new Object[]{
                            rs.getInt("room_id"),
                            rs.getString("room_number"),
                            rs.getString("room_type"),
                            rs.getDouble("price"),
                            rs.getString("status")
                    });
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Search error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void bookRoom() {
        int selectedRow = roomTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a room from the table to book.", "Selection Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int roomId = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());
        String roomNumber = tableModel.getValueAt(selectedRow, 1).toString();
        String checkIn = txtCheckIn.getText().trim();
        String checkOut = txtCheckOut.getText().trim();

        if (checkIn.isEmpty() || checkOut.isEmpty() || "YYYY-MM-DD".equals(checkIn) || "YYYY-MM-DD".equals(checkOut)) {
            JOptionPane.showMessageDialog(this, "Please enter check-in and check-out dates.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!checkIn.matches("\\d{4}-\\d{2}-\\d{2}") || !checkOut.matches("\\d{4}-\\d{2}-\\d{2}")) {
            JOptionPane.showMessageDialog(this, "Dates must be in YYYY-MM-DD format.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // Double check if room is still available
                try (PreparedStatement pstmt = conn.prepareStatement("SELECT status FROM rooms WHERE room_id = ? FOR UPDATE")) {
                    pstmt.setInt(1, roomId);
                    try (ResultSet rs = pstmt.executeQuery()) {
                        if (rs.next() && !"Available".equalsIgnoreCase(rs.getString("status"))) {
                            JOptionPane.showMessageDialog(this, "Sorry, this room has just been booked by another user.", "Not Available", JOptionPane.WARNING_MESSAGE);
                            conn.rollback();
                            refreshTable();
                            return;
                        }
                    }
                }

                // Insert reservation
                try (PreparedStatement pstmt = conn.prepareStatement(
                        "INSERT INTO reservations (customer_id, room_id, check_in, check_out, status) VALUES (?, ?, ?, ?, 'Active')")) {
                    pstmt.setInt(1, customerId);
                    pstmt.setInt(2, roomId);
                    pstmt.setString(3, checkIn);
                    pstmt.setString(4, checkOut);
                    pstmt.executeUpdate();
                }

                // Update room status
                try (PreparedStatement pstmt = conn.prepareStatement("UPDATE rooms SET status = 'Booked' WHERE room_id = ?")) {
                    pstmt.setInt(1, roomId);
                    pstmt.executeUpdate();
                }

                conn.commit();
                JOptionPane.showMessageDialog(this, "Room " + roomNumber + " booked successfully!");
                txtCheckIn.setText("YYYY-MM-DD");
                txtCheckOut.setText("YYYY-MM-DD");
                refreshTable();
            } catch (Exception ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Booking Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}
