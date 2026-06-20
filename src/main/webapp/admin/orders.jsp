<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <title>Admin - Quản lý Đơn hàng</title>
    <link rel="stylesheet" href="../style/admin.css">
    <link rel="stylesheet" href="../style/orders.css">
    <link rel="stylesheet" href="../style/customers.css">
    <link rel="stylesheet"
          href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/7.0.1/css/all.min.css"
          integrity="sha512-2SwdPD6INVrV/lHTZbO2nodKhrnDdJK9/kg2XD1r9uGqPo1cUbujc+IYdlYdEErWNu69gVcYgdxlmVmzTWnetw=="
          crossorigin="anonymous" referrerpolicy="no-referrer" />
    <link rel="stylesheet" href="../style/charts.css">
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
    <script src="../scripts/admin/admin.js"></script>
</head>

<body>
<div class="admin-container">
    <jsp:include page="sidebar.jsp" />

    <main class="main-content">
        <header class="admin-header">
            <div class="header-actions">
                <a href="../login.jsp" class="btn-logout"><i class="fas fa-user-circle"></i> Đăng
                    xuất</a>
            </div>
        </header>
        <h1>Quản lý Đơn hàng</h1>
        <section class="table-container">
            <div class="table-toolbar">
                <form action="" method="GET" class="filters">
                    <select id="filter-status" onchange="location.href='?page=1&status='+this.value">
                        <option value="">Tất cả Trạng thái</option>
                        <option value="chờ xử lý" ${statusFilter == 'Chờ xử lý' ? 'selected' : ''}>Chờ xử lý</option>
                        <option value="đang xử lý" ${statusFilter == 'Đang xử lý' ? 'selected' : ''}>Đang xử lý</option>
                        <option value="đang giao" ${statusFilter == 'Đang giao' ? 'selected' : ''}>Đang vận chuyển</option>
                        <option value="hoàn thành" ${statusFilter == 'Hoàn thành' ? 'selected' : ''}>Hoàn thành</option>
                        <option value="đã hủy" ${statusFilter == 'Đã hủy' ? 'selected' : ''}>Đã hủy</option>
                        <option value="cần xác minh" ${statusFilter == 'Cần xác minh' ? 'selected' : ''}>Cần xác minh</option>
                    </select>
                    <input type="text" name="search"
                           value="${searchKeyword}" placeholder="Tìm theo tên khách..." class="table-search">
                    <button type="submit" class="btn-secondary"><i class="fas fa-search"></i></button>
                </form>
            </div>
            <table class="table-general order-table">
                <thead>
                <tr>
                    <th>Mã ĐH</th>
                    <th>Khách Hàng</th>
                    <th>Sản Phẩm</th>
                    <th>Tổng Tiền</th>
                    <th>Trạng Thái</th>
                    <th>Chữ Ký Số</th>
                    <th>Ngày Đặt</th>
                    <th>Cập Nhật</th>
                    <th>Hành Động</th>
                </tr>
                </thead>
                <tbody>
                <c:choose>
                    <c:when test="${not empty orders}">
                        <c:forEach items="${orders}" var="order">
                            <tr>
                                <td><strong>${order.orderCode}</strong></td>
                                <td>${order.customerFullname}</td>
                                <td class="product-list">
                                    Đơn hàng #${order.id}
                                </td>
                                <td>
                                    <fmt:formatNumber value="${order.totalAmount}"
                                                      pattern="#,###" />₫
                                </td>
                                <td>
                                    <span class="status-badge status-${fn:replace(fn:toLowerCase(order.orderStatus), ' ', '-')}">${order.orderStatus}</span>
                                    <c:if test="${fn:toLowerCase(order.orderStatus) == 'đã hủy' and not empty order.cancelReason}">
                                        <br>
                                        <small class="cancel-reason-text">(${order.cancelReason})</small>
                                    </c:if>
                                </td>
                                <td class="text-center">
                                    <c:choose>
                                        <c:when test="${order.signatureStatus == 'valid'}">
                                            <span class="badge-sig badge-valid">
                                                <i class="fas fa-check-circle"></i> Hợp lệ
                                            </span>
                                        </c:when>
                                        <c:when test="${order.signatureStatus == 'invalid'}">
                                            <span class="badge-sig badge-invalid">
                                                <i class="fas fa-times-circle"></i> Giả mạo
                                            </span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="badge-sig badge-missing">
                                                Chưa ký
                                            </span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <c:choose>
                                        <c:when test="${not empty order.createdAt}">
                                            ${order.formattedCreatedAt}
                                        </c:when>
                                        <c:otherwise>N/A</c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <c:choose>
                                        <c:when test="${not empty order.updatedAt}">
                                            ${order.formattedUpdatedAt}
                                        </c:when>
                                        <c:otherwise>N/A</c:otherwise>
                                    </c:choose>
                                </td>
                                <td class="table-actions">
                                    <button class="btn-action btn-view" title="Xem"
                                            onclick="viewOrder(${order.id})"><i
                                            class="fas fa-eye"></i></button>

                                </td>
                            </tr>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <tr class="empty-state-row">
                            <td colspan="9" class="empty-message"> Hiện tại không có đơn hàng nào.</td>
                        </tr>
                    </c:otherwise>
                </c:choose>
                </tbody>
            </table>
            <c:if test="${totalPages > 1}">
                <div class="pagination">
                    <c:if test="${currentPage > 1}">
                        <a href="?page=${currentPage - 1}&status=${statusFilter}&search=${searchKeyword}">Trước</a>
                    </c:if>
                    <c:forEach begin="1" end="${totalPages}" var="i">
                        <a href="?page=${i}&status=${statusFilter}&search=${searchKeyword}"
                           class="${currentPage == i ? 'active' : ''}">${i}</a>
                    </c:forEach>
                    <c:if test="${currentPage < totalPages}">
                        <a href="?page=${currentPage + 1}&status=${statusFilter}&search=${searchKeyword}">Sau</a>
                    </c:if>
                </div>
            </c:if>
        </section>
    </main>
