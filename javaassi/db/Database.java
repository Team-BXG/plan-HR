package com.example.javaassi.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {

    private static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/employee_db";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Gembele1416@";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }
}
