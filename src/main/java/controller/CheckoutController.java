package controller;

import dao.AddressDao;
import dao.KeyDao;
import dao.OrderDao;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.cart.Cart;
import model.cart.CartItem;
import model.order.Order;
import model.user.Address;
import model.user.User;
import model.voucher.Voucher;
import util.OrderSignatureDataBuilder;
import util.SignatureUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "CheckoutController", urlPatterns = {"/checkout", "/checkout/apply-voucher"})
public class CheckoutController extends HttpServlet {
    private AddressDao addressDao;
    private dao.VoucherDao voucherDao;
    private dao.OrderDao orderDao;
    private KeyDao keyDao;

    @Override
    public void init() throws ServletException {
        super.init();
        try {
            this.addressDao = new AddressDao();
            this.voucherDao = new dao.VoucherDao();
            this.orderDao = new dao.OrderDao();
            this.keyDao = new KeyDao();
        } catch (Exception ex) {
            throw new ServletException("Failed to initialize CheckoutController: " + ex.getMessage(), ex);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession();

        Cart cart = (Cart) session.getAttribute("cart");
        if (cart == null || cart.getTotalQuantity() == 0) {
            session.removeAttribute("appliedVoucher");
            session.removeAttribute("voucherError");
            response.sendRedirect(request.getContextPath() + "/cart");
            return;
        }

        Voucher currentVoucher = (model.voucher.Voucher) session.getAttribute("appliedVoucher");
        if (currentVoucher != null) {
            if (!currentVoucher.isActive()
                    || cart.getTotalPrice() < currentVoucher.getMinOrderAmount()
                    || currentVoucher.getCurrentUsage() >= currentVoucher.getMaxUsage()
                    || currentVoucher.getValidFrom() != null && currentVoucher.getValidFrom().isAfter(java.time.LocalDateTime.now())
                    || currentVoucher.getValidTo() != null && currentVoucher.getValidTo().isBefore(java.time.LocalDateTime.now())) {
                session.removeAttribute("appliedVoucher");
                session.setAttribute("voucherError", "Mã khuyến mãi đã được gỡ vì giỏ hàng đã thay đổi hoặc không còn đủ điều kiện.");
            }
        }
        try {
            List<model.voucher.Voucher> vouchers = voucherDao.getActiveVouchers();
            request.setAttribute("vouchers", vouchers);
        } catch (Exception e) {
            e.printStackTrace();
        }

        model.voucher.Voucher appliedVoucher = (model.voucher.Voucher) session.getAttribute("appliedVoucher");
        if (appliedVoucher != null) {
            request.setAttribute("appliedVoucher", appliedVoucher);
        }

        String voucherError = (String) session.getAttribute("voucherError");
        if (voucherError != null) {
            request.setAttribute("voucherError", voucherError);
            session.removeAttribute("voucherError");
        }

        User user = (User) session.getAttribute("account");
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        try {
            List<Address> addresses = addressDao.findByUserId(user.getId());
            if (addresses != null && !addresses.isEmpty()) {
                Address defaultAddress = addresses.stream()
                        .filter(Address::isDefault)
                        .findFirst()
                        .orElse(addresses.get(0));

                session.setAttribute("defaultAddress", defaultAddress);
            } else {
                session.removeAttribute("defaultAddress");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        request.getRequestDispatcher("/thanhtoan.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        String servletPath = req.getServletPath();
        if ("/checkout/apply-voucher".equals(servletPath)) {
            handleApplyVoucher(req, resp);
            return;
        }

        resp.setContentType("application/json;charset=UTF-8");

        HttpSession session = req.getSession();
        Cart cart = (Cart) session.getAttribute("cart");

        if (cart == null || cart.getTotalQuantity() == 0) {
            writeJson(resp, false, "Giỏ hàng trống.");
            return;
        }

        User user = (User) session.getAttribute("account");
        if (user == null) {
            writeJson(resp, false, "Vui lòng đăng nhập để đặt hàng.");
            return;
        }

        Integer keyId = keyDao.getActiveKeyId(user.getId());
        if (keyId == null) {
            writeJson(resp, false, "Bạn chưa có khóa ký. Vui lòng tạo khóa trong trang tài khoản trước khi đặt hàng.");
            return;
        }

        String fullName = trim(req.getParameter("fullName"));
        String phone = trim(req.getParameter("phone"));
        String email = trim(req.getParameter("email"));
        String address = trim(req.getParameter("address"));
        String city = trim(req.getParameter("city"));
        String paymentMethod = trim(req.getParameter("paymentMethod"));
        String orderNote = trim(req.getParameter("orderNote"));

        if (isBlank(fullName)) {
            writeJson(resp, false, "Vui lòng nhập họ và tên.");
            return;
        }

        if (isBlank(phone)) {
            writeJson(resp, false, "Vui lòng nhập số điện thoại.");
            return;
        }

        if (isBlank(email)) {
            writeJson(resp, false, "Vui lòng nhập email.");
            return;
        }

        if (isBlank(address)) {
            writeJson(resp, false, "Vui lòng nhập địa chỉ.");
            return;
        }

        if (isBlank(city)) {
            writeJson(resp, false, "Vui lòng nhập tỉnh/thành phố, quận/huyện, phường/xã.");
            return;
        }

        if (isBlank(paymentMethod)) {
            writeJson(resp, false, "Vui lòng chọn phương thức thanh toán.");
            return;
        }

        String shippingAddress = address + ", " + city;

        Order order = new Order();
        order.setUserId(user.getId());
        order.setCustomerFullname(fullName);
        order.setCustomerPhone(phone);
        order.setCustomerEmail(email);
        order.setShippingAddress(shippingAddress);
        order.setCustomerNote(orderNote);
        order.setPaymentMethod(paymentMethod);

        double subtotal = cart.getTotalPrice();
        double shippingFee = subtotal >= 1000000 ? 0 : 30000;

        model.voucher.Voucher voucher = (model.voucher.Voucher) session.getAttribute("appliedVoucher");
        double discountAmount = 0;
        Integer voucherId = null;

        if (voucher != null) {
            if (voucher.isActive() && subtotal >= voucher.getMinOrderAmount()) {
                if ("percentage".equalsIgnoreCase(voucher.getDiscountType())
                        || "percent".equalsIgnoreCase(voucher.getDiscountType())) {
                    discountAmount = subtotal * (voucher.getDiscountValue() / 100.0);
                } else {
                    discountAmount = voucher.getDiscountValue();
                }
                voucherId = voucher.getId();
            } else {
                discountAmount = 0;
                voucherId = null;
            }
        }

        double totalAmount = subtotal + shippingFee - discountAmount;
        if (totalAmount < 0) {
            totalAmount = 0;
        }

        order.setSubtotalAmount(subtotal);
        order.setShippingFee(shippingFee);
        order.setDiscountAmount(discountAmount);
        order.setTotalAmount(totalAmount);
        order.setVoucherId(voucherId);
        order.setOrderStatus("Đang chờ xác thực");
        order.setPaymentStatus("chưa thanh toán");
        order.setSignatureStatus("unsigned");

        List<CartItem> items = cart.getItems();

        List<OrderSignatureDataBuilder.SignableItem> signableItems = new java.util.ArrayList<>();
        OrderDao orderDao = new OrderDao();
        for (CartItem item : items) {
            String color = orderDao.getVariantColorBySku(item.getSku());
            String productName = item.getProduct() != null ? item.getProduct().getNameProduct() : "";
            String productCode = item.getProduct() != null ? item.getProduct().getProductCode() : "";
            Integer variantId = orderDao.getVariantId(item.getSku());
            double lineTotal = item.getQuantity() * item.getPrice();

            signableItems.add(
                    new OrderSignatureDataBuilder.SignableItem(
                            variantId,
                            productCode,
                            item.getSku(),
                            productName,
                            item.getSize(),
                            color,
                            item.getQuantity(),
                            item.getPrice(),
                            lineTotal
                    )
            );
        }

        String orderCode = "ORD" + System.currentTimeMillis();
        order.setOrderCode(orderCode);
        order.setKeyId(keyId);
        order.setCreatedAt(java.time.LocalDateTime.now().withNano(0));

        try {
            String signedOrderData = OrderSignatureDataBuilder.build(order, signableItems);
            String orderHash = SignatureUtil.sha256Hex(signedOrderData);

            order.setOrderHash(orderHash);
            order.setSignedOrderData(signedOrderData);

            orderDao.createOrder(order, items);

            if (voucherId != null) {
                voucherDao.incrementUsage(voucherId);
            }

            session.removeAttribute("cart");
            session.removeAttribute("appliedVoucher");
            session.removeAttribute("voucherError");
            session.removeAttribute("checkoutFormData");

            resp.getWriter().write("{\"success\":true}");
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            writeJson(resp, false, "Lỗi xử lý đơn hàng: " + e.getMessage());
        }
    }

    private void handleApplyVoucher(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        HttpSession session = req.getSession();
        Map<String, String> checkoutFormData = new HashMap<>();
        checkoutFormData.put("fullName", trim(req.getParameter("fullName")));
        checkoutFormData.put("phone", trim(req.getParameter("phone")));
        checkoutFormData.put("email", trim(req.getParameter("email")));
        checkoutFormData.put("country", trim(req.getParameter("country")));
        checkoutFormData.put("address", trim(req.getParameter("address")));
        checkoutFormData.put("city", trim(req.getParameter("city")));
        checkoutFormData.put("paymentMethod", trim(req.getParameter("paymentMethod")));
        checkoutFormData.put("orderNote", trim(req.getParameter("orderNote")));
        session.setAttribute("checkoutFormData", checkoutFormData);

        String code = req.getParameter("promoCode");
        Cart cart = (Cart) session.getAttribute("cart");
        if (cart == null || cart.getTotalQuantity() == 0) {
            resp.sendRedirect("cart");
            return;
        }
        if (code == null || code.trim().isEmpty()) {
            session.setAttribute("voucherError", "Vui lòng nhập mã khuyến mãi.");
            resp.sendRedirect(req.getContextPath() + "/checkout");
            return;
        }
        model.voucher.Voucher voucher = voucherDao.getByCode(code.trim());
        if (voucher == null) {
            session.setAttribute("voucherError", "Mã khuyến mãi không tồn tại.");
            session.removeAttribute("appliedVoucher");
            resp.sendRedirect(req.getContextPath() + "/checkout");
            return;
        }
        if (!voucher.isActive()) {
            session.setAttribute("voucherError", "Mã khuyến mãi không còn hiệu lực.");
            session.removeAttribute("appliedVoucher");
            resp.sendRedirect(req.getContextPath() + "/checkout");
            return;
        }
        if (voucher.getValidFrom() != null && voucher.getValidFrom().isAfter(java.time.LocalDateTime.now())) {
            session.setAttribute("voucherError", "Mã khuyến mãi chưa bắt đầu.");
            session.removeAttribute("appliedVoucher");
            resp.sendRedirect(req.getContextPath() + "/checkout");
            return;
        }
        if (voucher.getValidTo() != null && voucher.getValidTo().isBefore(java.time.LocalDateTime.now())) {
            session.setAttribute("voucherError", "Mã khuyến mãi đã hết hạn.");
            session.removeAttribute("appliedVoucher");
            resp.sendRedirect(req.getContextPath() + "/checkout");
            return;
        }
        if (voucher.getCurrentUsage() >= voucher.getMaxUsage()) {
            session.setAttribute("voucherError", "Mã khuyến mãi đã hết lượt sử dụng.");
            session.removeAttribute("appliedVoucher");
            resp.sendRedirect(req.getContextPath() + "/checkout");
            return;
        }
        if (cart.getTotalPrice() < voucher.getMinOrderAmount()) {
            session.setAttribute("voucherError", "Đơn hàng chưa đủ điều kiện áp dụng mã.");
            session.removeAttribute("appliedVoucher");
            resp.sendRedirect(req.getContextPath() + "/checkout");
            return;
        }
        session.setAttribute("appliedVoucher", voucher);
        session.removeAttribute("voucherError");
        resp.sendRedirect(req.getContextPath() + "/checkout");
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }

    private void writeJson(HttpServletResponse resp, boolean success, String message) throws IOException {
        resp.getWriter().write(
                "{\"success\":" + success + ",\"message\":\"" + escapeJson(message) + "\"}"
        );
    }

    private String escapeJson(String value) {
        if (value == null) {
            return "";
        }

        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }
}