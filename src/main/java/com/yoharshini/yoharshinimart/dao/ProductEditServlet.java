package com.yoharshini.yoharshinimart.dao;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.yoharshini.yoharshinimart.model.Product;

@WebServlet("/ProductEditServlet")
public class ProductEditServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        int productId =
                Integer.parseInt(request.getParameter("id"));

        ProductDao dao = new ProductDao();

        Product product = null;

        for (Product p : dao.getAllProducts()) {

            if (p.getId() == productId) {
                product = p;
                break;
            }
        }

        if (product == null) {

            response.getWriter().println(
                "Product not found"
            );

            return;
        }

        response.setContentType("text/html");

        response.getWriter().println(
            "<h2>Edit Product</h2>"
        );

        response.getWriter().println(
            "<form action='ProductUpdateServlet' method='post'>"
        );

        response.getWriter().println(
            "<input type='hidden' name='id' value='" +
            product.getId() + "'>"
        );

        response.getWriter().println(
            "Product Name:<br>"
        );

        response.getWriter().println(
            "<input type='text' name='name' value='" +
            product.getName() + "' required><br><br>"
        );

        response.getWriter().println(
            "Description:<br>"
        );

        response.getWriter().println(
            "<textarea name='description' required>" +
            product.getDescription() +
            "</textarea><br><br>"
        );

        response.getWriter().println(
            "Price:<br>"
        );

        response.getWriter().println(
            "<input type='number' step='0.01' name='price' value='" +
            product.getPrice() + "' required><br><br>"
        );

        response.getWriter().println(
            "Stock Quantity:<br>"
        );

        response.getWriter().println(
            "<input type='number' name='stock' value='" +
            product.getStockQty() + "' required><br><br>"
        );

        response.getWriter().println(
            "Category:<br>"
        );

        response.getWriter().println(
            "<input type='text' name='category' value='" +
            product.getCategory() + "' required><br><br>"
        );

        response.getWriter().println(
            "<button type='submit'>Update Product</button>"
        );

        response.getWriter().println(
            "</form>"
        );
    }
}
