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
public class MainPanel extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener, KeyListener, ActionListener {
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
    public static final Color mainColor = new Color((int)(Math.random() * 0x1000000));
    public static final Font mainFont = new Font("SansSerif", Font.PLAIN, 10);

    private final Timer timer;
    private int numTicks;
    private int delay;

    private boolean showStatus;
    private boolean showMenu;
    private boolean wrapEnabled;
    private boolean showDatabase;
    private boolean showHighlight;

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
        matrix = new CellMatrix(105, 105);

        //configuration
        increment = 10;
        wrapEnabled = true;

        //randomizes seed
        maxP = 0.30;
        matrix.randomSeed(maxP);

        //instantiates timer
        timer = new Timer(delay, this);
        numTicks = 0;
        delay = 100;

        //database variables
        database = new Database();
        indexDB = -1;

        //default menu status
        showStatus = true;
        showMenu = true;
        showDatabase = false;
        showHighlight = false;

        //Sets JOptionPane theme
        UIManager UI = new UIManager();
        UI.put("OptionPane.messageForeground", mainColor);
        UI.put("OptionPane.background", Color.BLACK);
        UI.put("Panel.background", Color.BLACK);
        UI.put("Button.background", Color.BLACK);
        UI.put("Button.foreground", Color.WHITE);
        UI.put("Button.highlight", Color.WHITE);
        UI.put("TextField.background", Color.BLACK);
        UI.put("TextField.selectionBackground", mainColor);
        UI.put("TextField.foreground", Color.WHITE);
        UI.put("TextField.selectionForeground", Color.BLACK);

