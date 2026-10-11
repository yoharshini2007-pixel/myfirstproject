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

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String selectedRole = request.getParameter("role");

        if (email == null || email.trim().isEmpty()
                || password == null || password.isEmpty()
                || selectedRole == null || selectedRole.trim().isEmpty()) {

            response.sendError(
                HttpServletResponse.SC_BAD_REQUEST,
                "Email, password, and role are required."
            );
            return;
        }

        email = email.trim();
        selectedRole = selectedRole.trim().toUpperCase();

        try {
            // 1. ADMIN LOGIN (Permanent Email & Password)
            if ("ADMIN".equals(selectedRole)) {
                if ("admin@gmail.com".equals(email) && "12345".equals(password)) {
                    request.getSession().setAttribute("userId", 1);
                    request.getSession().setAttribute("role", "ADMIN");
                    response.sendRedirect(request.getContextPath() + "/home.html");
                    return;
                } else {
                    out.println("<h3>Invalid Admin Email or Password!</h3><a href='index.html'>Try Again</a>");
                    return;
                }
            }

            // 2. SELLER LOGIN (Any Email & Password accepted - Buyer maari)
            else if ("SELLER".equals(selectedRole)) {
                request.getSession().setAttribute("userId", 999); // Dummy Seller ID
                request.getSession().setAttribute("role", "SELLER");
                response.sendRedirect(request.getContextPath() + "/seller.html");
                return;
            }

            // 3. BUYER LOGIN (Database check)
            else if ("BUYER".equals(selectedRole)) {
                Connection con = DatabaseConnection.getConnection();
                String query = "SELECT * FROM users WHERE email = ? AND password_hash = ? AND role = ?";
                PreparedStatement ps = con.prepareStatement(query);

                ps.setString(1, email);
                ps.setString(2, password);
                ps.setString(3, "BUYER");

                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    int userId = rs.getInt("id");
                    request.getSession().setAttribute("userId", userId);
                    request.getSession().setAttribute("role", "BUYER");
                    response.sendRedirect(request.getContextPath() + "/buyer.html");
                } else {
                    out.println("<h3>Invalid Buyer Email or Password!</h3><a href='index.html'>Try Again</a>");
                }
                ps.close();
                con.close();
            } else {
                out.println("<h3>Invalid Role Selected!</h3><a href='index.html'>Try Again</a>");
            }

        } catch (Exception e) {
            e.printStackTrace();
            out.println("Error: " + e.getMessage());
        }
    }
}