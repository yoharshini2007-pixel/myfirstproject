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

@WebServlet("/CustomerDataServlet")
public class CustomerDataServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();

        String sql =
                "SELECT id, name, email, created_at " +
                "FROM users " +
                "WHERE role = 'BUYER' " +
                "ORDER BY id DESC";

        try (Connection con =
                     DatabaseConnection.getConnection();
             PreparedStatement ps =
                     con.prepareStatement(sql);
             ResultSet rs =
                     ps.executeQuery()) {

            out.print("[");

            boolean first = true;

            while (rs.next()) {

                if (!first) {
                    out.print(",");
                }

                out.print("{");

                out.print("\"id\":" +
                        rs.getInt("id") + ",");

                out.print("\"name\":\"" +
                        escapeJson(rs.getString("name")) +
                        "\",");

                out.print("\"email\":\"" +
                        escapeJson(rs.getString("email")) +
                        "\",");

                out.print("\"date\":\"" +
                        rs.getTimestamp("created_at") +
                        "\"");

                out.print("}");

                first = false;
            }

            out.print("]");

        } catch (Exception e) {

            e.printStackTrace();

            out.print("[]");
        }
    }

    private String escapeJson(String value) {

        if (value == null) {
            return "";
        }

        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }
}