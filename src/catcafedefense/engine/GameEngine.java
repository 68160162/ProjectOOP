package catcafedefense.engine;

import catcafedefense.model.*;
import catcafedefense.util.GameLog;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

// GameEngine ใช้จัดการข้อมูลและการทำงานหลักของเกม
public class GameEngine {

    private static final int ROWS = 3;
    private static final int COLS = 7;
    private static final int TOTAL_WAVES = 3;

    // เก็บแมวทั้งหมดที่ผู้เล่นวางไว้
    private final List<CatTower> cats;

    // เก็บศัตรูทั้งหมดที่อยู่ในฉาก
    private final List<Enemy> enemies;

    // เก็บข้อมูลฐานของร้านคาเฟ่
    private CafeBase base;

    // ใช้เก็บข้อความ log เหตุการณ์ต่าง ๆ ในเกม
    private final GameLog log;

    // เก็บแมวที่กำลังโจมตีในเทิร์นนั้น
    private final List<CatTower> attackingCats;

    // เก็บข้อมูลเอฟเฟกต์โดนโจมตี
    private final List<Object[]> hitEffects;

    // เก็บจำนวนเหรียญของผู้เล่น
    private int coins;

    // เก็บหมายเลข wave ปัจจุบัน
    private int currentWave;

    // เก็บเทิร์นปัจจุบัน
    private int turn;

    // ตัวแปรใช้ควบคุมการปล่อยศัตรู
    private int enemiesToSpawn;
    private int enemiesSpawned;
    private int spawnCounter;
    private int currentSpawnInterval;

    // constructor ของ GameEngine
    public GameEngine() {
        cats = new ArrayList<>();
        enemies = new ArrayList<>();
        log = new GameLog();
        attackingCats = new ArrayList<>();
        hitEffects = new ArrayList<>();

        resetAll();

        log.addLog("Welcome to Cat Café Defense!");
        log.addLog("Choose a cat and click a lane tile.");
    }

    // เริ่ม wave
    public boolean startWave() {
        if (cats.isEmpty()) {
            log.addLog("Place at least one cat before starting the wave.");
            return false;
        }

        enemiesToSpawn = getEnemiesForWave(currentWave);
        enemiesSpawned = 0;
        spawnCounter = 0;
        turn = 0;

        // wave 3 ลด HP ฐานให้ยากขึ้น
        if (currentWave == 3) {
            base = new CafeBase(2);
        } else {
            base = new CafeBase(3);
        }

        currentSpawnInterval = getSpawnIntervalForWave(currentWave);
        log.addLog("Wave " + currentWave + " started!");

        return true;
    }

    // อัปเดตเกมในแต่ละเทิร์น
    public WaveUpdateResult updateWave() {
        attackingCats.clear();
        hitEffects.clear();

        turn++;

        // 1) แมวโจมตี
        for (CatTower tower : cats) {
            Enemy target = findTargetForTower(tower);

            if (target != null) {
                attackingCats.add(tower);
                hitEffects.add(new Object[]{tower, target});
                tower.attack(enemies);
            }
        }

        // 2) ลบศัตรูที่ตายแล้ว และให้เงินรางวัล
        Iterator<Enemy> deadIterator = enemies.iterator();
        while (deadIterator.hasNext()) {
            Enemy enemy = deadIterator.next();
            if (enemy.isDead()) {
                deadIterator.remove();
                coins += 5;
                log.addLog("RatThief defeated! +5 coins");
            }
        }

        // 3) ให้ศัตรูเดินเข้าหาฐาน
        for (Enemy enemy : enemies) {
            enemy.move();
        }

        // 4) ตรวจว่าศัตรูถึงฐานหรือยัง
        Iterator<Enemy> baseIterator = enemies.iterator();
        while (baseIterator.hasNext()) {
            Enemy enemy = baseIterator.next();
            if (enemy.getCol() < 0) {
                baseIterator.remove();
                base.takeDamage(1);
                log.addLog("A RatThief reached the café! HP -1");
            }
        }

        // 5) ปล่อยศัตรูใหม่ตามรอบเวลา
        spawnCounter++;
        if (spawnCounter >= currentSpawnInterval && enemiesSpawned < enemiesToSpawn) {
            spawnEnemy();
            spawnCounter = 0;
        }

        // 6) เช็กเงื่อนไขแพ้
        if (base.getHp() <= 0) {
            log.addLog("Game Over!");
            return WaveUpdateResult.GAME_OVER;
        }

        // 7) เช็กเงื่อนไขผ่านด่าน
        if (enemiesSpawned >= enemiesToSpawn && enemies.isEmpty()) {
            log.addLog("Wave " + currentWave + " clear!");

            if (currentWave >= TOTAL_WAVES) {
                coins += 10;
                return WaveUpdateResult.GAME_WIN;
            } else {
                return WaveUpdateResult.WAVE_CLEAR;
            }
        }

        // อัปเดตสถานะเอฟเฟกต์โดนตีของศัตรู
        for (Enemy enemy : enemies) {
            enemy.updateHitEffect();
        }

        return WaveUpdateResult.RUNNING;
    }

