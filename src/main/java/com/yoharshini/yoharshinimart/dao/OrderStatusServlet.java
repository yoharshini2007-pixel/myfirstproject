package com.yoharshini.yoharshinimart.dao;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.yoharshini.yoharshinimart.util.DatabaseConnection;

@WebServlet("/OrderStatusServlet")
public class OrderStatusServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");

        try {

            String orderId =
                    request.getParameter("orderId");

            String status =
                    request.getParameter("status");

            if (orderId == null || status == null ||
                orderId.trim().isEmpty() ||
                status.trim().isEmpty()) {

                response.getWriter().println(
                    "Invalid order details"
                );

                return;
            }

            String sql =
                    "UPDATE orders SET status = ? WHERE id = ?";

            try (Connection con =
                         DatabaseConnection.getConnection();
                 PreparedStatement ps =
                         con.prepareStatement(sql)) {

                ps.setString(1, status);
                ps.setInt(2, Integer.parseInt(orderId));

                int result = ps.executeUpdate();

                if (result > 0) {

                    response.getWriter().println(
                        "Order Status Updated Successfully"
                    );

                } else {

                    response.getWriter().println(
                        "Order not found"
                    );
                }
            }

        } catch (Exception e) {

            e.printStackTrace();

            response.getWriter().println(
                "ERROR: " + e.getMessage()
            );
        }
    }
}