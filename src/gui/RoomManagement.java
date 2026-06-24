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

public class RoomManagement extends JFrame {

    private JLabel lblRoomId, lblRoomNo, lblRoomType, lblPrice, lblStatus;
    private JTextField txtRoomId, txtRoomNo, txtPrice;
    private JComboBox<String> cmbRoomType, cmbStatus;
    private JButton btnAdd, btnUpdate, btnDelete, btnSearch, btnBack, btnClear;
    private JTable roomTable;
    private DefaultTableModel tableModel;

    public RoomManagement() {
        setTitle("Room Management");
        setSize(900, 600);
        setLayout(null);
        getContentPane().setBackground(new Color(245, 248, 250));

        JLabel title = new JLabel("ROOM MANAGEMENT");
        title.setBounds(250, 15, 400, 40);
        title.setFont(new Font("Verdana", Font.BOLD, 22));
        title.setForeground(new Color(0, 102, 204));
        title.setHorizontalAlignment(SwingConstants.CENTER);

        lblRoomId = new JLabel("Room ID (Auto)");
        lblRoomId.setBounds(40, 80, 140, 25);
        txtRoomId = new JTextField();
        txtRoomId.setBounds(180, 80, 200, 30);
        txtRoomId.setEditable(false);

        lblRoomNo = new JLabel("Room Number *");
        lblRoomNo.setBounds(40, 130, 140, 25);
        txtRoomNo = new JTextField();
        txtRoomNo.setBounds(180, 130, 200, 30);

        lblRoomType = new JLabel("Room Type");
        lblRoomType.setBounds(40, 180, 140, 25);
        String[] types = {"Single", "Double", "Deluxe", "Suite"};
        cmbRoomType = new JComboBox<>(types);
        cmbRoomType.setBounds(180, 180, 200, 30);

        lblPrice = new JLabel("Price/Night *");
        lblPrice.setBounds(40, 230, 140, 25);
        txtPrice = new JTextField();
        txtPrice.setBounds(180, 230, 200, 30);

        lblStatus = new JLabel("Status");
        lblStatus.setBounds(40, 280, 140, 25);
        String[] statusOptions = {"Available", "Booked", "Maintenance"};
        cmbStatus = new JComboBox<>(statusOptions);
        cmbStatus.setBounds(180, 280, 200, 30);

        // Buttons
        btnAdd = new JButton("Add");
        btnAdd.setBounds(40, 360, 100, 35);
        btnAdd.setBackground(new Color(0, 153, 76));
        btnAdd.setForeground(Color.WHITE);

        btnUpdate = new JButton("Update");
        btnUpdate.setBounds(150, 360, 100, 35);
        btnUpdate.setBackground(new Color(0, 102, 204));
        btnUpdate.setForeground(Color.WHITE);

        btnDelete = new JButton("Delete");
        btnDelete.setBounds(260, 360, 100, 35);
        btnDelete.setBackground(new Color(204, 0, 0));
        btnDelete.setForeground(Color.WHITE);

        btnSearch = new JButton("Search");
        btnSearch.setBounds(40, 415, 100, 35);
        btnSearch.setBackground(new Color(255, 153, 0));
        btnSearch.setForeground(Color.WHITE);

        btnClear = new JButton("Clear");
        btnClear.setBounds(150, 415, 100, 35);
        btnClear.setBackground(new Color(128, 128, 128));
        btnClear.setForeground(Color.WHITE);

        btnBack = new JButton("Back");
        btnBack.setBounds(260, 415, 100, 35);
        btnBack.setBackground(new Color(64, 64, 64));
        btnBack.setForeground(Color.WHITE);

        // Table
        String[] columns = {"Room ID", "Room Number", "Room Type", "Price/Night", "Status"};
        tableModel = new DefaultTableModel(columns, 0);
        roomTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(roomTable);
        scrollPane.setBounds(410, 80, 450, 370);

        add(title);
        add(lblRoomId); add(txtRoomId);
        add(lblRoomNo); add(txtRoomNo);
        add(lblRoomType); add(cmbRoomType);
        add(lblPrice); add(txtPrice);
        add(lblStatus); add(cmbStatus);
        add(btnAdd); add(btnUpdate); add(btnDelete);
        add(btnSearch); add(btnClear); add(btnBack);
        add(scrollPane);

        // Action Listeners
        btnAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addRoom();
            }
        });

        btnUpdate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateRoom();
            }
        });

        btnDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteRoom();
            }
        });

        btnSearch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchRoom();
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

        // Table selection listener
        roomTable.getSelectionModel().addListSelectionListener(e -> {
            int selectedRow = roomTable.getSelectedRow();
            if (selectedRow != -1) {
                txtRoomId.setText(tableModel.getValueAt(selectedRow, 0).toString());
                txtRoomNo.setText(tableModel.getValueAt(selectedRow, 1).toString());
                cmbRoomType.setSelectedItem(tableModel.getValueAt(selectedRow, 2).toString());
                txtPrice.setText(tableModel.getValueAt(selectedRow, 3).toString());
                cmbStatus.setSelectedItem(tableModel.getValueAt(selectedRow, 4).toString());
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
             ResultSet rs = stmt.executeQuery("SELECT * FROM rooms")) {
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

    private void addRoom() {
        String roomNo = txtRoomNo.getText().trim();
        String roomType = cmbRoomType.getSelectedItem().toString();
        String priceText = txtPrice.getText().trim();
        String status = cmbStatus.getSelectedItem().toString();

        if (roomNo.isEmpty() || priceText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all mandatory fields.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceText);
            if (price <= 0) {
                JOptionPane.showMessageDialog(this, "Price must be greater than zero.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Price must be a valid numeric value.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("INSERT INTO rooms (room_number, room_type, price, status) VALUES (?, ?, ?, ?)")) {
            pstmt.setString(1, roomNo);
            pstmt.setString(2, roomType);
            pstmt.setDouble(3, price);
            pstmt.setString(4, status);
            pstmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Room added successfully!");
            clearFields();
            refreshTable();
        } catch (Exception e) {
            if (e.getMessage().contains("UNIQUE")) {
                JOptionPane.showMessageDialog(this, "Room number already exists.", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Error adding room: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
            e.printStackTrace();
        }
    }

    private void updateRoom() {
        if (txtRoomId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select a room from the table to update first.", "Selection Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = Integer.parseInt(txtRoomId.getText());
        String roomNo = txtRoomNo.getText().trim();
        String roomType = cmbRoomType.getSelectedItem().toString();
        String priceText = txtPrice.getText().trim();
        String status = cmbStatus.getSelectedItem().toString();

        if (roomNo.isEmpty() || priceText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all mandatory fields.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceText);
            if (price <= 0) {
                JOptionPane.showMessageDialog(this, "Price must be greater than zero.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Price must be a valid numeric value.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("UPDATE rooms SET room_number = ?, room_type = ?, price = ?, status = ? WHERE room_id = ?")) {
            pstmt.setString(1, roomNo);
            pstmt.setString(2, roomType);
            pstmt.setDouble(3, price);
            pstmt.setString(4, status);
            pstmt.setInt(5, id);
            pstmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Room updated successfully!");
            clearFields();
            refreshTable();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error updating room: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void deleteRoom() {
        if (txtRoomId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select a room from the table to delete first.", "Selection Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = Integer.parseInt(txtRoomId.getText());
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this room? This might affect reservations associated with it.", "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement("DELETE FROM rooms WHERE room_id = ?")) {
                pstmt.setInt(1, id);
                pstmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "Room deleted successfully!");
                clearFields();
                refreshTable();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error deleting room: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    private void searchRoom() {
        String query = JOptionPane.showInputDialog(this, "Enter Room Number to search:");
        if (query == null || query.trim().isEmpty()) {
            return;
        }

        tableModel.setRowCount(0);
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM rooms WHERE room_number LIKE ?")) {
            pstmt.setString(1, "%" + query.trim() + "%");
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

    private void clearFields() {
        txtRoomId.setText("");
        txtRoomNo.setText("");
        cmbRoomType.setSelectedIndex(0);
        txtPrice.setText("");
        cmbStatus.setSelectedIndex(0);
        roomTable.clearSelection();
    }
}
