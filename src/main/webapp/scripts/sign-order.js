function openSignModal(orderHash, canonicalData) {
    document.getElementById('sign-modal-hash').textContent = orderHash;
    document.getElementById('sign-modal-canonical').textContent = canonicalData;
    document.getElementById('sign-modal-signature').value = '';
    document.getElementById('sign-modal-error').textContent = '';
    document.getElementById('sign-order-overlay').style.display = 'flex';
}

function closeSignModal() {
    document.getElementById('sign-order-overlay').style.display = 'none';
    fetch('cancel-pending-order', { method: 'POST' }).catch(() => {});
}

function copyOrderHash() {
    const hash = document.getElementById('sign-modal-hash').textContent;
    navigator.clipboard.writeText(hash).then(() => {
        const btn = document.getElementById('btn-copy-hash');
        btn.textContent = 'Đã sao chép!';
        setTimeout(() => btn.textContent = 'Sao chép hash', 2000);
    });
}

function downloadHashFile() {
    const hash = document.getElementById('sign-modal-hash').textContent;
    const canonicalData = document.getElementById('sign-modal-canonical').textContent;

    //định dạng file để tool đọc được
    const content = canonicalData + '\nORDER_HASH=' + hash;
    const blob = new Blob([content], { type: 'text/plain;charset=utf-8' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = 'order_hash.txt';
    a.click();
    URL.revokeObjectURL(url);
}

async function submitSignedOrder() {
    const signature = document.getElementById('sign-modal-signature').value.trim();
    const errorEl = document.getElementById('sign-modal-error');

    if (!signature) {
        errorEl.textContent = 'Vui lòng nhập chữ ký trước khi xác nhận.';
        return;
    }

    const submitBtn = document.getElementById('btn-submit-signature');
    submitBtn.disabled = true;
    submitBtn.textContent = 'Đang xác thực...';
    errorEl.textContent = '';

    try {
        const resp = await fetch('submit-signed-order', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
            body: 'signature=' + encodeURIComponent(signature)
        });
        const data = await resp.json();

        if (data.success) {
            document.getElementById('sign-order-overlay').style.display = 'none';
            Swal.fire({
                icon: 'success',
                title: 'Đặt hàng thành công!',
                text: 'Đơn hàng đã được xác thực và ghi nhận.',
                timer: 2500,
                showConfirmButton: false
            }).then(() => {
                window.location.href = data.redirect || 'account';
            });
        } else {
            errorEl.textContent = data.message || 'Chữ ký không hợp lệ, vui lòng thử lại.';
        }
    } catch (e) {
        errorEl.textContent = 'Không thể kết nối đến máy chủ.';
    } finally {
        submitBtn.disabled = false;
        submitBtn.textContent = 'Xác nhận đặt hàng';
    }
}
