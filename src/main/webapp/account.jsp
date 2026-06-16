<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Việt Sắc Đỏ - Thông tin</title>
    <link rel="icon" href="image/logoaodai.jpg" type="image/jpeg">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/7.0.1/css/all.min.css"
          integrity="sha512-2SwdPD6INVrV/lHTZbO2nodKhrnDdJK9/kg2XD1r9uGqPo1cUbujc+IYdlYdEErWNu69gVcYgdxlmVmzTWnetw=="
          crossorigin="anonymous" referrerpolicy="no-referrer" />
    <link rel="stylesheet" href="style/account.css">
    <script src="scripts/account.js"></script>
    <script src="scripts/genKey.js"></script>
    <link rel="stylesheet" href="style/style-header.css">
    <link rel="stylesheet" href="style/footer.css">
    <link rel="stylesheet" href="style/breadcrumb.css">
    <script src="scripts/home.js"></script>
</head>

<body>
<div class="search-overlay-container" id="searchOverlay">

    <div class="search-overlay-header">
        <div class="logo">
            <a href="${pageContext.request.contextPath}/home">
                <img src="image/logo.png" alt="Logo Việt Sắc Đỏ">
            </a>
        </div>

        <form class="search-overlay-form">
            <input type="text" id="searchInput"
                   placeholder="áo dài truyền thống, quần áo dài, vòng tay...">
            <button type="submit"><i class="fa-solid fa-magnifying-glass"></i></button>
        </form>

        <div class="icons">
            <div class="user-menu">
                <a><i class="fa-regular fa-user"></i></a>
                <ul class="user">
                    <li><a href="login.jsp">Đăng nhập</a></li>
                    <li><a href="signup.jsp">Đăng ký</a></li>
                </ul>
            </div>
            <div class="mini-cart-menu">
                <a href="giohang.jsp" title="Giỏ hàng">
                    <i class="fa-solid fa-cart-shopping"></i>
                    <span class="mini-count_item count_item_pr">3</span>
                </a>
                <div class="mini-cart-content">
                    <div class="mini-empty-cart">
                        <p>Chưa có sản phẩm trong giỏ hàng</p>
                    </div>
                    <ul class="mini-cart-items-list">
                        <li> <img src="image/truyenthong1.png" alt="Áo dài truyền thống Quỳnh Hân">
                            <div class="mini-item-info">
                                <a href="product-information.jsp" class="mini-item-name">Áo dài truyền
                                    thống Quỳnh Hân</a>
                                <span class="mini-item-meta">Size A / Quỳnh Hân</span>
                                <span class="mini-item-price">711,000₫</span>
                                <span class="mini-quantity">x1</span>
                            </div>
                            <button class="remove-item"><i class="fa-solid fa-xmark"></i></button>
                        </li>
                        <li> <img src="image/truyenthong3.png" alt="Áo dài truyền thống Phúc Hương">
                            <div class="mini-item-info">
                                <a href="product-information.jsp" class="mini-item-name">Áo dài truyền
                                    thống Phúc Hương</a>
                                <span class="mini-item-meta">Size A / Phúc Hương</span>
                                <span class="mini-item-price">880,000₫</span>
                                <span class="mini-quantity">x1</span>
                            </div>
                            <button class="remove-item"><i class="fa-solid fa-xmark"></i></button>
                        </li>
                        <li> <img src="image/truyenthong4.png" alt="Áo dài truyền thống Quỳnh Châu">
                            <div class="mini-item-info">
                                <a href="product-information.jsp" class="mini-item-name">Áo dài truyền
                                    thống Quỳnh Châu</a>
                                <span class="mini-item-meta">Size A / Quỳnh Châu</span>
                                <span class="mini-item-price">790,000₫</span>
                                <span class="mini-quantity">x1</span>
                            </div>
                            <button class="remove-item"><i class="fa-solid fa-xmark"></i></button>
                        </li>
                    </ul>
                    <div class="mini-cart-footer">
                        <div class="mini-cart-total">
                                            <span>Tổng tiền tạm tính: <strong
                                                    class="mini-total-price">2,281,000₫</strong></span>
                        </div>
                        <a href="giohang.jsp" class="btn-pay">Tiến hành thanh toán</a>
                    </div>

                </div>
            </div>
        </div>
    </div>

    <div class="search-overlay-close-area" id="searchCloseArea"></div>
