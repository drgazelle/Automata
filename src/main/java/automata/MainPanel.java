package automata;

import dynamicpanel.*;

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


    //Animation Variables
    public static Color mainColor;
    public static Font mainFont;
    public static Font titleFont;

    private final Timer timer;
    private int numTicks;
    private int delay;

    private boolean showStatus;
    private boolean showGrid;
    private boolean showMenu;
    private boolean showDatabase;
    private boolean showHighlight;
    private boolean wrapEnabled;
    private boolean showStats;
    private boolean showBuffer;
    private boolean showModifier;

    //Menu Object
    private final DynamicPanel mainMenu;
    private final Simulation simulation;

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
        showBuffer = true;
        showMenu = true;
        showStats = false;
        showDatabase = false;
        showHighlight = false;
        showGrid = true;
        showModifier = false;

        //Sets Application Theme
        mainColor = new Color((int) (Math.random() * 0x1000000));
        mainFont = new Font("SansSerif", Font.PLAIN, 10);
        titleFont = new Font(mainFont.getFontName(), Font.BOLD, 4 * mainFont.getSize() / 3);
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

        //database variables
        database = new Database(20);

        //Main menu
        mainMenu = new DynamicPanel();
        updateMenu();

        //Simulation
        simulation = new Simulation(125, 18);
        simulation.reset(matrix);

        repaint();
    }

    /** Resizes grid with given navigateUp.
     *
     * @param i navigateUp numItems
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
        g.setFont(mainFont);
        if(showHighlight) {
            Rectangle rect = new Rectangle();
            rect.setFrameFromDiagonal(endPoint, startPoint);
            matrix.spotlightAll(rect);
        }
        matrix.drawMatrix(g, showGrid);
        if (showStatus) {
            paintStatus(g);
            g.setFont(mainFont);
        }
        if (showMenu) {
            int pX = AppDriver.WIDTH - 10 - mainMenu.getWidth();
            int pY = AppDriver.HEIGHT - 10 - mainMenu.getHeight();
            mainMenu.draw(g, pX, pY);
        }
        if (showDatabase) {
            database.paintDatabase(g);
            g.setFont(mainFont);
        }
        if (showStats) {
            simulation.draw(g, 10, AppDriver.HEIGHT - simulation.getBoxHeight() - 10);
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

        //Background box
        Shape tickBox = new Rectangle(pX, pY, boxWidth, boxHeight);
        g2.setColor(Color.black);
        g2.fill(tickBox);

        //Running indicator
        Shape indicator = new Rectangle(pX + (2 * boxHeight), pY, boxWidth / 3, boxHeight);
        g2.setColor(Color.RED);
        if (timer.isRunning()) {
            g2.setColor(Color.GREEN);
        }
        g2.fill(indicator);
        if (showBuffer && matrix.getBufferSize() > 0) {
            //Draws buffer progress
            //draws buffer numItems bar
            g2.setColor(Color.darkGray);
            int line_length = (int) ((double) boxWidth / matrix.getBufferMax() * matrix.getBufferSize());
            g2.fillRect(pX, pY + boxHeight + boxHeight / 8, line_length, boxHeight / 8);

            g2.setColor(Color.gray);
            line_length = (int) ((double) boxWidth / matrix.getBufferMax() * (matrix.getBufferIndex() + 1));
            g2.fillRect(pX, pY + boxHeight + boxHeight / 8, line_length, boxHeight / 8);
        }

        //wrap indicator
        g2.setColor(Color.white);
        if (!wrapEnabled) {
            g2.draw(tickBox);
        }

        g2.setColor(Color.white);
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
                                "Resize Grid [Q/E]",
                                "Change Speed [A/D]",
                                "Wrap-Around Grid [W]",
                                "Generate Random Seed [S]",
                                "Clear [C]",
                                "Toggle Grid [X]",
                                "Toggle Status [T]",
                                "Toggle Statistics [F]",
                                "Toggle Menu [G]",
                                "Toggle Modifiers [SHIFT]"};
        String[] modifierItems = {"Forward Tick [SHIFT + A]",
                                "Reverse Tick [SHIFT + D]",};
        String[] databaseItems = {"Close Database [J]",
                                "Search wiki-collections [;]",
                                "Navigate Database [U/N]",
                                "Rename Selected [H]",
                                "Remove Selected [K]",
                                "Clear Selection [M]",
                                "Save [Z]",
                                "Wipe Database [L]",
                                "Hover to Select"};

        //displays correct information
        String[] displayItems = menuItems;
        if(showModifier) {
            displayItems = modifierItems;
        }
        else if (showDatabase) {
            displayItems = databaseItems;
        }

        //updates mainMenu
        mainMenu.clear();
        mainMenu.addItem(new TextBar(title, titleFont, mainColor));
        for(String i : displayItems) {
            mainMenu.addItem(new TextBar(i, mainFont, Color.white));
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
        if (button == MouseEvent.BUTTON1) {
            Cell cell = matrix.findCellAt(mouseX, mouseY);
            if(cm != null && cell != null) {
                //places cell at coords
                int[] coords = matrix.getCellCoordinates(cell);
                //centers placement
                coords[0] -= cm.getNumRows() / 2;
                coords[1] -= cm.getNumCols() / 2;
                matrix.placeCellMatrix(coords[0], coords[1], cm);
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

    /** Updates position of drag selection
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

        updateSpotlight();

        if(showHighlight) {
            //resets select
            showHighlight = false;
            //resets points
            startPoint = null;
            endPoint = null;
        }

        if(showDatabase) {
            database.select(mouseX, mouseY);
            if(database.isSelected()) {
                importFromDB();
            }
        }
        if(showStats) {
            simulation.showToolTip(mouseX, mouseY);
        }
        repaint();
    }

    /** Updates spotlighted items */
    private void updateSpotlight() {
        matrix.clearSpotlight();
        Cell cell = matrix.findCellAt(mouseX, mouseY);
        if (cm != null && cell != null) {
            //if moving select
            int[] coords = matrix.getCellCoordinates(cell);
            //adjusts placement from center to corner;
            coords[0] -= cm.getNumRows() / 2;
            coords[1] -= cm.getNumCols() / 2;
            matrix.spotlightPlacement(coords[0], coords[1], cm);
        }
        else if (cell != null) cell.spotlight();
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
        simulation.update(numTicks);
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

        if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
            //activates modifiers on 'SHIFT'
            showModifier = true;
        }

        if (showModifier) {
            //if modifier pressed

            if (!timer.isRunning()) {
                //if paused
                if(e.getKeyCode() == KeyEvent.VK_D) {
                    //Singular tick
                    tick();
                }
                if(e.getKeyCode() == KeyEvent.VK_A) {
                    rollback();
                }
            }
        }
        else {
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
                //increases grid numItems on 'E'
                if (numRows < AppDriver.WIDTH / 4) {
                    numTicks = 0;
                    changeGrid(increment);
                    simulation.reset(matrix);
                }
            }
            if(e.getKeyCode() == KeyEvent.VK_Q) {
                //decreases grid numItems on 'Q'
                if (numRows > increment) {
                    numTicks = 0;
                    changeGrid(-1 * increment);
                    simulation.reset(matrix);
                }
            }
            if (e.getKeyCode() == KeyEvent.VK_X) {
                //turns grid on and off on 'X'
                showGrid = !showGrid;
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
            if (e.getKeyCode() == KeyEvent.VK_T) {
                //Two layer toggle for Status
                if(!showStatus) {
                    //case 1: hidden
                    showStatus = true;
                    showBuffer = true;
                }
                else if (showBuffer) {
                    //case 2: all shown
                    showBuffer = false;
                }
                else {
                    //case 3: only status
                    showStatus = false;
                }
            }
            if (e.getKeyCode() == KeyEvent.VK_G) {
                //toggles menu on 'G'
                showMenu = !showMenu;
            }
            if (e.getKeyCode() == KeyEvent.VK_F) {
                //toggles stats on 'F'
                showStats = !showStats;
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
                simulation.reset(matrix);
            }
            if (e.getKeyCode() == KeyEvent.VK_C) {
                //kills all cells on 'C'
                matrix.genocide();
                numTicks = 0;
                simulation.reset(matrix);
            }
            if(e.getKeyCode() == KeyEvent.VK_B) {
                if(mainFont.getSize() < 20) {
                    //increases font numItems if less than 50
                    mainFont = new Font(mainFont.getFontName(), mainFont.getStyle(), mainFont.getSize() + 2);
                    titleFont = new Font(mainFont.getFontName(), titleFont.getStyle(), 4 * mainFont.getSize() / 3);
                }
            }
            if(e.getKeyCode() == KeyEvent.VK_V) {
                if(mainFont.getSize() > 10) {
                    //decreases font numItems if greater than 14
                    mainFont = new Font(mainFont.getFontName(), mainFont.getStyle(), mainFont.getSize() - 2);
                    titleFont = new Font(mainFont.getFontName(), titleFont.getStyle(), 4 * mainFont.getSize() / 3);
                }
            }
        }

        if (e.getKeyCode() == KeyEvent.VK_J) {
            // toggles database and resets index 0n 'J'
            showDatabase = !showDatabase;
            database.clearSelection();
            cm = null;
            matrix.clearSpotlight();
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
            if (database.size() > 0) {
                //if database is visible and not empty
                if (e.getKeyCode() == KeyEvent.VK_U) {
                    //if database showing and exists, navigate down and update on 'U'
                    database.navigateDown();
                    importFromDB();
                }
                if (e.getKeyCode() == KeyEvent.VK_N) {
                    //if database showing and exists, navigate down and update on 'M'
                    database.navigateUp();
                    importFromDB();
                }
                if (database.isSelected()) {
                    //if item selected
                    if (e.getKeyCode() == KeyEvent.VK_H) {
                        //Renames on 'H'
                        MatrixData m = database.get();
                        String name = m.getTitle();

                        //Prompts User for new Name
                        String s = (String) JOptionPane.showInputDialog(
                                this, "Modify Matrix Name Below:", "Database",
                                JOptionPane.PLAIN_MESSAGE, null, null, name);
                        if (s != null) {
                            //if name changed
                            m.setTitle(s);
                            database.updateTitle(s);
                        }
                    }
                    if (e.getKeyCode() == KeyEvent.VK_K) {
                        //removes CellMatrix at Index
                        database.deleteIndex();
                        if(database.size() > 0) {
                            importFromDB();
                        }
                        else {
                            cm = null;
                            database.clearSelection();
                        }
                    }
                    if (e.getKeyCode() == KeyEvent.VK_G) {
                        //displays RLE String on 'G'
                        MatrixData m = database.get();
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
                        database.clearSelection();
                        matrix.clearSpotlight();
                    }
                }
                if (e.getKeyCode() == KeyEvent.VK_L) {
                    //wipes database on 'L'
                    database.wipe();
                    database.clearSelection();
                    cm = null;
                    matrix.clearSpotlight();
                }
            }
        }
        updateMenu();
        repaint();
    }

    private void rollback() {
        //rewinds matrix
        if (matrix.rollback()) {
            numTicks--;
            simulation.update(numTicks);
        }
    }

    private void tick() {
        matrix.tick(wrapEnabled);
        numTicks++;
        simulation.update(numTicks);
    }

    /** Accesses MatrixData from internal index and
     *  updates numRows, numColumns and matrix
     */
    private void importFromDB() {
        MatrixData m = database.get();
        cm = m.toCellMatrix();
        updateSpotlight();
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
        if(showModifier) {
            if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
                showModifier = false;
                updateMenu();
                repaint();
            }
        }
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
