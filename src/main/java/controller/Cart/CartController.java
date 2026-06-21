package controller.Cart;

import dao.ProductDao;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import model.cart.Cart;
import model.product.Product;
import model.product.ProductVariant;
import services.CloudinaryService;
import services.ProductService;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "CartController", value = "/cart")
public class CartController extends HttpServlet {
    private ProductService productService;
    private dao.VoucherDao voucherDao;

    public void init() throws ServletException {
        super.init();
        try {
            ProductDao pd = new ProductDao();
            org.jdbi.v3.core.Jdbi jdbi = pd.get();
            CloudinaryService cloudinary = new CloudinaryService();
            this.productService = new ProductService(jdbi, cloudinary);
            this.voucherDao = new dao.VoucherDao();
        } catch (Exception ex) {
            throw new ServletException(" " + ex.getMessage(), ex);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");

        if (action != null && action.equals("remove")) {
            removeFromCart(request, response);
        } else {
            request.getRequestDispatcher("/cart.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");

        if (action != null && action.equals("add")) {
            addToCart(request, response);
        } else if (action != null && action.equals("update")) {
            updateCart(request, response);
        } else if (action != null && action.equals("remove")) {
            removeFromCart(request, response);
        } else if (action != null && action.equals("applyVoucher")) {
            applyVoucher(request, response);
        }

    }

    private void applyVoucher(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            String code = request.getParameter("code");
            HttpSession session = request.getSession();
            Cart cart = (Cart) session.getAttribute("cart");

            if (cart == null || cart.getTotalQuantity() == 0) {
                response.getWriter().write("{\"success\":false, \"message\":\"Giỏ hàng trống\"}");
                return;
            }

            model.voucher.Voucher voucher = voucherDao.getByCode(code);

            if (voucher == null) {
                if ("MOCK-TEST-1".equals(code)) {
                    voucher = new model.voucher.Voucher();
                    voucher.setId(999);
                    voucher.setVoucherCode("MOCK-TEST-1");
                    voucher.setDiscountType("fixed");
                    voucher.setDiscountValue(50000);
                    voucher.setMinOrderAmount(200000);
                    voucher.setActive(true);
                    voucher.setValidFrom(java.time.LocalDateTime.now().minusDays(1));
                    voucher.setValidTo(java.time.LocalDateTime.now().plusDays(1));
                } else if ("MOCK-TEST-2".equals(code)) {
                    voucher = new model.voucher.Voucher();
                    voucher.setId(998);
                    voucher.setVoucherCode("MOCK-TEST-2");
                    voucher.setDiscountType("percentage");
                    voucher.setDiscountValue(10);
                    voucher.setMinOrderAmount(500000);
                    voucher.setActive(true);
                    voucher.setValidFrom(java.time.LocalDateTime.now().minusDays(1));
                    voucher.setValidTo(java.time.LocalDateTime.now().plusDays(1));
                } else {
                    response.getWriter().write("{\"success\":false, \"message\":\"Mã giảm giá không tồn tại\"}");
                    return;
                }
            }

            if (!voucher.isActive()) {
                response.getWriter().write("{\"success\":false, \"message\":\"Mã giảm giá đã hết hạn hoặc bị khóa\"}");
                return;
            }

            double minAmount = voucher.getMinOrderAmount();
            double cartTotal = cart.getTotalPrice();

            if (cartTotal < minAmount) {
                response.getWriter().write("{\"success\":false, \"message\":\"Đơn hàng chưa đạt giá trị tối thiểu: "
                        + java.text.NumberFormat.getCurrencyInstance(new java.util.Locale("vi", "VN")).format(minAmount)
                        + "\"}");
                return;
            }

            double discountAmount = 0;
            if ("percentage".equalsIgnoreCase(voucher.getDiscountType())
                    || "percent".equalsIgnoreCase(voucher.getDiscountType())) {
                discountAmount = cartTotal * (voucher.getDiscountValue() / 100.0);
            } else {
                discountAmount = voucher.getDiscountValue();
            }

            session.setAttribute("appliedVoucher", voucher);

            double shippingFee = (cartTotal >= 1000000) ? 0 : 30000;
            double finalTotal = cartTotal + shippingFee - discountAmount;
            if (finalTotal < 0)
                finalTotal = 0;

            response.getWriter().write(String.format(
                    "{\"success\":true, \"message\":\"Áp dụng mã thành công\", \"discountAmount\":%.0f, \"finalTotal\":%.0f}",
                    discountAmount, finalTotal));

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().write("{\"success\":false, \"message\":\"Lỗi hệ thống: " + e.getMessage() + "\"}");
        }
    }

    private void addToCart(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int productId = Integer.parseInt(request.getParameter("productId"));
            int quantity = Integer.parseInt(request.getParameter("quantity"));

            HttpSession session = request.getSession();
            Cart cart = (Cart) session.getAttribute("cart");
            if (cart == null) {
                cart = new Cart();
                session.setAttribute("cart", cart);
            }

            Product product = productService.getProduct(productId);

            if (product != null) {
                String sku = request.getParameter("sku");
                if (sku == null)
                    sku = "";
                String size = request.getParameter("size");
                if (size == null)
                    size = "";
                double price = 0;
                if (price == 0) {
                    List<ProductVariant> variants = product.getVariants();
                    if (variants != null) {
                        for (ProductVariant v : variants) {

                            if ((!sku.isEmpty() && sku.equals(v.getSku())) ||
                                    (!size.isEmpty() && size.equals(v.getSize()))) {

                                if (v.getDiscountedPrice() > 0 && v.getDiscountedPrice() < v.getCurrentPrice()) {
                                    price = v.getDiscountedPrice();
                                } else {
                                    price = v.getCurrentPrice();
                                }
                                break;
                            }
                        }
                        if (price == 0 && !variants.isEmpty())
                            price = variants.get(0).getCurrentPrice();
                    }
                }
                cart.addItem(product, quantity, price, sku, size);
                session.setAttribute("cart", cart);
            }

            String ajaxHeader = request.getHeader("X-Requested-With");
            boolean isAjax = "XMLHttpRequest".equals(ajaxHeader) ||
                    request.getContentType() != null
                            && request.getContentType().contains("application/x-www-form-urlencoded");

            if (isAjax) {
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write("{\"success\":true, \"message\":\"Đã thêm vào giỏ hàng\"}");
            } else {
                String referer = request.getHeader("Referer");
                response.sendRedirect(referer != null ? referer : "cart");
            }

        } catch (Exception e) {
            e.printStackTrace();

            String ajaxHeader = request.getHeader("X-Requested-With");
            boolean isAjax = "XMLHttpRequest".equals(ajaxHeader) ||
                    request.getContentType() != null
                            && request.getContentType().contains("application/x-www-form-urlencoded");

            if (isAjax) {
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write("{\"success\":false, \"message\":\"Lỗi: " + e.getMessage() + "\"}");
            } else {
                response.sendRedirect(request.getContextPath() + "/cart");
            }
        }
    }

