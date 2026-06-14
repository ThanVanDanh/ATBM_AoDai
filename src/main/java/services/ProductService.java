package services;

import dao.ProductDao;
import dao.ProductImageDao;
import dao.ProductVariantDao;
import model.product.Product;
import model.product.ProductImage;
import model.product.ProductListDTO;
import model.product.ProductVariant;
import org.jdbi.v3.core.Jdbi;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ProductService {

    private final Jdbi jdbi;
    private final CloudinaryService cloudinary;
    private final ProductDao productDao;
    private final ProductVariantDao variantDao;
    private final ProductImageDao imageDao;

    public ProductService(Jdbi jdbi, CloudinaryService cloudinary) {
        this.jdbi = Objects.requireNonNull(jdbi);
        this.cloudinary = Objects.requireNonNull(cloudinary);
        this.productDao = new ProductDao();
        this.variantDao = new ProductVariantDao();
        this.imageDao = new ProductImageDao();
    }

    public static class ImageUpload {
        private final InputStream inputStream;
        private final File file;
        private final String filename;
        private final String altText;
        private final boolean isThumbnail;

        public ImageUpload(InputStream inputStream, String filename, String altText, boolean isThumbnail) {
            this.inputStream = inputStream;
            this.file = null;
            this.filename = filename;
            this.altText = altText;
            this.isThumbnail = isThumbnail;
        }

        public ImageUpload(File file, String altText, boolean isThumbnail) {
            this.inputStream = null;
            this.file = file;
            this.filename = file != null ? file.getName() : null;
            this.altText = altText;
            this.isThumbnail = isThumbnail;
        }

        public InputStream getInputStream() {
            return inputStream;
        }

        public File getFile() {
            return file;
        }

        public String getFilename() {
            return filename;
        }

        public String getAltText() {
            return altText;
        }

        public boolean isThumbnail() {
            return isThumbnail;
        }
    }

    private static class UploadedImage {
        final String secureUrl;
        final String publicId;
        final String altText;
        final boolean isThumbnail;

        UploadedImage(String secureUrl, String publicId, String altText, boolean isThumbnail) {
            this.secureUrl = secureUrl;
            this.publicId = publicId;
            this.altText = altText;
            this.isThumbnail = isThumbnail;
        }
    }

    public int countTotalProducts() {
        return productDao.countTotalProducts();
    }

    public List<ProductListDTO> getListProductWithPagination(int limit, int offset) {
        return productDao.getListProductWithPagination(limit, offset);
    }

    public List<ProductListDTO> getListProductWithPaginationAndSort(int limit, int offset, String sortBy) {
        return productDao.getListProductWithPaginationAndSort(limit, offset, sortBy);
    }

    public List<ProductListDTO> getListProduct() {
        return productDao.getListProduct();
    }

    public List<ProductListDTO> getProductsByCategory(int categoryId) {
        return productDao.getProductsByCategory(categoryId);
    }
    public List<ProductListDTO> getActiveListProduct() {
        return productDao.getActiveListProduct();
    }

    public Product getProduct(int id) {
        return productDao.getProduct(id);
    }

    public double getPriceById(int productId) {
        List<ProductVariant> variants = variantDao.getByProductId(productId);

        if (variants != null && !variants.isEmpty()) {
            return variants.get(0).getCurrentPrice();
        }
        return 0.0;
    }


    public int createProduct(Product product, List<ProductVariant> variants, List<ImageUpload> uploads) {
        List<UploadedImage> uploaded = new ArrayList<>();

        try {
            if (uploads != null) {
                for (ImageUpload iu : uploads) {
                    CloudinaryService.UploadedImage u;
                    if (iu.getInputStream() != null) {
                        u = cloudinary.upload(iu.getInputStream(), iu.getFilename());
                    } else {
                        u = cloudinary.upload(iu.getFile());
                    }
                    if (u == null || u.getSecureUrl() == null) {
                        cleanupUploaded(uploaded);
                        throw new RuntimeException("Upload ảnh thất bại: " + iu.getFilename());
                    }
                    uploaded.add(
                            new UploadedImage(u.getSecureUrl(), u.getPublicId(), iu.getAltText(), iu.isThumbnail()));
                }
            }

            int newProductId = jdbi.inTransaction(handle -> {
                int pid = productDao.insert(handle, product);

                if (variants != null) {
                    for (ProductVariant v : variants) {
                        v.setProductId(pid);
                        variantDao.insert(handle, v);
                    }
                }

                if (!uploaded.isEmpty()) {
                    for (UploadedImage r : uploaded) {
                        ProductImage pi = new ProductImage(0);
                        pi.setProductId(pid);
                        pi.setImageUrl(r.secureUrl);
                        pi.setAltText(r.altText);
                        pi.setThumbnail(r.isThumbnail);
                        imageDao.insert(handle, pi);
                    }
                }

                ensureOnlyOneThumbnail(handle, pid);

                return pid;
            });

            return newProductId;

        } catch (RuntimeException ex) {
            cleanupUploaded(uploaded);
            throw ex;
        }
    }

    private void cleanupUploaded(List<UploadedImage> uploaded) {
        if (uploaded == null)
            return;
        for (UploadedImage u : uploaded) {
            if (u.publicId != null) {
                try {
                    cloudinary.deleteByPublicId(u.publicId);
                } catch (Exception ignore) {
                }
            }
        }
    }

    public boolean updateProduct(Product product,
                                 List<ProductVariant> variants,
                                 List<ImageUpload> newImageUploads,
                                 List<Integer> keepImageIds,
                                 List<Boolean> keepImageThumbs) throws Exception {

        System.out.println("=== ProductService.updateProduct ===");
        System.out.println("Product ID: " + product.getId());
        System.out.println("New Variants: " + (variants != null ? variants.size() : 0));
        System.out.println("Keep Images: " + (keepImageIds != null ? keepImageIds.size() : 0));
        System.out.println("New Images: " + (newImageUploads != null ? newImageUploads.size() : 0));

        List<UploadedImage> uploaded = new ArrayList<>();

        if (newImageUploads != null && !newImageUploads.isEmpty()) {
            for (ImageUpload iu : newImageUploads) {
                try {
                    CloudinaryService.UploadedImage u;
                    if (iu.getInputStream() != null) {
                        u = cloudinary.upload(iu.getInputStream(), iu.getFilename());
                    } else {
                        u = cloudinary.upload(iu.getFile());
                    }

                    if (u == null || u.getSecureUrl() == null) {
                        throw new RuntimeException("Upload failed: " + iu.getFilename());
                    }

                    uploaded.add(new UploadedImage(
                            u.getSecureUrl(),
                            u.getPublicId(),
                            iu.getAltText(),
                            iu.isThumbnail()));

                    System.out.println("Uploaded new image: " + u.getSecureUrl());

                } catch (Exception e) {
                    System.err.println("Upload failed: " + iu.getFilename() + " - " + e.getMessage());
                    cleanupUploaded(uploaded);
                    throw new Exception("Failed to upload image: " + iu.getFilename(), e);
                }
            }
        }

        List<String> imagesToDelete = new ArrayList<>();

        try {
            boolean success = jdbi.inTransaction(handle -> {
                boolean updated = productDao.update(handle, product);
                if (!updated) {
                    throw new RuntimeException("Failed to update product");
                }
                System.out.println("Product updated");

                List<ProductVariant> currentVariants = variantDao.getByProductId(handle, product.getId());
                List<String> incomingSkus = new ArrayList<>();

                if (variants != null && !variants.isEmpty()) {
                    for (ProductVariant v : variants) {
                        incomingSkus.add(v.getSku());
                        boolean exists = false;

                        for (ProductVariant existing : currentVariants) {
                            if (existing.getSku().equalsIgnoreCase(v.getSku())) {
                                existing.setSize(v.getSize());
                                existing.setColor(v.getColor());
                                existing.setCurrentPrice(v.getCurrentPrice());
                                existing.setStockQuantity(v.getStockQuantity());
                                variantDao.update(handle, existing);
                                exists = true;
                                break;
                            }
                        }

                        if (!exists) {
                            v.setProductId(product.getId());
                            variantDao.insert(handle, v);
                        }
                    }
                }

                for (ProductVariant existing : currentVariants) {
                    if (!incomingSkus.contains(existing.getSku())) {
                        variantDao.delete(handle, existing.getId());
                    }
                }

                List<ProductImage> existingImages = imageDao.getByProductId(handle, product.getId());

                for (ProductImage img : existingImages) {
                    boolean shouldKeep = keepImageIds != null && keepImageIds.contains(img.getId());
                    if (!shouldKeep) {
                        imagesToDelete.add(img.getImageUrl());
                    }
                }


                if (keepImageIds == null || keepImageIds.isEmpty()) {
                    imageDao.deleteByProductId(handle, product.getId());
                } else {
                    imageDao.deleteExcept(handle, product.getId(), keepImageIds);
                }

                if (keepImageIds != null && !keepImageIds.isEmpty()) {
                    for (int i = 0; i < keepImageIds.size(); i++) {
                        int imageId = keepImageIds.get(i);
                        boolean isThumb = keepImageThumbs != null && i < keepImageThumbs.size() && keepImageThumbs.get(i);
                        imageDao.updateThumbnail(handle, imageId, isThumb);
                    }
                }

                if (!uploaded.isEmpty()) {
                    for (UploadedImage u : uploaded) {
                        ProductImage img = new ProductImage(0);
                        img.setProductId(product.getId());
                        img.setImageUrl(u.secureUrl);
                        img.setAltText(u.altText);
                        img.setThumbnail(u.isThumbnail);

                        imageDao.insert(handle, img);
                    }
                }

                Integer selectedThumbnailId = null;

                if (keepImageThumbs != null) {
                    for (int i = 0; i < keepImageThumbs.size(); i++) {
                        if (keepImageThumbs.get(i)) {
                            selectedThumbnailId = keepImageIds.get(i);
                            break;
                        }
                    }
                }

                if (selectedThumbnailId == null && !uploaded.isEmpty()) {
                    for (UploadedImage u : uploaded) {
                        if (u.isThumbnail) {
                            break;
                        }
                    }
                }

                ensureOnlyOneThumbnail(handle, product.getId());

                return true;
            });

            if (!success) {
                throw new Exception("Transaction failed");
            }

        } catch (Exception e) {
            e.printStackTrace();
            cleanupUploaded(uploaded);

            throw new Exception("Failed to update product", e);
        }

        if (!imagesToDelete.isEmpty()) {
            for (String url : imagesToDelete) {
                try {
                    String publicId = extractPublicId(url);
                    if (publicId != null) {
                        cloudinary.deleteByPublicId(publicId);
                    }
                } catch (Exception e) {
                    System.err.println("Lỗi xóa ảnh Cloudinary: " + url + " - " + e.getMessage());
                }
            }
        }

        return true;
    }

    private String extractPublicId(String url) {
        if (url == null || url.isEmpty())
            return null;

        try {
            int uploadIndex = url.indexOf("/upload/");
            if (uploadIndex == -1)
                return null;

            String afterUpload = url.substring(uploadIndex + 8);

            int slashIndex = afterUpload.indexOf('/');
            if (slashIndex == -1)
                return null;

            String withExtension = afterUpload.substring(slashIndex + 1);

            int dotIndex = withExtension.lastIndexOf('.');
            if (dotIndex == -1)
                return withExtension;

            return withExtension.substring(0, dotIndex);

        } catch (Exception e) {
            System.err.println("Failed to extract public_id from: " + url);
            return null;
        }
    }

    public boolean applyDiscountBySku(String sku, String discountType, double discountValue) {
        try {
            ProductVariant variant = variantDao.getVariantBySku(sku);
            if (variant == null) {
                throw new RuntimeException("Không tìm thấy sản phẩm với SKU: " + sku);
            }

            double discountedPrice;
            if ("percentage".equals(discountType)) {
                discountedPrice = variant.getCurrentPrice() * (1 - discountValue / 100);
            } else {
                discountedPrice = variant.getCurrentPrice() - discountValue;
            }

            if (discountedPrice < 0)
                discountedPrice = 0;

            return variantDao.updateDiscountedPrice(variant.getId(), discountedPrice);

        } catch (Exception e) {
            System.err.println("Error applying discount: " + e.getMessage());
            return false;
        }
    }

    public int applyDiscountByCategories(List<Integer> categoryIds, String discountType, double discountValue) {
        try {
            List<ProductVariant> variants = jdbi.withHandle(handle -> {
                String selectSql = "SELECT pv.* FROM Product_variants pv " +
                        "INNER JOIN Products p ON pv.product_id = p.id " +
                        "WHERE p.category_id IN (<categoryIds>)";

                return handle.createQuery(selectSql)
                        .bindList("categoryIds", categoryIds)
                        .mapToBean(ProductVariant.class)
                        .list();
            });

            if (variants.isEmpty())
                return 0;

            int count = 0;
            for (ProductVariant v : variants) {
                double discountedPrice;
                if ("percentage".equals(discountType)) {
                    discountedPrice = v.getCurrentPrice() * (1 - discountValue / 100);
                } else {
                    discountedPrice = v.getCurrentPrice() - discountValue;
                }

                if (discountedPrice < 0)
                    discountedPrice = 0;

                boolean updated = variantDao.updateDiscountedPrice(v.getId(), discountedPrice);
                if (updated)
                    count++;
            }

            return count;

        } catch (Exception e) {
            System.err.println("Error applying batch discount: " + e.getMessage());
            return 0;
        }
    }

    private void ensureOnlyOneThumbnail(org.jdbi.v3.core.Handle handle, int productId) {
        String selectSql = "SELECT * FROM Product_images WHERE product_id = :productId ORDER BY id";
        List<ProductImage> images = handle.createQuery(selectSql)
                .bind("productId", productId)
                .mapToBean(ProductImage.class)
                .list();

        ProductImage thumbnailImage = null;
        for (ProductImage img : images) {
            if (img.isThumbnail()) {
                thumbnailImage = img;
                break;
            }
        }

        String resetSql = "UPDATE Product_images SET is_thumbnail = 0 WHERE product_id = :productId";
        handle.createUpdate(resetSql)
                .bind("productId", productId)
                .execute();

        if (thumbnailImage != null) {
            String updateSql = "UPDATE Product_images SET is_thumbnail = 1 WHERE id = :id";
            handle.createUpdate(updateSql)
                    .bind("id", thumbnailImage.getId())
                    .execute();
        } else if (!images.isEmpty()) {
            String updateSql = "UPDATE Product_images SET is_thumbnail = 1 WHERE id = :id";
            handle.createUpdate(updateSql)
                    .bind("id", images.get(0).getId())
                    .execute();
        }
    }
}
