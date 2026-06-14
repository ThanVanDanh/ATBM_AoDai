package controller;

import dao.ProductDao;
import dao.PolicyDao;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import model.product.Product;
import model.product.ProductListDTO;
import model.policy.Policy;
import services.CloudinaryService;
import services.ProductService;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

@WebServlet(name = "ProductController", value = "/product-detail")
public class ProductController extends HttpServlet {
    private ProductService productService;
    private PolicyDao policyDao;

    public void init() throws ServletException {
        super.init();
        try {
            ProductDao pd = new ProductDao();
            org.jdbi.v3.core.Jdbi jdbi = pd.get();
            CloudinaryService cloudinary = new CloudinaryService();
            this.productService = new ProductService(jdbi, cloudinary);
            this.policyDao = new PolicyDao();
        } catch (Exception ex) {
            throw new ServletException("Khởi tạo ProductController thất bại: " + ex.getMessage(), ex);
        }

    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String idParam = request.getParameter("id");
            if (idParam == null) {
                response.sendRedirect("index.jsp");
                return;
            }
            int id = Integer.parseInt(idParam);

            Product product = productService.getProduct(id);
            if (product == null || !"active".equalsIgnoreCase(product.getStatusProduct())) {
                response.sendRedirect("index.jsp");
                return;
            }
            request.setAttribute("p", product);
//            san pham cung loai
            if (product != null) {
                ProductDao dao = new ProductDao();
                List<ProductListDTO> relatedProducts = dao.getRelatedProducts(product.getCategoryId(), id, 5);
                request.setAttribute("relatedProducts", relatedProducts);
                
                Policy policy = policyDao.getByCategoryId(product.getCategoryId());
                request.setAttribute("policy", policy);
            }

            // côkie san pham da xem
            String txt = "";
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie c : cookies) {
                    if (c.getName().equals("viewed_products")) {
                        txt = URLDecoder.decode(c.getValue(), StandardCharsets.UTF_8);
                        break;
                    }
                }
            }
            String newTxt = updateViewedString(txt, id);
            String encodedValue = URLEncoder.encode(newTxt, StandardCharsets.UTF_8);

            Cookie c = new Cookie("viewed_products", encodedValue);
            c.setMaxAge(60 * 60 * 24 * 3);
            c.setPath("/");
            response.addCookie(c);

            List<Integer> listIds = parseStringToList(newTxt);

            listIds.remove(Integer.valueOf(id));

            if (!listIds.isEmpty()) {
                ProductDao dao = new ProductDao();
                List<ProductListDTO> viewedList = dao.getViewedProducts(listIds);

                List<ProductListDTO> sortedViewedList = new ArrayList<>();
                for(Integer viewId : listIds) {
                    for(ProductListDTO dto : viewedList) {
                        if(dto.getId() == viewId) {
                            sortedViewedList.add(dto);
                            break;
                        }
                    }
                }

                request.setAttribute("viewedProducts", sortedViewedList);
            }



            request.getRequestDispatcher("product-information.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            response.sendRedirect("index.jsp");
        }
    }

    private String updateViewedString(String txt, int currentId) {
        String idStr = String.valueOf(currentId);
        List<String> list = new ArrayList<>();

        if (txt != null && !txt.isEmpty()) {
            String[] temp = txt.split(",");
            for (String s : temp) {
                if(!s.trim().isEmpty()) list.add(s.trim());
            }
        }
        if (list.contains(idStr)) {
            list.remove(idStr);
        }
        list.add(0, idStr);
        if (list.size() > 5) {
            list.remove(list.size() - 1);
        }

        return String.join(",", list);
    }

    private List<Integer> parseStringToList(String txt) {
        List<Integer> list = new ArrayList<>();
        if (txt == null || txt.isEmpty()) return list;
        try {
            for (String s : txt.split(",")) {
                if(!s.trim().isEmpty()) {
                    list.add(Integer.parseInt(s.trim()));
                }
            }
        } catch (Exception e) {
        }
        return list;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    }
}