package catcafedefense.model;

import java.awt.Graphics2D;

// Enemy เป็นคลาสแม่แบบ abstract ของศัตรูทุกตัวในเกม
public abstract class Enemy {

    // เก็บตำแหน่งแถวของศัตรู
    private int row;

    // เก็บตำแหน่งคอลัมน์ของศัตรู
    private int col;

    // เก็บพลังชีวิตของศัตรู
    private int hp;

    // เก็บความเร็วในการเคลื่อนที่ของศัตรู
    private int speed;

    // ใช้นับจำนวนเทิร์นของเอฟเฟกต์โดนโจมตี
    private int hitEffectTicks = 0;

    // constructor ของ Enemy
    public Enemy(int row, int col, int hp, int speed) {
        this.row = row;       // กำหนดแถวของศัตรู
        this.col = col;       // กำหนดคอลัมน์ของศัตรู
        this.hp = hp;         // กำหนดพลังชีวิตเริ่มต้น
        this.speed = speed;   // กำหนดความเร็ว
    }

    // เมธอดสำหรับให้ศัตรูเดินไปทางซ้าย
    public void move() {
        col -= speed;
    }

    // เมธอดรับความเสียหายจากการโจมตี
    public void takeDamage(int damage) {
        hp -= damage;         // ลด HP ตามค่าความเสียหาย
        hitEffectTicks = 2;   // เปิดเอฟเฟกต์โดนตีไว้ชั่วคราว
    }

    // Overloading: เลือกได้ว่าจะให้แสดงเอฟเฟกต์หรือไม่
    public void takeDamage(int damage, boolean showEffect) {
        hp -= damage;         // ลด HP ตามค่าความเสียหาย
        if (showEffect) {
            hitEffectTicks = 2;
        }
    }

    // ตรวจว่าศัตรูกำลังอยู่ในสถานะโดนตีหรือไม่
    public boolean isHit() {
        return hitEffectTicks > 0;
    }

    // อัปเดตเอฟเฟกต์โดนตีให้ลดลงทีละเทิร์น
    public void updateHitEffect() {
        if (hitEffectTicks > 0) {
            hitEffectTicks--;
        }
    }

    // ตรวจว่าศัตรูตายแล้วหรือยัง
    public boolean isDead() {
        return hp <= 0;
    }

    // เมธอด abstract สำหรับวาดรูปศัตรู
    public abstract void draw(Graphics2D g2, int x, int y);

    // คืนค่าตำแหน่งแถวของศัตรู
    public int getRow() {
        return row;
    }

    // คืนค่าตำแหน่งคอลัมน์ของศัตรู
    public int getCol() {
        return col;
    }

    // คืนค่าพลังชีวิตปัจจุบัน
    public int getHp() {
        return hp;
    }

    // คืนค่าความเร็วของศัตรู
    public int getSpeed() {
        return speed;
    }
}