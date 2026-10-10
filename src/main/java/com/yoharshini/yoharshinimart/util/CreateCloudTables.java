package com.yoharshini.yoharshinimart.util;

import java.sql.Connection;
import java.sql.Statement;

public class CreateCloudTables {
    public static void main(String[] args) {
        try {
            Connection con = DatabaseConnection.getConnection();
            
            if (con != null) {
                Statement stmt = con.createStatement();
                
                
                stmt.executeUpdate("DROP TABLE IF EXISTS users");
                
                String createUsersTable = "CREATE TABLE users ("
                        + "id INT AUTO_INCREMENT PRIMARY KEY, "
                        + "username VARCHAR(50) NOT NULL, "
                        + "password VARCHAR(255) NOT NULL, "
                        + "password_hash VARCHAR(255), "
                        + "email VARCHAR(100)"
                        + ")";

                stmt.executeUpdate(createUsersTable);
                System.out.println("SUCCESS: Users table recreated with password_hash in Aiven Cloud!");
                
                con.close();
            } else {
                System.out.println("ERROR: Failed to make connection to the database!");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}