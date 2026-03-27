package catcafedefense.model;

import catcafedefense.util.ResourceLoader;

import java.awt.Graphics2D;
import java.awt.Image;

// RatThief เป็นศัตรูชนิดหนึ่งที่สืบทอดจาก Enemy
public class RatThief extends Enemy {

    // โหลดรูปของ RatThief จาก resources เก็บไว้ใช้ร่วมกันทั้งคลาส
    private static final Image RAT_IMAGE =
            ResourceLoader.loadImage("/resources/rat.png");

    // constructor ของ RatThief
    public RatThief(int row, int col, int hp) {
        super(row, col, hp, 1); // กำหนดความเร็วเริ่มต้นเป็น 1
    }

    // เมธอดวาดรูป RatThief ลงบนหน้าจอ
    @Override
    public void draw(Graphics2D g2, int x, int y) {
        g2.drawImage(RAT_IMAGE, x, y, 80, 80, null);
    }
}