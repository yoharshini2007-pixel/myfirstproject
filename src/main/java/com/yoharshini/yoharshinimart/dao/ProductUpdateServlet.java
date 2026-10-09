package com.yoharshini.yoharshinimart.dao;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.yoharshini.yoharshinimart.model.Product;

@WebServlet("/ProductUpdateServlet")
public class ProductUpdateServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        int id = Integer.parseInt(
                request.getParameter("id"));

        String name = request.getParameter("name");
        String description = request.getParameter("description");
        double price = Double.parseDouble(
                request.getParameter("price"));
        int stock = Integer.parseInt(
                request.getParameter("stock"));
        String category = request.getParameter("category");

        Product product = new Product();

        product.setId(id);
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setStockQty(stock);
        product.setCategory(category);

        ProductDao dao = new ProductDao();

        boolean updated = dao.updateProduct(product);

        if (updated) {

            response.sendRedirect("ProductServlet");

        } else {

            response.getWriter().println(
                    "Failed to update product"
            );
        }
    }
}
