package catcafedefense.model;

import catcafedefense.util.ResourceLoader;

import java.awt.Graphics2D;
import java.awt.Image;
import java.util.List;

// OrangeCat เป็นแมวชนิดหนึ่งที่สืบทอดจาก CatTower
public class OrangeCat extends CatTower {

    // โหลดรูปของ OrangeCat จาก resources เก็บไว้ใช้ร่วมกันทั้งคลาส
    private static final Image CAT_IMAGE =
            ResourceLoader.loadImage("/resources/orangecat.png");

    // constructor ของ OrangeCat
    public OrangeCat(int row, int col) {
        super(row, col, 2, 3, 10); // damage 2, range 3, cost 10
    }

    // เมธอดโจมตีศัตรู
    @Override
    public void attack(List<Enemy> enemies) {
        for (Enemy enemy : enemies) {

            // โจมตีเฉพาะศัตรูที่อยู่ในเลนเดียวกัน
            if (enemy.getRow() == getRow()) {
                int distance = enemy.getCol() - getCol();

                // ถ้าอยู่ในระยะโจมตี ให้โจมตีแล้วหยุด
                if (distance >= 0 && distance <= getRange()) {
                    enemy.takeDamage(getDamage());
                    break;
                }
            }
        }
    }

    // เมธอดวาดรูป OrangeCat ลงบนหน้าจอ
    @Override
    public void draw(Graphics2D g2, int x, int y) {
        g2.drawImage(CAT_IMAGE, x, y, 80, 80, null);
    }
}