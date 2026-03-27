package catcafedefense.model;

// CafeBase ใช้แทนฐานของร้านคาเฟ่ในเกม
public class CafeBase {

    // เก็บค่าพลังชีวิตของฐาน
    private int hp;

    // constructor ของ CafeBase
    public CafeBase(int hp) {
        this.hp = hp; // กำหนดค่า HP เริ่มต้นของฐาน
    }

    // ลดพลังชีวิตของฐานเมื่อโดนโจมตี
    public void takeDamage(int damage) {
        hp -= damage;
        if (hp < 0) {
            hp = 0;
        }
    }

    // คืนค่าพลังชีวิตปัจจุบันของฐาน
    public int getHp() {
        return hp;
    }
}