package catcafedefense.ui;

import catcafedefense.model.CatTower;      // คลาสแม่ของแมวป้องกันฐาน
import catcafedefense.model.ChubbyCat;     // แมวสายโจมตี/ความสามารถแบบ ChubbyCat
import catcafedefense.model.Enemy;         // คลาสแม่ของศัตรู
import catcafedefense.model.OrangeCat;     // แมว OrangeCat
import catcafedefense.model.RatThief;      // ศัตรูประเภทหนูขโมย
import catcafedefense.util.ResourceLoader; // ใช้โหลดรูปภาพจาก resources

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import java.awt.Image;
import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import javax.swing.Timer;

// GamePanel คือหน้าจอหลักตอนเล่นเกม
// สืบทอดจาก JPanel และรับ event เมาส์ด้วย MouseListener
public class GamePanel extends JPanel implements MouseListener {

    // ==================== CONSTANTS ====================

    // ขนาดหน้าต่างเกม
    private static final int WIDTH = 1080;
    private static final int HEIGHT = 720;

    // ค่าตำแหน่งเริ่มต้นของกระดาน และขนาดแต่ละช่อง
    private static final int GRID_X = 170;
    private static final int GRID_Y = 245;
    private static final int CELL_W = 120;
    private static final int CELL_H = 115;
    private static final int ROWS = 3; // จำนวนเลน
    private static final int COLS = 7; // จำนวนคอลัมน์

    // ระยะเยื้องของเลน และความสูงจริงของพื้นที่วิ่งในแต่ละเลน
    private static final int LANE_OFFSET_Y = 12;
    private static final int LANE_HEIGHT = 58;

    // ขนาดรูปของแมว
    private static final int TOWER_W = 70;
    private static final int TOWER_H = 70;

    // ขนาดรูปของศัตรู
    private static final int ENEMY_W = 62;
    private static final int ENEMY_H = 62;

    // ==================== GAME STATE ====================

    private enum GameState {
        PREPARE,     // ช่วงวางแมวก่อนเริ่มด่าน
        RUNNING,     // ด่านกำลังเล่น
        WAVE_CLEAR,  // ผ่านด่าน
        GAME_OVER,   // แพ้
        GAME_WIN     // ชนะทั้งเกม
    }

    // เก็บแมวทั้งหมดที่ผู้เล่นวางไว้
    private final List<CatTower> towers = new ArrayList<>();

    // เก็บศัตรูที่อยู่ในฉาก
    private final List<Enemy> enemies = new ArrayList<>();

    // เก็บข้อความ log การกระทำต่าง ๆ ในเกม
    private final List<String> logs = new ArrayList<>();

    // เก็บแมวที่กำลังโจมตีในเทิร์นนั้น
    private final List<CatTower> attackingTowers = new ArrayList<>();

    // เก็บข้อมูลเอฟเฟกต์โดนโจมตี
    private final List<Object[]> hitEffects = new ArrayList<>();

    // ตัวแปรสถานะพื้นฐานของเกม
    private int turn = 1;     // เทิร์นปัจจุบัน
    private int coins = 20;   // เงินเริ่มต้น
    private int baseHp = 3;   // เลือดฐาน

    // สถานะปัจจุบันของเกม
    private GameState gameState = GameState.PREPARE;

    // ข้อมูลเกี่ยวกับ wave
    private int currentWave = 1;       // wave ปัจจุบัน
    private final int totalWaves = 3;  // จำนวน wave ทั้งหมด

    // ตัวแปรใช้ควบคุมการปล่อยศัตรู
    private int enemiesToSpawn = 0;       // จำนวนศัตรูที่ต้องปล่อยใน wave นี้
    private int enemiesSpawned = 0;       // จำนวนศัตรูที่ปล่อยไปแล้ว
    private int spawnCounter = 0;         // ตัวนับเวลาเพื่อใช้ปล่อยศัตรู
    private int currentSpawnInterval = 2; // ระยะห่างในการปล่อยศัตรู

    // Timer สำหรับอัปเดตเกมทุกช่วงเวลา
    private Timer gameTimer;

    // ==================== UI COMPONENTS ====================

