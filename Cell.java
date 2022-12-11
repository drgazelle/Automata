import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

/** Cell Class renders a cell
 *  that is either alive or dead.
 *
 * @author RMizelle
 */
public class Cell {
    private Rectangle2D gridCell;
    private boolean alive;
    //display variables
    public static boolean gridEnabled = true;
    private boolean spotlit = false;

    /** 4-arg constructor that instantiates a Cell
     *
     * @param alive mortality of cell
     * @param pX position X
     * @param pY position Y
     * @param size side lengths
     */
    public Cell(boolean alive, double pX, double pY, double size) {
        this.alive = alive;
        gridCell = new Rectangle2D.Double(pX, pY, size, size);
    }

    /** 3-arg constructor that instantiates a default Cell
     *
     * @param pX position X
     * @param pY position Y
     * @param size side lengths
     */
    public Cell(double pX, double pY, double size) {
        alive = false;
        gridCell = new Rectangle2D.Double(pX, pY, size, size);
    }

    /** Returns Mortality of Cell
     *
     * @return true if Cell is living, false otherwise
     */
    public boolean isAlive() {
        return alive;
    }

    /** Sets alive to false */
    public void kill() {
        alive = false;
    }

    /** Sets alive to true */
    public void revive() {
        alive = true;
    }

    /** Inverses mortality status. */
    public void flip() {
        alive = !alive;
    }

    /** Spotlights cell */
    public void spotlight() {
        spotlit = true;
    }

    /** Removes spotlight from cell */
    public void unspotlight() {
        spotlit = false;
    }

    /** Accessor method for gridCell
     *
     * @return gridCell shape
     */
    public Shape getGridCell() {
        return gridCell;
    }

    /** Draws cell depending on configuration */
    public void drawCell(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        //generates respective color
        float[] hsbVal = Color.RGBtoHSB(200, 200, 200, null);
        //additionally brightness for dead cells
        double brightness = 0.0;
        //darkens for dead cells
        if(!alive) {
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
        if (gridEnabled) {
            g2.setColor(Color.black);
            g2.draw(gridCell);
        }
    }
}
