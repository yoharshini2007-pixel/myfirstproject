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

@WebServlet("/PaymentServlet")
public class PaymentServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();

        Object userIdObject = session.getAttribute("userId");

        if (userIdObject == null) {
            response.sendRedirect("index.html");
            return;
        }

        int userId = (Integer) userIdObject;

        String paymentMethod = request.getParameter("paymentMethod");

        double total = 0;

        try (Connection con = DatabaseConnection.getConnection()) {

            // 1. Calculate cart total
            String totalSql =
                    "SELECT SUM(c.quantity * p.price) AS total " +
                    "FROM cart_items c " +
                    "JOIN products p ON c.product_id = p.id " +
                    "WHERE c.user_id = ?";

            try (PreparedStatement ps = con.prepareStatement(totalSql)) {

                ps.setInt(1, userId);

                try (ResultSet rs = ps.executeQuery()) {

                    if (rs.next()) {
                        total = rs.getDouble("total");
                    }
                }
            }

            if (total <= 0) {

                response.setContentType("text/html");

                PrintWriter out = response.getWriter();

                out.println("<h3>Your cart is empty!</h3>");
                out.println("<a href='CartViewServlet'>Back to Cart</a>");

                return;
            }

            // 2. Create order
            String orderSql =
                    "INSERT INTO orders (buyer_id, status, total_amount) " +
                    "VALUES (?, 'CONFIRMED', ?)";

            int orderId;

            try (PreparedStatement ps =
                         con.prepareStatement(orderSql, PreparedStatement.RETURN_GENERATED_KEYS)) {

                ps.setInt(1, userId);
                ps.setDouble(2, total);

                ps.executeUpdate();

                try (ResultSet rs = ps.getGeneratedKeys()) {

                    if (rs.next()) {
                        orderId = rs.getInt(1);
                    } else {
                        throw new Exception("Order ID could not be created.");
                    }
                }
            }

            // 3. Copy cart items into order_items
            String cartSql =
                    "SELECT product_id, quantity, p.price " +
                    "FROM cart_items c " +
                    "JOIN products p ON c.product_id = p.id " +
                    "WHERE c.user_id = ?";

            String itemSql =
                    "INSERT INTO order_items " +
                    "(order_id, product_id, quantity, unit_price) " +
                    "VALUES (?, ?, ?, ?)";

            try (PreparedStatement cartPs = con.prepareStatement(cartSql);
                 PreparedStatement itemPs = con.prepareStatement(itemSql)) {

                cartPs.setInt(1, userId);

                try (ResultSet rs = cartPs.executeQuery()) {

                    while (rs.next()) {

                        itemPs.setInt(1, orderId);
                        itemPs.setInt(2, rs.getInt("product_id"));
                        itemPs.setInt(3, rs.getInt("quantity"));
                        itemPs.setDouble(4, rs.getDouble("price"));

                        itemPs.executeUpdate();
                    }
                }
            }

            // 4. Clear cart after order is created
            String clearCartSql =
                    "DELETE FROM cart_items WHERE user_id = ?";

            try (PreparedStatement ps = con.prepareStatement(clearCartSql)) {

                ps.setInt(1, userId);

                ps.executeUpdate();
            }

            // 5. Show payment confirmation
            response.setContentType("text/html");

            PrintWriter out = response.getWriter();

            out.println("<html>");
            out.println("<head>");
            out.println("<title>Payment Confirmation</title>");
            out.println("</head>");

            out.println("<body>");

            out.println("<h1>Yoharshinimart - Payment Confirmation</h1>");

            out.println("<h2>Order Details</h2>");

            out.println("<p>Order ID: " + orderId + "</p>");
            out.println("<p>Total Amount: ₹" + total + "</p>");
            out.println("<p>Payment Method: " + paymentMethod + "</p>");

            out.println("<h3>Payment Successful!</h3>");

            out.println("<p>Your order has been placed successfully.</p>");

            out.println("<a href='buyer.html'>Continue Shopping</a>");

            out.println("</body>");
            out.println("</html>");

        } catch (Exception e) {

            e.printStackTrace();

            response.setContentType("text/html");

            PrintWriter out = response.getWriter();

            out.println("<h3>Error processing payment/order.</h3>");
            out.println("<p>" + e.getMessage() + "</p>");
        }
    }
}