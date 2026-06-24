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

public class ReservationManagement extends JFrame {

    private JLabel lblResId, lblCustomerId, lblRoomId, lblCheckIn, lblCheckOut, lblStatus;
    private JTextField txtResId, txtCustomerId, txtRoomId, txtCheckIn, txtCheckOut;
    private JComboBox<String> cmbStatus;
    private JButton btnReserve, btnUpdate, btnCancel, btnClear, btnBack;
    private JTable resTable;
    private DefaultTableModel tableModel;

    public ReservationManagement() {
        setTitle("Reservation Management");
        setSize(950, 600);
        setLayout(null);
        getContentPane().setBackground(new Color(245, 245, 245));

        JLabel title = new JLabel("RESERVATION MANAGEMENT");
        title.setBounds(280, 15, 400, 40);
        title.setFont(new Font("Verdana", Font.BOLD, 22));
        title.setForeground(new Color(0, 102, 204));
        title.setHorizontalAlignment(SwingConstants.CENTER);

        lblResId = new JLabel("Reservation ID (Auto)");
        lblResId.setBounds(40, 80, 150, 25);
        txtResId = new JTextField();
        txtResId.setBounds(200, 80, 200, 30);
        txtResId.setEditable(false);

        lblCustomerId = new JLabel("Customer ID *");
        lblCustomerId.setBounds(40, 130, 150, 25);
        txtCustomerId = new JTextField();
        txtCustomerId.setBounds(200, 130, 200, 30);

        lblRoomId = new JLabel("Room ID *");
        lblRoomId.setBounds(40, 180, 150, 25);
        txtRoomId = new JTextField();
        txtRoomId.setBounds(200, 180, 200, 30);

        lblCheckIn = new JLabel("Check-In Date *");
        lblCheckIn.setBounds(40, 230, 150, 25);
        txtCheckIn = new JTextField("YYYY-MM-DD");
        txtCheckIn.setBounds(200, 230, 200, 30);

        lblCheckOut = new JLabel("Check-Out Date *");
        lblCheckOut.setBounds(40, 280, 150, 25);
        txtCheckOut = new JTextField("YYYY-MM-DD");
        txtCheckOut.setBounds(200, 280, 200, 30);

        lblStatus = new JLabel("Status");
        lblStatus.setBounds(40, 330, 150, 25);
        String[] statuses = {"Active", "Cancelled", "Completed"};
        cmbStatus = new JComboBox<>(statuses);
        cmbStatus.setBounds(200, 330, 200, 30);

        // Buttons
        btnReserve = new JButton("Reserve");
        btnReserve.setBounds(40, 400, 100, 35);
        btnReserve.setBackground(new Color(0, 153, 76));
        btnReserve.setForeground(Color.WHITE);

        btnUpdate = new JButton("Update");
        btnUpdate.setBounds(150, 400, 100, 35);
        btnUpdate.setBackground(new Color(0, 102, 204));
        btnUpdate.setForeground(Color.WHITE);

        btnCancel = new JButton("Cancel Res.");
        btnCancel.setBounds(260, 400, 110, 35);
        btnCancel.setBackground(new Color(204, 0, 0));
        btnCancel.setForeground(Color.WHITE);

        btnClear = new JButton("Clear");
        btnClear.setBounds(90, 455, 100, 35);
        btnClear.setBackground(new Color(128, 128, 128));
        btnClear.setForeground(Color.WHITE);

        btnBack = new JButton("Back");
        btnBack.setBounds(210, 455, 100, 35);
        btnBack.setBackground(new Color(64, 64, 64));
        btnBack.setForeground(Color.WHITE);

        // Table
        String[] columns = {"Res ID", "Cust ID", "Cust Name", "Room ID", "Room No", "Check In", "Check Out", "Status"};
        tableModel = new DefaultTableModel(columns, 0);
        resTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(resTable);
        scrollPane.setBounds(430, 80, 475, 410);

        add(title);
        add(lblResId); add(txtResId);
        add(lblCustomerId); add(txtCustomerId);
        add(lblRoomId); add(txtRoomId);
        add(lblCheckIn); add(txtCheckIn);
        add(lblCheckOut); add(txtCheckOut);
        add(lblStatus); add(cmbStatus);
        add(btnReserve); add(btnUpdate); add(btnCancel);
        add(btnClear); add(btnBack);
        add(scrollPane);

        // Action Listeners
        btnReserve.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createReservation();
            }
        });

        btnUpdate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateReservation();
            }
        });

        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cancelReservation();
            }
        });

        btnClear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearFields();
            }
        });

        btnBack.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new AdminDashboard().setVisible(true);
                dispose();
            }
        });

        resTable.getSelectionModel().addListSelectionListener(e -> {
            int selectedRow = resTable.getSelectedRow();
            if (selectedRow != -1) {
                txtResId.setText(tableModel.getValueAt(selectedRow, 0).toString());
                txtCustomerId.setText(tableModel.getValueAt(selectedRow, 1).toString());
                txtRoomId.setText(tableModel.getValueAt(selectedRow, 3).toString());
                txtCheckIn.setText(tableModel.getValueAt(selectedRow, 5).toString());
                txtCheckOut.setText(tableModel.getValueAt(selectedRow, 6).toString());
                cmbStatus.setSelectedItem(tableModel.getValueAt(selectedRow, 7).toString());
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
             ResultSet rs = stmt.executeQuery(
                     "SELECT r.reservation_id, r.customer_id, u.name as customer_name, r.room_id, rm.room_number, r.check_in, r.check_out, r.status " +
                     "FROM reservations r " +
                     "JOIN users u ON r.customer_id = u.id " +
                     "JOIN rooms rm ON r.room_id = rm.room_id")) {
            
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("reservation_id"),
                        rs.getInt("customer_id"),
                        rs.getString("customer_name"),
                        rs.getInt("room_id"),
                        rs.getString("room_number"),
                        rs.getString("check_in"),
                        rs.getString("check_out"),
                        rs.getString("status")
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading reservations: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private boolean validateEntities(Connection conn, int customerId, int roomId) throws Exception {
        // Validate Customer
        try (PreparedStatement pstmt = conn.prepareStatement("SELECT role FROM users WHERE id = ?")) {
            pstmt.setInt(1, customerId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (!rs.next() || !"CUSTOMER".equalsIgnoreCase(rs.getString("role"))) {
                    JOptionPane.showMessageDialog(this, "Invalid Customer ID. Customer does not exist.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                    return false;
                }
            }
        }

        // Validate Room
        try (PreparedStatement pstmt = conn.prepareStatement("SELECT status FROM rooms WHERE room_id = ?")) {
            pstmt.setInt(1, roomId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (!rs.next()) {
                    JOptionPane.showMessageDialog(this, "Invalid Room ID. Room does not exist.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                    return false;
                }
            }
        }
        return true;
    }

    private void createReservation() {
        String custIdStr = txtCustomerId.getText().trim();
        String roomIdStr = txtRoomId.getText().trim();
        String checkIn = txtCheckIn.getText().trim();
        String checkOut = txtCheckOut.getText().trim();
        String status = cmbStatus.getSelectedItem().toString();

        if (custIdStr.isEmpty() || roomIdStr.isEmpty() || checkIn.isEmpty() || checkOut.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all mandatory fields.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!checkIn.matches("\\d{4}-\\d{2}-\\d{2}") || !checkOut.matches("\\d{4}-\\d{2}-\\d{2}")) {
            JOptionPane.showMessageDialog(this, "Dates must be in YYYY-MM-DD format.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            int customerId = Integer.parseInt(custIdStr);
            int roomId = Integer.parseInt(roomIdStr);

            if (!validateEntities(conn, customerId, roomId)) return;

            // Check if Room is Available
            try (PreparedStatement pstmt = conn.prepareStatement("SELECT status FROM rooms WHERE room_id = ?")) {
                pstmt.setInt(1, roomId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next() && !"Available".equalsIgnoreCase(rs.getString("status"))) {
                        JOptionPane.showMessageDialog(this, "Room is not available (Status: " + rs.getString("status") + ").", "Room Not Available", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                }
            }

            // Create Reservation
            try (PreparedStatement pstmt = conn.prepareStatement(
                    "INSERT INTO reservations (customer_id, room_id, check_in, check_out, status) VALUES (?, ?, ?, ?, ?)")) {
                pstmt.setInt(1, customerId);
                pstmt.setInt(2, roomId);
                pstmt.setString(3, checkIn);
                pstmt.setString(4, checkOut);
                pstmt.setString(5, status);
                pstmt.executeUpdate();
            }

            // If reservation is Active, update Room status to Booked
            if ("Active".equalsIgnoreCase(status)) {
                try (PreparedStatement pstmt = conn.prepareStatement("UPDATE rooms SET status = 'Booked' WHERE room_id = ?")) {
                    pstmt.setInt(1, roomId);
                    pstmt.executeUpdate();
                }
            }

            JOptionPane.showMessageDialog(this, "Reservation created successfully!");
            clearFields();
            refreshTable();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "IDs must be integers.", "Validation Error", JOptionPane.WARNING_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Database Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void updateReservation() {
        if (txtResId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select a reservation to update first.", "Selection Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int resId = Integer.parseInt(txtResId.getText());
        String custIdStr = txtCustomerId.getText().trim();
        String roomIdStr = txtRoomId.getText().trim();
        String checkIn = txtCheckIn.getText().trim();
        String checkOut = txtCheckOut.getText().trim();
        String status = cmbStatus.getSelectedItem().toString();

        if (custIdStr.isEmpty() || roomIdStr.isEmpty() || checkIn.isEmpty() || checkOut.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all mandatory fields.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            int customerId = Integer.parseInt(custIdStr);
            int roomId = Integer.parseInt(roomIdStr);

            if (!validateEntities(conn, customerId, roomId)) return;

            // Fetch old room ID and old status
            int oldRoomId = -1;
            String oldStatus = "";
            try (PreparedStatement pstmt = conn.prepareStatement("SELECT room_id, status FROM reservations WHERE reservation_id = ?")) {
                pstmt.setInt(1, resId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        oldRoomId = rs.getInt("room_id");
                        oldStatus = rs.getString("status");
                    }
                }
            }

            // Update reservation
            try (PreparedStatement pstmt = conn.prepareStatement(
                    "UPDATE reservations SET customer_id = ?, room_id = ?, check_in = ?, check_out = ?, status = ? WHERE reservation_id = ?")) {
                pstmt.setInt(1, customerId);
                pstmt.setInt(2, roomId);
                pstmt.setString(3, checkIn);
                pstmt.setString(4, checkOut);
                pstmt.setString(5, status);
                pstmt.setInt(6, resId);
                pstmt.executeUpdate();
            }

            // Reset old room status if room changed or status changed
            if (oldRoomId != -1) {
                try (PreparedStatement pstmt = conn.prepareStatement("UPDATE rooms SET status = 'Available' WHERE room_id = ?")) {
                    pstmt.setInt(1, oldRoomId);
                    pstmt.executeUpdate();
                }
            }

            // Set new room status based on new status
            String newRoomStatus = "Available";
            if ("Active".equalsIgnoreCase(status)) {
                newRoomStatus = "Booked";
            }
            try (PreparedStatement pstmt = conn.prepareStatement("UPDATE rooms SET status = ? WHERE room_id = ?")) {
                pstmt.setString(1, newRoomStatus);
                pstmt.setInt(2, roomId);
                pstmt.executeUpdate();
            }

            JOptionPane.showMessageDialog(this, "Reservation updated successfully!");
            clearFields();
            refreshTable();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error updating reservation: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void cancelReservation() {
        if (txtResId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select a reservation to cancel first.", "Selection Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int resId = Integer.parseInt(txtResId.getText());
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to cancel this reservation?", "Confirm Cancel", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DatabaseConnection.getConnection()) {
                int roomId = -1;
                try (PreparedStatement pstmt = conn.prepareStatement("SELECT room_id FROM reservations WHERE reservation_id = ?")) {
                    pstmt.setInt(1, resId);
                    try (ResultSet rs = pstmt.executeQuery()) {
                        if (rs.next()) {
                            roomId = rs.getInt("room_id");
                        }
                    }
                }

                // Update reservation status to Cancelled
                try (PreparedStatement pstmt = conn.prepareStatement("UPDATE reservations SET status = 'Cancelled' WHERE reservation_id = ?")) {
                    pstmt.setInt(1, resId);
                    pstmt.executeUpdate();
                }

                // Make room Available again
                if (roomId != -1) {
                    try (PreparedStatement pstmt = conn.prepareStatement("UPDATE rooms SET status = 'Available' WHERE room_id = ?")) {
                        pstmt.setInt(1, roomId);
                        pstmt.executeUpdate();
                    }
                }

                JOptionPane.showMessageDialog(this, "Reservation cancelled successfully!");
                clearFields();
                refreshTable();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error cancelling reservation: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    private void clearFields() {
        txtResId.setText("");
        txtCustomerId.setText("");
        txtRoomId.setText("");
        txtCheckIn.setText("YYYY-MM-DD");
        txtCheckOut.setText("YYYY-MM-DD");
        cmbStatus.setSelectedIndex(0);
        resTable.clearSelection();
    }
}
