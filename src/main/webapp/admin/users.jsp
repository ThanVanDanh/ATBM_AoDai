<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
        <!DOCTYPE html>
        <html lang="en">

        <head>
            <meta charset="UTF-8">
            <title>Admin - Quản lý tài khoản</title>
            <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/7.0.1/css/all.min.css"
                integrity="sha512-2SwdPD6INVrV/lHTZbO2nodKhrnDdJK9/kg2XD1r9uGqPo1cUbujc+IYdlYdEErWNu69gVcYgdxlmVmzTWnetw=="
                crossorigin="anonymous" referrerpolicy="no-referrer" />
            <link rel="stylesheet" href="../style/admin.css">
            <link rel="stylesheet" href="../style/customers.css">
            <link rel="stylesheet" href="../style/charts.css">
            <script>
                var APP_CTX = '${pageContext.request.contextPath}';
            </script>
            <script src="../scripts/admin.js"></script>
        </head>

        <body>
            <div class="admin-container">
                <jsp:include page="sidebar.jsp" />

                <main class="main-content">
                    <header class="admin-header">
                        <div class="header-actions">
                            <a href="../login.jsp" class="btn-logout"><i class="fas fa-user-circle"></i> Đăng xuất</a>
                        </div>
                    </header>
                    <h1>QUẢN LÝ TÀI KHOẢN</h1>
                    <section class="overview-cards-customer">
                        <div class="stat-card-customer">
                            <div class="card-icon" style="background-color: #e0f7fa;">
                                <i class="fas fa-users" style="color: #00796b;"></i>
                            </div>
                            <div class="card-info">
                                <h4>Tổng số tài khoản</h4>
                                <p>${totalCustomers}</p>
                            </div>
                        </div>
                        <div class="stat-card-customer">
                            <div class="card-icon" style="background-color: #e8f5e9;">
                                <i class="fas fa-user-plus" style="color: #388e3c;"></i>
                            </div>
                            <div class="card-info">
                                <h4>Tài khoản mới trong tuần</h4>
                                <p>${newCustomersThisWeek}</p>
                            </div>
                        </div>
                    </section>
                    <section class="table-container">
                        <div class="table-toolbar">
                            <form action="" method="GET" class="filters">
                                <select name="status" id="filter-status" onchange="this.form.submit()">
                                    <option value="">Tất cả trạng thái</option>
                                    <option value="active" ${statusFilter=='active' ? 'selected' : '' }>Hoạt động
                                    </option>
                                    <option value="banned" ${statusFilter=='banned' ? 'selected' : '' }>Bị khóa</option>
                                    <option value="inactive" ${statusFilter=='inactive' ? 'selected' : '' }>Chưa kích
                                        hoạt</option>

                                </select>
                                <input type="text" name="search" value="${searchKeyword}"
                                    placeholder="Tìm theo tên hoặc email..." class="table-search">
                                <button type="submit" class="btn-secondary"><i class="fas fa-search"></i></button>
                            </form>
                        </div>
                        <table class="table-general customer-table">
                            <thead>
                                <tr>
                                    <th>Tên</th>
                                    <th>Email</th>
                                    <th>Điện thoại</th>
                                    <th>Ngày đăng ký</th>
                                    <th>Địa chỉ</th>
                                    <th>Số đơn hàng</th>
                                    <th>Tài khoản</th>
                                    <th>Trạng thái tài khoản</th>
                                    <th>Hành động</th>
                                </tr>
                            </thead>
                            <tbody>
                            <c:choose>
                                <c:when test="${empty users}">
                                    <tr class="empty-state-row">
                                        <td colspan="9" style="text-align: center; padding: 40px; color: #666;">
                                            Hiện tại không có tài khoản nào.
                                        </td>
                                    </tr>
                                </c:when>
                                <c:otherwise>
                                    <c:forEach var="user" items="${users}">
                                        <tr>
                                            <td>${user.fullName}</td>
                                            <td>${user.email}</td>
                                            <td>${user.phone}</td>
                                            <td>${user.createdAt}</td>
                                            <td>${user.authProvider}</td>
                                            <td>0</td>
                                            <td>
                                                <form method="post" action="update-role" style="margin:0;">
                                                    <input type="hidden" name="userId" value="${user.id}" />
                                                    <select name="role" onchange="this.form.submit()">
                                                        <option value="user" ${user.role eq 'user' ? 'selected' : '' }>User</option>
                                                        <option value="admin" ${user.role eq 'admin' ? 'selected' : '' }>Admin</option>
                                                    </select>
                                                </form>
                                            </td>
                                            <td>
                        <span class="status-badge status-active"
                              style="${user.status eq 'active' ? '' : 'display:none;'}">Hoạt động</span>
                                                <span class="status-badge status-blocked"
                                                      style="${user.status eq 'banned' ? '' : 'display:none;'}">Bị khóa</span>
                                                <span class="status-badge status-inactive"
                                                      style="${user.status eq 'inactive' ? '' : 'display:none;'}">Chưa kích hoạt</span>
                                            </td>
                                            <td class="table-actions">
                                                <button class="btn-action btn-view" title="Xem" data-id="${user.id}"
                                                        data-fullname="${user.fullName}" data-email="${user.email}"
                                                        data-phone="${user.phone}" data-address="${user.authProvider}"
                                                        data-createdat="${user.createdAt}" data-status="${user.status}">
                                                    <i class="fas fa-eye"></i>
                                                </button>
                                                <button class="btn-action btn-block" title="Khóa" data-id="${user.id}"
                                                        data-name="${user.fullName}"
                                                        style="${user.status eq 'banned' ? 'display: none;' : ''}">
                                                    <i class="fas fa-ban"></i>
                                                </button>
                                                <button class="btn-action btn-unlock" title="Mở khóa" data-id="${user.id}"
                                                        data-name="${user.fullName}"
                                                        style="${user.status eq 'banned' ? '' : 'display: none;'}">
                                                    <i class="fas fa-check-circle"></i>
                                                </button>
                                                <button class="btn-action btn-delete" title="Xóa" data-id="${user.id}"
                                                        data-name="${user.fullName}">
                                                    <i class="fas fa-trash-alt"></i>
                                                </button>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </c:otherwise>
                            </c:choose>
                            </tbody>
                        </table>
                        <c:if test="${totalPages > 1}">
                        <div class="pagination">
                            <c:if test="${currentPage > 1}">
                                <a
                                    href="?page=${currentPage - 1}&status=${statusFilter}&search=${searchKeyword}">Trước</a>
                            </c:if>
                            <c:forEach begin="1" end="${totalPages}" var="i">
                                <a href="?page=${i}&status=${statusFilter}&search=${searchKeyword}"
                                    class="${currentPage == i ? 'active' : ''}">${i}</a>
                            </c:forEach>
                            <c:if test="${currentPage < totalPages}">
                                <a
                                    href="?page=${currentPage + 1}&status=${statusFilter}&search=${searchKeyword}">Sau</a>
                            </c:if>
                        </div>
                        </c:if>
                    </section>

                </main>
            </div>
            <div id="customer-modal" class="modal" style="display: none;">
                <div class="modal-content">
                    <span class="close-modal">&times;</span>
                    <h2>Chi tiết tài khoản: <span id="modal-fullName"></span></h2>
                    <div class="modal-body">
                        <div class="modal-tabs">
                            <button class="tab-link active" data-tab="tab-info">Thông tin cá nhân</button>
                            <button class="tab-link" data-tab="tab-history">Lịch sử đơn hàng</button>
                        </div>
                        <div id="tab-info" class="tab-content active">
                            <h3>Thông tin cá nhân</h3>
                            <p><strong>Email:</strong> <span id="modal-email"></span></p>
                            <p><strong>Điện thoại:</strong> <span id="modal-phone"></span></p>
                            <p><strong>Địa chỉ:</strong> <span id="modal-address"></span></p>
                            <p><strong>Ngày đăng ký:</strong> <span id="modal-createdAt"></span></p>
                            <p><strong>Trạng thái tài khoản:</strong> <span id="modal-status"></span></p>
                        </div>
                        <div id="tab-history" class="tab-content">
                            <h3>Lịch sử đơn hàng</h3>
                            <ul id="order-history-list">
                                <!-- Javascript will populate this -->
                            </ul>
                        </div>
                    </div>
                </div>
            </div>
            <div id="delete-confirm-modal" class="modal" style="display: none;">
                <div class="modal-content">
                    <span class="close-modal">&times;</span>
                    <h2>Xác nhận xóa</h2>
                    <p>Bạn có chắc chắn muốn xóa tài khoản <strong
                            id="customer-name-to-delete">${user.fullName}</strong> không?</p>
                    <p>Hành động này không thể hoàn tác.</p>

                    <div class="confirm-actions">
                        <button id="btn-cancel-delete" class="btn-secondary">Hủy bỏ</button>
                        <button id="btn-confirm-delete" class="btn-danger">Xác nhận Xóa</button>
                    </div>
                </div>
            </div>
            <div id="status-confirm-modal" class="modal" style="display: none;">
                <div class="modal-content">
                    <span class="close-modal">&times;</span>

                    <h2 id="modal-status-title">Xác nhận thay đổi</h2>

                    <p>Bạn có chắc chắn muốn <strong id="modal-action-text" style="color: #d9534f;">...</strong>
                        tài khoản của tài khoản <strong id="modal-customer-name">...</strong> không?</p>

                    <div class="confirm-actions">
                        <button id="btn-cancel-status" class="btn-secondary">Hủy bỏ</button>
                        <button id="btn-confirm-status" class="btn-danger">Xác nhận</button>
                    </div>
                </div>
            </div>
            <script src="${pageContext.request.contextPath}/scripts/admin/admin.js"></script>
        </body>

        </html>