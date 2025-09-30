import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.Random;

public class Inprogram extends JPanel implements Runnable {
    // จำนวนอุกกาบาต
    private int count;

    // เก็บรูปอุกกาบาตที่ "ใช้งานอยู่" ของแต่ละลูก และสไปรต์ทั้งหมด
    private Image[] meteor;           // รูปของแต่ละลูก (เลือกจาก meteorSprites)
    private Image[] meteorSprites;    // คลังรูปหลายไฟล์

    // ตำแหน่ง/ความเร็ว และสถานะระเบิด
    private int[] meteorX, meteorY;
    private float[] speedX, speedY;
    private boolean[] exploding;   // true = กำลังระเบิด

    // รูปพื้นหลัง
    private Image background;

    // แสดงจำนวนอุกกาบาต
    private JLabel infoText;

    // ควบคุมเธรด
    private volatile boolean alive = true;
    private final Random rng = new Random();

    /* -------------------- Constructor -------------------- */
    public Inprogram() {
        setPreferredSize(new Dimension(Vitality.frame_Width, Vitality.frame_Height));
        setBackgroundImage();
        loadMeteorSprites();     // โหลดหลายรูปครั้งเดียว
        new Thread(this, "MeteorLoop").start(); // สตาร์ทเธรดเกมลูป
    }

    /* -------------------- Setter -------------------- */
    public void setInfoText(JLabel infoText) {
        this.infoText = infoText;
        updateMeteorCount();
    }

    // โหลดรูปพื้นหลัง
    public void setBackgroundImage() {
        String bgPath = System.getProperty("user.dir")
                + File.separator + "image" + File.separator + "background.jpg";
        background = new ImageIcon(bgPath).getImage();
    }

    // โหลด "หลายรูป" ของอุกกาบาตไว้ในคลัง (ปรับชื่อไฟล์ตามที่มีจริงในโฟลเดอร์ image/)
    private void loadMeteorSprites() {
        String base = System.getProperty("user.dir") + File.separator + "image" + File.separator;

        // >>> ปรับรายชื่อไฟล์ตามโปรเจกต์ของคุณ <<<
        String[] names = {"1.png", "2.png", "3.png", "4.png"};

        meteorSprites = new Image[names.length];
        for (int i = 0; i < names.length; i++) {
            meteorSprites[i] = new ImageIcon(base + names[i]).getImage();
        }
    }

    // สร้างอุกกาบาต
    public void setMeteor(int count) {
        this.count = Math.max(0, count);

        meteor    = new Image[count];
        meteorX   = new int[count];
        meteorY   = new int[count];
        speedX    = new float[count];
        speedY    = new float[count];
        exploding = new boolean[count];


        for (int i = 0; i < count; i++) {
            exploding[i] = false;

            // สุ่มเลือกรูปจากคลังหลายไฟล์
            meteor[i] = meteorSprites[rng.nextInt(meteorSprites.length)];

            // ตำแหน่งสุ่ม
            meteorX[i] = rng.nextInt(Math.max(1, Vitality.frame_Width - Vitality.mtSize));
            meteorY[i] = rng.nextInt(Math.max(1, Vitality.frame_Height - Vitality.mtSize));

            // ความเร็วสุ่ม (กัน 0 และมีค่าต่ำสุดเล็กน้อย)

            do {
                speedX[i] = rng.nextInt(Vitality.spMeteor * 2 + 1) - Vitality.spMeteor;
            } while (speedX[i] == 0);

// ความเร็วสุ่ม Y (กันไม่ให้เป็น 0)
            do {
                speedY[i] = rng.nextInt(Vitality.spMeteor * 2 + 1) - Vitality.spMeteor;
            } while (speedY[i] == 0);

        }
        updateMeteorCount();
        repaint();
    }


    public void stop() { alive = false; }

    /* -------------------- วาดภาพ -------------------- */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // วาดพื้นหลัง
         g.drawImage(background, 0, 0, getWidth(), getHeight(), this);


