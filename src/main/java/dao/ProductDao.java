package dao;

import model.product.Product;
import model.product.ProductImage;
import model.product.ProductListDTO;
import model.product.ProductVariant;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.statement.PreparedBatch;

import java.util.List;

public class ProductDao extends BaseDao {
    private final Jdbi jdbi;

    public ProductDao() {
        this.jdbi = get();
    }

    private String normalizeVietnamese(String text) {
        if (text == null || text.isEmpty())
            return "";

        String result = text.toLowerCase();

        result = result.replaceAll("[àáạảãâầấậẩẫăằắặẳẵ]", "a");
        result = result.replaceAll("[èéẹẻẽêềếệểễ]", "e");
        result = result.replaceAll("[ìíịỉĩ]", "i");
        result = result.replaceAll("[òóọỏõôồốộổỗơờớợởỡ]", "o");
        result = result.replaceAll("[ùúụủũưừứựửữ]", "u");
        result = result.replaceAll("[ỳýỵỷỹ]", "y");
        result = result.replaceAll("đ", "d");

        result = result.replaceAll("[^a-z0-9\\s]", " ");

        result = result.replaceAll("\\s+", " ");

        return result.trim();
    }

    public int countTotalProducts() {
        String sql = "SELECT COUNT(*) FROM Products";
        return get().withHandle(handle -> handle.createQuery(sql)
                .mapTo(Integer.class)
                .one());
    }

    public List<ProductListDTO> getListProductWithPagination(int limit, int offset) {
        return getListProductWithPaginationAndSort(limit, offset, "id-desc");
    }

    public List<ProductListDTO> getListProductWithPaginationAndSort(int limit, int offset, String sortBy) {
        String orderByClause;
        switch (sortBy) {
            case "name-asc":
                orderByClause = "ORDER BY p.name_product ASC";
                break;
            case "name-desc":
                orderByClause = "ORDER BY p.name_product DESC";
                break;
            case "price-asc":
                orderByClause = "ORDER BY price ASC";
                break;
            case "price-desc":
                orderByClause = "ORDER BY price DESC";
                break;
            case "id-asc":
                orderByClause = "ORDER BY p.id ASC";
                break;
            case "id-desc":
            default:
                orderByClause = "ORDER BY p.id DESC";
                break;
        }

        String sql = "SELECT " +
                "p.id, " +
                "p.name_product, " +
                "p.product_code, " +
                "p.status_product, " +
                "p.created_at, " +
                "p.category_id, " +
                "c.name_category AS categoryName, " +
                "(SELECT current_price FROM Product_variants WHERE product_id = p.id ORDER BY id LIMIT 1) AS price, " +
                "(SELECT image_url FROM Product_images WHERE product_id = p.id AND is_thumbnail = 1 LIMIT 1) AS thumbnail, "
                +
                "(SELECT sku FROM Product_variants WHERE product_id = p.id ORDER BY id LIMIT 1) AS sku, " +
                "COALESCE((SELECT COUNT(*) FROM Product_variants WHERE product_id = p.id), 0) AS variantCount, " +
                "COALESCE((SELECT SUM(stock_quantity) FROM Product_variants WHERE product_id = p.id), 0) AS totalStock "
                +
                "FROM Products p " +
                "LEFT JOIN Categories c ON p.category_id = c.id " +
                orderByClause + " " +
                "LIMIT :limit OFFSET :offset";

        return get().withHandle(handle -> handle.createQuery(sql)
                .bind("limit", limit)
                .bind("offset", offset)
                .mapToBean(ProductListDTO.class)
                .list());
    }

    public Product getBestSellingProduct() {
        String sql = "SELECT p.*, SUM(oi.quantity) as totalSold " +
                "FROM Order_items oi " +
                "JOIN Product_variants pv ON oi.variant_id = pv.id " +
                "JOIN Products p ON pv.product_id = p.id " +
                "GROUP BY p.id " +
                "ORDER BY totalSold DESC " +
                "LIMIT 1";

        return get().withHandle(handle -> handle.createQuery(sql)
                .mapToBean(Product.class)
                .findFirst()
                .orElse(null));
    }

