import javax.swing.ImageIcon;
import javax.swing.JFrame;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/** AppDriver class creates the containing
 *  window for the simulation.
 *
 * @author RMizelle
 */
public class AppDriver {
    //window dimensions
    public static final int WIDTH = 700;
    public static final int HEIGHT = 700;

    private static JFrame frame;
    public static MainPanel panel;

    public static void main(String[] args) {
        panel = new MainPanel();
        frame = new JFrame("Automata Lite");
        frame.setIconImage(new ImageIcon("src/main/resources/cell.png").getImage());
        // frame attributes
        frame.setContentPane(panel);
        frame.setSize(WIDTH + 17, HEIGHT + 40);
        frame.setLocation(10, 10);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setVisible(true);
        frame.setBackground(Color.black);
    }
}
