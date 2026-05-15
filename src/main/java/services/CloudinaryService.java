package services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import java.io.*;
import java.util.Properties;
import java.util.Map;

public class CloudinaryService {

    private Cloudinary cloudinary;

    public CloudinaryService(){
        initFromProperties();
    }

    private void initFromProperties() {
        try (InputStream input = CloudinaryService.class.getClassLoader().getResourceAsStream("cloudinary.properties")) {
            if (input == null) {
                throw new IllegalAccessException("Không tìm thấy file cloudinary.properties");
            }
            Properties prop = new Properties();
            prop.load(input);
            String cloudName = prop.getProperty("cloud_name");
            String apiKey = prop.getProperty("api_key");
            String apiSecret = prop.getProperty("api_secret");
            cloudinary = new Cloudinary(ObjectUtils.asMap("cloud_name", cloudName, "api_key", apiKey, "api_secret", apiSecret));
        } catch (IllegalAccessException | IOException e) {
            throw new RuntimeException("Khởi tạo Cloudinary thất bại",e);
        }
    }

    public UploadedImage upload(InputStream inputStream, String filename) {
        if (inputStream == null) return null;
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> res = cloudinary.uploader().upload(inputStream,
                    ObjectUtils.asMap("resource_type", "auto", "public_id", stripExtension(filename)));
            return mapToUploadedImage(res);
        } catch (Exception e) {
            try {
                File tmp = streamToTempFile(inputStream, filename);
                UploadedImage ui = upload(tmp);
                if (tmp.exists()) tmp.delete();
                return ui;
            } catch (Exception ex) {
                throw new RuntimeException("Upload Cloudinary thất bại (stream->tmp) cho " + filename, ex);
            }
        }
    }
    public UploadedImage upload(File file) {
        if (file == null) return null;
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> res = cloudinary.uploader().upload(file,
                    ObjectUtils.asMap("resource_type", "auto", "public_id", stripExtension(file.getName())));
            return mapToUploadedImage(res);
        } catch (Exception e) {
            throw new RuntimeException("Upload Cloudinary thất bại cho file: " + file.getAbsolutePath(), e);
        }
    }
    public boolean deleteByPublicId(String publicId) {
        if (publicId == null) return false;
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> res = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            Object result = res.get("result");
            return (result != null && (result.equals("ok") || result.equals("not found")));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    private UploadedImage mapToUploadedImage(Map<String, Object> res) {
        if (res == null) return null;
        String secureUrl = (String) res.get("secure_url");
        String publicId = (String) res.get("public_id");
        return new UploadedImage(secureUrl, publicId, res);
    }

    private String stripExtension(String filename) {
        if (filename == null) return null;
        int i = filename.lastIndexOf('.');
        if (i > 0) return filename.substring(0, i);
        return filename;
    }

    private File streamToTempFile(InputStream in, String filename) throws IOException {
        String prefix = "cld_";
        String suffix = null;
        int i = filename != null ? filename.lastIndexOf('.') : -1;
        if (i > 0) suffix = filename.substring(i);
        File tmp = File.createTempFile(prefix, suffix);
        try (OutputStream os = new FileOutputStream(tmp)) {
            byte[] buf = new byte[8192];
            int len;
            while ((len = in.read(buf)) != -1) os.write(buf, 0, len);
            os.flush();
        }
        return tmp;
    }
    public String uploadImage(String filePath) {
        if (cloudinary == null) return null;

        try {
            java.io.File file = new java.io.File(filePath);
            Map uploadResult = cloudinary.uploader().upload(file, ObjectUtils.emptyMap());
            return (String) uploadResult.get("secure_url");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    public static class UploadedImage {
        private final String secureUrl;
        private final String publicId;
        private final Map<String, Object> rawResult;

        public UploadedImage(String secureUrl, String publicId, Map<String, Object> rawResult) {
            this.secureUrl = secureUrl;
            this.publicId = publicId;
            this.rawResult = rawResult;
        }

        public String getSecureUrl() { return secureUrl; }
        public String getPublicId() { return publicId; }
        public Map<String, Object> getRawResult() { return rawResult; }
    }
}