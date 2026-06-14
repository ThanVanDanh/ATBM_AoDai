package controller.admin.product;

import com.google.gson.Gson;
import dao.ProductDao;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import model.product.Product;
import model.product.ProductListDTO;
import model.product.ProductVariant;
import services.CloudinaryService;
import services.ProductService;
import services.ProductService.ImageUpload;
import org.jdbi.v3.core.Jdbi;
import util.GsonUtil;
import util.PaginationUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@WebServlet(name = "AdminProductController", urlPatterns = {
        "/admin/product/add",
        "/admin/product/delete",
        "/admin/product/update",
        "/admin/product/get"
})
@MultipartConfig(maxFileSize = 1024 * 1024 * 10, maxRequestSize = 1024 * 1024 * 50)
public class AdminProductController extends HttpServlet {

    private ProductService productService;
    private ProductDao productDao;
    private Gson gson = new Gson();

    @Override
    public void init() throws ServletException {
        super.init();
        try {
            productDao = new ProductDao();
            Jdbi jdbi = productDao.get();

            CloudinaryService cloudinary = new CloudinaryService();

            productService = new ProductService(jdbi, cloudinary);
            gson = GsonUtil.getGson();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new ServletException("Init failed: " + ex.getMessage(), ex);
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

        String uri = req.getRequestURI();

        if (uri != null && uri.contains("/get")) {
            handleGetProduct(req, resp);
            return;
        }

        try {
            if (productService == null) {
                throw new IllegalStateException("ProductService chưa tạo");
            }

            int pageSize = 10;
            String sortBy = req.getParameter("sort");

            String searchKeyword = req.getParameter("search");
            boolean isSearchMode = (searchKeyword != null && !searchKeyword.trim().isEmpty());

            if (!isSearchMode && (sortBy == null || sortBy.isEmpty())) {
                sortBy = "id-desc";
            }
            
            int totalProducts;
            List<ProductListDTO> products;

            if (isSearchMode) {
                totalProducts = productDao.countSearchResultsAdmin(searchKeyword);
                PaginationUtils.PageInfo pageInfo = PaginationUtils.calculate(
                        req.getParameter("page"),
                        totalProducts,
                        pageSize
                );

                products = productDao.searchProductsAdmin(searchKeyword, pageInfo.getCurrentPage(), pageSize, sortBy);

                Map<String, Object> response = new java.util.HashMap<>();
                response.put("products", products);
                response.put("currentPage", pageInfo.getCurrentPage());
                response.put("totalPages", pageInfo.getTotalPages());
                response.put("totalProducts", totalProducts);
                response.put("searchKeyword", searchKeyword);

                String json = gson.toJson(response);
                resp.getWriter().write(json);
                resp.setStatus(HttpServletResponse.SC_OK);
            } else {
                totalProducts = productService.countTotalProducts();
                PaginationUtils.PageInfo pageInfo = PaginationUtils.calculate(
                        req.getParameter("page"),
                        totalProducts,
                        pageSize
                );

                products = productService.getListProductWithPaginationAndSort(
                        pageSize,
                        pageInfo.getOffset(),
                        sortBy
                );
                Map<String, Object> response = new java.util.HashMap<>();
                response.put("products", products);
                response.put("currentPage", pageInfo.getCurrentPage());
                response.put("totalPages", pageInfo.getTotalPages());
                response.put("totalProducts", totalProducts);

                String json = gson.toJson(response);
                resp.getWriter().write(json);
                resp.setStatus(HttpServletResponse.SC_OK);
            }

        } catch (Exception ex) {
            ex.printStackTrace();

            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"" + escapeJson(ex.getMessage()) + "\"}");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        addCorsHeaders(resp);
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        String uri = req.getRequestURI();

        if (uri != null && uri.contains("/delete")) {
            handleDelete(req, resp);
            return;
        }

        if (uri != null && uri.contains("/update")) {
            handleUpdate(req, resp);
            return;
        }

        String action = req.getParameter("action");
        if ("delete".equals(action)) {
            handleDelete(req, resp);
            return;
        }

        handleAdd(req, resp);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        handleDelete(req, resp);
    }

    private void handleGetProduct(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");

        try {
            String idStr = req.getParameter("id");

            if (idStr == null || idStr.trim().isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"success\":false, \"error\":\"ID là bắt buộc\"}");
                return;
            }

            int productId;
            try {
                productId = Integer.parseInt(idStr.trim());
            } catch (NumberFormatException e) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"success\":false, \"error\":\"ID không hợp lệ\"}");
                return;
            }

            Product product = productService.getProduct(productId);

