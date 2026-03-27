package catcafedefense.model;

import java.awt.Graphics2D;
import java.util.List;

// CatTower เป็นคลาสแม่แบบ abstract ของแมวทุกตัวในเกม
public abstract class CatTower {

    // เก็บตำแหน่งแถวของแมว
    private int row;

    // เก็บตำแหน่งคอลัมน์ของแมว
    private int col;

    // เก็บค่าพลังโจมตีของแมว
    private int damage;

    // เก็บระยะโจมตีของแมว
    private int range;

    // เก็บราคาของแมว
    private int cost;

    // constructor ของ CatTower
    public CatTower(int row, int col, int damage, int range, int cost) {
        this.row = row;         // กำหนดแถวของแมว
        this.col = col;         // กำหนดคอลัมน์ของแมว
        this.damage = damage;   // กำหนดค่าพลังโจมตี
        this.range = range;     // กำหนดระยะโจมตี
        this.cost = cost;       // กำหนดราคาของแมว
    }

    // เมธอด abstract สำหรับโจมตีศัตรู
    public abstract void attack(List<Enemy> enemies);

    // เมธอด abstract สำหรับวาดรูปแมวบนหน้าจอ
    public abstract void draw(Graphics2D g2, int x, int y);

    // คืนค่าตำแหน่งแถวของแมว
    public int getRow() {
        return row;
    }

    // คืนค่าตำแหน่งคอลัมน์ของแมว
    public int getCol() {
        return col;
    }

    // คืนค่าพลังโจมตีของแมว
    public int getDamage() {
        return damage;
    }

    // คืนค่าระยะโจมตีของแมว
    public int getRange() {
        return range;
    }

    // คืนค่าราคาของแมว
    public int getCost() {
        return cost;
    }
}