        repaint();
    }

    /** Ticks matrix to next generation according to the
     *  rules of the Conway's Game of Life.
     */
    public void tick() {
        int numRows = matrix.getNumRows();
        int numColumns = matrix.getNumCols();

        //next generation matrix
        CellMatrix g2 = new CellMatrix(numRows, numColumns);
        //navigates grid horizontally
        for (int x = 0; x < numRows; x++) {
            //navigates grid vertically
            for (int y = 0; y < numColumns; y++) {
                //Gen 1 Cell at index
                Cell c = matrix.getCell(x, y);
                //num of living neighbors
                int numLiving = matrix.numLivingNeighbors(x, y, wrapEnabled);
                if (!c.isAlive() && numLiving == 3) {
                    //reproduction
                    g2.getCell(x, y).revive();
                }
                else if (c.isAlive() && (numLiving < 2 || numLiving > 3)) {
                    //over or under population
                    g2.getCell(x, y).kill();
                }
                else if (c.isAlive()) {
                    //if previously alive
                    g2.getCell(x, y).revive();
                }
                else {
                    //isolation or previously dead
                    g2.getCell(x, y).kill();
                }
                if(c.isSpotlit()) {
                    //if cell is spotlit
                    g2.getCell(x, y).spotlight();
                }
            }
        }
        //migrates previous to current generation
        matrix = g2;
        numTicks++;
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

    @Override
    /** Paint method for MainPanel that draws
     *  the CellMatrix, status, and menu.
     *
     * @param g graphics
     */
    public void paintComponent(Graphics g) {
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
            paintMenu(g);
            g.setFont(mainFont);
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
        int boxHeight = 25;
        int boxWidth = 75;
        int statusWidth = boxWidth / 3;
        int border = 10;
        int pX = AppDriver.WIDTH - boxWidth - border;
        int pY = border;

        Shape tickBox = new Rectangle(pX, pY, boxWidth, boxHeight);
        g2.setColor(Color.black);
        g2.fill(tickBox);

        Shape status = new Rectangle(pX + (2 * statusWidth), pY, statusWidth, boxHeight);
        g2.setColor(Color.RED);
        if (timer.isRunning()) {
            g2.setColor(Color.GREEN);
        }

        g2.fill(status);
        g2.setColor(Color.white);

        if (!wrapEnabled) {
            g2.draw(tickBox);
        }

        String digits = String.valueOf(numTicks);
        g2.setFont(new Font(g2.getFont().getFontName(), Font.PLAIN, boxHeight / 2));

        if (digits.length() > 4) {
            digits = digits.substring(0, digits.length() - 4) + "K";
        }
        while (digits.length() < 4) {
            digits = "0" + digits;
        }
        FontMetrics metrics = getFontMetrics(g2.getFont());
        int dY = metrics.getAscent();
        int dX = metrics.stringWidth(digits);
        //centers text vertically
        pY += boxHeight - ((boxHeight - dY) / 2) - 1;
        //centers text horizontally
        boxWidth = 2 * boxWidth / 3;
        pX += ((boxWidth - dX) / 2);

        g2.drawString(digits, pX, pY);
    }

    /** Displays the keycodes to
     *  manipulate simulation.
     *
     * @param g graphics
     */
    public void paintMenu(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        //list of menu items + current status
        int numRows = matrix.getNumRows();
        int numColumns = matrix.getNumCols();

        String[] menuItems = {"Automata"                                       //title
                                    + " (" + numRows + "x" + numColumns + ") " //size
                                    + "(" + delay + "ms)",                     //speed
                                "Toggle Simulation [SPACE]",
                                "Resize Grid [Q/E]",
                                "Change Speed [A/D]",
                                "Wrap-Around Grid [W]",
                                "Generate Random Seed [S]",
                                "Save [Z]",
                                "Toggle Grid [X]",
                                "Clear [C]",
                                "Open Database [J]",
                                "Search wikicollections [;]",
                                "Navigate Database [U/N]",
                                "Rename Selected [H]",
                                "Remove Selected [K]",
                                "Clear Selection [M]",
                                "Wipe Database [L]",
                                "Toggle Menu [T]",
                                "Toggle Status [R]"};

        //font defining aspects
        FontMetrics metrics = getFontMetrics(g.getFont());
        //position and size variables
        int d = metrics.getAscent();
        int boxHeight = (d + 2) * menuItems.length + 10;
        int boxWidth = 200;
        int border = 10;
        int pX = AppDriver.WIDTH - boxWidth - border;
        int pY = AppDriver.HEIGHT - boxHeight - border;

        //box for menu background
        Shape menuBackground = new Rectangle(pX, pY, boxWidth, boxHeight);
        g2.setColor(Color.black);
        g2.fill(menuBackground);
        //OPT: menu border
        //g2.setColor(Color.white);
        //g2.draw(menuBackground);

        for (String item : menuItems) {
            //loops through menu items
            if(item.equals(menuItems[0])) {
                //if title
                g2.setColor(mainColor);
                g2.setFont(new Font(g2.getFont().getFontName(), Font.PLAIN, 4 * g2.getFont().getSize() / 3));

                FontMetrics metricsTitle = getFontMetrics(g.getFont());
                pY += 4;
            }
            else {
                g2.setColor(Color.white);
                g2.setFont(mainFont);
            }
            g2.drawString(item, pX + 5, pY + d);
            pY += d + 2;
        }
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
                int[] coords = matrix.getCellCoordinates(cell);
                //centers placement
                coords[0] -= cm.getNumRows() / 2;
                coords[1] -= cm.getNumCols() / 2;
                matrix.placeCellMatrix(coords[0], coords[1], cm);
            }
            else if (cell != null) cell.flip();
        }
        else if(button == MouseEvent.BUTTON3) {
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
            int[] coords = matrix.getCellCoordinates(cell);
            coords[0] -= cm.getNumRows() / 2;
            coords[1] -= cm.getNumCols() / 2;
            matrix.spotlightPlacement(coords[0], coords[1], cm);
        }
        else if (cell != null) cell.spotlight();
        if(showHighlight) {
            //resets spotlight
            showHighlight = false;
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
        tick();
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
        if (e.getKeyCode() == KeyEvent.VK_D && timer.getDelay() > 1) {
            //speeds up timer on 'D'
            if (delay >= 100) {
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
            if (showHighlight) {
                //TO-DO
            }
            database.add(matrix.toMatrixData());

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

        if (showDatabase)
            if(e.getKeyCode() == KeyEvent.VK_SEMICOLON) {
                //Prompts User for search term
                String s = (String) JOptionPane.showInputDialog(
                        this, "Search wikicollection:", "Database",
                        JOptionPane.PLAIN_MESSAGE, null, null, "Enter Search Here");
                if (s != null) {
                    //if name changed
                    database.addFromSearch(s);
                }
            }
            if (database.databaseSize() > 0) {
                //if database is visible and not empty
                if (e.getKeyCode() == KeyEvent.VK_U) {
                    //if database showing and exists, navigate down and update on 'U'
                    indexDB--;
                    if (indexDB < 0) {
                        indexDB = database.databaseSize() - 1;
                    }
                    importFromDB();
                }
                if (e.getKeyCode() == KeyEvent.VK_N) {
                    //if database showing and exists, navigate down and update on 'M'
                    indexDB++;
                    if (indexDB >= database.databaseSize()) {
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
                        if (indexDB == database.databaseSize()) {
                            indexDB--;
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
        repaint();
    }

    /** Accesses MatrixData from internal index and
     *  updates numRows, numColumns and matrix
     */
    private void importFromDB() {
        MatrixData m = database.get(indexDB);
        int[] size = m.getSize();
        numTicks = 0;
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
        if (notches > 0) {
            if (matrix.getNumRows() < AppDriver.WIDTH / 4) {
                numTicks = 0;
                changeGrid(increment);
            }
        }
        else {
            if (matrix.getNumRows() > increment) {
                numTicks = 0;
                changeGrid(-1 * increment);
            }
        }
        repaint();
    }
}