        // วาดอุกกาบาต
            for (int i = 0; i < count; i++) {
                if (meteor[i] != null) {
                    g.drawImage(meteor[i], meteorX[i], meteorY[i],
                            Vitality.mtSize, Vitality.mtSize, this);
                }
            }
        }


    /* -------------------- เอฟเฟกต์ระเบิด -------------------- */
    private void explode(int index) {
        if (index < 0 || index >= count || meteor[index] == null || exploding[index]) {
            return;
        }

        exploding[index] = true; // ตั้งสถานะก่อน

        // โหลดไฟล์ gif ระเบิด (ตรวจชื่อไฟล์จริงในโฟลเดอร์ photo/)
        String expPath = System.getProperty("user.dir")
                + File.separator + "image" + File.separator + "explosion.gif"; // ถ้าไฟล์ชื่ออื่น แก้ตรงนี้
        meteor[index] = new ImageIcon(expPath).getImage();

        // หยุดการเคลื่อนที่
        speedX[index] = 0;
        speedY[index] = 0;

        updateMeteorCount();

        // ผ่านไป 0.3 วิ → ลบออก
        new Thread(() -> {
            try { Thread.sleep(300); } catch (InterruptedException ignored) {}
            meteor[index] = null;
            exploding[index] = false;
            updateMeteorCount();
            repaint();
        }).start();
    }

    /* -------------------- HUD -------------------- */
    private void updateMeteorCount() {
        if (infoText == null) return;
        int remaining = 0;
        for (int i = 0; i < count; i++) {
            if (meteor[i] != null && !exploding[i]) remaining++;
        }
        final int rem = remaining;

        SwingUtilities.invokeLater(() -> {
            infoText.setForeground(Color.BLACK);
            infoText.setFont(new Font("Arial", Font.BOLD, 18));
            infoText.setText("Number of Meteorites : " + rem);
        });
    }

    /* -------------------- เกมลูป -------------------- */
    @Override
    public void run() {
        final int sz = Vitality.mtSize;

        while (alive) {
            if (meteor != null) {
                for (int i = 0; i < count; i++) {
                    if (meteor[i] == null || exploding[i]) continue;

                    // อัปเดตตำแหน่ง
                    meteorX[i] += Math.round(speedX[i]);
                    meteorY[i] += Math.round(speedY[i]);

                    int W = getWidth();
                    if (W <= 0) W = Vitality.frame_Width;

                    int H = getHeight();
                    if (H <= 0) H = Vitality.frame_Height;

                    // ชนซ้าย/ขวา
                    if (meteorX[i] <= 0) {
                        meteorX[i] = 0;
                        speedX[i] = Math.abs(speedX[i]);
                    } else if (meteorX[i] >= W - sz) {
                        meteorX[i] = W - sz;
                        speedX[i] = -Math.abs(speedX[i]);
                    }

                    // ชนบน/ล่าง
                    if (meteorY[i] <= 0) {
                        meteorY[i] = 0;
                        speedY[i] = Math.abs(speedY[i]);
                    } else if (meteorY[i] >= H - sz) {
                        meteorY[i] = H - sz;
                        speedY[i] = -Math.abs(speedY[i]);
                    }
                }

                // ตรวจชนกัน (ถ้าศูนย์กลางห่างกันน้อยกว่าเส้นผ่านศูนย์กลาง => ระเบิดเร็วสุด)
                for (int i = 0; i < count; i++) {
                    if (meteor[i] == null || exploding[i]) {
                        continue;
                    }
                    for (int j = i + 1; j < count; j++) {
                        if (meteor[j] == null || exploding[j]) {
                            continue;
                        }

                        float ax = meteorX[i] + sz / 2f, ay = meteorY[i] + sz / 2f;
                        float bx = meteorX[j] + sz / 2f, by = meteorY[j] + sz / 2f;

                        float dx = ax - bx, dy = ay - by;
                        float dist2 = dx * dx + dy * dy;
                        float collide = sz; // ใช้เส้นผ่านศูนย์กลางเป็นเกณฑ์ง่ายๆ

                        if (dist2 < collide * collide) {
                            float sp1 = Math.abs(speedX[i]) + Math.abs(speedY[i]);
                            float sp2 = Math.abs(speedX[j]) + Math.abs(speedY[j]);
                            if (sp1 < sp2) explode(i); else explode(j);
                        }
                    }
                }
            }

            repaint();
            try { Thread.sleep(16); } catch (InterruptedException ignored) {}
        }
    }
}
