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

@WebServlet("/OrderDataServlet")
public class OrderDataServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();

        String orderSql =
                "SELECT id, buyer_id, buyer_name, phone, address, " +
                "city, pin_code, status, total_amount, created_at " +
                "FROM orders ORDER BY id DESC";

        String itemSql =
                "SELECT oi.product_id, oi.quantity, oi.unit_price, " +
                "p.name, p.image_url " +
                "FROM order_items oi " +
                "JOIN products p ON oi.product_id = p.id " +
                "WHERE oi.order_id = ?";

        try (Connection con =
                     DatabaseConnection.getConnection();
             PreparedStatement orderPs =
                     con.prepareStatement(orderSql);
             ResultSet orderRs =
                     orderPs.executeQuery()) {

            out.print("[");

            boolean firstOrder = true;

            while (orderRs.next()) {

                if (!firstOrder) {
                    out.print(",");
                }

                int orderId =
                        orderRs.getInt("id");

                out.print("{");

                out.print("\"id\":" +
                        orderId + ",");

                out.print("\"buyerId\":" +
                        orderRs.getInt("buyer_id") + ",");

                out.print("\"buyerName\":\"" +
                        escapeJson(orderRs.getString("buyer_name")) +
                        "\",");

                out.print("\"phone\":\"" +
                        escapeJson(orderRs.getString("phone")) +
                        "\",");

                out.print("\"address\":\"" +
                        escapeJson(orderRs.getString("address")) +
                        "\",");

                out.print("\"city\":\"" +
                        escapeJson(orderRs.getString("city")) +
                        "\",");

                out.print("\"pinCode\":\"" +
                        escapeJson(orderRs.getString("pin_code")) +
                        "\",");

                out.print("\"status\":\"" +
                        escapeJson(orderRs.getString("status")) +
                        "\",");

                out.print("\"total\":" +
                        orderRs.getDouble("total_amount") + ",");

                out.print("\"date\":\"" +
                        orderRs.getTimestamp("created_at") +
                        "\",");

                // =========================
                // ORDER ITEMS
                // =========================

                out.print("\"items\":[");

                boolean firstItem = true;

                try (PreparedStatement itemPs =
                             con.prepareStatement(itemSql)) {

                    itemPs.setInt(1, orderId);

                    try (ResultSet itemRs =
                                 itemPs.executeQuery()) {

                        while (itemRs.next()) {

                            if (!firstItem) {
                                out.print(",");
                            }

                            out.print("{");

                            out.print("\"productId\":" +
                                    itemRs.getInt("product_id") + ",");

                            out.print("\"name\":\"" +
                                    escapeJson(
                                            itemRs.getString("name")) +
                                    "\",");

                            out.print("\"quantity\":" +
                                    itemRs.getInt("quantity") + ",");

                            out.print("\"unitPrice\":" +
                                    itemRs.getDouble("unit_price") + ",");

                            out.print("\"imageUrl\":\"" +
                                    escapeJson(
                                            itemRs.getString("image_url")) +
                                    "\"");

                            out.print("}");

                            firstItem = false;
                        }
                    }
                }

                out.print("]");

                out.print("}");

                firstOrder = false;
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