</div>

<div id="order-modal" class="modal" style="display: none;">
    <div class="modal-content large">
        <span class="close-modal" onclick="closeOrderModal()">&times;</span>
        <h2>Chi tiết Đơn hàng: <span id="modal-order-code">#</span></h2>
        <div class="modal-body">
            <div class="modal-flex">
                <div class="modal-section">
                    <div class="customer-information">
                        <h3>Thông tin khách hàng</h3>
                        <p><strong>Tên:</strong> <span id="modal-customer-name">-</span></p>
                        <p><strong>Email:</strong> <span id="modal-customer-email">-</span></p>
                        <p><strong>SĐT:</strong> <span id="modal-customer-phone">-</span></p>
                        <p><strong>Địa chỉ:</strong> <span id="modal-shipping-address">-</span></p>
                        <p><strong>Ghi chú:</strong> <span id="modal-customer-note">-</span></p>
                    </div>

                    <div class="signature-information">
                        <h3><i class="fas fa-shield-alt"></i> Thông tin Chữ ký số</h3>
                        <p class="mb-8"><strong>Trạng thái xác minh:</strong> <span id="modal-signature-status">-</span></p>
                        <p class="mb-8"><strong>Thời gian ký:</strong> <span id="modal-signed-at">-</span></p>
                        <p class="mb-4"><strong>Dữ liệu ký (Payload):</strong></p>
                        <textarea id="modal-signed-data" class="signature-textarea mb-8" readonly></textarea>
                        <p class="mb-4"><strong>Chữ ký (Base64):</strong></p>
                        <textarea id="modal-signature" class="signature-textarea break-all" readonly></textarea>
                    </div>

                    <div class="update-status-container">
                        <h3>Cập nhật Trạng thái</h3>
                        <input type="hidden" id="modal-order-id" value="">
                        <div class="status-update-form">
                            <select id="modal-status-select" class="update-status-select">
                                <option value="Chờ xử lý">Chờ xử lý</option>
                                <option value="Đang xử lý">Đang xử lý</option>
                                <option value="Đang giao">Đang vận chuyển</option>
                                <option value="Hoàn thành">Hoàn thành</option>
                                <option value="Đã hủy">Đã hủy</option>
                                <option value="Cần xác minh">Cần xác minh</option>
                            </select>
                            <button class="btn-primary" onclick="updateOrderStatus()">Cập nhật</button>
                        </div>
                        <div class="modal-actions">
                            <button class="btn-secondary"><i class="fas fa-print"></i> In Hóa Đơn</button>
                        </div>
                    </div>
                </div>

                <div class="modal-section order-products-section">
                    <div class="order-products-box">
                        <h3>Sản phẩm trong Đơn hàng</h3>
                        <table class="order-detail-table">
                            <colgroup>
                                <col class="col-product">
                                <col class="col-quantity">
                                <col class="col-price">
                                <col class="col-total">
                            </colgroup>
                            <thead>
                            <tr>
                                <th>Sản Phẩm</th>
                                <th>SL</th>
                                <th>Giá</th>
                                <th>Tổng</th>
                            </tr>
                            </thead>
                            <tbody id="modal-order-items">
                            </tbody>
                            <tfoot>
                            <tr>
                                <td colspan="3" class="text-center"><strong>Tổng tiền hàng</strong></td>
                                <td id="modal-subtotal">0₫</td>
                            </tr>
                            <tr>
                                <td colspan="3" class="text-center"><strong>Phí vận chuyển</strong></td>
                                <td id="modal-shipping">0₫</td>
                            </tr>
                            <tr id="modal-voucher-row">
                                <td colspan="3" class="text-center"><strong>Voucher giảm giá</strong></td>
                                <td id="modal-discount">0₫</td>
                            </tr>
                            <tr>
                                <td colspan="3" class="text-center"><strong>Thành tiền</strong></td>
                                <td><strong id="modal-total">0₫</strong></td>
                            </tr>
                            </tfoot>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<div id="cancel-reason-modal" class="modal" style="display: none;">
    <div class="modal-content cancel-modal-content">
        <span class="close-cancel-modal close-modal">&times;</span>
        <h2 class="cancel-modal-title">Lý do hủy đơn hàng</h2>
        <p class="cancel-modal-desc">Vui lòng chọn lý do hủy đơn:</p>
        <div class="cancel-reasons">
            <label class="radio-label">
                <input type="radio" name="admin-cancel-reason" value="Khách hàng yêu cầu hủy" checked>
                <span>Khách hàng yêu cầu hủy</span>
            </label>
            <label class="radio-label">
                <input type="radio" name="admin-cancel-reason" value="Hết hàng">
                <span>Hết hàng</span>
            </label>
            <label class="radio-label">
                <input type="radio" name="admin-cancel-reason" value="Không liên lạc được với khách">
                <span>Không liên lạc được với khách</span>
            </label>
            <label class="radio-label">
                <input type="radio" name="admin-cancel-reason" value="Sai thông tin đơn hàng">
                <span>Sai thông tin đơn hàng</span>
            </label>
            <label class="radio-label">
                <input type="radio" name="admin-cancel-reason" value="other">
                <span>Lý do khác</span>
            </label>
        </div>
        <div id="admin-other-reason-container" class="other-reason-container" style="display: none;">
            <textarea id="admin-other-reason-text" placeholder="Nhập lý do..." class="other-reason-textarea"></textarea>
        </div>
        <div class="confirm-actions flex-end">
            <button id="btn-cancel-reason-close" class="btn-secondary">Đóng</button>
            <button id="btn-confirm-cancel-reason" class="btn-danger">Xác nhận hủy</button>
        </div>
    </div>
