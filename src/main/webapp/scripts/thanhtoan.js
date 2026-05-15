
    function handleIncreaseQuantity(event) {
    const btn = event.currentTarget;
    const quantityDisplay = btn.previousElementSibling;

    if (quantityDisplay) {
    let currentQuantity = parseInt(quantityDisplay.textContent);
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
    btn.addEventListener('click', handleIncreaseQuantity);
});

    btnDecreaseList.forEach(btn => {
    btn.addEventListener('click', handleDecreaseQuantity);
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
            alert(`Đã bỏ sản phẩm: ${cartItem.querySelector('.item-details p').textContent}!`);
            cartItem.remove();
        } else {
            alert("Lỗi: Không tìm thấy sản phẩm để xóa!");
        }
    };
});

    const popupOverlay = document.getElementById("popupOverlay");
    const btnShowPromo = document.getElementById("btnOpenkm");
    const btnCloseTop  = document.getElementById("btnCloseTop");
    const btnCloseBottom = document.getElementById("btnCloseBottom");

    if (btnShowPromo && popupOverlay) {
    btnShowPromo.onclick = ()=> popupOverlay.style.display = "flex";
}
    if (btnCloseTop && popupOverlay) {
    btnCloseTop.onclick = ()=> popupOverlay.style.display = "none";
}
    if (btnCloseBottom && popupOverlay) {
    btnCloseBottom.onclick = ()=> popupOverlay.style.display = "none";
}