    private void updateCart(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int productId = Integer.parseInt(request.getParameter("id"));
            int quantity = Integer.parseInt(request.getParameter("quantity"));
            String sku = request.getParameter("sku");

            HttpSession session = request.getSession();
            Cart cart = (Cart) session.getAttribute("cart");

            if (cart != null) {
                cart.updateQuantity(productId, sku, quantity);
                session.setAttribute("cart", cart);
            }
            String referer = request.getHeader("Referer");
            response.sendRedirect(referer != null ? referer : request.getContextPath() + "/cart");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/cart");
        }
    }

    private void removeFromCart(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String idParam = request.getParameter("id");
            String sku = request.getParameter("sku");

            if (sku == null)
                sku = "";

            if (idParam != null) {
                int productId = Integer.parseInt(idParam);
                HttpSession session = request.getSession();
                Cart cart = (Cart) session.getAttribute("cart");

                if (cart != null) {

                    cart.remove(productId, sku);
                    session.setAttribute("cart", cart);
                    session.removeAttribute("appliedVoucher");
                    session.removeAttribute("voucherError");
                    System.out.println(" Called cart.remove()");
                } else {
                    System.out.println(" Cart is NULL in Session");
                }
            }

            String referer = request.getHeader("Referer");
            response.sendRedirect(referer != null ? referer : (request.getContextPath() + "/cart"));

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/cart");
        }
    }

}