</div>
<jsp:include page="header.jsp" />
<div class="breadcrumb-container">
    <nav aria-label="breadcrumb">
        <ol class="breadcrumb">
            <li class="breadcrumb-item"><a href="index.jsp">Trang Chủ</a></li>
            <li class="breadcrumb-item active" aria-current="page">Tài khoản</li>
        </ol>
    </nav>
</div>
<main>
    <div class="account-container">
        <nav class="account-nav">
            <h2>TRANG TÀI KHOẢN</h2>
            <p>Xin chào, ${user.fullName}</p>
            <ul>
                <li>
                    <a class="active tab-btn" id="nav-details">Thông tin tài khoản</a>
                </li>
                <li>
                    <a class="tab-btn" id="nav-addresses">Địa chỉ</a>
                </li>
                <li>
                    <a class="tab-btn" id="nav-key">Tạo key</a>
                </li>
                <li>
                    <a href="Logout">Đăng xuất</a>
                </li>
            </ul>
        </nav>
        <div class="account-content">
            <div class="content-section" id="account-details">
                <h3>TÀI KHOẢN</h3>
                <c:if test="${not empty user}">
                    <p><strong>Tên tài khoản:</strong> ${user.fullName}</p>
                    <p><strong>Email:</strong> ${user.email}</p>
                    <p><strong>Điện thoại:</strong> ${user.phone}</p>
                </c:if>

                <hr class="account-divider">

                <h3>ĐƠN HÀNG CỦA BẠN</h3>
                <div class="order-history-table">
                    <table class="table-classic">
                        <thead>
                        <tr>
                            <th>Mã đơn hàng</th>
                            <th>Ngày mua</th>
                            <th>Tổng thanh toán</th>
                            <th>Trạng thái</th>
                            <th>Chữ ký điện tử</th>
                            <th>Thao tác</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:choose>
                            <c:when test="${not empty orders}">
                                <c:forEach var="order" items="${orders}">
                                    <tr>
                                        <td><strong>${order.orderCode}</strong></td>
                                        <td>${order.formattedCreatedAt}</td>
                                        <td>
                                            <strong style="color: #d32f2f;">
                                                <fmt:formatNumber value="${order.totalAmount}" pattern="#,###"/>₫
                                            </strong>
                                        </td>
                                        <td>
                                            <c:set var="statusClass" value="status-pending" />
                                            <c:choose>
                                                <c:when
                                                        test="${order.orderStatus.toLowerCase().contains('giao')}">
                                                    <c:set var="statusClass"
                                                           value="status-shipping" />
                                                </c:when>
                                                <c:when
                                                        test="${order.orderStatus.toLowerCase().contains('hoàn thành')}">
                                                    <c:set var="statusClass"
                                                           value="status-completed" />
                                                </c:when>
                                                <c:when
                                                        test="${order.orderStatus.toLowerCase().contains('hủy')}">
                                                    <c:set var="statusClass"
                                                           value="status-canceled" />
                                                </c:when>
                                            </c:choose>
                                            <span
                                                    class="status-badge ${statusClass}">${order.orderStatus}</span>
                                        </td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${order.signatureStatus == 'valid'}">
                                                    <span class="status-badge status-completed">Hợp lệ</span>
                                                </c:when>
                                                <c:when test="${order.signatureStatus == 'invalid'}">
                                                    <span class="status-badge status-canceled">Không hợp lệ</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="status-badge status-pending">Chưa ký</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>
                                            <c:if
                                                    test="${order.orderStatus.toLowerCase().contains('chờ') or order.orderStatus.toLowerCase().contains('đang xử lý')}">
                                                <button onclick="cancelOrder(${order.id})"
                                                        class="action-btn-cancel action-link">Hủy
                                                    đơn</button>
                                            </c:if>
                                            <a href="javascript:void(0)"
                                               onclick="viewOrderDetails(${order.id})"
                                               class="action-link action-view"
                                               title="Xem chi tiết"><i class="fas fa-eye"></i></a>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </c:when>
                            <c:otherwise>
                                <tr>
                                    <td colspan="6"
                                        style="text-align: center; padding: 30px; color: #777;">
                                        Bạn chưa có đơn hàng nào. <a
                                            href="${pageContext.request.contextPath}/home"
                                            style="color: #d32f2f;">Mua sắm ngay</a>
                                    </td>
                                </tr>
                            </c:otherwise>
                        </c:choose>
                        </tbody>
                    </table>
                </div>
            </div>

            <div class="content-section" id="account-addresses" style="display: none;">
                <h3>ĐỊA CHỈ CỦA BẠN</h3>
                <button type="button" class="btn-primary open-modal" id="add-address-btn">Thêm địa
                    chỉ</button>

                <c:if test="${empty addresses}">
                    <p style="margin-top: 15px;">Bạn chưa lưu địa chỉ nào.</p>
                </c:if>

                <c:forEach var="addr" items="${addresses}">
                    <div class="address-card">
                        <p>
                            <strong>Họ tên:</strong> ${addr.recipientName}
                            <c:if test="${addr.isDefault}">
                                <span class="default-badge">Địa chỉ mặc định</span>
                            </c:if>
                        </p>
                        <p><strong>Địa chỉ:</strong> ${addr.addressLine}, ${addr.cityProvince},
                                ${addr.country}</p>
                        <p><strong>Số điện thoại:</strong> ${addr.recipientPhone}</p>

                        <a href="#" class="edit-address-link open-modal" data-id="${addr.id}"
                           data-name="${addr.recipientName}" data-phone="${addr.recipientPhone}"
                           data-addr="${addr.addressLine}" data-city="${addr.cityProvince}"
                           data-default="${addr.isDefault}"> Chỉnh sửa địa chỉ
                        </a>

                        <a href="#" class="link-delete delete-address-link" data-id="${addr.id}">
                            Xóa
                        </a>
                    </div>
                </c:forEach>
            </div>

            <div class="content-section" id="account-key" style="display: none;">
                <h3>QUẢN LÝ KHÓA BẢO MẬT</h3>

                <c:if test="${empty currentPublicKey}">
                    <div class="key-status-alert info">
                        <p><i class="fas fa-info-circle"></i> Tài khoản của bạn chưa cấu hình Chữ ký số. Để đảm bảo đơn hàng không bị thay đổi trái phép, hãy khởi tạo khóa.</p>

                        <div class="key-action-container">

                            <button type="button" class="btn-primary" id="btn-generate-key" onclick="generateAndDownloadKeyPair()">
                                <i class="fas fa-plus"></i> Khởi tạo cặp khóa mới
                            </button>

                            <div class="key-import-group">
                                <button type="button" class="btn-secondary" id="btn-import-key" onclick="document.getElementById('import-key-file').click()">
                                    <i class="fas fa-upload"></i> Tải lên Public Key (.pem)
                                </button>
                                <button type="button" class="btn-secondary" id="btn-paste-key" onclick="openImportTextModal()">
                                    <i class="fas fa-paste"></i> Dán Public Key
                                </button>
                            </div>

                        </div>

                        <input type="file" id="import-key-file" accept=".pem,.txt" style="display: none;" onchange="handleImportPublicKey(event)">
                    </div>
                </c:if>

                <c:if test="${not empty currentPublicKey}">
                    <div class="key-card active-key">
                        <div class="key-info">
                            <p><strong>Trạng thái:</strong> <span class="status-badge status-shipping">Đang hoạt động</span></p>
                            <p><strong>Mã khóa (Key ID):</strong> #${currentKeyId}</p>
                            <p><strong>Ngày kích hoạt:</strong> ${currentKeyCreatedAt}</p>
                        </div>
                        <button type="button" class="action-btn-cancel" style="background-color: #d32f2f; color: white;" onclick="openRevokeKeyModal()">Báo Mất / Hủy Khóa Này</button>
                    </div>
                </c:if>

                <div id="key-download-container" style="display: none; margin-top: 20px; padding: 15px; border: 1px dashed #d32f2f; background: #fff5f5; border-radius: 5px;">
                    <h4 style="color: #d32f2f;"><i class="fas fa-exclamation-triangle"></i> TẢI XUỐNG PRIVATE KEY</h4>
                    <p style="font-size: 13px; color: #555;">Hệ thống đã lưu Public Key. File Private Key dưới đây thuộc quyền sở hữu cá nhân của bạn, trang web không sao lưu dữ liệu này.</p>
                    <div style="margin-top: 10px; display: flex; gap: 10px;">
                        <a href="#" id="download-key-link" class="btn-primary" style="text-decoration: none; text-align: center; line-height: 35px;">Tải xuống File .pem</a>
                    </div>
                </div>
            </div>

            <div class="modal-overlay" id="revoke-key-modal" style="display: none;">
                <div class="modal-content confirm-modal">
                    <button class="modal-close" onclick="closeRevokeKeyModal()">&times;</button>
                    <h4 class="text-danger">XÁC NHẬN BÁO MẤT KHÓA</h4>
                    <p>Hệ thống sẽ tiến hành <strong>đóng băng khóa hiện tại</strong> ngay tại thời điểm này.</p>
                    <p class="text-muted" style="font-size: 12px;">Các đơn hàng phát sinh sau thời gian này dùng khóa cũ sẽ bị từ chối. Các đơn hàng cũ trước đó vẫn được bảo toàn để đối chiếu.</p>
                    <div class="modal-actions center">
                        <button type="button" class="btn-secondary" onclick="closeRevokeKeyModal()">Hủy bỏ</button>
                        <button type="button" class="btn-danger" onclick="confirmRevokeKey()">Xác nhận hủy khóa</button>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <div class="modal-overlay" id="import-text-modal" style="display: none;">
        <div class="modal-content" style="max-width: 500px;">
            <button class="modal-close" onclick="closeImportTextModal()">&times;</button>
            <h4 style="color: #d32f2f; margin-bottom: 15px;">NHẬP PUBLIC KEY THỦ CÔNG</h4>
            <p style="font-size: 13px; color: #555; margin-bottom: 10px;">
                Vui lòng dán toàn bộ nội dung của Public Key (bao gồm cả dòng <code>BEGIN</code> và <code>END</code>) vào ô bên dưới:
            </p>
            <textarea id="pasted-public-key" spellcheck="false"
                      style="width: 100%; height: 180px; padding: 10px; border: 1px solid #ddd; border-radius: 5px; font-family: monospace; font-size: 12px; resize: vertical;"
                      placeholder="-----BEGIN PUBLIC KEY-----&#10;MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8A...&#10;-----END PUBLIC KEY-----"></textarea>

            <div style="display: flex; justify-content: end; gap: 10px; margin-top: 15px;">
                <button type="button" class="btn-secondary" onclick="closeImportTextModal()">Hủy bỏ</button>
                <button type="button" class="btn-primary" onclick="submitPastedKey()">Lưu Khóa</button>
            </div>
        </div>
    </div>
