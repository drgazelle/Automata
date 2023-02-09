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
    public static int WIDTH = 700;
    public static int HEIGHT = 700;

    private static JFrame frame;
    public static MainPanel panel;

    public static void main(String[] args) {
        panel = new MainPanel();
        frame = new JFrame("Automata");
        frame.setIconImage(new ImageIcon("src/main/resources/cell.png").getImage());
        // frame attributes
        frame.setContentPane(panel);
        frame.setSize(WIDTH + 17, HEIGHT + 40);
        frame.setLocation(50, 50);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setVisible(true);
        frame.setBackground(Color.black);

        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.out.println("\nExporting Data...");
                //Exports Database on Close
                if(panel.getDatabase().exportDatabase()) {
                    System.out.println("Data Exported Successfully");
                }
                else {
                    System.out.println("ERROR: Failed to Export");
                }
                frame.dispose();
                System.exit(0);
            }
        });

        //frame.addComponentListener(new ComponentAdapter() {
        //    public void componentResized(ComponentEvent componentEvent) {
        //        HEIGHT = frame.getHeight() - 17;
        //        WIDTH = frame.getWidth() - 40;
        //        panel.repaint();
        //   }
        //});
    }
}
