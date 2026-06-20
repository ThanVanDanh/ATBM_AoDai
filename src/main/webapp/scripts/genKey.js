    async function generateAndDownloadKeyPair() {
        try {
            const keyPair = await window.crypto.subtle.generateKey({
            name: "RSASSA-PKCS1-v1_5",
            modulusLength: 2048,
            publicExponent: new Uint8Array([1, 0, 1]), hash: "SHA-256" },true,["sign", "verify"]);
        const publicKeyBuffer = await window.crypto.subtle.exportKey("spki", keyPair.publicKey);
        const privateKeyBuffer = await window.crypto.subtle.exportKey("pkcs8", keyPair.privateKey);

        const publicKeyPem = toPem(publicKeyBuffer, "PUBLIC KEY");
        const privateKeyPem = toPem(privateKeyBuffer, "PRIVATE KEY");

        await registerPublicKey(publicKeyPem);
        downloadTextFile("vsd-private-key.pem", privateKeyPem);
        alert("Đã tạo khóa thành công.");
        window.location.reload();
        } catch (error) {
            console.error(error);
            alert("Không thể tạo cặp khóa: " + error.message);}
        }

    function toPem(arrayBuffer, label) {
        const base64 = arrayBufferToBase64(arrayBuffer);
        const lines = base64.match(/.{1,64}/g) || [];
        return [
            "-----BEGIN " + label + "-----",
            ...lines,
            "-----END " + label + "-----"
        ].join("\n");
    }

    function arrayBufferToBase64(buffer) {
        const bytes = new Uint8Array(buffer);
        let binary = "";
        for (let i = 0; i < bytes.length; i++) {
            binary += String.fromCharCode(bytes[i]);
        }
        return window.btoa(binary);
    }

    function downloadTextFile(filename, content) {
        const blob = new Blob([content], { type: "application/x-pem-file" });
        const url = URL.createObjectURL(blob);

        const a = document.createElement("a");
        a.href = url;
        a.download = filename;
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);

        URL.revokeObjectURL(url);
    }

    function validatePublicKeyPem(raw) {
        let text = raw.trim();
        if (!text) return "Nội dung khóa trống.";
        if (text.indexOf("-----BEGIN PRIVATE KEY-----") !== -1) {
            return "Bạn đang dán PRIVATE KEY! Vui lòng dán PUBLIC KEY.";
        }
        if (text.indexOf("-----BEGIN PUBLIC KEY-----") === -1) {
            return "Thiếu dòng mở đầu: -----BEGIN PUBLIC KEY-----";
        }
        if (text.indexOf("-----END PUBLIC KEY-----") === -1) {
            return "Thiếu dòng kết thúc: -----END PUBLIC KEY-----";
        }
        var start = text.indexOf("-----BEGIN PUBLIC KEY-----") + "-----BEGIN PUBLIC KEY-----".length;
        var end = text.indexOf("-----END PUBLIC KEY-----");
        var body = text.substring(start, end).replace(/[\s\r\n]+/g, "");
        if (!body) return "Nội dung Base64 của khóa trống.";
        if (/[^A-Za-z0-9+/=]/.test(body)) {
            return "Nội dung khóa chứa ký tự không hợp lệ. Chỉ cho phép ký tự Base64 (A-Z, a-z, 0-9, +, /, =).";
        }
        return null;
    }

    async function registerPublicKey(publicKeyPem) {
        const response = await fetch("register-key", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                publicKey: publicKeyPem
            })
        });

        const data = await response.json();

        if (!response.ok || !data.success) {
            throw new Error(data.message || "Lỗi lưu khóa vào cơ sở dữ liệu từ phía máy chủ.");
        }
    }
    async function handleImportPublicKey(event) {
        const file = event.target.files[0];
        if (!file) return;

        const reader = new FileReader();

        reader.onload = async function(e) {
            const content = e.target.result;

            if (!content.includes("-----BEGIN PUBLIC KEY-----")) {
                alert("File không hợp lệ! Vui lòng chọn file chứa Public Key định dạng PEM.");
                event.target.value = "";
                return;
            }

            try {
                await registerPublicKey(content);
                alert("Đã nhập khóa thành công! Public key đã được cập nhật vào hệ thống.");
                location.reload();
            } catch (error) {
                console.error(error);
                alert("Lỗi khi lưu khóa: " + error.message);
            }
        };
        reader.readAsText(file);
    }
    function openImportTextModal() {
        const modal = document.getElementById('import-text-modal');
        if (modal) modal.style.display = 'flex';
    }

    function closeImportTextModal() {
        const modal = document.getElementById('import-text-modal');
        if (modal) {
            modal.style.display = 'none';
            document.getElementById('pasted-public-key').value = '';
        }
    }

    async function submitPastedKey() {
        const keyContent = document.getElementById('pasted-public-key').value.trim();

        if (!keyContent) {
            alert("Vui lòng dán nội dung khóa trước khi lưu!");
            return;
        }

        const error = validatePublicKeyPem(keyContent);
        if (error) {
            alert("Định dạng Public Key không hợp lệ!\n\n" + error + "\n\nVui lòng kiểm tra lại và dán đúng định dạng PEM:\n-----BEGIN PUBLIC KEY-----\nMIIBIjAN...\n-----END PUBLIC KEY-----");
            return;
        }

        try {
            await registerPublicKey(keyContent);
            alert("Đã nhập khóa thành công! Public key đã được cập nhật vào hệ thống.");
            closeImportTextModal();
            location.reload();
        } catch (error) {
            console.error(error);
            alert("Lỗi khi lưu khóa: " + error.message);
        }
    }
