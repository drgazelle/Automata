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
    private Rectangle2D gridCell;
    private boolean alive;
    private boolean spotlit;

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

    /** Removes spotlight from cell */
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

    /**
     * Draws cell depending on configuration.
     *
     * @param g        graphics component
     * @param showGrid
     */
    public void drawCell(Graphics g, boolean showGrid) {
        Graphics2D g2 = (Graphics2D) g;
        //generates respective color
        float[] hsbVal = Color.RGBtoHSB(200, 200, 200, null);
        //additionally brightness for dead cells
        double brightness = 0.0;
        //darkens for dead cells
        if (!alive) {
            hsbVal[2] = (float) (hsbVal[2] * 0.10);
            //increments brightness
            brightness++;
        }
        //lightens for highlight
        if (spotlit) {
            hsbVal[2] = (float) (hsbVal[2] * (1.25 + brightness));
        }
        g2.setColor(Color.getHSBColor(hsbVal[0], hsbVal[1], hsbVal[2]));
        //fills gridCell
        g2.fill(gridCell);
        //adds border
        if (showGrid) {
            g2.setColor(Color.black);
            g2.draw(gridCell);
        }
    }
}