            if (product == null) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("{\"success\":false, \"error\":\"Sản phẩm không tồn tại\"}");
                return;
            }

            String json = gson.toJson(product);
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(json);

        } catch (Exception ex) {
            ex.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"success\":false, \"error\":\"" + escapeJson(ex.getMessage()) + "\"}");
        }
    }

    private void handleUpdate(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String contentType = req.getContentType();
        if (contentType == null || !contentType.toLowerCase().startsWith("multipart/")) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Yêu cầu multipart/form-data");
            return;
        }

        try {
            String idStr = req.getParameter("product-id");
            if (idStr == null || idStr.trim().isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.setContentType("application/json;charset=UTF-8");
                resp.getWriter().write("{\"success\":false,\"error\":\"Product ID là bắt buộc\"}");
                return;
            }

            int productId = Integer.parseInt(idStr.trim());

            String name = safe(req.getParameter("product-name"));
            String code = safe(req.getParameter("product-code"));
            String description = safe(req.getParameter("product-description"));
            String status = safe(req.getParameter("product-status"));
            String cat = safe(req.getParameter("product-category"));

            if (name.isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.setContentType("application/json;charset=UTF-8");
                resp.getWriter().write("{\"success\":false,\"error\":\"Tên sản phẩm bắt buộc\"}");
                return;
            }

            int categoryId = 0;
            if (!cat.isEmpty()) {
                try {
                    categoryId = Integer.parseInt(cat);
                } catch (Exception e) {
                }
            }

            Product product = new Product(productId);
            product.setNameProduct(name);
            product.setProductCode(code);
            product.setDescription(description);
            product.setStatusProduct(status.isEmpty() ? "active" : status);
            product.setCategoryId(categoryId);

            List<ProductVariant> variants = new ArrayList<>();
            String[] skus = optional(req.getParameterValues("variant-sku[]"), req.getParameterValues("variant-sku"));

            if (skus != null) {
                String[] sizes = optional(req.getParameterValues("variant-size[]"), req.getParameterValues("variant-size"));
                String[] colors = optional(req.getParameterValues("variant-color[]"), req.getParameterValues("variant-color"));
                String[] prices = optional(req.getParameterValues("variant-price[]"), req.getParameterValues("variant-price"));
                String[] stocks = optional(req.getParameterValues("variant-quantity[]"), req.getParameterValues("variant-quantity"));

                for (int i = 0; i < skus.length; i++) {
                    String sku = get(skus, i);
                    if (sku == null || sku.isEmpty()) continue;

                    ProductVariant v = new ProductVariant(0);
                    v.setSku(sku);
                    v.setSize(get(sizes, i));
                    v.setColor(get(colors, i));

                    double price = 0;
                    try {
                        price = Double.parseDouble(get(prices, i));
                    } catch (Exception e) {
                    }
                    v.setCurrentPrice(price);

                    int stock = 0;
                    try {
                        stock = Integer.parseInt(get(stocks, i));
                    } catch (Exception e) {
                    }
                    v.setStockQuantity(stock);

                    variants.add(v);
                }
            }
            List<Integer> keepImageIds = new ArrayList<>();
            List<Boolean> keepImageThumbs = new ArrayList<>();
            String[] keepIds = optional(req.getParameterValues("keepImageId[]"), req.getParameterValues("keepImageId"));
            String[] keepThumbs = optional(req.getParameterValues("keepImageIsThumb[]"), req.getParameterValues("keepImageIsThumb"));
            
            if (keepIds != null) {
                for (int i = 0; i < keepIds.length; i++) {
                    try {
                        keepImageIds.add(Integer.parseInt(keepIds[i].trim()));
                        boolean isThumb = "1".equals(get(keepThumbs, i));
                        keepImageThumbs.add(isThumb);
                    } catch (Exception e) {
                        System.err.println("Invalid keepImageId: " + keepIds[i]);
                    }
                }
            }
            List<ImageUpload> uploads = new ArrayList<>();
            String[] alts = optional(req.getParameterValues("productImageAlt[]"), req.getParameterValues("productImageAlt"));
            String[] thumbs = optional(req.getParameterValues("productImageIsThumb[]"), req.getParameterValues("productImageIsThumb"));

            int idx = 0;
            for (Part part : req.getParts()) {
                if (!"productImages".equals(part.getName())) continue;
                if (part.getSize() == 0) continue;

                String filename = getFilename(part);
                InputStream is = part.getInputStream();
                String alt = get(alts, idx);
                boolean thumb = "1".equals(get(thumbs, idx));

                uploads.add(new ImageUpload(is, filename, alt, thumb));
                idx++;
            }
            boolean success = productService.updateProduct(product, variants, uploads, keepImageIds, keepImageThumbs);

            resp.setContentType("application/json;charset=UTF-8");
            resp.getWriter().write("{\"success\":true,\"id\":" + productId + "}");
            resp.setStatus(HttpServletResponse.SC_OK);

        } catch (Exception ex) {
            ex.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.setContentType("application/json;charset=UTF-8");
            resp.getWriter().write("{\"success\":false,\"error\":\"" + escapeJson(ex.getMessage()) + "\"}");
        }
    }

    private void handleDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");

        try {
            String idStr = req.getParameter("id");
            if (idStr == null || idStr.trim().isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"success\":false, \"error\":\"ID sản phẩm là bắt buộc\"}");
                return;
            }

            int productId;
            try {
                productId = Integer.parseInt(idStr.trim());
            } catch (NumberFormatException e) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"success\":false, \"error\":\"ID không hợp lệ\"}");
                return;
            }

            if (!productDao.exists(productId)) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("{\"success\":false, \"error\":\"Sản phẩm không tồn tại\"}");
                return;
            }

            boolean success = productDao.delete(productId);

            if (success) {
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().write("{\"success\":true, \"message\":\"Xóa sản phẩm thành công\"}");
            } else {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                resp.getWriter().write("{\"success\":false, \"error\":\"Không thể xóa sản phẩm\"}");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"success\":false, \"error\":\"" + escapeJson(ex.getMessage()) + "\"}");
        }
    }

    private void handleAdd(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String contentType = req.getContentType();
        if (contentType == null || !contentType.toLowerCase().startsWith("multipart/")) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Yêu cầu multipart/form-data");
            return;
        }

        try {
            String name = safe(req.getParameter("product-name"));
            String code = safe(req.getParameter("product-code"));
            String description = safe(req.getParameter("product-description"));
            String status = safe(req.getParameter("product-status"));
            String cat = safe(req.getParameter("product-category"));

            if (name.isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.setContentType("application/json;charset=UTF-8");
                resp.getWriter().write("{\"success\":false,\"error\":\"Tên sản phẩm bắt buộc\"}");
                return;
            }

            int categoryId = 0;
            if (!cat.isEmpty()) {
                try {
                    categoryId = Integer.parseInt(cat);
                } catch (Exception e) {
                }
            }

            Product product = new Product(0);
            product.setNameProduct(name);
            product.setProductCode(code);
            product.setDescription(description);
            product.setStatusProduct(status.isEmpty() ? "active" : status);
            product.setCategoryId(categoryId);

            List<ProductVariant> variants = new ArrayList<>();
            String[] skus = optional(req.getParameterValues("variant-sku[]"), req.getParameterValues("variant-sku"));

            if (skus != null) {
                String[] sizes = optional(req.getParameterValues("variant-size[]"), req.getParameterValues("variant-size"));
                String[] colors = optional(req.getParameterValues("variant-color[]"), req.getParameterValues("variant-color"));
                String[] prices = optional(req.getParameterValues("variant-price[]"), req.getParameterValues("variant-price"));
                String[] stocks = optional(req.getParameterValues("variant-quantity[]"), req.getParameterValues("variant-quantity"));

                for (int i = 0; i < skus.length; i++) {
                    String sku = get(skus, i);
                    if (sku == null || sku.isEmpty()) continue;

                    ProductVariant v = new ProductVariant(0);
                    v.setSku(sku);
                    v.setSize(get(sizes, i));
                    v.setColor(get(colors, i));

                    double price = 0;
                    try {
                        price = Double.parseDouble(get(prices, i));
                    } catch (Exception e) {
                    }
                    v.setCurrentPrice(price);

                    int stock = 0;
                    try {
                        stock = Integer.parseInt(get(stocks, i));
                    } catch (Exception e) {
                    }
                    v.setStockQuantity(stock);

                    variants.add(v);
                }
            }
            System.out.println("Variants: " + variants.size());

            List<ImageUpload> uploads = new ArrayList<>();
            String[] alts = optional(req.getParameterValues("productImageAlt[]"), req.getParameterValues("productImageAlt"));
            String[] thumbs = optional(req.getParameterValues("productImageIsThumb[]"), req.getParameterValues("productImageIsThumb"));

            int idx = 0;
            for (Part part : req.getParts()) {
                if (!"productImages".equals(part.getName())) continue;
                if (part.getSize() == 0) continue;

                String filename = getFilename(part);
                InputStream is = part.getInputStream();
                String alt = get(alts, idx);
                boolean thumb = "1".equals(get(thumbs, idx));

                uploads.add(new ImageUpload(is, filename, alt, thumb));
                idx++;
            }

            int newId = productService.createProduct(product, variants, uploads);

            resp.setContentType("application/json;charset=UTF-8");
            resp.getWriter().write("{\"success\":true,\"id\":" + newId + "}");
            resp.setStatus(HttpServletResponse.SC_OK);

        } catch (Exception ex) {
            ex.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.setContentType("application/json;charset=UTF-8");
            resp.getWriter().write("{\"success\":false,\"error\":\"" + escapeJson(ex.getMessage()) + "\"}");
        }
    }

    private String safe(String s) {
        return s == null ? "" : s.trim();
    }

    private String[] optional(String[] a, String[] b) {
        return a != null ? a : b;
    }

    private String get(String[] arr, int i) {
        return (arr != null && i >= 0 && i < arr.length) ? arr[i] : null;
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
}