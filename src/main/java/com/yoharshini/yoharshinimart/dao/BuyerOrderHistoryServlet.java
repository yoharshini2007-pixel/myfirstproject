package com.yoharshini.yoharshinimart.dao;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.yoharshini.yoharshinimart.util.DatabaseConnection;

@WebServlet("/BuyerOrderHistoryServlet")
public class BuyerOrderHistoryServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/index.html");
            return;
        }

        int userId = (Integer) session.getAttribute("userId");

        response.setContentType("text/html;charset=UTF-8");

        PrintWriter out = response.getWriter();

        out.println("<!DOCTYPE html>");
        out.println("<html><head>");
        out.println("<meta charset='UTF-8'>");
        out.println("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        out.println("<title>My Orders - Yoharshini Mart</title>");

        out.println("<style>");
        out.println("*{box-sizing:border-box;}");
        out.println("body{margin:0;font-family:Arial,sans-serif;background:linear-gradient(135deg,#e8f5e9,#e3f2fd,#fff3e0);min-height:100vh;}");
        out.println(".header{background:linear-gradient(135deg,#173c33,#285c4f);color:white;padding:22px 35px;}");
        out.println(".logo{font-size:25px;font-weight:bold;}");
        out.println(".container{max-width:1000px;margin:35px auto;padding:20px;}");
        out.println("h1{text-align:center;color:#173c33;}");
        out.println(".order-card{background:white;margin:25px 0;padding:25px;border-radius:20px;box-shadow:0 8px 25px rgba(0,0,0,0.12);}");
        out.println(".order-top{display:flex;justify-content:space-between;align-items:center;flex-wrap:wrap;gap:10px;}");
        out.println(".order-id{font-size:21px;font-weight:bold;color:#173c33;}");
        out.println(".amount{font-size:20px;font-weight:bold;color:#c47a00;}");
        out.println(".tracking{display:flex;justify-content:space-between;align-items:flex-start;margin:40px 10px 20px;position:relative;}");
        out.println(".tracking:before{content:'';position:absolute;top:20px;left:8%;right:8%;height:5px;background:#ddd;z-index:0;}");
        out.println(".step{text-align:center;width:25%;position:relative;z-index:1;}");
        out.println(".circle{width:42px;height:42px;border-radius:50%;margin:auto;display:flex;align-items:center;justify-content:center;background:#ddd;font-size:20px;}");
        out.println(".active .circle{background:#2e7d32;color:white;box-shadow:0 0 0 6px #dff5e3;}");
        out.println(".step-text{margin-top:10px;font-size:13px;font-weight:bold;color:#777;}");
        out.println(".active .step-text{color:#2e7d32;}");
        out.println(".status-box{margin-top:25px;padding:15px;border-radius:12px;background:#f1f8f4;text-align:center;font-weight:bold;color:#173c33;}");
        out.println(".continue{display:block;width:230px;max-width:100%;margin:30px auto 10px;padding:14px;text-align:center;text-decoration:none;background:#173c33;color:white;border-radius:10px;font-weight:bold;}");
        out.println(".cancelled{background:#ffebee;color:#b71c1c;}");
        out.println("@media(max-width:600px){.container{padding:10px;}.order-card{padding:18px;}.tracking{margin-left:0;margin-right:0;}.step-text{font-size:10px;}.circle{width:35px;height:35px;font-size:16px;}}");
        out.println("</style></head><body>");

        out.println("<div class='header'><div class='logo'>Yoharshini Mart</div></div>");
        out.println("<div class='container'><h1>My Order History</h1>");

        String sql =
                "SELECT id, status, total_amount " +
                "FROM orders WHERE buyer_id = ? ORDER BY id DESC";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {

                boolean hasOrders = false;

                while (rs.next()) {

                    hasOrders = true;

                    int orderId = rs.getInt("id");
                    double total = rs.getDouble("total_amount");

                    String status = rs.getString("status");

                    if (status == null || status.trim().isEmpty()) {
                        status = "PENDING";
                    } else {
                        status = status.trim()
                                .toUpperCase(Locale.ROOT)
                                .replace(' ', '_');
                    }

                    boolean cancelled = status.equals("CANCELLED");
                    boolean confirmed = status.equals("CONFIRMED")
                            || status.equals("SHIPPED")
                            || status.equals("OUT_FOR_DELIVERY")
                            || status.equals("DELIVERED");

                    boolean shipped = status.equals("SHIPPED")
                            || status.equals("OUT_FOR_DELIVERY")
                            || status.equals("DELIVERED");

                    boolean outForDelivery = status.equals("OUT_FOR_DELIVERY")
                            || status.equals("DELIVERED");

                    boolean delivered = status.equals("DELIVERED");

                    out.println("<div class='order-card'>");
                    out.println("<div class='order-top'>");
                    out.println("<div class='order-id'>Order ID: " + orderId + "</div>");
                    out.println("<div class='amount'>Total: Rs. " + total + "</div>");
                    out.println("</div>");

                    if (!cancelled) {

                        out.println("<div class='tracking'>");

                        out.println("<div class='step " + (confirmed ? "active" : "") + "'>");
                        out.println("<div class='circle'>1</div>");
                        out.println("<div class='step-text'>Confirmed</div>");
                        out.println("</div>");

                        out.println("<div class='step " + (shipped ? "active" : "") + "'>");
                        out.println("<div class='circle'>2</div>");
                        out.println("<div class='step-text'>Shipped</div>");
                        out.println("</div>");

                        out.println("<div class='step " + (outForDelivery ? "active" : "") + "'>");
                        out.println("<div class='circle'>3</div>");
                        out.println("<div class='step-text'>Out for Delivery</div>");
                        out.println("</div>");

                        out.println("<div class='step " + (delivered ? "active" : "") + "'>");
                        out.println("<div class='circle'>4</div>");
                        out.println("<div class='step-text'>Delivered</div>");
                        out.println("</div>");

                        out.println("</div>");
                    }

                    out.println("<div class='status-box "
                            + (cancelled ? "cancelled" : "") + "'>");

                    switch (status) {
                        case "PENDING":
                            out.println("Your order is pending.");
                            break;
                        case "CONFIRMED":
                            out.println("Your order has been confirmed.");
                            break;
                        case "SHIPPED":
                            out.println("Your order has been shipped.");
                            break;
                        case "OUT_FOR_DELIVERY":
                            out.println("Your order is out for delivery.");
                            break;
                        case "DELIVERED":
                            out.println("Your order has been delivered.");
                            break;
                        case "CANCELLED":
                            out.println("Your order has been cancelled.");
                            break;
                        default:
                            out.println("Current order status: "
                                    + status.replace('_', ' '));
                            break;
                    }

                    out.println("</div></div>");
                }

                if (!hasOrders) {
                    out.println("<div class='order-card'><h2 style='text-align:center;'>No orders found.</h2></div>");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            out.println("<div class='order-card'><h3>Error loading order history.</h3></div>");
        }

        out.println("<a class='continue' href='buyer.html'>Continue Shopping</a>");
        out.println("</div></body></html>");
    }
}