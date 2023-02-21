import java.awt.Graphics;

/** CellMatrix class generates and
 *  modifies a 2D array of Cell
 *  Objects that can generate and modify.
 *
 * @author RMizelle
 */
public class CellMatrix {
    private Cell[][] matrix;
    private final int numRows;
    private final int numCols;

    /**
     * 2-arg constructor instantiates a 2D matrix
     * of Cell objects of size numRows by NumCols.
     *
     * @param numR Matrix length
     * @param numC Matrix numCols
     */
    public CellMatrix(int numR, int numC) {
        numRows = numR;
        numCols = numC;

        matrix = new Cell[numRows][numCols];
        double size = ((double) AppDriver.WIDTH) / numR;
        for (int x = 0; x < numR; x++) {
            for (int y = 0; y < numC; y++) {
                matrix[x][y] = new Cell(x * size, y * size, size);
            }
        }
    }

    /** Accessor method for numRows
     *
     * @return number of rows
     */
    public int getNumRows() {
        return numRows;
    }

    /** Accessor method for numCols
     *
     * @return number of columns
     */
    public int getNumCols() {
        return numCols;
    }

    /** Ticks matrix to next generation according to the
     *  rules of the Conway's Game of Life.
     */
    public void tick(Boolean wrapEnabled) {
        //TO-DO
    }

    /** Counts the number of neighbors that are living.
     *
     * @param pX position X
     * @param pY position Y
     * @param wrapEnabled edge condition
     *
     * @return number of living neighbors
     */
    private int numLivingNeighbors(int pX, int pY, boolean wrapEnabled) {
        //TO-DO
        return -1;
    }

    /** Passes through paintComponent and
     *  draws Cell Matrix.
     *
     * @param g graphics
     */
    public void drawMatrix(Graphics g) {
        for (Cell[] row : matrix) {
            for (Cell c : row) {
                c.drawCell(g);
            }
        }
    }

    /** removes spotlight from all Cells */
    public void clearSpotlight() {
        for (Cell[] row : matrix) {
            for(Cell c : row) {
                c.unspotlight();
            }
        }
    }

    /** Finds Cell at given mouse location.
     *
     * @param mouseX mouse horizontal position
     * @param mouseY mouse vertical position
     * @return Cell at mouseX, MouseY, null if not found
     */
    public Cell findCellAt(int mouseX, int mouseY) {
        for (Cell[] row : matrix) {
            for (Cell c : row) {
                if (c.getGridCell().contains(mouseX, mouseY)) {
                    return c;
                }
            }
        }
        return null;
    }

    /** Randomly generates starter seed with given
     *  probability.
     *
     * @param probability % chance for cell to be alive
     */
    public void randomSeed(double probability) {
        for (Cell[] cells : matrix) {
            for (Cell c : cells) {
                if (Math.random() < probability) {
                    c.revive();
                }
            }
        }
    }

    /** Kills all cells. */
    public void genocide() {
        for (Cell[] cells : matrix) {
            for (Cell c : cells) {
                c.kill();
            }
        }
    }

    /** String representation of CellMatrix in RLE format
     *
     * @return RLE string (i.e. bo$2bo$3o!)
     */
    @Override
    public String toString() {
        //TO-DO
        return "";
    }

    /** Finds last index of living cell in a row
     *
     * @param c roll of cells
     * @return index of living cell
     */
    private int endIndex(int c) {
        //TO-DO
        return -1;
    }

    /** Converts RLE to CellMatrix */
    public void fromRLE(String rleString) {
        //TO-DO
    }
}