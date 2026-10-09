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

    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
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

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    total = rs.getDouble("total");
                }
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

            out.println("<title>Delivery Address - Yoharshini Mart</title>");

            out.println("<style>");

            out.println("body {");
            out.println("font-family: Arial, sans-serif;");
            out.println("background: linear-gradient(135deg,#e8f5e9,#e3f2fd);");
            out.println("margin:0;");
            out.println("padding:40px;");
            out.println("}");

            out.println(".container {");
            out.println("max-width:600px;");
            out.println("margin:auto;");
            out.println("background:white;");
            out.println("padding:30px;");
            out.println("border-radius:18px;");
            out.println("box-shadow:0 5px 20px rgba(0,0,0,0.15);");
            out.println("}");

            out.println("h1 {");
            out.println("color:#173c33;");
            out.println("text-align:center;");
            out.println("}");

            out.println("h2 {");
            out.println("color:#285c4f;");
            out.println("}");

            out.println("label {");
            out.println("font-weight:bold;");
            out.println("display:block;");
            out.println("margin-top:15px;");
            out.println("}");

            out.println("input, textarea {");
            out.println("width:100%;");
            out.println("padding:12px;");
            out.println("margin-top:7px;");
            out.println("border:1px solid #ccc;");
            out.println("border-radius:8px;");
            out.println("box-sizing:border-box;");
            out.println("}");

            out.println("textarea {");
            out.println("height:90px;");
            out.println("resize:none;");
            out.println("}");

            out.println(".total {");
            out.println("background:#e8f5e9;");
            out.println("padding:15px;");
            out.println("margin-top:20px;");
            out.println("border-radius:10px;");
            out.println("font-size:20px;");
            out.println("font-weight:bold;");
            out.println("color:#173c33;");
            out.println("}");

            out.println("button {");
            out.println("width:100%;");
            out.println("padding:14px;");
            out.println("margin-top:20px;");
            out.println("background:#173c33;");
            out.println("color:white;");
            out.println("border:none;");
            out.println("border-radius:8px;");
            out.println("font-size:17px;");
            out.println("cursor:pointer;");
            out.println("}");

            out.println("button:hover {");
            out.println("background:#285c4f;");
            out.println("}");

            out.println(".back {");
            out.println("display:block;");
            out.println("text-align:center;");
            out.println("margin-top:15px;");
            out.println("color:#173c33;");
            out.println("}");

            out.println("</style>");

            out.println("</head>");

            out.println("<body>");

            out.println("<div class='container'>");

            out.println("<h1> Yoharshini Mart</h1>");

            out.println("<h2> Delivery Address</h2>");

            out.println("<form action='PaymentServlet' method='post'>");

            out.println("<input type='hidden' name='amount' value='" + total + "'>");

            out.println("<label>Full Name *</label>");
            out.println("<input type='text' name='buyerName' required>");

            out.println("<label>Phone Number *</label>");
            out.println("<input type='tel' name='phone' required>");

            out.println("<label>Delivery Address *</label>");
            out.println("<textarea name='address' required></textarea>");

            out.println("<label>City *</label>");
            out.println("<input type='text' name='city' required>");

            out.println("<label>PIN Code *</label>");
            out.println("<input type='text' name='pinCode' maxlength='6' required>");

            out.println("<div class='total'>");
            out.println("Total Amount: Rs." + total);
            out.println("</div>");

            out.println("<button type='submit'>");
            out.println("Continue to Payment ");
            out.println("</button>");

            out.println("</form>");

            out.println("<a class='back' href='CartViewServlet'>");
            out.println(" Back to Cart");
            out.println("</a>");

            out.println("</div>");

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