package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AdminDashboard extends JFrame {

    private JButton btnCustomers, btnRooms, btnReservations;
    private JButton btnPayments, btnReports, btnLogout;

    public AdminDashboard() {
        setTitle("Admin Dashboard");
        setSize(600, 500);
        setLayout(null);
        getContentPane().setBackground(new Color(240, 248, 255));

        JLabel title = new JLabel("ADMIN DASHBOARD");
        title.setBounds(100, 20, 400, 40);
        title.setFont(new Font("Verdana", Font.BOLD, 22));
        title.setForeground(new Color(0, 51, 153));
        title.setHorizontalAlignment(SwingConstants.CENTER);

        btnCustomers = new JButton("Manage Customers");
        btnCustomers.setBounds(180, 90, 220, 40);
        btnCustomers.setBackground(new Color(0, 102, 204));
        btnCustomers.setForeground(Color.WHITE);
        btnCustomers.setFont(new Font("Arial", Font.BOLD, 14));

        btnRooms = new JButton("Manage Rooms");
        btnRooms.setBounds(180, 145, 220, 40);
        btnRooms.setBackground(new Color(0, 153, 76));
        btnRooms.setForeground(Color.WHITE);
        btnRooms.setFont(new Font("Arial", Font.BOLD, 14));

        btnReservations = new JButton("Manage Reservations");
        btnReservations.setBounds(180, 200, 220, 40);
        btnReservations.setBackground(new Color(255, 153, 0));
        btnReservations.setForeground(Color.WHITE);
        btnReservations.setFont(new Font("Arial", Font.BOLD, 14));

        btnPayments = new JButton("Manage Payments");
        btnPayments.setBounds(180, 255, 220, 40);
        btnPayments.setBackground(new Color(102, 0, 204));
        btnPayments.setForeground(Color.WHITE);
        btnPayments.setFont(new Font("Arial", Font.BOLD, 14));

        btnReports = new JButton("Reports");
        btnReports.setBounds(180, 310, 220, 40);
        btnReports.setBackground(new Color(0, 153, 153));
        btnReports.setForeground(Color.WHITE);
        btnReports.setFont(new Font("Arial", Font.BOLD, 14));

        btnLogout = new JButton("Logout");
        btnLogout.setBounds(180, 365, 220, 40);
        btnLogout.setBackground(new Color(204, 0, 0));
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFont(new Font("Arial", Font.BOLD, 14));

        add(title);
        add(btnCustomers);
        add(btnRooms);
        add(btnReservations);
        add(btnPayments);
        add(btnReports);
        add(btnLogout);

        // Wiring listeners
        btnCustomers.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new CustomerManagement().setVisible(true);
                dispose();
            }
        });

        btnRooms.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new RoomManagement().setVisible(true);
                dispose();
            }
        });

        btnReservations.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new ReservationManagement().setVisible(true);
                dispose();
            }
        });

        btnPayments.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new PaymentManagement().setVisible(true);
                dispose();
            }
        });

        btnReports.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new ReportsForm().setVisible(true);
                dispose();
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
}
