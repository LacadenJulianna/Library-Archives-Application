# 📚 Library Archives Application

The **Library Archives Application** is a Java-based system designed to manage a digital library archive. It supports two types of users: **Guest** and **Admin (Personnel)**. The system allows book cataloging, competition voting, and library data management using a structured MVC architecture and a relational database.

---

## 🚀 Features

### 👥 User Roles

#### 1. Guest User
- 🔍 Browse books available in the archive.
- 🗳️ Vote for books in ongoing competitions.

#### 2. Admin (Personnel)
- ➕ Add new books to the library.
- 🗑️ Delete books from the library.
- 📝 Update book information.
- 📤 Upload books and manage metadata.
- 🏆 Create and manage competitions.
- ✋ Start/Stop competitions.
- 🏅 Manage awards and nominees.

---

## 🗂️ Project Structure
library_archive_application/
│
├── .idea/                          # IDE configuration files
│
├── res/                            # Resource files
│   ├── .gitkeep                    # Placeholder for empty directory
│   ├── book_test.csv               # Sample book data in CSV
│   ├── book_test.txt               # Sample book data in text format
│   └── teambach.sql                # SQL script for database schema
│
├── src/
│   ├── databaseConnection/         # Database connectivity classes
│
│   ├── guest/                      # Guest user module
│   │   ├── model/                  # Guest logic (viewing, voting)
│   │   │   ├── GuestBookCatalogModel.java
│   │   │   ├── GuestDashboardModel.java
│   │   │   ├── RateManagerModel.java
│   │   │   └── VoteSelectionModel.java
│   │   └── view/                   # Guest GUI components
│
│   ├── login/                      # Login module
│
│   ├── personnel/                  # Admin (personnel) module
│   │   ├── model/                  # Admin logic (CRUD, competition)
│   │   │   ├── AwardManagerModel.java
│   │   │   ├── BookUploadModel.java
│   │   │   ├── CategoryManagerModel.java
│   │   │   ├── CompetitionManagerModel.java
│   │   │   ├── NomineesManagerModel.java
│   │   │   ├── PersonnelBookCatalogModel.java
│   │   │   └── PersonnelDashboardModel.java
│   │   └── view/                   # Admin GUI components
│
│   └── utilities/                  # Shared utility classes/functions
│
└── LibraryArchivesApplication.java # Main application entry point


---

## 🛠️ Technologies Used

- **Java (JDK 17+)**
- **Java Swing** (GUI)
- **MySQL** (Database)
- **JDBC** (Database connectivity)

---

## 🧪 Sample Data

The `/res/` directory includes:
- `book_test.csv` and `book_test.txt`: Sample book entries for testing.
- `teambach.sql`: SQL file to set up the MySQL database schema.

---

## 🧾 Setup Instructions

1. **Database Setup**
   - Import the `teambach.sql` into your MySQL server (e.g., via phpMyAdmin or CLI).
   - Update database credentials in the `databaseConnection` class.

2. **Run the Application**
   - Open the project in your preferred Java IDE (IntelliJ recommended).
   - Run `LibraryArchivesApplication.java`.

3. **Login**
   - Admins will log in through the login interface.
   - Guests can directly access the book catalog and competition.

---

## 📌 Notes

- Ensure MySQL is running before starting the application.
- Admin CRUD operations are fully functional and accessible after login.
- Guests do not need to log in but have limited permissions (viewing and voting only).

---
