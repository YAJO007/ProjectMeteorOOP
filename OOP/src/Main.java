import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // รันบน Swing Thread
        SwingUtilities.invokeLater(() -> {
            // ให้ผู้ใช้ใส่จำนวนอุกกาบาต
            String input = JOptionPane.showInputDialog("Meteorite:");

            // ถ้าไม่ได้ใส่อะไร กำหนดค่าเริ่มต้นเป็น 10
            int count = 0;
            if (input != null && !input.isEmpty()) {
                count = Integer.parseInt(input); // แปลงเป็นตัวเลข
            }

            // สร้างหน้าต่างเกม
            FrameMT frame = new FrameMT(count);
            frame.setVisible(true);
        });
    }
}
