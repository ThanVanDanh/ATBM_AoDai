package test;
import dao.ProductDao;
import model.product.Product;
import services.CloudinaryService;

import java.util.ArrayList;
import java.util.List;

public class MainTest {
    public static void main(String[] args) {
        ProductDao productDao = new ProductDao();
        CloudinaryService cloudService = new CloudinaryService();

        System.out.println("Đang upload ảnh lên Cloudinary, vui lòng chờ...");
        String localPath = "/Users/laiqua/Downloads/Nama.jpg";
        String imgUrl = cloudService.uploadImage(localPath);

        if (imgUrl != null) {
            System.out.println("Upload thành công! Link ảnh: " + imgUrl);
            List<Product> list = new ArrayList<>();
//            list.add(new Product(15, "Áo dài Cloudinary Test", 990000, imgUrl));
            System.out.println("Đã lưu sản phẩm vào Database!");
        } else {
            System.out.println("Lỗi upload ảnh, không thể lưu vào DB.");
        }
    }
}