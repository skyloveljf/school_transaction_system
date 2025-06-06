import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

public class ImageBase64Test {
    
    public static void main(String[] args) {
        String uploadPath = "./uploads/images";
        String imagePath = "/uploads/images/cf67fa47-4fe5-48f0-9ad6-b017c1d36247.jpg";
        
        try {
            // 构建文件路径
            String fileName = imagePath.substring("/uploads/images/".length());
            Path filePath = Paths.get(uploadPath, fileName);
            
            File imageFile = filePath.toFile();
            System.out.println("文件路径: " + filePath.toAbsolutePath());
            System.out.println("文件存在: " + imageFile.exists());
            System.out.println("文件大小: " + imageFile.length() + " bytes");
            
            if (imageFile.exists()) {
                // 读取文件并转换为Base64
                try (FileInputStream fileInputStream = new FileInputStream(imageFile)) {
                    byte[] fileBytes = fileInputStream.readAllBytes();
                    String base64String = Base64.getEncoder().encodeToString(fileBytes);
                    
                    String dataUrl = "data:image/jpeg;base64," + base64String;
                    System.out.println("Base64长度: " + base64String.length());
                    System.out.println("DataURL前100字符: " + dataUrl.substring(0, Math.min(100, dataUrl.length())));
                    System.out.println("Base64转换成功！");
                }
            }
        } catch (IOException e) {
            System.err.println("转换失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 