</div>

<script src="${pageContext.request.contextPath}/scripts/admin/admin.js"></script>
<script>
    function viewOrder(orderId) {
        fetch('${pageContext.request.contextPath}/admin/order-details?orderId=' + orderId)
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    const order = data.order;
                    const items = data.items;

                    document.getElementById('modal-order-id').value = order.id;
                    document.getElementById('modal-order-code').textContent = order.orderCode;
                    document.getElementById('modal-customer-name').textContent = order.customerFullname || '-';
                    document.getElementById('modal-customer-email').textContent = order.customerEmail || '-';
                    document.getElementById('modal-customer-phone').textContent = order.customerPhone || '-';
                    document.getElementById('modal-shipping-address').textContent = order.shippingAddress || '-';
                    document.getElementById('modal-customer-note').textContent = order.customerNote || 'Không có';

                    const sigStatusSpan = document.getElementById('modal-signature-status');
                    if (order.signatureStatus === 'valid') {
                        sigStatusSpan.innerHTML = '<span class="sig-text-valid"><i class="fas fa-check-circle"></i> Hợp lệ (Toàn vẹn dữ liệu)</span>';
                    } else if (order.signatureStatus === 'invalid') {
                        sigStatusSpan.innerHTML = '<span class="sig-text-invalid"><i class="fas fa-times-circle"></i> Giả mạo / Bị thay đổi (Cảnh báo)</span>';
                    } else {
                        sigStatusSpan.innerHTML = '<span class="sig-text-missing">Chưa ký chữ ký số</span>';
                    }

                    document.getElementById('modal-signed-data').value = order.signedOrderData || 'Không có dữ liệu';
                    document.getElementById('modal-signature').value = order.orderSignature || 'Không có chữ ký';
                    document.getElementById('modal-signed-at').textContent = order.signedAt || '-';

                    const statusSelect = document.getElementById('modal-status-select');
                    const updateBtn = document.querySelector('.status-update-form .btn-primary');
                    
                    statusSelect.disabled = false;
                    updateBtn.disabled = false;
                    updateBtn.style.opacity = '1';

                    for (let option of statusSelect.options) {
                        option.disabled = false;
                        if (option.value === order.orderStatus) {
                            option.selected = true;
                        }
                    }

                    if (order.signatureStatus === 'invalid') {
                        for (let option of statusSelect.options) {
                            if (option.value !== 'Đã hủy' && option.value !== 'Cần xác minh') {
                                option.disabled = true;
                            }
                        }
                        if (order.orderStatus === 'Đã hủy') {
                            updateBtn.disabled = true;
                            updateBtn.style.opacity = '0.5';
                            statusSelect.disabled = true;
                        }
                    } else if (order.signatureStatus === 'unsigned') {
                        for (let option of statusSelect.options) {
                            if (option.value !== 'Đã hủy' && option.value !== 'Đang chờ xác thực') {
                                option.disabled = true;
                            }
                        }
                        if (order.orderStatus === 'Đã hủy') {
                            updateBtn.disabled = true;
                            updateBtn.style.opacity = '0.5';
                            statusSelect.disabled = true;
                        }
                    } else {
                        if (order.orderStatus === 'Đã hủy' || order.orderStatus === 'Hoàn thành') {
                            updateBtn.disabled = true;
                            updateBtn.style.opacity = '0.5';
                            statusSelect.disabled = true;
                        }
                    }

                    const tbody = document.getElementById('modal-order-items');
                    tbody.innerHTML = '';
                    const fmt = new Intl.NumberFormat('vi-VN');

                    items.forEach(item => {
                        const tr = document.createElement('tr');
                        const name = item.productName || ('Sản phẩm #' + item.variantId);
                        const size = item.size || 'N/A';
                        const price = fmt.format(item.priceAtPurchase);
                        const total = fmt.format(item.priceAtPurchase * item.quantity);
                        tr.innerHTML = '<td>' + name + ' (' + size + ')</td>' +
                            '<td>' + item.quantity + '</td>' +
                            '<td>' + price + '₫</td>' +
                            '<td>' + total + '₫</td>';
                        tbody.appendChild(tr);
                    });

                    document.getElementById('modal-subtotal').textContent = fmt.format(order.subtotalAmount || 0) + '₫';
                    document.getElementById('modal-shipping').textContent = order.shippingFee > 0 ? fmt.format(order.shippingFee) + '₫' : 'Miễn phí';

                    if (order.discountAmount > 0) {
                        document.getElementById('modal-discount').textContent = '-' + fmt.format(order.discountAmount) + '₫';
                    } else {
                        document.getElementById('modal-discount').textContent = '0₫';
                    }

                    document.getElementById('modal-total').textContent = fmt.format(order.totalAmount) + '₫';

                    document.getElementById('order-modal').style.display = 'flex';
                } else {
                    alert(data.message || 'Lỗi tải thông tin đơn hàng');
                }
            })
            .catch(err => {
                console.error(err);
                alert('Lỗi kết nối server');
            });
    }

    function closeOrderModal() {
        document.getElementById('order-modal').style.display = 'none';
    }

    function updateOrderStatus() {
        const orderId = document.getElementById('modal-order-id').value;
        const newStatus = document.getElementById('modal-status-select').value;

        if (!orderId) {
            alert('Lỗi: Không có mã đơn hàng');
            return;
        }

        const normalizedStatus = newStatus.trim().toLocaleLowerCase('vi-VN');

        if (normalizedStatus === 'đã hủy' || normalizedStatus === 'đã huỷ') {
            showCancelReasonModal(orderId, newStatus);
            return;
        }

        sendStatusUpdate(orderId, newStatus, '');
    }

    function showCancelReasonModal(orderId, status) {
        window.pendingCancelOrderId = orderId;
        window.pendingCancelStatus = status;
        document.getElementById('cancel-reason-modal').style.display = 'flex';

        const firstRadio = document.querySelector('input[name="admin-cancel-reason"]');
        if (firstRadio) firstRadio.checked = true;
        document.getElementById('admin-other-reason-container').style.display = 'none';
        document.getElementById('admin-other-reason-text').value = '';
    }

    function sendStatusUpdate(orderId, status, cancelReason) {
        let bodyStr = 'orderId=' + encodeURIComponent(orderId)
            + '&status=' + encodeURIComponent(status);
        if (cancelReason) {
            bodyStr += '&cancelReason=' + encodeURIComponent(cancelReason);
        }

        fetch('${pageContext.request.contextPath}/admin/update-order-status', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: bodyStr
        })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    alert('Cập nhật trạng thái thành công!');
                    closeOrderModal();
                    location.reload();
                } else {
                    alert(data.message || 'Lỗi cập nhật trạng thái');
                }
            })
            .catch(err => {
                console.error(err);
                alert('Lỗi kết nối server');
            });
    }

    document.addEventListener('DOMContentLoaded', function () {
        const cancelReasonModal = document.getElementById('cancel-reason-modal');
        if (!cancelReasonModal) return;

        document.querySelectorAll('input[name="admin-cancel-reason"]').forEach(radio => {
            radio.addEventListener('change', function () {
                document.getElementById('admin-other-reason-container').style.display =
                    this.value === 'other' ? 'block' : 'none';
            });
        });

        function closeCancelReasonModal() {
            cancelReasonModal.style.display = 'none';
        }

        document.getElementById('btn-cancel-reason-close').addEventListener('click', closeCancelReasonModal);
        document.querySelector('.close-cancel-modal').addEventListener('click', closeCancelReasonModal);

        document.getElementById('btn-confirm-cancel-reason').addEventListener('click', function () {
            const selectedReason = document.querySelector('input[name="admin-cancel-reason"]:checked');

            if (!selectedReason) {
                alert('Vui lòng chọn lý do hủy đơn!');
                return;
            }

            let cancelReason = selectedReason.value;

            if (cancelReason === 'other') {
                cancelReason = document.getElementById('admin-other-reason-text').value.trim();
                if (!cancelReason) {
                    alert('Vui lòng nhập lý do hủy đơn!');
                    return;
                }
            }

            closeCancelReasonModal();
            sendStatusUpdate(window.pendingCancelOrderId, window.pendingCancelStatus, cancelReason);
        });
    });


</script>
</body>
</html>