    public List<ProductListDTO> getListProduct() {
        String sql = "SELECT " +
                "p.id, " +
                "p.name_product, " +
                "p.product_code, " +
                "p.status_product, " +
                "p.created_at, " +
                "p.category_id, " +
                "c.name_category AS categoryName, " +
                "(SELECT current_price FROM Product_variants WHERE product_id = p.id ORDER BY id LIMIT 1) AS price, " +
                "(SELECT discounted_price FROM Product_variants WHERE product_id = p.id ORDER BY id LIMIT 1) AS discountedPrice, "
                +
                "(SELECT image_url FROM Product_images WHERE product_id = p.id AND is_thumbnail = 1 LIMIT 1) AS thumbnail, "
                +
                "(SELECT sku FROM Product_variants WHERE product_id = p.id ORDER BY id LIMIT 1) AS sku, " +
                "COALESCE((SELECT COUNT(*) FROM Product_variants WHERE product_id = p.id), 0) AS variantCount, " +
                "COALESCE((SELECT SUM(stock_quantity) FROM Product_variants WHERE product_id = p.id), 0) AS totalStock "
                +
                "FROM Products p " +
                "LEFT JOIN Categories c ON p.category_id = c.id " +
                "ORDER BY p.id DESC";

        return get().withHandle(handle -> handle.createQuery(sql)
                .mapToBean(ProductListDTO.class)
                .list());
    }

    public List<ProductListDTO> getActiveListProduct() {
        String sql = "SELECT " +
                "p.id, " +
                "p.name_product, " +
                "p.product_code, " +
                "p.status_product, " +
                "p.created_at, " +
                "p.category_id, " +
                "c.name_category AS categoryName, " +
                "(SELECT current_price FROM Product_variants WHERE product_id = p.id ORDER BY id LIMIT 1) AS price, " +
                "(SELECT discounted_price FROM Product_variants WHERE product_id = p.id ORDER BY id LIMIT 1) AS discountedPrice, "
                +
                "(SELECT image_url FROM Product_images WHERE product_id = p.id AND is_thumbnail = 1 LIMIT 1) AS thumbnail, "
                +
                "(SELECT sku FROM Product_variants WHERE product_id = p.id ORDER BY id LIMIT 1) AS sku, " +
                "COALESCE((SELECT COUNT(*) FROM Product_variants WHERE product_id = p.id), 0) AS variantCount, " +
                "COALESCE((SELECT SUM(stock_quantity) FROM Product_variants WHERE product_id = p.id), 0) AS totalStock "
                +
                "FROM Products p " +
                "LEFT JOIN Categories c ON p.category_id = c.id " +
                "WHERE p.status_product = 'active' " +
                "ORDER BY p.id DESC";

        return get().withHandle(handle -> handle.createQuery(sql)
                .mapToBean(ProductListDTO.class)
                .list());
    }
    public int countActiveProducts() {
        String sql = "SELECT COUNT(*) FROM Products WHERE status_product = 'active'";
        return get().withHandle(handle -> handle.createQuery(sql).mapTo(Integer.class).one());
    }

    public List<ProductListDTO> getAllActiveProductsPayload(int page, int pageSize, String sortBy) {
        int offset = (page - 1) * pageSize;
        String orderBy = "p.id DESC";

        switch (sortBy) {
            case "alpha-asc":
                orderBy = "p.name_product ASC";
                break;
            case "alpha-desc":
                orderBy = "p.name_product DESC";
                break;
            case "price-asc":
                orderBy = "price ASC";
                break;
            case "price-desc":
                orderBy = "price DESC";
                break;
            case "created-desc":
                orderBy = "p.created_at DESC";
                break;
        }

        String sql = "SELECT " +
                "p.id, p.name_product, p.product_code, p.status_product, p.created_at, p.category_id, c.name_category AS categoryName, "
                +
                "(SELECT current_price FROM Product_variants WHERE product_id = p.id ORDER BY id LIMIT 1) AS price, " +
                "(SELECT discounted_price FROM Product_variants WHERE product_id = p.id ORDER BY id LIMIT 1) AS discountedPrice, "
                +
                "(SELECT image_url FROM Product_images WHERE product_id = p.id AND is_thumbnail = 1 LIMIT 1) AS thumbnail, "
                +
                "(SELECT sku FROM Product_variants WHERE product_id = p.id ORDER BY id LIMIT 1) AS sku, " +
                "COALESCE((SELECT COUNT(*) FROM Product_variants WHERE product_id = p.id), 0) AS variantCount, " +
                "COALESCE((SELECT SUM(stock_quantity) FROM Product_variants WHERE product_id = p.id), 0) AS totalStock "
                +
                "FROM Products p " +
                "LEFT JOIN Categories c ON p.category_id = c.id " +
                "WHERE p.status_product = 'active' " +
                "ORDER BY " + orderBy + " " +
                "LIMIT :limit OFFSET :offset";

        return get().withHandle(handle -> handle.createQuery(sql)
                .bind("limit", pageSize)
                .bind("offset", offset)
                .mapToBean(ProductListDTO.class)
                .list());
    }

