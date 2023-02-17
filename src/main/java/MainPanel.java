import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.UIManager;
import java.awt.*;
import java.awt.event.*;

/** MainPanel class renders a CellMatrix
 *  representing an interactive version
 *  of Conway's Game of Life.
 *
 * @author RMizelle
 */
public class MainPanel extends JPanel implements MouseListener, MouseMotionListener,
                                            MouseWheelListener, KeyListener, ActionListener {

    //Mouse Positions
    private int mouseX;
    private int mouseY;


    //Matrix Variables
    private CellMatrix matrix;
    private final double maxP;
    private final int increment;

    //Animation Variables
    private final Timer timer;
    private int numTicks;
    private int delay;

    //Theme Variables
    public static Color mainColor;
    public static Font mainFont;
    public static Font titleFont;

    //Configuration Variables
    private boolean showStatus;
    private boolean showMenu;
    private boolean wrapEnabled;

    //Menu Object
    private final DynamicMenu mainMenu;

    /** 0-arg constructor adds Mouse Listeners
     *  and instantiates the matrix and timer.
     */
    public MainPanel() {
        //adds listeners
        addMouseListener(this);
        addMouseMotionListener(this);
        addMouseWheelListener(this);
        addKeyListener(this);
        this.setFocusable(true);

        //instantiates matrix
        matrix = new CellMatrix(100, 100);

        //configuration
        increment = 5;
        wrapEnabled = true;

        //randomizes seed
        maxP = 0.30;
        matrix.randomSeed(maxP);

        //instantiates timer
        delay = 20;
        numTicks = 0;
        timer = new Timer(delay, this);

        //default menu status
        showStatus = true;
        showMenu = true;
        Cell.gridEnabled = true;

        //Sets Application Theme
        mainColor = new Color((int) (Math.random() * 0x1000000));
        mainFont = new Font("SansSerif", Font.PLAIN, 10);
        titleFont = new Font(mainFont.getFontName(), Font.PLAIN, 4 * mainFont.getSize() / 3);

        Color backColor = Color.black;
        Color textColor = Color.white;

        //JOptionPane theme
        UIManager.put("OptionPane.messageForeground", mainColor);
        UIManager.put("OptionPane.background", Color.BLACK);
        UIManager.put("Panel.background", Color.BLACK);
        UIManager.put("Button.background", Color.BLACK);
        UIManager.put("Button.foreground", Color.WHITE);
        UIManager.put("Button.highlight", Color.WHITE);
        UIManager.put("TextField.background", Color.BLACK);
        UIManager.put("TextField.selectionBackground", mainColor);
        UIManager.put("TextField.foreground", textColor);
        UIManager.put("TextField.selectionForeground", backColor);
        mainMenu = generateMenu();
        repaint();
    }

    /** Resizes grid with given increment.
     *
     * @param i increment size
     */
    private void changeGrid(int i) {
        int numRows = matrix.getNumRows() + i;
        int numColumns = matrix.getNumCols() + i;

        matrix = new CellMatrix(numRows, numColumns);
        matrix.randomSeed(maxP);
    }

    /** Paint method for MainPanel that draws
     *  the CellMatrix, status, and menu.
     *
     * @param g graphics
     */
    @Override
    public void paintComponent(Graphics g) {
        DynamicMenu.setTitleColor(mainColor);
        DynamicMenu.setMainFont(mainFont);
        DynamicMenu.setTitleFont(titleFont);

        g.setFont(mainFont);
        matrix.drawMatrix(g);
        if (showStatus) {
            paintStatus(g);
            g.setFont(mainFont);
        }
        if(showMenu) {
            int pX = AppDriver.WIDTH - 10 - mainMenu.getBoxWidth();
            int pY = AppDriver.HEIGHT - 10 - mainMenu.getBoxHeight();
            mainMenu.setTitle("Automata Lite"
                                + " (" + matrix.getNumRows() + "x" + matrix.getNumCols() + ")"
                                + " (" + delay + "ms)");
            mainMenu.paintMenu(g, pX, pY);
        }
    }

    /** Displays an indicator if the
     *  simulation is running and number
     *  of ticks.
     *
     * @param g graphics
     */
    public void paintStatus(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        //initial conditions
        int boxHeight = (int) (mainFont.getSize() * 2.5);
        int boxWidth = boxHeight * 3;
        int border = 10;
        int pX = AppDriver.WIDTH - boxWidth - border;
        int pY = border;

        Shape tickBox = new Rectangle(pX, pY, boxWidth, boxHeight);
        g2.setColor(Color.black);
        g2.fill(tickBox);

        Shape indicator = new Rectangle(pX + (2 * boxHeight), pY, boxWidth / 3, boxHeight);
        g2.setColor(Color.RED);
        if (timer.isRunning()) {
            g2.setColor(Color.GREEN);
        }

        g2.fill(indicator);
        g2.setColor(Color.white);

        if (!wrapEnabled) {
            g2.draw(tickBox);
        }

        String digits = String.valueOf(numTicks);
        g2.setFont(titleFont);

        if (digits.length() > 4) {
            digits = digits.substring(0, digits.length() - 4) + "K";
        }
        while (digits.length() < 4) {
            digits = "0" + digits;
        }

        FontMetrics metrics = getFontMetrics(mainFont);
        int dY = metrics.getAscent();
        //centers text vertically
        pY += (boxHeight + dY) / 2;
        //centers text horizontally
        pX += boxHeight / 2;

        g2.drawString(digits, pX, pY);
    }

    /** Displays the keycodes to
     *  manipulate simulation.
     *
     * @return DynamicMenu with list of controls
     */
    private DynamicMenu generateMenu() {
        //list of menu items + current status
        int numRows = matrix.getNumRows();
        int numColumns = matrix.getNumCols();

        String title = "Automata Lite" + " (" + numRows + "x" + numColumns + ") " + "(" + delay + "ms)";
        String[] menuItems = {"Toggle Simulation [SPACE]",
                                "Resize Grid [Q/E]",
                                "Change Speed [A/D]",
                                "Single Tick [F]",
                                "Wrap-Around Grid [W]",
                                "Generate Random Seed [S]",
                                "Modify RLE [G]",
                                "Toggle Status [R]",
                                "Toggle Menu [T]",
                                "Resize UI [V/B]",
                                "Toggle Grid [X]",
                                "Clear [C]"};
        return new DynamicMenu(title, menuItems);
    }

    /**
     * Flips cell status when clicked.
     *
     * @param e the event to be processed
     */
    @Override
    public void mouseClicked(MouseEvent e) {

    }

    /**
     * Invoked when a mouse button has been pressed on a component.
     *
     * @param e the event to be processed
     */
    @Override
    public void mousePressed(MouseEvent e) {
        int button = e.getButton();
        if(button == MouseEvent.BUTTON1) {
            Cell cell = matrix.findCellAt(mouseX, mouseY);
            if (cell != null) {
                cell.flip();
                repaint();
            }
        }
    }

    /**
     * Invoked when a mouse button has been released on a component.
     *
     * @param e the event to be processed
     */
    @Override
    public void mouseReleased(MouseEvent e) {

    }

    /**
     * Invoked when the mouse enters a component.
     *
     * @param e the event to be processed
     */
    @Override
    public void mouseEntered(MouseEvent e) {

    }

    /**
     * Invoked when the mouse exits a component.
     *
     * @param e the event to be processed
     */
    @Override
    public void mouseExited(MouseEvent e) {
        matrix.clearSpotlight();
        repaint();
    }

    /**
     * Invoked when a mouse button is pressed on a component and then
     * dragged.  {@code MOUSE_DRAGGED} events will continue to be
     * delivered to the component where the drag originated until the
     * mouse button is released (regardless of whether the mouse position
     * is within the bounds of the component).
     * <p>
     * Due to platform-dependent Drag&amp;Drop implementations,
     * {@code MOUSE_DRAGGED} events may not be delivered during a native
     * Drag&amp;Drop operation.
     *
     * @param e the event to be processed
     */
    @Override
    public void mouseDragged(MouseEvent e) {

    }

    /**
     * Spotlights Cell on hover.
     *
     * @param e the event to be processed
     */
    @Override
    public void mouseMoved(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();

        matrix.clearSpotlight();
        Cell cell = matrix.findCellAt(mouseX, mouseY);
        if (cell != null) {
            cell.spotlight();
        }
        repaint();
    }

    /**
     * Ticks CellMatrix and repaints.
     *
     * @param e the event to be processed
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        matrix.tick(wrapEnabled);
        numTicks++;
        repaint();
    }

    /**
     * Invoked when a key has been typed.
     * See the class description for {@link KeyEvent} for a definition of
     * a key typed event.
     *
     * @param e the event to be processed
     */
    @Override
    public void keyTyped(KeyEvent e) {

    }

    /**
     * Enables keyboard control of simulation
     *
     * @param e the event to be processed
     */
    @Override
    public void keyPressed(KeyEvent e) {
        int numRows = matrix.getNumRows();
        int numCols = matrix.getNumCols();

        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            //toggles simulation on space-bar
            if(timer.isRunning()) {
                timer.stop();
            }
            else {
                timer.start();
            }
        }
        if(e.getKeyCode() == KeyEvent.VK_F) {
            //Singular tick
            matrix.tick(wrapEnabled);
            numTicks++;
        }
        if (e.getKeyCode() == KeyEvent.VK_D && timer.getDelay() > 1) {
            //speeds up timer on 'D'
            if (delay > 100) {
                delay -= 10;
            }
            else {
                delay -= 1;
            }
            timer.setDelay(delay);
        }
        if (e.getKeyCode() == KeyEvent.VK_A && timer.getDelay() < 5000) {
            //slows down timer on up 'A'
            if (delay >= 100) {
                delay += 10;
            }
            else {
                delay += 1;
            }
            timer.setDelay(delay);
        }
        if(e.getKeyCode() == KeyEvent.VK_E) {
            //increases grid size on 'E'
            if (numRows < AppDriver.WIDTH / 4) {
                numTicks = 0;
                changeGrid(increment);
            }
        }
        if(e.getKeyCode() == KeyEvent.VK_Q) {
            //decreases grid size on 'Q'
            if (numRows > increment) {
                numTicks = 0;
                changeGrid(-1 * increment);
            }
        }
        if (e.getKeyCode() == KeyEvent.VK_X) {
            //turns grid on and off on 'X'
            Cell.gridEnabled = !Cell.gridEnabled;
        }
        if (e.getKeyCode() == KeyEvent.VK_R) {
            //toggles status on 'R'
            showStatus = !showStatus;
        }
        if (e.getKeyCode() == KeyEvent.VK_T) {
            //toggles menu on 'T'
            showMenu = !showMenu;
        }
        if (e.getKeyCode() == KeyEvent.VK_W) {
            //toggles wrap around on 'W'
            wrapEnabled = !wrapEnabled;
        }
        if (e.getKeyCode() == KeyEvent.VK_S) {
            //randomizes matrix seed on 'S'
            matrix.genocide();
            matrix.randomSeed(maxP);
            numTicks = 0;
        }
        if (e.getKeyCode() == KeyEvent.VK_G) {
            //displays RLE String on 'G'
            String rleString = matrix.toString();

            //Prompts User for new Name
            String s = (String) JOptionPane.showInputDialog(
                    this, "Modify RLE Below:", "Database",
                    JOptionPane.PLAIN_MESSAGE, null, null, rleString);
            if (s != null) {
                matrix.fromRLE(s);
                numTicks = 0;
            }
        }
        if (e.getKeyCode() == KeyEvent.VK_C) {
            //kills all cells on 'C'
            matrix.genocide();
            numTicks = 0;
        }
        if(e.getKeyCode() == KeyEvent.VK_B) {
            if(mainFont.getSize() < 20) {
                //increases font size if less than 50
                mainFont = new Font(mainFont.getFontName(), Font.PLAIN, mainFont.getSize() + 2);
                titleFont = new Font(mainFont.getFontName(), Font.PLAIN, 4 * mainFont.getSize() / 3);
            }
        }
        if(e.getKeyCode() == KeyEvent.VK_V) {
            if(mainFont.getSize() > 10) {
                //decreases font size if greater than 14
                mainFont = new Font(mainFont.getFontName(), Font.PLAIN, mainFont.getSize() - 2);
                titleFont = new Font(mainFont.getFontName(), Font.PLAIN, 4 * mainFont.getSize() / 3);
            }
        }
        repaint();
    }

    /**
     * Invoked when a key has been released.
     * See the class description for {@link KeyEvent} for a definition of
     * a key released event.
     *
     * @param e the event to be processed
     */
    @Override
    public void keyReleased(KeyEvent e) {

    }

    /**
     * Invoked when the mouse wheel is rotated.
     *
     * @param e the event to be processed
     * @see MouseWheelEvent
     */
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        int notches = e.getWheelRotation();
        //
        if (notches > 0) {
            //expands grid
            if (matrix.getNumRows() < AppDriver.WIDTH / 4) {
                numTicks = 0;
                changeGrid(increment);
            }
        }
        else if (matrix.getNumRows() > increment) {
            //contracts grid
            numTicks = 0;
            changeGrid(-1 * increment);
        }
        repaint();
    }
}
