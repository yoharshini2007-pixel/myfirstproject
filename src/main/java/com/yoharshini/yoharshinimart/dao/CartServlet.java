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
import javax.servlet.http.HttpSession;

import com.yoharshini.yoharshinimart.util.DatabaseConnection;

@WebServlet("/CartServlet")
public class CartServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();

        Object userIdObject = session.getAttribute("userId");

        if (userIdObject == null) {
            response.sendRedirect("index.html");
            return;
        }

        int userId = (Integer) userIdObject;

        int productId = Integer.parseInt(request.getParameter("productId"));

        String sql = "INSERT INTO cart_items (user_id, product_id, quantity) "
                   + "VALUES (?, ?, 1)";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, productId);

            ps.executeUpdate();

            response.setContentType("text/html");
            PrintWriter out = response.getWriter();

            out.println("<script>");
            out.println("alert('Product added to cart! 🛒');");
            out.println("window.location.href='cart.html';");
            out.println("</script>");

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("Error adding product to cart.");
        }
    }
}