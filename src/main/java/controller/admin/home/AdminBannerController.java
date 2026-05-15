package controller.admin.home;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dao.BannerDao;
import dao.BannerDao.BannerDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import services.CloudinaryService;
import services.CloudinaryService.UploadedImage;
import util.GsonUtil;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "AdminBannerController", urlPatterns = {
        "/admin/banner/api",
        "/admin/banner/api/*"
})
@MultipartConfig(maxFileSize = 1024 * 1024 * 10, maxRequestSize = 1024 * 1024 * 20)
public class AdminBannerController extends HttpServlet {

    private Gson gson;
    private BannerDao bannerDao;
    private CloudinaryService cloudinaryService;

    @Override
    public void init() throws ServletException {
        this.gson = GsonUtil.getGson();
        this.bannerDao = new BannerDao();
        this.cloudinaryService = new CloudinaryService();
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) {
        setJsonHeaders(resp);
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        setJsonHeaders(resp);

        String pathInfo = req.getPathInfo();

        try {
            if (pathInfo == null || pathInfo.equals("/") || pathInfo.equals("/list")) {
                List<BannerDTO> banners = bannerDao.getAllBanners();
                JsonObject response = new JsonObject();
                response.addProperty("success", true);
                response.add("banners", gson.toJsonTree(banners));
                resp.getWriter().write(gson.toJson(response));
            } else {
                String idStr = pathInfo.substring(1);
                int id = Integer.parseInt(idStr);
                BannerDTO banner = bannerDao.getById(id);

                if (banner != null) {
                    JsonObject response = new JsonObject();
                    response.addProperty("success", true);
                    response.add("banner", gson.toJsonTree(banner));
                    resp.getWriter().write(gson.toJson(response));
                } else {
                    sendErrorResponse(resp, HttpServletResponse.SC_NOT_FOUND, "Banner không tồn tại");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendErrorResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Lỗi server: " + e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        setJsonHeaders(resp);

        String pathInfo = req.getPathInfo();

        try {
            String contentType = req.getContentType();
            if (contentType == null || !contentType.toLowerCase().startsWith("multipart/")) {
                sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, "Yêu cầu multipart/form-data");
                return;
            }

            Part filePart = req.getPart("image");
            if (filePart == null || filePart.getSize() == 0) {
                sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, "Vui lòng chọn ảnh banner");
                return;
            }

            String altText = req.getParameter("altText");
            String sortOrderStr = req.getParameter("sortOrder");
            String isActiveStr = req.getParameter("isActive");

            String filename = getFileName(filePart);
            UploadedImage uploaded = cloudinaryService.upload(filePart.getInputStream(), "banner_" + System.currentTimeMillis() + "_" + filename);

            if (uploaded == null || uploaded.getSecureUrl() == null) {
                sendErrorResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Upload ảnh thất bại");
                return;
            }

            int sortOrder = sortOrderStr != null && !sortOrderStr.isEmpty()
                    ? Integer.parseInt(sortOrderStr)
                    : bannerDao.getNextSortOrder();
            boolean isActive = isActiveStr == null || isActiveStr.isEmpty() || "true".equals(isActiveStr) || "1".equals(isActiveStr);

            int newId = bannerDao.insert(
                    uploaded.getSecureUrl(),
                    altText != null ? altText : "",
                    sortOrder,
                    isActive
            );

            if (newId > 0) {
                JsonObject response = new JsonObject();
                response.addProperty("success", true);
                response.addProperty("message", "Thêm banner thành công");
                response.addProperty("bannerId", newId);
                response.addProperty("imageUrl", uploaded.getSecureUrl());
                resp.getWriter().write(gson.toJson(response));
            } else {
                sendErrorResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Lưu banner vào database thất bại");
            }

        } catch (Exception e) {
            e.printStackTrace();
            sendErrorResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Lỗi server: " + e.getMessage());
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        setJsonHeaders(resp);

        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, "Banner ID is required");
            return;
        }

        try {
            int id = Integer.parseInt(pathInfo.substring(1));
            BannerDTO existing = bannerDao.getById(id);

            if (existing == null) {
                sendErrorResponse(resp, HttpServletResponse.SC_NOT_FOUND, "Banner không tồn tại");
                return;
            }

            String contentType = req.getContentType();
            boolean isMultipart = contentType != null && contentType.toLowerCase().startsWith("multipart/");

            String imageUrl = existing.imageUrl;
            String altText = existing.altText;
            int sortOrder = existing.sortOrder;
            boolean isActive = existing.isActive;

            if (isMultipart) {
                Part filePart = req.getPart("image");

                if (filePart != null && filePart.getSize() > 0) {
                    String filename = getFileName(filePart);
                    UploadedImage uploaded = cloudinaryService.upload(filePart.getInputStream(), "banner_" + System.currentTimeMillis() + "_" + filename);

                    if (uploaded != null && uploaded.getSecureUrl() != null) {
                        imageUrl = uploaded.getSecureUrl();
                    }
                }

                String altTextParam = req.getParameter("altText");
                String sortOrderParam = req.getParameter("sortOrder");
                String isActiveParam = req.getParameter("isActive");

                if (altTextParam != null) altText = altTextParam;
                if (sortOrderParam != null && !sortOrderParam.isEmpty()) sortOrder = Integer.parseInt(sortOrderParam);
                if (isActiveParam != null) isActive = "true".equals(isActiveParam) || "1".equals(isActiveParam);

            } else {
                String body = new String(req.getInputStream().readAllBytes(), "UTF-8");
                JsonObject json = gson.fromJson(body, JsonObject.class);

                if (json.has("altText")) altText = json.get("altText").getAsString();
                if (json.has("sortOrder")) sortOrder = json.get("sortOrder").getAsInt();
                if (json.has("isActive")) isActive = json.get("isActive").getAsBoolean();
            }

            boolean success = bannerDao.update(id, imageUrl, altText, sortOrder, isActive);

            if (success) {
                JsonObject response = new JsonObject();
                response.addProperty("success", true);
                response.addProperty("message", "Cập nhật banner thành công");
                resp.getWriter().write(gson.toJson(response));
            } else {
                sendErrorResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Cập nhật thất bại");
            }

        } catch (NumberFormatException e) {
            sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, "ID không hợp lệ");
        } catch (Exception e) {
            e.printStackTrace();
            sendErrorResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Lỗi server: " + e.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        setJsonHeaders(resp);

        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, "Banner ID is required");
            return;
        }

        try {
            int id = Integer.parseInt(pathInfo.substring(1));
            BannerDTO existing = bannerDao.getById(id);

            if (existing == null) {
                sendErrorResponse(resp, HttpServletResponse.SC_NOT_FOUND, "Banner không tồn tại");
                return;
            }

            // Xóa trong database
            boolean success = bannerDao.delete(id);

            if (success) {
                JsonObject response = new JsonObject();
                response.addProperty("success", true);
                response.addProperty("message", "Xóa banner thành công");
                resp.getWriter().write(gson.toJson(response));
            } else {
                sendErrorResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Xóa thất bại");
            }

        } catch (NumberFormatException e) {
            sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, "ID không hợp lệ");
        } catch (Exception e) {
            e.printStackTrace();
            sendErrorResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Lỗi server: " + e.getMessage());
        }
    }

    private String getFileName(Part part) {
        String contentDisp = part.getHeader("content-disposition");
        for (String token : contentDisp.split(";")) {
            if (token.trim().startsWith("filename")) {
                return token.substring(token.indexOf("=") + 2, token.length() - 1);
            }
        }
        return "unknown";
    }

    private void setJsonHeaders(HttpServletResponse resp) {
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type");
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
