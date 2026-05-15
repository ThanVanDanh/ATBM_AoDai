function copyVoucherCode() {
    var codeElement = document.getElementById('voucher-code-display');
    if (!codeElement) return;

    var code = codeElement.textContent;

    if (navigator.clipboard && navigator.clipboard.writeText) {
        navigator.clipboard.writeText(code).then(function () {
            alert('Đã sao chép mã: ' + code);
        }).catch(function () {
            fallbackCopy(code);
        });
    } else {
        fallbackCopy(code);
    }
}

function fallbackCopy(text) {
    var textarea = document.createElement('textarea');
    textarea.value = text;
    textarea.style.position = 'fixed';
    textarea.style.opacity = '0';
    document.body.appendChild(textarea);
    textarea.select();
    try {
        document.execCommand('copy');
        alert('Đã sao chép mã: ' + text);
    } catch (err) {
        alert('Không thể sao chép. Vui lòng copy thủ công: ' + text);
    }
    document.body.removeChild(textarea);
}