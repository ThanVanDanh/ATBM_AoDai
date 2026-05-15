package controller.admin.product;

import com.google.gson.Gson;
import dao.PolicyDao;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.policy.Policy;
import org.jdbi.v3.core.Jdbi;
import services.PolicyService;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "AdminPolicyController", urlPatterns = {
        "/admin/policy/add",
        "/admin/policy/list",
        "/admin/policy/delete"
})
public class AdminPolicyController extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private PolicyService policyService;
    private PolicyDao policyDao;
    private final Gson gson = new Gson();

    @Override
    public void init() throws ServletException {
        super.init();
        try {
            policyDao = new PolicyDao();
            Jdbi jdbi = policyDao.get();
            policyService = new PolicyService(jdbi);
        } catch (Throwable t) {
            throw new ServletException("Khởi tạo PolicyService thất bại: " + t.getMessage(), t);
        }
    }

    private void addCorsHeaders(HttpServletResponse resp) {
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "GET, POST, DELETE, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type, Accept");
        resp.setHeader("Access-Control-Max-Age", "3600");
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

        try {
            List<Policy> policies = policyService.getAllPolicies();
            resp.getWriter().write(gson.toJson(policies));
            resp.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception ex) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"" + escape(ex.getMessage()) + "\"}");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        addCorsHeaders(resp);
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json;charset=UTF-8");

        try {
            String uri = req.getRequestURI();
            if (uri != null && uri.contains("/delete")) {
                handleDelete(req, resp);
                return;
            }

            String idStr = req.getParameter("id");
            String categoryIdStr = safe(req.getParameter("policy-category"));
            String policyText = safe(req.getParameter("policy-text"));

            if (categoryIdStr.isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"success\":false, \"error\":\"Danh mục là bắt buộc\"}");
                return;
            }

            if (policyText.isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"success\":false, \"error\":\"Nội dung chính sách là bắt buộc\"}");
                return;
            }

            int categoryId;
            try {
                categoryId = Integer.parseInt(categoryIdStr);
            } catch (NumberFormatException e) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"success\":false, \"error\":\"Danh mục không hợp lệ\"}");
                return;
            }

            Policy policy = new Policy();
            policy.setCategoryId(categoryId);
            policy.setPolicyText(policyText);

            boolean isUpdate = (idStr != null && !idStr.trim().isEmpty());

            if (isUpdate) {
                int id = Integer.parseInt(idStr.trim());
                policy.setId(id);

                boolean success = policyService.updatePolicy(policy);

                if (success) {
                    resp.getWriter().write("{\"success\":true, \"id\":" + id + ", \"action\":\"update\"}");
                } else {
                    resp.setStatus(HttpServletResponse.SC_CONFLICT);
                    resp.getWriter().write("{\"success\":false, \"error\":\"Danh mục này đã có chính sách khác hoặc không tìm thấy\"}");
                }
            } else {
                int newId = policyService.createPolicy(policy);

                if (newId > 0) {
                    resp.getWriter().write("{\"success\":true, \"id\":" + newId + ", \"action\":\"add\"}");
                } else if (newId == -2) {
                    resp.setStatus(HttpServletResponse.SC_CONFLICT);
                    resp.getWriter().write("{\"success\":false, \"error\":\"Danh mục này đã có chính sách\"}");
                } else {
                    resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    resp.getWriter().write("{\"success\":false, \"error\":\"Không thể thêm chính sách\"}");
                }
            }
        } catch (NumberFormatException ex) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"success\":false, \"error\":\"ID không hợp lệ\"}");
        } catch (Exception ex) {
            log("Error in doPost: " + ex.getMessage(), ex);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"success\":false, \"error\":\"" + escape(ex.getMessage()) + "\"}");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        handleDelete(req, resp);
    }

    private void handleDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        addCorsHeaders(resp);
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json;charset=UTF-8");

        try {
            String idStr = req.getParameter("id");
            if (idStr == null || idStr.trim().isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"success\":false, \"error\":\"ID là bắt buộc\"}");
                return;
            }

            int id = Integer.parseInt(idStr.trim());
            boolean success = policyService.deletePolicy(id);

            if (success) {
                resp.getWriter().write("{\"success\":true}");
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("{\"success\":false, \"error\":\"Không tìm thấy chính sách\"}");
            }

        } catch (NumberFormatException ex) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"success\":false, \"error\":\"ID không hợp lệ\"}");
        } catch (Exception ex) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"success\":false, \"error\":\"" + escape(ex.getMessage()) + "\"}");
        }
    }

    private String safe(String s) {
        return s == null ? "" : s.trim();
    }

    private String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }
}
