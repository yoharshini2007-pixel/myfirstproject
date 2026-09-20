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

@WebServlet("/RegisterServlet")
public class RegisterServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        out.println("<html><head><title>Register</title></head><body>");
        out.println("<h2>Create Account</h2>");

        out.println("<form action='RegisterServlet' method='post'>");

        out.println("Name:<br>");
        out.println("<input type='text' name='name' required><br><br>");

        out.println("Email:<br>");
        out.println("<input type='email' name='email' required><br><br>");

        out.println("Password:<br>");
        out.println("<input type='password' name='password' required><br><br>");

        out.println("<button type='submit'>Register</button>");

        out.println("</form>");
        out.println("</body></html>");
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        String sql = "INSERT INTO users (name, email, password_hash, role) VALUES (?, ?, ?, ?)";

        try {
            Connection con = DatabaseConnection.getConnection();

            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, password);
            ps.setString(4, "BUYER");

            ps.executeUpdate();

            ps.close();
            con.close();

            response.sendRedirect("index.html");

        } catch (Exception e) {
            e.printStackTrace();

            response.setContentType("text/html");
            response.getWriter().println(
                "<h3>Registration failed!</h3><p>" + e.getMessage() + "</p>"
            );
        }
    }
}