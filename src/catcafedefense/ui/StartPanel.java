package catcafedefense.ui;

import catcafedefense.util.ResourceLoader; // ใช้โหลดรูปภาพจากโฟลเดอร์ resources

import javax.swing.*;
import java.awt.*;

// StartPanel คือหน้าจอเริ่มต้นของเกม
// สืบทอดจาก JPanel เพื่อใช้เป็นหน้าจอหนึ่งในหน้าต่างหลัก
public class StartPanel extends JPanel {

    // เก็บรูปโลโก้และภาพพื้นหลังของหน้าเริ่มต้น
    private final Image logoImage;
    private final Image backgroundImage;

    // constructor ของ StartPanel
    public StartPanel(GameFrame frame) {

        setPreferredSize(new Dimension(1080, 720)); // กำหนดขนาด panel
        setLayout(null); // ใช้จัดวางตำแหน่งแบบกำหนดเอง

        // โหลดรูปภาพจาก resources
        logoImage = ResourceLoader.loadImage("/resources/logo.png");
        backgroundImage = ResourceLoader.loadImage("/resources/background2.png");

        // สร้างข้อความชื่อเกม
        JLabel title = new JLabel("Cat Café Defense");
        title.setFont(new Font("Comic Sans MS", Font.BOLD, 12)); // กำหนดฟอนต์
        title.setForeground(new Color(92, 63, 54)); // กำหนดสีข้อความ
        title.setBounds(490, 320, 500, 60); // กำหนดตำแหน่งและขนาด
        add(title); // เพิ่มข้อความลงบน panel

        // สร้างปุ่ม Play สำหรับเริ่มเกม
        JButton playButton = new JButton("Play");
        playButton.setFont(new Font("Comic Sans MS", Font.BOLD, 24));
        playButton.setBounds(430, 380, 220, 70);
        playButton.setBackground(new Color(245, 190, 215));
        playButton.setForeground(new Color(92, 63, 54));
        playButton.setFocusPainted(false); // ปิดเส้น focus ตอนคลิก
        playButton.addActionListener(e -> frame.showGamePanel()); // เมื่อกดให้เปลี่ยนไปหน้าเกม
        add(playButton);

        // สร้างปุ่ม Exit สำหรับปิดโปรแกรม
        JButton exitButton = new JButton("Exit");
        exitButton.setFont(new Font("Comic Sans MS", Font.BOLD, 24));
        exitButton.setBounds(430, 470, 220, 70);
        exitButton.setBackground(new Color(244, 216, 167));
        exitButton.setForeground(new Color(92, 63, 54));
        exitButton.setFocusPainted(false); // ปิดเส้น focus ตอนคลิก
        exitButton.addActionListener(e -> System.exit(0)); // เมื่อกดให้ปิดโปรแกรม
        add(exitButton);
    }

    // เมธอดสำหรับวาดองค์ประกอบต่าง ๆ บนหน้าจอ
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // ล้างพื้นหลังเดิมก่อนวาดใหม่
        Graphics2D g2 = (Graphics2D) g;

        // เปิด anti-aliasing ให้ภาพและเส้นดูเนียนขึ้น
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // วาดภาพพื้นหลังเต็มหน้าจอ
        g2.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);

        // วาดโลโก้เกมบนหน้าเริ่มต้น
        g2.drawImage(logoImage, 315, -15, 450, 450, this);
    }
}