package com.yoharshini.yoharshinimart.dao;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.yoharshini.yoharshinimart.util.DatabaseConnection;

@WebServlet("/AddProductServlet")
public class AddProductServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        try {
            // =========================
            // SELLER ID (Dynamic Handling)
            // =========================
            HttpSession session = request.getSession(true);
            int sellerId = 999; // Default fallback seller ID

            if (session != null && session.getAttribute("userId") != null) {
                try {
                    sellerId = Integer.parseInt(session.getAttribute("userId").toString());
                } catch (Exception e) {
                    sellerId = 999;
                }
            } else {
                session.setAttribute("userId", 999);
                session.setAttribute("role", "SELLER");
            }

            // =========================
            // PRODUCT DETAILS
            // =========================
            String name = request.getParameter("pname");
            String description = request.getParameter("pdescription");
            String category = request.getParameter("pcategory");
            String subCategory = request.getParameter("psubcategory");
            String price = request.getParameter("pprice");
            String stockQty = request.getParameter("pstock");
            String imageUrl = request.getParameter("imageUrl");

            // Null checks & Trimming
            if (name == null) name = "";
            if (description == null) description = "";
            if (category == null) category = "";
            if (subCategory == null) subCategory = "";
            if (price == null || price.trim().isEmpty()) price = "0";
            if (stockQty == null || stockQty.trim().isEmpty()) stockQty = "0";
            if (imageUrl == null) imageUrl = "";

            name = name.trim();
            description = description.trim();
            category = category.trim();
            subCategory = subCategory.trim();
            imageUrl = imageUrl.trim();

            if (name.isEmpty() || category.isEmpty()) {
                out.println("ERROR: Product name and category are required.");
                return;
            }

            double productPrice = Double.parseDouble(price.trim());
            int productStock = Integer.parseInt(stockQty.trim());

            // =========================
            // DATABASE CONNECTION & INSERT
            // =========================
            try (Connection conn = DatabaseConnection.getConnection()) {

                // 1. ENSURE SELLER EXISTS IN USERS TABLE (Fixes Foreign Key Error Automatically)
                String checkUserSql = "SELECT id FROM users WHERE id = ?";
                try (PreparedStatement checkPs = conn.prepareStatement(checkUserSql)) {
                    checkPs.setInt(1, sellerId);
                    try (ResultSet rs = checkPs.executeQuery()) {
                        if (!rs.next()) {
                            // User illana automatic-ah insert pannidum
                            String insertUserSql = "INSERT INTO users (id, name, email, password_hash, role) VALUES (?, 'Dynamic Seller', 'seller@gmail.com', '12345', 'SELLER')";
                            try (PreparedStatement insertPs = conn.prepareStatement(insertUserSql)) {
                                insertPs.setInt(1, sellerId);
                                insertPs.executeUpdate();
                            }
                        }
                    }
                }

                // 2. INSERT PRODUCT
                String sql = "INSERT INTO products " +
                             "(seller_id, name, description, price, stock_qty, " +
                             "category, subcategory, image_url) " +
                             "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setInt(1, sellerId);
                    ps.setString(2, name);
                    ps.setString(3, description);
                    ps.setDouble(4, productPrice);
                    ps.setInt(5, productStock);
                    ps.setString(6, category);
                    ps.setString(7, subCategory);
                    ps.setString(8, imageUrl);

                    int result = ps.executeUpdate();

                    if (result > 0) {
                        out.println("<h3>Product Added Successfully!</h3><a href='seller.html'>Add Another Product</a> | <a href='ProductServlet'>View Products</a>");
                    } else {
                        out.println("<h3>Product Add Failed</h3>");
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            out.println("ERROR: " + e.getMessage());
        }
    }
}