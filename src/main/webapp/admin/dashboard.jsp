<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
            <%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
                <!DOCTYPE html>
                <html lang="en">

                <head>
                    <meta charset="UTF-8">
                    <title>Admin Dashboard</title>
                    <link rel="stylesheet" href="${pageContext.request.contextPath}/style/admin.css">
                    <link rel="stylesheet"
                        href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/7.0.1/css/all.min.css"
                        integrity="sha512-2SwdPD6INVrV/lHTZbO2nodKhrnDdJK9/kg2XD1r9uGqPo1cUbujc+IYdlYdEErWNu69gVcYgdxlmVmzTWnetw=="
                        crossorigin="anonymous" referrerpolicy="no-referrer" />
                    <link rel="stylesheet" href="${pageContext.request.contextPath}/style/dashboard.css">
                </head>

                <body>
                    <div class="admin-container">
                        <jsp:include page="sidebar.jsp" />
                        <main class="main-content">
                            <header class="admin-header">
                                <div class="header-actions">
                                    <a href="${pageContext.request.contextPath}/logout" class="btn-logout"><i
                                            class="fas fa-user-circle"></i> Đăng xuất</a>
                                </div>
                            </header>
                            <section class="dashboard-stats">
                                <div class="stat-card">
                                    <div class="card-icon" style="background-color: #e8f5e9;">
                                        <i class="fas fa-dollar-sign" style="color: #388e3c;"></i>
                                    </div>
                                    <div class="card-info">
                                        <h4>Doanh Thu</h4>
                                        <p>
                                            <fmt:formatNumber value="${totalRevenue}" pattern="#,###"/>₫
                                        </p>
                                    </div>
                                </div>
                                <div class="stat-card">
                                    <div class="card-icon" style="background-color: #e0f7fa;">
                                        <i class="fas fa-shopping-cart" style="color: #00796b;"></i>
                                    </div>
                                    <div class="card-info">
                                        <h4>Tổng đơn hàng</h4>
                                        <p>${totalOrders}</p>
                                    </div>
                                </div>
                                <div class="stat-card">
                                    <div class="card-icon" style="background-color: #fff3e0;">
                                        <i class="fas fa-users" style="color: #f57c00;"></i>
                                    </div>
                                    <div class="card-info">
                                        <h4>Khách Mới (Tuần)</h4>
                                        <p>${newCustomersWeek}</p>
                                    </div>
                                </div>
                                <div class="stat-card">
                                    <div class="card-icon" style="background-color: #fce4ec;">
                                        <i class="fas fa-box-open" style="color: #c2185b;"></i>
                                    </div>
                                    <div class="card-info">
                                        <h4>Sản Phẩm Bán Chạy</h4>
                                        <p title="${bestSellingProduct.nameProduct}">
                                            <c:choose>
                                                <c:when test="${not empty bestSellingProduct}">
                                                    ${bestSellingProduct.nameProduct}
                                                </c:when>
                                                <c:otherwise>Chưa có dữ liệu</c:otherwise>
                                            </c:choose>
                                        </p>
                                    </div>
                                </div>
                            </section>
                            <section class="dashboard-recent-activity">
                                <div class="recent-panel">
                                    <h3><i class="fas fa-receipt"></i> Đơn Hàng Gần Đây</h3>
                                    <table class="recent-table">
                                        <thead>
                                            <tr>
                                                <th>Mã ĐH</th>
                                                <th>Khách Hàng</th>
                                                <th>Tổng Tiền</th>
                                                <th>Trạng Thái</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <c:forEach var="order" items="${recentOrders}">
                                                <tr>
                                                    <td><strong>#${order.id}</strong></td>
                                                    <td>${order.customerFullname}</td>
                                                    <td>
                                                        <fmt:formatNumber value="${order.totalAmount}" pattern="#,###"/>₫
                                                    </td>
                                                    <td>
                                                        <span
                                                            class="status-badge status-${fn:replace(fn:toLowerCase(order.orderStatus), ' ', '-')}">${order.orderStatus}</span>
                                                    </td>
                                                </tr>
                                            </c:forEach>
                                        </tbody>
                                    </table>
                                </div>
                                <div class="recent-panel">
                                    <h3><i class="fas fa-user-plus"></i> Khách Hàng Mới</h3>
                                    <table class="recent-table">
                                        <thead>
                                            <tr>
                                                <th>Tên</th>
                                                <th>Email</th>
                                                <th>Ngày ĐK</th>
                                                <th>Trạng Thái</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <c:forEach var="user" items="${newCustomers}">
                                                <tr>
                                                    <td>${user.fullName}</td>
                                                    <td>${fn:substring(user.email, 0, 15)}...</td>
                                                    <td>
                                                        ${user.formattedCreatedDate}
                                                    </td>
                                                    <td>
                                                        <span
                                                            class="status-badge status-${fn:replace(fn:toLowerCase(user.status), ' ', '-')}">
                                                            ${user.status == 'active' ? 'Hoạt động' : 'Bị khóa'}
                                                        </span>
                                                    </td>
                                                </tr>
                                            </c:forEach>
                                        </tbody>
                                    </table>
                                </div>

                                <div class="recent-panel">
                                    <h3><i class="fas fa-calendar-alt"></i> Doanh Thu Theo Tháng (6 tháng)</h3>
                                    <table class="recent-table">
                                        <thead>
                                            <tr>
                                                <th>Tháng/Năm</th>
                                                <th>Số Đơn Hàng</th>
                                                <th>Doanh Thu</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <c:forEach var="stat" items="${monthlyRevenue}">
                                                <tr>
                                                    <td><strong>${stat.monthYear}</strong></td>
                                                    <td>${stat.orderCount}</td>
                                                    <td>
                                                        <fmt:formatNumber value="${stat.revenue}" pattern="#,###"/>₫
                                                    </td>
                                                </tr>
                                            </c:forEach>
                                        </tbody>
                                    </table>
                                </div>

                                <div class="recent-panel">
                                    <h3><i class="fas fa-tags"></i> Khuyến Mãi Đang Chạy</h3>
                                    <table class="recent-table">
                                        <thead>
                                            <tr>
                                                <th>Mã Voucher</th>
                                                <th>Loại</th>
                                                <th>Ngày Hết Hạn</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <c:forEach var="voucher" items="${activeVouchers}">
                                                <tr>
                                                    <td><strong>${voucher.voucherCode}</strong></td>
                                                    <td>
                                                        <c:choose>
                                                            <c:when test="${voucher.discountType == 'percent'}">
                                                                Giảm ${voucher.discountValue}%
                                                            </c:when>
                                                            <c:when test="${voucher.discountType == 'fixed'}">
                                                                Giảm
                                                                <fmt:formatNumber value="${voucher.discountValue}"
                                                                                  pattern="#,###"/>₫
                                                            </c:when>
                                                            <c:when test="${voucher.discountType == 'shipping'}">
                                                                Miễn phí vận chuyển
                                                            </c:when>
                                                            <c:otherwise>
                                                                ${voucher.discountType}
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </td>
                                                    <td><span class="status-badge status-processing">
                                                            ${voucher.formattedValidToDate}
                                                        </span></td>
                                                </tr>
                                            </c:forEach>
                                            <c:if test="${empty activeVouchers}">
                                                <tr>
                                                    <td colspan="3" style="text-align: center;">Không có khuyến mãi nào
                                                        đang chạy.</td>
                                                </tr>
                                            </c:if>
                                        </tbody>
                                    </table>
                                </div>
                            </section>
                        </main>
                    </div>
                    <script src="${pageContext.request.contextPath}/scripts/admin/admin.js"></script>
                </body>

                </html>