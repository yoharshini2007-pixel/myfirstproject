package com.yoharshini.yoharshinimart.dao;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.yoharshini.yoharshinimart.util.DatabaseConnection;

@WebServlet("/AddProductServlet")
public class AddProductServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");

        PrintWriter out = response.getWriter();

        try {

            // =========================
            // SELLER ID
            // =========================

            HttpSession session = request.getSession(false);

if (session == null
        || session.getAttribute("userId") == null
        || !"SELLER".equalsIgnoreCase(
                String.valueOf(session.getAttribute("role")))) {

    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
    out.println("ERROR: Only logged-in sellers can add products.");
    return;
}

int sellerId;

try {
    sellerId = Integer.parseInt(
        session.getAttribute("userId").toString()
    );

    if (sellerId <= 0) {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        return;
    }

} catch (NumberFormatException e) {
    response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
    return;
}


            // =========================
            // PRODUCT DETAILS
            // =========================

            String name =
                    request.getParameter("pname");

            String description =
                    request.getParameter("pdescription");

            String category =
                    request.getParameter("pcategory");

            String subCategory =
                    request.getParameter("psubcategory");

            String price =
                    request.getParameter("pprice");

            String stockQty =
                    request.getParameter("pstock");

            String imageUrl =
                    request.getParameter("imageUrl");


            // =========================
            // NULL CHECK
            // =========================

            if (name == null) {
                name = "";
            }

            if (description == null) {
                description = "";
            }

            if (category == null) {
                category = "";
            }

            if (subCategory == null) {
                subCategory = "";
            }

            if (price == null || price.trim().isEmpty()) {
                price = "0";
            }

            if (stockQty == null || stockQty.trim().isEmpty()) {
                stockQty = "0";
            }

            if (imageUrl == null) {
                imageUrl = "";
            }


            // =========================
            // VALIDATION
            // =========================

            if (name.trim().isEmpty()) {
                out.println("ERROR: Product name is missing.");
                return;
            }

            if (category.trim().isEmpty()) {
                out.println("ERROR: Category is missing.");
                return;
            }
            double productPrice;
int productStock;

try {
    productPrice = Double.parseDouble(price.trim());
    productStock = Integer.parseInt(stockQty.trim());
} catch (NumberFormatException e) {
    out.println("ERROR: Enter a valid price and stock quantity.");
    return;
}

if (!Double.isFinite(productPrice) || productPrice <= 0) {
    out.println("ERROR: Price must be greater than zero.");
    return;
}

if (productStock < 0) {
    out.println("ERROR: Stock quantity cannot be negative.");
    return;
}


            // =========================
            // INSERT PRODUCT
            // =========================

            String sql =
                    "INSERT INTO products " +
                    "(seller_id, name, description, price, stock_qty, " +
                    "category, subcategory, image_url) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";


            try (
                Connection conn =
                        DatabaseConnection.getConnection();

                PreparedStatement ps =
                        conn.prepareStatement(sql)
            ) {

                ps.setInt(1,sellerId);

                ps.setString(2, name);

                ps.setString(3, description);

                ps.setDouble(
                        4,
                        productPrice
                );

                ps.setInt(
                        5,
                        productStock
                );

                ps.setString(6, category);

                ps.setString(7, subCategory);

                ps.setString(8, imageUrl);


                int result =
                        ps.executeUpdate();


                if (result > 0) {

                    out.println(
                            "Product Added Successfully"
                    );

                } else {

                    out.println(
                            "Product Add Failed"
                    );
                }
            }

        } catch (Exception e) {

            e.printStackTrace();

            out.println(
                    "ERROR: " + e.getMessage()
            );
        }
    }
}