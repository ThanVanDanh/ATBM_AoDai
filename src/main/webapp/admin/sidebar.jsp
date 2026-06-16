<%-- Created by IntelliJ IDEA. User: laiqua Date: 23/1/26 Time: 23:00 To change this template use File | Settings | File
    Templates. --%>
    <%@ page contentType="text/html;charset=UTF-8" language="java" %>
        <div class="sidebar">
            <div class="sidebar-header">
                <a href="${pageContext.request.contextPath}/admin/dashboard.jsp">
                    <img src="${pageContext.request.contextPath}/image/logo.png" alt="Logo Việt Sắc Đỏ">
                </a>
                <h2>Trang Admin</h2>
            </div>
            <nav class="sidebar-nav">
                <ul>
                    <li class="nav-item active"><a href="${pageContext.request.contextPath}/admin/dashboard"><i
                                class="fas fa-tachometer-alt"></i> Tổng quan</a></li>
                    <li class="nav-item"><a href="${pageContext.request.contextPath}/admin/product.jsp"><i
                                class="fas fa-box-open"></i> Quản lý Sản phẩm</a></li>
                    <li class="nav-item"><a href="${pageContext.request.contextPath}/admin/home"><i
                                class="fas fa-house"></i> Quản lý Trang chủ</a></li>
                    <li class="nav-item"><a href="${pageContext.request.contextPath}/admin/orders"><i
                                class="fas fa-shopping-cart"></i> Quản lý Đơn hàng</a></li>
                    <li class="nav-item"><a href="${pageContext.request.contextPath}/admin/users"><i
                                class="fas fa-users"></i> Quản lý Tài khoản</a></li>
                    <li class="nav-item"><a href="${pageContext.request.contextPath}/admin/contact-list"><i
                                class="fa-regular fa-address-book"></i> Quản lý Liên hệ</a></li>
                    <li class="nav-item"><a href="${pageContext.request.contextPath}/admin/promotions.jsp"><i
                                class="fas fa-tags"></i> Khuyến mãi</a></li>
                    <li class="nav-item">
                        <a href="${pageContext.request.contextPath}/home"><i class="fas fa-sign-out-alt"></i> Trở
                            về Trang Chủ</a>
                    </li>
                </ul>
            </nav>
        </div>