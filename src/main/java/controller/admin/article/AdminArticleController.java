package controller.admin.article;

import com.google.gson.Gson;
import dao.ArticleDao;
import dao.VoucherDao;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import model.article.Article;
import model.article.ArticleListDTO;
import org.jdbi.v3.core.Jdbi;
import services.ArticleService;
import services.CloudinaryService;
import util.GsonUtil;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@WebServlet(name = "AdminArticleController", urlPatterns = {
        "/admin/article/list",
        "/admin/article/get",
        "/admin/article/add",
        "/admin/article/update",
        "/admin/article/delete"
})
@MultipartConfig(maxFileSize = 1024 * 1024 * 10, maxRequestSize = 1024 * 1024 * 20)
public class AdminArticleController extends HttpServlet {
    private ArticleService articleService;
    private ArticleDao articleDao;
    private VoucherDao voucherDao;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        super.init();
        try {
            articleDao = new ArticleDao();
            voucherDao = new VoucherDao();
            Jdbi jdbi = articleDao.get();

            CloudinaryService cloudinary = new CloudinaryService();

            articleService = new ArticleService(jdbi, cloudinary);

            gson = GsonUtil.getGson();
        } catch (Exception ex) {
            ex.printStackTrace();
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
            handleGetArticle(req, resp);
        } else {
            handleListArticles(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        addCorsHeaders(resp);
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        String uri = req.getRequestURI();

        if (uri.contains("/delete")) {
            handleDelete(req, resp);
        } else if (uri.contains("/update")) {
            handleUpdate(req, resp);
        } else if (uri.contains("/add")) {
            handleAdd(req, resp);
        }
    }

    private void handleListArticles(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        System.out.println("=== handleListArticles ===");

        try {
            List<ArticleListDTO> articles = articleService.getListArticles();

            String json = gson.toJson(articles);
            resp.getWriter().write(json);
            resp.setStatus(HttpServletResponse.SC_OK);

        } catch (Exception ex) {
            ex.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"" + escapeJson(ex.getMessage()) + "\"}");
        }
    }

    private void handleGetArticle(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String idStr = req.getParameter("id");

            if (idStr == null || idStr.trim().isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\":\"ID là bắt buộc\"}");
                return;
            }

            int articleId = Integer.parseInt(idStr.trim());
            Article article = articleService.getArticle(articleId);

