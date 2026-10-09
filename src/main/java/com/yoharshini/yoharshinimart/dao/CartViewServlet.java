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

@WebServlet("/CartViewServlet")
public class CartViewServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Object userIdObject = session.getAttribute("userId");

        if (userIdObject == null) {
            response.sendRedirect(
                request.getContextPath() + "/index.html"
            );
            return;
        }

        int userId = (Integer) userIdObject;

        response.setContentType("text/html; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();

        String sql =
            "SELECT c.product_id, c.quantity, p.name, p.price " +
            "FROM cart_items c " +
            "JOIN products p ON c.product_id = p.id " +
            "WHERE c.user_id = ?";

        double total = 0;
        boolean hasItems = false;

        out.println("<!DOCTYPE html>");
        out.println("<html lang='en'>");
        out.println("<head>");
        out.println("<meta charset='UTF-8'>");
        out.println("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        out.println("<title>Yoharshini Mart - Shopping Cart</title>");

        out.println("<style>");
        out.println("* { box-sizing: border-box; }");
        out.println("body { margin:0; font-family:Arial,sans-serif;");
        out.println("background:linear-gradient(135deg,#f3e8ff,#e3f2ff,#ffffff);");
        out.println("color:#34304a; min-height:100vh; }");

        out.println("header { background:linear-gradient(110deg,#7963b6,#6d9ed6);");
        out.println("color:white; padding:22px 6%; display:flex;");
        out.println("justify-content:space-between; align-items:center;");
        out.println("gap:15px; flex-wrap:wrap;");
        out.println("box-shadow:0 4px 16px rgba(75,65,120,0.18); }");

        out.println("header h1 { margin:0; font-size:25px; }");
        out.println(".back { display:inline-block; color:white;");
        out.println("text-decoration:none; border:1px solid rgba(255,255,255,0.8);");
        out.println("padding:10px 15px; border-radius:9px; }");
        out.println(".back:hover { background:white; color:#66539b; }");

        out.println("main { max-width:950px; margin:35px auto; padding:0 18px; }");
        out.println(".page-title { color:#62508f; font-size:28px; margin-bottom:8px; }");
        out.println(".subtitle { color:#77718b; margin-bottom:25px; }");

        out.println(".product { background:rgba(255,255,255,0.95);");
        out.println("border:1px solid #e4dcf5; border-radius:16px;");
        out.println("padding:22px; margin-bottom:16px;");
        out.println("box-shadow:0 5px 18px rgba(91,76,143,0.08); }");

        out.println(".product h3 { margin:0 0 14px; color:#62508f;");
        out.println("font-size:20px; overflow-wrap:anywhere; }");
        out.println(".details { display:flex; gap:10px; flex-wrap:wrap; }");
        out.println(".detail { background:#f3efff; color:#574985;");
        out.println("border-radius:8px; padding:9px 12px; font-size:14px; }");
        out.println(".price { background:#e8f3ff; color:#315f91; }");

        out.println(".remove { margin-top:15px; padding:10px 18px;");
        out.println("background:#c45a78; color:white; border:none;");
        out.println("border-radius:8px; cursor:pointer; }");

        out.println(".summary { margin-top:25px; padding:25px;");
        out.println("background:white; border:1px solid #ded8f2;");
        out.println("border-radius:16px;");
        out.println("box-shadow:0 6px 20px rgba(91,76,143,0.10); }");

        out.println(".summary h2 { margin-top:0; color:#62508f; }");
        out.println(".total { border-top:1px solid #e7e1f2;");
        out.println("padding-top:18px; margin-top:18px; display:flex;");
        out.println("justify-content:space-between; gap:12px; flex-wrap:wrap;");
        out.println("font-size:19px; font-weight:bold; }");
        out.println(".amount { color:#5e4a9c; font-size:25px; }");

        out.println(".checkout { width:100%; margin-top:22px;");
        out.println("padding:15px; border:none; border-radius:10px;");
        out.println("color:white; background:linear-gradient(100deg,#8066bf,#609bd7);");
        out.println("font-size:16px; font-weight:bold; cursor:pointer; }");
        out.println(".checkout:hover { filter:brightness(1.06); }");

        out.println(".empty { text-align:center; padding:35px 20px;");
        out.println("background:white; border:1px solid #e4dcf5;");
        out.println("border-radius:16px; }");
        out.println(".empty h3 { color:#62508f; }");
        out.println("footer { text-align:center; padding:20px;");
        out.println("color:#77718b; font-size:13px; }");

        out.println("@media(max-width:600px) {");
        out.println("header { padding:18px; }");
        out.println("header h1 { font-size:21px; }");
        out.println("main { margin-top:25px; }");
        out.println(".product,.summary { padding:18px; }");
        out.println(".page-title { font-size:24px; }");
        out.println("}");
        out.println("</style>");
        out.println("</head><body>");

        out.println("<header>");
        out.println("<h1>Yoharshini Mart</h1>");
        out.println("<a class='back' href='" +
            request.getContextPath() +
            "/buyer.html'>Back to Home</a>");
        out.println("</header>");

        out.println("<main>");
        out.println("<h2 class='page-title'>Your Shopping Cart</h2>");
        out.println("<p class='subtitle'>Review your items before confirming your order.</p>");

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    hasItems = true;

                    int productId = rs.getInt("product_id");
                    String name = rs.getString("name");
                    double price = rs.getDouble("price");
                    int quantity = rs.getInt("quantity");

                    double itemTotal = price * quantity;
                    total += itemTotal;

                    out.println("<section class='product'>");
                    out.println("<h3>" + escapeHtml(name) + "</h3>");

                    out.println("<div class='details'>");
                    out.println("<span class='detail price'>Price: Rs. " +
                        String.format(java.util.Locale.US, "%.2f", price) +
                        "</span>");

                    out.println("<span class='detail'>Quantity: " +
                        quantity + "</span>");

                    out.println("<span class='detail price'>Item Total: Rs. " +
                        String.format(java.util.Locale.US, "%.2f", itemTotal) +
                        "</span>");
                    out.println("</div>");

                    out.println("<form action='" +
                        request.getContextPath() +
                        "/RemoveCartItemServlet' method='post' " +
                        "onsubmit=\"return confirm('Remove this product from your cart?');\">");

                    out.println("<input type='hidden' name='productId' value='" +
                        productId + "'>");

                    out.println("<button type='submit' class='remove'>Remove</button>");
                    out.println("</form>");
                    out.println("</section>");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();

            out.println("<section class='empty'>");
            out.println("<h3>Unable to load your cart</h3>");
            out.println("<p>Please try again.</p>");
            out.println("</section>");
            out.println("</main></body></html>");
            return;
        }

        if (!hasItems) {
            out.println("<section class='empty'>");
            out.println("<h3>Your cart is empty</h3>");
            out.println("<p>Add a product before placing an order.</p>");
            out.println("</section>");
        } else {
            out.println("<section class='summary'>");
            out.println("<h2>Order Summary</h2>");

            out.println("<div class='total'>");
            out.println("<span>Total Amount</span>");
            out.println("<span class='amount'>Rs. " +
                String.format(java.util.Locale.US, "%.2f", total) +
                "</span>");
            out.println("</div>");

            out.println("<form action='" +
                request.getContextPath() +
                "/OrderServlet' method='post'>");

            out.println("<button class='checkout' type='submit'>Confirm Order</button>");
            out.println("</form>");
            out.println("</section>");
        }

        out.println("</main>");
        out.println("<footer>Yoharshini Mart - Thank you for shopping with us.</footer>");
        out.println("</body></html>");
    }

    private String escapeHtml(String value) {
        if (value == null) {
            return "";
        }

        return value.replace("&", "&amp;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;")
                    .replace("\"", "&quot;")
                    .replace("'", "&#39;");
    }
}