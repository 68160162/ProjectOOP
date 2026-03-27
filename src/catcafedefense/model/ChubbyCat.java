package catcafedefense.model;

import catcafedefense.util.ResourceLoader;

import java.awt.Graphics2D;
import java.awt.Image;
import java.util.List;

// ChubbyCat เป็นแมวชนิดหนึ่งที่สืบทอดจาก CatTower
public class ChubbyCat extends CatTower {

    // โหลดรูปของ ChubbyCat จาก resources เก็บไว้ใช้ร่วมกันทั้งคลาส
    private static final Image CAT_IMAGE =
            ResourceLoader.loadImage("/resources/chubbycat.png");

    // constructor ของ ChubbyCat
    public ChubbyCat(int row, int col) {
        super(row, col, 4, 1, 15); // damage 4, range 1, cost 15
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

    // เมธอดวาดรูป ChubbyCat ลงบนหน้าจอ
    @Override
    public void draw(Graphics2D g2, int x, int y) {
        g2.drawImage(CAT_IMAGE, x, y, 80, 80, null);
    }
}