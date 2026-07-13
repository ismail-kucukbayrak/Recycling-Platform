# ♻️ Recycling Platform

![Java](https://img.shields.io/badge/Java-Swing-orange)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Database-blue)
![JDBC](https://img.shields.io/badge/JDBC-Connectivity-green)
![Status](https://img.shields.io/badge/Status-Completed-brightgreen)

A desktop application developed using **Java (Swing)** and **PostgreSQL** for managing recycling processes.  
The system supports three different user roles: **Residents**, **Collector Companies**, and **Administrators**.

---

## 🚀 Features

### 👤 Resident
- Register and log in to the system
- Add recyclable waste (plastic, glass, electronic)
- View personal waste reports

### 🚛 Collector Company
- Register and log in
- View available waste in the warehouse
- Create pickup appointments

### 🛠️ Administrator
- View warehouse status
- List appointments
- View monthly total waste statistics
- List residents who added waste
- Search and delete residents
- Reset monthly waste records

---

### 🔹 Main Menu
The main menu serves as the entry point of the system, allowing users to navigate to role-based authentication screens for Residents, Collector Companies, and Administrators.

<img src="images/MainMenu.png" width="80%">

---

### 🔹 Waste Addition (Resident)
Residents can select the waste type and enter the amount to add recyclable materials to the system. Once submitted, the data is stored in the database and automatically reflected in the warehouse via triggers.

<img src="images/WasteAddition.png" width="90%">

---

### 🔹 Appointment Creation (Collector Company)
Collector companies can view the available waste in the warehouse and create pickup appointments by selecting the waste type, amount, and date. The system validates stock availability and updates the warehouse automatically after the appointment is created.

<img src="images/WasteAppointment.png" width="90%">

---

### 🔹 Admin Panel
The admin panel serves as the central control unit of the system, providing comprehensive management and monitoring capabilities. Administrators can view the current state of the warehouse, track and manage upcoming appointments, and access monthly waste statistics. In addition, the panel allows listing residents who have contributed waste, searching users by name, and removing them from the system if necessary. Administrative actions such as resetting monthly waste records are also supported. All operations are executed through database functions, ensuring data consistency and real-time updates across the system.

<img src="images/AuthorizedPanel.png" width="90%">

---

## 🗄️ Database Structure

### 📌 Tables
- `admins`
- `neighborhood_residents` (residents)
- `collector_companies`
- `warehouse`
- `appointments`

<img src="images/Tables.png" width="80%">

---

### ⚙️ Functions

The system heavily relies on PostgreSQL functions:

- Authentication functions  
- Registration functions  
- Waste insertion  
- Reporting  
- Appointment creation  
- Warehouse management (via triggers)  
- Admin operations  

<img src="images/Functions.png" width="90%">

---

## 🏗️ Project Structure

    recycling_platform/
    │
    ├── lib/
    │   └── postgresql-42.3.1.jar
    │
    ├── src/db/
    │   ├── MainScreen.java
    │   ├── DBConnection.java
    │   ├── NeighborhoodResidentLoginScreen.java
    │   ├── NeighborhoodResidentRegisterScreen.java
    │   ├── CollectorCompanyLoginScreen.java
    │   ├── CollectorCompanyRegisterScreen.java
    │   ├── AdminLoginScreen.java
    │
    ├── database.sql
    └── README.md

---

## ⚙️ Setup

1. Create a database using **pgAdmin**  
2. Run the `database.sql` file on your database  
3. Import the project into **Eclipse IDE**  
4. Update database credentials in `DBConnection.java`:

    private static final String URL = "jdbc:postgresql://localhost:5432/recycling_platform";
    private static final String USER = "postgres";
    private static final String PASSWORD = "your_password";

5. Run the project  

---

## 🔐 Admin Login

    Phone: 0
    Password: 0

---

## 🧠 Technical Details

- **Java Swing** → UI  
- **PostgreSQL** → Database  
- **JDBC** → Database connectivity  
- **PL/pgSQL** → Business logic (functions & triggers)  

### Trigger Logic
- When waste is added → warehouse is updated  
- When an appointment is created → warehouse stock decreases  

---

## 📌 Notes

- Core business logic is implemented in the database layer  
- Uses a **database-driven architecture**  
- PostgreSQL functions are central to the system  

---
