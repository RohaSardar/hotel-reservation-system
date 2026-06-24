package gui;

import database.DatabaseConnection;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class ReportsForm extends JFrame {

    private JTabbedPane tabbedPane;
    private JTable tableCustomers, tableRooms, tableReservations, tablePayments;
    private DefaultTableModel modelCustomers, modelRooms, modelReservations, modelPayments;
    private JButton btnBack, btnRefresh;

    public ReportsForm() {
        setTitle("System Reports");
        setSize(850, 600);
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();
        topPanel.setBackground(new Color(0, 102, 204));
        JLabel title = new JLabel("HOTEL SYSTEM REPORTS");
        title.setFont(new Font("Verdana", Font.BOLD, 22));
        title.setForeground(Color.WHITE);
        topPanel.add(title);

        tabbedPane = new JTabbedPane();

        // 1. Customers Tab
        String[] colsCustomers = {"ID", "Name", "Username", "Email", "Phone", "Address"};
        modelCustomers = new DefaultTableModel(colsCustomers, 0);
        tableCustomers = new JTable(modelCustomers);
        tabbedPane.addTab("Customers", new JScrollPane(tableCustomers));

        // 2. Rooms Tab
        String[] colsRooms = {"Room ID", "Room Number", "Room Type", "Price/Night", "Status"};
        modelRooms = new DefaultTableModel(colsRooms, 0);
        tableRooms = new JTable(modelRooms);
        tabbedPane.addTab("Rooms", new JScrollPane(tableRooms));

        // 3. Reservations Tab
        String[] colsReservations = {"Res ID", "Customer Name", "Room Number", "Check In", "Check Out", "Status"};
        modelReservations = new DefaultTableModel(colsReservations, 0);
        tableReservations = new JTable(modelReservations);
        tabbedPane.addTab("Reservations", new JScrollPane(tableReservations));

        // 4. Payments Tab
        String[] colsPayments = {"Payment ID", "Res ID", "Customer Name", "Room Number", "Amount Paid", "Date"};
        modelPayments = new DefaultTableModel(colsPayments, 0);
        tablePayments = new JTable(modelPayments);
        tabbedPane.addTab("Payments", new JScrollPane(tablePayments));

        // Bottom Panel
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(new Color(245, 245, 245));
        
        btnRefresh = new JButton("Refresh Data");
        btnRefresh.setBackground(new Color(0, 153, 76));
        btnRefresh.setForeground(Color.WHITE);
        btnRefresh.setFont(new Font("Arial", Font.BOLD, 14));

        btnBack = new JButton("Back to Dashboard");
        btnBack.setBackground(new Color(204, 0, 0));
        btnBack.setForeground(Color.WHITE);
        btnBack.setFont(new Font("Arial", Font.BOLD, 14));

        bottomPanel.add(btnRefresh);
        bottomPanel.add(btnBack);

        add(topPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        btnRefresh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadAllReports();
            }
        });

        btnBack.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new AdminDashboard().setVisible(true);
                dispose();
            }
        });

        loadAllReports();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void loadAllReports() {
        loadCustomersReport();
        loadRoomsReport();
        loadReservationsReport();
        loadPaymentsReport();
    }

    private void loadCustomersReport() {
        modelCustomers.setRowCount(0);
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM users WHERE role = 'CUSTOMER'")) {
            while (rs.next()) {
                modelCustomers.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("address")
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadRoomsReport() {
        modelRooms.setRowCount(0);
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM rooms")) {
            while (rs.next()) {
                modelRooms.addRow(new Object[]{
                        rs.getInt("room_id"),
                        rs.getString("room_number"),
                        rs.getString("room_type"),
                        rs.getDouble("price"),
                        rs.getString("status")
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadReservationsReport() {
        modelReservations.setRowCount(0);
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "SELECT r.reservation_id, u.name as customer_name, rm.room_number, r.check_in, r.check_out, r.status " +
                     "FROM reservations r " +
                     "JOIN users u ON r.customer_id = u.id " +
                     "JOIN rooms rm ON r.room_id = rm.room_id")) {
            while (rs.next()) {
                modelReservations.addRow(new Object[]{
                        rs.getInt("reservation_id"),
                        rs.getString("customer_name"),
                        rs.getString("room_number"),
                        rs.getString("check_in"),
                        rs.getString("check_out"),
                        rs.getString("status")
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadPaymentsReport() {
        modelPayments.setRowCount(0);
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "SELECT p.payment_id, p.reservation_id, u.name as customer_name, rm.room_number, p.amount, p.payment_date " +
                     "FROM payments p " +
                     "JOIN reservations r ON p.reservation_id = r.reservation_id " +
                     "JOIN users u ON r.customer_id = u.id " +
                     "JOIN rooms rm ON r.room_id = rm.room_id")) {
            while (rs.next()) {
                modelPayments.addRow(new Object[]{
                        rs.getInt("payment_id"),
                        rs.getInt("reservation_id"),
                        rs.getString("customer_name"),
                        rs.getString("room_number"),
                        rs.getDouble("amount"),
                        rs.getString("payment_date")
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