    // ปุ่มเลือกแมวและเริ่มด่าน
    private JButton orangeButton;
    private JButton chubbyButton;
    private JButton waveButton;

    // ==================== UI DATA ====================

    // เก็บชื่อแมวที่ผู้เล่นเลือกอยู่ตอนนี้
    private String selectedCat = null;

    // ข้อความในกล่อง overlay ตอนผ่านด่าน/แพ้/ชนะ
    private String overlayTitle = "";
    private String overlayMessage = "";
    private String overlayButtonText = "";

    // ==================== IMAGES / RESOURCES ====================

    // รูปภาพต่าง ๆ ที่ใช้ในเกม
    private final Image backgroundImage;
    private final Image cafeImage;
    private final Image cakeImage;
    private final Image logo2Image;
    private final Image clockIcon;
    private final Image coinIcon;
    private final Image heartIcon;
    private final Image orangeHitImage;
    private final Image chubbyHitImage;

    // ==================== CONSTRUCTOR ====================

    // constructor ของ GamePanel
    public GamePanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT)); // กำหนดขนาด panel
        setBackground(new Color(255, 240, 246));        // กำหนดสีพื้นหลัง
        setLayout(null);                                // ใช้จัดวางตำแหน่งเองแบบ absolute

        // โหลดรูปภาพจากโฟลเดอร์ resources
        backgroundImage = ResourceLoader.loadImage("/resources/background.png");
        cafeImage = ResourceLoader.loadImage("/resources/cafe.png");
        cakeImage = ResourceLoader.loadImage("/resources/cake.png");
        logo2Image = ResourceLoader.loadImage("/resources/logo2.png");
        clockIcon = ResourceLoader.loadImage("/resources/clock.png");
        coinIcon = ResourceLoader.loadImage("/resources/coin.png");
        heartIcon = ResourceLoader.loadImage("/resources/heart.png");
        orangeHitImage = ResourceLoader.loadImage("/resources/orangeHit.png");
        chubbyHitImage = ResourceLoader.loadImage("/resources/chubbyHit.png");

        addMouseListener(this); // ให้ panel นี้รับการคลิกเมาส์
        setupButtons();         // สร้างปุ่มต่าง ๆ

        // ข้อความเริ่มต้นใน log
        logs.add("Welcome to Cat Café Defense!");
        logs.add("Choose a cat and click a lane tile.");
    }

    // ==================== SETUP METHODS ====================

    // เมธอดสร้างและตั้งค่าปุ่มทั้งหมด
    private void setupButtons() {
        orangeButton = new JButton("OrangeCat      : 10");
        chubbyButton = new JButton("ChubbyCat      : 15");
        waveButton = new JButton("Start Wave");

        // ย่อรูปเหรียญแล้วนำไปใช้เป็น icon บนปุ่ม
        Image scaledCoin = coinIcon.getScaledInstance(18, 18, Image.SCALE_SMOOTH);
        ImageIcon coinButtonIcon = new ImageIcon(scaledCoin);

        orangeButton.setIcon(coinButtonIcon);
        chubbyButton.setIcon(coinButtonIcon);

        // ให้ข้อความอยู่ด้านซ้ายของ icon
        orangeButton.setHorizontalTextPosition(JButton.LEFT);
        chubbyButton.setHorizontalTextPosition(JButton.LEFT);

        // ระยะห่างระหว่าง icon กับข้อความ
        orangeButton.setIconTextGap(8);
        chubbyButton.setIconTextGap(8);

        // กำหนดตำแหน่งและขนาดปุ่ม
        orangeButton.setBounds(600, 590, 240, 42);
        chubbyButton.setBounds(600, 642, 240, 42);
        waveButton.setBounds(870, 590, 160, 90);

        // กำหนดฟอนต์
        Font buttonFont = new Font("Comic Sans MS", Font.BOLD, 16);
        orangeButton.setFont(buttonFont);
        chubbyButton.setFont(buttonFont);
        waveButton.setFont(new Font("Comic Sans MS", Font.BOLD, 17));

        // กำหนดสีพื้นหลังปุ่ม
        orangeButton.setBackground(new Color(244, 216, 167));
        chubbyButton.setBackground(new Color(244, 216, 167));
        waveButton.setBackground(new Color(245, 190, 215));

        // กำหนดสีข้อความ
        orangeButton.setForeground(new Color(92, 63, 54));
        chubbyButton.setForeground(new Color(92, 63, 54));
        waveButton.setForeground(new Color(92, 63, 54));

        // ปิดเส้น focus ตอนคลิกปุ่ม
        orangeButton.setFocusPainted(false);
        chubbyButton.setFocusPainted(false);
        waveButton.setFocusPainted(false);

        // กำหนดเส้นขอบปุ่ม
        orangeButton.setBorder(BorderFactory.createLineBorder(new Color(220, 190, 170), 2));
        chubbyButton.setBorder(BorderFactory.createLineBorder(new Color(220, 190, 170), 2));
        waveButton.setBorder(BorderFactory.createLineBorder(new Color(245, 190, 215), 2));

        // เมื่อกดปุ่ม OrangeCat ให้เลือกแมวชนิดนี้
        orangeButton.addActionListener(e -> {
            selectedCat = "OrangeCat";
            addLog("Selected OrangeCat");
            repaint();
        });

        // เมื่อกดปุ่ม ChubbyCat ให้เลือกแมวชนิดนี้
        chubbyButton.addActionListener(e -> {
            selectedCat = "ChubbyCat";
            addLog("Selected ChubbyCat");
            repaint();
        });

        // เมื่อกดปุ่ม Start Wave ให้เริ่มด่าน
        waveButton.addActionListener(e -> {
            startWave();
        });

        // เพิ่มปุ่มลงบน panel
        add(orangeButton);
        add(chubbyButton);
        add(waveButton);
    }

    // เพิ่มข้อความลง log โดยให้ข้อความใหม่อยู่บนสุด
    private void addLog(String message) {
        logs.add(0, message);
        if (logs.size() > 6) {
            logs.remove(logs.size() - 1); // เก็บ log ไว้ไม่เกิน 6 บรรทัด
        }
    }

    // แสดง overlay กลางหน้าจอ
    private void showOverlay(String title, String message, String buttonText) {
        overlayTitle = title;
        overlayMessage = message;
        overlayButtonText = buttonText;
        repaint();
    }

    // ==================== GAME FLOW ====================

    // ไปยัง wave ถัดไป
    private void goToNextWave() {
        coins += 10;      // โบนัสผ่านด่าน
        currentWave++;    // เพิ่มเลข wave

        // ล้างข้อมูลในฉาก
        towers.clear();
        enemies.clear();
        attackingTowers.clear();
        hitEffects.clear();
        selectedCat = null;

        gameState = GameState.PREPARE; // กลับไปโหมดเตรียมตัว

        // เปิดปุ่มให้ใช้งานได้อีกครั้ง
        orangeButton.setEnabled(true);
        chubbyButton.setEnabled(true);
        waveButton.setEnabled(true);

        // ล้าง overlay
        overlayTitle = "";
        overlayMessage = "";
        overlayButtonText = "";

        addLog("+10 coins bonus! Total coins: " + coins);
        addLog("Prepare for Wave " + currentWave);
        repaint();
    }

    // เริ่มเกมใหม่ทั้งหมด
    private void restartGame() {
        currentWave = 1;
        turn = 1;
        coins = 20;
        baseHp = 3;

        // ล้างข้อมูลทั้งหมด
        towers.clear();
        enemies.clear();
        attackingTowers.clear();
        hitEffects.clear();
        selectedCat = null;

        enemiesToSpawn = 0;
        enemiesSpawned = 0;
        spawnCounter = 0;

        gameState = GameState.PREPARE;

        // เปิดปุ่มทั้งหมด
        orangeButton.setEnabled(true);
        chubbyButton.setEnabled(true);
        waveButton.setEnabled(true);

        // ล้าง overlay
        overlayTitle = "";
        overlayMessage = "";
        overlayButtonText = "";

        // รีเซ็ต log
        logs.clear();
        logs.add("Welcome to Cat Café Defense!");
        logs.add("Choose a cat and click a lane tile.");

        repaint();
    }

    // เริ่ม wave
    private void startWave() {
        if (gameState != GameState.PREPARE) {
            return; // เริ่มได้เฉพาะตอนอยู่สถานะเตรียมตัว
        }

        if (towers.isEmpty()) {
            addLog("Place at least one cat before starting the wave.");
            repaint();
            return;
        }

        // รีเซ็ตค่าการ spawn
        enemiesToSpawn = getEnemiesForWave(currentWave);
        enemiesSpawned = 0;
        spawnCounter = 0;
        turn = 0;

        // wave 3 ลด HP ฐานให้ยากขึ้น
        if (currentWave == 3) {
            baseHp = 2;
        } else {
            baseHp = 3;
        }

        currentSpawnInterval = getSpawnIntervalForWave(currentWave);
        gameState = GameState.RUNNING; // เปลี่ยนสถานะเป็นกำลังเล่น

        // ปิดปุ่มระหว่างเล่น
        orangeButton.setEnabled(false);
        chubbyButton.setEnabled(false);
        waveButton.setEnabled(false);

        addLog("Wave " + currentWave + " started!");

        // ถ้ามี timer เดิมอยู่ให้หยุดก่อน
        if (gameTimer != null && gameTimer.isRunning()) {
            gameTimer.stop();
        }

        // สร้าง timer ให้เรียก updateWave ทุก 1 วินาที
        gameTimer = new Timer(1000, e -> updateWave());
        gameTimer.start();

        repaint();
    }

    // อัปเดตเกมในแต่ละเทิร์น
    private void updateWave() {
        if (gameState != GameState.RUNNING) {
            return;
        }

        attackingTowers.clear(); // ล้างข้อมูลแมวที่โจมตีในเทิร์นก่อน
        hitEffects.clear();      // ล้างเอฟเฟกต์ก่อนหน้า

        turn++;

        // 1) แมวโจมตี
        for (CatTower tower : towers) {
            Enemy target = findTargetForTower(tower); // หาเป้าหมายของแมวตัวนี้

            if (target != null) {
                attackingTowers.add(tower);

                // คำนวณตำแหน่งเอฟเฟกต์โดนตี
                int targetX = GRID_X + target.getCol() * CELL_W + (CELL_W - ENEMY_W) / 2 + 20;
                int targetY = GRID_Y + target.getRow() * CELL_H + LANE_OFFSET_Y + (LANE_HEIGHT - ENEMY_H) / 2 + 18;

                hitEffects.add(new Object[]{tower, targetX, targetY});
                tower.attack(enemies); // ให้แมวโจมตีศัตรู
            }
        }

        // 2) ลบศัตรูที่ตายแล้ว และให้เงินรางวัล
        Iterator<Enemy> deadIterator = enemies.iterator();
        while (deadIterator.hasNext()) {
            Enemy enemy = deadIterator.next();
            if (enemy.isDead()) {
                deadIterator.remove();
                coins += 5;
                addLog("RatThief defeated! +5 coins");
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
                baseHp--;
                addLog("A RatThief reached the café! HP -1");
            }
        }

        // 5) ปล่อยศัตรูใหม่ตามรอบเวลา
        spawnCounter++;
        if (spawnCounter >= currentSpawnInterval && enemiesSpawned < enemiesToSpawn) {
            spawnEnemy();
            spawnCounter = 0;
        }

        // 6) เช็กเงื่อนไขแพ้
        if (baseHp <= 0) {
            gameTimer.stop();
            gameState = GameState.GAME_OVER;
            addLog("Game Over!");
            showOverlay("Game Over", "The rats stole the cakes!", "Play Again");
            return;
        }

        // 7) เช็กเงื่อนไขผ่านด่าน
        if (enemiesSpawned >= enemiesToSpawn && enemies.isEmpty()) {
            gameTimer.stop();
            addLog("Wave " + currentWave + " clear!");

            if (currentWave >= totalWaves) {
                coins += 10;
                gameState = GameState.GAME_WIN;
                showOverlay("You Win!", "Final bonus +10 coins! Total: " + coins, "Play Again");
            } else {
                gameState = GameState.WAVE_CLEAR;
                showOverlay("Wave " + currentWave + " Clear!", "Press Next to claim +10 coins", "Next");
            }
            return;
        }

        // อัปเดตสถานะเอฟเฟกต์โดนตีของศัตรู
        for (Enemy enemy : enemies) {
            enemy.updateHitEffect();
        }

        repaint();
    }

    // ==================== GAME LOGIC ====================

    // สร้างศัตรูใหม่ในเลนต่าง ๆ
    private void spawnEnemy() {
        if (enemiesSpawned >= enemiesToSpawn) {
            return; // ถ้าปล่อยครบแล้วไม่ต้องทำอะไร
        }

        int lane = enemiesSpawned % ROWS; // สลับเลนไปเรื่อย ๆ
        enemies.add(new RatThief(lane, COLS - 1, 5)); // ปล่อยหนูที่คอลัมน์ขวาสุด HP 5
        enemiesSpawned++;

        addLog("RatThief appears in Lane " + (lane + 1));
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
        return null; // ไม่พบเป้าหมาย
    }

    // ตรวจว่าช่องที่คลิกมีแมวอยู่แล้วหรือยัง
    private boolean hasTowerAt(int row, int col) {
        for (CatTower tower : towers) {
            if (tower.getRow() == row && tower.getCol() == col) {
                return true;
            }
        }
        return false;
    }

    // ==================== RENDERING ====================

    // เมธอดวาดหน้าจอทั้งหมด
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // ล้างพื้นหลังเดิมก่อนวาดใหม่
        Graphics2D g2 = (Graphics2D) g;

        // เปิด anti-aliasing ให้ภาพดูเนียนขึ้น
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // วาดพื้นหลัง
        g2.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);

        // วาดองค์ประกอบต่าง ๆ บนหน้าจอ
        drawTopBar(g2);
        drawGrid(g2);
        drawTowers(g2);
        drawHitEffects(g2);
        drawEnemies(g2);
        drawStatusPanel(g2);
        drawLogPanel(g2);
        drawSelectedText(g2);
        drawOverlay(g2);
    }

    // วาดกล่อง overlay ตอนผ่านด่าน / แพ้ / ชนะ
    private void drawOverlay(Graphics2D g2) {
        if (gameState != GameState.WAVE_CLEAR &&
                gameState != GameState.GAME_OVER &&
                gameState != GameState.GAME_WIN) {
            return;
        }

        // วาดพื้นหลังสีดำโปร่งใสทับทั้งจอ
        g2.setColor(new Color(0, 0, 0, 90));
        g2.fillRect(0, 0, WIDTH, HEIGHT);

        int boxW = 360;
        int boxH = 220;
        int boxX = (WIDTH - boxW) / 2;
        int boxY = (HEIGHT - boxH) / 2;

        // วาดกล่องข้อความตรงกลาง
        g2.setColor(new Color(255, 248, 244, 245));
        g2.fillRoundRect(boxX, boxY, boxW, boxH, 30, 30);

        g2.setColor(new Color(230, 210, 205));
        g2.drawRoundRect(boxX, boxY, boxW, boxH, 30, 30);

        g2.setColor(new Color(92, 63, 54));

        // วาดหัวข้อ
        Font titleFont = new Font("Comic Sans MS", Font.BOLD, 28);
        g2.setFont(titleFont);
        java.awt.FontMetrics fmTitle = g2.getFontMetrics();
        int titleX = boxX + (boxW - fmTitle.stringWidth(overlayTitle)) / 2;
        int titleY = boxY + 65;
        g2.drawString(overlayTitle, titleX, titleY);

        // วาดข้อความรอง
        Font msgFont = new Font("Comic Sans MS", Font.PLAIN, 18);
        g2.setFont(msgFont);
        java.awt.FontMetrics fmMsg = g2.getFontMetrics();
        int msgX = boxX + (boxW - fmMsg.stringWidth(overlayMessage)) / 2;
        int msgY = boxY + 105;
        g2.drawString(overlayMessage, msgX, msgY);

        // วาดปุ่มใน overlay
        int btnW = 140;
        int btnH = 42;
        int btnX = boxX + (boxW - btnW) / 2;
        int btnY = boxY + 145;

        g2.setColor(new Color(245, 190, 215));
        g2.fillRoundRect(btnX, btnY, btnW, btnH, 20, 20);

        g2.setColor(new Color(220, 170, 195));
        g2.drawRoundRect(btnX, btnY, btnW, btnH, 20, 20);

        g2.setColor(new Color(92, 63, 54));
        Font btnFont = new Font("Comic Sans MS", Font.BOLD, 18);
        g2.setFont(btnFont);
        java.awt.FontMetrics fmBtn = g2.getFontMetrics();
        int btnTextX = btnX + (btnW - fmBtn.stringWidth(overlayButtonText)) / 2;
        int btnTextY = btnY + ((btnH - fmBtn.getHeight()) / 2) + fmBtn.getAscent();
        g2.drawString(overlayButtonText, btnTextX, btnTextY);
    }

    // วาดแถบบนของเกม
    private void drawTopBar(Graphics2D g2) {
        g2.setColor(new Color(255, 206, 226));
        g2.fillRect(0, 0, WIDTH, 70);

        // วาดโลโก้เกม
        g2.drawImage(logo2Image, 25, -65, 200, 200, this);

        // กล่องแสดง wave ปัจจุบัน
        int boxX = 860;
        int boxY = 14;
        int boxW = 150;
        int boxH = 40;

        g2.setColor(new Color(255, 248, 244, 220));
        g2.fillRoundRect(boxX, boxY, boxW, boxH, 20, 20);

        g2.setColor(new Color(230, 210, 205));
        g2.drawRoundRect(boxX, boxY, boxW, boxH, 20, 20);

        g2.setColor(new Color(92, 63, 54));
        g2.setFont(new Font("Comic Sans MS", Font.BOLD, 18));
        g2.drawString("Wave " + currentWave, boxX + 35, boxY + 25);
    }

    // วาดกระดานแต่ละเลน
    private void drawGrid(Graphics2D g2) {
        g2.setFont(new Font("Comic Sans MS", Font.BOLD, 20));

        for (int row = 0; row < ROWS; row++) {
            int laneY = GRID_Y + row * CELL_H;

            // วาดเค้กด้านซ้ายของแต่ละเลน
            g2.drawImage(cakeImage, 35, laneY - 20, 110, 110, this);

            // วาดเส้นแบ่งช่องในเลน
            g2.setColor(new Color(230, 180, 185, 90));
            for (int col = 1; col < COLS; col++) {
                int x = GRID_X + col * CELL_W;
                g2.drawLine(x, laneY + 14, x, laneY + 68);
            }

            // วาดกรอบรอบเลน
            g2.setColor(new Color(240, 200, 205, 120));
            g2.drawRoundRect(GRID_X, laneY + 12, CELL_W * COLS, 58, 30, 30);
        }
    }

    // วาดแมวที่ผู้เล่นวางไว้
    private void drawTowers(Graphics2D g2) {
        for (CatTower tower : towers) {
            int laneY = GRID_Y + tower.getRow() * CELL_H + LANE_OFFSET_Y;

            int x = GRID_X + tower.getCol() * CELL_W + (CELL_W - TOWER_W) / 2;
            int y = laneY + (LANE_HEIGHT - TOWER_H) / 2 - 20;

            tower.draw(g2, x, y); // เรียกเมธอดวาดของแมว

            // กำหนดชื่อที่จะแสดงบนแมว
            String label = (tower instanceof OrangeCat) ? "Orange" : "Chubby";

            // วาดพื้นหลังป้ายชื่อ
            g2.setColor(new Color(255, 248, 244, 230));
            g2.fillRoundRect(x + 8, y - 14, 54, 16, 10, 10);

            g2.setColor(new Color(230, 210, 205));
            g2.drawRoundRect(x + 8, y - 14, 54, 16, 10, 10);

            // วาดข้อความชื่อแมว
            g2.setFont(new Font("Comic Sans MS", Font.PLAIN, 10));
            g2.setColor(new Color(92, 63, 54));
            g2.drawString(label, x + 15, y - 3);
        }
    }

    // วาดเอฟเฟกต์ตอนแมวโจมตีโดนศัตรู
    private void drawHitEffects(Graphics2D g2) {
        if (gameState != GameState.RUNNING || hitEffects.isEmpty()) {
            return;
        }

        for (Object[] effect : hitEffects) {
            CatTower tower = (CatTower) effect[0];
            int targetX = (int) effect[1];
            int targetY = (int) effect[2];

            // เลือกรูปเอฟเฟกต์ตามชนิดของแมว
            Image hit = (tower instanceof OrangeCat) ? orangeHitImage : chubbyHitImage;

            g2.drawImage(hit, targetX - 12, targetY - 12, 28, 28, this);
        }
    }

    // วาดศัตรูทั้งหมด
    private void drawEnemies(Graphics2D g2) {
        for (Enemy enemy : enemies) {
            int laneY = GRID_Y + enemy.getRow() * CELL_H + LANE_OFFSET_Y;
            int x = GRID_X + enemy.getCol() * CELL_W + (CELL_W - ENEMY_W) / 2;
            int y = laneY + (LANE_HEIGHT - ENEMY_H) / 2 - 20;

            enemy.draw(g2, x, y); // วาดตัวศัตรู

            // ถ้าศัตรูเพิ่งโดนตี ให้แสดงเอฟเฟกต์เรือง ๆ
            if (enemy.isHit()) {
                g2.setColor(new Color(255, 170, 170, 40));
                g2.fillRoundRect(x + 6, y + 6, 44, 44, 14, 14);
            }

            // พื้นหลังหลอด HP
            g2.setColor(new Color(255, 255, 255, 220));
            g2.fillRoundRect(x + 8, y - 10, 40, 6, 6, 6);

            // หลอด HP ปัจจุบัน
            g2.setColor(new Color(255, 120, 120));
            int hpWidth = Math.max(0, enemy.getHp()) * 8; // HP เต็ม 5 = 40 พิกเซล
            g2.fillRoundRect(x + 8, y - 10, hpWidth, 6, 6, 6);

            // ขอบหลอด HP
            g2.setColor(new Color(120, 80, 80));
            g2.drawRoundRect(x + 8, y - 10, 40, 6, 6, 6);

            // ตัวเลข HP
            g2.setFont(new Font("Comic Sans MS", Font.PLAIN, 12));
            g2.setColor(new Color(92, 63, 54));
            g2.drawString(String.valueOf(enemy.getHp()), x + 52, y - 4);
        }
    }

    // วาดกล่องสถานะ Turn / Coins / HP
    private void drawStatusPanel(Graphics2D g2) {
        int x = 800;
        int y = 80;
        int w = 250;
        int h = 100;

        // พื้นกล่อง
        g2.setColor(new Color(255, 248, 244, 235));
        g2.fillRoundRect(x, y, w, h, 25, 25);

        // ขอบกล่อง
        g2.setColor(new Color(230, 210, 205));
        g2.drawRoundRect(x, y, w, h, 25, 25);

        int iconSize = 18;

        // แสดง Turn
        g2.drawImage(clockIcon, x + 18, y + 10, iconSize, iconSize, this);
        g2.setColor(new Color(92, 63, 54));
        g2.setFont(new Font("Comic Sans MS", Font.PLAIN, 16));
        g2.drawString("Turn   : " + turn, x + 45, y + 25);

        // แสดง Coins
        g2.drawImage(coinIcon, x + 18, y + 40, iconSize, iconSize, this);
        g2.drawString("Coins  : " + coins, x + 45, y + 55);

        // แสดง HP
        g2.drawImage(heartIcon, x + 18, y + 70, iconSize, iconSize, this);
        g2.drawString("HP     : " + baseHp, x + 45, y + 85);
    }

    // วาดกล่อง log ด้านล่าง
    private void drawLogPanel(Graphics2D g2) {
        int logX = 35;
        int logY = 570;
        int logW = 540;
        int logH = 120;

        g2.setColor(new Color(255, 248, 244, 235));
        g2.fillRoundRect(logX, logY, logW, logH, 25, 25);

        g2.setColor(new Color(230, 210, 205));
        g2.drawRoundRect(logX, logY, logW, logH, 25, 25);

        g2.setColor(new Color(164, 114, 94));
        g2.setFont(new Font("Comic Sans MS", Font.BOLD, 24));
        g2.drawString("Action Log", logX + 20, logY + 38);

        // แสดงข้อความ log ทีละบรรทัด
        g2.setFont(new Font("Comic Sans MS", Font.PLAIN, 16));
        int y = logY + 72;
        for (String log : logs) {
            g2.drawString(log, logX + 20, y);
            y += 22;
            if (y > logY + logH - 10) {
                break;
            }
        }
    }

    // วาดข้อความบอกว่าเลือกแมวตัวไหนอยู่
    private void drawSelectedText(Graphics2D g2) {
        if (selectedCat != null) {
            g2.setColor(new Color(92, 63, 54));
            g2.setFont(new Font("Comic Sans MS", Font.PLAIN,14));
            g2.drawString("Selected: " + selectedCat, 650, 575);
        }
    }

    // ==================== EVENT HANDLING ====================

    // จัดการเมื่อผู้ใช้คลิกเมาส์
    @Override
    public void mouseClicked(MouseEvent e) {
        int mouseX = e.getX();
        int mouseY = e.getY();

        // ถ้าอยู่ในสถานะ overlay ให้เช็กว่าคลิกปุ่มใน overlay หรือไม่
        if (gameState == GameState.WAVE_CLEAR ||
                gameState == GameState.GAME_OVER ||
                gameState == GameState.GAME_WIN) {

            int boxW = 360;
            int boxH = 220;
            int boxX = (WIDTH - boxW) / 2;
            int boxY = (HEIGHT - boxH) / 2;

            int btnW = 140;
            int btnH = 42;
            int btnX = boxX + (boxW - btnW) / 2;
            int btnY = boxY + 145;

            if (mouseX >= btnX && mouseX <= btnX + btnW &&
                    mouseY >= btnY && mouseY <= btnY + btnH) {

                // ถ้าผ่าน wave ให้ไปด่านถัดไป ถ้าแพ้/ชนะให้เริ่มเกมใหม่
                if (gameState == GameState.WAVE_CLEAR) {
                    goToNextWave();
                } else {
                    restartGame();
                }
            }
            return;
        }

        // ถ้าไม่ใช่ช่วงเตรียมตัว ห้ามวางแมว
        if (gameState != GameState.PREPARE) {
            addLog("You can place cats only before starting the wave.");
            repaint();
            return;
        }

        // ถ้าคลิกนอกกระดานด้านซ้ายบน ไม่ต้องทำอะไร
        if (mouseX < GRID_X || mouseY < GRID_Y) {
            return;
        }

        // แปลงตำแหน่งเมาส์เป็น row / col ของกระดาน
        int col = (mouseX - GRID_X) / CELL_W;
        int row = (mouseY - GRID_Y) / CELL_H;

        // ถ้าเกินขอบกระดาน ไม่ต้องทำอะไร
        if (row < 0 || row >= ROWS || col < 0 || col >= COLS) {
            return;
        }

        // อนุญาตให้วางแมวเฉพาะฝั่งซ้าย
        if (col > 2) {
            addLog("Place cats only on the left side.");
            repaint();
            return;
        }

        // ถ้าช่องนี้มีแมวอยู่แล้ว ห้ามวางซ้ำ
        if (hasTowerAt(row, col)) {
            addLog("This tile already has a cat.");
            repaint();
            return;
        }

        // ถ้าเลือก OrangeCat อยู่
        if ("OrangeCat".equals(selectedCat)) {
            int cost = 10;
            if (coins < cost) {
                addLog("Not enough coins for OrangeCat!");
                repaint();
                return;
            }

            coins -= cost;
            towers.add(new OrangeCat(row, col));
            addLog("OrangeCat placed in Lane " + (row + 1) + " (-10 coins, left: " + coins + ")");
            selectedCat = null;

            // ถ้าเลือก ChubbyCat อยู่
        } else if ("ChubbyCat".equals(selectedCat)) {
            int cost = 15;
            if (coins < cost) {
                addLog("Not enough coins for ChubbyCat!");
                repaint();
                return;
            }

            coins -= cost;
            towers.add(new ChubbyCat(row, col));

            addLog("ChubbyCat placed in Lane " + (row + 1) + " (-15 coins, left: " + coins + ")");
            selectedCat = null;
        }

        repaint();
    }

    // เมธอดจาก MouseListener ที่ยังไม่ได้ใช้งาน
    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }
}