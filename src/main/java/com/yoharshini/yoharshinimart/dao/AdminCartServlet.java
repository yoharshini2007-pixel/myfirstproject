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

@WebServlet("/AdminCartServlet")
public class AdminCartServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        Object role =
                request.getSession().getAttribute("role");

        if (role == null ||
            !"ADMIN".equalsIgnoreCase(role.toString())) {

            response.sendRedirect("index.html");
            return;
        }

        response.setContentType("text/html;charset=UTF-8");

        PrintWriter out = response.getWriter();

        String sql =
            "SELECT c.user_id, " +
            "u.name AS buyer_name, " +
            "p.name AS product_name, " +
            "p.price, " +
            "c.quantity, " +
            "(p.price * c.quantity) AS item_total " +
            "FROM cart_items c " +
            "JOIN users u ON c.user_id = u.id " +
            "JOIN products p ON c.product_id = p.id " +
            "ORDER BY c.user_id";

        double grandTotal = 0;
        boolean hasItems = false;

        try (Connection con =
                 DatabaseConnection.getConnection();
             PreparedStatement ps =
                 con.prepareStatement(sql);
             ResultSet rs =
                 ps.executeQuery()) {

            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Admin Cart</title>");

            out.println("<style>");
            out.println("body{font-family:Arial;background:#f4f4f4;padding:30px;}");
            out.println(".container{max-width:1000px;margin:auto;}");
            out.println(".card{background:white;padding:20px;margin:15px 0;border-radius:10px;box-shadow:0 2px 8px #ddd;}");
            out.println("h1{color:#173c33;}");
            out.println(".total{background:#173c33;color:white;padding:20px;border-radius:10px;font-size:22px;}");
            out.println("p{font-size:17px;}");
            out.println("a{display:inline-block;margin-bottom:20px;text-decoration:none;color:#173c33;font-weight:bold;}");
            out.println("</style>");

            out.println("</head>");
            out.println("<body>");

            out.println("<div class='container'>");

            out.println("<a href='home.html'>← Back to Admin Home</a>");

            out.println("<h1>🛒 Admin Cart</h1>");
            out.println("<h2>Buyers' Current Cart Items</h2>");

            while (rs.next()) {

                hasItems = true;

                int userId =
                    rs.getInt("user_id");

                String buyerName =
                    rs.getString("buyer_name");

                String productName =
                    rs.getString("product_name");

                double price =
                    rs.getDouble("price");

                int quantity =
                    rs.getInt("quantity");

                double itemTotal =
                    rs.getDouble("item_total");

                grandTotal += itemTotal;

                out.println("<div class='card'>");

                out.println("<h2>👤 Buyer: "
                        + buyerName + "</h2>");

                out.println("<p>Buyer ID: "
                        + userId + "</p>");

                out.println("<p>📦 Product: "
                        + productName + "</p>");

                out.println("<p>💰 Price: ₹"
                        + price + "</p>");

                out.println("<p>🔢 Quantity: "
                        + quantity + "</p>");

                out.println("<p>💵 Item Total: ₹"
                        + itemTotal + "</p>");

                out.println("</div>");
            }

            if (!hasItems) {

                out.println("<div class='card'>");

                out.println("<h2>Your cart is empty!</h2>");

                out.println(
                    "<p>No buyer currently has items in their cart.</p>"
                );

                out.println("</div>");
            }

            out.println("<div class='total'>");
            out.println("Total Cart Value: ₹" + grandTotal);
            out.println("</div>");

            out.println("</div>");

            out.println("</body>");
            out.println("</html>");

        } catch (Exception e) {

            e.printStackTrace();

            out.println(
                "<h3>Error loading admin cart: "
                + e.getMessage()
                + "</h3>"
            );
        }
    }
}