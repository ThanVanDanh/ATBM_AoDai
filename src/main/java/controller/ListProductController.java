package controller;

import dao.ProductDao;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import model.product.ProductListDTO;
import services.CloudinaryService;
import services.ProductService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "ListProductController", value = "/list-product")
public class ListProductController extends HttpServlet {

    private ProductDao productDao;

    @Override
    public void init() throws ServletException {
        super.init();
        try {
            this.productDao = new ProductDao();
        } catch (Exception ex) {
            throw new ServletException("Lỗi khởi tạo: " + ex.getMessage(), ex);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            int page = 1;
            int pageSize = 10;
            if (request.getParameter("page") != null) {
                try {
                    page = Integer.parseInt(request.getParameter("page"));
                } catch (NumberFormatException e) {
                    page = 1;
                }
            }

            String sortBy = request.getParameter("sort-by");
            
            String searchKeyword = request.getParameter("search");
            boolean isSearchMode = (searchKeyword != null && !searchKeyword.trim().isEmpty());
            
            if (!isSearchMode && (sortBy == null || sortBy.isEmpty())) {
                sortBy = "alpha-asc";
            }

            int totalProducts;
            int totalPages;
            List<ProductListDTO> list;

            if (isSearchMode) {
                totalProducts = productDao.countSearchResults(searchKeyword);
                totalPages = (int) Math.ceil((double) totalProducts / pageSize);
                
                if (page < 1) page = 1;
                if (page > totalPages && totalPages > 0) page = totalPages;
                
                list = productDao.searchProducts(searchKeyword, page, pageSize, sortBy);
                request.setAttribute("searchKeyword", searchKeyword);
                request.setAttribute("pageTitle", "Kết quả tìm kiếm: \"" + searchKeyword + "\"");
            } else {
                totalProducts = productDao.countActiveProducts();
                totalPages = (int) Math.ceil((double) totalProducts / pageSize);
                
                if (page < 1) page = 1;
                if (page > totalPages && totalPages > 0) page = totalPages;
                
                list = productDao.getAllActiveProductsPayload(page, pageSize, sortBy);
                request.setAttribute("pageTitle", "Tất cả sản phẩm");
            }

            request.setAttribute("list", list);
            request.setAttribute("currentPage", page);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("sortBy", sortBy);
            request.setAttribute("totalProducts", totalProducts);

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

            List<Integer> listIds = new ArrayList<>();
            if (!txt.isEmpty()) {
                try {
                    for (String s : txt.split(",")) {
                        if(!s.trim().isEmpty()) listIds.add(Integer.parseInt(s.trim()));
                    }
                } catch (Exception e) {}
            }

            if (!listIds.isEmpty()) {
                java.util.Collections.reverse(listIds);
                List<ProductListDTO> viewedList = productDao.getViewedProducts(listIds);
                request.setAttribute("viewedProducts", viewedList);
            }

            request.getRequestDispatcher("all-product.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/index.jsp");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}