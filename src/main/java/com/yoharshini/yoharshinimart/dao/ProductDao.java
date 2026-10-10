
package com.yoharshini.yoharshinimart.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.yoharshini.yoharshinimart.model.Product;
import com.yoharshini.yoharshinimart.util.DatabaseConnection;

public class ProductDao {

    public boolean addProduct(Product product) {

        String query = "INSERT INTO products " +
                "(seller_id, name, description, price, stock_qty, category) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, product.getSellerId());
            pstmt.setString(2, product.getName());
            pstmt.setString(3, product.getDescription());
            pstmt.setDouble(4, product.getPrice());
            pstmt.setInt(5, product.getStockQty());
            pstmt.setString(6, product.getCategory());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Product> getAllProducts() {

        List<Product> products = new ArrayList<>();
        String query = "SELECT * FROM products";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {

                Product p = new Product();

                p.setId(rs.getInt("id"));
                p.setSellerId(rs.getInt("seller_id"));
                p.setName(rs.getString("name"));
                p.setDescription(rs.getString("description"));
                p.setPrice(rs.getDouble("price"));
                p.setStockQty(rs.getInt("stock_qty"));
                p.setCategory(rs.getString("category"));
                p.setSubCategory(rs.getString("subcategory"));
                p.setImageUrl(rs.getString("image_url"));
                p.setCreatedAt(rs.getTimestamp("created_at"));

                products.add(p);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return products;
    }

    public List<Product> getProductsBySeller(int sellerId) {

        List<Product> products = new ArrayList<>();

        String query = "SELECT * FROM products WHERE seller_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, sellerId);

            try (ResultSet rs = pstmt.executeQuery()) {

                while (rs.next()) {

                    Product p = new Product();

                    p.setId(rs.getInt("id"));
                    p.setSellerId(rs.getInt("seller_id"));
                    p.setName(rs.getString("name"));
                    p.setDescription(rs.getString("description"));
                    p.setPrice(rs.getDouble("price"));
                    p.setStockQty(rs.getInt("stock_qty"));
                    p.setCategory(rs.getString("category"));
                    p.setSubCategory(rs.getString("subcategory"));
                    p.setImageUrl(rs.getString("image_url"));
                    p.setCreatedAt(rs.getTimestamp("created_at"));

                    products.add(p);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return products;
    }

    
public boolean deleteProduct(int productId, int sellerId) {

    String query = "DELETE FROM products WHERE id = ? AND seller_id = ?";

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(query)) {

        pstmt.setInt(1, productId);
        pstmt.setInt(2, sellerId);

        return pstmt.executeUpdate() > 0;

    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}

    public boolean updateProduct(Product product) {

        String query = "UPDATE products SET " +
                "name = ?, description = ?, price = ?, " +
                "stock_qty = ?, category = ? " +
                "WHERE id = ? AND seller_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, product.getName());
            pstmt.setString(2, product.getDescription());
            pstmt.setDouble(3, product.getPrice());
            pstmt.setInt(4, product.getStockQty());
            pstmt.setString(5, product.getCategory());
            pstmt.setInt(6, product.getId());
            pstmt.setInt(7, product.getSellerId());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}