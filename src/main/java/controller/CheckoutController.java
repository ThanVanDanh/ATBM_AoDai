package controller;

import dao.AddressDao;
import dao.KeyDao;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import model.cart.Cart;
import model.cart.CartItem;
import model.order.Order;
import model.user.Address;
import model.user.User;
import util.OrderSignatureDataBuilder;
import util.SignatureUtil;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "CheckoutController", urlPatterns = { "/checkout", "/checkout/apply-voucher" })
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
            response.sendRedirect(request.getContextPath() + "/cart");
            return;
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
            resp.getWriter().write("{\"success\":false,\"message\":\"Giỏ hàng trống.\"}");
            return;
        }

        User user = (User) session.getAttribute("account");
        if (user == null) {
            resp.getWriter().write("{\"success\":false,\"message\":\"Vui lòng đăng nhập để đặt hàng.\"}");
            return;
        }

        Integer keyId = keyDao.getActiveKeyId(user.getId());
        if (keyId == null) {
            resp.getWriter().write("{\"success\":false,\"message\":\"Bạn chưa có khóa ký. Vui lòng tạo khóa trong trang tài khoản trước khi đặt hàng.\"}");
            return;
        }

        String fullName = req.getParameter("fullName");
        String phone = req.getParameter("phone");
        String email = req.getParameter("email");
        String address = req.getParameter("address");
        String city = req.getParameter("city");
        String paymentMethod = req.getParameter("paymentMethod");
        String orderNote = req.getParameter("orderNote");

        String shippingAddress = (address != null ? address : "") + ", " + (city != null ? city : "");

        Order order = new Order();
        order.setUserId(user.getId());
        order.setOrderCode("ORD" + System.currentTimeMillis());
        order.setCustomerFullname(fullName);
        order.setCustomerPhone(phone);
        order.setCustomerEmail(email);
        order.setShippingAddress(shippingAddress);
        order.setCustomerNote(orderNote);
        order.setPaymentMethod(paymentMethod != null ? paymentMethod : "cod");

        double subtotal = cart.getTotalPrice();
        double shippingFee = (subtotal >= 300000) ? 0 : 30000;

        model.voucher.Voucher voucher = (model.voucher.Voucher) session.getAttribute("appliedVoucher");
        double discountAmount = 0;
        Integer voucherId = null;

        if (voucher != null) {
            if ("percentage".equalsIgnoreCase(voucher.getDiscountType())
                    || "percent".equalsIgnoreCase(voucher.getDiscountType())) {
                discountAmount = subtotal * (voucher.getDiscountValue() / 100.0);
            } else {
                discountAmount = voucher.getDiscountValue();
            }
            voucherId = voucher.getId();
        }

        double totalAmount = subtotal + shippingFee - discountAmount;
        if (totalAmount < 0)
            totalAmount = 0;

        order.setSubtotalAmount(subtotal);
        order.setShippingFee(shippingFee);
        order.setDiscountAmount(discountAmount);
        order.setTotalAmount(totalAmount);
        order.setVoucherId(voucherId);
        order.setOrderStatus("Đang chờ xác thực");
        order.setPaymentStatus("chưa thanh toán");
        order.setSignatureStatus("unsigned");

        List<CartItem> items = cart.getItems();

        try {
            String canonicalData = OrderSignatureDataBuilder.build(order, items);
            String orderHash = SignatureUtil.sha256Hex(canonicalData);

            order.setKeyId(keyId);
            order.setOrderHash(orderHash);
            order.setSignedOrderData(canonicalData);

            //insert vào db, chờ user ký
            orderDao.createOrder(order, items);

            if (voucherId != null) {
                voucherDao.incrementUsage(voucherId);
            }

            session.removeAttribute("cart");
            session.removeAttribute("appliedVoucher");
            session.removeAttribute("voucherError");

            resp.getWriter().write("{\"success\":true}");
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"success\":false,\"message\":\"Lỗi xử lý đơn hàng: " + e.getMessage() + "\"}");
        }
    }

    private void handleApplyVoucher(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        HttpSession session = req.getSession();
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
}
