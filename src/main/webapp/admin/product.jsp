<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Admin - Quản lý Sản phẩm</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/7.0.1/css/all.min.css"
          integrity="sha512-2SwdPD6INVrV/lHTZbO2nodKhrnDdJK9/kg2XD1r9uGqPo1cUbujc+IYdlYdEErWNu69gVcYgdxlmVmzTWnetw=="
          crossorigin="anonymous" referrerpolicy="no-referrer"/>
    <link rel="stylesheet" href="../style/admin.css">
    <link rel="stylesheet" href="../style/productStyle.css">

</head>
<body>
<div class="admin-container">
    <jsp:include page="sidebar.jsp" />
    <main class="main-content">
        <header class="admin-header">
            <div class="header-actions">
                <a href="${pageContext.request.contextPath}/login.jsp" class="btn-logout">
                    <i class="fas fa-user-circle"></i> Đăng xuất
                </a>
            </div>
        </header>

        <div class="tab-navigation">
            <button class="tab-button active" data-tab="products-tab">
                <i class="fas fa-box-open"></i> Quản lý Sản phẩm
            </button>
            <button class="tab-button" data-tab="categories-tab">
                <i class="fas fa-folder"></i> Quản lý Danh mục
            </button>
            <button class="tab-button" data-tab="policies-tab">
                <i class="fas fa-file-contract"></i> Quản lý Chính sách
            </button>
        </div>

        <div id="products-tab" class="tab-content active">
            <section class="product-section">
                <div class="product-list-header">
                    <h2>Danh sách Sản phẩm</h2>

                    <div class="search-wrapper" style="margin-left:auto; margin-right:12px;">
                        <input type="search" id="globalSearchInput" class="search-input"
                               placeholder="Tìm theo tên, mã, danh mục..." aria-label="Tìm sản phẩm">
                    </div>

                    <div class="sort-wrapper" style="margin-right:12px;">
                        <select id="sort-select" class="search-input" style="width: 150px; padding: 6px 12px;">
                            <option value="id-desc">Mới nhất</option>
                            <option value="id-asc">Cũ nhất</option>
                            <option value="name-asc">Tên A → Z</option>
                            <option value="name-desc">Tên Z → A</option>
                            <option value="price-asc">Giá tăng dần</option>
                            <option value="price-desc">Giá giảm dần</option>
                        </select>
                    </div>

                    <div class="actions-row">
                        <a href="#" class="btn btn-primary" id="addProductBtn">
                            <i class="fas fa-plus"></i> Thêm Sản phẩm
                        </a>
                        <a href="#" class="btn btn-primary" id="discountMarketing">
                            <i class="fa-solid fa-tags"></i> Giảm giá Marketing
                        </a>
                    </div>
                </div>

                <table class="product-table">
                    <thead>
                    <tr>
                        <th>Ảnh</th>
                        <th>Tên / Mã</th>
                        <th>Danh mục</th>
                        <th>Trạng thái</th>
                        <th>Tồn kho</th>
                        <th>Giá</th>
                        <th>Ngày tạo</th>
                        <th>Cài đặt</th>
                    </tr>
                    </thead>
                    <tbody id="productTableBody">
                    </tbody>
                </table>

                <div class="pagination">
                    <a href="#">Trước</a>
                    <a href="#" class="active">1</a>
                    <a href="#">2</a>
                    <a href="#">3</a>
                    <a href="#">Sau</a>
                </div>
            </section>
        </div>

        <div id="categories-tab" class="tab-content">
            <section class="category-section">
                <div class="category-list-header"
                     style="display: flex; align-items: center; justify-content: space-between; margin-bottom: 20px;">
                    <h2>Danh sách Danh mục</h2>
                    <div class="actions-row">
                        <a href="#" class="btn btn-primary" id="addCategoryBtnTop">
                            <i class="fas fa-folder-plus"></i> Thêm Danh mục
                        </a>
                    </div>
                </div>

                <table class="product-table">
                    <thead>
                    <tr>
                        <th>Tên Danh mục</th>
                        <th style="width: 200px;">Slug</th>
                        <th>Mô tả</th>
                        <th style="width: 100px; text-align: center">Số SP</th>
                        <th style="width: 120px; text-align: center">Cài đặt</th>
                    </tr>
                    </thead>
                    <tbody id="categoryTableBody">
                    </tbody>
                </table>
            </section>
        </div>

        <div id="policies-tab" class="tab-content">
            <section class="policy-section">
                <div class="policy-list-header"
                     style="display: flex; align-items: center; justify-content: space-between; margin-bottom: 20px;">
                    <h2>Danh sách Chính sách</h2>
                    <div class="actions-row">
                        <a href="#" class="btn btn-primary" id="addPolicyBtnTop">
                            <i class="fas fa-plus"></i> Thêm Chính sách
                        </a>
                    </div>
                </div>

                <table class="product-table">
                    <thead>
                    <tr>
                        <th>Danh mục</th>
                        <th>Nội dung chính sách</th>
                        <th style="width: 150px;">Ngày tạo</th>
                        <th style="width: 120px; text-align: center">Cài đặt</th>
                    </tr>
                    </thead>
                    <tbody id="policyTableBody">
                    </tbody>
                </table>
            </section>
        </div>

        <div id="addProductModal" class="modal-overlay" aria-hidden="true">
            <div class="modal-content" role="dialog" aria-modal="true" aria-labelledby="modalTitle">
                <div class="modal-header">
                    <h2 id="modalTitle">Thêm Sản phẩm mới</h2>
                    <span class="close-button" id="closeModalBtn">&times;</span>
                </div>

                <form id="addProductForm" action="${pageContext.request.contextPath}/admin/product/add"
                      method="post" enctype="multipart/form-data" novalidate>
                    <div class="modal-body">
                        <div class="modal-form-grid">
                            <div class="form-group-modal full-width">
                                <label for="product-name">Tên Sản phẩm <span style="color:red">*</span></label>
                                <input name="product-name" type="text" id="product-name" required
                                       placeholder="Tên sản phẩm">
                            </div>

                            <div class="form-group-modal">
                                <label for="product-code">Mã sản phẩm</label>
                                <input name="product-code" type="text" id="product-code"
                                       placeholder="VD: ADTT01">
                            </div>

                            <div class="form-group-modal">
                                <label for="product-category">Danh mục <span style="color:red">*</span></label>
                                <select name="product-category" id="product-category" required>
                                    <option value="">-- Đang tải danh mục --</option>
                                </select>
                            </div>

                            <div class="form-group-modal">
                                <label for="product-status">Trạng thái</label>
                                <select name="product-status" id="product-status">
                                    <option value="active">active</option>
                                    <option value="inactive">inactive</option>
                                </select>
                            </div>

                            <div class="form-group-modal full-width">
                                <label for="product-description">Mô tả</label>
                                <textarea name="product-description" id="product-description"
                                          placeholder="Mô tả chi tiết"></textarea>
                            </div>

                            <div class="form-group-modal full-width">
                                <h3>Biến thể (Product_variants)</h3>
                                <div id="variantsContainer" class="variants-list"></div>
                                <a href="#" id="addVariantBtn" class="btn btn-secondary" style="margin-top:8px;">
                                    + Thêm Biến thể
                                </a>
                            </div>

                            <div class="form-group-modal full-width">
                                <h3>Hình ảnh (Product_images)</h3>
                                <label class="product-image-upload" for="product-image-input">
                                    <i class="fas fa-cloud-upload-alt"></i>
                                    <p>Nhấn để tải ảnh lên (Hỗ trợ nhiều ảnh)</p>
                                </label>
                                <input type="file" id="product-image-input" name="productImages"
                                       accept="image/*" multiple style="display:none;">
                                <div class="image-preview-grid" id="imagePreviewGrid"></div>
                                <p class="meta">Bạn có thể đánh dấu 1 ảnh làm thumbnail.</p>
                            </div>
                        </div>
                    </div>

                    <div class="modal-footer">
                        <button type="button" class="btn-modal btn-modal-secondary" id="cancelModalBtn">
                            Hủy
                        </button>
                        <button type="submit" class="btn-modal btn-modal-primary" id="modalSubmitBtn">
                            Lưu Sản phẩm
                        </button>
                    </div>
                </form>
            </div>
        </div>

        <div id="addCategoryModal" class="modal-overlay" aria-hidden="true">
            <div class="modal-content" role="dialog" aria-modal="true" aria-labelledby="categoryModalTitle">
                <div class="modal-header">
                    <h3 id="categoryModalTitle">Thêm Danh mục</h3>
                    <span class="close-button" id="closeCategoryModalBtn">&times;</span>
                </div>

                <form id="addCategoryForm" action="${pageContext.request.contextPath}/admin/category/add"
                      method="post" novalidate>
                    <div class="modal-body">
                        <div class="modal-form-grid">
                            <div class="form-group-modal full-width">
                                <label for="category-name">Tên danh mục <span style="color:red">*</span></label>
                                <input type="text" id="category-name" name="category-name"
                                       placeholder="VD: Áo dài" required>
                            </div>

                            <div class="form-group-modal full-width">
                                <label for="category-slug">Slug (tự động nếu để trống)</label>
                                <input type="text" id="category-slug" name="category-slug"
                                       placeholder="ao-dai">
                            </div>

                            <div class="form-group-modal full-width">
                                <label for="category-description">Mô tả</label>
                                <textarea id="category-description" name="category-description"></textarea>
                            </div>

                            <div class="form-group-modal full-width">
                                <label for="category-parent">Danh mục cha (nếu có)</label>
                                <select id="category-parent" name="category-parent">
                                    <option value="">-- Không --</option>
                                </select>
                            </div>
                        </div>
                    </div>

                    <div class="modal-footer">
                        <button type="button" class="btn-modal btn-modal-secondary" id="cancelCategoryModalBtn">
                            Hủy
                        </button>
                        <button type="submit" class="btn-modal btn-modal-primary" id="categorySubmitBtn">
                            Lưu Danh mục
                        </button>
                    </div>
                </form>
            </div>
        </div>

        <!-- Discount Marketing Modal -->
        <div id="discountMarketingModal" class="modal-overlay" aria-hidden="true">
            <div class="modal-content" role="dialog" aria-modal="true" aria-labelledby="discountModalTitle">
                <div class="modal-header">
                    <h3 id="discountModalTitle">Giảm giá Marketing</h3>
                    <span class="close-button" id="closeDiscountModalBtn">&times;</span>
                </div>

                <div class="modal-body">
                    <div class="modal-form-grid">
                        <!-- Chọn phương thức giảm giá -->
                        <div class="form-group-modal full-width">
                            <label for="discount-method">Phương thức giảm giá <span style="color:red">*</span></label>
                            <select id="discount-method" name="discount-method" required>
                                <option value="">-- Chọn phương thức --</option>
                                <option value="single">Giảm giá theo mã sản phẩm</option>
                                <option value="batch">Giảm giá hàng loạt theo danh mục</option>
                            </select>
                        </div>

                        <!-- Option 1: Giảm giá theo mã sản phẩm -->
                        <div id="single-discount-section" class="discount-section full-width" style="display:none;">
                            <div class="form-group-modal">
                                <label for="product-code-discount">Mã sản phẩm <span style="color:red">*</span></label>
                                <input type="text" id="product-code-discount" placeholder="VD: ADTT01">
                                <button type="button" id="check-product-btn" class="btn btn-secondary" style="margin-top:8px;">
                                    Kiểm tra giá
                                </button>
                            </div>

                            <div class="form-group-modal">
                                <label for="current-price-display">Giá hiện tại</label>
                                <input type="text" id="current-price-display" readonly
                                       placeholder="Nhập mã sản phẩm và bấm 'Kiểm tra giá'"
                                       style="background-color: #f0f0f0; cursor: not-allowed;">
                            </div>

                            <div class="form-group-modal">
                                <label for="single-discount-type">Loại giảm giá</label>
                                <select id="single-discount-type">
                                    <option value="percentage">Giảm theo %</option>
                                    <option value="fixed">Giảm số tiền cố định</option>
                                </select>
                            </div>

                            <div class="form-group-modal">
                                <label for="single-discount-value">
                                    <span id="single-discount-label">Phần trăm giảm (%)</span>
                                </label>
                                <input type="number" id="single-discount-value"
                                       placeholder="VD: 10" min="0" step="0.01">
                            </div>
                        </div>

                        <!-- Option 2: Giảm giá hàng loạt theo danh mục -->
                        <div id="batch-discount-section" class="discount-section full-width" style="display:none;">
                            <div class="form-group-modal full-width">
                                <label>Chọn danh mục <span style="color:red">*</span></label>
                                <div id="category-checkbox-list" class="checkbox-list"
                                     style="max-height: 200px; overflow-y: auto; border: 1px solid #ddd; padding: 10px; border-radius: 4px;">
                                    <!-- Categories will be loaded here via JavaScript -->
                                </div>
                            </div>

                            <div class="form-group-modal">
                                <label for="batch-discount-type">Loại giảm giá</label>
                            <div class="form-group-modal">
                                <label for="batch-discount-type">Loại giảm giá</label>
                                <select id="batch-discount-type">
                                    <option value="percentage">Giảm theo %</option>
                                    <option value="fixed">Giảm số tiền cố định</option>
                                </select>
                            </div>

                            <div class="form-group-modal">
                                <label for="batch-discount-value">
                                    <span id="batch-discount-label">Phần trăm giảm (%)</span>
                                </label>
                                <input type="number" id="batch-discount-value"
                                       placeholder="VD: 15" min="0" step="0.01">
                            </div>
                        </div>
                    </div>
                </div>

                <div class="modal-footer">
                    <button type="button" class="btn-modal btn-modal-secondary" id="cancelDiscountModalBtn">
                        Hủy
                    </button>
                    <button type="button" class="btn-modal btn-modal-primary" id="applyDiscountBtn">
                        Lưu giảm giá
                    </button>
                </div>
            </div>
        </div>

    </main>

    <!-- Policy Modal -->

    <!-- Policy Modal -->
    <div id="addPolicyModal" class="modal-overlay" aria-hidden="true">
        <div class="modal-content" role="dialog" aria-modal="true" aria-labelledby="policyModalTitle">
            <div class="modal-header">
                <h3 id="policyModalTitle">Thêm Chính sách</h3>
                <span class="close-button" id="closePolicyModalBtn">&times;</span>
            </div>

            <form id="addPolicyForm" action="${pageContext.request.contextPath}/admin/policy/add"
                  method="post" novalidate>
                <div class="modal-body">
                    <div class="modal-form-grid">
                        <div class="form-group-modal full-width">
                            <label for="policy-category">Danh mục <span style="color:red">*</span></label>
                            <select id="policy-category" name="policy-category" required>
                                <option value="">-- Chọn danh mục --</option>
                            </select>
                        </div>

                        <div class="form-group-modal full-width">
                            <label for="policy-text">Nội dung chính sách <span style="color:red">*</span></label>
                            <textarea id="policy-text" name="policy-text" rows="10" 
                                      placeholder="Nhập nội dung chính sách cho danh mục này..." required></textarea>
                        </div>
                    </div>
                </div>

                <div class="modal-footer">
                    <button type="button" class="btn-modal btn-modal-secondary" id="cancelPolicyModalBtn">
                        Hủy
                    </button>
                    <button type="submit" class="btn-modal btn-modal-primary" id="policySubmitBtn">
                        Lưu Chính sách
                    </button>
                </div>
            </form>
        </div>
    </div>
</div>

<script>
    const CTX = '${pageContext.request.contextPath}';
</script>

<script src="../scripts/admin/product.js"></script>
<script src="../scripts/admin/product-discount.js"></script>
<script src="${pageContext.request.contextPath}/scripts/admin/admin.js"></script>
</body>
</html>
