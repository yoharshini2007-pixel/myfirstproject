package com.yoharshini.yoharshinimart.dao;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.yoharshini.yoharshinimart.util.DatabaseConnection;

@WebServlet("/AddToCartServlet")
public class AddToCartServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");

        try {

            HttpSession session = request.getSession(false);

if (session == null) {
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    response.getWriter().println("Please login first.");
    return;
}

Object userIdObject = session.getAttribute("userId");
Object roleObject = session.getAttribute("role");

if (userIdObject == null || roleObject == null) {
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    response.getWriter().println("Please login first.");
    return;
}

if (!"BUYER".equalsIgnoreCase(roleObject.toString())) {
    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
    response.getWriter().println("Only buyers can add products to cart.");
    return;
}


            int userId =
                    Integer.parseInt(userIdObject.toString());

            String productIdValue = request.getParameter("productId");

int productId;

try {
    productId = Integer.parseInt(productIdValue);
} catch (NumberFormatException e) {
    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    response.getWriter().println("ERROR: Invalid product ID.");
    return;
}

if (productId <= 0) {
    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    response.getWriter().println("ERROR: Product ID must be positive.");
    return;
}

            String sql =
                    "INSERT INTO cart_items " +
                    "(user_id, product_id, quantity) " +
                    "VALUES (?, ?, 1)";

            try (
                Connection con =
                    DatabaseConnection.getConnection();

                PreparedStatement ps =
                    con.prepareStatement(sql)
            ) {

                ps.setInt(1, userId);
                ps.setInt(2, productId);

                int result =
                    ps.executeUpdate();

                if (result > 0) {
                    response.getWriter().println(
                        "Product added to cart"
                    );
                } else {
                    response.getWriter().println(
                        "Failed to add product"
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