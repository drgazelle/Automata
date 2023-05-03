package automata;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

/** Cell Class renders a selectable cell
 *  that is either alive or dead.
 *
 * @author RMizelle
 */
public class Cell {
    private final Rectangle2D gridCell;
    private boolean alive;
    private boolean spotlit;
    private Color mainColor = new Color(0xc8c8c8);
    private Color backColor = new Color(0x141414);

    /** 3-arg constructor that instantiates a default Cell.
     *
     * @param pX position X
     * @param pY position Y
     * @param size side lengths
     */
    public Cell(double pX, double pY, double size) {
        gridCell = new Rectangle2D.Double(pX, pY, size, size);
    }

    /** Returns Mortality of Cell.
     *
     * @return true if Cell is living, false otherwise
     */
    public boolean isAlive() {
        return alive;
    }

    /** Sets alive to false. */
    public void kill() {
        alive = false;
    }

    /** Sets alive to true. */
    public void revive() {
        alive = true;
    }

    /** Inverses mortality status. */
    public void flip() {
        alive = !alive;
    }

    /** Accessor Method for Spotlit.
     *
     * @return true if spotlit, false otherwise.
     */
    public boolean isSpotlit() {
        return spotlit;
    }

    /** Spotlights cell */
    public void spotlight() {
        spotlit = true;
    }

    /** Removes select from cell */
    public void unspotlight() {
        spotlit = false;
    }

    /** Accessor method for gridCell.
     *
     * @return gridCell shape
     */
    public Shape getGridCell() {
        return gridCell;
    }

    /** Sets cell color to heat map
     *
     * @param numLiving living neighbors
     */
    public void heatMap(int numLiving) {
        int[] hexValues = new int[]{0x81D58, 0xA2c6b,0x0D47a1, 0x1976D2, 0x2196F3, 0xFFFF00, 0xFFC107,
                                        0xFF5722, 0xdE64A19, 0xB71c1c};
        backColor = new Color(hexValues[0]);
        mainColor = new Color(hexValues[numLiving + 1]);
    }

    public Color getMainColor() {
        return mainColor;
    }

    public void setMainColor(Color c) {
        mainColor = c;
    }

    public void setBackColor(Color c) {
        backColor = c;
    }

    /**
     * Draws cell depending on configuration.
     *
     * @param g        graphics component
     * @param showGrid if grid enabled
     */
    public void drawCell(Graphics g, boolean showGrid) {
        Graphics2D g2 = (Graphics2D) g;
        //generates respective color
        Color c;
        //darkens for dead cells
        if (!alive) {
            c = backColor;
        }
        else {
            c = mainColor;
        }
        //lightens for highlight
        if (spotlit) {
            c = c.brighter().brighter();
        }
        g2.setColor(c);
        //fills gridCell
        g2.fill(gridCell);
        //adds border
        if (showGrid) {
            g2.setColor(Color.black);
            g2.draw(gridCell);
        }
    }
}
