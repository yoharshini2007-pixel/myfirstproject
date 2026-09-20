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

@WebServlet("/AddProductServlet")
public class AddProductServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request,
                           HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        String sellerId = "2";
        System.out.println("ADD PRODUCT SERVLET CALLED");
        String name = request.getParameter("pname");
        String description = request.getParameter("pdescription");
        String category = request.getParameter("pcategory");
        String price = request.getParameter("pprice");
        String stockQty = request.getParameter("pstock");

        String sql = "INSERT INTO products " +
                     "(seller_id, name, description, price, stock_qty, category) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, Integer.parseInt(sellerId));
            ps.setString(2, name);
            ps.setString(3, description);
            ps.setDouble(4, Double.parseDouble(price));
            ps.setInt(5, Integer.parseInt(stockQty));
            ps.setString(6, category);

            int result = ps.executeUpdate();

            if (result > 0) {
                out.println("<h2>Product Added Successfully!</h2>");
                out.println("<a href='seller.html'>Add Another Product</a>");
            } else {
                out.println("<h2>Failed to Add Product</h2>");
            }

        } catch (Exception e) {
            e.printStackTrace(out);
        }
    }
}
