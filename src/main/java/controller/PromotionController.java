package controller;

import dao.ArticleDao;
import dao.VoucherDao;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.article.ArticleListDTO;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet(name = "PromotionController", value = "/promotion")
public class PromotionController extends HttpServlet {
    private ArticleDao articleDao;
    private VoucherDao voucherDao;

    @Override
    public void init() throws ServletException {
        super.init();
        try {
            this.articleDao = new ArticleDao();
            this.voucherDao = new VoucherDao();
        } catch (Exception ex) {
            throw new ServletException("Khởi tạo PromotionController thất bại: " + ex.getMessage(), ex);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            List<ArticleListDTO> allArticles = articleDao.getListArticles();
            List<ArticleListDTO> publishedArticles = allArticles.stream()
                    .filter(article -> "published".equals(article.getStatusArticles()))
                    .collect(Collectors.toList());
            
            publishedArticles.sort((a, b) -> {
                if (a.getStartDate() != null && b.getStartDate() != null) {
                    return b.getStartDate().compareTo(a.getStartDate());
                } else if (a.getCreatedAt() != null && b.getCreatedAt() != null) {
                    return b.getCreatedAt().compareTo(a.getCreatedAt());
                }
                return 0;
            });
            
            request.setAttribute("articles", publishedArticles);
            request.getRequestDispatcher("promotion.jsp").forward(request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/home");
        }
    }
}
