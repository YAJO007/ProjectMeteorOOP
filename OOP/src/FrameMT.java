import javax.swing.*;
import java.awt.*;

public class FrameMT extends JFrame {
    private JLabel infoLabel;
    private Inprogram meteorPanel;

    public FrameMT(int count) {
        setTitle("Meteorite");
        setSize(Vitality.frame_Width, Vitality.frame_Height);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        infoLabel = new JLabel("Meteorites : 0");

        meteorPanel = new Inprogram();
        meteorPanel.setInfoText(infoLabel);
        meteorPanel.setBackgroundImage();
        meteorPanel.setMeteor(count);

        // จัด layout
        setLayout(new BorderLayout());
        add(infoLabel, BorderLayout.NORTH);
        add(meteorPanel, BorderLayout.CENTER);
    }
    //หยุดเธรดเกมก่อน
    @Override
    public void dispose() {
        meteorPanel.stop();
        super.dispose();
    }
}
