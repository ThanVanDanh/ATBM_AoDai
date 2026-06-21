<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="vi">

<head>
    <meta charset="UTF-8">
    <title>Áo dài Linen</title>
    <link rel="icon" href="image/logoaodai.jpg" type="image/jpeg">

    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/7.0.1/css/all.min.css"
          crossorigin="anonymous"
          referrerpolicy="no-referrer" />
    <link rel="stylesheet" href="style/aodai.css">
    <link rel="stylesheet" href="style/style-header.css">
    <link rel="stylesheet" href="style/footer.css">
    <link rel="stylesheet" href="style/breadcrumb.css">
    <link rel="stylesheet" href="style/backtop.css">
    <link rel="stylesheet" href="https://unpkg.com/swiper/swiper-bundle.min.css">
    <link rel="stylesheet" href="style/quick-view.css">
    <link rel="stylesheet" href="style/thanhtoan.css">

    <script src="scripts/home.js"></script>
    <script src="scripts/backtop.js"></script>
    <script src="https://unpkg.com/swiper/swiper-bundle.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
</head>

<body>

<jsp:include page="header.jsp" />

<div class="breadcrumb-container">
    <nav aria-label="breadcrumb">
        <ol class="breadcrumb">
            <li class="breadcrumb-item">
                <a href="${pageContext.request.contextPath}/home">Trang Chủ</a>
            </li>
            <li class="breadcrumb-item">
                <a href="${pageContext.request.contextPath}/checkout">Thanh toán</a>
            </li>
            <li class="breadcrumb-item active" aria-current="page">Tiến hành thanh toán</li>
        </ol>
    </nav>
</div>