    public Product getProduct(int id) {
        return get().withHandle(handle -> {
            Product product = handle.createQuery("SELECT * FROM Products WHERE id = :id")
                    .bind("id", id)
                    .mapToBean(Product.class)
                    .findFirst()
                    .orElse(null);

            if (product != null) {
                product.setVariants(handle.createQuery(
                        "SELECT * FROM Product_variants WHERE product_id = :id ORDER BY FIELD(size, 'S', 'M', 'L', 'XL', 'XXL');")
                        .bind("id", id)
                        .mapToBean(ProductVariant.class)
                        .list());

                product.setImages(handle.createQuery("SELECT * FROM Product_images WHERE product_id = :id")
                        .bind("id", id)
                        .mapToBean(ProductImage.class)
                        .list());
            }
            return product;
        });
    }

    public List<ProductListDTO> getProductsByCategory(int categoryId) {
        String sql = "SELECT p.id, p.name_product, " +
                "(SELECT current_price FROM Product_variants WHERE product_id = p.id LIMIT 1) AS price, " +
                "(SELECT discounted_price FROM Product_variants WHERE product_id = p.id LIMIT 1) AS discountedPrice, " +
                "(SELECT image_url FROM Product_images WHERE product_id = p.id AND is_thumbnail = 1 LIMIT 1) AS thumbnail, "
                +
                "(SELECT sku FROM Product_variants WHERE product_id = p.id LIMIT 1) AS sku " +
                "FROM Products p " +
                "WHERE p.category_id = :categoryId";

        return get().withHandle(handle -> handle.createQuery(sql)
                .bind("categoryId", categoryId)
                .mapToBean(ProductListDTO.class)
                .list());
    }

    public List<ProductListDTO> getViewedProducts(List<Integer> ids) {
        if (ids == null || ids.isEmpty())
            return new java.util.ArrayList<>();

        String sql = "SELECT p.id, p.name_product, " +
                "(SELECT current_price FROM Product_variants WHERE product_id = p.id LIMIT 1) AS price, " +
                "(SELECT discounted_price FROM Product_variants WHERE product_id = p.id LIMIT 1) AS discountedPrice, " +
                "(SELECT image_url FROM Product_images WHERE product_id = p.id AND is_thumbnail = 1 LIMIT 1) AS thumbnail, "
                +
                "(SELECT sku FROM Product_variants WHERE product_id = p.id LIMIT 1) AS sku " +
                "FROM Products p " +
                "WHERE p.id IN (<listId>)" +
                "AND p.status_product = 'active'";

        return get().withHandle(handle -> handle.createQuery(sql)
                .bindList("listId", ids)
                .mapToBean(ProductListDTO.class)
                .list());
    }

    public List<ProductListDTO> getRelatedProducts(int categoryId, int currentProductId, int limit) {
        String sql = "SELECT p.id, p.name_product, " +
                "(SELECT current_price FROM Product_variants WHERE product_id = p.id LIMIT 1) AS price, " +
                "(SELECT discounted_price FROM Product_variants WHERE product_id = p.id LIMIT 1) AS discountedPrice, " +
                "(SELECT image_url FROM Product_images WHERE product_id = p.id AND is_thumbnail = 1 LIMIT 1) AS thumbnail, "
                +
                "(SELECT sku FROM Product_variants WHERE product_id = p.id LIMIT 1) AS sku " +
                "FROM Products p " +
                "WHERE p.category_id = :categoryId " +
                "AND p.id != :currentProductId " +
                "AND p.status_product = 'active' " +
                "ORDER BY RAND() " +
                "LIMIT :limit";

        return get().withHandle(handle -> handle.createQuery(sql)
                .bind("categoryId", categoryId)
                .bind("currentProductId", currentProductId)
                .bind("limit", limit)
                .mapToBean(ProductListDTO.class)
                .list());
    }

    public int insert(Product product) {

        return get().withHandle(handle -> insert(handle, product));
    }

