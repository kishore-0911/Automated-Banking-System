import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.SQLException;

public class DatabaseSetup {
    private static final String SERVER_URL = "jdbc:mysql://localhost:3306/";
    private static final String USER = "root";
    private static final String PASSWORD = "Kishore@lh44";

    public static void main(String[] args) {
        System.out.println("Starting Database Initialization...");

        try {
            // Load Driver
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Error: MySQL JDBC Driver not found!");
            e.printStackTrace();
            return;
        }

        // 1. Connect to MySQL server and create database if not exists
        try (Connection conn = DriverManager.getConnection(SERVER_URL, USER, PASSWORD);
             Statement stmt = conn.createStatement()) {
            
            System.out.println("Connected to MySQL server. Creating database 'banking_db' if it does not exist...");
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS banking_db");
            System.out.println("Database 'banking_db' created/verified successfully.");
            
        } catch (SQLException e) {
            System.err.println("Error connecting to MySQL server or creating database:");
            e.printStackTrace();
            return;
        }

        // 2. Connect to the banking_db database and create tables
        String dbUrl = SERVER_URL + "banking_db";
        try (Connection conn = DriverManager.getConnection(dbUrl, USER, PASSWORD);
             Statement stmt = conn.createStatement()) {
            
            System.out.println("Connected to 'banking_db'. Creating tables...");

            String createAccountsTable = "CREATE TABLE IF NOT EXISTS accounts (" +
                    "account_id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "account_number VARCHAR(50) UNIQUE NOT NULL, " +
                    "account_holder_name VARCHAR(100) NOT NULL, " +
                    "email VARCHAR(100) NOT NULL, " +
                    "balance DECIMAL(15, 2) NOT NULL, " +
                    "account_type VARCHAR(20) NOT NULL, " +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "is_active BOOLEAN DEFAULT TRUE" +
                    ")";
            stmt.executeUpdate(createAccountsTable);
            System.out.println("Table 'accounts' verified/created.");

            String createTransactionsTable = "CREATE TABLE IF NOT EXISTS transactions (" +
                    "transaction_id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "from_account VARCHAR(50), " +
                    "to_account VARCHAR(50), " +
                    "amount DECIMAL(15, 2) NOT NULL, " +
                    "transaction_type VARCHAR(20) NOT NULL, " +
                    "description VARCHAR(255), " +
                    "transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "status VARCHAR(20) NOT NULL, " +
                    "FOREIGN KEY (from_account) REFERENCES accounts(account_number) ON DELETE SET NULL, " +
                    "FOREIGN KEY (to_account) REFERENCES accounts(account_number) ON DELETE SET NULL" +
                    ")";
            stmt.executeUpdate(createTransactionsTable);
            System.out.println("Table 'transactions' verified/created.");

            System.out.println("Database Initialization completed successfully!");

        } catch (SQLException e) {
            System.err.println("Error creating tables:");
            e.printStackTrace();
        }
    }
}
