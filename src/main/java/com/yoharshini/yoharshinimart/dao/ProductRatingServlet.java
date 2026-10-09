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

@WebServlet("/ProductRatingServlet")
public class ProductRatingServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String sql =
            "SELECT product_id, " +
            "ROUND(AVG(rating), 1) AS average_rating, " +
            "COUNT(*) AS total_reviews " +
            "FROM reviews " +
            "GROUP BY product_id";

        PrintWriter out = response.getWriter();

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            out.print("{");

            boolean first = true;

            while (rs.next()) {

                if (!first) {
                    out.print(",");
                }

                int productId = rs.getInt("product_id");
                double averageRating = rs.getDouble("average_rating");
                int totalReviews = rs.getInt("total_reviews");

                out.print("\"" + productId + "\":{");
                out.print("\"averageRating\":" + averageRating + ",");
                out.print("\"totalReviews\":" + totalReviews);
                out.print("}");

                first = false;
            }

            out.print("}");

        } catch (Exception e) {

            e.printStackTrace();

            response.setStatus(
                HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            );

            out.print("{}");
        }
    }
}