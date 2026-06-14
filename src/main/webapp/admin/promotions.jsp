<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Admin - Quản lý Khuyến mãi</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/7.0.1/css/all.min.css"
          integrity="sha512-2SwdPD6INVrV/lHTZbO2nodKhrnDdJK9/kg2XD1r9uGqPo1cUbujc+IYdlYdEErWNu69gVcYgdxlmVmzTWnetw=="
          crossorigin="anonymous" referrerpolicy="no-referrer"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/style/admin.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/style/promitionsStyle.css">
    <script src="https://cdn.ckeditor.com/4.22.1/full-all/ckeditor.js"></script>
    <style>
        .cke_notifications_area {
            display: none !important;
        }

        .cke_notification {
            display: none !important;
        }

        .cke_notification_warning {
            display: none !important;
        }
    </style>
</head>
<body>
<div class="admin-container">
    <!-- Sidebar -->
    <jsp:include page="sidebar.jsp" />


    <!-- Main Content -->
    <main class="main-content">
        <header class="admin-header">
            <div class="header-actions">
                <a href="${pageContext.request.contextPath}/login.jsp" class="btn-logout">
                    <i class="fas fa-user-circle"></i> Đăng xuất
                </a>
            </div>
        </header>

        <!-- Tab Navigation -->
        <div class="tab-navigation" style="margin-bottom: 20px; border-bottom: 2px solid #e0e0e0;">
            <button class="tab-button active" data-tab="articles-tab">
                <i class="fas fa-newspaper"></i> Quản lý Bài viết
            </button>
            <button class="tab-button" data-tab="vouchers-tab">
                <i class="fas fa-ticket-alt"></i> Quản lý Voucher
            </button>
        </div>



        <!-- Articles Tab Content -->
        <div id="articles-tab" class="tab-content active">
            <div class="promotion-editor-header">
                <h2 id="editor-title">Tạo bài viết khuyến mãi mới</h2>
            </div>

            <form id="articleForm" enctype="multipart/form-data">
                <div class="promotion-editor-container">
                    <div class="editor-main">
                        <div class="form-group">
                            <label for="article-title">Tiêu đề bài viết <span style="color:red">*</span></label>
                            <input type="text" id="article-title" name="article-title"
                                   placeholder="Ví dụ: Giảm giá sốc cuối năm 50%..." required>
                        </div>

                        <div class="form-group">
                            <label for="article-content">Nội dung bài viết</label>
                            <textarea id="article-content" name="article-content"></textarea>
                        </div>

                        <div class="form-group">
                            <label for="voucher-select">Mã voucher (tùy chọn)</label>
                            <select id="voucher-select" name="voucher-id">
                                <option value="">-- Không có voucher --</option>
                            </select>
                        </div>
                    </div>

                    <div class="editor-sidebar">
                        <div class="publish-card">
                            <h3><i class="fas fa-play-circle"></i> Hành động</h3>
                            <div class="publish-actions">
                                <button type="button" class="btn btn-draft" id="btn-save-draft">Lưu nháp</button>
                                <button type="button" class="btn btn-publish" id="btn-publish">Đăng bài</button>
                                <button type="button" class="btn btn-secondary" id="btn-cancel" style="display:none;">Hủy</button>
                            </div>
                        </div>

                        <div class="publish-card">
                            <h3><i class="fas fa-calendar-alt"></i> Lịch hoạt động</h3>
                            <div class="form-group-sidebar">
                                <label for="start-date">Ngày bắt đầu</label>
                                <input type="datetime-local" id="start-date" name="start-date">
                            </div>
                            <div class="form-group-sidebar">
                                <label for="end-date">Ngày kết thúc</label>
                                <input type="datetime-local" id="end-date" name="end-date">
                            </div>
                        </div>

                        <div class="publish-card">
                            <h3><i class="fas fa-image"></i> Ảnh đại diện (Banner)</h3>

                            <!-- Existing Image Display -->
                            <div id="existing-image-container" style="display:none; margin-bottom:10px;">
                                <p style="font-size:12px; color:#666; margin-bottom:8px;">Ảnh hiện tại:</p>
                                <img src="" alt="Banner hiện tại" id="existing-image"
                                     style="max-width:100%; border-radius:8px; border:2px solid #28a745;">
                                <button type="button" class="btn btn-secondary" id="btn-change-image"
                                        style="margin-top:8px; width:100%;">
                                    <i class="fas fa-upload"></i> Thay ảnh mới
                                </button>
                            </div>

                            <!-- Upload New Image -->
                            <div id="upload-image-container">
                                <input type="file" id="banner-image" name="banner-image" accept="image/*" style="display:none;">
                                <label for="banner-image" class="image-upload-box" id="upload-label">
                                    <i class="fas fa-cloud-upload-alt"></i>
                                    <p>Nhấn để tải ảnh lên</p>
                                </label>
                            </div>

                            <!-- New Image Preview -->
                            <div id="new-image-preview-container" style="display:none; margin-top:10px;">
                                <p style="font-size:12px; color:#007bff; margin-bottom:8px;">Ảnh mới:</p>
                                <img src="" alt="Xem trước ảnh mới" id="new-image-preview"
                                     style="max-width:100%; border-radius:8px; border:2px solid #007bff;">
                                <button type="button" class="btn btn-secondary" id="btn-remove-new-image"
                                        style="margin-top:8px; width:100%;">
                                    Hủy ảnh mới
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            </form>

            <!-- Articles List -->
            <div class="promotion-list-section">
                <h2>Danh sách Khuyến mãi</h2>

                <table class="promo-table">
                    <thead>
                    <tr>
                        <th>Tiêu đề bài viết</th>
                        <th>Voucher</th>
                        <th>Trạng thái</th>
                        <th>Thời gian hoạt động</th>
                        <th>Hành động</th>
                    </tr>
                    </thead>
                    <tbody id="articles-table-body">
                    <!-- Articles will be loaded here -->
                    </tbody>
                </table>
            </div>
        </div>
        <!-- End Articles Tab -->

        <!-- Vouchers Tab Content -->
        <div id="vouchers-tab" class="tab-content">
            <div class="promotion-editor-header">
                <h2 id="voucher-editor-title">Tạo Voucher mới</h2>
            </div>

            <form id="voucherForm">
                <div class="promotion-editor-container">
                    <div class="editor-main">
                        <div class="form-group">
                            <label for="voucher-code">Mã Voucher <span style="color:red">*</span></label>
                            <input type="text" id="voucher-code" name="voucher-code"
                                   placeholder="VD: SALE50, FREESHIP" required
                                   style="font-family: monospace; text-transform: uppercase;">
                        </div>

                        <div class="form-group">
                            <label for="discount-type">Loại giảm giá <span style="color:red">*</span></label>
                            <select id="discount-type" name="discount-type" required>
                                <option value="percentage">Phần trăm (%)</option>
                                <option value="fixed">Số tiền cố định (đ)</option>
                            </select>
                        </div>

                        <div class="form-group">
                            <label for="discount-value">Giá trị giảm <span style="color:red">*</span></label>
                            <input type="number" id="discount-value" name="discount-value"
                                   placeholder="VD: 50 (nếu %), 100000 (nếu số tiền)" required step="0.01" min="0">
                        </div>

                        <div class="form-group">
                            <label for="min-order-amount">Giá trị đơn hàng tối thiểu</label>
                            <input type="number" id="min-order-amount" name="min-order-amount"
                                   placeholder="VD: 200000" step="1000" min="0" value="0">
                        </div>

                        <div class="form-group">
                            <label for="max-usage">Số lượt sử dụng tối đa</label>
                            <input type="number" id="max-usage" name="max-usage"
                                   placeholder="VD: 100" min="1" value="100">
                        </div>
                    </div>

                    <div class="editor-sidebar">
                        <div class="publish-card">
                            <h3><i class="fas fa-play-circle"></i> Hành động</h3>
                            <div class="publish-actions">
                                <button type="button" class="btn btn-publish" id="btn-save-voucher">
                                    Lưu Voucher
                                </button>
                                <button type="button" class="btn btn-secondary" id="btn-cancel-voucher" style="display:none;">
                                    Hủy
                                </button>
                            </div>
                        </div>

                        <div class="publish-card">
                            <h3><i class="fas fa-calendar-alt"></i> Thời gian hiệu lực</h3>
                            <div class="form-group-sidebar">
                                <label for="voucher-valid-from">Bắt đầu</label>
                                <input type="datetime-local" id="voucher-valid-from" name="voucher-valid-from">
                            </div>
                            <div class="form-group-sidebar">
                                <label for="voucher-valid-to">Kết thúc</label>
                                <input type="datetime-local" id="voucher-valid-to" name="voucher-valid-to">
                            </div>
                        </div>

                        <div class="publish-card">
                            <h3><i class="fas fa-toggle-on"></i> Trạng thái</h3>
                            <div class="form-group-sidebar">
                                <label style="display: flex; align-items: center; gap: 8px; cursor: pointer;">
                                    <input type="checkbox" id="voucher-is-active" name="voucher-is-active" checked
                                           style="width: 20px; height: 20px; cursor: pointer;">
                                    <span>Kích hoạt ngay</span>
                                </label>
                            </div>
                        </div>
                    </div>
                </div>
            </form>

            <!-- Vouchers List -->
            <div class="promotion-list-section">
                <h2>Danh sách Voucher</h2>

                <table class="promo-table">
                    <thead>
                    <tr>
                        <th>Mã Voucher</th>
                        <th>Loại</th>
                        <th>Giá trị</th>
                        <th>Đơn tối thiểu</th>
                        <th>Số lượt</th>
                        <th>Thời gian</th>
                        <th>Trạng thái</th>
                        <th>Hành động</th>
                    </tr>
                    </thead>
                    <tbody id="vouchers-table-body">
                    <!-- Vouchers will be loaded here -->
                    </tbody>
                </table>
            </div>
        </div>
        <!-- End Vouchers Tab -->

    </main>