</main>
<div class="modal-overlay" id="add-address-modal" style="display: none;">
    <div class="modal-content">
        <button class="modal-close" id="close-add-modal">&times;</button>
        <h4>THÊM ĐỊA CHỈ MỚI</h4>
        <form method="POST" action="add-address">
            <div class="form-row">
                <div class="form-group half-width">
                    <label for="add-ho">Họ</label>
                    <input type="text" id="add-ho" name="ho" required>
                </div>
                <div class="form-group half-width">
                    <label for="add-ten">Tên</label>
                    <input type="text" id="add-ten" name="ten" required>
                </div>
            </div>
            <div class="form-group">
                <label for="add-sdt">Số điện thoại</label>
                <input type="text" id="add-sdt" name="sdt" required>
            </div>
            <div class="form-group">
                <label for="add-diachi">Địa chỉ</label>
                <input type="text" id="add-diachi" name="diachi" required>
            </div>
            <div class="form-row">
                <div class="form-group half-width">
                    <label for="add-quocgia">Quốc gia</label>
                    <select id="add-quocgia" name="quocgia">
                        <option value="Vietnam">Vietnam</option>
                    </select>
                </div>
                <div class="form-group half-width">
                    <label for="add-tinhthanh">Tỉnh thành</label>
                    <select id="add-tinhthanh" name="tinhthanh">
                        <option value="HCM">Hồ Chí Minh</option>
                        <option value="HN">Hà Nội</option>
                    </select>
                </div>
            </div>
            <div class="form-group-checkbox">
                <input type="checkbox" id="add-default" name="macdinh" value="true">
                <label for="add-default">Đặt làm địa chỉ mặc định?</label>
            </div>
            <div class="modal-actions">
                <button type="button" class="btn-secondary modal-close"
                        id="cancel-add-modal">Hủy</button>
                <button type="submit" class="btn-primary">Thêm địa chỉ</button>
            </div>
        </form>
    </div>
