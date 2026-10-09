package com.yoharshini.yoharshinimart.dao;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/ProductDeleteServlet")
public class ProductDeleteServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");

        try {

            String id = request.getParameter("id");

response.getWriter().println("Received ID: " + id);

int productId = Integer.parseInt(id);

            ProductDao dao =
                    new ProductDao();

            boolean deleted =
                    dao.deleteProduct(productId);

            if (deleted) {

                response.getWriter().println(
                        "Product Deleted Successfully"
                );

            } else {

                response.getWriter().println(
                        "Failed to delete product"
                );
            }

        } catch (Exception e) {

            e.printStackTrace();

            response.getWriter().println(
                    "ERROR: " + e.getMessage()
            );
        }
    }
}