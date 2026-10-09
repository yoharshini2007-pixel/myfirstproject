package com.yoharshini.yoharshinimart.dao;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.yoharshini.yoharshinimart.util.DatabaseConnection;

@WebServlet("/UpdateOrderStatusServlet")
public class UpdateOrderStatusServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        String orderIdText = request.getParameter("orderId");
        String status = request.getParameter("status");

        response.setContentType("text/html;charset=UTF-8");

        PrintWriter out = response.getWriter();

        try {

            int orderId = Integer.parseInt(orderIdText);

            String sql =
                    "UPDATE orders SET status = ? WHERE id = ?";

            try (Connection con = DatabaseConnection.getConnection();
                 PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setString(1, status);
                ps.setInt(2, orderId);

                int rows = ps.executeUpdate();

                if (rows > 0) {

                    response.sendRedirect("SellerOrdersServlet");

                } else {

                    out.println("<h3>Order not found.</h3>");
                    out.println("<a href='SellerOrdersServlet'>Back to Orders</a>");
                }
            }

        } catch (Exception e) {

            e.printStackTrace();

            out.println("<h3>Error updating order status.</h3>");
            out.println("<p>" + e.getMessage() + "</p>");

            out.println("<br>");

            out.println(
                "<a href='SellerOrdersServlet'>Back to Orders</a>"
            );
        }
    }
}