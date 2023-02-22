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
    private Point startPoint;
    private Point endPoint;


    //Matrix Variables
    private CellMatrix matrix;
    private CellMatrix cm;
    private final double maxP;
    private final int increment;

    //Database Variables
    private final Database database;
    private int indexDB;

    //Animation Variables
    public static Color mainColor;
    public static Font mainFont;
    public static Font titleFont;

    private final Timer timer;
    private int numTicks;
    private int delay;

    private boolean showStatus;
    private boolean showMenu;
    private boolean showDatabase;
    private boolean showHighlight;
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

        //database variables
        database = new Database();
        indexDB = -1;

        //default menu status
        showStatus = true;
        showMenu = true;
        showDatabase = false;
        showHighlight = false;
        Cell.gridEnabled = true;

        //Sets Application Theme
        mainColor = new Color((int) (Math.random() * 0x1000000));
        mainFont = new Font("SansSerif", Font.PLAIN, 10);
        titleFont = new Font(mainFont.getFontName(), Font.PLAIN, 4 * mainFont.getSize() / 3);

        Color backColor = Color.black;
        Color textColor = Color.white;

        //JOptionPane theme
        UIManager.put("OptionPane.messageForeground", mainColor);
        UIManager.put("OptionPane.background", backColor);
        UIManager.put("Panel.background", backColor);
        UIManager.put("Button.background", backColor);
        UIManager.put("Button.foreground", textColor);
        UIManager.put("Button.highlight", textColor);
        UIManager.put("TextField.background", backColor);
        UIManager.put("TextField.selectionBackground", mainColor);
        UIManager.put("TextField.foreground", textColor);
        UIManager.put("TextField.selectionForeground", backColor);

        DynamicMenu.setTextColor(mainColor);
        mainMenu = new DynamicMenu(null, null);
        updateMenu();

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
        if(showHighlight) {
            Rectangle rect = new Rectangle();
            rect.setFrameFromDiagonal(endPoint, startPoint);
            matrix.spotlightAll(rect);
        }
        matrix.drawMatrix(g);
        if (showStatus) {
            paintStatus(g);
            g.setFont(mainFont);
        }
        if(showMenu) {
            int pX = AppDriver.WIDTH - 10 - mainMenu.getBoxWidth();
            int pY = AppDriver.HEIGHT - 10 - mainMenu.getBoxHeight();
            mainMenu.paintMenu(g, pX, pY);
        }
        if (showDatabase) {
            database.paintDatabase(g, indexDB);
            g.setFont(mainFont);
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

    /** Updates the mainMenu to display
     *  relevant keycodes and configuration
     */
    private void updateMenu() {
        //list of menu items + current status
        int numRows = matrix.getNumRows();
        int numColumns = matrix.getNumCols();
        String title = "Automata" + " (" + numRows + "x" + numColumns + ") " + "(" + delay + "ms)";
        String[] menuItems = {"Toggle Simulation [SPACE]",
                                "Single Tick [F]",
                                "Resize Grid [Q/E]",
                                "Change Speed [A/D]",
                                "Wrap-Around Grid [W]",
                                "Generate Random Seed [S]",
                                "Save [Z]",
                                "Toggle Grid [X]",
                                "Clear [C]"};
        String[] databaseItems = {"Close Database [J]",
                                "Search wiki-collections [;]",
                                "Navigate Database [U/N]",
                                "Print RLE [H]",
                                "Rename Selected [H]",
                                "Remove Selected [K]",
                                "Clear Selection [M]",
                                "Wipe Database [L]",
                                "Toggle Menu [T]",
                                "Toggle Status [R]"};

        //displays correct information
        String[] displayItems = menuItems;
        if (showDatabase) {
            displayItems = databaseItems;
        }

        //updates mainMenu
        mainMenu.setItems(displayItems);
        mainMenu.setTitle(title);
    }

    /** Accessor Method for Database
     *
     * @return database
     */
    public Database getDatabase() {
        return database;
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
            if(cm != null && cell != null) {
                //places cell at coords
                int[] coords = matrix.getCellCoordinates(cell);
                //centers placement
                coords[0] -= cm.getNumRows() / 2;
                coords[1] -= cm.getNumCols() / 2;
                if(matrix.placeCellMatrix(coords[0], coords[1], cm)) {
                    database.setIndexColor(new Color(34, 139, 34));
                }
                else {
                    database.setIndexColor(Color.red);
                }
            }
            else if (cell != null) cell.flip();
        }
        else if (button == MouseEvent.BUTTON3) {
            startPoint = new Point(mouseX, mouseY);
            if (endPoint == null) {
                endPoint = new Point(mouseX, mouseY);
            }
            showHighlight = true;
        }
        repaint();
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
        if(showHighlight) {
            mouseX = e.getX();
            mouseY = e.getY();
            endPoint = new Point(mouseX, mouseY);
            repaint();
        }
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
        if (cm != null && cell != null) {
            //if moving spotlight
            int[] coords = matrix.getCellCoordinates(cell);
            //adjusts placement from center to corner;
            coords[0] -= cm.getNumRows() / 2;
            coords[1] -= cm.getNumCols() / 2;
            if (matrix.spotlightPlacement(coords[0], coords[1], cm)) {
                database.setIndexColor(Color.darkGray);
            }
            else {
                database.setIndexColor(Color.red);
            }
        }
        else if (cell != null) cell.spotlight();

        if(showHighlight) {
            //resets spotlight
            showHighlight = false;
            //resets points
            startPoint = null;
            endPoint = null;
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
        if (e.getKeyCode() == KeyEvent.VK_Z) {
            // Saves Cell Matrix on 'Z'
            MatrixData md;
            if (showHighlight) {
                md = matrix.fromSpotlight().toMatrixData();
            }
            else {
                md = matrix.toMatrixData();
            }
            if(md != null) {
                database.add(md);
            }
        }
        if (e.getKeyCode() == KeyEvent.VK_R) {
            //toggles status on 'R'
            showStatus = !showStatus;
        }
        if (e.getKeyCode() == KeyEvent.VK_T) {
            //toggles menu on 'T'
            showMenu = !showMenu;
        }
        if (e.getKeyCode() == KeyEvent.VK_J) {
            // toggles database and resets index 0n 'J'
            showDatabase = !showDatabase;
            indexDB = -1;
            cm = null;
            matrix.clearSpotlight();
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

        if (showDatabase) {
            if (e.getKeyCode() == KeyEvent.VK_SEMICOLON) {
                //Prompts User for search term
                String s = (String) JOptionPane.showInputDialog(
                        this, "Search wikicollection:", "Database",
                        JOptionPane.PLAIN_MESSAGE, null, null, "Enter Search Here");
                if (s != null) {
                    //if name changed
                    database.addFromSearch(s);
                }
            }
            if (database.sizeDB() > 0) {
                //if database is visible and not empty
                if (e.getKeyCode() == KeyEvent.VK_U) {
                    //if database showing and exists, navigate down and update on 'U'
                    indexDB--;
                    if (indexDB < 0) {
                        indexDB = database.sizeDB() - 1;
                    }
                    importFromDB();
                }
                if (e.getKeyCode() == KeyEvent.VK_N) {
                    //if database showing and exists, navigate down and update on 'M'
                    indexDB++;
                    if (indexDB >= database.sizeDB()) {
                        indexDB = 0;
                    }
                    importFromDB();
                }
                if (indexDB > -1) {
                    //if item selected
                    if (e.getKeyCode() == KeyEvent.VK_H) {
                        //Renames on 'H'
                        MatrixData m = database.get(indexDB);
                        String name = m.getTitle();

                        //Prompts User for new Name
                        String s = (String) JOptionPane.showInputDialog(
                                this, "Modify Matrix Name Below:", "Database",
                                JOptionPane.PLAIN_MESSAGE, null, null, name);
                        if (s != null) {
                            //if name changed
                            m.setTitle(s);
                        }
                    }
                    if (e.getKeyCode() == KeyEvent.VK_K) {
                        //removes CellMatrix at Index
                        database.removeAtIndex(indexDB);
                        //moves up if at bottom of list
                        if (indexDB == database.sizeDB()) {
                            indexDB--;
                        }
                    }
                    if (e.getKeyCode() == KeyEvent.VK_G) {
                        //displays RLE String on 'G'
                        MatrixData m = database.get(indexDB);
                        String rleString = m.getRleString();

                        //Prompts User for new Name
                        String s = (String) JOptionPane.showInputDialog(
                                this, "Modify RLE Below:", "Database",
                                JOptionPane.PLAIN_MESSAGE, null, null, rleString);
                        if (s != null) {
                            //if name changed
                            m.setRleString(s);
                            importFromDB();
                        }
                    }
                    if (e.getKeyCode() == KeyEvent.VK_M) {
                        //Clear selection at index
                        cm = null;
                        indexDB = -1;
                    }
                }
                if (e.getKeyCode() == KeyEvent.VK_L) {
                    //wipes database on 'L'
                    database.wipe();
                    indexDB = -1;
                }
            }
        }
        updateMenu();
        repaint();
    }

    /** Accesses MatrixData from internal index and
     *  updates numRows, numColumns and matrix
     */
    private void importFromDB() {
        MatrixData m = database.get(indexDB);
        cm = m.toCellMatrix();
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
        else {
            //contracts grid
            if (matrix.getNumRows() > increment) {
                numTicks = 0;
                changeGrid(-1 * increment);
            }
        }
        repaint();
    }
}