<div class="container">
    <main class="checkout-layout">

        <div class="left-column">
            <form id="checkoutForm" action="${pageContext.request.contextPath}/checkout" method="post">
                <div class="card shipping-info">
                    <h3>Thông tin giao hàng</h3>

                    <input type="text"
                           name="fullName"
                           placeholder="Nhập họ và tên"
                           required
                           value="${not empty sessionScope.checkoutFormData.fullName ? sessionScope.checkoutFormData.fullName : sessionScope.account.fullName}">

                    <div class="input-with-icon">
                        <input type="tel"
                               name="phone"
                               placeholder="Nhập số điện thoại"
                               required
                               value="${not empty sessionScope.checkoutFormData.phone ? sessionScope.checkoutFormData.phone : sessionScope.account.phone}">
                        <span class="flag-icon">🇻🇳</span>
                    </div>

                    <input type="email"
                           name="email"
                           placeholder="Nhập email"
                           required
                           value="${not empty sessionScope.checkoutFormData.email ? sessionScope.checkoutFormData.email : sessionScope.account.email}">

                    <input type="text"
                           name="country"
                           value="${not empty sessionScope.checkoutFormData.country ? sessionScope.checkoutFormData.country : (not empty sessionScope.defaultAddress ? sessionScope.defaultAddress.country : 'Vietnam')}"
                           readonly>

                    <input type="text"
                           name="address"
                           placeholder="Địa chỉ, tên đường"
                           required
                           value="${not empty sessionScope.checkoutFormData.address ? sessionScope.checkoutFormData.address : (not empty sessionScope.defaultAddress ? sessionScope.defaultAddress.addressLine : '')}">

                    <input type="text"
                           name="city"
                           placeholder="Tỉnh/TP, Quận/Huyện, Phường/Xã"
                           required
                           value="${not empty sessionScope.checkoutFormData.city ? sessionScope.checkoutFormData.city : (not empty sessionScope.defaultAddress ? sessionScope.defaultAddress.cityProvince : '')}">
                </div>

                <div class="spacer"></div>

                <div class="card shipping-method">
                    <h3>Phương thức giao hàng</h3>
                    <input type="text"
                           placeholder="Giao hàng nhanh (Mặc định)"
                           value="Giao hàng tiêu chuẩn - 30,000₫"
                           readonly>
                </div>

                <div class="spacer"></div>

                <div class="card payment-method">
                    <h3>Phương thức thanh toán</h3>

                    <label class="radio-option">
                        <input type="radio" name="paymentMethod" value="cod" ${empty sessionScope.checkoutFormData.paymentMethod or sessionScope.checkoutFormData.paymentMethod == 'cod' ? 'checked' : ''} required>Thanh toán khi nhận hàng
                    </label>
                    <label class="radio-option">
                        <input type="radio" name="paymentMethod" value="payoo" ${sessionScope.checkoutFormData.paymentMethod == 'payoo' ? 'checked' : ''}>
                        Thanh toán online qua Payoo (Thẻ ATM, VISA, Mastercard, v.v...)
                        <div class="payment-logos">
                            <img src="image/payoo-logo-jpg-inkythuatso.jpg" alt="Payoo" style="height: 15px;">
                            <i class="fab fa-cc-visa"></i>
                            <i class="fab fa-cc-mastercard"></i>
                            <i class="fas fa-credit-card"></i>
                        </div>
                    </label>
                </div>

                <div class="spacer"></div>

                <div class="card order-note">
                    <input type="text" name="orderNote" placeholder="Ghi chú đơn hàng" value="${not empty sessionScope.checkoutFormData.orderNote ? sessionScope.checkoutFormData.orderNote : ''}">                </div>
            </form>
        </div>

        <div class="right-column">

            <div class="card cart-summary">
                <h3>Giỏ hàng</h3>

                <c:choose>
                    <c:when test="${empty sessionScope.cart or sessionScope.cart.totalQuantity == 0}">
                        <div style="text-align: center; padding: 20px;">
                            <p>Giỏ hàng của bạn đang trống</p>
                            <a href="${pageContext.request.contextPath}/all-product.jsp"
                               style="color: #d32f2f; text-decoration: none;">Tiếp tục mua sắm</a>
                        </div>
                    </c:when>

                    <c:otherwise>
                        <c:forEach var="item" items="${sessionScope.cart.items}">
                            <div class="cart-item js-cart-item"
                                 data-product-id="${item.product.id}"
                                 data-sku="${item.sku}">

                                <img src="${not empty item.product.images ? item.product.images[0].imageUrl : 'image/no-image.png'}"
                                     alt="${item.product.nameProduct}"
                                     class="product-image">

                                <div class="item-details">
                                    <p>${item.product.nameProduct}</p>
                                    <p class="item-variant">${item.sku} / ${item.size}</p>
                                    <p class="current-price">
                                        <fmt:formatNumber value="${item.price}" pattern="#,###" />₫
                                    </p>
                                </div>

                                <div class="item-controls">
                                    <button type="button"
                                            class="remove-btn js-open-modal"
                                            aria-label="Xóa sản phẩm">
                                        <i class="fa-solid fa-trash-can"></i>
                                    </button>

                                    <div class="overlay js-overlay" style="display: none;">
                                        <div class="popup">
                                            <h2>Bạn muốn bỏ khỏi giỏ hàng</h2>
                                            <p class="product">${item.product.nameProduct}</p>

                                            <div class="actions">
                                                <button type="button" class="btn normal js-close-modal">
                                                    Quay lại
                                                </button>

                                                <a href="${pageContext.request.contextPath}/cart?action=remove&id=${item.product.id}&sku=${item.sku}"
                                                   class="btn danger js-remove-item-link"
                                                   style="text-decoration: none; color: white;">
                                                    Bỏ sản phẩm
                                                </a>
                                            </div>
                                        </div>
                                    </div>

                                    <div class="quantity-control">
                                        <form action="${pageContext.request.contextPath}/cart"
                                              method="post"
                                              style="display: flex; align-items: center;">
                                            <input type="hidden" name="action" value="update">
                                            <input type="hidden" name="id" value="${item.product.id}">
                                            <input type="hidden" name="sku" value="${item.sku}">

                                            <button type="submit"
                                                    name="quantity"
                                                    value="${item.quantity > 1 ? item.quantity - 1 : 1}"
                                                    class="js-decrease-quantity">-</button>

                                            <span class="js-quantity-display" style="margin: 0 10px;">
                                                    ${item.quantity}
                                            </span>

                                            <button type="submit"
                                                    name="quantity"
                                                    value="${item.quantity + 1}"
                                                    class="js-increase-quantity">+</button>
                                        </form>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
            </div>

            <div class="spacer"></div>

            <div class="card promotion">
                <h3>Mã khuyến mãi</h3>

                <c:if test="${not empty requestScope.voucherError}">
                    <div style="background-color: #fff3cd; color: #856404; padding: 10px; margin-bottom: 10px; border-radius: 4px; font-size: 13px; border: 1px solid #ffeeba;">
                        <i class="fa-solid fa-triangle-exclamation"></i>
                            ${requestScope.voucherError}
                    </div>
                </c:if>

                <c:if test="${not empty sessionScope.appliedVoucher}">
                    <div style="background-color: #e8f5e9; color: #2e7d32; padding: 10px; margin-bottom: 10px; border-radius: 4px; font-size: 13px; border: 1px solid #c8e6c9;">
                        Đang áp dụng mã: <strong>${sessionScope.appliedVoucher.voucherCode}</strong>
                    </div>
                </c:if>

                <div class="select-promo">
                    <span>Chọn mã</span>
                    <button type="button" class="btnKhuyenMai" id="btnOpenkm">
                        <i class="fa-solid fa-chevron-right"></i>
                    </button>

                    <div class="popupOverlay" id="popupOverlay">
                        <div class="promoModal">
                            <div class="header-khuyenMai">
                                <span class="btnCloseTop" id="btnCloseTop">
                                    <i class="fa-solid fa-xmark"></i>
                                </span>
                                <h3 class="h3-khuyenmai">Chọn mã khuyến mãi</h3>
                            </div>

                            <c:choose>
                                <c:when test="${not empty requestScope.vouchers}">
                                    <div class="promo-list" style="padding: 15px; overflow-y: auto; max-height: 300px;">
                                        <c:forEach var="v" items="${requestScope.vouchers}">
                                            <div class="promo-item"
                                                 style="border: 1px solid #ddd; padding: 10px; margin-bottom: 10px; border-radius: 8px;">
                                                <div style="font-weight: bold; color: #d32f2f;">
                                                        ${v.voucherCode}
                                                </div>

                                                <div>
                                                    Giảm
                                                    <fmt:formatNumber value="${v.discountValue}" pattern="#,###" />
                                                        ${v.discountType == 'percentage' ? '%' : '₫'}
                                                </div>

                                                <div style="font-size: 12px; color: #666;">
                                                    Đơn tối thiểu:
                                                    <fmt:formatNumber value="${v.minOrderAmount}" pattern="#,###" />₫
                                                </div>

                                                <button type="button"
                                                        class="btn-use-promo"
                                                        onclick="applyPromo('${v.voucherCode}')"
                                                        style="margin-top: 5px; background: #333; color: white; border: none; padding: 4px 8px; border-radius: 4px; cursor: pointer;">
                                                    Dùng ngay
                                                </button>
                                            </div>
                                        </c:forEach>
                                    </div>
                                </c:when>

                                <c:otherwise>
                                    <div class="noPromoBox">
                                        <img src="image/discount.png" class="promoIcon">
                                        <p>Không có mã khuyến mãi phù hợp</p>
                                    </div>
                                </c:otherwise>
                            </c:choose>

                            <div class="bottomZone">
                                <button type="button" class="btnCloseBottom" id="btnCloseBottom">Đóng</button>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="apply-promo">
                    <input type="text" id="promoInput" placeholder="Nhập mã khuyến mãi">
                    <button type="button" class="btn-apply" onclick="applyPromoInput()">Áp dụng</button>
                </div>
            </div>

            <div class="spacer"></div>

            <div class="card order-total">
                <h3>Tóm tắt đơn hàng</h3>

                <c:set var="cartTotal" value="${sessionScope.cart.totalPrice}" />
                <c:set var="shippingFee" value="${cartTotal >= 1000000 ? 0 : 30000}" />
                <c:set var="discountAmount" value="0" />

                <c:if test="${not empty sessionScope.appliedVoucher}">
                    <c:set var="voucher" value="${sessionScope.appliedVoucher}" />
                    <c:choose>
                        <c:when test="${voucher.discountType == 'percentage' or voucher.discountType == 'percent'}">
                            <c:set var="discountAmount" value="${cartTotal * (voucher.discountValue / 100)}" />
                        </c:when>
                        <c:otherwise>
                            <c:set var="discountAmount" value="${voucher.discountValue}" />
                        </c:otherwise>
                    </c:choose>
                </c:if>

                <c:set var="finalTotal" value="${cartTotal + shippingFee - discountAmount}" />

                <c:if test="${finalTotal < 0}">
                    <c:set var="finalTotal" value="0" />
                </c:if>

                <div class="total-row">
                    <span>Tổng tiền hàng</span>
                    <span>
                        <fmt:formatNumber value="${cartTotal}" pattern="#,###" />₫
                    </span>
                </div>

                <div class="total-row">
                    <span>Phí vận chuyển</span>
                    <span>
                        <c:choose>
                            <c:when test="${shippingFee == 0}">
                                <span style="color: green;">Miễn phí</span>
                            </c:when>
                            <c:otherwise>
                                <fmt:formatNumber value="${shippingFee}" pattern="#,###" />₫
                            </c:otherwise>
                        </c:choose>
                    </span>
                </div>

                <div class="total-row">
                    <span>Voucher giảm giá</span>
                    <span>
                        -
                        <fmt:formatNumber value="${discountAmount}" pattern="#,###" />₫
                    </span>
                </div>

                <hr>

                <div class="total-row final-total">
                    <span>Tổng thanh toán</span>
                    <span>
                        <fmt:formatNumber value="${finalTotal}" pattern="#,###" />₫
                    </span>
                </div>

                <button type="button" class="btn-order" onclick="placeOrder()">Đặt hàng</button>
            </div>
        </div>
    </main>
