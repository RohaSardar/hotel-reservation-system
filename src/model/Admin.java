package model;

public class Admin extends Person {
    private String username;
    private String password;

    public Admin() {
        super();
    }

    public Admin(String username, String password, String name, String email, String phone, String address) {
        super(name, email, phone, address);
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // Method Overriding (Polymorphism)
    @Override
    public String getRole() {
        return "Admin";
    }

    @Override
    public String toString() {
        return "Admin Username: " + username + ", " + super.toString();
    }
}
