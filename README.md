# 🏨 Hotel Reservation System

## Student Information
| Field | Details |
|---|---|
| **Student Name** | Roha Sardar |
| **Roll Number** | l1f23bsse0410 |
| **Course** | Software Construction & Development |
| **Project Phase** | Phase 2 – Final Submission |

---

## 📋 Project Description

The **Hotel Reservation System** is a complete Java desktop application built using **Java Swing** for the GUI and **SQLite** for database connectivity via JDBC. The system allows hotel administrators to manage rooms, customers, reservations, and payments, while customers can browse available rooms and book them directly.

---

## ✨ Features

### 🔐 Admin Panel
- Secure **Login System** with role-based access (Admin / Customer)
- **Customer Management** – Add, Update, Delete, Search customers (CRUD)
- **Room Management** – Add, Update, Delete, Search rooms (CRUD)
- **Reservation Management** – Create, update, and cancel bookings; room status auto-updates
- **Payment Management** – Record payments, generate and save receipts as text files
- **System Reports** – Tabbed views of all Customers, Rooms, Reservations, and Payments

### 👤 Customer Panel
- **Browse & Book Rooms** – Search by room type, select and book with check-in/check-out dates
- **My Reservations** – View personal booking history
- **My Profile** – Update email, phone, and address
- **Registration Form** – New customers can self-register

---

## 🛠 Technologies Used

| Component | Technology |
|---|---|
| **Language** | Java (Core Java + OOP) |
| **Frontend / GUI** | Java Swing |
| **Database** | SQLite (file-based, bundled) |
| **Database Connectivity** | JDBC (`sqlite-jdbc-3.45.2.0.jar`) |
| **IDE** | IntelliJ IDEA / Eclipse |
| **Version Control** | Git + GitHub |

---

## 🗂 OOP Concepts Demonstrated

- **Abstraction** – Abstract class `Person` with abstract method `getRole()`
- **Inheritance** – `Customer` and `Admin` extend `Person`
- **Encapsulation** – Private fields with public getters/setters in all model classes
- **Polymorphism** – Method overriding of `getRole()` and `toString()`
- **Constructor Overloading** – Multiple constructors in `Customer`, `Room`, `Reservation`, `Payment`
- **Classes & Objects** – Full OOP design with model, database, and GUI layers

---

## 📁 Project Structure

```
projectlab/
├── src/
│   ├── Main.java                       ← App entry point
│   ├── database/
│   │   └── DatabaseConnection.java     ← JDBC SQLite connection & schema
│   ├── model/
│   │   ├── Person.java                 ← Abstract base class
│   │   ├── Customer.java               ← Inherits Person
│   │   ├── Admin.java                  ← Inherits Person
│   │   ├── Room.java                   ← Room model
│   │   ├── Reservation.java            ← Reservation model
│   │   └── Payment.java                ← Payment model
│   └── gui/
│       ├── LoginForm.java
│       ├── AdminDashboard.java
│       ├── CustomerDashboard.java
│       ├── CustomerRegistrationForm.java
│       ├── CustomerManagement.java
│       ├── RoomManagement.java
│       ├── ReservationManagement.java
│       ├── PaymentManagement.java
│       ├── ReportsForm.java
│       └── RoomBrowsing.java
├── lib/
│   └── sqlite-jdbc.jar                 ← SQLite JDBC driver
├── database/                           ← Auto-created hotel.db goes here
├── docs/
│   ├── Name Roha Sardar.docx          ← Phase 1 Proposal
│   ├── Report.docx                    ← Final Documentation Report
│   └── Presentation.pptx             ← Presentation Slides
├── run.bat                             ← One-click compile & run script
└── README.md
```

---

## 🚀 Installation & Running Steps

### Prerequisites
1. Install **Java JDK 8 or higher** from [https://www.oracle.com/java/technologies/downloads/](https://www.oracle.com/java/technologies/downloads/)
2. Make sure `javac` and `java` are in your system PATH

### Run the Application

#### Option 1 – Double-click (Windows)
Simply double-click **`run.bat`** in the project folder. It will:
- Create the `bin/` directory
- Compile all Java source files
- Launch the application

#### Option 2 – Command Line
```bash
# Compile
javac -cp "lib/sqlite-jdbc.jar" src/database/*.java src/model/*.java src/gui/*.java src/Main.java -d bin

# Run
java -cp "bin;lib/sqlite-jdbc.jar" Main
```

---

## 🔑 Default Login Credentials

| Role | Username | Password |
|---|---|---|
| Admin | `admin` | `admin123` |
| Customer | `customer` | `customer123` |

> The database (`database/hotel.db`) is auto-created on first launch with these default accounts and 3 sample rooms.

---

## 🗄 Database Schema

**Tables:**
- `users` – Stores admin and customer login credentials + personal info
- `rooms` – Room catalog with type, price, and availability status
- `reservations` – Booking records with foreign keys to users and rooms
- `payments` – Payment transactions linked to reservations

---

## 🔮 Future Enhancements

- Generate PDF receipts and reports
- Add email notifications for booking confirmation
- Implement room image gallery
- Online/web-based version using Java Servlets
- Admin analytics dashboard with charts (occupancy rates, revenue)
- Password hashing for improved security
- Multiple hotel branch support

---

## 📸 Screenshots

> Screenshots of the running application are located in the `docs/` folder.

---

*Developed as a Software Construction & Development Semester Project*
