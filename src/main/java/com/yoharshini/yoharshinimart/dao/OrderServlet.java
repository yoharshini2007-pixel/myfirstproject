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

@WebServlet("/OrderServlet")
public class OrderServlet extends HttpServlet {

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

        double total = 0;

        String sql =
                "SELECT SUM(c.quantity * p.price) AS total " +
                "FROM cart_items c " +
                "JOIN products p ON c.product_id = p.id " +
                "WHERE c.user_id = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                total = rs.getDouble("total");
            }

            if (total <= 0) {
                response.setContentType("text/html");
                PrintWriter out = response.getWriter();

                out.println("<h3>Your cart is empty!</h3>");
                out.println("<a href='CartViewServlet'>Back to Cart</a>");

                return;
            }

            response.setContentType("text/html");
            PrintWriter out = response.getWriter();

            out.println("<html>");
            out.println("<head>");
            out.println("<title>Order Confirmation</title>");
            out.println("</head>");

            out.println("<body>");

            out.println("<h1>Yoharshinimart - Order Confirmation</h1>");

            out.println("<h2>Order Summary</h2>");
            out.println("<p>Total Amount: ₹" + total + "</p>");

            out.println("<form action='PaymentServlet' method='post'>");

            out.println("<input type='hidden' name='amount' value='" + total + "'>");

            out.println("<h3>Select Payment Method</h3>");

            out.println("<input type='radio' name='paymentMethod' value='COD' required>");
            out.println(" Cash on Delivery<br><br>");

            out.println("<input type='radio' name='paymentMethod' value='UPI'>");
            out.println(" UPI<br><br>");

            out.println("<input type='radio' name='paymentMethod' value='CARD'>");
            out.println(" Card<br><br>");

            out.println("<button type='submit'>Continue</button>");

            out.println("</form>");

            out.println("<br>");
            out.println("<a href='CartViewServlet'>Back to Cart</a>");

            out.println("</body>");
            out.println("</html>");

        } catch (Exception e) {

            e.printStackTrace();

            response.setContentType("text/html");

            PrintWriter out = response.getWriter();

            out.println("<h3>Error processing order.</h3>");
            out.println("<p>" + e.getMessage() + "</p>");
        }
    }
}