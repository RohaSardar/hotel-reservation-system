package model;

public class Payment {
    private int paymentId;
    private int reservationId;
    private double amount;
    private String paymentDate;

    public Payment() {
    }

    public Payment(int reservationId, double amount, String paymentDate) {
        this.reservationId = reservationId;
        this.amount = amount;
        this.paymentDate = paymentDate;
    }

    public Payment(int paymentId, int reservationId, double amount, String paymentDate) {
        this.paymentId = paymentId;
        this.reservationId = reservationId;
        this.amount = amount;
        this.paymentDate = paymentDate;
    }

    public int getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(int paymentId) {
        this.paymentId = paymentId;
    }

    public int getReservationId() {
        return reservationId;
    }

    public void setReservationId(int reservationId) {
        this.reservationId = reservationId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(String paymentDate) {
        this.paymentDate = paymentDate;
    }
}
