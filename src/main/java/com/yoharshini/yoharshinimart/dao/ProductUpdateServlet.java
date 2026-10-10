package com.yoharshini.yoharshinimart.dao;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.yoharshini.yoharshinimart.model.Product;

@WebServlet("/ProductUpdateServlet")
public class ProductUpdateServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request,
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
        int id;
        double price;
        int stock;

        String name = request.getParameter("name");
        String description = request.getParameter("description");
        String category = request.getParameter("category");

        try {
            sellerId = Integer.parseInt(
                    session.getAttribute("userId").toString());
            id = Integer.parseInt(request.getParameter("id"));
            price = Double.parseDouble(request.getParameter("price"));
            stock = Integer.parseInt(request.getParameter("stock"));
        } catch (NumberFormatException | NullPointerException e) {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Invalid product details");
            return;
        }

        if (name == null || name.trim().isEmpty()
                || description == null || description.trim().isEmpty()
                || category == null || category.trim().isEmpty()
                || !Double.isFinite(price) || price <= 0
                || stock < 0 || id <= 0) {

            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Please enter valid product details");
            return;
        }

        Product product = new Product();
        product.setId(id);
        product.setSellerId(sellerId);
        product.setName(name.trim());
        product.setDescription(description.trim());
        product.setPrice(price);
        product.setStockQty(stock);
        product.setCategory(category.trim());

        ProductDao dao = new ProductDao();
        boolean updated = dao.updateProduct(product);

        if (updated) {
            response.sendRedirect(
                    request.getContextPath() + "/ProductServlet");
        } else {
            response.sendError(
                    HttpServletResponse.SC_NOT_FOUND,
                    "Product not found or access denied");
        }
    }
}