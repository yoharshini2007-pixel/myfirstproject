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

@WebServlet("/DashboardGrowthServlet")
public class DashboardGrowthServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        double productGrowth = 0;
        double orderGrowth = 0;
        double customerGrowth = 0;
        double revenueGrowth = 0;
        double reviewGrowth = 0;

        try (Connection con = DatabaseConnection.getConnection()) {

            productGrowth = getGrowth(con,
                    "products", "created_at");

            orderGrowth = getGrowth(con,
                    "orders", "created_at");

            customerGrowth = getBuyerGrowth(con);

            revenueGrowth = getRevenueGrowth(con);

            reviewGrowth = getGrowth(con,
                    "reviews", "created_at");

        } catch (Exception e) {
            e.printStackTrace();
        }

        PrintWriter out = response.getWriter();

        out.print("{"
                + "\"productGrowth\":" + productGrowth + ","
                + "\"orderGrowth\":" + orderGrowth + ","
                + "\"customerGrowth\":" + customerGrowth + ","
                + "\"revenueGrowth\":" + revenueGrowth + ","
                + "\"reviewGrowth\":" + reviewGrowth
                + "}");
    }


    private double getGrowth(Connection con,
                             String table,
                             String dateColumn)
            throws Exception {

        String sql =
            "SELECT " +
            "SUM(CASE WHEN MONTH(" + dateColumn + ") = MONTH(CURRENT_DATE()) " +
            "AND YEAR(" + dateColumn + ") = YEAR(CURRENT_DATE()) THEN 1 ELSE 0 END), " +

            "SUM(CASE WHEN MONTH(" + dateColumn + ") = MONTH(DATE_SUB(CURRENT_DATE(), INTERVAL 1 MONTH)) " +
            "AND YEAR(" + dateColumn + ") = YEAR(DATE_SUB(CURRENT_DATE(), INTERVAL 1 MONTH)) THEN 1 ELSE 0 END) " +

            "FROM " + table;

        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {

                double current = rs.getDouble(1);
                double previous = rs.getDouble(2);

                return calculatePercentage(current, previous);
            }
        }

        return 0;
    }


    private double getBuyerGrowth(Connection con)
            throws Exception {

        String sql =
            "SELECT " +
            "SUM(CASE WHEN role = 'BUYER' " +
            "AND MONTH(created_at) = MONTH(CURRENT_DATE()) " +
            "AND YEAR(created_at) = YEAR(CURRENT_DATE()) THEN 1 ELSE 0 END), " +

            "SUM(CASE WHEN role = 'BUYER' " +
            "AND MONTH(created_at) = MONTH(DATE_SUB(CURRENT_DATE(), INTERVAL 1 MONTH)) " +
            "AND YEAR(created_at) = YEAR(DATE_SUB(CURRENT_DATE(), INTERVAL 1 MONTH)) THEN 1 ELSE 0 END) " +

            "FROM users";

        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {

                double current = rs.getDouble(1);
                double previous = rs.getDouble(2);

                return calculatePercentage(current, previous);
            }
        }

        return 0;
    }


    private double getRevenueGrowth(Connection con)
            throws Exception {

        String sql =
            "SELECT " +

            "COALESCE(SUM(CASE " +
            "WHEN MONTH(created_at) = MONTH(CURRENT_DATE()) " +
            "AND YEAR(created_at) = YEAR(CURRENT_DATE()) " +
            "THEN total_amount ELSE 0 END),0), " +

            "COALESCE(SUM(CASE " +
            "WHEN MONTH(created_at) = MONTH(DATE_SUB(CURRENT_DATE(), INTERVAL 1 MONTH)) " +
            "AND YEAR(created_at) = YEAR(DATE_SUB(CURRENT_DATE(), INTERVAL 1 MONTH)) " +
            "THEN total_amount ELSE 0 END),0) " +

            "FROM orders";

        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {

                double current = rs.getDouble(1);
                double previous = rs.getDouble(2);

                return calculatePercentage(current, previous);
            }
        }

        return 0;
    }


    private double calculatePercentage(double current,
                                       double previous) {

        if (previous == 0) {

            if (current > 0) {
                return 100;
            }

            return 0;
        }

        return ((current - previous) / previous) * 100;
    }
}