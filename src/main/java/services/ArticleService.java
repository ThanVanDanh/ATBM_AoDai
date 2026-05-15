package services;

import dao.ArticleDao;
import dao.VoucherDao;
import model.article.Article;
import model.article.ArticleListDTO;
import org.jdbi.v3.core.Jdbi;
import services.CloudinaryService.UploadedImage;

import java.io.InputStream;
import java.util.List;

public class ArticleService {
    private final Jdbi jdbi;
    private final CloudinaryService cloudinaryService;
    private final ArticleDao articleDao;
    private final VoucherDao voucherDao;

    public ArticleService(Jdbi jdbi, CloudinaryService cloudinaryService) {
        this.jdbi = jdbi;
        this.cloudinaryService = cloudinaryService;
        this.articleDao = new ArticleDao();
        this.voucherDao = new VoucherDao();
    }

    public List<ArticleListDTO> getListArticles() {
        return articleDao.getListArticles();
    }

    public List<ArticleListDTO> getPublishedArticles() {
        return articleDao.getPublishedArticles();
    }

    public Article getArticle(int id) {
        return articleDao.getByIdWithVoucher(id);
    }

    public int createArticle(Article article, InputStream bannerImage, String filename) throws Exception {
        String uploadedPublicId = null;

        try {
            if (bannerImage != null && filename != null) {
                System.out.println("Uploading banner image: " + filename);
                UploadedImage uploaded = cloudinaryService.upload(bannerImage, filename);

                if (uploaded != null) {
                    article.setBannerImageUrl(uploaded.getSecureUrl());
                    uploadedPublicId = uploaded.getPublicId();
                    System.out.println("Uploaded banner: " + uploaded.getSecureUrl());
                    System.out.println("   Public ID: " + uploadedPublicId);
                }
            }

            int articleId = jdbi.inTransaction(handle -> {
                return articleDao.insert(handle, article);
            });

            System.out.println("Created article ID: " + articleId);
            return articleId;

        } catch (Exception ex) {
            System.err.println("Error creating article: " + ex.getMessage());
            ex.printStackTrace();

            if (uploadedPublicId != null) {
                try {
                    cloudinaryService.deleteByPublicId(uploadedPublicId);
                    System.out.println("Cleaned up uploaded banner: " + uploadedPublicId);
                } catch (Exception e) {
                    System.err.println("Failed to cleanup banner: " + e.getMessage());
                }
            }

            throw new Exception("Failed to create article: " + ex.getMessage());
        }
    }

    public boolean updateArticle(Article article, InputStream newBannerImage, String filename) throws Exception {
        String oldPublicId = null;
        String newPublicId = null;

        try {
            Article existingArticle = articleDao.getById(article.getId());
            if (existingArticle == null) {
                throw new Exception("Article not found");
            }

            String oldBannerUrl = existingArticle.getBannerImageUrl();
            oldPublicId = extractPublicId(oldBannerUrl);

            if (newBannerImage != null && filename != null) {
                System.out.println("Uploading new banner: " + filename);
                UploadedImage uploaded = cloudinaryService.upload(newBannerImage, filename);

                if (uploaded != null) {
                    article.setBannerImageUrl(uploaded.getSecureUrl());
                    newPublicId = uploaded.getPublicId();
                    System.out.println("Uploaded new banner: " + uploaded.getSecureUrl());
                    System.out.println("   Public ID: " + newPublicId);
                }
            } else {
                article.setBannerImageUrl(oldBannerUrl);
            }

            boolean success = jdbi.inTransaction(handle -> {
                return articleDao.update(handle, article);
            });

            if (success) {
                if (newPublicId != null && oldPublicId != null && !oldPublicId.equals(newPublicId)) {
                    try {
                        cloudinaryService.deleteByPublicId(oldPublicId);
                        System.out.println("Deleted old banner: " + oldPublicId);
                    } catch (Exception e) {
                        System.err.println("Failed to delete old banner: " + e.getMessage());
                    }
                }

                System.out.println("Updated article ID: " + article.getId());
                return true;
            } else {
                if (newPublicId != null) {
                    try {
                        cloudinaryService.deleteByPublicId(newPublicId);
                        System.out.println("Rolled back new banner: " + newPublicId);
                    } catch (Exception e) {
                        System.err.println("Failed to rollback banner: " + e.getMessage());
                    }
                }
                throw new Exception("Failed to update article in database");
            }

        } catch (Exception ex) {
            System.err.println("Error updating article: " + ex.getMessage());
            ex.printStackTrace();
            throw new Exception("Failed to update article: " + ex.getMessage());
        }
    }

    public boolean deleteArticle(int articleId) throws Exception {
        try {
            Article article = articleDao.getById(articleId);
            if (article == null) {
                throw new Exception("Article not found");
            }

            String publicId = extractPublicId(article.getBannerImageUrl());

            boolean deleted = articleDao.delete(articleId);

            if (deleted) {
                if (publicId != null) {
                    try {
                        cloudinaryService.deleteByPublicId(publicId);
                        System.out.println("Deleted banner from Cloudinary: " + publicId);
                    } catch (Exception e) {
                        System.err.println("Failed to delete banner: " + e.getMessage());
                    }
                }

                System.out.println("Deleted article ID: " + articleId);
                return true;
            }

            return false;

        } catch (Exception ex) {
            System.err.println("Error deleting article: " + ex.getMessage());
            ex.printStackTrace();
            throw new Exception("Failed to delete article: " + ex.getMessage());
        }
    }

    public boolean publishArticle(int articleId) {
        return articleDao.updateStatus(articleId, "published");
    }

    public boolean unpublishArticle(int articleId) {
        return articleDao.updateStatus(articleId, "draft");
    }

    private String extractPublicId(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return null;
        }

        int lastSlash = imageUrl.lastIndexOf('/');
        int lastDot = imageUrl.lastIndexOf('.');

        if (lastSlash != -1 && lastDot != -1 && lastDot > lastSlash) {
            return imageUrl.substring(lastSlash + 1, lastDot);
        }

        return null;
    }
}