package com.yoharshini.yoharshinimart.dao;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.yoharshini.yoharshinimart.model.Product;
import com.yoharshini.yoharshinimart.util.DatabaseConnection;

@WebServlet("/ProductDetailsServlet")
public class ProductDetailsServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");

        String idValue = request.getParameter("id");

        if (idValue == null || idValue.trim().isEmpty()) {
            response.sendError(400, "Product ID is required");
            return;
        }

        int productId;

        try {
            productId = Integer.parseInt(idValue);
        } catch (NumberFormatException e) {
            response.sendError(400, "Invalid product ID");
            return;
        }

        ProductDao dao = new ProductDao();
        List<Product> products = dao.getAllProducts();

        Product p = null;

        for (Product product : products) {
            if (product.getId() == productId) {
                p = product;
                break;
            }
        }

        if (p == null) {
            response.sendError(404, "Product not found");
            return;
        }

        PrintWriter out = response.getWriter();
        String contextPath = request.getContextPath();

        out.println("<!DOCTYPE html>");
        out.println("<html lang='en'>");
        out.println("<head>");
        out.println("<meta charset='UTF-8'>");
        out.println("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        out.println("<title>Product Details - Yoharshini Mart</title>");

        out.println("<style>");
        out.println("body{margin:0;font-family:Arial,sans-serif;background:#f5f7fa;color:#222;}");
        out.println(".header{background:#173c33;color:white;padding:20px 30px;font-size:26px;font-weight:bold;}");
        out.println(".container{max-width:1000px;margin:35px auto;padding:25px;background:white;border-radius:12px;box-shadow:0 3px 12px #ddd;}");
        out.println(".details{display:flex;gap:35px;flex-wrap:wrap;}");
        out.println(".image-box{flex:1;min-width:260px;display:flex;align-items:center;justify-content:center;background:#fafafa;border:1px solid #eee;border-radius:10px;min-height:300px;}");
        out.println(".image-box img{max-width:100%;height:300px;object-fit:contain;padding:10px;}");
        out.println(".info{flex:1;min-width:260px;}");
        out.println("h1{color:#173c33;font-size:30px;}");
        out.println(".price{font-size:30px;font-weight:bold;margin:20px 0;color:#222;}");
        out.println(".label{font-weight:bold;color:#173c33;}");
        out.println(".description{line-height:1.7;color:#555;}");
        out.println(".stock{color:#188038;font-weight:bold;margin:18px 0;}");
        out.println(".button{display:inline-block;padding:13px 20px;margin:10px 8px 0 0;border:0;border-radius:6px;text-decoration:none;font-size:16px;font-weight:bold;cursor:pointer;}");
        out.println(".back{background:#173c33;color:white;}");
        out.println(".cart{background:#ff9f00;color:white;}");
        out.println(".review{padding:15px;margin:12px 0;background:#f5f7fa;border-radius:8px;}");
        out.println(".stars{color:#f5a623;font-size:22px;margin:0;}");
        out.println("textarea{box-sizing:border-box;}");
        out.println("@media(max-width:600px){.container{margin:15px;padding:15px}.header{font-size:21px}.image-box img{height:240px}h1{font-size:25px}}");
        out.println("</style>");

        out.println("</head>");
        out.println("<body>");

        out.println("<div class='header'>🛍️ Yoharshini Mart</div>");
        out.println("<div class='container'>");

        // Product details
        out.println("<div class='details'>");
        out.println("<div class='image-box'>");

        if (p.getImageUrl() != null &&
            !p.getImageUrl().trim().isEmpty()) {
            out.println("<img src='" + escapeHtml(p.getImageUrl()) +
                        "' alt='" + escapeHtml(p.getName()) + "'>");
        } else {
            out.println("<span style='font-size:70px'>📦</span>");
        }

        out.println("</div>");
        out.println("<div class='info'>");

        out.println("<h1>" + escapeHtml(p.getName()) + "</h1>");

        out.println("<p><span class='label'>Category:</span> " +
                    escapeHtml(p.getCategory()) + "</p>");

        out.println("<p><span class='label'>Subcategory:</span> " +
                    escapeHtml(p.getSubCategory()) + "</p>");

        out.println("<div class='price'>₹" +
                    String.format(java.util.Locale.US, "%,.2f", p.getPrice()) +
                    "</div>");

        out.println("<p class='label'>Description</p>");
        out.println("<p class='description'>" +
                    escapeHtml(p.getDescription()) + "</p>");

        if (p.getStockQty() > 0) {
            out.println("<p class='stock'>✓ Available Stock: " +
                        p.getStockQty() + "</p>");
        } else {
            out.println("<p style='color:red;font-weight:bold'>Out of Stock</p>");
        }

        // Back to Products button
 out.println("<a class='button back' href='" + request.getContextPath() +"/buyer.html'>← Back to Products</a>");
        // Add to Cart button
        if (p.getStockQty() > 0) {
            out.println("<button class='button cart' onclick='addToCart(" +
                        p.getId() + ")'>🛒 Add to Cart</button>");
        }

        out.println("</div>");
        out.println("</div>");

        // Customer reviews
        out.println("<hr style='margin:30px 0;'>");
        out.println("<h2>⭐ Customer Reviews</h2>");

        String reviewSql =
            "SELECT rating, comment, created_at FROM reviews " +
            "WHERE product_id = ? ORDER BY created_at DESC";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(reviewSql)) {

            ps.setInt(1, p.getId());

            try (ResultSet rs = ps.executeQuery()) {
                boolean hasReviews = false;

                while (rs.next()) {
                    hasReviews = true;

                    int rating = rs.getInt("rating");
                    String comment = rs.getString("comment");
                    java.sql.Timestamp timestamp =
                        rs.getTimestamp("created_at");

                    String reviewDate = timestamp == null
                        ? ""
                        : timestamp.toString();

                    out.println("<div class='review'>");

                    out.println("<p class='stars'>" +
                        "★".repeat(rating) +
                        "☆".repeat(5 - rating) + "</p>");

                    out.println("<p>" + escapeHtml(comment) + "</p>");

                    out.println("<small style='color:#666;'>" +
                        escapeHtml(reviewDate) + "</small>");

                    out.println("</div>");
                }

                if (!hasReviews) {
                    out.println("<p>No reviews yet. Be the first to review!</p>");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            out.println("<p>Unable to load reviews right now.</p>");
        }

        // Review submission form
        out.println("<hr style='margin:30px 0;'>");
        out.println("<h2>⭐ Write a Product Review</h2>");

        out.println("<form action='" + contextPath +
                    "/AddReviewServlet' method='post'>");

        out.println("<input type='hidden' name='productId' value='" +
                    p.getId() + "'>");

        out.println("<label for='rating'>Your Rating:</label><br>");
        out.println("<select name='rating' id='rating' required " +
                    "style='padding:10px;margin:10px 0;'>");

        out.println("<option value=''>Select stars</option>");
        out.println("<option value='5'>★★★★★ - Excellent</option>");
        out.println("<option value='4'>★★★★ - Very Good</option>");
        out.println("<option value='3'>★★★ - Good</option>");
        out.println("<option value='2'>★★ - Fair</option>");
        out.println("<option value='1'>★ - Poor</option>");

        out.println("</select><br>");

        out.println("<label for='comment'>Your Comment:</label><br>");
        out.println("<textarea name='comment' id='comment' rows='4' " +
                    "required maxlength='2000' " +
                    "placeholder='Write your review here...' " +
                    "style='width:100%;max-width:500px;padding:12px;" +
                    "margin:10px 0;border:1px solid #ccc;border-radius:6px;'>" +
                    "</textarea><br>");

        out.println("<button type='submit' class='button cart'>" +
                    "Submit Review</button>");

        out.println("</form>");

        // Add to Cart JavaScript
        out.println("<script>");
        out.println("function addToCart(id){");
        out.println("const data=new URLSearchParams();");
        out.println("data.append('productId',id);");
        out.println("fetch('" + contextPath +
                    "/CartServlet',{method:'POST',headers:{'Content-Type':'application/x-www-form-urlencoded'},body:data.toString()})");
        out.println(".then(r=>{if(!r.ok)throw new Error('Cart request failed');window.location.href='" +
                    contextPath + "/CartViewServlet';})");
        out.println(".catch(()=>alert('Unable to add product to cart. Please try again.'));");
        out.println("}");
        out.println("</script>");

        out.println("</div>");
        out.println("</body>");
        out.println("</html>");
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