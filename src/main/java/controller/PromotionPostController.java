package controller;

import dao.ArticleDao;
import dao.VoucherDao;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.article.Article;
import model.voucher.Voucher;

import java.io.IOException;

@WebServlet(name = "PromotionPostController", value = "/promotion-post")
public class PromotionPostController extends HttpServlet {
    private ArticleDao articleDao;
    private VoucherDao voucherDao;

    @Override
    public void init() throws ServletException {
        super.init();
        try {
            this.articleDao = new ArticleDao();
            this.voucherDao = new VoucherDao();
        } catch (Exception ex) {
            throw new ServletException(ex.getMessage(), ex);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String idParam = request.getParameter("id");
            
            if (idParam == null || idParam.trim().isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/promotion");
                return;
            }
            
            int articleId = Integer.parseInt(idParam);
            Article article = articleDao.getById(articleId);
            
            if (article == null || !"published".equals(article.getStatusArticles())) {
                response.sendRedirect(request.getContextPath() + "/promotion");
                return;
            }
            
            if (article.getVoucherId() != null && article.getVoucherId() > 0) {
                Voucher voucher = voucherDao.getById(article.getVoucherId());
                request.setAttribute("voucher", voucher);
            }
            
            request.setAttribute("article", article);
            request.getRequestDispatcher("promotionsPost.jsp").forward(request, response);
            
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/promotion");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/home");
        }
    }
}
