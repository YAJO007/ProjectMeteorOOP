import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.Random;

public class Inprogram extends JPanel implements Runnable {
    // จำนวนอุกกาบาต
    private int count;

    // เก็บรูปและตำแหน่ง/ความเร็วของอุกกาบาต
    private Image[] meteor;
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
        // ขนาด panel = ตามค่าที่ตั้งใน Vitality
        setPreferredSize(new Dimension(Vitality.window_Width, Vitality.window_Height));
        setDoubleBuffered(true); // ป้องกันภาพกระพริบ
        new Thread(this, "MeteorLoop").start(); // สตาร์ทเธรดเกมลูป
    }

    /* -------------------- Setter -------------------- */
    public void setInfoText(JLabel infoText) {
        this.infoText = infoText;
        updateMeteorCount();
    }

    // โหลดรูปพื้นหลัง (แก้ path ให้ตรงกับโปรเจกต์ของคุณ)
    public void setBackgroundImage() {
        String bgPath = System.getProperty("user.dir")
                + File.separator + "photo" + File.separator + "bk.png";
        background = new ImageIcon(bgPath).getImage();
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

        // ไฟล์อุกกาบาต (ปรับชื่อให้ตรงกับไฟล์จริงในโฟลเดอร์ photo/)
        String meteorPath = System.getProperty("user.dir")
                + File.separator + "photo" + File.separator + "photo2.png";

        for (int i = 0; i < count; i++) {
            exploding[i] = false;
            meteor[i] = new ImageIcon(meteorPath).getImage();

            // ตำแหน่งสุ่ม
            meteorX[i] = rng.nextInt(Vitality.window_Width - Vitality.meteorSize);
            meteorY[i] = rng.nextInt(Vitality.window_Height - Vitality.meteorSize);

            // ความเร็วสุ่ม (ไม่ต่ำกว่า 0.5)
            speedX[i] = rng.nextInt(Vitality.speedMeteor * 2 + 1) - Vitality.speedMeteor;
            speedY[i] = rng.nextInt(Vitality.speedMeteor * 2 + 1) - Vitality.speedMeteor;
            if (speedX[i] == 0) {
                speedX[i] = 1;
            }
            if (speedY[i] == 0) {
                speedY[i] = 1;
            }

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
        if (background != null) {
            g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
        }

        // วาดอุกกาบาต
        if (meteor != null) {
            for (int i = 0; i < count; i++) {
                if (meteor[i] != null) {
                    g.drawImage(meteor[i], meteorX[i], meteorY[i],
                            Vitality.meteorSize, Vitality.meteorSize, this);
                }
            }
        }
    }

    /* -------------------- เอฟเฟกต์ระเบิด -------------------- */
    private void explode(int index) {


        // โหลดไฟล์ gif ระเบิด
        String expPath = System.getProperty("user.dir")
                + File.separator + "photo" + File.separator + "gitB.gif";
        meteor[index] = new ImageIcon(expPath).getImage();

        // หยุดการเคลื่อนที่
        speedX[index] = 0;
        speedY[index] = 0;

        updateMeteorCount();

        // ผ่านไป 0.3 วิ → ลบออก
        new Thread(() -> {
            try
            { Thread.sleep(300); }
            catch
            (InterruptedException ignored) {}
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
        final int sz = Vitality.meteorSize;

        while (alive) {
            if (meteor != null) {
                for (int i = 0; i < count; i++) {
                    if (meteor[i] == null || exploding[i]) continue;

                    // อัปเดตตำแหน่ง
                    meteorX[i] += Math.round(speedX[i]);
                    meteorY[i] += Math.round(speedY[i]);

                    int W = getWidth();
                    if (W <= 0) {
                        W = Vitality.window_Width;
                    }

                    int H = getHeight();
                    if (H <= 0) {
                        H = Vitality.window_Height;
                    }

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

                // ตรวจชนกัน
                for (int i = 0; i < count; i++) {
                    if (meteor[i] == null || exploding[i]) {
                        continue;
                    }
                    for (int j = i + 1; j < count; j++) {
                        if (meteor[j] == null || exploding[j]) {
                            continue;
                        }

                        float ax = meteorX[i] + sz/2f, ay = meteorY[i] + sz/2f;
                        float bx = meteorX[j] + sz/2f, by = meteorY[j] + sz/2f;

                        float dx = ax - bx, dy = ay - by;
                        float dist2 = dx*dx + dy*dy;
                        float collide = sz;


                        //ชนละไม่เปลี่ยนทิศทาง
                        if (dist2 < collide * collide) {
                            float sp1 = Math.abs(speedX[i]) + Math.abs(speedY[i]);
                            float sp2 = Math.abs(speedX[j]) + Math.abs(speedY[j]);
                            if (sp1 < sp2) {
                                explode(i);
                            } else {
                                explode(j);
                            }
                        }
                    }
                }
            }

            repaint();
            try { Thread.sleep(16); }
            catch (InterruptedException ignored) {

            }

        }
    }
}
