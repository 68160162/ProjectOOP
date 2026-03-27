package catcafedefense.util;

import javax.swing.ImageIcon;
import java.awt.Image;
import java.net.URL;

// ResourceLoader ใช้สำหรับโหลดไฟล์ resource ต่าง ๆ เช่น icon และ image
public class ResourceLoader {

    // เมธอดสำหรับโหลดไฟล์เป็น ImageIcon
    public static ImageIcon loadIcon(String path) {
        URL url = ResourceLoader.class.getResource(path); // ค้นหาไฟล์ resource ตาม path ที่ระบุ

        // ถ้าไม่พบไฟล์ ให้แจ้ง error ออกมา
        if (url == null) {
            throw new IllegalArgumentException("ไม่พบ resource: " + path);
        }

        return new ImageIcon(url); // สร้างและคืนค่าเป็น ImageIcon
    }

    // เมธอดสำหรับโหลดไฟล์เป็น Image
    public static Image loadImage(String path) {
        return loadIcon(path).getImage(); // โหลด icon ก่อน แล้วดึงออกมาเป็น Image
    }
}