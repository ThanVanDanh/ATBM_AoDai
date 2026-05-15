<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Admin - Quản lý Trang chủ</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/7.0.1/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/style/admin.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/style/dashboard.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/style/alert.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/style/adminhome.css">
</head>
<body>
<div class="admin-container">
    <jsp:include page="sidebar.jsp" />
    <main class="main-content">
        <header class="admin-header">
            <h2> Quản lý Trang chủ</h2>
            <div class="header-actions">
                <a href="${pageContext.request.contextPath}/home" target="_blank" class="btn-preview">
                    Xem trang chủ
                </a>
            </div>
        </header>
        <div class="home-config-container">
            <div id="alert-container"></div>

            <div class="banner-management-section">
                <div class="section-card">
                    <div class="section-header">
                        <h3><i class="fas fa-images"></i> Quản lý Banner Slideshow</h3>
                        <button class="btn-add-banner" onclick="openBannerModal()">
                            <i class="fas fa-plus"></i> Thêm Banner
                        </button>
                    </div>
                    <div class="section-body">
                        <div id="banner-list" class="banner-list">
                            <div class="empty-state" id="banner-empty-state">
                                <i class="fas fa-image"></i>
                                <p>Đang tải danh sách banner...</p>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            
            <hr class="section-divider">

            <button class="btn-add-section" onclick="addNewSection()">
                <i class="fas fa-plus-circle"></i> Thêm Section Mới
            </button>
            <div id="sections-container">
                <div class="empty-state" id="empty-state">
                    <i class="fas fa-inbox"></i>
                    <p>Đang tải cấu hình...</p>
                </div>
            </div>
        </div>
    </main>
</div>

<div id="bannerModal" class="modal">
    <div class="modal-content">
        <div class="modal-header">
            <h3 id="bannerModalTitle"><i class="fas fa-image"></i> Thêm Banner Mới</h3>
            <button class="modal-close" onclick="closeBannerModal()">&times;</button>
        </div>
        <form id="bannerForm" enctype="multipart/form-data">
            <input type="hidden" id="bannerId" name="bannerId">
            <div class="form-group">
                <label><i class="fas fa-image"></i> Ảnh Banner <span class="required">*</span></label>
                <div class="image-upload-area" id="bannerImageUpload">
                    <input type="file" id="bannerImage" name="image" accept="image/*" onchange="previewBannerImage(this)">
                    <div class="upload-placeholder" id="uploadPlaceholder">
                        <i class="fas fa-cloud-upload-alt"></i>
                        <p>Kéo thả hoặc click để chọn ảnh</p>
                        <span>PNG, JPG, JPEG (Khuyến nghị: 1920x600px)</span>
                    </div>
                    <img id="bannerPreview" class="image-preview" style="display:none;">
                </div>
            </div>
            <div class="form-group">
                <label><i class="fas fa-heading"></i> Mô tả (Alt Text)</label>
                <input type="text" id="bannerAltText" name="altText" placeholder="Mô tả ngắn về banner...">
            </div>
            <div class="form-row">
                <div class="form-group half">
                    <label><i class="fas fa-sort-numeric-up"></i> Thứ tự hiển thị</label>
                    <input type="number" id="bannerSortOrder" name="sortOrder" min="1" value="1">
                </div>
                <div class="form-group half">
                    <label><i class="fas fa-toggle-on"></i> Trạng thái</label>
                    <select id="bannerIsActive" name="isActive">
                        <option value="true">Hiển thị</option>
                        <option value="false">Ẩn</option>
                    </select>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn-cancel" onclick="closeBannerModal()">Hủy</button>
                <button type="submit" class="btn-save" id="btnSaveBanner">
                    <i class="fas fa-save"></i> Lưu Banner
                </button>
            </div>
        </form>
    </div>
</div>

