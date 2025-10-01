import javax.swing.*;
import java.awt.*;
import java.util.InputMismatchException;

public class Main {
    public static void main(String[] args) {
        // รันบน Swing Thread
        SwingUtilities.invokeLater(() -> {
            // ให้ผู้ใช้ใส่จำนวนอุกกาบาต
            String input = JOptionPane.showInputDialog("Meteorite:");

            // ถ้าไม่ได้ใส่อะไร
            int count = 0;
            try {
                if (input != null && !input.isEmpty()) {
                    count = Integer.parseInt(input);
                }
            }
            catch (Exception e) {
                System.out.println("Please enter only numeric values");
                return;
            }


            FrameMT frame = new FrameMT(count);
            frame.setVisible(true);
        });
    }
}
