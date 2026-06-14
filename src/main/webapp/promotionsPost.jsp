<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>
        <c:choose>
            <c:when test="${not empty article}">${article.title} - Việt Sắc Đỏ</c:when>
            <c:otherwise>Chi tiết Khuyến mãi - Việt Sắc Đỏ</c:otherwise>
        </c:choose>
    </title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/style/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/style/style-header.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/style/footer.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/style/promotionsPoststyle.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/7.0.1/css/all.min.css"
          integrity="sha512-2SwdPD6INVrV/lHTZbO2nodKhrnDdJK9/kg2XD1r9uGqPo1cUbujc+IYdlYdEErWNu69gVcYgdxlmVmzTWnetw=="
          crossorigin="anonymous" referrerpolicy="no-referrer"/>
    <script src="${pageContext.request.contextPath}/scripts/home.js"></script>
    <script src="${pageContext.request.contextPath}/scripts/promotionPost.js"></script>

</head>
<body>

<jsp:include page="header.jsp"/>

<main>
    <c:choose>
        <c:when test="${empty article}">
            <div style="text-align:center; padding:80px 20px;">
                <i class="fas fa-exclamation-circle" style="font-size:64px; color:#e74c3c;"></i>
                <h2 style="color:#e74c3c; margin-top:20px;">Không tìm thấy bài viết</h2>
                <p style="color:#666; margin:20px 0;">Bài viết này không tồn tại hoặc đã bị xóa.</p>
                <a href="${pageContext.request.contextPath}/promotion" style="display:inline-block; padding:10px 20px; background:#320000; color:white; text-decoration:none; border-radius:4px; margin-top:20px;">
                    Quay lại danh sách khuyến mãi
                </a>
            </div>
        </c:when>
        <c:otherwise>
            <article class="blog-post">
                <h1>${article.title}</h1>
                <p class="post-meta">
                    Đăng bởi Việt Sắc Đỏ
                    <c:if test="${not empty article.startDate}">
                        | Ngày ${article.startDate.toString().substring(0, 10).replace('-', '/')}
                    </c:if>
                    <c:if test="${empty article.startDate and not empty article.createdAt}">
                        | Ngày ${article.createdAt.toString().substring(0, 10).replace('-', '/')}
                    </c:if>
                </p>

                <c:if test="${not empty article.bannerImageUrl}">
                    <div class="image-placeholder">
                        <img id="imgpost" src="${article.bannerImageUrl}"
                             alt="${article.title}"
                             style="width:auto; max-width:100%; height:auto; display:block; margin:0 auto;"
                             onerror="this.style.display='none'">
                    </div>
                </c:if>

                <div class="post-content">
                    <c:choose>
                        <c:when test="${not empty article.content}">
                            ${article.content}
                        </c:when>
                        <c:otherwise>
                            <p>Nội dung đang được cập nhật...</p>
                        </c:otherwise>
                    </c:choose>
                </div>

                <c:if test="${not empty voucher}">
                    <div class="voucher-box">
                        <h3>
                            <i class="fas fa-ticket-alt"></i> Thông tin mã khuyến mãi
                        </h3>
                        <div style="display:flex; align-items:center; gap:20px; flex-wrap:wrap;">
                            <div style="flex:1; min-width:250px;">
                                <p style="margin:10px 0; font-size:15px;">
                                    <strong>🎫 Mã voucher:</strong>
                                    <span class="voucher-code" id="voucher-code-display">${voucher.voucherCode}</span>
                                </p>
                                <p style="margin:10px 0; font-size:15px;">
                                    <strong>💰 Giá trị giảm:</strong>
                                    <c:choose>
                                        <c:when test="${voucher.discountType == 'percentage'}">
                                            Giảm ${voucher.discountValue}%
                                        </c:when>
                                        <c:otherwise>
                                            Giảm <fmt:formatNumber value="${voucher.discountValue}" pattern="#,###"/>đ
                                        </c:otherwise>
                                    </c:choose>
                                </p>
                                <p style="margin:10px 0; font-size:15px;">
                                    <strong>📦 Đơn tối thiểu:</strong>
                                    <c:choose>
                                        <c:when test="${voucher.minOrderAmount > 0}">
                                            <fmt:formatNumber value="${voucher.minOrderAmount}" pattern="#,###"/>đ
                                        </c:when>
                                        <c:otherwise>
                                            Không yêu cầu
                                        </c:otherwise>
                                    </c:choose>
                                </p>
                                <c:if test="${not empty voucher.validFrom and not empty voucher.validTo}">
                                    <p style="margin:10px 0; font-size:14px; color:#666;">
                                        <i class="far fa-calendar-alt"></i>
                                        <strong>Thời hạn:</strong> Từ ${voucher.validFrom.toString().substring(0, 10).replace('-', '/')} đến ${voucher.validTo.toString().substring(0, 10).replace('-', '/')}
                                    </p>
                                </c:if>
                            </div>
                            <button onclick="copyVoucherCode()" class="btn-copy-voucher">
                                <i class="fas fa-copy"></i> Sao chép mã
                            </button>
                        </div>
                    </div>
                </c:if>
            </article>
        </c:otherwise>
    </c:choose>
</main>

<jsp:include page="footer.jsp"/>



</body>
</html>