    public int insert(Handle handle, Product product) {
        String sql = "INSERT INTO Products (name_product, product_code, description, status_product, category_id, search_product) "
                +
                "VALUES (:nameProduct, :productCode, :description, :statusProduct, :categoryId, :searchProduct)";

        Integer catId = product.getCategoryId() == 0 ? null : product.getCategoryId();
        String searchProduct = normalizeVietnamese(product.getNameProduct());

        return handle.createUpdate(sql)
                .bind("nameProduct", product.getNameProduct())
                .bind("productCode", product.getProductCode())
                .bind("description", product.getDescription())
                .bind("statusProduct", product.getStatusProduct() == null ? "active" : product.getStatusProduct())
                .bind("categoryId", catId)
                .bind("searchProduct", searchProduct)
                .executeAndReturnGeneratedKeys("id")
                .mapTo(int.class)
                .one();
    }

    public boolean exists(int id) {
        String sql = "SELECT COUNT(*) FROM Products WHERE id = :id";
        return get().withHandle(handle -> {
            Integer count = handle.createQuery(sql).bind("id", id).mapTo(Integer.class).one();
            return count > 0;
        });
    }

    public boolean delete(int productId) {
        return get().withHandle(handle -> {
            return handle.inTransaction(h -> {
                h.createUpdate("DELETE FROM Product_images WHERE product_id = :productId")
                        .bind("productId", productId)
                        .execute();

                h.createUpdate("DELETE FROM Product_variants WHERE product_id = :productId")
                        .bind("productId", productId)
                        .execute();

                int affected = h.createUpdate("DELETE FROM Products WHERE id = :productId")
                        .bind("productId", productId)
                        .execute();

                return affected > 0;
            });
        });
    }

    public int countVariants(int productId) {
        String sql = "SELECT COUNT(*) FROM Product_variants WHERE product_id = :productId";
        return get()
                .withHandle(handle -> handle.createQuery(sql).bind("productId", productId).mapTo(Integer.class).one());
    }

    public int countImages(int productId) {
        String sql = "SELECT COUNT(*) FROM Product_images WHERE product_id = :productId";
        return get()
                .withHandle(handle -> handle.createQuery(sql).bind("productId", productId).mapTo(Integer.class).one());
    }

    public boolean update(Product product) {
        return get().withHandle(handle -> {
            return update(handle, product);
        });
    }

    public boolean update(Handle handle, Product product) {
        String sql = "UPDATE Products SET " +
                "name_product = ?, " +
                "product_code = ?, " +
                "description = ?, " +
                "status_product = ?, " +
                "category_id = ?, " +
                "search_product = ? " +
                "WHERE id = ?";

        String searchProduct = normalizeVietnamese(product.getNameProduct());

        int rows = handle.createUpdate(sql)
                .bind(0, product.getNameProduct())
                .bind(1, product.getProductCode())
                .bind(2, product.getDescription())
                .bind(3, product.getStatusProduct())
                .bind(4, product.getCategoryId() > 0 ? product.getCategoryId() : null)
                .bind(5, searchProduct)
                .bind(6, product.getId())
                .execute();

        return rows > 0;
    }

