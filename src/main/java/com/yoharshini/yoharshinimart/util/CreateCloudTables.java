package com.yoharshini.yoharshinimart.util;

import java.sql.Connection;
import java.sql.Statement;

public class CreateCloudTables {
    public static void main(String[] args) {
        try {
            Connection con = DatabaseConnection.getConnection();
            
            if (con != null) {
                Statement stmt = con.createStatement();
                
                // பழைய அட்டவணைகளை வரிசையாக நீக்குதல் (Foreign key constraints காரணமாக மாற்று வரிசை)
                stmt.executeUpdate("DROP TABLE IF EXISTS reviews");
                stmt.executeUpdate("DROP TABLE IF EXISTS cart_items");
                stmt.executeUpdate("DROP TABLE IF EXISTS order_items");
                stmt.executeUpdate("DROP TABLE IF EXISTS orders");
                stmt.executeUpdate("DROP TABLE IF EXISTS products");
                stmt.executeUpdate("DROP TABLE IF EXISTS users");
                
                // 1. Users Table
                stmt.executeUpdate("CREATE TABLE users ("
                        + "id INT AUTO_INCREMENT PRIMARY KEY, "
                        + "name VARCHAR(100) NOT NULL, "
                        + "email VARCHAR(100) NOT NULL UNIQUE, "
                        + "password_hash VARCHAR(255) NOT NULL, "
                        + "role ENUM('BUYER', 'SELLER', 'ADMIN') NOT NULL, "
                        + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
                        + ")");

                // 2. Products Table
                stmt.executeUpdate("CREATE TABLE products ("
                        + "id INT AUTO_INCREMENT PRIMARY KEY, "
                        + "seller_id INT NOT NULL, "
                        + "name VARCHAR(150) NOT NULL, "
                        + "description TEXT, "
                        + "price DECIMAL(10,2) NOT NULL, "
                        + "stock_qty INT NOT NULL, "
                        + "category VARCHAR(100) NOT NULL, "
                        + "subcategory VARCHAR(100), "
                        + "image_url VARCHAR(255), "
                        + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
                        + "FOREIGN KEY (seller_id) REFERENCES users(id) ON DELETE CASCADE"
                        + ")");

                // 3. Orders Table
                stmt.executeUpdate("CREATE TABLE orders ("
                        + "id INT AUTO_INCREMENT PRIMARY KEY, "
                        + "buyer_id INT NOT NULL, "
                        + "status ENUM('PENDING', 'CONFIRMED', 'SHIPPED', 'DELIVERED', 'CANCELLED') DEFAULT 'PENDING', "
                        + "total_amount DECIMAL(10,2) NOT NULL, "
                        + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
                        + "FOREIGN KEY (buyer_id) REFERENCES users(id) ON DELETE CASCADE"
                        + ")");

                // 4. Order Items Table
                stmt.executeUpdate("CREATE TABLE order_items ("
                        + "id INT AUTO_INCREMENT PRIMARY KEY, "
                        + "order_id INT NOT NULL, "
                        + "product_id INT NOT NULL, "
                        + "quantity INT NOT NULL, "
                        + "unit_price DECIMAL(10,2) NOT NULL, "
                        + "FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE, "
                        + "FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE"
                        + ")");

                // 5. Cart Items Table
                stmt.executeUpdate("CREATE TABLE cart_items ("
                        + "id INT AUTO_INCREMENT PRIMARY KEY, "
                        + "user_id INT NOT NULL, "
                        + "product_id INT NOT NULL, "
                        + "quantity INT NOT NULL, "
                        + "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE, "
                        + "FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE"
                        + ")");

                // 6. Reviews Table
                stmt.executeUpdate("CREATE TABLE reviews ("
                        + "id INT AUTO_INCREMENT PRIMARY KEY, "
                        + "product_id INT NOT NULL, "
                        + "user_id INT NOT NULL, "
                        + "rating INT CHECK (rating >= 1 AND rating <= 5), "
                        + "comment TEXT, "
                        + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
                        + "FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE, "
                        + "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE"
                        + ")");

                System.out.println("SUCCESS: All e-commerce tables created successfully in Aiven Cloud!");
                con.close();
            } else {
                System.out.println("ERROR: Failed to make connection to the database!");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}