    // ไปยัง wave ถัดไป
    public void goToNextWave() {
        coins += 10;
        currentWave++;

        cats.clear();
        enemies.clear();
        attackingCats.clear();
        hitEffects.clear();

        enemiesToSpawn = 0;
        enemiesSpawned = 0;
        spawnCounter = 0;
        currentSpawnInterval = 2;

        log.addLog("+10 coins bonus! Total coins: " + coins);
        log.addLog("Prepare for Wave " + currentWave);
    }

    // เริ่มเกมใหม่ทั้งหมด
    public void restartGame() {
        resetAll();

        log.getLogs().clear();
        log.addLog("Welcome to Cat Café Defense!");
        log.addLog("Choose a cat and click a lane tile.");
    }

    private void resetAll() {
        cats.clear();
        enemies.clear();
        attackingCats.clear();
        hitEffects.clear();

        base = new CafeBase(3);
        coins = 20;
        currentWave = 1;
        turn = 1;

        enemiesToSpawn = 0;
        enemiesSpawned = 0;
        spawnCounter = 0;
        currentSpawnInterval = 2;
    }

    // วางแมวลงในกระดาน
    public boolean placeCat(String selectedCat, int row, int col) {
        if (hasTowerAt(row, col)) {
            log.addLog("This tile already has a cat.");
            return false;
        }

        if ("OrangeCat".equals(selectedCat)) {
            CatTower cat = new OrangeCat(row, col);
            if (coins < cat.getCost()) {
                log.addLog("Not enough coins for OrangeCat!");
                return false;
            }

            coins -= cat.getCost();
            cats.add(cat);
            log.addLog("OrangeCat placed in Lane " + (row + 1) + " (-10 coins, left: " + coins + ")");
            return true;
        }

        if ("ChubbyCat".equals(selectedCat)) {
            CatTower cat = new ChubbyCat(row, col);
            if (coins < cat.getCost()) {
                log.addLog("Not enough coins for ChubbyCat!");
                return false;
            }

            coins -= cat.getCost();
            cats.add(cat);
            log.addLog("ChubbyCat placed in Lane " + (row + 1) + " (-15 coins, left: " + coins + ")");
            return true;
        }

        return false;
    }

    // สร้างศัตรูใหม่ในเลนต่าง ๆ
    private void spawnEnemy() {
        if (enemiesSpawned >= enemiesToSpawn) {
            return;
        }

        int lane = enemiesSpawned % ROWS;
        enemies.add(new RatThief(lane, COLS - 1, 5));
        enemiesSpawned++;

        log.addLog("RatThief appears in Lane " + (lane + 1));
    }

    // กำหนดจำนวนศัตรูในแต่ละ wave
    private int getEnemiesForWave(int wave) {
        switch (wave) {
            case 1: return 2;
            case 2: return 3;
            case 3: return 5;
            default: return 0;
        }
    }

    // กำหนดความถี่ในการปล่อยศัตรูแต่ละ wave
    private int getSpawnIntervalForWave(int wave) {
        switch (wave) {
            case 1: return 2;
            case 2: return 2;
            case 3: return 1;
            default: return 2;
        }
    }

    // หาเป้าหมายให้แมว โดยดูจากศัตรูในเลนเดียวกันและอยู่ในระยะยิง
    private Enemy findTargetForTower(CatTower tower) {
        for (Enemy enemy : enemies) {
            if (enemy.getRow() == tower.getRow()) {
                int distance = enemy.getCol() - tower.getCol();
                if (distance >= 0 && distance <= tower.getRange()) {
                    return enemy;
                }
            }
        }
        return null;
    }

    // ตรวจว่าช่องที่คลิกมีแมวอยู่แล้วหรือยัง
    public boolean hasTowerAt(int row, int col) {
        for (CatTower tower : cats) {
            if (tower.getRow() == row && tower.getCol() == col) {
                return true;
            }
        }
        return false;
    }

    public List<CatTower> getCats() {
        return cats;
    }

    public List<Enemy> getEnemies() {
        return enemies;
    }

    public CafeBase getBase() {
        return base;
    }

    public GameLog getLog() {
        return log;
    }

    public List<CatTower> getAttackingCats() {
        return attackingCats;
    }

    public List<Object[]> getHitEffects() {
        return hitEffects;
    }

    public int getCoins() {
        return coins;
    }

    public int getCurrentWave() {
        return currentWave;
    }

    public int getTurn() {
        return turn;
    }

    public int getTotalWaves() {
        return TOTAL_WAVES;
    }

    public enum WaveUpdateResult {
        RUNNING,
        WAVE_CLEAR,
        GAME_OVER,
        GAME_WIN
    }
}