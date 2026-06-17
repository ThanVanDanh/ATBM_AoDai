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

    const btnOpenList = document.querySelectorAll(".js-open-modal");
    const btnCloseList = document.querySelectorAll(".js-close-modal");
    const btnRemoveList = document.querySelectorAll(".js-remove-item");

    btnOpenList.forEach(btnOpen => {
        btnOpen.onclick = () => {
            const overlay = btnOpen.nextElementSibling;
            if (overlay) {
                overlay.style.display = "flex";
            }
        };
    });

    btnCloseList.forEach(btnClose => {
        btnClose.onclick = () => {
            const overlay = btnClose.closest(".js-overlay");
            if (overlay) {
                overlay.style.display = "none";
            }
        };
    });

    btnRemoveList.forEach(btnRemove => {
        btnRemove.onclick = () => {
            const overlay = btnRemove.closest(".js-overlay");
            if (overlay) {
                overlay.style.display = "none";
            }

            const cartItem = btnRemove.closest(".js-cart-item");

            if (cartItem) {
                const itemName = cartItem.querySelector(".item-details p");
                alert(`Đã bỏ sản phẩm: ${itemName ? itemName.textContent : ""}!`);
                cartItem.remove();
            } else {
                alert("Lỗi: Không tìm thấy sản phẩm để xóa!");
            }
        };
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