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

@WebServlet("/AddReviewServlet")
public class AddReviewServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("userId") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Please login before submitting a review.");
            return;
        }

        int userId;
        int productId;
        int rating;

        try {
            userId = Integer.parseInt(
                String.valueOf(session.getAttribute("userId"))
            );
            productId = Integer.parseInt(request.getParameter("productId"));
            rating = Integer.parseInt(request.getParameter("rating"));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Invalid review information.");
            return;
        }

        String comment = request.getParameter("comment");

        if (productId <= 0 || rating < 1 || rating > 5) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Choose a rating from 1 to 5.");
            return;
        }

        if (comment == null || comment.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Please write a comment.");
            return;
        }

        String sql = "INSERT INTO reviews "
                   + "(product_id, user_id, rating, comment) "
                   + "VALUES (?, ?, ?, ?)";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, productId);
            ps.setInt(2, userId);
            ps.setInt(3, rating);
            ps.setString(4, comment.trim());

            int rows = ps.executeUpdate();

            if (rows > 0) {
                response.sendRedirect(
    "ProductDetailsServlet?id=" + productId + "&review=success"
);
return;
            } else {
                response.setStatus(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR
                );
                response.getWriter().write("Review could not be saved.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(
                HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            );
            response.getWriter().write("Error saving review.");
        }
    }
}