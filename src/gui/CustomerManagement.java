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
import java.util.ArrayList;
import java.util.List;

public class CustomerManagement extends JFrame {

    private JLabel lblId, lblName, lblEmail, lblPhone, lblAddress, lblUsername, lblPassword;
    private JTextField txtId, txtName, txtEmail, txtPhone, txtAddress, txtUsername, txtPassword;
    private JButton btnAdd, btnUpdate, btnDelete, btnSearch, btnBack, btnClear;
    private JTable customerTable;
    private DefaultTableModel tableModel;

    public CustomerManagement() {
        setTitle("Customer Management");
        setSize(900, 650);
        setLayout(null);
        getContentPane().setBackground(new Color(240, 248, 255));

        JLabel title = new JLabel("CUSTOMER MANAGEMENT");
        title.setBounds(250, 15, 400, 40);
        title.setFont(new Font("Verdana", Font.BOLD, 22));
        title.setForeground(new Color(0, 102, 204));
        title.setHorizontalAlignment(SwingConstants.CENTER);

        lblId = new JLabel("Customer ID (Auto)");
        lblId.setBounds(40, 80, 140, 25);
        txtId = new JTextField();
        txtId.setBounds(180, 80, 200, 30);
        txtId.setEditable(false);

        lblName = new JLabel("Full Name *");
        lblName.setBounds(40, 125, 140, 25);
        txtName = new JTextField();
        txtName.setBounds(180, 125, 200, 30);

        lblUsername = new JLabel("Username *");
        lblUsername.setBounds(40, 170, 140, 25);
        txtUsername = new JTextField();
        txtUsername.setBounds(180, 170, 200, 30);

        lblPassword = new JLabel("Password *");
        lblPassword.setBounds(40, 215, 140, 25);
        txtPassword = new JTextField();
        txtPassword.setBounds(180, 215, 200, 30);

        lblEmail = new JLabel("Email *");
        lblEmail.setBounds(40, 260, 140, 25);
        txtEmail = new JTextField();
        txtEmail.setBounds(180, 260, 200, 30);

        lblPhone = new JLabel("Phone");
        lblPhone.setBounds(40, 305, 140, 25);
        txtPhone = new JTextField();
        txtPhone.setBounds(180, 305, 200, 30);

        lblAddress = new JLabel("Address");
        lblAddress.setBounds(40, 350, 140, 25);
        txtAddress = new JTextField();
        txtAddress.setBounds(180, 350, 200, 30);

        // Buttons
        btnAdd = new JButton("Add");
        btnAdd.setBounds(40, 410, 100, 35);
        btnAdd.setBackground(new Color(0, 153, 76));
        btnAdd.setForeground(Color.WHITE);

        btnUpdate = new JButton("Update");
        btnUpdate.setBounds(150, 410, 100, 35);
        btnUpdate.setBackground(new Color(0, 102, 204));
        btnUpdate.setForeground(Color.WHITE);

        btnDelete = new JButton("Delete");
        btnDelete.setBounds(260, 410, 100, 35);
        btnDelete.setBackground(new Color(204, 0, 0));
        btnDelete.setForeground(Color.WHITE);

        btnSearch = new JButton("Search");
        btnSearch.setBounds(40, 460, 100, 35);
        btnSearch.setBackground(new Color(255, 153, 0));
        btnSearch.setForeground(Color.WHITE);

        btnClear = new JButton("Clear");
        btnClear.setBounds(150, 460, 100, 35);
        btnClear.setBackground(new Color(128, 128, 128));
        btnClear.setForeground(Color.WHITE);

        btnBack = new JButton("Back");
        btnBack.setBounds(260, 460, 100, 35);
        btnBack.setBackground(new Color(64, 64, 64));
        btnBack.setForeground(Color.WHITE);

        // Table
        String[] columns = {"ID", "Name", "Username", "Email", "Phone", "Address"};
        tableModel = new DefaultTableModel(columns, 0);
        customerTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(customerTable);
        scrollPane.setBounds(410, 80, 450, 415);

        add(title);
        add(lblId); add(txtId);
        add(lblName); add(txtName);
        add(lblUsername); add(txtUsername);
        add(lblPassword); add(txtPassword);
        add(lblEmail); add(txtEmail);
        add(lblPhone); add(txtPhone);
        add(lblAddress); add(txtAddress);
        add(btnAdd); add(btnUpdate); add(btnDelete);
        add(btnSearch); add(btnClear); add(btnBack);
        add(scrollPane);

        // Action Listeners
        btnAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addCustomer();
            }
        });

        btnUpdate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateCustomer();
            }
        });

        btnDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteCustomer();
            }
        });

        btnSearch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchCustomer();
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

        // Table Selection Listener
        customerTable.getSelectionModel().addListSelectionListener(e -> {
            int selectedRow = customerTable.getSelectedRow();
            if (selectedRow != -1) {
                txtId.setText(tableModel.getValueAt(selectedRow, 0).toString());
                txtName.setText(tableModel.getValueAt(selectedRow, 1).toString());
                txtUsername.setText(tableModel.getValueAt(selectedRow, 2).toString());
                txtEmail.setText(tableModel.getValueAt(selectedRow, 3).toString());
                txtPhone.setText(tableModel.getValueAt(selectedRow, 4).toString());
                txtAddress.setText(tableModel.getValueAt(selectedRow, 5).toString());
                
                // Fetch password
                try (Connection conn = DatabaseConnection.getConnection();
                     PreparedStatement pstmt = conn.prepareStatement("SELECT password FROM users WHERE id = ?")) {
                    pstmt.setInt(1, Integer.parseInt(txtId.getText()));
                    try (ResultSet rs = pstmt.executeQuery()) {
                        if (rs.next()) {
                            txtPassword.setText(rs.getString("password"));
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
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
             ResultSet rs = stmt.executeQuery("SELECT * FROM users WHERE role = 'CUSTOMER'")) {
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("address")
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading customers: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void addCustomer() {
        String name = txtName.getText().trim();
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText().trim();
        String email = txtEmail.getText().trim();
        String phone = txtPhone.getText().trim();
        String address = txtAddress.getText().trim();

        if (name.isEmpty() || username.isEmpty() || password.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all mandatory fields.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!email.contains("@") || !email.contains(".")) {
            JOptionPane.showMessageDialog(this, "Please enter a valid email.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!phone.isEmpty() && !phone.matches("\\d{10,15}")) {
            JOptionPane.showMessageDialog(this, "Phone number must be digits only and 10 to 15 long.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "INSERT INTO users (username, password, role, name, email, phone, address) VALUES (?, ?, 'CUSTOMER', ?, ?, ?, ?)")) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, name);
            pstmt.setString(4, email);
            pstmt.setString(5, phone);
            pstmt.setString(6, address);
            pstmt.executeUpdate();
            
            JOptionPane.showMessageDialog(this, "Customer added successfully!");
            clearFields();
            refreshTable();
        } catch (Exception e) {
            if (e.getMessage().contains("UNIQUE")) {
                JOptionPane.showMessageDialog(this, "Username already exists.", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Error adding customer: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
            e.printStackTrace();
        }
    }

    private void updateCustomer() {
        if (txtId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select a customer to update from the table first.", "Selection Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int id = Integer.parseInt(txtId.getText());
        String name = txtName.getText().trim();
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText().trim();
        String email = txtEmail.getText().trim();
        String phone = txtPhone.getText().trim();
        String address = txtAddress.getText().trim();

        if (name.isEmpty() || username.isEmpty() || password.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all mandatory fields.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "UPDATE users SET username = ?, password = ?, name = ?, email = ?, phone = ?, address = ? WHERE id = ?")) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, name);
            pstmt.setString(4, email);
            pstmt.setString(5, phone);
            pstmt.setString(6, address);
            pstmt.setInt(7, id);
            pstmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Customer updated successfully!");
            clearFields();
            refreshTable();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error updating customer: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void deleteCustomer() {
        if (txtId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select a customer to delete from the table first.", "Selection Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = Integer.parseInt(txtId.getText());
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this customer? All their reservations will remain.", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement("DELETE FROM users WHERE id = ?")) {
                pstmt.setInt(1, id);
                pstmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "Customer deleted successfully!");
                clearFields();
                refreshTable();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error deleting customer: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    private void searchCustomer() {
        String query = JOptionPane.showInputDialog(this, "Enter Customer Name to search:");
        if (query == null || query.trim().isEmpty()) {
            return;
        }
        
        tableModel.setRowCount(0);
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM users WHERE role = 'CUSTOMER' AND name LIKE ?")) {
            pstmt.setString(1, "%" + query.trim() + "%");
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    tableModel.addRow(new Object[]{
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("username"),
                            rs.getString("email"),
                            rs.getString("phone"),
                            rs.getString("address")
                    });
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Search error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void clearFields() {
        txtId.setText("");
        txtName.setText("");
        txtUsername.setText("");
        txtPassword.setText("");
        txtEmail.setText("");
        txtPhone.setText("");
        txtAddress.setText("");
        customerTable.clearSelection();
    }
}
