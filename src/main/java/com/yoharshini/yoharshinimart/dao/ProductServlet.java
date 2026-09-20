package com.yoharshini.yoharshinimart.dao;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.yoharshini.yoharshinimart.model.Product;

@WebServlet("/ProductServlet")
public class ProductServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");

        PrintWriter out = response.getWriter();

        ProductDao dao = new ProductDao();
        List<Product> products = dao.getAllProducts();

        out.println("<h2>Products</h2>");

        for (Product p : products) {
            out.println("<p>");
            out.println("Name: " + p.getName());
            out.println("<br>Price: ₹" + p.getPrice());
            out.println("<br>Category: " + p.getCategory());
            out.println("<br>Stock: " + p.getStockQty());
            out.println("</p>");
        }
    }
}