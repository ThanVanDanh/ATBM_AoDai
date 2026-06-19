
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