</div>
<div class="modal-overlay" id="edit-address-modal" style="display: none;">
    <div class="modal-content">
        <button class="modal-close" id="close-edit-modal">&times;</button>
        <h4>SỬA ĐỊA CHỈ</h4>
        <form action="edit-address" method="POST">
            <input type="hidden" id="edit-id" name="id">

            <div class="form-row">
                <div class="form-group half-width">
                    <label for="edit-ho">Họ</label>
                    <input type="text" id="edit-ho" name="ho" required>
                </div>
                <div class="form-group half-width">
                    <label for="edit-ten">Tên</label>
                    <input type="text" id="edit-ten" name="ten" required>
                </div>
            </div>
            <div class="form-group">
                <label for="edit-sdt">Số điện thoại</label>
                <input type="text" id="edit-sdt" name="sdt" required>
            </div>
            <div class="form-group">
                <label for="edit-congty">Công ty</label>
                <input type="text" id="edit-congty" name="congty">
            </div>
            <div class="form-group">
                <label for="edit-diachi">Địa chỉ</label>
                <input type="text" id="edit-diachi" name="diachi" required>
            </div>
            <div class="form-row">
                <div class="form-group half-width">
                    <label for="edit-quocgia">Quốc gia</label>
                    <select id="edit-quocgia" name="quocgia">
                        <option value="Vietnam">Vietnam</option>
                    </select>
                </div>
                <div class="form-group half-width">
                    <label for="edit-tinhthanh">Tỉnh thành</label>
                    <select id="edit-tinhthanh" name="tinhthanh">
                        <option value="HCM">Hồ Chí Minh</option>
                        <option value="HN">Hà Nội</option>
                    </select>
                </div>
            </div>
            <div class="form-group-checkbox">
                <input type="checkbox" id="edit-default" name="macdinh" value="true">
                <label for="edit-default">Đặt làm địa chỉ mặc định?</label>
            </div>

            <div class="modal-actions">
                <button type="button" class="btn-secondary modal-close"
                        id="cancel-edit-modal">Hủy</button>
                <button type="submit" class="btn-primary">Cập nhật địa chỉ</button>
            </div>
        </form>
    </div>
