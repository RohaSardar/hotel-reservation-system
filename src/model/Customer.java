package model;

public class Customer extends Person {
    private int customerId;

    // Constructor Overloading 1: Default constructor
    public Customer() {
        super();
    }

    // Constructor Overloading 2: Without customer ID (useful for new customer registration before database insertion)
    public Customer(String name, String email, String phone, String address) {
        super(name, email, phone, address);
    }

    // Constructor Overloading 3: With customer ID
    public Customer(int customerId, String name, String email, String phone, String address) {
        super(name, email, phone, address);
        this.customerId = customerId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    // Method Overriding (Polymorphism)
    @Override
    public String getRole() {
        return "Customer";
    }

    @Override
    public String toString() {
        return "Customer ID: " + customerId + ", " + super.toString();
    }
}
