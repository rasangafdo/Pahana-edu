package com.pahanaedu.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBConnection {
    private static final Logger logger = Logger.getLogger(DBConnection.class.getName());

    private static final String URL = "jdbc:mysql://localhost:3306/pahanaedu";
    private static final String USER = "root";
    private static final String PASSWORD = "1234";
    private static final String DRIVER_CLASS = "com.mysql.cj.jdbc.Driver";

    // Singleton instance of DbConnector
    private static DBConnection instance;

    // Single shared Connection object
    private Connection connection;

    // Private constructor to prevent external instantiation
    private DBConnection() {
        try {
            Class.forName(DRIVER_CLASS);
            this.connection = DriverManager.getConnection(URL, USER, PASSWORD);
            logger.info("Database connection established successfully.");
        } catch (ClassNotFoundException e) {
            logger.log(Level.SEVERE, "JDBC Driver not found!", e);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database connection failed!", e);
        }
    }

    // Public method to provide access to the singleton instance
    public static synchronized DBConnection getInstance() {
        if (instance == null) {
            instance = new DBConnection();
        }
        return instance;
    }

    // Method to get the shared connection
    public Connection getConnection() {
        try {
            // Check if connection is closed or invalid, then recreate
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                logger.info("Database connection re-established.");
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to re-establish database connection!", e);
        }
        return connection;
    }
}
