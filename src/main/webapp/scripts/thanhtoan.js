function showLoading() {
    if (typeof Swal !== "undefined") {
        Swal.fire({
            title: "Đang xử lý...",
            text: "Vui lòng chờ trong giây lát",
            allowOutsideClick: false,
            didOpen: () => Swal.showLoading()
        });
    }
}

function closeLoading() {
    if (typeof Swal !== "undefined") {
        Swal.close();
    }
}

function showSuccess(message) {
    if (typeof Swal !== "undefined") {
        Swal.fire({
            icon: "success",
            title: "Đặt hàng thành công!",
            text: message,
            timer: 2500,
            showConfirmButton: false
        }).then(() => {
            window.location.href = "account";
        });
    } else {
        alert(message);
        window.location.href = "account";
    }
}

function showError(message) {
    if (typeof Swal !== "undefined") {
        Swal.fire({
            icon: "error",
            title: "Thất bại",
            text: message
        });
    } else {
        alert(message);
    }
}

function placeOrder() {
    const form = document.getElementById("checkoutForm");

    if (!form) {
        showError("Không tìm thấy form thanh toán.");
        return;
    }

    if (!form.checkValidity()) {
        form.reportValidity();
        return;
    }

    const formData = new URLSearchParams(new FormData(form));
    const actionUrl = form.getAttribute("action") || "checkout";

    showLoading();

    fetch(actionUrl, {
        method: "POST",
        headers: {
            "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8"
        },
        body: formData
    })
        .then(response => response.json())
        .then(data => {
            closeLoading();

            if (data.success) {
                showSuccess("Đơn hàng đang chờ bạn xác thực bằng chữ ký số.");
            } else {
                showError(data.message || "Có lỗi xảy ra, vui lòng thử lại.");
            }
        })
        .catch(() => {
            closeLoading();
            showError("Không thể kết nối đến máy chủ.");
        });
}

window.placeOrder = placeOrder;

document.addEventListener("DOMContentLoaded", function () {
    function handleIncreaseQuantity(event) {
        const btn = event.currentTarget;
        const quantityDisplay = btn.previousElementSibling;

        if (quantityDisplay) {
            let currentQuantity = parseInt(quantityDisplay.textContent);

            if (Number.isNaN(currentQuantity)) {
                currentQuantity = 1;
            }

            currentQuantity += 1;
            quantityDisplay.textContent = currentQuantity;
            quantityDisplay.dataset.quantity = currentQuantity;
        }
    }

    function handleDecreaseQuantity(event) {
        const btn = event.currentTarget;
        const quantityDisplay = btn.nextElementSibling;

        if (quantityDisplay) {
            let currentQuantity = parseInt(quantityDisplay.textContent);

            if (Number.isNaN(currentQuantity)) {
                currentQuantity = 1;
            }

            if (currentQuantity > 1) {
                currentQuantity -= 1;
                quantityDisplay.textContent = currentQuantity;
                quantityDisplay.dataset.quantity = currentQuantity;
            } else {
                alert("Số lượng tối thiểu là 1. Nhấn thùng rác để xóa sản phẩm.");
            }
        }
    }

    const btnIncreaseList = document.querySelectorAll(".js-increase-quantity");
    const btnDecreaseList = document.querySelectorAll(".js-decrease-quantity");

    btnIncreaseList.forEach(btn => {
        btn.addEventListener("click", handleIncreaseQuantity);
    });

    btnDecreaseList.forEach(btn => {
        btn.addEventListener("click", handleDecreaseQuantity);
    });
    document.addEventListener("click", function (event) {
        const openBtn = event.target.closest(".js-open-modal");

        if (openBtn) {
            event.preventDefault();

            const cartItem = openBtn.closest(".js-cart-item");
            const overlay = cartItem ? cartItem.querySelector(".js-overlay") : null;

            if (overlay) {
                overlay.style.display = "flex";
            }

            return;
        }

        const closeBtn = event.target.closest(".js-close-modal");

        if (closeBtn) {
            event.preventDefault();

            const overlay = closeBtn.closest(".js-overlay");

            if (overlay) {
                overlay.style.display = "none";
            }

            return;
        }

        if (event.target.classList.contains("js-overlay")) {
            event.target.style.display = "none";
        }
    });

    const popupOverlay = document.getElementById("popupOverlay");
    const btnShowPromo = document.getElementById("btnOpenkm");
    const btnCloseTop = document.getElementById("btnCloseTop");
    const btnCloseBottom = document.getElementById("btnCloseBottom");

    if (btnShowPromo && popupOverlay) {
        btnShowPromo.onclick = () => {
            popupOverlay.style.display = "flex";
        };
    }

    if (btnCloseTop && popupOverlay) {
        btnCloseTop.onclick = () => {
            popupOverlay.style.display = "none";
        };
    }

    if (btnCloseBottom && popupOverlay) {
        btnCloseBottom.onclick = () => {
            popupOverlay.style.display = "none";
        };
    }
});