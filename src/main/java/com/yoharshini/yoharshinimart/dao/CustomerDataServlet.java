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

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        StringBuilder json = new StringBuilder("[");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM users WHERE role = 'customer' OR role IS NULL");
             ResultSet rs = ps.executeQuery()) {

            boolean first = true;
            while (rs.next()) {
                if (!first) {
                    json.append(",");
                }
                
                int id = rs.getInt("id");
                String name = rs.getString("name") != null ? rs.getString("name") : "N/A";
                String email = rs.getString("email") != null ? rs.getString("email") : "N/A";
                
                // Extra details from register form (If column names exist in your DB table)
                String phone = getOptionalString(rs, "phone");
                String address = getOptionalString(rs, "address");
                String joinedDate = getOptionalString(rs, "created_at");

                json.append("{")
                    .append("\"id\":").append(id).append(",")
                    .append("\"name\":\"").append(escapeJson(name)).append("\",")
                    .append("\"email\":\"").append(escapeJson(email)).append("\",")
                    .append("\"phone\":\"").append(escapeJson(phone)).append("\",")
                    .append("\"address\":\"").append(escapeJson(address)).append("\",")
                    .append("\"date\":\"").append(escapeJson(joinedDate)).append("\"")
                    .append("}");

                first = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        json.append("]");
        out.print(json.toString());
        out.flush();
    }

    private String getOptionalString(ResultSet rs, String columnName) {
        try {
            String val = rs.getString(columnName);
            return val != null ? val : "N/A";
        } catch (Exception e) {
            return "N/A";
        }
    }

    private String escapeJson(String input) {
        if (input == null) return "";
        return input.replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\b", "\\b")
                    .replace("\f", "\\f")
                    .replace("\n", "\\n")
                    .replace("\r", "\\r")
                    .replace("\t", "\\t");
    }
}