<script>
    var CTX = '<c:out value="${pageContext.request.contextPath}" />';
    
    var ALL_CATEGORIES = [
        <c:forEach var="c" items="${categories}" varStatus="s">
        { id: <c:out value="${c.id}" />, name: '<c:out value="${c.nameCategory}" />' }<c:if test="${!s.last}">,</c:if>
        </c:forEach>
    ];

    var loadedSections = {};

    document.addEventListener('DOMContentLoaded', function() {
        console.log('Admin Home Page loaded');
        console.log('Categories:', ALL_CATEGORIES.length);
        
        if (ALL_CATEGORIES.length === 0) {
            showAlert('Chưa có danh mục nào. Vui lòng tạo danh mục trước.', 'danger');
        }

        loadBanners();
        loadAllSections();
    });

    function loadAllSections() {
        fetch(CTX + '/admin/home/api')
            .then(function(response) {
                if (!response.ok) throw new Error('HTTP ' + response.status);
                return response.json();
            })
            .then(function(data) {
                console.log('All sections from DB:', data);
                if (data.success && data.sections && data.sections.length > 0) {
                    data.sections.forEach(function(key) {
                        loadSection(key);
                    });
                } else {
                    showEmptyState();
                }
            })
            .catch(function(error) {
                console.error('Error loading sections list:', error);
                showEmptyState();
            });
    }

    function loadBanners() {
        fetch(CTX + '/admin/banner/api')
            .then(function(response) { 
                if (!response.ok) {
                    throw new Error('HTTP ' + response.status);
                }
                return response.json(); 
            })
            .then(function(data) {
                if (data.success) {
                    renderBannerList(data.banners);
                } else {
                    showAlert('Lỗi tải banner: ' + data.error, 'danger');
                }
            })
            .catch(function(error) {
                console.error('Error loading banners:', error);
                document.getElementById('banner-empty-state').innerHTML = 
                    '<i class="fas fa-exclamation-triangle"></i><p>Không thể tải danh sách banner</p>';
            });
    }

    function renderBannerList(banners) {
        var container = document.getElementById('banner-list');
        
        if (!banners || banners.length === 0) {
            container.innerHTML = '<div class="empty-state"><i class="fas fa-image"></i><p>Chưa có banner nào. Hãy thêm banner mới!</p></div>';
            return;
        }
        
        var html = '<div class="banner-grid">';
        banners.forEach(function(banner) {
            var statusClass = banner.isActive ? 'active' : 'inactive';
            var statusText = banner.isActive ? 'Hiển thị' : 'Ẩn';
            html += '<div class="banner-item" data-id="' + banner.id + '">' +
                '<div class="banner-image-wrapper">' +
                    '<img src="' + banner.imageUrl + '" alt="' + (banner.altText || 'Banner') + '">' +
                    '<div class="banner-overlay">' +
                        '<button class="btn-icon" onclick="editBanner(' + banner.id + ')" title="Sửa"><i class="fas fa-edit"></i></button>' +
                        '<button class="btn-icon btn-danger" onclick="deleteBanner(' + banner.id + ')" title="Xóa"><i class="fas fa-trash"></i></button>' +
                    '</div>' +
                '</div>' +
                '<div class="banner-info">' +
                    '<span class="banner-order">#' + banner.sortOrder + '</span>' +
                    '<span class="banner-status ' + statusClass + '">' + statusText + '</span>' +
                '</div>' +
                '<p class="banner-alt">' + (banner.altText || 'Không có mô tả') + '</p>' +
            '</div>';
        });
        html += '</div>';
        
        container.innerHTML = html;
    }

    function openBannerModal(bannerId) {
        var modal = document.getElementById('bannerModal');
        var form = document.getElementById('bannerForm');
        var title = document.getElementById('bannerModalTitle');
        
        form.reset();
        document.getElementById('bannerId').value = '';
        document.getElementById('bannerPreview').style.display = 'none';
        document.getElementById('uploadPlaceholder').style.display = 'flex';
        document.getElementById('bannerImage').required = true;
        
        if (bannerId) {
            title.innerHTML = '<i class="fas fa-edit"></i> Chỉnh sửa Banner';
            document.getElementById('bannerImage').required = false;
            loadBannerForEdit(bannerId);
        } else {
            title.innerHTML = '<i class="fas fa-image"></i> Thêm Banner Mới';
        }
        
        modal.style.display = 'flex';
    }

    function closeBannerModal() {
        document.getElementById('bannerModal').style.display = 'none';
    }

    function loadBannerForEdit(bannerId) {
        fetch(CTX + '/admin/banner/api/' + bannerId)
            .then(function(response) { return response.json(); })
            .then(function(data) {
                if (data.success && data.banner) {
                    var b = data.banner;
                    document.getElementById('bannerId').value = b.id;
                    document.getElementById('bannerAltText').value = b.altText || '';
                    document.getElementById('bannerSortOrder').value = b.sortOrder;
                    document.getElementById('bannerIsActive').value = b.isActive ? 'true' : 'false';
                    
                    if (b.imageUrl) {
                        document.getElementById('bannerPreview').src = b.imageUrl;
                        document.getElementById('bannerPreview').style.display = 'block';
                        document.getElementById('uploadPlaceholder').style.display = 'none';
                    }
                }
            });
    }

    function previewBannerImage(input) {
        if (input.files && input.files[0]) {
            var reader = new FileReader();
            reader.onload = function(e) {
                document.getElementById('bannerPreview').src = e.target.result;
                document.getElementById('bannerPreview').style.display = 'block';
                document.getElementById('uploadPlaceholder').style.display = 'none';
            };
            reader.readAsDataURL(input.files[0]);
        }
    }

    function editBanner(id) {
        openBannerModal(id);
    }

    function deleteBanner(id) {
        if (!confirm('Bạn có chắc muốn xóa banner này?')) return;
        
        fetch(CTX + '/admin/banner/api/' + id, { method: 'DELETE' })
            .then(function(response) { return response.json(); })
            .then(function(data) {
                if (data.success) {
                    showAlert('Xóa banner thành công!', 'success');
                    loadBanners();
                } else {
                    showAlert('Lỗi: ' + data.error, 'danger');
                }
            })
            .catch(function(error) {
                showAlert('Lỗi kết nối server', 'danger');
            });
    }

    document.getElementById('bannerForm').addEventListener('submit', function(e) {
        e.preventDefault();
        
        var bannerId = document.getElementById('bannerId').value;
        var formData = new FormData(this);
        
        var btnSave = document.getElementById('btnSaveBanner');
        var originalHtml = btnSave.innerHTML;
        btnSave.disabled = true;
        btnSave.innerHTML = '<span class="loading"></span> Đang lưu...';
        
        var url = CTX + '/admin/banner/api';
        var method = 'POST';
        
        if (bannerId) {
            url = CTX + '/admin/banner/api/' + bannerId;
            method = 'PUT';
        }
        
        fetch(url, {
            method: method,
            body: formData
        })
        .then(function(response) { return response.json(); })
        .then(function(data) {
            if (data.success) {
                showAlert(bannerId ? 'Cập nhật banner thành công!' : 'Thêm banner thành công!', 'success');
                closeBannerModal();
                loadBanners();
            } else {
                showAlert('Lỗi: ' + data.error, 'danger');
            }
        })
        .catch(function(error) {
            console.error('Error:', error);
            showAlert('Lỗi kết nối server', 'danger');
        })
        .finally(function() {
            btnSave.disabled = false;
            btnSave.innerHTML = originalHtml;
        });
    });

    window.addEventListener('click', function(e) {
        var modal = document.getElementById('bannerModal');
        if (e.target === modal) {
            closeBannerModal();
        }
    });


    function loadSection(sectionKey) {
        if (loadedSections[sectionKey]) return;
        
        console.log('Loading section:', sectionKey);
        
        fetch(CTX + '/admin/home/api/' + sectionKey)
            .then(function(response) {
                if (!response.ok) throw new Error('HTTP ' + response.status);
                return response.json();
            })
            .then(function(data) {
                console.log('Loaded:', sectionKey, data);
                renderSection(sectionKey, data);
                loadedSections[sectionKey] = true;
                hideEmptyState();
            })
            .catch(function(error) {
                console.error('Error loading section:', sectionKey, error);
                renderSection(sectionKey, { title: '', tabs: [] });
                loadedSections[sectionKey] = true;
                hideEmptyState();
            });
    }

    function renderSection(sectionKey, data) {
        var container = document.getElementById('sections-container');
        var isSetType = sectionKey.indexOf('set_') === 0;
        var typeLabel = isSetType ? 'Set đồ (1 danh mục)' : 'Section thường (tối đa 4 tabs)';
        var titleValue = data.title || '';
        
        var html = '<div class="section-card" id="section-' + sectionKey + '">' +
            '<div class="section-header">' +
                '<h3><i class="fas fa-layer-group"></i> ' + sectionKey + ' <small>' + typeLabel + '</small></h3>' +
                '<button class="btn-delete-section" onclick="removeSection(\'' + sectionKey + '\')" title="Xóa section"><i class="fas fa-trash"></i></button>' +
            '</div>' +
            '<div class="section-body">' +
                '<div class="form-group">' +
                    '<label><i class="fas fa-heading"></i> Tiêu đề hiển thị</label>' +
                    '<input type="text" id="title-' + sectionKey + '" value="' + titleValue + '" placeholder="Nhập tiêu đề section...">' +
                '</div>' +
                '<div class="tabs-container">' +
                    '<h4><i class="fas fa-list"></i> Danh sách Tab</h4>' +
                    '<div id="tabs-' + sectionKey + '"></div>' +
                    (isSetType ? '' : '<button class="btn-add-tab" onclick="addTab(\'' + sectionKey + '\')"><i class="fas fa-plus"></i> Thêm Tab</button>') +
                '</div>' +
            '</div>' +
            '<div class="section-footer">' +
                '<button class="btn-save" onclick="saveSection(\'' + sectionKey + '\')" id="btn-save-' + sectionKey + '">' +
                    '<i class="fas fa-save"></i> Lưu cấu hình' +
                '</button>' +
            '</div>' +
        '</div>';
        
        container.insertAdjacentHTML('beforeend', html);
        
        if (isSetType) {
            var categoryId = data.categoryId || (data.tabs && data.tabs[0] ? data.tabs[0].categoryId : '');
            addTab(sectionKey, categoryId);
        } else {
            if (data.tabs && data.tabs.length > 0) {
                data.tabs.forEach(function(tab) {
                    addTab(sectionKey, tab.categoryId);
                });
            } else {
                addTab(sectionKey);
            }
        }
    }

    function addTab(sectionKey, selectedId) {
        selectedId = selectedId || '';
        var container = document.getElementById('tabs-' + sectionKey);
        var isSetType = sectionKey.indexOf('set_') === 0;
        var currentTabs = container.querySelectorAll('.tab-row').length;
        
        if (isSetType && currentTabs >= 1) {
            showAlert('Set đồ chỉ có thể chọn 1 danh mục', 'danger');
            return;
        }
        if (!isSetType && currentTabs >= 4) {
            showAlert('Tối đa 4 tabs cho mỗi section', 'danger');
            return;
        }
        
        var tabIndex = currentTabs + 1;
        var options = '<option value="">-- Chọn danh mục --</option>';
        ALL_CATEGORIES.forEach(function(c) {
            var selected = (c.id == selectedId) ? 'selected' : '';
            options += '<option value="' + c.id + '" ' + selected + '>' + c.name + '</option>';
        });
        
        var removeBtn = isSetType ? '' : '<button class="btn-remove-tab" onclick="removeTab(this, \'' + sectionKey + '\')" title="Xóa tab"><i class="fas fa-times"></i></button>';
        
        var html = '<div class="tab-row" data-position="' + tabIndex + '">' +
            '<label>Tab ' + tabIndex + ':</label>' +
            '<select class="tab-select" onchange="validateTabs(\'' + sectionKey + '\')">' + options + '</select>' +
            removeBtn +
        '</div>';
        
        container.insertAdjacentHTML('beforeend', html);
    }

    function removeTab(button, sectionKey) {
        var tabRow = button.closest('.tab-row');
        tabRow.remove();
        
        var container = document.getElementById('tabs-' + sectionKey);
        var tabs = container.querySelectorAll('.tab-row');
        tabs.forEach(function(tab, index) {
            tab.dataset.position = index + 1;
            tab.querySelector('label').textContent = 'Tab ' + (index + 1) + ':';
        });
    }

    function validateTabs(sectionKey) {
        var container = document.getElementById('tabs-' + sectionKey);
        var selects = container.querySelectorAll('.tab-select');
        var values = [];
        var hasDuplicate = false;
        
        selects.forEach(function(select) {
            if (select.value && values.indexOf(select.value) !== -1) {
                hasDuplicate = true;
                select.style.borderColor = '#dc3545';
            } else {
                select.style.borderColor = '#ced4da';
                if (select.value) values.push(select.value);
            }
        });
        
        if (hasDuplicate) {
            showAlert('Không được chọn trùng danh mục!', 'danger');
        }
        
        return !hasDuplicate;
    }

    function saveSection(sectionKey) {
        if (!validateTabs(sectionKey)) return;
        
        var title = document.getElementById('title-' + sectionKey).value.trim();
        var container = document.getElementById('tabs-' + sectionKey);
        var selects = container.querySelectorAll('.tab-select');
        var isSetType = sectionKey.indexOf('set_') === 0;
        
        var payload = { title: title };
        
        if (isSetType) {
            var categoryId = selects[0] ? selects[0].value : '';
            if (!categoryId) {
                showAlert('Vui lòng chọn danh mục cho Set đồ', 'danger');
                return;
            }
            payload.categoryId = parseInt(categoryId);
        } else {
            var tabs = [];
            selects.forEach(function(select, index) {
                if (select.value) {
                    tabs.push({ position: index + 1, categoryId: parseInt(select.value) });
                }
            });
            
            if (tabs.length === 0) {
                showAlert('Vui lòng chọn ít nhất 1 danh mục', 'danger');
                return;
            }
            payload.tabs = tabs;
        }
        
        var saveBtn = document.getElementById('btn-save-' + sectionKey);
        var originalHtml = saveBtn.innerHTML;
        saveBtn.disabled = true;
        saveBtn.innerHTML = '<span class="loading"></span> Đang lưu...';
        
        console.log('Saving section:', sectionKey, payload);
        
        fetch(CTX + '/admin/home/api/' + sectionKey, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        })
        .then(function(response) { return response.json(); })
        .then(function(data) {
            console.log('Response:', data);
            if (data.success) {
                showAlert('Lưu cấu hình thành công!', 'success');
            } else {
                showAlert('Lỗi: ' + (data.error || 'Không xác định'), 'danger');
            }
        })
        .catch(function(error) {
            console.error('Error:', error);
            showAlert('Lỗi kết nối server', 'danger');
        })
        .finally(function() {
            saveBtn.disabled = false;
            saveBtn.innerHTML = originalHtml;
        });
    }

    function addNewSection() {
        var name = prompt('Nhập mã ID cho section mới (viết liền, không dấu):\n\nVí dụ: tet_2026, summer_sale, set_combo');
        if (!name) return;
        
        var key = name.trim().toLowerCase().replace(/\s+/g, '_').replace(/[^a-z0-9_]/g, '');
        
        if (!key) {
            showAlert('Mã section không hợp lệ', 'danger');
            return;
        }
        
        if (loadedSections[key]) {
            showAlert('Section này đã tồn tại!', 'danger');
            return;
        }
        
        loadSection(key);
    }

    function removeSection(sectionKey) {
        if (!confirm('Bạn có chắc muốn xóa section "' + sectionKey + '"?')) return;
        
        var sectionEl = document.getElementById('section-' + sectionKey);
        if (sectionEl) {
            sectionEl.remove();
            delete loadedSections[sectionKey];
        }
        
        if (Object.keys(loadedSections).length === 0) {
            showEmptyState();
        }
    }

    function showAlert(message, type) {
        var container = document.getElementById('alert-container');
        var id = 'alert-' + Date.now();
        var icon = type === 'success' ? 'fa-check-circle' : 'fa-exclamation-circle';
        
        var html = '<div class="alert alert-' + type + '" id="' + id + '">' +
            '<i class="fas ' + icon + '"></i> ' + message +
            '<button onclick="this.parentElement.remove()" style="float:right;background:none;border:none;cursor:pointer;font-size:18px;">&times;</button>' +
        '</div>';
        
        container.insertAdjacentHTML('beforeend', html);
        
        setTimeout(function() {
            var alertEl = document.getElementById(id);
            if (alertEl) alertEl.remove();
        }, 5000);
    }

    function hideEmptyState() {
        var emptyState = document.getElementById('empty-state');
        if (emptyState) emptyState.style.display = 'none';
    }

    function showEmptyState() {
        var emptyState = document.getElementById('empty-state');
        if (emptyState) {
            emptyState.innerHTML = '<i class="fas fa-inbox"></i><p>Chưa có section nào.</p>';
            emptyState.style.display = 'block';
        }
    }
</script>
<script src="${pageContext.request.contextPath}/scripts/admin/admin.js"></script>
</body>
</html>
