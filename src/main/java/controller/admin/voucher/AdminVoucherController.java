package controller.admin.voucher;

import com.google.gson.Gson;
import dao.VoucherDao;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.voucher.Voucher;
import util.GsonUtil;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@WebServlet(name = "AdminVoucherController", urlPatterns = {
        "/admin/voucher/list",
        "/admin/voucher/get",
        "/admin/voucher/add",
        "/admin/voucher/delete"
})
public class AdminVoucherController extends HttpServlet {

    private VoucherDao voucherDao;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        super.init();
        try {
            voucherDao = new VoucherDao();
            gson = GsonUtil.getGson();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new ServletException("Init failed: " + ex.getMessage(), ex);
        }
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        addCorsHeaders(resp);
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        addCorsHeaders(resp);
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json;charset=UTF-8");

        String uri = req.getRequestURI();

        if (uri.contains("/get")) {
            handleGetVoucher(req, resp);
        } else {
            handleListVouchers(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        addCorsHeaders(resp);
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        String uri = req.getRequestURI();

        if (uri.contains("/delete")) {
            handleDelete(req, resp);
        } else {
            handleAddOrUpdate(req, resp);
        }
    }

    private void handleListVouchers(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            List<Voucher> vouchers = voucherDao.getAll();

            String json = gson.toJson(vouchers);
            resp.getWriter().write(json);
            resp.setStatus(HttpServletResponse.SC_OK);

        } catch (Exception ex) {
            ex.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"" + escapeJson(ex.getMessage()) + "\"}");
        }
    }

    private void handleGetVoucher(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String idStr = req.getParameter("id");

            if (idStr == null || idStr.trim().isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\":\"ID là bắt buộc\"}");
                return;
            }

            int voucherId = Integer.parseInt(idStr.trim());
            Voucher voucher = voucherDao.getById(voucherId);

            if (voucher == null) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("{\"error\":\"Voucher không tồn tại\"}");
                return;
            }

            String json = gson.toJson(voucher);
            resp.getWriter().write(json);
            resp.setStatus(HttpServletResponse.SC_OK);

        } catch (Exception ex) {
            ex.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"" + escapeJson(ex.getMessage()) + "\"}");
        }
    }

    private void handleAddOrUpdate(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");

        try {
            String idStr = safe(req.getParameter("voucher-id"));
            String code = safe(req.getParameter("voucher-code"));
            String type = safe(req.getParameter("discount-type"));
            String valueStr = safe(req.getParameter("discount-value"));
            String minAmountStr = safe(req.getParameter("min-order-amount"));
            String maxUsageStr = safe(req.getParameter("max-usage"));
            String validFromStr = safe(req.getParameter("valid-from"));
            String validToStr = safe(req.getParameter("valid-to"));
            String activeStr = safe(req.getParameter("is-active"));

            if (code.isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\":\"Mã voucher là bắt buộc\"}");
                return;
            }

            boolean isUpdate = !idStr.isEmpty();
            int voucherId = isUpdate ? Integer.parseInt(idStr) : 0;

            if (!isUpdate && voucherDao.existsByCode(code)) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\":\"Mã voucher đã tồn tại\"}");
                return;
            }

            if (isUpdate && voucherDao.existsByCodeExcept(code, voucherId)) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\":\"Mã voucher đã tồn tại\"}");
                return;
            }

            Voucher voucher = isUpdate ? new Voucher(voucherId) : new Voucher();
            voucher.setVoucherCode(code);
            voucher.setDiscountType(type.isEmpty() ? "percentage" : type);
            voucher.setDiscountValue(Double.parseDouble(valueStr.isEmpty() ? "0" : valueStr));
            voucher.setMinOrderAmount(Double.parseDouble(minAmountStr.isEmpty() ? "0" : minAmountStr));
            voucher.setMaxUsage(Integer.parseInt(maxUsageStr.isEmpty() ? "100" : maxUsageStr));
            voucher.setValidFrom(parseDateTime(validFromStr));
            voucher.setValidTo(parseDateTime(validToStr));
            voucher.setActive("true".equals(activeStr) || "1".equals(activeStr));

            if (isUpdate) {
                Voucher existing = voucherDao.getById(voucherId);
                voucher.setCurrentUsage(existing.getCurrentUsage());

                boolean success = voucherDao.update(voucher);
                resp.getWriter().write("{\"success\":true,\"id\":" + voucherId + "}");
            } else {
                int newId = voucherDao.insert(voucher);
                resp.getWriter().write("{\"success\":true,\"id\":" + newId + "}");
            }

            resp.setStatus(HttpServletResponse.SC_OK);

        } catch (Exception ex) {
            ex.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"" + escapeJson(ex.getMessage()) + "\"}");
        }
    }
    private void handleDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        System.out.println("=== handleDelete Voucher ===");

        resp.setContentType("application/json;charset=UTF-8");

        try {
            String idStr = req.getParameter("id");

            if (idStr == null || idStr.trim().isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\":\"ID voucher là bắt buộc\"}");
                return;
            }

            int voucherId = Integer.parseInt(idStr.trim());
            System.out.println("Deleting voucher ID: " + voucherId);
            boolean success = voucherDao.delete(voucherId);

            if (success) {
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().write("{\"success\":true,\"message\":\"Xóa voucher thành công\"}");
            } else {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                resp.getWriter().write("{\"error\":\"Không thể xóa voucher\"}");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"" + escapeJson(ex.getMessage()) + "\"}");
        }
    }

    private void addCorsHeaders(HttpServletResponse resp) {
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "GET, POST, DELETE, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type, Accept");
        resp.setHeader("Access-Control-Max-Age", "3600");
    }

    private String safe(String s) {
        return s == null ? "" : s.trim();
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
    }

    private LocalDateTime parseDateTime(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDateTime.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (Exception e) {
            System.err.println("Failed to parse date: " + dateStr);
            return null;
        }
    }
}