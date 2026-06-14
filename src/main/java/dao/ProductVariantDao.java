package dao;

import model.product.ProductVariant;
import org.jdbi.v3.core.Handle;

import java.util.List;

public class ProductVariantDao extends BaseDao {

    public int insert(ProductVariant variant) {
        return get().withHandle(handle -> insert(handle, variant));
    }

    public int insert(Handle handle, ProductVariant variant) {
        String sql = "INSERT INTO Product_variants (product_id, sku, size, color, current_price, stock_quantity, discounted_price) " +
                "VALUES (:productId, :sku, :size, :color, :currentPrice, :stockQuantity, :discountedPrice)";

        return handle.createUpdate(sql)
                .bind("productId", variant.getProductId())
                .bind("sku", variant.getSku())
                .bind("size", variant.getSize())
                .bind("color", variant.getColor())
                .bind("currentPrice", variant.getCurrentPrice())
                .bind("stockQuantity", variant.getStockQuantity())
                .bind("discountedPrice", variant.getDiscountedPrice())
                .executeAndReturnGeneratedKeys("id")
                .mapTo(int.class)
                .one();
    }

    public void deleteByProductId(int productId) {
        get().withHandle(handle -> {
            deleteByProductId(handle, productId);
            return null;
        });
    }


    public void deleteByProductId(Handle handle, int productId) {
        String sql = "DELETE FROM Product_variants WHERE product_id = :productId";
        handle.createUpdate(sql)
                .bind("productId", productId)
                .execute();
    }

    public List<ProductVariant> getByProductId(int productId) {
        return get().withHandle(handle -> getByProductId(handle, productId));
    }


    public List<ProductVariant> getByProductId(Handle handle, int productId) {
        String sql = "SELECT * FROM Product_variants WHERE product_id = :productId ORDER BY id";
        return handle.createQuery(sql)
                .bind("productId", productId)
                .mapToBean(ProductVariant.class)
                .list();
    }


    public boolean update(ProductVariant variant) {
        return get().withHandle(handle -> update(handle, variant));
    }

    public boolean update(Handle handle, ProductVariant variant) {
        String sql = "UPDATE Product_variants SET " +
                "sku = :sku, " +
                "size = :size, " +
                "color = :color, " +
                "current_price = :currentPrice, " +
                "discounted_price = :discountedPrice, " +
                "stock_quantity = :stockQuantity " +
                "WHERE id = :id";

        int affected = handle.createUpdate(sql)
                .bind("id", variant.getId())
                .bind("sku", variant.getSku())
                .bind("size", variant.getSize())
                .bind("color", variant.getColor())
                .bind("currentPrice", variant.getCurrentPrice())
                .bind("discountedPrice", variant.getDiscountedPrice())
                .bind("stockQuantity", variant.getStockQuantity())
                .execute();

        return affected > 0;
    }

    public boolean delete(int variantId) {
        return get().withHandle(handle -> delete(handle, variantId));
    }

    public boolean delete(Handle handle, int variantId) {
        String sql = "DELETE FROM Product_variants WHERE id = :id";
        int affected = handle.createUpdate(sql)
                .bind("id", variantId)
                .execute();
        return affected > 0;
    }

    public boolean updateDiscountedPriceByProductId(int productId, Double discountedPrice) {
        String sql = "UPDATE Product_variants SET discounted_price = :discountedPrice WHERE product_id = :productId";
        return get().withHandle(handle ->
                handle.createUpdate(sql)
                        .bind("discountedPrice", discountedPrice)
                        .bind("productId", productId)
                        .execute() > 0
        );
    }

    public int updateDiscountedPriceByCategoryIds(List<Integer> categoryIds, Double discountedPrice) {
        if (categoryIds == null || categoryIds.isEmpty()) return 0;

        String sql = "UPDATE Product_variants pv " +
                "INNER JOIN Products p ON pv.product_id = p.id " +
                "SET pv.discounted_price = :discountedPrice " +
                "WHERE p.category_id IN (<categoryIds>)";

        return get().withHandle(handle ->
                handle.createUpdate(sql)
                        .bind("discountedPrice", discountedPrice)
                        .bindList("categoryIds", categoryIds)
                        .execute()
        );
    }

    public boolean updateDiscountedPrice(int variantId, double discountedPrice) {
        String sql = "UPDATE Product_variants SET discounted_price = :discountedPrice WHERE id = :id";
        return get().withHandle(handle ->
                handle.createUpdate(sql)
                        .bind("discountedPrice", discountedPrice)
                        .bind("id", variantId)
                        .execute() > 0
        );
    }

    public ProductVariant getVariantBySku(String sku) {
        String sql = "SELECT * FROM Product_variants WHERE sku = :sku LIMIT 1";
        
        return get().withHandle(handle ->
                handle.createQuery(sql)
                        .bind("sku", sku)
                        .mapToBean(ProductVariant.class)
                        .findFirst()
                        .orElse(null)
        );
    }

    public ProductVariant getFirstVariantByProductCode(String code) {
        String sqlBySku = "SELECT * FROM Product_variants WHERE sku = :code ORDER BY id LIMIT 1";
        
        ProductVariant variant = get().withHandle(handle ->
                handle.createQuery(sqlBySku)
                        .bind("code", code)
                        .mapToBean(ProductVariant.class)
                        .findFirst()
                        .orElse(null)
        );
        
        if (variant != null) {
            return variant;
        }

        String sqlByProductCode = "SELECT pv.* FROM Product_variants pv " +
                "INNER JOIN Products p ON pv.product_id = p.id " +
                "WHERE p.product_code = :code " +
                "ORDER BY pv.id LIMIT 1";

        return get().withHandle(handle ->
                handle.createQuery(sqlByProductCode)
                        .bind("code", code)
                        .mapToBean(ProductVariant.class)
                        .findFirst()
                        .orElse(null)
        );
    }
}
