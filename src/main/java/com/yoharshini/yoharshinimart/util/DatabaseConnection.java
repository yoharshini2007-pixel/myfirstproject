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

            String url;
            if (host != null && !host.isEmpty()) {
                url = "jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=true&trustServerCertificate=true&allowPublicKeyRetrieval=true";
            } else {
                url = "jdbc:mysql://localhost:3306/yoharshinimart?useSSL=false&allowPublicKeyRetrieval=true";
                username = "root";
                password = "";
            }

            con = DriverManager.getConnection(url, username, password);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return con;
    }
}