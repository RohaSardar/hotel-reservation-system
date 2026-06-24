package gui;

import database.DatabaseConnection;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class CustomerRegistrationForm extends JFrame {

    private JLabel lblName, lblUser, lblPass, lblEmail, lblPhone, lblAddress;
    private JTextField txtName, txtUser, txtEmail, txtPhone, txtAddress;
    private JPasswordField txtPass;
    private JButton btnRegister, btnBack;

    public CustomerRegistrationForm() {
        setTitle("Customer Registration");
        setSize(550, 520);
        setLayout(null);
        getContentPane().setBackground(new Color(245, 245, 245));

        JLabel title = new JLabel("CUSTOMER REGISTRATION");
        title.setBounds(100, 20, 350, 40);
        title.setFont(new Font("Verdana", Font.BOLD, 20));
        title.setForeground(new Color(0, 102, 204));
        title.setHorizontalAlignment(SwingConstants.CENTER);

        lblName = new JLabel("Full Name *");
        lblName.setBounds(60, 90, 100, 25);
        txtName = new JTextField();
        txtName.setBounds(180, 90, 260, 30);

        lblUser = new JLabel("Username *");
        lblUser.setBounds(60, 140, 100, 25);
        txtUser = new JTextField();
        txtUser.setBounds(180, 140, 260, 30);

        lblPass = new JLabel("Password *");
        lblPass.setBounds(60, 190, 100, 25);
        txtPass = new JPasswordField();
        txtPass.setBounds(180, 190, 260, 30);

        lblEmail = new JLabel("Email *");
        lblEmail.setBounds(60, 240, 100, 25);
        txtEmail = new JTextField();
        txtEmail.setBounds(180, 240, 260, 30);

        lblPhone = new JLabel("Phone");
        lblPhone.setBounds(60, 290, 100, 25);
        txtPhone = new JTextField();
        txtPhone.setBounds(180, 290, 260, 30);

        lblAddress = new JLabel("Address");
        lblAddress.setBounds(60, 340, 100, 25);
        txtAddress = new JTextField();
        txtAddress.setBounds(180, 340, 260, 30);

        btnRegister = new JButton("Register");
        btnRegister.setBounds(130, 410, 120, 35);
        btnRegister.setBackground(new Color(0, 153, 76));
        btnRegister.setForeground(Color.WHITE);
        btnRegister.setFont(new Font("Arial", Font.BOLD, 14));

        btnBack = new JButton("Back to Login");
        btnBack.setBounds(270, 410, 140, 35);
        btnBack.setBackground(new Color(204, 0, 0));
        btnBack.setForeground(Color.WHITE);
        btnBack.setFont(new Font("Arial", Font.BOLD, 14));

        add(title);
        add(lblName);
        add(txtName);
        add(lblUser);
        add(txtUser);
        add(lblPass);
        add(txtPass);
        add(lblEmail);
        add(txtEmail);
        add(lblPhone);
        add(txtPhone);
        add(lblAddress);
        add(txtAddress);
        add(btnRegister);
        add(btnBack);

        btnRegister.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleRegistration();
            }
        });

        btnBack.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new LoginForm().setVisible(true);
                dispose();
            }
        });

        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void handleRegistration() {
        String name = txtName.getText().trim();
        String username = txtUser.getText().trim();
        String password = new String(txtPass.getPassword()).trim();
        String email = txtEmail.getText().trim();
        String phone = txtPhone.getText().trim();
        String address = txtAddress.getText().trim();

        // Validation Checks
        if (name.isEmpty() || username.isEmpty() || password.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all mandatory (*) fields.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!email.contains("@") || !email.contains(".")) {
            JOptionPane.showMessageDialog(this, "Please enter a valid email address.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!phone.isEmpty() && !phone.matches("\\d{10,15}")) {
            JOptionPane.showMessageDialog(this, "Phone number must be digits only and between 10 to 15 digits.", "Validation Error", JOptionPane.ERROR_MESSAGE);
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

            int result = pstmt.executeUpdate();
            if (result > 0) {
                JOptionPane.showMessageDialog(this, "Registration Successful! You can now login.");
                new LoginForm().setVisible(true);
                dispose();
            }
        } catch (Exception e) {
            String errorMsg = e.getMessage();
            if (errorMsg.contains("UNIQUE constraint failed")) {
                JOptionPane.showMessageDialog(this, "Username already exists. Please choose a different one.", "Registration Error", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Database Error: " + errorMsg, "Error", JOptionPane.ERROR_MESSAGE);
            }
            e.printStackTrace();
        }
    }
}
