import dynamicpanel.ProgressBar;
import dynamicpanel.TextBar;

import javax.swing.*;
import java.awt.*;

public class DynamicPanelTest {
    public static int WIDTH = 700;
    public static int HEIGHT = 700;

    private static JFrame frame;
    public static TestPanel panel;

    public static void main(String[] args) {
        panel = new TestPanel();
        frame = new JFrame("DynamicPanel");
        // frame attributes
        frame.setContentPane(panel);
        frame.setSize(WIDTH + 17, HEIGHT + 40);
        frame.setLocation(50, 50);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setVisible(true);
        frame.setBackground(Color.white);
}

static class TestPanel extends JPanel {

    DynamicMenu dp = new DynamicMenu("food", null);

    public TestPanel() {
        this.setFocusable(true);

        Font titleFont = new Font("SanSerif", Font.BOLD, 45);
        Font textFont = new Font(titleFont.getFontName(), titleFont.getStyle(), (int) Math.round(2 * titleFont.getSize() / 3.0));


        TextBar title = new TextBar("HELLO", titleFont);
        title.setColor(Color.blue);

        TextBar line = new TextBar("1000", titleFont);
        line.setColor(Color.white);

        ProgressBar bar = new ProgressBar(100, 20, 0, 100, 50);
        dp.add(bar);
        dp.add(title);
        dp.add(line);
        dp.add(bar);
        dp.select(2);
        dp.add(line);

        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        dp.draw(g, 10, 10);
    }
}
}
