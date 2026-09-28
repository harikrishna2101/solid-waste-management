# ♻️ Smart Waste Management System

A **Java Full Stack Web Application** developed using **Spring Boot**, **Spring Security**, **Spring Data JPA**, **Hibernate**, **Thymeleaf**, and **MySQL** to simplify municipal waste complaint management.

The system provides a centralized platform where citizens can report roadside garbage by uploading images and GPS location, municipal administrators can assign complaints to sanitation workers, and workers can update complaint statuses until completion.

---

## 🌟 Project Overview

Traditional waste complaint management is often slow, manual, and lacks transparency. This application digitizes the complete complaint lifecycle, enabling faster response and efficient communication between citizens, administrators, and sanitation workers.

The application supports:

- 👤 Citizens reporting waste complaints
- 👨‍💼 Municipal administrators reviewing and assigning complaints
- 👷 Sanitation workers updating complaint progress
- 📍 GPS-based location tracking
- 📷 Image upload for complaint verification
- ✅ Complaint status tracking from submission to completion

---

# ✨ Features

## 👤 Citizen Module

- User Registration & Login
- Secure Authentication
- Raise Garbage Complaints
- Upload Garbage Images
- GPS Location Capture
- Area & Landmark Details
- Track Complaint Status
- Complaint History
- Profile Management

---

## 👨‍💼 Admin Module

- Secure Login
- Dashboard Overview
- View All Complaints
- Manage Registered Citizens
- Search & Filter Complaints
- Assign Complaints to Workers
- Set Complaint Priority
- Monitor Complaint Progress
- Delete Complaints

---

## 👷 Worker Module

- Secure Login
- View Assigned Complaints
- View Complaint Details
- Update Complaint Status
- Upload Cleaning Completion Image
- Mark Complaints as Completed

---

# 🛠️ Technology Stack

## Frontend

- HTML5
- CSS3
- Bootstrap 5
- JavaScript
- Thymeleaf

---

## Backend

- Java 21
- Spring Boot
- Spring MVC
- Spring Security
- Spring Data JPA
- Hibernate

---

## Database

- MySQL

---

## Build Tool

- Maven

---

## IDE

- Visual Studio Code

---

## Version Control

- Git
- GitHub

---

# 📁 Project Structure

```text
Smart-Waste-Management-System
│
├── src
│   ├── main
│   │   ├── java
│   │   ├── resources
│   │   │   ├── static
│   │   │   ├── templates
│   │   │   └── application.properties
│
├── uploads
├── screenshots
├── schema.sql
├── pom.xml
└── README.md
```

---

# 🚀 Getting Started

## 1️⃣ Clone the Repository

```bash
git clone https://github.com/Shweta-naik197/Smart-Waste-Management-System.git
```

---

## 2️⃣ Navigate to Project

```bash
cd Smart-Waste-Management-System
```

---

## 3️⃣ Create Database

```sql
CREATE DATABASE smart_waste_management;
```

---

## 4️⃣ Configure Database

Open

```text
src/main/resources/application.properties
```

Update:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/smart_waste_management

spring.datasource.username=root

spring.datasource.password=your_password
```

---

## 5️⃣ Run the Application

Using Maven

```bash
mvn spring-boot:run
```

or run

```
SmartWasteApplication.java
```

from your IDE.

---

# 👥 User Roles

| Role | Responsibilities |
|------|------------------|
| Citizen | Register, Login, Raise Complaint, Track Status |
| Admin | Review Complaints, Assign Workers, Manage Users |
| Worker | Update Complaint Status, Upload Completion Image |

---

# 🔄 System Workflow

1. Citizen registers and logs into the portal.
2. Citizen uploads a garbage image.
3. GPS coordinates are captured automatically.
4. Complaint is submitted.
5. Admin reviews the complaint.
6. Admin assigns a sanitation worker.
7. Worker receives assigned complaint.
8. Worker cleans the waste.
9. Worker uploads completion image.
10. Complaint is marked as completed.
11. Citizen can track the completed status.

---

# 📸 Screenshots

## 🏠 Home Page

### Landing Page

![Home Page](screenshots/homepage.png)

### About Section

![About](screenshots/homepage-about.png)

### Features & Services

![Features](screenshots/homepage-features.png)

### System Workflow

![Workflow](screenshots/homepage-workflow.png)

### Contact Section

![Contact](screenshots/homepage-contact.png)

---

## 🔐 Authentication

### Citizen Login

![Login](screenshots/login-page.png)

### Admin Login

![Admin Login](screenshots/admin-login.png)

---

## 👤 Citizen Module

### Citizen Dashboard

![Citizen Dashboard](screenshots/citizen-dashboard.png)

### Raise Complaint

![Raise Complaint](screenshots/raise-complaint.png)

### Complaint History

![Complaint History](screenshots/citizen-complaints.png)

---

## 👨‍💼 Admin Module

### Admin Dashboard

![Admin Dashboard](screenshots/admin-dashboard.png)

### Manage Complaints

![Manage Complaints](screenshots/manage-complaints.png)

### Complaint Review

![Complaint Review](screenshots/complaint-review.png)

### Assign Worker

![Assign Worker](screenshots/assign-worker.png)

### Complaint Completed

![Completed Complaint](screenshots/completed-status.png)

---

# 🚀 Future Enhancements

- Google Maps Integration
- AI-Based Waste Classification
- Email Notifications
- SMS Notifications
- Push Notifications
- Mobile Application
- Real-Time Complaint Tracking
- Analytics Dashboard
- QR-Based Worker Verification
- Admin Report Generation
- Complaint Heatmap
- Cloud Image Storage

---

# 📚 Key Concepts Learned

- Java Full Stack Development
- Spring Boot
- Spring MVC
- Spring Security
- Spring Data JPA
- Hibernate ORM
- MVC Architecture
- CRUD Operations
- Role-Based Authentication
- Session Management
- File Upload Handling
- MySQL Integration
- Thymeleaf Template Engine
- Bootstrap UI Design
- Git & GitHub Version Control

---

# 💻 Installation Requirements

- Java 21
- Maven
- MySQL 8+
- Visual Studio Code / IntelliJ IDEA
- Git

---

# 🤝 Contributing

Contributions are welcome.

1. Fork the repository
2. Create a new branch

```bash
git checkout -b feature-name
```

3. Commit your changes

```bash
git commit -m "Added new feature"
```

4. Push the branch

```bash
git push origin feature-name
```

5. Open a Pull Request


# 👩‍💻 Developed By

## **Shweta Naik**

**Information Science & Engineering Student**

Canara Engineering College

📧 Email:shwetanaik1691@gmail.com

🔗 GitHub: https://github.com/Shweta-naik197

---
