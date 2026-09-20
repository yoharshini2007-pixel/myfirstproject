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

@WebServlet("/CartViewServlet")
public class CartViewServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();

        Object userIdObject = session.getAttribute("userId");

        if (userIdObject == null) {
            response.sendRedirect("index.html");
            return;
        }

        int userId = (Integer) userIdObject;

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        String sql =
            "SELECT c.quantity, p.name, p.price " +
            "FROM cart_items c " +
            "JOIN products p ON c.product_id = p.id " +
            "WHERE c.user_id = ?";

        double total = 0;

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);

            ResultSet rs = ps.executeQuery();

            out.println("<html><head>");
            out.println("<title>Yoharshinimart - Shopping Cart</title>");
            out.println("</head><body>");

            out.println("<h1>🛒 Yoharshinimart - Shopping Cart</h1>");
            out.println("<a href='buyer.html'>← Back to Home</a>");

            out.println("<h2>Your Cart Items</h2>");

            boolean hasItems = false;

            while (rs.next()) {

                hasItems = true;

                String name = rs.getString("name");
                double price = rs.getDouble("price");
                int quantity = rs.getInt("quantity");

                double itemTotal = price * quantity;
                total += itemTotal;

                out.println("<div style='border:1px solid #ddd; padding:15px; margin:10px;'>");
                out.println("<h3>" + name + "</h3>");
                out.println("<p>Price: ₹" + price + "</p>");
                out.println("<p>Quantity: " + quantity + "</p>");
                out.println("<p>Item Total: ₹" + itemTotal + "</p>");
                out.println("</div>");
            }

            if (!hasItems) {
                out.println("<p>Your cart is empty!</p>");
            }

            out.println("<h3>Total Amount: ₹" + total + "</h3>");

            if (hasItems) {
                out.println("<button>Confirm Order (Checkout)</button>");
            }

            out.println("</body></html>");

        } catch (Exception e) {
            e.printStackTrace();
            out.println("<h3>Error loading cart: " + e.getMessage() + "</h3>");
        }
    }
}