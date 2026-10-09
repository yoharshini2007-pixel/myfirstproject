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

@WebServlet("/CustomerCountServlet")
public class CustomerCountServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();

        int customerCount = 0;

        String sql = "SELECT COUNT(*) FROM users WHERE role = 'BUYER'";

        try {
            Connection con = DatabaseConnection.getConnection();

            PreparedStatement ps = con.prepareStatement(sql);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                customerCount = rs.getInt(1);
            }

            rs.close();
            ps.close();
            con.close();

            out.print("{\"count\":" + customerCount + "}");

        } catch (Exception e) {
            e.printStackTrace();
            out.print("{\"count\":0}");
        }
    }
}