<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<header>
    <div class="header">
        <div class="logo">
            <a href="${pageContext.request.contextPath}/home">
                <img src="${pageContext.request.contextPath}/image/logo.png" alt="Logo Việt Sắc Đỏ">
            </a>
        </div>
        <nav>
            <ul class="menu">
                <%--                <li><a href="${pageContext.request.contextPath}/home">Trang Chủ</a></li>--%>
                <c:set var="ctx" value="${pageContext.request.contextPath}"/>
                <c:set var="menuAll" value="${requestScope.menuAllCategories}"/>

                <c:forEach var="cat" items="${menuAll}">
                    <c:if test="${cat.parentId == 0 || cat.parentId == null}">
                        <c:set var="children" value="${null}"/>
                        <c:forEach var="tmp" items="${menuAll}">
                            <c:if test="${tmp.parentId == cat.id}">
                                <c:set var="children" value="${menuAll}"/>
                            </c:if>
                        </c:forEach>

                        <c:set var="hasChildren" value="${false}"/>
                        <c:forEach var="tmp2" items="${menuAll}">
                            <c:if test="${tmp2.parentId == cat.id}">
                                <c:set var="hasChildren" value="${true}"/>
                            </c:if>
                        </c:forEach>

                        <c:choose>
                            <c:when test="${hasChildren}">

                                <c:set var="hasGrandChild" value="${false}"/>
                                <c:forEach var="child" items="${menuAll}">
                                    <c:if test="${child.parentId == cat.id}">
                                        <c:forEach var="grand" items="${menuAll}">
                                            <c:if test="${grand.parentId == child.id}">
                                                <c:set var="hasGrandChild" value="${true}"/>
                                            </c:if>
                                        </c:forEach>
                                    </c:if>
                                </c:forEach>

                                <c:choose>
                                    <c:when test="${hasGrandChild}">
                                        <li class="has-megamenu">
                                            <c:choose>
                                                <c:when test="${empty cat.slug}">
                                                    <a href="#">${cat.nameCategory}<i class="fa-solid fa-chevron-down"></i></a>
                                                </c:when>
                                                <c:otherwise>
                                                    <a href="${ctx}/danh-muc/${cat.slug}">${cat.nameCategory}<i class="fa-solid fa-chevron-down"></i></a>
                                                </c:otherwise>
                                            </c:choose>

                                            <ul class="sub-menu">
                                                <div class="mega-menu-container">
                                                    <div class="mega-menu-content">
                                                        <c:forEach var="col" items="${menuAll}">
                                                            <c:if test="${col.parentId == cat.id}">
                                                                <div class="category-column">
                                                                    <h3>${col.nameCategory}</h3>
                                                                    <ul>
                                                                        <c:set var="hasGrand" value="${false}"/>
                                                                        <c:forEach var="g" items="${menuAll}">
                                                                            <c:if test="${g.parentId == col.id}">
                                                                                <c:set var="hasGrand" value="${true}"/>
                                                                            </c:if>
                                                                        </c:forEach>

                                                                        <c:choose>
                                                                            <c:when test="${hasGrand}">
                                                                                <c:forEach var="g" items="${menuAll}">
                                                                                    <c:if test="${g.parentId == col.id}">
                                                                                        <c:choose>
                                                                                            <c:when test="${empty g.slug}">
                                                                                                <li><a href="#">${g.nameCategory}</a></li>
                                                                                            </c:when>
                                                                                            <c:otherwise>
                                                                                                <li><a href="${ctx}/danh-muc/${g.slug}">${g.nameCategory}</a></li>
                                                                                            </c:otherwise>
                                                                                        </c:choose>
                                                                                    </c:if>
                                                                                </c:forEach>
                                                                            </c:when>
                                                                            <c:otherwise>
                                                                                <c:choose>
                                                                                    <c:when test="${empty col.slug}">
                                                                                        <li><a href="#">${col.nameCategory}</a></li>
                                                                                    </c:when>
                                                                                    <c:otherwise>
                                                                                        <li><a href="${ctx}/danh-muc/${col.slug}">${col.nameCategory}</a></li>
                                                                                    </c:otherwise>
                                                                                </c:choose>
                                                                            </c:otherwise>
                                                                        </c:choose>
                                                                    </ul>
                                                                </div>
                                                            </c:if>
                                                        </c:forEach>
                                                    </div>
                                                </div>
                                            </ul>
                                        </li>
                                    </c:when>
                                    <c:otherwise>
                                        <li>
                                            <c:choose>
                                                <c:when test="${empty cat.slug}">
                                                    <a href="#">${cat.nameCategory}<i class="fa-solid fa-chevron-down"></i></a>
                                                </c:when>
                                                <c:otherwise>
                                                    <a href="${ctx}/danh-muc/${cat.slug}">${cat.nameCategory}<i class="fa-solid fa-chevron-down"></i></a>
                                                </c:otherwise>
                                            </c:choose>
                                            <ul class="sub-menu">
                                                <c:forEach var="child" items="${menuAll}">
                                                    <c:if test="${child.parentId == cat.id}">
                                                        <c:choose>
                                                            <c:when test="${empty child.slug}">
                                                                <li><a href="#">${child.nameCategory}</a></li>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <li><a href="${ctx}/danh-muc/${child.slug}">${child.nameCategory}</a></li>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </c:if>
                                                </c:forEach>
                                            </ul>
                                        </li>
                                    </c:otherwise>
                                </c:choose>

                            </c:when>

                            <c:otherwise>
                                <li>
                                    <c:choose>
                                        <c:when test="${empty cat.slug}">
                                            <a href="#">${cat.nameCategory}</a>
                                        </c:when>
                                        <c:otherwise>
                                            <a href="${ctx}/danh-muc/${cat.slug}">${cat.nameCategory}</a>
                                        </c:otherwise>
                                    </c:choose>
                                </li>
                            </c:otherwise>
                        </c:choose>
                    </c:if>
                </c:forEach>

                <li><a href="${pageContext.request.contextPath}/contact_us">Liên Hệ</a></li>
                <li><a href="${pageContext.request.contextPath}/promotion">Chương trình khuyến mãi</a></li>
            </ul>
        </nav>
        <div class="icons">

            <a href="#" id="searchTrigger"><i class="fa-solid fa-magnifying-glass"></i></a>

            <div class="user-menu">
                <a><i class="fa-regular fa-user"></i></a>
                <ul class="user">
                    <% if (session.getAttribute("account") !=null) { %>
                    <% model.user.User u=(model.user.User) session.getAttribute("account"); if
                    ("admin".equals(u.getRole())) { %>
                    <li><a href="${pageContext.request.contextPath}/admin/dashboard.jsp">Quản trị</a>
                    </li>
                    <% } %>
                    <li><a href="${pageContext.request.contextPath}/account">Tài khoản</a>
                    </li>
                    <li><a href="${pageContext.request.contextPath}/Logout">Đăng xuất</a>
                    </li>
                    <% } else { %>
                    <li><a href="${pageContext.request.contextPath}/login.jsp">Đăng
                        nhập</a></li>
                    <li><a href="${pageContext.request.contextPath}/signup.jsp">Đăng
                        ký</a></li>
                    <% } %>
                </ul>
            </div>

            <div class="mini-cart-menu">
                <a href="${pageContext.request.contextPath}/cart" title="Giỏ hàng">
                    <i class="fa-solid fa-cart-shopping"></i>
                    <span class="mini-count_item count_item_pr">
                        ${sessionScope.cart == null ? 0 : sessionScope.cart.totalQuantity}
                    </span>
                </a>

                <div class="mini-cart-content">
                    <div class="mini-empty-cart js-mini-cart-empty"
                         style="display: ${sessionScope.cart == null || sessionScope.cart.totalQuantity == 0 ? 'block' : 'none'}; text-align: center; padding: 20px;">
                        <p>Chưa có sản phẩm trong giỏ hàng</p>
                    </div>

                    <div class="js-mini-cart-has-item"
                         style="display: ${sessionScope.cart != null && sessionScope.cart.totalQuantity > 0 ? 'block' : 'none'};">

                        <ul class="mini-cart-items-list js-mini-cart-list">
                            <c:if test="${sessionScope.cart != null}">
                                <c:forEach var="item" items="${sessionScope.cart.items}">
                                    <li class="item-cart-row">
                                        <div class="img-container">
                                            <img src="${not empty item.product.images ? item.product.images[0].imageUrl : pageContext.request.contextPath.concat('/image/no-image.png')}"
                                                 alt="${item.product.nameProduct}">
                                        </div>

                                        <div class="mini-item-info">
                                            <a href="${pageContext.request.contextPath}/product-detail?id=${item.product.id}" class="mini-item-name">
                                                    ${item.product.nameProduct}
                                            </a>
                                            <div class="mini-item-meta" style="font-size: 12px; color: #666; margin-bottom: 5px;">
                                                <c:if test="${not empty item.size}">Size: <strong>${item.size}</strong></c:if>
                                                <c:if test="${not empty item.sku}"> / Mã: ${item.sku}</c:if>
                                            </div>
                                            <span class="mini-item-price">
                                                <fmt:formatNumber value="${item.price}" pattern="#,###"/>₫
                                                                    </span>
                                            <span class="mini-quantity">x${item.quantity}</span>
                                        </div>
                                        <a href="${pageContext.request.contextPath}/cart?action=remove&id=${item.product.id}&sku=${item.sku}"
                                           class="remove-item" onclick="return confirm('Bạn có chắc muốn xóa sản phẩm này?')">
                                            <i class="fa-solid fa-xmark"></i>
                                        </a>
                                    </li>
                                </c:forEach>
                            </c:if>
                        </ul>

                        <div class="mini-cart-footer">
                            <div class="mini-cart-total">
                                                        <span>Tổng tiền tạm tính:
                                                            <strong class="mini-total-price js-mini-total-price">
                                        <fmt:formatNumber value="${sessionScope.cart.totalPrice}" pattern="#,###"/>₫
                                                            </strong>
                                                        </span>
                            </div>
                            <a href="${pageContext.request.contextPath}/cart" class="btn-pay">Tiến hành thanh toán</a>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</header>

