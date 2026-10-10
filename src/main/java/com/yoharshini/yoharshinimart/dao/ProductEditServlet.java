
package com.yoharshini.yoharshinimart.dao;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.yoharshini.yoharshinimart.model.Product;

@WebServlet("/ProductEditServlet")
public class ProductEditServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null
                || session.getAttribute("userId") == null
                || !"SELLER".equalsIgnoreCase(
                        String.valueOf(session.getAttribute("role")))) {

            response.sendRedirect(
                    request.getContextPath() + "/index.html");
            return;
        }

        int sellerId;
        int productId;

        try {
            sellerId = Integer.parseInt(
                    session.getAttribute("userId").toString());

            productId = Integer.parseInt(
                    request.getParameter("id"));
        } catch (NumberFormatException e) {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        ProductDao dao = new ProductDao();
        List<Product> products = dao.getProductsBySeller(sellerId);

        Product product = null;

        for (Product p : products) {
            if (p.getId() == productId) {
                product = p;
                break;
            }
        }

        if (product == null) {
            response.sendError(
                    HttpServletResponse.SC_NOT_FOUND,
                    "Product not found or access denied");
            return;
        }

        response.setContentType("text/html;charset=UTF-8");

        response.getWriter().println("<h2>Edit Product</h2>");

        response.getWriter().println(
                "<form action='" + request.getContextPath()
                + "/ProductUpdateServlet' method='post'>");

        response.getWriter().println(
                "<input type='hidden' name='id' value='"
                + product.getId() + "'>");

        response.getWriter().println(
                "Product Name:<br><input type='text' name='name' value='"
                + escape(product.getName()) + "' required><br><br>");

        response.getWriter().println(
                "Description:<br><textarea name='description' required>"
                + escape(product.getDescription())
                + "</textarea><br><br>");

        response.getWriter().println(
                "Price:<br><input type='number' min='0.01' step='0.01' "
                + "name='price' value='" + product.getPrice()
                + "' required><br><br>");

        response.getWriter().println(
                "Stock Quantity:<br><input type='number' min='0' "
                + "name='stock' value='" + product.getStockQty()
                + "' required><br><br>");

        response.getWriter().println(
                "Category:<br><input type='text' name='category' value='"
                + escape(product.getCategory())
                + "' required><br><br>");

        response.getWriter().println(
                "<button type='submit'>Update Product</button>");

        response.getWriter().println("</form>");
    }

    private String escape(String value) {
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