    public List<ProductListDTO> searchProducts(String keyword, int page, int pageSize, String sortBy) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllActiveProductsPayload(page, pageSize, sortBy);
        }

        int offset = (page - 1) * pageSize;
        String orderBy = "MATCH(p.search_product) AGAINST(:keyword IN NATURAL LANGUAGE MODE) DESC";

        if (sortBy != null && !sortBy.isEmpty() && !sortBy.equals("relevance")) {
            switch (sortBy) {
                case "alpha-asc": orderBy = "p.name_product ASC"; break;
                case "alpha-desc": orderBy = "p.name_product DESC"; break;
                case "price-asc": orderBy = "price ASC"; break;
                case "price-desc": orderBy = "price DESC"; break;
                case "created-desc": orderBy = "p.created_at DESC"; break;
            }
        }

        String sql = "SELECT " +
                "p.id, p.name_product, p.product_code, p.status_product, p.created_at, p.category_id, c.name_category AS categoryName, " +
                "(SELECT current_price FROM Product_variants WHERE product_id = p.id ORDER BY id LIMIT 1) AS price, " +
                "(SELECT discounted_price FROM Product_variants WHERE product_id = p.id ORDER BY id LIMIT 1) AS discountedPrice, " +
                "(SELECT image_url FROM Product_images WHERE product_id = p.id AND is_thumbnail = 1 LIMIT 1) AS thumbnail, " +
                "(SELECT sku FROM Product_variants WHERE product_id = p.id ORDER BY id LIMIT 1) AS sku, " +
                "COALESCE((SELECT COUNT(*) FROM Product_variants WHERE product_id = p.id), 0) AS variantCount, " +
                "COALESCE((SELECT SUM(stock_quantity) FROM Product_variants WHERE product_id = p.id), 0) AS totalStock " +
                "FROM Products p " +
                "LEFT JOIN Categories c ON p.category_id = c.id " +
                "WHERE p.status_product = 'active' " +
                "AND MATCH(p.search_product) AGAINST(:keyword IN NATURAL LANGUAGE MODE) " +
                "ORDER BY " + orderBy + " " +
                "LIMIT :limit OFFSET :offset";

        return get().withHandle(handle ->
                handle.createQuery(sql)
                        .bind("keyword", keyword.trim())
                        .bind("limit", pageSize)
                        .bind("offset", offset)
                        .mapToBean(ProductListDTO.class)
                        .list()
        );
    }

    public int countSearchResults(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return countActiveProducts();
        }

        String sql = "SELECT COUNT(*) FROM Products p " +
                "WHERE p.status_product = 'active' " +
                "AND MATCH(p.search_product) AGAINST(:keyword IN NATURAL LANGUAGE MODE)";

        return get().withHandle(handle ->
                handle.createQuery(sql)
                        .bind("keyword", keyword.trim())
                        .mapTo(Integer.class)
                        .one()
        );
    }


    public List<ProductListDTO> searchProductsAdmin(String keyword, int page, int pageSize, String sortBy) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getListProductWithPaginationAndSort(pageSize, (page - 1) * pageSize, sortBy);
        }

        int offset = (page - 1) * pageSize;
        String orderBy = "MATCH(p.search_product) AGAINST(:keyword IN NATURAL LANGUAGE MODE) DESC";

        if (sortBy != null && !sortBy.isEmpty() && !sortBy.equals("relevance") && !sortBy.equals("id-desc")) {
            switch (sortBy) {
                case "name-asc": orderBy = "p.name_product ASC"; break;
                case "name-desc": orderBy = "p.name_product DESC"; break;
                case "price-asc": orderBy = "price ASC"; break;
                case "price-desc": orderBy = "price DESC"; break;
                case "id-asc": orderBy = "p.id ASC"; break;
            }
        }

        String sql = "SELECT " +
                "p.id, p.name_product, p.product_code, p.status_product, p.created_at, p.category_id, c.name_category AS categoryName, " +
                "(SELECT current_price FROM Product_variants WHERE product_id = p.id ORDER BY id LIMIT 1) AS price, " +
                "(SELECT image_url FROM Product_images WHERE product_id = p.id AND is_thumbnail = 1 LIMIT 1) AS thumbnail, " +
                "(SELECT sku FROM Product_variants WHERE product_id = p.id ORDER BY id LIMIT 1) AS sku, " +
                "COALESCE((SELECT COUNT(*) FROM Product_variants WHERE product_id = p.id), 0) AS variantCount, " +
                "COALESCE((SELECT SUM(stock_quantity) FROM Product_variants WHERE product_id = p.id), 0) AS totalStock " +
                "FROM Products p " +
                "LEFT JOIN Categories c ON p.category_id = c.id " +
                "WHERE MATCH(p.search_product) AGAINST(:keyword IN NATURAL LANGUAGE MODE) " +
                "ORDER BY " + orderBy + " " +
                "LIMIT :limit OFFSET :offset";

        return get().withHandle(handle ->
                handle.createQuery(sql)
                        .bind("keyword", keyword.trim())
                        .bind("limit", pageSize)
                        .bind("offset", offset)
                        .mapToBean(ProductListDTO.class)
                        .list()
        );
    }

    public int countSearchResultsAdmin(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return countTotalProducts();
        }

        String sql = "SELECT COUNT(*) FROM Products p " +
                "WHERE MATCH(p.search_product) AGAINST(:keyword IN NATURAL LANGUAGE MODE)";

        return get().withHandle(handle ->
                handle.createQuery(sql)
                        .bind("keyword", keyword.trim())
                        .mapTo(Integer.class)
                        .one()
        );
    }
}