</div>
<div class="modal-overlay" id="delete-address-modal" style="display: none;">
    <div class="modal-content confirm-modal">
        <button class="modal-close">&times;</button>

        <h4 class="text-danger">XÁC NHẬN XÓA</h4>

        <p>Bạn có chắc chắn muốn xóa địa chỉ này không?</p>
        <p class="text-muted">Hành động này không thể hoàn tác.</p>

        <div class="modal-actions center">
            <button type="button" class="btn-secondary modal-close">Hủy bỏ</button>
            <a href="#" id="confirm-delete-btn" class="btn-danger">Xóa ngay</a>
        </div>
    </div>
</div>
<div class="modal-overlay" id="order-details-modal" style="display: none;">
    <div class="modal-content" style="max-width: 800px;">
        <button class="modal-close" id="close-order-details">&times;</button>
        <h4>CHI TIẾT ĐƠN HÀNG <span id="modal-order-code" style="color: #d32f2f;"></span></h4>

        <div class="order-info-summary" style="margin-bottom: 20px; font-size: 13px;">
            <p><strong>Ngày đặt:</strong> <span id="modal-order-date"></span></p>
            <p><strong>Trạng thái:</strong> <span id="modal-order-status" class="status-badge"></span>
            </p>
            <p><strong>Địa chỉ nhận hàng:</strong> <span id="modal-order-address"></span></p>
        </div>

        <div style="max-height: 400px; overflow-y: auto; border: 1px solid #eee; border-radius: 4px;">
            <table class="table-classic" style="margin-top: 0;">
                <thead>
                <tr>
                    <th>Sản phẩm</th>
                    <th>Đơn giá</th>
                    <th>SL</th>
                    <th>Thành tiền</th>
                </tr>
                </thead>
                <tbody id="modal-order-items-body">
                </tbody>
            </table>
        </div>

        <div style="margin-top: 20px; text-align: right;">
            <p>Tạm tính: <span id="modal-subtotal"></span></p>
            <p>Phí vận chuyển: <span id="modal-shipping"></span></p>
            <p>Giảm giá: <span id="modal-discount" style="color: green;"></span></p>
            <h3 style="color: #d32f2f; margin-top: 10px;">TỔNG CỘNG: <span id="modal-total"></span></h3>
        </div>
    </div>
