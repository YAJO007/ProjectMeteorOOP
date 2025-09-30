import javax.swing.*;
import java.awt.*;

public class FrameMT extends JFrame {
    private JLabel infoLabel;
    private Inprogram meteorPanel;

    public FrameMT(int count) {
        // ตั้งค่าหน้าต่าง
        setTitle("Meteorite");
        setSize(Vitality.frame_Width, Vitality.frame_Height);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // สร้าง label แสดงจำนวน
        infoLabel = new JLabel("Meteorites : 0");
//        infoLabel.setForeground(Color.BLUE);

        // สร้าง panel เกม
        meteorPanel = new Inprogram();
        meteorPanel.setInfoText(infoLabel);
        meteorPanel.setBackgroundImage();
        meteorPanel.setMeteor(count);

        // จัด layout
        setLayout(new BorderLayout());
        add(infoLabel, BorderLayout.NORTH);
        add(meteorPanel, BorderLayout.CENTER);
    }

    @Override
    public void dispose() {
        meteorPanel.stop(); // หยุด thread ตอนปิด
        super.dispose();
    }
}
