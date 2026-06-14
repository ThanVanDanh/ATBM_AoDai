package controller.Cart;

import dao.ProductDao;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import model.cart.Cart;
import model.cart.CartItem;
import model.product.Product;
import services.CloudinaryService;
import services.ProductService;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "AddCartController", value = "/add-cart")
public class AddCartController extends HttpServlet {
    private ProductService productService;

    public void init() throws ServletException {
        super.init();
        try {
            ProductDao pd = new ProductDao();
            org.jdbi.v3.core.Jdbi jdbi = pd.get();
            CloudinaryService cloudinary = new CloudinaryService();
            this.productService = new ProductService(jdbi, cloudinary);
        } catch (Exception ex) {
            throw new ServletException("Khởi tạo ProductController thất bại: " + ex.getMessage(), ex);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            String productIdRaw = request.getParameter("productId");
            String quantityRaw = request.getParameter("quantity");

            if (productIdRaw == null || quantityRaw == null) {
                out.print("{\"status\": \"error\", \"message\": \"Thiếu thông tin sản phẩm\"}");
                return;
            }

            int productId = Integer.parseInt(productIdRaw);
            int quantity = Integer.parseInt(quantityRaw);

            String sku = request.getParameter("sku");
            if (sku == null) sku = "";

            String size = request.getParameter("size");
            if (size == null) size = "";

            String priceRaw = request.getParameter("price");
            double price = 0.0;
            if (priceRaw != null && !priceRaw.isEmpty()) {
                try {
                    price = Double.parseDouble(priceRaw);
                } catch (NumberFormatException e) {
                    price = productService.getPriceById(productId);
                }
            } else {
                price = productService.getPriceById(productId);
            }

            HttpSession session = request.getSession();
            Cart cart = (Cart) session.getAttribute("cart");
            if (cart == null) {
                cart = new Cart();
            }

            Product product = productService.getProduct(productId);
            if (product != null) {
                cart.addItem(product, quantity, price, sku, size);

                session.setAttribute("cart", cart);

                StringBuilder json = new StringBuilder();
                json.append("{");
                json.append("\"status\": \"success\",");
                json.append("\"totalQuantity\": ").append(cart.getTotalQuantity()).append(",");
                json.append("\"totalPrice\": ").append(cart.getTotalPrice()).append(",");
                json.append("\"items\": [");
                int count = 0;

                for (CartItem item : cart.getItems()) {
                    if (count > 0) json.append(",");
                    json.append("{");

                    json.append("\"product\": {");
                    json.append("\"id\": ").append(item.getProduct().getId()).append(",");

                    String pName = item.getProduct().getNameProduct().replace("\"", "\\\"");
                    json.append("\"nameProduct\": \"").append(pName).append("\",");

                    String pCode = item.getProduct().getProductCode() != null ? item.getProduct().getProductCode() : "";
                    json.append("\"productCode\": \"").append(pCode).append("\",");

                    json.append("\"images\": [");
                    if (item.getProduct().getImages() != null && !item.getProduct().getImages().isEmpty()) {
                        json.append("{ \"imageUrl\": \"").append(item.getProduct().getImages().get(0).getImageUrl()).append("\" }");
                    }
                    json.append("]");

                    json.append("},");

                    json.append("\"quantity\": ").append(item.getQuantity()).append(",");
                    json.append("\"price\": ").append(item.getPrice()).append(",");

                    String itemSku = item.getSku() != null ? item.getSku() : "";
                    json.append("\"sku\": \"").append(itemSku).append("\",");

                    String itemSize = item.getSize() != null ? item.getSize() : "";
                    json.append("\"size\": \"").append(itemSize).append("\"");

                    json.append("}");
                    count++;
                }
                json.append("]");
                json.append("}");

                out.print(json.toString());

            } else {
                out.print("{\"status\": \"error\", \"message\": \"Sản phẩm không tồn tại\"}");
            }

        } catch (Exception e) {
            e.printStackTrace();
            out.print("{\"status\": \"error\", \"message\": \"Lỗi server: " + e.getMessage() + "\"}");
        }
        out.flush();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}