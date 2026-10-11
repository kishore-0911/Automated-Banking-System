# Automated Banking System

A full-stack banking application built with Java, MySQL, and a responsive web dashboard.

## Features

- Create savings, checking, and business accounts
- Deposit and withdraw funds
- Transfer funds between accounts
- View account balances and transaction history
- Store account and transaction data in MySQL
- REST API for banking operations
- Web interface built with HTML, CSS, and JavaScript

## Tech Stack

- **Backend:** Java, built-in `HttpServer`, JDBC
- **Frontend:** HTML5, CSS3, JavaScript
- **Database:** MySQL
- **JSON Processing:** Google Gson
- **Frontend Server:** Node.js
- **Backend Port:** `8080`
- **MySQL Port:** `3306`

## Project Structure

```text
Automated-Banking-System/
└── banking-app/
    ├── bin/
    ├── frontend/
    │   ├── index.html
    │   └── server.js
    ├── lib/
    │   ├── gson-2.10.1.jar
    │   └── mysql-connector-j-9.3.0.jar
    ├── src/
    │   ├── Account.java
    │   ├── AccountDAO.java
    │   ├── BankingAPI.java
    │   ├── BankingService.java
    │   ├── DatabaseConnection.java
    │   ├── DatabaseSetup.java
    │   ├── Transaction.java
    │   └── TransactionDAO.java
    └── .gitignore
```

## Getting Started

### Prerequisites

Install the following:

- Java JDK 11 or later
- MySQL Server
- Node.js

### 1. Clone the Repository

```bash
git clone https://github.com/kishore-0911/Automated-Banking-System.git
cd Automated-Banking-System/banking-app
```

### 2. Configure the Database

Open `src/DatabaseConnection.java` and `src/DatabaseSetup.java`.

Configure your MySQL connection details. The database URL is:

```java
jdbc:mysql://localhost:3306/banking_db
```

Update the username and password to match your local MySQL configuration.

### 3. Compile the Java Application

Run this command from the `banking-app` directory:

```bash
javac -cp "lib/*" -d bin src/*.java
```

### 4. Initialize the Database

**Windows:**

```powershell
java -cp "bin;lib/*" DatabaseSetup
```

**Linux/macOS:**

```bash
java -cp "bin:lib/*" DatabaseSetup
```

### 5. Start the Backend

Run the following command from the `banking-app` directory.

**Windows:**

```powershell
java -cp "bin;lib/*" BankingAPI
```

**Linux/macOS:**

```bash
java -cp "bin:lib/*" BankingAPI
```

The backend API runs at:

`http://localhost:8080`

### 6. Start the Frontend

Open another terminal:

```bash
cd frontend
node server.js
```

Open your browser at:

`http://localhost:3000`

Make sure MySQL is running before starting the application.

## REST API Documentation

Base URL: `http://localhost:8080/api`

| Method | Endpoint | Description |
|---|---|---|
| GET | `/accounts` | Retrieve all accounts |
| POST | `/accounts` | Create a new account |
| POST | `/deposit` | Deposit funds |
| POST | `/withdraw` | Withdraw funds |
| POST | `/transfer` | Transfer funds |
| GET | `/balance?accountNumber=...` | Retrieve account balance |
| GET | `/transactions?accountNumber=...` | Retrieve transaction history |

### Example: Create an Account

**Request**

```http
POST /api/accounts
Content-Type: application/json
```

```json
{
  "name": "John Doe",
  "email": "john.doe@example.com",
  "initialDeposit": 1000.00,
  "accountType": "SAVINGS"
}
```

**Example Response**

```json
{
  "accountNumber": "ACC123456789"
}
```

## Database

The application uses MySQL to store banking account and transaction data.

- **Database:** `banking_db`
- **MySQL Port:** `3306`
- **Backend API Port:** `8080`

## Learning Objectives

- Apply Java object-oriented programming concepts
- Understand REST API development
- Implement database connectivity using JDBC
- Organize code into service and data-access layers
- Practice frontend and backend integration
- Use Git and GitHub for version control
