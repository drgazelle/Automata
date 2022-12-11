import javax.swing.ImageIcon;
import javax.swing.JFrame;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.WildcardType;

/** AppDriver class creates the containing
 *  window for the simulation.
 *
 * @author RMizelle
 */
public class AppDriver {
    //window dimensions
    public static int WIDTH = 600;
    public static int HEIGHT = 600;

    private static JFrame frame;

    public static MainPanel panel;

    public static void main(String[] args) {
        panel = new MainPanel();
        frame = new JFrame("Automata");
        frame.setIconImage(new ImageIcon("resources/cell.png").getImage());
        // frame attributes
        frame.setContentPane(panel);
        frame.setSize(WIDTH + 17, HEIGHT + 40);
        frame.setLocation(50, 50);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(true);
        frame.setVisible(true);
        frame.setBackground(Color.black);
        //Exports Database on Close
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                panel.getDatabase().exportDatabase();
                frame.dispose();
                System.exit(0);
            }
        });
        //frame.addComponentListener(new ComponentAdapter() {
        //    public void componentResized(ComponentEvent componentEvent) {
        //        HEIGHT = frame.getHeight();
        //        WIDTH = frame.getWidth();
        //        panel.repaint();
        //    }
        //});
    }
}