</div>

<div id="cancel-order-modal" class="modal-overlay" style="display: none;">
    <div class="modal-content" style="max-width: 450px;">
        <button class="modal-close">&times;</button>
        <h2 style="color: #8B0000; margin-bottom: 20px;">Hủy đơn hàng</h2>
        <input type="hidden" id="cancel-order-id" value="">

        <p style="margin-bottom: 15px;">Vui lòng chọn lý do hủy đơn:</p>

        <div class="cancel-reasons"
             style="display: flex; flex-direction: column; gap: 10px; margin-bottom: 15px;">
            <label style="display: flex; align-items: center; gap: 8px; cursor: pointer;">
                <input type="radio" name="cancel-reason" value="Đặt nhầm sản phẩm" checked>
                <span>Đặt nhầm sản phẩm</span>
            </label>
            <label style="display: flex; align-items: center; gap: 8px; cursor: pointer;">
                <input type="radio" name="cancel-reason" value="Đổi ý, không muốn mua nữa">
                <span>Đổi ý, không muốn mua nữa</span>
            </label>
            <label style="display: flex; align-items: center; gap: 8px; cursor: pointer;">
                <input type="radio" name="cancel-reason" value="Muốn thay đổi địa chỉ giao hàng">
                <span>Muốn thay đổi địa chỉ giao hàng</span>
            </label>
            <label style="display: flex; align-items: center; gap: 8px; cursor: pointer;">
                <input type="radio" name="cancel-reason" value="Tìm thấy giá rẻ hơn ở nơi khác">
                <span>Tìm thấy giá rẻ hơn ở nơi khác</span>
            </label>
            <label style="display: flex; align-items: center; gap: 8px; cursor: pointer;">
                <input type="radio" name="cancel-reason" value="other">
                <span>Lý do khác</span>
            </label>
        </div>

        <div id="other-reason-container" style="display: none; margin-bottom: 15px;">
                            <textarea id="other-reason-text" placeholder="Nhập lý do của bạn..."
                                      style="width: 100%; height: 80px; padding: 10px; border: 1px solid #ddd; border-radius: 5px; resize: none;"></textarea>
        </div>

        <div style="display: flex; justify-content: center; justify-content: end;">
            <button id="cancel-modal-close-btn" class="btn-secondary"
                    style="padding: 10px 20px; border: 1px solid #ddd; background: #f5f5f5; border-radius: 5px; cursor: pointer;">Đóng</button>
            <button id="confirm-cancel-btn" class="btn-primary"
                    style="padding: 10px 20px; background: #8B0000; color: white; border: none; border-radius: 5px; cursor: pointer;">Xác nhận hủy</button>
        </div>
    </div>
</div>

<jsp:include page="footer.jsp" />
</body>

</html>