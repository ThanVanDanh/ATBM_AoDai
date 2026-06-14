package controller.admin.product;

import com.google.gson.Gson;
import dao.CategoryDao;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.product.Category;
import org.jdbi.v3.core.Jdbi;
import services.CategoryService;
import java.io.IOException;
import java.util.*;

@WebServlet(name = "AdminCategoryController", urlPatterns = {
        "/admin/category/add",
        "/admin/category/list",
        "/admin/category/delete"
})
@MultipartConfig
public class AdminCategoryController extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private CategoryService categoryService;
    private CategoryDao categoryDao;
    private final Gson gson = new Gson();

    @Override
    public void init() throws ServletException {
        super.init();
        try {
            categoryDao = new CategoryDao();
            Jdbi jdbi = categoryDao.get();
            categoryService = new CategoryService(jdbi);
        } catch (Throwable t) {
            throw new ServletException("Khởi tạo CategoryService thất bại: " + t.getMessage(), t);
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
            List<Category> cats = categoryService.getAllCategories();

            Map<Integer, Integer> productCounts = categoryDao.getProductCountsForAllCategories();

            List<Map<String, Object>> response = new ArrayList<>();
            for (Category cat : cats) {
                Map<String, Object> catMap = new HashMap<>();
                catMap.put("id", cat.getId());
                catMap.put("nameCategory", cat.getNameCategory());
                catMap.put("slug", cat.getSlug());
                catMap.put("description", cat.getDescription());
                catMap.put("parentCategoryId", cat.getParentId());
                catMap.put("productCount", productCounts.getOrDefault(cat.getId(), 0));
                response.add(catMap);
            }

            resp.getWriter().write(gson.toJson(response));
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
            String name = safe(req.getParameter("category-name"));
            String slug = safe(req.getParameter("category-slug"));
            String description = safe(req.getParameter("category-description"));
            String parent = safe(req.getParameter("category-parent"));

            if (name.isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"success\":false, \"error\":\"Tên danh mục là bắt buộc\"}");
                return;
            }

            Category cat = new Category();
            cat.setNameCategory(name);
            cat.setSlug(slug.isEmpty() ? generateSlug(name) : slug);
            cat.setDescription(description.isEmpty() ? null : description);

            Integer parentId = null;
            if (!parent.isEmpty()) {
                try {
                    int parsed = Integer.parseInt(parent);
                    if (parsed > 0) {
                        parentId = parsed;
                    }
                } catch (NumberFormatException e) {
                    log("Invalid parent ID: " + parent);
                }
            }
            cat.setParentId(parentId);

            boolean isUpdate = (idStr != null && !idStr.trim().isEmpty());

            if (isUpdate) {
                int id = Integer.parseInt(idStr.trim());
                cat.setId(id);

                if (parentId != null && parentId == id) {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    resp.getWriter().write("{\"success\":false, \"error\":\"Không thể chọn chính category này làm parent\"}");
                    return;
                }

                boolean success = categoryService.updateCategory(cat);

                if (success) {
                    resp.getWriter().write("{\"success\":true, \"id\":" + id + ", \"action\":\"update\"}");
                } else {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    resp.getWriter().write("{\"success\":false, \"error\":\"Category not found\"}");
                }
            } else {
                int newId = categoryService.createCategory(cat);

                if (newId > 0) {
                    resp.getWriter().write("{\"success\":true, \"id\":" + newId + ", \"slug\":\"" + escape(cat.getSlug()) + "\", \"action\":\"add\"}");
                } else if (newId == -2) {
                    resp.setStatus(HttpServletResponse.SC_CONFLICT);
                    resp.getWriter().write("{\"success\":false, \"error\":\"Slug đã tồn tại\"}");
                } else {
                    resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    resp.getWriter().write("{\"success\":false, \"error\":\"Không thể thêm danh mục\"}");
                }
            }
        } catch (NumberFormatException ex) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"success\":false, \"error\":\"Invalid ID format\"}");
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
                resp.getWriter().write("{\"success\":false, \"error\":\"ID is required\"}");
                return;
            }

            int id = Integer.parseInt(idStr.trim());

            int childCount = categoryDao.countChildCategories(id);
            int productCount = categoryDao.countProducts(id);

            if (childCount > 0 || productCount > 0) {
                String jsonResponse = String.format(
                        "{\"success\":false, \"canDelete\":false, \"childCount\":%d, \"productCount\":%d}",
                        childCount, productCount
                );
                resp.setStatus(HttpServletResponse.SC_CONFLICT);
                resp.getWriter().write(jsonResponse);
                return;
            }

            boolean success = categoryService.deleteCategory(id);
            if (success) {
                resp.getWriter().write("{\"success\":true, \"canDelete\":true}");
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("{\"success\":false, \"error\":\"Category not found\"}");
            }

        } catch (NumberFormatException ex) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"success\":false, \"error\":\"Invalid ID format\"}");
        } catch (IllegalStateException ex) {
            resp.setStatus(HttpServletResponse.SC_CONFLICT);
            resp.getWriter().write("{\"success\":false, \"error\":\"" + escape(ex.getMessage()) + "\"}");
        } catch (Exception ex) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"success\":false, \"error\":\"" + escape(ex.getMessage()) + "\"}");
        }
    }

    private String safe(String s) {
        return s == null ? "" : s.trim();
    }

    private String generateSlug(String name) {
        if (name == null || name.isEmpty()) return "";
        return name.toLowerCase()
                .replaceAll("[àáạảãâầấậẩẫăằắặẳẵ]", "a")
                .replaceAll("[èéẹẻẽêềếệểễ]", "e")
                .replaceAll("[ìíịỉĩ]", "i")
                .replaceAll("[òóọỏõôồốộổỗơờớợởỡ]", "o")
                .replaceAll("[ùúụủũưừứựửữ]", "u")
                .replaceAll("[ỳýỵỷỹ]", "y")
                .replaceAll("[đ]", "d")
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-+|-+$", "");
    }

    private String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }
}