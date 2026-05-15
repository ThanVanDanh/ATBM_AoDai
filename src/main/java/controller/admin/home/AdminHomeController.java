package controller.admin.home;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dao.CategoryDao;
import dao.HomeDao;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.home.Home;
import model.product.Category;
import util.GsonUtil;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@WebServlet(name = "AdminHomeController", urlPatterns = {
        "/admin/home",
        "/admin/home/api/*"
})
public class AdminHomeController extends HttpServlet {

    private Gson gson;
    private CategoryDao categoryDao;
    private HomeDao homeDao;

    @Override
    public void init() throws ServletException {
        this.gson = GsonUtil.getGson();
        this.categoryDao = new CategoryDao();
        this.homeDao = new HomeDao();
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) {
        setJsonHeaders(resp);
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String servletPath = req.getServletPath();
        String pathInfo = req.getPathInfo();

        System.out.println("=== AdminHomeController GET ===");
        System.out.println("servletPath: " + servletPath);
        System.out.println("pathInfo: " + pathInfo);

        if (servletPath.startsWith("/admin/home/api")) {
            handleApiGetConfig(req, resp, pathInfo);
        } else {
            handleAdminPage(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String servletPath = req.getServletPath();
        String pathInfo = req.getPathInfo();

        System.out.println("=== AdminHomeController POST ===");
        System.out.println("servletPath: " + servletPath);
        System.out.println("pathInfo: " + pathInfo);

        if (!servletPath.startsWith("/admin/home/api")) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        handleApiSaveConfig(req, resp, pathInfo);
    }

    private void handleAdminPage(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            List<Category> categories = categoryDao.getAll();
            req.setAttribute("categories", categories);
        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("categories", new ArrayList<Category>());
        }

        req.getRequestDispatcher("/admin/home.jsp").forward(req, resp);
    }

    private void handleApiGetConfig(HttpServletRequest req, HttpServletResponse resp, String pathInfo)
            throws IOException {
        setJsonHeaders(resp);

        String sectionKey = extractSectionKey(pathInfo);
        if (sectionKey == null || sectionKey.isBlank()) {
            try {
                List<String> allKeys = homeDao.getAllSectionKeys();
                JsonObject response = new JsonObject();
                response.addProperty("success", true);
                response.add("sections", gson.toJsonTree(allKeys));
                resp.getWriter().write(gson.toJson(response));
            } catch (Exception e) {
                e.printStackTrace();
                sendErrorResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "Database error: " + e.getMessage());
            }
            return;
        }

        try {
            String title = homeDao.getSectionTitle(sectionKey);
            List<Home> tabs = homeDao.getSectionTabs(sectionKey);

            JsonObject response = new JsonObject();
            response.addProperty("key", sectionKey);
            response.addProperty("title", title != null ? title : "");

            if (sectionKey.startsWith("set_") && tabs != null && !tabs.isEmpty()) {
                response.addProperty("categoryId", tabs.get(0).getCategoryId());
            }

            response.add("tabs", gson.toJsonTree(tabs));

            resp.getWriter().write(gson.toJson(response));

        } catch (Exception e) {
            e.printStackTrace();
            sendErrorResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error: " + e.getMessage());
        }
    }

    private void handleApiSaveConfig(HttpServletRequest req, HttpServletResponse resp, String pathInfo)
            throws IOException {
        setJsonHeaders(resp);

        String sectionKey = extractSectionKey(pathInfo);
        if (sectionKey == null || sectionKey.isBlank()) {
            sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, "Section key is required");
            return;
        }

        try {
            String body = new String(req.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            JsonObject json = gson.fromJson(body, JsonObject.class);

            String title = json.has("title") && !json.get("title").isJsonNull()
                    ? json.get("title").getAsString()
                    : "";

            int maxTabs = sectionKey.startsWith("set_") ? 1 : 4;

            List<Home> tabs = parseTabs(json, sectionKey, maxTabs);

            validateTabs(tabs);

            boolean success = homeDao.saveSection(sectionKey, title, tabs, maxTabs);

            if (success) {
                JsonObject response = new JsonObject();
                response.addProperty("success", true);
                response.addProperty("message", "Lưu cấu hình thành công");
                resp.getWriter().write(gson.toJson(response));
            } else {
                sendErrorResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Lỗi khi lưu vào database");
            }

        } catch (IllegalArgumentException e) {
            System.err.println(" Validation error: " + e.getMessage());
            sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            System.err.println(" Error saving config: " + e.getMessage());
            e.printStackTrace();
            sendErrorResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error: " + e.getMessage());
        }
    }

    private String extractSectionKey(String pathInfo) {
        if (pathInfo == null || pathInfo.equals("/")) {
            return null;
        }
        return pathInfo.substring(1);
    }

    private List<Home> parseTabs(JsonObject json, String sectionKey, int maxTabs) {
        List<Home> tabs = new ArrayList<>();

        if (sectionKey.startsWith("set_")) {
            if (!json.has("categoryId")) {
                throw new IllegalArgumentException("Vui lòng chọn danh mục cho Set đồ");
            }
            int categoryId = json.get("categoryId").getAsInt();
            Home h = new Home();
            h.setPosition(1);
            h.setCategoryId(categoryId);
            tabs.add(h);
        } else {
            if (!json.has("tabs") || !json.get("tabs").isJsonArray()) {
                throw new IllegalArgumentException("Dữ liệu tabs không hợp lệ");
            }

            JsonArray tabsArray = json.getAsJsonArray("tabs");
            for (JsonElement element : tabsArray) {
                JsonObject tabObj = element.getAsJsonObject();

                if (!tabObj.has("position") || !tabObj.has("categoryId")) {
                    continue;
                }

                int position = tabObj.get("position").getAsInt();
                int categoryId = tabObj.get("categoryId").getAsInt();

                if (position < 1 || position > maxTabs) {
                    throw new IllegalArgumentException("Position phải từ 1 đến " + maxTabs);
                }

                Home h = new Home();
                h.setPosition(position);
                h.setCategoryId(categoryId);
                tabs.add(h);
            }

            if (tabs.isEmpty()) {
                throw new IllegalArgumentException("Vui lòng chọn ít nhất 1 danh mục");
            }
        }

        return tabs;
    }

    private void validateTabs(List<Home> tabs) {
        Set<Integer> seenCategories = new HashSet<>();

        for (Home tab : tabs) {
            int categoryId = tab.getCategoryId();

            Category category = categoryDao.getById(categoryId);
            if (category == null) {
                throw new IllegalArgumentException("Danh mục không tồn tại (ID: " + categoryId + ")");
            }
            if (!seenCategories.add(categoryId)) {
                throw new IllegalArgumentException("Không được chọn trùng danh mục (ID: " + categoryId + ")");
            }
        }
    }

    private void setJsonHeaders(HttpServletResponse resp) {
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json;charset=UTF-8");
    }

    private void sendErrorResponse(HttpServletResponse resp, int statusCode, String message) throws IOException {
        resp.setStatus(statusCode);
        JsonObject error = new JsonObject();
        error.addProperty("success", false);
        error.addProperty("error", message);
        resp.getWriter().write(gson.toJson(error));
    }
}
