package com.example.pqchatclient.Model;

import com.example.pqchatclient.Utils.Encrypt;

import java.sql.*;

public class DatabaseDriver {
    // SQLite
    private Connection connection;
    public DatabaseDriver() {
        try{
            this.connection = DriverManager.getConnection("jdbc:sqlite:pqchat_database.db");

        }catch (Exception e){
            System.out.println("[LOG] >>> Database connection failed");
        }
    }
}
