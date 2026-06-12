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
