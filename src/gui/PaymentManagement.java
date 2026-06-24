package gui;

import database.DatabaseConnection;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PaymentManagement extends JFrame {

    private JLabel lblPaymentId, lblReservationId, lblAmount, lblDate;
    private JTextField txtPaymentId, txtReservationId, txtAmount, txtDate;
    private JTextArea txtReceipt;
    private JButton btnPay, btnReceipt, btnClear, btnBack;
    private JTable payTable;
    private DefaultTableModel tableModel;

    public PaymentManagement() {
        setTitle("Payment Management");
        setSize(950, 600);
        setLayout(null);
        getContentPane().setBackground(new Color(240, 248, 255));

        JLabel title = new JLabel("PAYMENT FORM");
        title.setBounds(280, 15, 400, 40);
        title.setFont(new Font("Verdana", Font.BOLD, 22));
        title.setForeground(new Color(0, 102, 204));
        title.setHorizontalAlignment(SwingConstants.CENTER);

        lblPaymentId = new JLabel("Payment ID (Auto)");
        lblPaymentId.setBounds(40, 80, 150, 25);
        txtPaymentId = new JTextField();
        txtPaymentId.setBounds(200, 80, 200, 30);
        txtPaymentId.setEditable(false);

        lblReservationId = new JLabel("Reservation ID *");
        lblReservationId.setBounds(40, 130, 150, 25);
        txtReservationId = new JTextField();
        txtReservationId.setBounds(200, 130, 200, 30);

        lblAmount = new JLabel("Amount Paid *");
        lblAmount.setBounds(40, 180, 150, 25);
        txtAmount = new JTextField();
        txtAmount.setBounds(200, 180, 200, 30);

        lblDate = new JLabel("Payment Date");
        lblDate.setBounds(40, 230, 150, 25);
        
        // Auto-fill today's date
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        txtDate = new JTextField(sdf.format(new Date()));
        txtDate.setBounds(200, 230, 200, 30);

        // Buttons
        btnPay = new JButton("Pay");
        btnPay.setBounds(40, 290, 110, 35);
        btnPay.setBackground(new Color(0, 153, 76));
        btnPay.setForeground(Color.WHITE);

        btnReceipt = new JButton("Save Receipt");
        btnReceipt.setBounds(160, 290, 120, 35);
        btnReceipt.setBackground(new Color(0, 102, 204));
        btnReceipt.setForeground(Color.WHITE);

        btnClear = new JButton("Clear");
        btnClear.setBounds(290, 290, 110, 35);
        btnClear.setBackground(new Color(128, 128, 128));
        btnClear.setForeground(Color.WHITE);

        btnBack = new JButton("Back");
        btnBack.setBounds(160, 340, 120, 35);
        btnBack.setBackground(new Color(64, 64, 64));
        btnBack.setForeground(Color.WHITE);

        // Receipt text area
        txtReceipt = new JTextArea();
        txtReceipt.setEditable(false);
        txtReceipt.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane receiptScroll = new JScrollPane(txtReceipt);
        receiptScroll.setBounds(40, 395, 360, 140);
        receiptScroll.setBorder(BorderFactory.createTitledBorder("Receipt Preview"));

        // Table
        String[] columns = {"Payment ID", "Res ID", "Amount Paid", "Date"};
        tableModel = new DefaultTableModel(columns, 0);
        payTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(payTable);
        scrollPane.setBounds(430, 80, 475, 455);

        add(title);
        add(lblPaymentId); add(txtPaymentId);
        add(lblReservationId); add(txtReservationId);
        add(lblAmount); add(txtAmount);
        add(lblDate); add(txtDate);
        add(btnPay); add(btnReceipt); add(btnClear); add(btnBack);
        add(receiptScroll);
        add(scrollPane);

        // Action Listeners
        btnPay.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                processPayment();
            }
        });

        btnReceipt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveReceiptToFile();
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

        payTable.getSelectionModel().addListSelectionListener(e -> {
            int selectedRow = payTable.getSelectedRow();
            if (selectedRow != -1) {
                txtPaymentId.setText(tableModel.getValueAt(selectedRow, 0).toString());
                txtReservationId.setText(tableModel.getValueAt(selectedRow, 1).toString());
                txtAmount.setText(tableModel.getValueAt(selectedRow, 2).toString());
                txtDate.setText(tableModel.getValueAt(selectedRow, 3).toString());
                
                generateReceiptPreview();
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
             ResultSet rs = stmt.executeQuery("SELECT * FROM payments")) {
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("payment_id"),
                        rs.getInt("reservation_id"),
                        rs.getDouble("amount"),
                        rs.getString("payment_date")
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading payments: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void processPayment() {
        String resIdStr = txtReservationId.getText().trim();
        String amountStr = txtAmount.getText().trim();
        String date = txtDate.getText().trim();

        if (resIdStr.isEmpty() || amountStr.isEmpty() || date.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all mandatory fields.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!date.matches("\\d{4}-\\d{2}-\\d{2}")) {
            JOptionPane.showMessageDialog(this, "Date must be in YYYY-MM-DD format.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
            if (amount <= 0) {
                JOptionPane.showMessageDialog(this, "Amount must be positive.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Amount must be a valid number.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            int reservationId = Integer.parseInt(resIdStr);

            // Verify reservation ID exists
            try (PreparedStatement pstmt = conn.prepareStatement("SELECT reservation_id FROM reservations WHERE reservation_id = ?")) {
                pstmt.setInt(1, reservationId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (!rs.next()) {
                        JOptionPane.showMessageDialog(this, "Reservation ID does not exist.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                }
            }

            // Insert payment
            int generatedId = -1;
            try (PreparedStatement pstmt = conn.prepareStatement(
                    "INSERT INTO payments (reservation_id, amount, payment_date) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setInt(1, reservationId);
                pstmt.setDouble(2, amount);
                pstmt.setString(3, date);
                pstmt.executeUpdate();
                
                try (ResultSet keys = pstmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        generatedId = keys.getInt(1);
                    }
                }
            }

            JOptionPane.showMessageDialog(this, "Payment processed successfully!");
            txtPaymentId.setText(String.valueOf(generatedId));
            
            refreshTable();
            generateReceiptPreview();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Reservation ID must be an integer.", "Validation Error", JOptionPane.WARNING_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Database Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void generateReceiptPreview() {
        String payId = txtPaymentId.getText();
        String resId = txtReservationId.getText();
        String amount = txtAmount.getText();
        String date = txtDate.getText();

        if (payId.isEmpty()) {
            txtReceipt.setText("");
            return;
        }

        // Fetch customer name and room number for the receipt
        String customerName = "Unknown";
        String roomNumber = "Unknown";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT u.name, rm.room_number FROM reservations r " +
                     "JOIN users u ON r.customer_id = u.id " +
                     "JOIN rooms rm ON r.room_id = rm.room_id " +
                     "WHERE r.reservation_id = ?")) {
            pstmt.setInt(1, Integer.parseInt(resId));
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    customerName = rs.getString("name");
                    roomNumber = rs.getString("room_number");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        StringBuilder sb = new StringBuilder();
        sb.append("====================================\n");
        sb.append("         HOTEL RESERVATION SYSTEM   \n");
        sb.append("             PAYMENT RECEIPT        \n");
        sb.append("====================================\n");
        sb.append("Receipt ID    : ").append(payId).append("\n");
        sb.append("Reservation ID: ").append(resId).append("\n");
        sb.append("Customer Name : ").append(customerName).append("\n");
        sb.append("Room Number   : ").append(roomNumber).append("\n");
        sb.append("Amount Paid   : PKRs ").append(amount).append("\n");
        sb.append("Date          : ").append(date).append("\n");
        sb.append("====================================\n");
        sb.append("      Thank you for your stay!      \n");
        sb.append("====================================\n");

        txtReceipt.setText(sb.toString());
    }

    private void saveReceiptToFile() {
        String receiptText = txtReceipt.getText();
        if (receiptText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No receipt generated yet. Perform a payment or select one.", "Receipt Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String fileName = "Receipt_" + txtPaymentId.getText() + ".txt";
        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write(receiptText);
            JOptionPane.showMessageDialog(this, "Receipt saved successfully as " + fileName);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error saving receipt: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void clearFields() {
        txtPaymentId.setText("");
        txtReservationId.setText("");
        txtAmount.setText("");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        txtDate.setText(sdf.format(new Date()));
        txtReceipt.setText("");
        payTable.clearSelection();
    }
}
