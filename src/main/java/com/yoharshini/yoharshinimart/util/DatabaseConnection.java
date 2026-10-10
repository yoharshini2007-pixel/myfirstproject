package com.yoharshini.yoharshinimart.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    public static Connection getConnection() {
        Connection con = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            String host = System.getenv("MYSQLHOST");
            String port = System.getenv("MYSQLPORT");
            String database = System.getenv("MYSQLDATABASE");
            String username = System.getenv("MYSQLUSER");
            String password = System.getenv("MYSQLPASSWORD");

            String url = "jdbc:mysql://" + host + ":" + port
                    + "/" + database
                    + "?useSSL=false&allowPublicKeyRetrieval=true";

            con = DriverManager.getConnection(url, username, password);

        } catch (ClassNotFoundException e) {
            System.out.println("MySQL JDBC Driver not found!");
            e.printStackTrace();

        } catch (SQLException | RuntimeException e) {
            System.out.println("Database Connection Failed!");
            e.printStackTrace();
        }

        return con;
    }
}