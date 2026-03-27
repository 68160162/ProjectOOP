package catcafedefense.ui;

import javax.swing.JFrame;

// GameFrame เป็นหน้าต่างหลักของเกม
public class GameFrame extends JFrame {

    public GameFrame() {
        setTitle("Cat Café Defense"); // ตั้งชื่อหน้าต่างโปรแกรม
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // ปิดโปรแกรมทันทีเมื่อกดปุ่ม X
        setResizable(false); // ไม่อนุญาตให้ผู้ใช้ย่อ/ขยายหน้าต่าง

        showStartPanel(); // เริ่มต้นด้วยหน้า StartPanel ก่อน

        pack();
        setLocationRelativeTo(null); // ทำให้หน้าต่างแสดงตรงกลางจอ
        setVisible(true); // แสดงหน้าต่างออกมาบนหน้าจอ
    }

    // เมธอดสำหรับสลับไปหน้าเริ่มเกม
    public void showStartPanel() {
        setContentPane(new StartPanel(this));
        revalidate();
        repaint();
    }

    // เมธอดสำหรับสลับไปหน้าเล่นเกมจริง
    public void showGamePanel() {
        setContentPane(new GamePanel());
        revalidate();
        repaint();
    }
}