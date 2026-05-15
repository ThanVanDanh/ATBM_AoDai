package dao;

import model.product.Category;
import model.product.ProductListDTO;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.List;


public class CategoryDao extends BaseDao {

    private final Jdbi jdbi;

    public CategoryDao() {
        this.jdbi = get();
    }

    public int insert(Category category) {

        return jdbi.withHandle(handle -> insert(handle, category));
    }
    public int insert(Handle handle, Category category) {
        String sql = "INSERT INTO Categories (name_category, slug, description, parent_category_id) " +
                "VALUES (:nameCategory, :slug, :description, :parentId)";

        Integer parent = (category.getParentId() != null && category.getParentId() == 0) ? null : category.getParentId();

        return handle.createUpdate(sql)
                .bind("nameCategory", category.getNameCategory())
                .bind("slug", category.getSlug())
                .bind("description", category.getDescription())
                .bind("parentId", parent)
                .executeAndReturnGeneratedKeys("id")
                .mapTo(int.class)
                .one();
    }

    public List<Category> getAll() {
        String sql = "SELECT id, name_category, slug, description, parent_category_id FROM Categories ORDER BY name_category";

        try {
            List<Category> result = jdbi.withHandle(handle -> {
                return handle.createQuery(sql)
                        .map((rs, ctx) -> {
                            Category c = new Category();
                            c.setId(rs.getInt("id"));
                            c.setNameCategory(rs.getString("name_category"));
                            c.setSlug(rs.getString("slug"));
                            c.setDescription(rs.getString("description"));
                            c.setParentId(rs.getObject("parent_category_id", Integer.class));
                            return c;
                        })
                        .list();
            });

            return result;

        } catch (Exception e) {
            System.err.println(" Error in getAll(): " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    public Category getById(int id) {
        String sql = "SELECT id, name_category, slug, description, parent_category_id FROM Categories WHERE id = :id";
        try {
            Category result = jdbi.withHandle(handle ->
                    handle.createQuery(sql)
                            .bind("id", id)
                            .map((rs, ctx) -> {
                                Category c = new Category();
                                c.setId(rs.getInt("id"));
                                c.setNameCategory(rs.getString("name_category"));
                                c.setSlug(rs.getString("slug"));
                                c.setDescription(rs.getString("description"));
                                c.setParentId(rs.getObject("parent_category_id", Integer.class));
                                return c;
                            })
                            .findOne()
                            .orElse(null)
            );
            return result;
        } catch (Exception e) {
            System.err.println(" Error in CategoryDao.getById: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public boolean existsBySlug(String slug) {
        if (slug == null || slug.isEmpty()) return false;

        Long count = jdbi.withHandle(handle ->
                handle.createQuery("SELECT COUNT(*) FROM Categories WHERE slug = :slug")
                        .bind("slug", slug)
                        .mapTo(Long.class)
                        .one()
        );
        return count != null && count > 0;
    }

    public Category getCategoryBySlug(String slug) {
        return get().withHandle(handle ->
                handle.createQuery("SELECT * FROM Categories WHERE slug = :slug")
                        .bind("slug", slug)
                        .mapToBean(Category.class)
                        .findFirst()
                        .orElse(null)
        );
    }
    public int countProductsByCategory(int categoryId) {
        String sql = "SELECT COUNT(*) FROM Products WHERE category_id = :categoryId and status_product = 'active'";

        return get().withHandle(handle ->
                handle.createQuery(sql)
                        .bind("categoryId", categoryId)
                        .mapTo(Integer.class)
                        .one()
        );
    }

    public List<ProductListDTO> getProductsByCategoryPayload(int categoryId, int page, int pageSize, String sortBy) {
        String orderByClause = "ORDER BY p.name_product ASC";

        if (sortBy != null) {
            switch (sortBy) {
                case "alpha-desc": orderByClause = "ORDER BY p.name_product DESC"; break;
                case "price-asc": orderByClause = "ORDER BY price ASC"; break;
                case "price-desc": orderByClause = "ORDER BY price DESC"; break;
                case "created-desc": orderByClause = "ORDER BY p.id DESC"; break;
                default: orderByClause = "ORDER BY p.name_product ASC";
            }
        }

        String sql = "SELECT p.id, p.name_product, " +
                "(SELECT current_price FROM Product_variants WHERE product_id = p.id LIMIT 1) AS price, " +
                "(SELECT discounted_price FROM Product_variants WHERE product_id = p.id LIMIT 1) AS discountedPrice, " + // THÊM DÒNG NÀY
                "(SELECT image_url FROM Product_images WHERE product_id = p.id AND is_thumbnail = 1 LIMIT 1) AS thumbnail, " +
                "(SELECT sku FROM Product_variants WHERE product_id = p.id LIMIT 1) AS sku " +
                "FROM Products p " +
                "WHERE p.category_id = :categoryId AND p.status_product = 'active'" +
                orderByClause + " " +
                "LIMIT :limit OFFSET :offset";

        int offset = (page - 1) * pageSize;

        return get().withHandle(handle ->
                handle.createQuery(sql)
                        .bind("categoryId", categoryId)
                        .bind("limit", pageSize)
                        .bind("offset", offset)
                        .mapToBean(ProductListDTO.class)
                        .list()
        );
    }
    public boolean update(Category category) {
        String sql = "UPDATE Categories SET " +
                "name_category = :nameCategory, " +
                "slug = :slug, " +
                "description = :description, " +
                "parent_category_id = :parentId " +
                "WHERE id = :id";

        return jdbi.withHandle(handle -> {
            Integer parentId = category.getParentId();
            if (parentId != null && parentId == 0) {
                parentId = null;
            }
            int affected = handle.createUpdate(sql)
                    .bind("id", category.getId())
                    .bind("nameCategory", category.getNameCategory())
                    .bind("slug", category.getSlug())
                    .bind("description", category.getDescription())
                    .bind("parentId", parentId)
                    .execute();
            return affected > 0;
        });
    }

    public int countChildCategories(int categoryId) {
        String sql = "SELECT COUNT(*) FROM Categories WHERE parent_category_id = :categoryId";
        return jdbi.withHandle(handle ->
                handle.createQuery(sql).bind("categoryId", categoryId).mapTo(Integer.class).one()
        );
    }

    public int countProducts(int categoryId) {
        String sql = "SELECT COUNT(*) FROM Products WHERE category_id = :categoryId";
        return jdbi.withHandle(handle ->
                handle.createQuery(sql).bind("categoryId", categoryId).mapTo(Integer.class).one()
        );
    }

    public Map<Integer, Integer> getProductCountsForAllCategories() {
        String sql = "SELECT category_id, COUNT(*) as product_count " +
                "FROM Products " +
                "GROUP BY category_id";

        return jdbi.withHandle(handle -> {
            Map<Integer, Integer> counts = new HashMap<>();

            handle.createQuery(sql)
                    .map((rs, ctx) -> {
                        int categoryId = rs.getInt("category_id");
                        int count = rs.getInt("product_count");
                        counts.put(categoryId, count);
                        return null;
                    })
                    .list();

            return counts;
        });
    }

    public boolean delete(int id) {
        int childCount = countChildCategories(id);
        int productCount = countProducts(id);

        if (childCount > 0 || productCount > 0) {
            StringBuilder msg = new StringBuilder("Không thể xóa danh mục này vì:\n");
            if (childCount > 0) {
                msg.append("- Còn ").append(childCount).append(" danh mục con\n");
            }
            if (productCount > 0) {
                msg.append("- Còn ").append(productCount).append(" sản phẩm\n");
            }
            msg.append("\nVui lòng xóa ");
            if (childCount > 0 && productCount > 0) {
                msg.append("các danh mục con và sản phẩm");
            } else if (childCount > 0) {
                msg.append("các danh mục con");
            } else {
                msg.append("các sản phẩm");
            }
            msg.append(" trước.");

            throw new IllegalStateException(msg.toString());
        }

        String sql = "DELETE FROM Categories WHERE id = :id";
        return jdbi.withHandle(handle -> {
            int affected = handle.createUpdate(sql).bind("id", id).execute();
            return affected > 0;
        });
    }

    public boolean exists(int id) {
        String sql = "SELECT COUNT(*) FROM Categories WHERE id = :id";
        return jdbi.withHandle(handle -> {
            Integer count = handle.createQuery(sql).bind("id", id).mapTo(Integer.class).one();
            return count > 0;
        });
    }

}
