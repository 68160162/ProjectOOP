package catcafedefense.util;

import java.util.ArrayList;
import java.util.List;

// GameLog ใช้สำหรับเก็บข้อความเหตุการณ์ต่าง ๆ ที่เกิดขึ้นในเกม
public class GameLog {

    // เก็บรายการข้อความ log ทั้งหมด
    private final List<String> logs;

    // constructor ของ GameLog
    public GameLog() {
        logs = new ArrayList<>(); // สร้าง ArrayList สำหรับเก็บข้อความ log
    }

    // เพิ่มข้อความใหม่เข้าไปใน log
    public void addLog(String message) {
        logs.add(0, message); // เพิ่มข้อความใหม่ไว้ตำแหน่งบนสุด

        // ถ้ามีเกิน 8 บรรทัด ให้ลบข้อความเก่าสุดออก
        if (logs.size() > 8) {
            logs.remove(logs.size() - 1);
        }
    }

    // คืนค่ารายการ log ทั้งหมด
    public List<String> getLogs() {
        return logs;
    }
}