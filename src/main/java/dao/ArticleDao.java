package dao;

import model.article.Article;
import model.article.ArticleListDTO;
import model.voucher.Voucher;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;

import java.util.List;

public class ArticleDao extends BaseDao {
    private final Jdbi jdbi;

    public ArticleDao() {
        this.jdbi = get();
    }

    public List<ArticleListDTO> getListArticles() {
        String sql = "SELECT " +
                "a.id, " +
                "a.title, " +
                "a.status_articles AS statusArticles, " +
                "a.start_date AS startDate, " +
                "a.end_date AS endDate, " +
                "a.created_at AS createdAt, " +
                "a.banner_image_url AS bannerImageUrl, " +
                "a.voucher_id AS voucherId, " +
                "v.voucher_code AS voucherCode, " +
                "v.discount_type AS discountType, " +
                "v.discount_value AS discountValue " +
                "FROM Articles a " +
                "LEFT JOIN Vouchers v ON a.voucher_id = v.id " +
                "ORDER BY a.created_at DESC";

        return jdbi.withHandle(handle ->
                handle.createQuery(sql)
                        .mapToBean(ArticleListDTO.class)
                        .list()
        );
    }

    public List<ArticleListDTO> getPublishedArticles() {
        String sql = "SELECT " +
                "a.id, " +
                "a.title, " +
                "a.status_articles AS statusArticles, " +
                "a.start_date AS startDate, " +
                "a.end_date AS endDate, " +
                "a.created_at AS createdAt, " +
                "a.banner_image_url AS bannerImageUrl, " +
                "a.voucher_id AS voucherId, " +
                "v.voucher_code AS voucherCode, " +
                "v.discount_type AS discountType, " +
                "v.discount_value AS discountValue " +
                "FROM Articles a " +
                "LEFT JOIN Vouchers v ON a.voucher_id = v.id " +
                "WHERE a.status_articles = 'published' " +
                "AND a.start_date <= NOW() " +
                "AND (a.end_date IS NULL OR a.end_date >= NOW()) " +
                "ORDER BY a.created_at DESC";

        return jdbi.withHandle(handle ->
                handle.createQuery(sql)
                        .mapToBean(ArticleListDTO.class)
                        .list()
        );
    }

    public Article getById(int id) {
        return jdbi.withHandle(handle -> {
            Article article = handle.createQuery("SELECT * FROM Articles WHERE id = :id")
                    .bind("id", id)
                    .mapToBean(Article.class)
                    .findFirst()
                    .orElse(null);

            if (article != null && article.getVoucherId() != null) {
                Voucher voucher = handle.createQuery("SELECT * FROM Vouchers WHERE id = :voucherId")
                        .bind("voucherId", article.getVoucherId())
                        .mapToBean(Voucher.class)
                        .findFirst()
                        .orElse(null);

                article.setVoucher(voucher);
            }

            return article;
        });
    }

    public Article getByIdWithVoucher(int id) {
        return getById(id);
    }

    public int insert(Article article) {
        return jdbi.withHandle(handle -> insert(handle, article));
    }

    public int insert(Handle handle, Article article) {
        String sql = "INSERT INTO Articles " +
                "(voucher_id, title, content, banner_image_url, status_articles, start_date, end_date) " +
                "VALUES (:voucherId, :title, :content, :banner, :status, :startDate, :endDate)";

        return handle.createUpdate(sql)
                .bind("voucherId", article.getVoucherId())
                .bind("title", article.getTitle())
                .bind("content", article.getContent())
                .bind("banner", article.getBannerImageUrl())
                .bind("status", article.getStatusArticles())
                .bind("startDate", article.getStartDate())
                .bind("endDate", article.getEndDate())
                .executeAndReturnGeneratedKeys("id")
                .mapTo(int.class)
                .one();
    }

    public boolean update(Article article) {
        return jdbi.withHandle(handle -> update(handle, article));
    }

    public boolean update(Handle handle, Article article) {
        String sql = "UPDATE Articles SET " +
                "voucher_id = :voucherId, " +
                "title = :title, " +
                "content = :content, " +
                "banner_image_url = :banner, " +
                "status_articles = :status, " +
                "start_date = :startDate, " +
                "end_date = :endDate " +
                "WHERE id = :id";

        int rows = handle.createUpdate(sql)
                .bind("id", article.getId())
                .bind("voucherId", article.getVoucherId())
                .bind("title", article.getTitle())
                .bind("content", article.getContent())
                .bind("banner", article.getBannerImageUrl())
                .bind("status", article.getStatusArticles())
                .bind("startDate", article.getStartDate())
                .bind("endDate", article.getEndDate())
                .execute();

        return rows > 0;
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM Articles WHERE id = :id";
        return jdbi.withHandle(handle ->
                handle.createUpdate(sql)
                        .bind("id", id)
                        .execute() > 0
        );
    }

    public boolean exists(int id) {
        String sql = "SELECT COUNT(*) FROM Articles WHERE id = :id";
        Integer count = jdbi.withHandle(handle ->
                handle.createQuery(sql)
                        .bind("id", id)
                        .mapTo(Integer.class)
                        .one()
        );
        return count > 0;
    }

    public int countByVoucherId(int voucherId) {
        String sql = "SELECT COUNT(*) FROM Articles WHERE voucher_id = :voucherId";
        return jdbi.withHandle(handle ->
                handle.createQuery(sql)
                        .bind("voucherId", voucherId)
                        .mapTo(Integer.class)
                        .one()
        );
    }

    public boolean updateStatus(int id, String status) {
        String sql = "UPDATE Articles SET status_articles = :status WHERE id = :id";
        return jdbi.withHandle(handle ->
                handle.createUpdate(sql)
                        .bind("id", id)
                        .bind("status", status)
                        .execute() > 0
        );
    }
}