</div>

<jsp:include page="footer.jsp" />

<script>
    function applyPromo(code) {
        if (!code || code.trim() === "") {
            alert("Vui lòng nhập mã khuyến mãi");
            return;
        }

        const checkoutForm = document.getElementById("checkoutForm");
        const form = document.createElement("form");
        form.method = "post";
        form.action = "${pageContext.request.contextPath}/checkout/apply-voucher";

        if (checkoutForm) {
            const formData = new FormData(checkoutForm);
            formData.forEach((value, key) => {
                const field = document.createElement("input");
                field.type = "hidden";
                field.name = key;
                field.value = value;
                form.appendChild(field);
            });
        }
        const input = document.createElement("input");
        input.type = "hidden";
        input.name = "promoCode";
        input.value = code.trim();

        form.appendChild(input);
        document.body.appendChild(form);
        form.submit();
    }

    function applyPromoInput() {
        const promoInput = document.getElementById("promoInput");
        const code = promoInput ? promoInput.value.trim() : "";

        if (code) {
            applyPromo(code);
        } else {
            alert("Vui lòng nhập mã khuyến mãi");
        }
    }

    function placeOrder() {
        const form = document.getElementById("checkoutForm");

        if (!form) {
            Swal.fire({
                icon: "error",
                title: "Lỗi",
                text: "Không tìm thấy form thanh toán."
            });
            return;
        }

        if (!form.checkValidity()) {
            form.reportValidity();
            return;
        }

        const formData = new URLSearchParams(new FormData(form));

        Swal.fire({
            title: "Đang xử lý...",
            text: "Vui lòng chờ trong giây lát",
            allowOutsideClick: false,
            didOpen: () => Swal.showLoading()
        });

        fetch(form.action, {
            method: "POST",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8"
            },
            body: formData
        })
            .then(resp => resp.json())
            .then(data => {
                Swal.close();

                if (data.success) {
                    Swal.fire({
                        icon: "success",
                        title: "Đặt hàng thành công!",
                        text: "Đơn hàng đang chờ bạn xác thực bằng chữ ký số.",
                        timer: 2500,
                        showConfirmButton: false
                    }).then(() => {
                        window.location.href = "${pageContext.request.contextPath}/account";
                    });
                } else {
                    Swal.fire({
                        icon: "error",
                        title: "Thất bại",
                        text: data.message || "Có lỗi xảy ra, vui lòng thử lại."
                    });
                }
            })
            .catch(() => {
                Swal.close();
                Swal.fire({
                    icon: "error",
                    title: "Lỗi",
                    text: "Không thể kết nối đến máy chủ."
                });
            });
    }

    const popupOverlay = document.getElementById("popupOverlay");
    const btnShowPromo = document.getElementById("btnOpenkm");
    const btnCloseTop = document.getElementById("btnCloseTop");
    const btnCloseBottom = document.getElementById("btnCloseBottom");

    if (btnShowPromo && popupOverlay) {
        btnShowPromo.onclick = function (e) {
            e.preventDefault();
            popupOverlay.style.display = "flex";
        };
    }

    if (btnCloseTop && popupOverlay) {
        btnCloseTop.onclick = function () {
            popupOverlay.style.display = "none";
        };
    }

    if (btnCloseBottom && popupOverlay) {
        btnCloseBottom.onclick = function () {
            popupOverlay.style.display = "none";
        };
    }
</script>
<script src="${pageContext.request.contextPath}/scripts/thanhtoan.js"></script>
</body>
</html>