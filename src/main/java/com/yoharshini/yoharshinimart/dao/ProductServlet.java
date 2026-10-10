
package com.yoharshini.yoharshinimart.dao;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.yoharshini.yoharshinimart.model.Product;

@WebServlet("/ProductServlet")
public class ProductServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        // Verify seller login
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

        try {
            sellerId = Integer.parseInt(
                    session.getAttribute("userId").toString());
        } catch (NumberFormatException e) {
            response.sendError(
                    HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        response.setContentType("text/html;charset=UTF-8");

        PrintWriter out = response.getWriter();

        // Display products belonging to the logged-in seller
        ProductDao dao = new ProductDao();
        List<Product> products = dao.getProductsBySeller(sellerId);

        out.println("<!DOCTYPE html>");
        out.println("<html lang='en'>");
        out.println("<head>");
        out.println("<meta charset='UTF-8'>");
        out.println("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        out.println("<title>My Products - Yoharshini Mart</title>");

        out.println("<style>");
        out.println("*{box-sizing:border-box;}");
        out.println("body{margin:0;font-family:Arial,sans-serif;background:#f3f7f4;color:#26352d;}");
        out.println(".header{background:linear-gradient(135deg,#173c33,#285c4f);color:white;padding:22px 5%;display:flex;justify-content:space-between;align-items:center;flex-wrap:wrap;gap:12px;}");
        out.println(".logo{font-size:25px;font-weight:bold;}");
        out.println(".back{color:white;text-decoration:none;background:#ffffff20;padding:10px 16px;border-radius:8px;}");
        out.println(".container{max-width:1200px;margin:30px auto;padding:0 20px;}");
        out.println(".heading{display:flex;justify-content:space-between;align-items:center;flex-wrap:wrap;gap:10px;margin-bottom:25px;}");
        out.println("h1{color:#173c33;margin:0;}");
        out.println(".count{background:#dff3e5;color:#1b6338;padding:9px 15px;border-radius:20px;font-weight:bold;}");
        out.println(".grid{display:grid;grid-template-columns:repeat(auto-fit,minmax(250px,1fr));gap:24px;}");
        out.println(".card{background:white;border-radius:16px;overflow:hidden;box-shadow:0 5px 20px #173c3315;border:1px solid #e5ece7;transition:transform .2s,box-shadow .2s;}");
        out.println(".card:hover{transform:translateY(-5px);box-shadow:0 10px 26px #173c3325;}");
        out.println(".product-image{width:100%;height:210px;object-fit:contain;background:#fff;padding:12px;}");
        out.println(".no-image{height:210px;display:flex;align-items:center;justify-content:center;background:#edf4ef;color:#718278;font-size:18px;}");
        out.println(".content{padding:18px;}");
        out.println(".name{font-size:20px;font-weight:bold;color:#173c33;margin:0 0 8px;overflow-wrap:anywhere;}");
        out.println(".description{font-size:14px;line-height:1.5;color:#69756e;min-height:42px;overflow-wrap:anywhere;}");
        out.println(".category{display:inline-block;background:#e6f3e9;color:#286640;padding:7px 10px;border-radius:20px;font-size:12px;font-weight:bold;margin:8px 0;}");
        out.println(".price{font-size:23px;font-weight:bold;color:#bd7815;margin:12px 0;}");
        out.println(".stock{font-size:14px;color:#526258;margin-bottom:18px;}");
        out.println(".actions{display:flex;gap:10px;}");
        out.println(".btn{flex:1;text-align:center;text-decoration:none;padding:11px 8px;border-radius:8px;color:white;font-size:14px;font-weight:bold;}");
        out.println(".edit{background:#247a50;}");
        out.println(".delete{background:#c74444;}");
        out.println(".empty{background:white;text-align:center;padding:45px 20px;border-radius:16px;color:#65736a;}");
        out.println(".add{display:inline-block;margin-top:20px;background:#173c33;color:white;text-decoration:none;padding:12px 18px;border-radius:9px;}");
        out.println("footer{text-align:center;color:#819087;padding:25px;font-size:13px;}");
        out.println("@media(max-width:500px){.container{padding:0 12px;}.grid{grid-template-columns:1fr;}.product-image,.no-image{height:230px;}.header{padding:18px;}.logo{font-size:21px;}}");
        out.println("</style>");

        out.println("</head>");
        out.println("<body>");

        out.println("<header class='header'>");
        out.println("<div class='logo'>Yoharshini Mart | Seller</div>");
        out.println("<a class='back' href='seller.html'>← Back to Dashboard</a>");
        out.println("</header>");

        out.println("<main class='container'>");
        out.println("<div class='heading'>");
        out.println("<h1>My Products</h1>");
        out.println("<span class='count'>Total Products: "
                + products.size() + "</span>");
        out.println("</div>");

        if (products.isEmpty()) {

            out.println("<div class='empty'>");
            out.println("<h2>No products found</h2>");
            out.println("<p>Add your first product to see it here.</p>");
            out.println("<a class='add' href='seller.html'>+ Add New Product</a>");
            out.println("</div>");

        } else {

            out.println("<div class='grid'>");

            for (Product p : products) {

                out.println("<article class='card'>");

                String imageUrl = p.getImageUrl();

                if (imageUrl != null && !imageUrl.trim().isEmpty()) {

                    out.println("<img class='product-image' src='"
                            + escape(imageUrl)
                            + "' alt='"
                            + escape(p.getName())
                            + "' onerror=\"this.style.display='none';this.nextElementSibling.style.display='flex';\">");

                    out.println("<div class='no-image' style='display:none;'>No Image Available</div>");

                } else {

                    out.println("<div class='no-image'>No Image Available</div>");

                }

                out.println("<div class='content'>");

                out.println("<h2 class='name'>"
                        + escape(p.getName()) + "</h2>");

                out.println("<div class='description'>"
                        + escape(p.getDescription()) + "</div>");

                out.println("<span class='category'>"
                        + escape(p.getCategory()) + "</span>");

                out.println("<div class='price'>₹"
                        + p.getPrice() + "</div>");

                out.println("<div class='stock'>Stock Available: <strong>"
                        + p.getStockQty() + "</strong></div>");

                out.println("<div class='actions'>");

                out.println("<a class='btn edit' href='ProductEditServlet?id="
                        + p.getId() + "'>Edit</a>");

                
out.println("<form method='post' action='ProductDeleteServlet' "
        + "onsubmit=\"return confirm('Are you sure you want to delete this product?');\" "
        + "style='flex:1;margin:0;'>");

out.println("<input type='hidden' name='id' value='"
        + p.getId() + "'>");

out.println("<button type='submit' class='btn delete' "
        + "style='width:100%;border:none;cursor:pointer;'>Delete</button>");

out.println("</form>");

                out.println("</div>");
                out.println("</div>");
                out.println("</article>");
            }

            out.println("</div>");
        }

        out.println("</main>");
        out.println("<footer>© Yoharshini Mart | Seller Dashboard</footer>");
        out.println("</body>");
        out.println("</html>");
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