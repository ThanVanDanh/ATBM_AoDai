package controller;

import dao.CategoryDao;
import dao.ProductDao;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import model.product.ProductListDTO;
import model.product.Category;
import services.CloudinaryService;
import services.ProductService;
import util.PaginationUtils;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "CategoryController", urlPatterns = {"/danh-muc/*"})
public class CategoryController extends HttpServlet {

    private ProductService productService;
    private CategoryDao categoryDao;
    private ProductDao productDao;

    @Override
    public void init() throws ServletException {
        super.init();
        try {
            this.productDao = new ProductDao();
            this.categoryDao = new CategoryDao();
            this.productService = new ProductService(productDao.get(), new CloudinaryService());
        } catch (Exception ex) {
            throw new ServletException("Lỗi khởi tạo CategoryController: " + ex.getMessage(), ex);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String pathInfo = request.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }
        String slug = pathInfo.substring(1);

        try {
            List<Category> listCategories = categoryDao.getAll();
            request.setAttribute("listCategories", listCategories);
//            danh muc
            Category currentCategory = categoryDao.getCategoryBySlug(slug);
            if (currentCategory == null) {
                response.sendError(404, "Danh mục không tồn tại");
                return;
            }
//            phan trang
            int pageSize = 10;
            String sortBy = request.getParameter("sort-by");
            if (sortBy == null || sortBy.isEmpty()) {
                sortBy = "alpha-asc";
            }

            int totalProducts = categoryDao.countProductsByCategory(currentCategory.getId());
            
            PaginationUtils.PageInfo pageInfo = PaginationUtils.calculate(request.getParameter("page"), totalProducts, pageSize);

            List<ProductListDTO> list = categoryDao.getProductsByCategoryPayload(currentCategory.getId(), pageInfo.getCurrentPage(), pageSize, sortBy);

            request.setAttribute("currentCategory", currentCategory);
            request.setAttribute("list", list);

            request.setAttribute("currentPage", pageInfo.getCurrentPage());
            request.setAttribute("totalPages", pageInfo.getTotalPages());
            request.setAttribute("sortBy", sortBy);
//            cookie san pham da xem
            String txt = "";
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie c : cookies) {
                    if (c.getName().equals("viewed_products")) {
                        txt = java.net.URLDecoder.decode(c.getValue(), java.nio.charset.StandardCharsets.UTF_8);
                        break;
                    }
                }
            }

            List<Integer> listIds = new java.util.ArrayList<>();
            if (!txt.isEmpty()) {
                try {
                    for (String s : txt.split(",")) {
                        if(!s.trim().isEmpty()) listIds.add(Integer.parseInt(s.trim()));
                    }
                } catch (Exception e) {}
            }

            if (!listIds.isEmpty()) {
                List<model.product.ProductListDTO> viewedList = productDao.getViewedProducts(listIds);
                request.setAttribute("viewedProducts", viewedList);
            }
            request.getRequestDispatcher("/list-product.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/index.jsp");
        }
    }
}