<div class="search-overlay-container" id="searchOverlay">
    <div class="search-overlay-header">
        <div class="logo">
            <a href="${pageContext.request.contextPath}/index.jsp">
                <img src="${pageContext.request.contextPath}/image/logo.png" alt="Logo Việt Sắc Đỏ">
            </a>
        </div>

        <form class="search-overlay-form" action="${pageContext.request.contextPath}/list-product" method="get">
            <input type="text" name="search" id="searchInput" placeholder="áo dài truyền thống, quần áo dài, vòng tay..." required>
            <button type="submit"><i class="fa-solid fa-magnifying-glass"></i></button>
        </form>

        <div class="icons">
            <div class="user-menu">
                <a><i class="fa-regular fa-user"></i></a>
                <ul class="user">
                    <% if (session.getAttribute("account") !=null) { %>
                    <% model.user.User u=(model.user.User) session.getAttribute("account"); if
                    ("admin".equals(u.getRole())) { %>
                    <li><a href="${pageContext.request.contextPath}/admin/dashboard.jsp">Quản trị</a>
                    </li>
                    <% } %>
                    <li><a href="${pageContext.request.contextPath}/account">Tài khoản</a>
                    </li>
                    <li><a href="${pageContext.request.contextPath}/Logout">Đăng xuất</a>
                    </li>
                    <% } else { %>
                    <li><a href="${pageContext.request.contextPath}/login.jsp">Đăng
                        nhập</a></li>
                    <li><a href="${pageContext.request.contextPath}/signup.jsp">Đăng
                        ký</a></li>
                    <% } %>
                </ul>
            </div>

            <div class="mini-cart-menu">
                <a href="${pageContext.request.contextPath}/cart" title="Giỏ hàng">
                    <i class="fa-solid fa-cart-shopping"></i>
                    <span class="mini-count_item count_item_pr">
                        ${sessionScope.cart == null ? 0 : sessionScope.cart.totalQuantity}
                    </span>
                </a>

                <div class="mini-cart-content">
                    <div class="mini-empty-cart js-mini-cart-empty"
                         style="display: ${sessionScope.cart == null || sessionScope.cart.totalQuantity == 0 ? 'block' : 'none'}; text-align: center; padding: 20px;">
                        <p>Chưa có sản phẩm trong giỏ hàng</p>
                    </div>

                    <div class="js-mini-cart-has-item"
                         style="display: ${sessionScope.cart != null && sessionScope.cart.totalQuantity > 0 ? 'block' : 'none'};">

                        <ul class="mini-cart-items-list js-mini-cart-list">
                            <c:if test="${sessionScope.cart != null}">
                                <c:forEach var="item" items="${sessionScope.cart.items}">
                                    <li class="item-cart-row">
                                        <div class="img-container">
                                            <img src="${not empty item.product.images ? item.product.images[0].imageUrl : pageContext.request.contextPath.concat('/image/no-image.png')}"
                                                 alt="${item.product.nameProduct}">
                                        </div>

                                        <div class="mini-item-info">
                                            <a href="${pageContext.request.contextPath}/product-detail?id=${item.product.id}" class="mini-item-name">
                                                    ${item.product.nameProduct}
                                            </a>
                                            <div class="mini-item-meta" style="font-size: 12px; color: #666; margin-bottom: 5px;">
                                                <c:if test="${not empty item.size}">Size: <strong>${item.size}</strong></c:if>
                                                <c:if test="${not empty item.sku}"> / Mã: ${item.sku}</c:if>
                                            </div>
                                            <span class="mini-item-price">
                                                <fmt:formatNumber value="${item.price}" pattern="#,###"/>₫
                                                                    </span>
                                            <span class="mini-quantity">x${item.quantity}</span>
                                        </div>
                                        <a href="${pageContext.request.contextPath}/cart?action=remove&id=${item.product.id}&sku=${item.sku}"
                                           class="remove-item" onclick="return confirm('Bạn có chắc muốn xóa sản phẩm này?')">
                                            <i class="fa-solid fa-xmark"></i>
                                        </a>
                                    </li>
                                </c:forEach>
                            </c:if>
                        </ul>

                        <div class="mini-cart-footer">
                            <div class="mini-cart-total">
                                                        <span>Tổng tiền tạm tính:
                                                            <strong class="mini-total-price js-mini-total-price">
                                        <fmt:formatNumber value="${sessionScope.cart.totalPrice}" pattern="#,###"/>₫
                                                            </strong>
                                                        </span>
                            </div>
                            <a href="${pageContext.request.contextPath}/cart" class="btn-pay">Tiến hành thanh toán</a>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <div class="search-overlay-close-area" id="searchCloseArea"></div>
</div>

<c:if test="${not empty pageTitle}">
    <div class="breadcrumb-container">
        <nav aria-label="breadcrumb">
            <ol class="breadcrumb">
                <li class="breadcrumb-item">
                    <a href="${pageContext.request.contextPath}/home">Trang Chủ</a>
                </li>

                <c:if test="${pageTitle != 'Tất cả sản phẩm'}">
                    <li class="breadcrumb-item">
                        <a href="${pageContext.request.contextPath}/list-product">Tất cả sản phẩm</a>
                    </li>
                </c:if>

                <li class="breadcrumb-item active" aria-current="page">${pageTitle}</li>
            </ol>
        </nav>
    </div>
</c:if>