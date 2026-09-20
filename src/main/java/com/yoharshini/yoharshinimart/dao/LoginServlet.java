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

        try {

            Connection con = DatabaseConnection.getConnection();

            String query =
                "SELECT * FROM users WHERE email = ? AND password_hash = ?";

            PreparedStatement ps = con.prepareStatement(query);

            ps.setString(1, email);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                int userId = rs.getInt("id");
                String role = rs.getString("role");
                System.out.println("LOGIN ROLE = " + role);

                request.getSession().setAttribute("userId", userId);
                request.getSession().setAttribute("role", role);

                if("ADMIN".equalsIgnoreCase(role)) {

                    response.sendRedirect(
                        request.getContextPath() + "/home.html"
                    );

                } else if ("BUYER".equalsIgnoreCase(role)) {

                    response.sendRedirect(
                        request.getContextPath() + "/buyer.html"
                    );

                } else if ("SELLER".equalsIgnoreCase(role)) {

                    response.sendRedirect(
                        request.getContextPath() + "/seller.html"
                    );

                }

            } else {

                out.println(
                    "<h3>Invalid Email or Password!</h3>" +
                    "<a href='index.html'>Try Again</a>"
                );
            }

        } catch (Exception e) {

            e.printStackTrace();
            out.println("Error: " + e.getMessage());
        }
    }
}