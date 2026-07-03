<h1 align="center">Customer Relationship Management System</h1>

<h3 align="center">A JavaFX desktop CRM and appointment-scheduling system with customer management, timezone-aware scheduling, and real-time reporting backed by MySQL.</h3>

<div align="center">
  
![Java](https://img.shields.io/badge/java-%23ED8B00?style=for-the-badge&logoColor=white)
![JavaFX](https://img.shields.io/badge/JavaFX-0078D7?style=for-the-badge&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-005C84?style=for-the-badge&logo=mysql&logoColor=white)
![MIT License](https://img.shields.io/badge/MIT%20License-gray?style=for-the-badge)

</div>

## Key Features

- User login with locale-aware English / French UI labels
- Timezone detection and display on the login screen
- Login attempt logging to `login_activity.txt`
- Full customer management: create, update, delete, and view customer records
- Appointment scheduling with customer, user, and contact assignment
- Business-hours validation (8:00 AM – 10:00 PM EST) automatically converted to the user's local timezone
- Customer double-booking prevention when creating or updating appointments
- Appointment views filtered by all, current week, or current month
- Automatic upcoming-appointment notification on login (alerts if an appointment starts within 15 minutes)
- Reporting dashboard with contact schedules, month/type totals, and today's appointment count
- Live clock displaying the user's current local time

> **Security Note:** This project was originally built as a University assignment. User passwords are currently stored and compared in plain text. For any production use, passwords should be hashed with a strong algorithm such as `bcrypt`, `Argon2`, or `PBKDF2`, and each password should include a unique salt.

## Tech Stack

- Java 11
- JavaFX 11
- Maven
- MySQL Connector/J 8.0.33

## Screenshots

### Login Screen
<img width="602" height="432" alt="loginScreen" src="https://github.com/user-attachments/assets/f7649d93-85b1-4dc3-9e7b-94b0dcaf532e" />

### Customer Management
<img width="1201" height="713" alt="customerManagement" src="https://github.com/user-attachments/assets/dd655afb-309b-4267-b8ce-7aa1888c781b" />

### Appointment Scheduling
<img width="1201" height="713" alt="appointmentScheduling" src="https://github.com/user-attachments/assets/a1975111-d885-4507-9048-8e54daf44050" />

### Reports
<img width="1201" height="713" alt="reports" src="https://github.com/user-attachments/assets/5e001f51-5eb6-4f07-8058-5f801743bee0" />

## Database Schema

The application uses a relational MySQL schema with six core tables:

| Table | Description |
|-------|-------------|
| `users` | Application login accounts |
| `countries` | Country catalog |
| `first_level_divisions` | States / provinces linked to countries |
| `customers` | Customer records linked to a division |
| `contacts` | Appointment contacts |
| `appointments` | Scheduled appointments linked to customers, users, and contacts |

<img width="940" height="705" alt="crmDBSchema" src="https://github.com/user-attachments/assets/cb1498dd-4d7c-48b6-9c28-1515cf565b20" />

## Core Modules

### Login
Validates credentials against the `users` table, detects the system locale to display English or French text, shows the user's timezone, and records every login attempt to `login_activity.txt`.

### Customers
Manage customer records with name, address, postal code, phone, country, and first-level division. Customers with existing appointments cannot be deleted until those appointments are removed.

### Appointments
Create and update appointments with title, description, location, type, date/time, customer, user, and contact. The form validates:
- All fields are populated
- Times are in `HH:mm` format
- Start time is before end time
- The appointment falls within business hours (8:00 AM – 10:00 PM EST) converted to local time
- The customer does not already have a conflicting appointment

### Reports
Generate three report views:
- Appointments by selected contact
- Total appointments for a selected month and type
- Total appointments scheduled for today

## Running It Locally

### Prerequisites

- Java 11 SDK
- Maven
- MySQL Server
- An IDE with JavaFX support (IntelliJ IDEA recommended)

### 1. Clone the Repository

```bash
git clone https://github.com/GoldRino456/Customer-Relationship-Management-System.git
```

### 2. Create the Database

Build the `users`, `countries`, `first_level_divisions`, `contacts`, `customers`, and `appointments` tables in your MySQL database.

### 3. Configure the Database Connection

Copy the example configuration file and fill in your MySQL credentials:

```bash
cp src/main/resources/config.properties.example src/main/resources/config.properties
```

Edit `config.properties`:

```properties
db.driver=com.mysql.cj.jdbc.Driver
db.url=jdbc:mysql://localhost:3306/YOUR_DATABASE_NAME
db.user=YOUR_USERNAME
db.password=YOUR_PASSWORD
```

### 4. Run the Application

```bash
mvn clean javafx:run
```

The login screen will open for the RBSS application.

## Project Architecture

```
Customer Relationship Management System/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── com/crmsystem/
│   │   │   │   ├── ScheduleApplication.java
│   │   │   │   ├── LoginController.java
│   │   │   │   ├── MainScreenController.java
│   │   │   │   ├── CustomerInfoController.java
│   │   │   │   ├── AppointmentInfoController.java
│   │   │   │   ├── Customer.java
│   │   │   │   ├── Appointment.java
│   │   │   │   ├── Country.java
│   │   │   │   ├── CustomerQuery.java
│   │   │   │   ├── AppointmentQuery.java
│   │   │   │   └── UserQuery.java
│   │   │   └── helper/
│   │   │       └── JDBC.java
│   │   └── resources/
│   │       ├── com/crmsystem/
│   │       │   ├── login-screen.fxml
│   │       │   ├── main-screen.fxml
│   │       │   ├── customerInfo-screen.fxml
│   │       │   └── appointmentInfo-screen.fxml
│   │       └── config.properties.example
│   └── test/
├── pom.xml
└── README.md
```

## Author

Developed By: **Ethan H. Eastwood**

- Website: [EthanEastwood.dev](https://ethaneastwood.dev)
- Github: [@GoldRino456](https://github.com/GoldRino456)
- LinkedIn: [@ethan-h-eastwood](https://linkedin.com/in/ethan-h-eastwood)

A special thank you to **Western Governors University** for the opportunity and support throughout this project.
