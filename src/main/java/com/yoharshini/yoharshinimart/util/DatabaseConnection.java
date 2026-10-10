package com.yoharshini.yoharshinimart.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    public static Connection getConnection() {
        Connection con = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Render Environment Variables உடன் Local-க்கு Fallback வசதி
            String host = System.getenv("MYSQLHOST");
            if (host == null || host.isEmpty()) {
                host = "localhost";
            }

            String port = System.getenv("MYSQLPORT");
            if (port == null || port.isEmpty()) {
                port = "3306";
            }

            String database = System.getenv("MYSQLDATABASE");
            if (database == null || database.isEmpty()) {
                database = "yoharshinimart"; // உங்கள் டேட்டாபேஸ் பெயர்
            }

            String username = System.getenv("MYSQLUSER");
            if (username == null || username.isEmpty()) {
                username = "root";
            }

            String password = System.getenv("MYSQLPASSWORD");
            if (password == null) {
                password = ""; // உங்கள் லோக்கல் பாஸ்வேர்ட் (தேவைப்பட்டால் மாற்றிக் கொள்ளலாம்)
            }

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