package controller;

import dao.AddressDao;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import model.cart.Cart;
import model.cart.CartItem;
import model.order.Order;
import model.user.Address;
import model.user.User;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "CheckoutController", urlPatterns = { "/checkout", "/checkout/apply-voucher" })
public class CheckoutController extends HttpServlet {
    private AddressDao addressDao;
    private dao.VoucherDao voucherDao;
    private dao.OrderDao orderDao;

    @Override
    public void init() throws ServletException {
        super.init();
        try {
            this.addressDao = new AddressDao();
            this.voucherDao = new dao.VoucherDao();
            this.orderDao = new dao.OrderDao();
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

        HttpSession session = req.getSession();
        Cart cart = (Cart) session.getAttribute("cart");

        if (cart == null || cart.getTotalQuantity() == 0) {
            resp.sendRedirect("cart");
            return;
        }

        User user = (User) session.getAttribute("account");

        String fullName = req.getParameter("fullName");
        String phone = req.getParameter("phone");
        String email = req.getParameter("email");
        String address = req.getParameter("address");
        String city = req.getParameter("city");
        String paymentMethod = req.getParameter("paymentMethod");
        String orderNote = req.getParameter("orderNote");

        String shippingAddress = (address != null ? address : "") + ", " + (city != null ? city : "");

        Order order = new Order();
        if (user != null) {
            order.setUserId(user.getId());
        }
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
        order.setOrderStatus("chờ xử lý");
        order.setPaymentStatus("chưa thanh toán");

        try {
            int orderId = orderDao.createOrder(order, cart.getItems());

            if (voucherId != null) {
                voucherDao.incrementUsage(voucherId);
            }

            session.removeAttribute("cart");
            session.removeAttribute("appliedVoucher");
            session.removeAttribute("voucherError");

            if ("true".equals(req.getParameter("ajax"))) {
                resp.setContentType("application/json");
                resp.getWriter().write("{\"success\": true}");
            } else {
                resp.sendRedirect(req.getContextPath() + "/account");
            }

        } catch (Exception e) {
            e.printStackTrace();
            if ("true".equals(req.getParameter("ajax"))) {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                resp.setContentType("application/json");
                resp.getWriter()
                        .write("{\"success\": false, \"message\": \"Đặt hàng thất bại: " + e.getMessage() + "\"}");
            } else {
                req.setAttribute("errorMessage", "Đặt hàng thất bại: " + e.getMessage());
                req.getRequestDispatcher("/thanhtoan.jsp").forward(req, resp);
            }
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
