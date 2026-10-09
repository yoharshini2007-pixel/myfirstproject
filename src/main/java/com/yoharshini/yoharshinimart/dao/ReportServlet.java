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

import com.yoharshini.yoharshinimart.util.DatabaseConnection;

@WebServlet("/ReportServlet")
public class ReportServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        int totalProducts = 0;
        int totalOrders = 0;
        int totalReviews = 0;
        int totalCustomers = 0;
        double revenue = 0;

        try (Connection con = DatabaseConnection.getConnection()) {

            // =========================================
            // PRODUCTS
            // Count products belonging to seller ID 2
            // =========================================

            String productSql =
                    "SELECT COUNT(*) FROM products";

            try (PreparedStatement ps =
                         con.prepareStatement(productSql);
                 ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    totalProducts = rs.getInt(1);
                }
            }


            // =========================================
            // ORDERS
            // Count all actual customer orders
            // =========================================

            String orderSql =
                    "SELECT COUNT(*) FROM orders";

            try (PreparedStatement ps =
                         con.prepareStatement(orderSql);
                 ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    totalOrders = rs.getInt(1);
                }
            }


            // =========================================
            // REVENUE
            // Actual revenue from orders
            // =========================================

            String revenueSql =
                    "SELECT COALESCE(SUM(total_amount), 0) FROM orders";

            try (PreparedStatement ps =
                         con.prepareStatement(revenueSql);
                 ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    revenue = rs.getDouble(1);
                }
            }


            // =========================================
            // REVIEWS
            // Actual reviews from database
            // =========================================

            String reviewSql =
                    "SELECT COUNT(*) FROM reviews";

            try (PreparedStatement ps =
                         con.prepareStatement(reviewSql);
                 ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    totalReviews = rs.getInt(1);
                }
            }


            // =========================================
            // CUSTOMERS
            // Actual BUYER count
            // =========================================

            String customerSql =
                    "SELECT COUNT(*) FROM users WHERE role = 'BUYER'";

            try (PreparedStatement ps =
                         con.prepareStatement(customerSql);
             ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    totalCustomers = rs.getInt(1);
                }
            }

        } catch (Exception e) {

            e.printStackTrace();
        }


        // =========================================
        // SEND ALL VALUES AS JSON
        // =========================================

        PrintWriter out = response.getWriter();

        out.print("{"
                + "\"totalProducts\":" + totalProducts + ","
                + "\"totalOrders\":" + totalOrders + ","
                + "\"totalCustomers\":" + totalCustomers + ","
                + "\"revenue\":" + revenue + ","
                + "\"totalReviews\":" + totalReviews
                + "}");
    }
}