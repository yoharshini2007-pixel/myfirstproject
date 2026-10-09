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

@WebServlet("/PaymentServlet")
public class PaymentServlet extends HttpServlet {

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

        String buyerName = request.getParameter("buyerName");
        String phone = request.getParameter("phone");
        String address = request.getParameter("address");
        String city = request.getParameter("city");
        String pinCode = request.getParameter("pinCode");

        String paymentMethod = request.getParameter("paymentMethod");

        double total = 0;

        try (Connection con = DatabaseConnection.getConnection()) {

            // =========================
            // GET CART TOTAL
            // =========================

            String totalSql =
                    "SELECT SUM(c.quantity * p.price) AS total " +
                    "FROM cart_items c " +
                    "JOIN products p ON c.product_id = p.id " +
                    "WHERE c.user_id = ?";

            try (PreparedStatement ps =
                         con.prepareStatement(totalSql)) {

                ps.setInt(1, userId);

                try (ResultSet rs = ps.executeQuery()) {

                    if (rs.next()) {
                        total = rs.getDouble("total");
                    }
                }
            }

            // =========================
            // CHECK CART
            // =========================

            if (total <= 0) {

                response.setContentType("text/html");

                PrintWriter out = response.getWriter();

                out.println("<h2>🛒 Your cart is empty!</h2>");
                out.println("<a href='buyer.html'>Continue Shopping</a>");

                return;
            }

            // ==================================================
            // FIRST TIME:
            // SHOW PAYMENT SELECTION PAGE
            // ==================================================

            if (paymentMethod == null ||
                paymentMethod.trim().isEmpty()) {

                response.setContentType("text/html");

                PrintWriter out = response.getWriter();

                out.println("<html>");
                out.println("<head>");
                out.println("<title>Payment - Yoharshini Mart</title>");

                out.println("<style>");

                out.println("* { box-sizing:border-box; }");

                out.println("body {");
                out.println("margin:0;");
                out.println("font-family:Arial,sans-serif;");
                out.println("background:linear-gradient(135deg,#e8f5e9,#e3f2fd,#fff3e0);");
                out.println("min-height:100vh;");
                out.println("padding:30px;");
                out.println("}");

                out.println(".box {");
                out.println("max-width:650px;");
                out.println("margin:auto;");
                out.println("background:white;");
                out.println("padding:30px;");
                out.println("border-radius:22px;");
                out.println("box-shadow:0 10px 35px rgba(0,0,0,.15);");
                out.println("}");

                out.println("h1 {");
                out.println("text-align:center;");
                out.println("color:#173c33;");
                out.println("}");

                out.println(".amount {");
                out.println("background:#173c33;");
                out.println("color:white;");
                out.println("padding:20px;");
                out.println("border-radius:15px;");
                out.println("text-align:center;");
                out.println("margin:20px 0;");
                out.println("}");

                out.println(".amount h2 {");
                out.println("font-size:30px;");
                out.println("margin:8px;");
                out.println("}");

                out.println(".payment {");
                out.println("display:block;");
                out.println("padding:18px;");
                out.println("margin:12px 0;");
                out.println("border:2px solid #ddd;");
                out.println("border-radius:14px;");
                out.println("cursor:pointer;");
                out.println("}");

                out.println(".payment:hover {");
                out.println("border-color:#173c33;");
                out.println("background:#f1f8f4;");
                out.println("}");

                out.println(".payment input {");
                out.println("margin-right:10px;");
                out.println("}");

                out.println(".button {");
                out.println("width:100%;");
                out.println("padding:15px;");
                out.println("margin-top:20px;");
                out.println("background:#173c33;");
                out.println("color:white;");
                out.println("border:0;");
                out.println("border-radius:12px;");
                out.println("font-size:18px;");
                out.println("cursor:pointer;");
                out.println("}");

                out.println("</style>");
                out.println("</head>");

                out.println("<body>");

                out.println("<div class='box'>");

                out.println("<h1> Choose Payment Method</h1>");

                out.println("<div class='amount'>");
                out.println("<div>Total Amount</div>");
                out.println("<h2>₹" + total + "</h2>");
                out.println("</div>");

                /*
                 * IMPORTANT:
                 * Submit back to PaymentServlet
                 */

                out.println("<form action='PaymentServlet' method='post'>");

                // Delivery details
                out.println("<input type='hidden' name='buyerName' value='" +
                        buyerName + "'>");

                out.println("<input type='hidden' name='phone' value='" +
                        phone + "'>");

                out.println("<input type='hidden' name='address' value='" +
                        address + "'>");

                out.println("<input type='hidden' name='city' value='" +
                        city + "'>");

                out.println("<input type='hidden' name='pinCode' value='" +
                        pinCode + "'>");

                // COD
                out.println("<label class='payment'>");
                out.println("<input type='radio' " +
                        "name='paymentMethod' value='COD' required>");
                out.println(" <b>Cash on Delivery</b>");
                out.println("<br>");
                out.println("<small>Pay when your order arrives</small>");
                out.println("</label>");

                // UPI
                out.println("<label class='payment'>");
                out.println("<input type='radio' " +
                        "name='paymentMethod' value='UPI'>");
                out.println(" <b>UPI</b>");
                out.println("<br>");
                out.println("<small>Pay using UPI</small>");
                out.println("</label>");

                // CARD
                out.println("<label class='payment'>");
                out.println("<input type='radio' " +
                        "name='paymentMethod' value='CARD'>");
                out.println(" <b>Debit / Credit Card</b>");
                out.println("<br>");
                out.println("<small>Secure card payment</small>");
                out.println("</label>");

                out.println("<button class='button' type='submit'>");
                out.println("Place Order ");
                out.println("</button>");

                out.println("</form>");

                out.println("<br>");
                out.println("<a href='CartViewServlet'> Back to Cart</a>");

                out.println("</div>");

                out.println("</body>");
                out.println("</html>");

                return;
            }

            // ==================================================
            // PAYMENT METHOD RECEIVED
            // ==================================================

            // =========================
            // CREATE ORDER
            // =========================

            String orderSql =
                    "INSERT INTO orders " +
                    "(buyer_id, status, total_amount, buyer_name, phone, address, city, pin_code) " +
                    "VALUES (?, 'CONFIRMED', ?, ?, ?, ?, ?, ?)";

            int orderId;

            try (PreparedStatement ps =
                         con.prepareStatement(
                                 orderSql,
                                 PreparedStatement.RETURN_GENERATED_KEYS)) {

                ps.setInt(1, userId);
                ps.setDouble(2, total);
                ps.setString(3, buyerName);
                ps.setString(4, phone);
                ps.setString(5, address);
                ps.setString(6, city);
                ps.setString(7, pinCode);

                ps.executeUpdate();

                try (ResultSet rs = ps.getGeneratedKeys()) {

                    if (rs.next()) {
                        orderId = rs.getInt(1);
                    } else {
                        throw new Exception(
                                "Order ID could not be created.");
                    }
                }
            }

            // =========================
            // SAVE ORDER ITEMS
            // =========================

            String cartSql =
                    "SELECT product_id, quantity, p.price " +
                    "FROM cart_items c " +
                    "JOIN products p ON c.product_id = p.id " +
                    "WHERE c.user_id = ?";

            String itemSql =
                    "INSERT INTO order_items " +
                    "(order_id, product_id, quantity, unit_price) " +
                    "VALUES (?, ?, ?, ?)";

            try (PreparedStatement cartPs =
                         con.prepareStatement(cartSql);
                 PreparedStatement itemPs =
                         con.prepareStatement(itemSql)) {

                cartPs.setInt(1, userId);

                try (ResultSet rs = cartPs.executeQuery()) {

                    while (rs.next()) {

                        itemPs.setInt(1, orderId);
                        itemPs.setInt(2,
                                rs.getInt("product_id"));
                        itemPs.setInt(3,
                                rs.getInt("quantity"));
                        itemPs.setDouble(4,
                                rs.getDouble("price"));

                        itemPs.executeUpdate();
                    }
                }
            }

            // =========================
            // CLEAR CART
            // =========================

            String clearSql =
                    "DELETE FROM cart_items WHERE user_id = ?";

            try (PreparedStatement ps =
                         con.prepareStatement(clearSql)) {

                ps.setInt(1, userId);
                ps.executeUpdate();
            }

            // =========================
            // SUCCESS PAGE
            // =========================

            response.setContentType("text/html");

            PrintWriter out = response.getWriter();

            out.println("<html>");
            out.println("<head>");

            out.println("<title>Order Successful</title>");

            out.println("<style>");

            out.println("body {");
            out.println("margin:0;");
            out.println("font-family:Arial,sans-serif;");
            out.println("background:linear-gradient(135deg,#e8f5e9,#e3f2fd,#fff3e0);");
            out.println("min-height:100vh;");
            out.println("display:flex;");
            out.println("align-items:center;");
            out.println("justify-content:center;");
            out.println("}");

            out.println(".success {");
            out.println("background:white;");
            out.println("width:90%;");
            out.println("max-width:600px;");
            out.println("padding:35px;");
            out.println("text-align:center;");
            out.println("border-radius:25px;");
            out.println("box-shadow:0 10px 40px rgba(0,0,0,.15);");
            out.println("}");

            out.println(".check {");
            out.println("font-size:70px;");
            out.println("animation:bounce 1s infinite alternate;");
            out.println("}");

            out.println("@keyframes bounce {");
            out.println("from {transform:translateY(0);}");
            out.println("to {transform:translateY(-10px);}");
            out.println("}");

            out.println("h1 { color:#173c33; }");

            out.println(".details {");
            out.println("text-align:left;");
            out.println("background:#f4f8f5;");
            out.println("padding:20px;");
            out.println("border-radius:15px;");
            out.println("line-height:2;");
            out.println("margin-top:20px;");
            out.println("}");

            out.println(".btn {");
            out.println("display:inline-block;");
            out.println("padding:13px 20px;");
            out.println("margin:15px 5px;");
            out.println("border-radius:10px;");
            out.println("text-decoration:none;");
            out.println("font-weight:bold;");
            out.println("}");

            out.println(".orders {");
            out.println("background:#173c33;");
            out.println("color:white;");
            out.println("}");

            out.println(".shop {");
            out.println("background:#e8f5e9;");
            out.println("color:#173c33;");
            out.println("}");

            out.println("</style>");

            out.println("</head>");

            out.println("<body>");

            out.println("<div class='success'>");

            out.println("<div class='check'></div>");

            out.println("<h1>Order Placed Successfully</h1>");

            out.println("<p>Thank you for shopping with Yoharshini Mart </p>");

            out.println("<div class='details'>");

            out.println("<b> Order ID:</b> " +
                    orderId + "<br>");

            out.println("<b> Total:</b> Rs." +
                    total + "<br>");

            out.println("<b> Payment:</b> " +
                    paymentMethod + "<br>");

            out.println("<b> Name:</b> " +
                    buyerName + "<br>");

            out.println("<b> Phone:</b> " +
                    phone + "<br>");

            out.println("<b> Address:</b> " +
                    address + "<br>");

            out.println("<b> City:</b> " +
                    city + "<br>");

            out.println("<b> PIN:</b> " +
                    pinCode);

            out.println("</div>");

            out.println("<a class='btn orders' " +
                    "href='BuyerOrderHistoryServlet'>");
            out.println(" My Orders");
            out.println("</a>");

            out.println("<a class='btn shop' " +
                    "href='buyer.html'>");
            out.println(" Continue Shopping");
            out.println("</a>");

            out.println("</div>");

            out.println("</body>");
            out.println("</html>");

        } catch (Exception e) {

            e.printStackTrace();

            response.setContentType("text/html");

            PrintWriter out = response.getWriter();

            out.println("<h2>❌ Error processing order</h2>");
            out.println("<p>" + e.getMessage() + "</p>");
        }
    }
}