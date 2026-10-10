
package com.yoharshini.yoharshinimart.dao;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/ProductDeleteServlet")
public class ProductDeleteServlet extends HttpServlet {

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

        int productId;
        int sellerId;

        try {
            productId = Integer.parseInt(
                    request.getParameter("id"));

            sellerId = Integer.parseInt(
                    session.getAttribute("userId").toString());

        } catch (NumberFormatException | NullPointerException e) {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Invalid product ID");
            return;
        }

        if (productId <= 0) {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Invalid product ID");
            return;
        }

        ProductDao dao = new ProductDao();

        boolean deleted = dao.deleteProduct(productId, sellerId);

        if (deleted) {
            response.sendRedirect(
                    request.getContextPath() + "/ProductServlet");
        } else {
            response.sendError(
                    HttpServletResponse.SC_NOT_FOUND,
                    "Product not found or access denied");
        }
    }
}