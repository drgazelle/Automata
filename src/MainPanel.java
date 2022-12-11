import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.*;
import java.security.Key;

/** MainPanel class renders a CellMatrix
 *  representing Conway's Game of Life.
 *
 * @author RMizelle
 */
public class MainPanel extends JPanel implements MouseListener, MouseMotionListener, KeyListener, ActionListener {
    //Mouse Positions
    private int mouseX;
    private int mouseY;


    //Grid Variables
    private CellMatrix matrix;
    private int numRows = 100;
    private int numColumns = 100;
    private final double maxP = 0.30;
    private final int increment = 5;


    private final Database database;

    //Animation Variables
    private final Timer timer;
    private int numTicks = 0;
    private int delay = 100;
    private boolean showStatus = true;
    private boolean showMenu = true;
    private boolean wrapEnabled = true;
    private boolean showDatabase = false;
    private int indexDatabase = -1;

    /** 0-arg constructor adds Mouse Listeners
     *  and instantiates the matrix and timer.
     */
    public MainPanel() {
        //adds listeners
        addMouseListener(this);
        addMouseMotionListener(this);
        addKeyListener(this);
        this.setFocusable(true);
        //instantiates matrix
        matrix = new CellMatrix(numRows, numColumns);
        matrix.randomSeed(maxP);
        //instantiates timer
        timer = new Timer(delay, this);
        //database variables
        database = new Database();
        repaint();
    }

    /** Ticks matrix to next generation according to the
     *  rules of the Conway's Game of Life.
     */
    public void tick() {
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
        numColumns += i;
        numRows += i;
        matrix = new CellMatrix(numRows, numColumns);
    }

    @Override
    /** Paint method for MainPanel that draws
     *  the CellMatrix, status, and menu.
     *
     * @param g graphics
     */
    public void paintComponent(Graphics g) {
        matrix.drawMatrix(g);
        if (showStatus) {
            paintStatus(g);
        }
        if(showMenu) {
            paintMenu(g);
        }
        if (showDatabase) {
            database.paintDatabase(g, indexDatabase);
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

        if (!wrapEnabled) {
            g2.setColor(Color.white);
            g2.draw(tickBox);
        }

        String digits = String.valueOf(numTicks);
        if (digits.length() > 4) {
            digits = digits.substring(0, digits.length() - 4) + "K";
        }
        while (digits.length() < 4) {
            digits = "0" + digits;
        }
        FontMetrics metrics = getFontMetrics(this.getFont());
        int dy = metrics.getAscent() - 24; //MODIFY
        int dx = metrics.stringWidth(digits);
        g2.setColor(Color.white);
        g2.drawString(digits, (pX + ((boxWidth - statusWidth - dx) / 2)), pY + ((boxHeight - dy) / 2));
    }

    /** Displays the keycodes to
     *  manipulate simulation.
     *
     * @param g graphics
     */
    public void paintMenu(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        //list of menu items
        String[] menuItems = {"Automata"
                                    + " (" + numRows + "x" + numColumns + ") "
                                    + "(" + delay + "ms)",
                                "Toggle Simulation [SPACE]",
                                "Change Speed [UP/DOWN]",
                                "Resize Grid [Q/E]",
                                "Wrap-Around Grid [W]",
                                "Generate Random Seed [R]",
                                "Clear [C]",
                                "Open Database [D]",
                                "Save [A]",
                                "Toggle Menu [M]",
                                "Toggle Status [S]",
                                "Toggle Grid [G]"};

        //font defining aspects
        g2.setFont(new Font(g2.getFont().getFontName(), Font.PLAIN, 10));
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
                g2.setColor(Color.GREEN);
                g2.setFont(new Font(g2.getFont().getFontName(), Font.PLAIN, 14));

                FontMetrics metricsTitle = getFontMetrics(g.getFont());
                pY += (metricsTitle.getAscent() / 2) - 3;
            }
            else {
                g2.setColor(Color.white);
                g2.setFont(new Font(g2.getFont().getFontName(), Font.PLAIN, 10));
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
            if (cell != null) cell.flip();
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
        if (cell != null) cell.spotlight();
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
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            //toggles simulation on space-bar
            if(timer.isRunning()) {
                timer.stop();
                repaint();
            }
            else {
                timer.start();
            }
        }
        if (e.getKeyCode() == KeyEvent.VK_UP && timer.getDelay() > 1) {
            //speeds up timer on up arrow
            if (delay >= 100) {
                delay -= 10;
            }
            else {
                delay -= 1;
            }
            timer.setDelay(delay);
            repaint();

        }
        if (e.getKeyCode() == KeyEvent.VK_DOWN && timer.getDelay() < 5000) {
            //slows down timer on up arrow
            if (delay >= 100) {
                delay += 10;
            }
            else {
                delay += 1;
            }
            timer.setDelay(delay);
            repaint();
        }
        if(e.getKeyCode() == KeyEvent.VK_E) {
            //increases grid size on 'E'
            if (numRows < AppDriver.WIDTH / 3) {
                numTicks = 0;
                changeGrid(increment);
                matrix.randomSeed(maxP);
            }
            repaint();
        }
        if(e.getKeyCode() == KeyEvent.VK_Q) {
            //decreases grid size on 'Q'
            if (numRows > increment) {
                numTicks = 0;
                changeGrid(-1 * increment);
                matrix.randomSeed(maxP);
            }
            repaint();
        }
        if (e.getKeyCode() == KeyEvent.VK_G) {
            //turns grid on and off on 'G'
            Cell.gridEnabled = !Cell.gridEnabled;
            repaint();
        }
        if (e.getKeyCode() == KeyEvent.VK_A) {
            database.add(matrix.toMatrixData());
            if(showDatabase) {
                repaint();
            }
        }
        if (e.getKeyCode() == KeyEvent.VK_S) {
            //toggles status on 'S'
            showStatus = !showStatus;
            repaint();
        }
        if (e.getKeyCode() == KeyEvent.VK_M) {
            //toggles menu on 'M'
            showMenu = !showMenu;
            repaint();
        }
        if (e.getKeyCode() == KeyEvent.VK_D) {
            showDatabase = !showDatabase;
            indexDatabase = -1;
            repaint();
        }
        if (e.getKeyCode() == KeyEvent.VK_W) {
            //toggles wrap around on 'W'
            wrapEnabled = !wrapEnabled;
            repaint();
        }
        if (e.getKeyCode() == KeyEvent.VK_R) {
            //randomizes matrix seed on 'R'
            matrix.genocide();
            matrix.randomSeed(maxP);
            numTicks = 0;
            repaint();
        }
        if (e.getKeyCode() == KeyEvent.VK_C) {
            //kills all cells on 'C'
            matrix.genocide();
            numTicks = 0;
            repaint();
        }
        if(showDatabase && e.getKeyCode() == KeyEvent.VK_LEFT && indexDatabase > 0) {
            //if database showing and exists, navigate down and update
            indexDatabase--;
            importFromDatabase();
        }
        if(showDatabase && e.getKeyCode() == KeyEvent.VK_RIGHT && indexDatabase < database.databaseSize() - 1) {
            //if database showing and exists, navigate up and update
            indexDatabase++;
            importFromDatabase();
        }
    }

    /** Accesses MatrixData from internal index and
     *  updates numRows, numColumns and matrix
     */
    private void importFromDatabase() {
        MatrixData m = database.get(indexDatabase);
        int[] size = m.getSize();
        numRows = size[0];
        numColumns = size[1];
        matrix = m.toCellMatrix();
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
}