            if (article == null) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("{\"error\":\"Bài viết không tồn tại\"}");
                return;
            }

            String json = gson.toJson(article);
            resp.getWriter().write(json);
            resp.setStatus(HttpServletResponse.SC_OK);

        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"ID không hợp lệ\"}");
        } catch (Exception ex) {
            ex.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"" + escapeJson(ex.getMessage()) + "\"}");
        }
    }

    private void handleAdd(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String contentType = req.getContentType();
        if (contentType == null || !contentType.toLowerCase().startsWith("multipart/")) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Yêu cầu multipart/form-data");
            return;
        }

        try {
            String title = safe(req.getParameter("article-title"));
            String content = safe(req.getParameter("article-content"));
            String status = safe(req.getParameter("article-status"));
            String voucherIdStr = safe(req.getParameter("voucher-id"));
            String startDateStr = safe(req.getParameter("start-date"));
            String endDateStr = safe(req.getParameter("end-date"));

            if (title.isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.setContentType("application/json;charset=UTF-8");
                resp.getWriter().write("{\"error\":\"Tiêu đề là bắt buộc\"}");
                return;
            }

            Article article = new Article();
            article.setTitle(title);
            article.setContent(content);
            article.setStatusArticles(status.isEmpty() ? "draft" : status);

            if (!voucherIdStr.isEmpty()) {
                try {
                    article.setVoucherId(Integer.parseInt(voucherIdStr));
                } catch (NumberFormatException e) {
                    System.err.println("Invalid voucher ID: " + voucherIdStr);
                }
            }

            article.setStartDate(parseDateTime(startDateStr));
            article.setEndDate(parseDateTime(endDateStr));

            InputStream bannerStream = null;
            String bannerFilename = null;

            for (Part part : req.getParts()) {
                if ("banner-image".equals(part.getName()) && part.getSize() > 0) {
                    bannerFilename = getFilename(part);
                    bannerStream = part.getInputStream();
                    System.out.println("Banner image: " + bannerFilename);
                    break;
                }
            }

            int newId = articleService.createArticle(article, bannerStream, bannerFilename);
            resp.setContentType("application/json;charset=UTF-8");
            resp.getWriter().write("{\"success\":true,\"id\":" + newId + "}");
            resp.setStatus(HttpServletResponse.SC_OK);

        } catch (Exception ex) {
            ex.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.setContentType("application/json;charset=UTF-8");
            resp.getWriter().write("{\"error\":\"" + escapeJson(ex.getMessage()) + "\"}");
        }
    }

    private void handleUpdate(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String contentType = req.getContentType();
        if (contentType == null || !contentType.toLowerCase().startsWith("multipart/")) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Yêu cầu multipart/form-data");
            return;
        }

        try {
            String idStr = req.getParameter("article-id");
            if (idStr == null || idStr.trim().isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.setContentType("application/json;charset=UTF-8");
                resp.getWriter().write("{\"error\":\"Article ID là bắt buộc\"}");
                return;
            }

            int articleId = Integer.parseInt(idStr.trim());
            String title = safe(req.getParameter("article-title"));
            String content = safe(req.getParameter("article-content"));
            String status = safe(req.getParameter("article-status"));
            String voucherIdStr = safe(req.getParameter("voucher-id"));
            String startDateStr = safe(req.getParameter("start-date"));
            String endDateStr = safe(req.getParameter("end-date"));

            if (title.isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.setContentType("application/json;charset=UTF-8");
                resp.getWriter().write("{\"error\":\"Tiêu đề là bắt buộc\"}");
                return;
            }

            Article article = new Article(articleId);
            article.setTitle(title);
            article.setContent(content);
            article.setStatusArticles(status.isEmpty() ? "draft" : status);

            if (!voucherIdStr.isEmpty()) {
                try {
                    article.setVoucherId(Integer.parseInt(voucherIdStr));
                } catch (NumberFormatException e) {
                    System.err.println("Invalid voucher ID: " + voucherIdStr);
                }
            }

            article.setStartDate(parseDateTime(startDateStr));
            article.setEndDate(parseDateTime(endDateStr));

            InputStream bannerStream = null;
            String bannerFilename = null;

            for (Part part : req.getParts()) {
                if ("banner-image".equals(part.getName()) && part.getSize() > 0) {
                    bannerFilename = getFilename(part);
                    bannerStream = part.getInputStream();
                    break;
                }
            }

            boolean success = articleService.updateArticle(article, bannerStream, bannerFilename);
            resp.setContentType("application/json;charset=UTF-8");
            resp.getWriter().write("{\"success\":true,\"id\":" + articleId + "}");
            resp.setStatus(HttpServletResponse.SC_OK);

        } catch (Exception ex) {
            ex.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.setContentType("application/json;charset=UTF-8");
            resp.getWriter().write("{\"error\":\"" + escapeJson(ex.getMessage()) + "\"}");
        }
    }

    private void handleDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");

        try {
            String idStr = req.getParameter("id");

            if (idStr == null || idStr.trim().isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\":\"ID bài viết là bắt buộc\"}");
                return;
            }

            int articleId = Integer.parseInt(idStr.trim());
            System.out.println("Deleting article ID: " + articleId);

            if (!articleDao.exists(articleId)) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("{\"error\":\"Bài viết không tồn tại\"}");
                return;
            }

            boolean success = articleService.deleteArticle(articleId);

            if (success) {
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().write("{\"success\":true,\"message\":\"Xóa bài viết thành công\"}");
            } else {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                resp.getWriter().write("{\"error\":\"Không thể xóa bài viết\"}");
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

    private String getFilename(Part part) {
        String cd = part.getHeader("content-disposition");
        if (cd == null) return null;
        for (String t : cd.split(";")) {
            t = t.trim();
            if (t.startsWith("filename")) {
                String fn = t.substring(t.indexOf('=') + 1).trim().replace("\"", "");
                return fn.substring(Math.max(fn.lastIndexOf('/'), fn.lastIndexOf('\\')) + 1);
            }
        }
        return null;
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