</div>

<script>
    const CTX = '${pageContext.request.contextPath}';
    let ckeditorInstance = null;
    let currentEditId = null;
    let currentBannerUrl = null;


    document.addEventListener('DOMContentLoaded', function() {
        ckeditorInstance = CKEDITOR.replace('article-content', {
            height: 500,
            language: 'vi',
            toolbar: [
                { name: 'document', items: [ 'Source', '-', 'Preview' ] },
                { name: 'clipboard', items: [ 'Cut', 'Copy', 'Paste', 'PasteText', 'PasteFromWord', '-', 'Undo', 'Redo' ] },
                '/',
                { name: 'basicstyles', items: [ 'Bold', 'Italic', 'Underline', 'Strike', '-', 'RemoveFormat' ] },
                { name: 'paragraph', items: [ 'NumberedList', 'BulletedList', '-', 'Outdent', 'Indent', '-', 'Blockquote' ] },
                { name: 'links', items: [ 'Link', 'Unlink' ] },
                { name: 'insert', items: [ 'Image', 'Table', 'HorizontalRule' ] },
                '/',
                { name: 'styles', items: [ 'Format', 'Font', 'FontSize' ] },
                { name: 'colors', items: [ 'TextColor', 'BGColor' ] }
            ]
        });

        loadVouchers();
        loadArticles();

        setupEventListeners();
    });

    function setupEventListeners() {
        document.querySelectorAll('.tab-button').forEach(button => {
            button.addEventListener('click', function() {
                const tabId = this.getAttribute('data-tab');
                switchTab(tabId);
            });
        });

        document.getElementById('btn-save-draft').addEventListener('click', function() {
            saveArticle('draft');
        });

        document.getElementById('btn-publish').addEventListener('click', function() {
            saveArticle('published');
        });

        document.getElementById('btn-cancel').addEventListener('click', function() {
            resetForm();
        });

        document.getElementById('btn-save-voucher').addEventListener('click', function() {
            saveVoucher();
        });

        document.getElementById('btn-cancel-voucher').addEventListener('click', function() {
            resetVoucherForm();
        });

        document.getElementById('banner-image').addEventListener('change', function(e) {
            const file = e.target.files[0];
            if (file) {
                const reader = new FileReader();
                reader.onload = function(ev) {
                    document.getElementById('new-image-preview').src = ev.target.result;
                    document.getElementById('new-image-preview-container').style.display = 'block';
                    document.getElementById('upload-image-container').style.display = 'none';
                    document.getElementById('existing-image-container').style.display = 'none';
                };
                reader.readAsDataURL(file);
            }
        });

        document.getElementById('btn-change-image').addEventListener('click', function() {
            document.getElementById('existing-image-container').style.display = 'none';
            document.getElementById('upload-image-container').style.display = 'block';
            currentBannerUrl = null;
        });


        document.getElementById('btn-remove-new-image').addEventListener('click', function() {
            document.getElementById('banner-image').value = '';
            document.getElementById('new-image-preview').src = '';
            document.getElementById('new-image-preview-container').style.display = 'none';

            if (currentBannerUrl) {
                document.getElementById('existing-image-container').style.display = 'block';
            } else {
                document.getElementById('upload-image-container').style.display = 'block';
            }
        });
    }

    function loadVouchers() {
        fetch(CTX + '/admin/voucher/list')
            .then(response => response.json())
            .then(vouchers => {
                const select = document.getElementById('voucher-select');
                select.innerHTML = '<option value="">-- Không có voucher --</option>';

                vouchers.forEach(voucher => {
                    const option = document.createElement('option');
                    option.value = voucher.id;
                    option.textContent = voucher.voucherCode + ' - ' +
                        (voucher.discountType === 'percentage' ? voucher.discountValue + '%' : voucher.discountValue + 'đ');
                    select.appendChild(option);
                });
            })
            .catch(error => {
                console.error('Error loading vouchers:', error);
            });
    }


    function loadArticles() {
        fetch(CTX + '/admin/article/list')
            .then(response => response.json())
            .then(articles => {
                displayArticles(articles);
            })
            .catch(error => {
                console.error('Error loading articles:', error);
                alert('Không thể tải danh sách bài viết');
            });
    }

    function displayArticles(articles) {
        const tbody = document.getElementById('articles-table-body');
        tbody.innerHTML = '';

        if (!articles || articles.length === 0) {
            tbody.innerHTML = '<tr><td colspan="5" style="text-align:center;padding:40px;">Chưa có bài viết nào</td></tr>';
            return;
        }

        articles.forEach(article => {
            const row = document.createElement('tr');

            const titleCell = document.createElement('td');
            titleCell.innerHTML = '<strong>' + escapeHtml(article.title) + '</strong>';
            row.appendChild(titleCell);

            const voucherCell = document.createElement('td');
            if (article.voucherCode) {
                voucherCell.innerHTML = '<span style="font-family:monospace; background:#f0f0f0; padding:4px 8px; border-radius:4px;">' +
                    escapeHtml(article.voucherCode) + '</span>';
            } else {
                voucherCell.textContent = '-';
                voucherCell.style.color = '#999';
            }
            row.appendChild(voucherCell);

            const statusCell = document.createElement('td');
            const statusSpan = document.createElement('span');
            statusSpan.className = 'status';

            if (article.statusArticles === 'published') {
                statusSpan.classList.add('active');
                statusSpan.textContent = 'Đang chạy';
            } else {
                statusSpan.classList.add('draft');
                statusSpan.textContent = 'Nháp';
            }
            statusCell.appendChild(statusSpan);
            row.appendChild(statusCell);

            const periodCell = document.createElement('td');
            periodCell.className = 'period';
            if (article.startDate && article.endDate) {
                periodCell.innerHTML =
                    'Bắt đầu: ' + formatDateTime(article.startDate) + '<br>' +
                    'Kết thúc: ' + formatDateTime(article.endDate);
            } else {
                periodCell.textContent = 'Chưa lên lịch';
                periodCell.style.color = '#999';
            }
            row.appendChild(periodCell);

            const actionCell = document.createElement('td');
            actionCell.className = 'action-icons';

            const editBtn = document.createElement('a');
            editBtn.href = '#';
            editBtn.title = 'Sửa';
            editBtn.innerHTML = '<i class="fas fa-edit"></i>';
            editBtn.onclick = function(e) {
                e.preventDefault();
                editArticle(article.id);
            };

            const deleteBtn = document.createElement('a');
            deleteBtn.href = '#';
            deleteBtn.className = 'delete';
            deleteBtn.title = 'Xóa';
            deleteBtn.innerHTML = '<i class="fas fa-trash-alt"></i>';
            deleteBtn.onclick = function(e) {
                e.preventDefault();
                deleteArticle(article.id, article.title);
            };

            actionCell.appendChild(editBtn);
            actionCell.appendChild(document.createTextNode(' '));
            actionCell.appendChild(deleteBtn);
            row.appendChild(actionCell);

            tbody.appendChild(row);
        });
    }

    function saveArticle(status) {
        const title = document.getElementById('article-title').value.trim();

        if (!title) {
            alert('Vui lòng nhập tiêu đề bài viết');
            return;
        }

        const content = ckeditorInstance.getData();

        const formData = new FormData();

        if (currentEditId) {
            formData.append('article-id', currentEditId);
        }

        formData.append('article-title', title);
        formData.append('article-content', content);
        formData.append('article-status', status);

        const voucherId = document.getElementById('voucher-select').value;
        if (voucherId) {
            formData.append('voucher-id', voucherId);
        }

        const startDate = document.getElementById('start-date').value;
        if (startDate) {
            formData.append('start-date', startDate);
        }

        const endDate = document.getElementById('end-date').value;
        if (endDate) {
            formData.append('end-date', endDate);
        }

        const bannerInput = document.getElementById('banner-image');
        if (bannerInput.files.length > 0) {
            formData.append('banner-image', bannerInput.files[0]);
        }

        const url = currentEditId
            ? CTX + '/admin/article/update'
            : CTX + '/admin/article/add';

        const btnDraft = document.getElementById('btn-save-draft');
        const btnPublish = document.getElementById('btn-publish');
        btnDraft.disabled = true;
        btnPublish.disabled = true;
        btnDraft.textContent = 'Đang lưu...';
        btnPublish.textContent = 'Đang lưu...';

        fetch(url, {
            method: 'POST',
            body: formData
        })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    alert(currentEditId ? 'Cập nhật bài viết thành công!' : 'Tạo bài viết thành công!');
                    resetForm();
                    loadArticles();
                } else {
                    alert('Lỗi: ' + (data.error || 'Unknown error'));
                }
            })
            .catch(error => {
                console.error('Error saving article:', error);
                alert('Lỗi: ' + error.message);
            })
            .finally(() => {
                btnDraft.disabled = false;
                btnPublish.disabled = false;
                btnDraft.textContent = 'Lưu nháp';
                btnPublish.textContent = 'Đăng bài';
            });
    }

    function editArticle(articleId) {
        fetch(CTX + '/admin/article/get?id=' + articleId)
            .then(response => response.json())
            .then(article => {
                console.log('Loaded article:', article);

                document.getElementById('editor-title').textContent = 'Chỉnh sửa bài viết';

                document.getElementById('article-title').value = article.title || '';
                ckeditorInstance.setData(article.content || '');
                document.getElementById('voucher-select').value = article.voucherId || '';

                if (article.startDate) {
                    document.getElementById('start-date').value = formatDateTimeLocal(article.startDate);
                }
                if (article.endDate) {
                    document.getElementById('end-date').value = formatDateTimeLocal(article.endDate);
                }

                if (article.bannerImageUrl) {
                    console.log('Banner URL:', article.bannerImageUrl);

                    document.getElementById('existing-image').src = article.bannerImageUrl;
                    document.getElementById('existing-image-container').style.display = 'block';

                    document.getElementById('upload-image-container').style.display = 'none';
                    document.getElementById('new-image-preview-container').style.display = 'none';

                    currentBannerUrl = article.bannerImageUrl;
                } else {
                    document.getElementById('existing-image-container').style.display = 'none';
                    document.getElementById('upload-image-container').style.display = 'block';
                    document.getElementById('new-image-preview-container').style.display = 'none';
                    currentBannerUrl = null;
                }

                document.getElementById('banner-image').value = '';

                currentEditId = articleId;
                document.getElementById('btn-cancel').style.display = 'inline-block';

                window.scrollTo({ top: 0, behavior: 'smooth' });
            })
            .catch(error => {
                console.error('Error loading article:', error);
                alert('Không thể tải bài viết');
            });
    }

    function deleteArticle(articleId, title) {
        if (!confirm('Bạn có chắc muốn xóa bài viết "' + title + '"?\n\nThao tác này không thể hoàn tác!')) {
            return;
        }

        fetch(CTX + '/admin/article/delete', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            },
            body: 'id=' + articleId
        })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    alert('Xóa bài viết thành công!');
                    loadArticles();
                } else {
                    alert('Lỗi: ' + (data.error || 'Unknown error'));
                }
            })
            .catch(error => {
                console.error('Error deleting article:', error);
                alert('Lỗi: ' + error.message);
            });
    }

    function resetForm() {
        document.getElementById('editor-title').textContent = 'Tạo bài viết khuyến mãi mới';
        document.getElementById('article-title').value = '';
        ckeditorInstance.setData('');
        document.getElementById('voucher-select').value = '';
        document.getElementById('start-date').value = '';
        document.getElementById('end-date').value = '';

        document.getElementById('banner-image').value = '';
        document.getElementById('existing-image').src = '';
        document.getElementById('new-image-preview').src = '';
        document.getElementById('existing-image-container').style.display = 'none';
        document.getElementById('new-image-preview-container').style.display = 'none';
        document.getElementById('upload-image-container').style.display = 'block';

        currentEditId = null;
        currentBannerUrl = null;
        document.getElementById('btn-cancel').style.display = 'none';
    }

    function switchTab(tabId) {
        document.querySelectorAll('.tab-content').forEach(tab => {
            tab.classList.remove('active');
        });

        document.querySelectorAll('.tab-button').forEach(btn => {
            btn.classList.remove('active');
        });

        document.getElementById(tabId).classList.add('active');

        document.querySelector('[data-tab="' + tabId + '"]').classList.add('active');

        if (tabId === 'vouchers-tab') {
            loadVouchersTable();
        }
    }

    function loadVouchersTable() {
        fetch(CTX + '/admin/voucher/list')
            .then(response => response.json())
            .then(vouchers => {
                displayVouchers(vouchers);
            })
            .catch(error => {
                console.error('Error loading vouchers:', error);
            });
    }

    function displayVouchers(vouchers) {
        const tbody = document.getElementById('vouchers-table-body');
        tbody.innerHTML = '';

        if (!vouchers || vouchers.length === 0) {
            tbody.innerHTML = '<tr><td colspan="8" style="text-align:center;padding:40px;">Chưa có voucher nào</td></tr>';
            return;
        }

        vouchers.forEach(voucher => {
            const row = document.createElement('tr');

            const codeCell = document.createElement('td');
            codeCell.innerHTML = '<strong style="font-family:monospace; background:#f0f0f0; padding:4px 8px; border-radius:4px;">' +
                escapeHtml(voucher.voucherCode) + '</strong>';
            row.appendChild(codeCell);

            const typeCell = document.createElement('td');
            typeCell.textContent = voucher.discountType === 'percentage' ? 'Phần trăm' : 'Số tiền';
            row.appendChild(typeCell);

            const valueCell = document.createElement('td');
            if (voucher.discountType === 'percentage') {
                valueCell.textContent = voucher.discountValue + '%';
                valueCell.style.color = '#e74c3c';
                valueCell.style.fontWeight = 'bold';
            } else {
                valueCell.textContent = Number(voucher.discountValue).toLocaleString('vi-VN') + 'đ';
                valueCell.style.color = '#27ae60';
                valueCell.style.fontWeight = 'bold';
            }
            row.appendChild(valueCell);

            const minCell = document.createElement('td');
            minCell.textContent = Number(voucher.minOrderAmount).toLocaleString('vi-VN') + 'đ';
            row.appendChild(minCell);

            const usageCell = document.createElement('td');
            usageCell.textContent = voucher.currentUsage + '/' + voucher.maxUsage;
            if (voucher.currentUsage >= voucher.maxUsage) {
                usageCell.style.color = 'red';
                usageCell.style.fontWeight = 'bold';
            }
            row.appendChild(usageCell);

            const periodCell = document.createElement('td');
            periodCell.className = 'period';
            periodCell.style.fontSize = '12px';
            periodCell.innerHTML =
                formatDateTime(voucher.validFrom) + '<br>' +
                formatDateTime(voucher.validTo);
            row.appendChild(periodCell);

            const statusCell = document.createElement('td');
            const statusSpan = document.createElement('span');
            statusSpan.className = 'status';

            const now = new Date();
            const validFrom = new Date(voucher.validFrom);
            const validTo = new Date(voucher.validTo);

            if (!voucher.isActive) {
                statusSpan.classList.add('draft');
                statusSpan.textContent = 'Tắt';
            } else if (voucher.currentUsage >= voucher.maxUsage) {
                statusSpan.classList.add('expired');
                statusSpan.textContent = 'Hết lượt';
            } else if (now < validFrom) {
                statusSpan.classList.add('draft');
                statusSpan.textContent = 'Chưa bắt đầu';
            } else if (now > validTo) {
                statusSpan.classList.add('expired');
                statusSpan.textContent = 'Hết hạn';
            } else {
                statusSpan.classList.add('active');
                statusSpan.textContent = 'Hoạt động';
            }
            statusCell.appendChild(statusSpan);
            row.appendChild(statusCell);

            const actionCell = document.createElement('td');
            actionCell.className = 'action-icons';

            const editBtn = document.createElement('a');
            editBtn.href = '#';
            editBtn.title = 'Sửa';
            editBtn.innerHTML = '<i class="fas fa-edit"></i>';
            editBtn.onclick = function(e) {
                e.preventDefault();
                editVoucher(voucher.id);
            };

            const deleteBtn = document.createElement('a');
            deleteBtn.href = '#';
            deleteBtn.className = 'delete';
            deleteBtn.title = 'Xóa';
            deleteBtn.innerHTML = '<i class="fas fa-trash-alt"></i>';
            deleteBtn.onclick = function(e) {
                e.preventDefault();
                deleteVoucher(voucher.id, voucher.voucherCode);
            };

            actionCell.appendChild(editBtn);
            actionCell.appendChild(document.createTextNode(' '));
            actionCell.appendChild(deleteBtn);
            row.appendChild(actionCell);

            tbody.appendChild(row);
        });
    }

    let currentVoucherEditId = null;

    function saveVoucher() {
        const code = document.getElementById('voucher-code').value.trim().toUpperCase();
        const type = document.getElementById('discount-type').value;
        const value = document.getElementById('discount-value').value;
        const minAmount = document.getElementById('min-order-amount').value;
        const maxUsage = document.getElementById('max-usage').value;
        const validFrom = document.getElementById('voucher-valid-from').value;
        const validTo = document.getElementById('voucher-valid-to').value;
        const isActive = document.getElementById('voucher-is-active').checked;

        if (!code) {
            alert('Vui lòng nhập mã voucher');
            return;
        }

        if (!value || parseFloat(value) <= 0) {
            alert('Vui lòng nhập giá trị giảm hợp lệ');
            return;
        }

        const formData = new URLSearchParams();

        if (currentVoucherEditId) {
            formData.append('voucher-id', currentVoucherEditId);
        }

        formData.append('voucher-code', code);
        formData.append('discount-type', type);
        formData.append('discount-value', value);
        formData.append('min-order-amount', minAmount || '0');
        formData.append('max-usage', maxUsage || '100');
        formData.append('valid-from', validFrom);
        formData.append('valid-to', validTo);
        formData.append('is-active', isActive ? '1' : '0');

        const btnSave = document.getElementById('btn-save-voucher');
        btnSave.disabled = true;
        btnSave.textContent = 'Đang lưu...';

        fetch(CTX + '/admin/voucher/add', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            },
            body: formData.toString()
        })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    alert(currentVoucherEditId ? 'Cập nhật voucher thành công!' : 'Tạo voucher thành công!');
                    resetVoucherForm();
                    loadVouchersTable();
                    loadVouchers();
                } else {
                    alert('Lỗi: ' + (data.error || 'Unknown error'));
                }
            })
            .catch(error => {
                console.error('Error saving voucher:', error);
                alert('Lỗi: ' + error.message);
            })
            .finally(() => {
                btnSave.disabled = false;
                btnSave.textContent = 'Lưu Voucher';
            });
    }
    function editVoucher(voucherId) {
        fetch(CTX + '/admin/voucher/get?id=' + voucherId)
            .then(response => response.json())
            .then(voucher => {
                console.log('Loaded voucher:', voucher);

                document.getElementById('voucher-editor-title').textContent = 'Chỉnh sửa Voucher';

                document.getElementById('voucher-code').value = voucher.voucherCode || '';
                document.getElementById('discount-type').value = voucher.discountType || 'percentage';
                document.getElementById('discount-value').value = voucher.discountValue || '';
                document.getElementById('min-order-amount').value = voucher.minOrderAmount || '0';
                document.getElementById('max-usage').value = voucher.maxUsage || '100';
                document.getElementById('voucher-is-active').checked = voucher.isActive;

                if (voucher.validFrom) {
                    document.getElementById('voucher-valid-from').value = formatDateTimeLocal(voucher.validFrom);
                }
                if (voucher.validTo) {
                    document.getElementById('voucher-valid-to').value = formatDateTimeLocal(voucher.validTo);
                }

                currentVoucherEditId = voucherId;
                document.getElementById('btn-cancel-voucher').style.display = 'inline-block';

                window.scrollTo({ top: 0, behavior: 'smooth' });
            })
            .catch(error => {
                console.error('Error loading voucher:', error);
                alert('Không thể tải voucher');
            });
    }

    function deleteVoucher(voucherId, code) {
        if (!confirm('Bạn có chắc muốn xóa voucher "' + code + '"?\n\nLưu ý: Các bài viết sử dụng voucher này sẽ không bị xóa.')) {
            return;
        }

        fetch(CTX + '/admin/voucher/delete', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            },
            body: 'id=' + voucherId
        })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    alert('Xóa voucher thành công!');
                    loadVouchersTable();
                    loadVouchers();
                } else {
                    alert('Lỗi: ' + (data.error || 'Unknown error'));
                }
            })
            .catch(error => {
                console.error('Error deleting voucher:', error);
                alert('Lỗi: ' + error.message);
            });
    }

    function resetVoucherForm() {
        document.getElementById('voucher-editor-title').textContent = 'Tạo Voucher mới';
        document.getElementById('voucher-code').value = '';
        document.getElementById('discount-type').value = 'percentage';
        document.getElementById('discount-value').value = '';
        document.getElementById('min-order-amount').value = '0';
        document.getElementById('max-usage').value = '100';
        document.getElementById('voucher-valid-from').value = '';
        document.getElementById('voucher-valid-to').value = '';
        document.getElementById('voucher-is-active').checked = true;

        currentVoucherEditId = null;
        document.getElementById('btn-cancel-voucher').style.display = 'none';
    }

    function escapeHtml(text) {
        if (!text) return '';
        const div = document.createElement('div');
        div.textContent = text;
        return div.innerHTML;
    }

    function formatDateTime(dateStr) {
        if (!dateStr) return '-';
        const date = new Date(dateStr);
        const d = String(date.getDate()).padStart(2, '0');
        const m = String(date.getMonth() + 1).padStart(2, '0');
        const y = date.getFullYear();
        const h = String(date.getHours()).padStart(2, '0');
        const min = String(date.getMinutes()).padStart(2, '0');
        return d + '/' + m + '/' + y + ' ' + h + ':' + min;
    }

    function formatDateTimeLocal(dateStr) {
        if (!dateStr) return '';
        const date = new Date(dateStr);
        const y = date.getFullYear();
        const m = String(date.getMonth() + 1).padStart(2, '0');
        const d = String(date.getDate()).padStart(2, '0');
        const h = String(date.getHours()).padStart(2, '0');
        const min = String(date.getMinutes()).padStart(2, '0');
        return y + '-' + m + '-' + d + 'T' + h + ':' + min;
    }

    console.log('✅ Promotion management loaded');
</script>
<script src="${pageContext.request.contextPath}/scripts/admin/admin.js"></script>
</body>
</html>
