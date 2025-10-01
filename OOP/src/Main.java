import javax.swing.*;
import java.awt.*;
import java.util.InputMismatchException;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            String input = JOptionPane.showInputDialog("Meteorite:");

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
