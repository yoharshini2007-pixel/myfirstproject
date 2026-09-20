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

@WebServlet("/ProductDataServlet")
public class ProductDataServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
@Override 
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();

        ProductDao dao = new ProductDao();
        List<Product> products = dao.getAllProducts();

        out.print("[");

        for (int i = 0; i < products.size(); i++) {

            Product p = products.get(i);

            out.print("{");
            out.print("\"id\":" + p.getId() + ",");
            out.print("\"name\":\"" + p.getName() + "\",");
            out.print("\"price\":" + p.getPrice() + ",");
            out.print("\"category\":\"" + p.getCategory() + "\",");
            out.print("\"stock\":" + p.getStockQty());
            out.print("}");

            if (i < products.size() - 1) {
                out.print(",");
            }
        }

        out.print("]");
    }
}