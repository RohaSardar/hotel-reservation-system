package gui;

import database.DatabaseConnection;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginForm extends JFrame {

    private JLabel title, lblUser, lblPass;
    private JTextField txtUser;
    private JPasswordField txtPass;
    private JButton btnLogin, btnRegister, btnExit;

    public LoginForm() {
        setTitle("Hotel Reservation System - Login");
        setSize(500, 420);
        setLayout(null);
        getContentPane().setBackground(new Color(230, 245, 255));

        title = new JLabel("HOTEL RESERVATION SYSTEM");
        title.setBounds(60, 20, 380, 40);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setForeground(new Color(0, 102, 204));
        title.setHorizontalAlignment(SwingConstants.CENTER);

        lblUser = new JLabel("Username");
        lblUser.setBounds(80, 100, 100, 25);
        lblUser.setFont(new Font("Arial", Font.BOLD, 14));

        txtUser = new JTextField();
        txtUser.setBounds(190, 100, 220, 30);

        lblPass = new JLabel("Password");
        lblPass.setBounds(80, 155, 100, 25);
        lblPass.setFont(new Font("Arial", Font.BOLD, 14));

        txtPass = new JPasswordField();
        txtPass.setBounds(190, 155, 220, 30);

        btnLogin = new JButton("Login");
        btnLogin.setBounds(70, 230, 100, 35);
        btnLogin.setBackground(new Color(0, 153, 76));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFont(new Font("Arial", Font.BOLD, 14));

        btnRegister = new JButton("Register");
        btnRegister.setBounds(190, 230, 110, 35);
        btnRegister.setBackground(new Color(0, 102, 204));
        btnRegister.setForeground(Color.WHITE);
        btnRegister.setFont(new Font("Arial", Font.BOLD, 14));

        btnExit = new JButton("Exit");
        btnExit.setBounds(320, 230, 100, 35);
        btnExit.setBackground(new Color(204, 0, 0));
        btnExit.setForeground(Color.WHITE);
        btnExit.setFont(new Font("Arial", Font.BOLD, 14));

        add(title);
        add(lblUser);
        add(txtUser);
        add(lblPass);
        add(txtPass);
        add(btnLogin);
        add(btnRegister);
        add(btnExit);

        // Action Listeners
        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });

        btnRegister.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new CustomerRegistrationForm().setVisible(true);
                dispose();
            }
        });

        btnExit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void handleLogin() {
        String username = txtUser.getText().trim();
        String password = new String(txtPass.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both Username and Password", "Login Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM users WHERE username = ? AND password = ?")) {
            
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String role = rs.getString("role");
                    String name = rs.getString("name");
                    int userId = rs.getInt("id");

                    JOptionPane.showMessageDialog(this, "Welcome " + name + "! Logged in as " + role);
                    
                    if ("ADMIN".equalsIgnoreCase(role)) {
                        new AdminDashboard().setVisible(true);
                    } else {
                        new CustomerDashboard(userId, name).setVisible(true);
                    }
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid Username or Password", "Login Failed", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // Run standalone test
        DatabaseConnection.initializeDatabase();
        SwingUtilities.invokeLater(() -> new LoginForm().setVisible(true));
    }
}
