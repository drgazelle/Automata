import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

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
    private int size;

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

    public int getNumRows() {
        return numRows;
    }

    public int getNumCols() {
        return numCols;
    }


    /** Accessor Method for Cell.
     *
     * @param pX position x
     * @param pY position y
     * @return Cell at position
     */
    public Cell getCell(int pX, int pY) {
        return matrix[pX][pY];
    }

    /** Ticks matrix to next generation according to the
     *  rules of the Conway's Game of Life.
     */
    public void tick(Boolean wrapEnabled) {

        //next generation matrix
        CellMatrix g2 = new CellMatrix(numRows, numCols);
        //navigates grid horizontally
        for (int x = 0; x < numRows; x++) {
            //navigates grid vertically
            for (int y = 0; y < numCols; y++) {
                //Gen 1 Cell at index
                Cell c = matrix[x][y];
                //num of living neighbors
                int numLiving = numLivingNeighbors(x, y, wrapEnabled);
                if (!c.isAlive() && numLiving == 3) {
                    //reproduction
                    g2.matrix[x][y].revive();
                }
                else if (c.isAlive() && (numLiving < 2 || numLiving > 3)) {
                    //over or under population
                    g2.matrix[x][y].kill();
                }
                else if (c.isAlive()) {
                    //if previously alive
                    g2.matrix[x][y].revive();
                }
                else {
                    //isolation or previously dead
                    g2.matrix[x][y].kill();
                }
                if(c.isSpotlit()) {
                    //if cell is spotlit
                    g2.matrix[x][y].spotlight();
                }
            }
        }
        //migrates previous to current generation
        matrix = g2.matrix;
    }

    /** Counts the number of neighbors that are living.
     *
     * @param pX position X
     * @param pY position Y
     * @return number of living neighbors
     */
    public int numLivingNeighbors(int pX, int pY, boolean wrapEnabled) {
        //sets bounds for X
        int startX = pX - 1;
        int endX = pX + 1;
        //fixes conditions for end cases
        if (startX < 0) startX++;
        if (endX >= matrix.length) endX--;

        //sets bounds for Y
        int startY = pY - 1;
        int endY = pY + 1;
        //fixes conditions for end cases
        if (startY < 0) startY++;
        if (endY >= matrix[0].length) endY--;

        //resets count
        int num = 0;
        //loops through surrounding grid, horizontally
        for (int x = startX; x <= endX; x++) {
            //loops through surrounding grid, vertically
            for(int y = startY; y <= endY; y++) {
                //skips over compared cell
                if(x == pX && y == pY) {
                    if (y + 1 < matrix[0].length) {
                        //if inside matrix, increment
                        y++;
                    }
                    else {
                        //else skip over
                        continue;
                    }
                }
                if(matrix[x][y].isAlive()) {
                    //if alive, increment
                    num++;
                }
            }
        }

        if(wrapEnabled) {
            //position of sides
            int sideL = 0;
            int sideR = matrix.length - 1;
            int sideT = 0;
            int sideB = matrix[0].length - 1;

            if (pX == sideL) {
                //if left side
                if (pY == sideT) {
                    //if top left corner
                    if (matrix[sideR][sideB].isAlive()) {
                        //if bottom right corner alive, increment
                        num++;
                    }
                }
                if (pY == sideB) {
                    //if bottom left corner
                    if(matrix[sideR][sideT].isAlive()) {
                        //if top right corner alive, increment
                        num++;
                    }
                }
                for (int y = startY; y <= endY; y++) {
                    //loops through opposite side
                    if (matrix[sideR][y].isAlive()) {
                        //if alive, increment
                        num++;
                    }
                }
            }
            else if (pX == sideR) {
                //if right side
                if (pY == sideT) {
                    //if top right corner
                    if (matrix[sideL][sideB].isAlive()) {
                        //if bottom left corner alive, increment
                        num++;
                    }
                }
                if (pY == sideB) {
                    //if bottom right corner
                    if(matrix[sideL][sideT].isAlive()) {
                        //if top left corner alive, increment
                        num++;
                    }
                }
                for (int y = startY; y <= endY; y++) {
                    //loops through opposite side
                    if (matrix[sideL][y].isAlive()) {
                        //if alive, increment
                        num++;
                    }
                }
            }
            if (pY == sideT) {
                for (int x = startX; x <= endX; x++) {
                    //loops through opposite side
                    if (matrix[x][sideB].isAlive()) {
                        //if alive, increment
                        num++;
                    }
                }
            }
            else if (pY == sideB) {
                for (int x = startX; x <= endX; x++) {
                    //loops through opposite side
                    if (matrix[x][sideT].isAlive()) {
                        //if alive, increment
                        num++;
                    }
                }
            }
        }
        return num;
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

    /** Finds (x,y) coordinates of a given Cell
     *
     * @param cell to be located
     * @return coordinates of cell object as int[]
     */
    public int[] getCellCoordinates(Cell cell) {
        for (int x = 0; x < matrix.length; x++) {
            for(int y = 0; y <matrix[0].length; y++) {
                if (matrix[x][y].equals(cell)) {
                    int[] coords = {x, y};
                    return coords;
                }
            }
        }
        return null;
    }

    /** Places cell matrix at given coordinate
     *
     * @param pX x position
     * @param pY y position
     * @param cm matrix to be placed
     */
    public boolean placeCellMatrix(int pX, int pY, CellMatrix cm) {
        //Adjusts for bounds
        int[] coords = adjustToBounds(pX, pY, cm);
        if(coords == null) {
            return false;
        }
        pX = coords[0];
        pY = coords[1];

        for (int x = pX; x < cm.matrix.length + pX; x++) {
            for (int y = pY; y < cm.matrix[0].length + pY; y++) {
                if(cm.matrix[x - pX][y - pY].isAlive()) {
                    matrix[x][y].revive();
                }
                else {
                    matrix[x][y].kill();
                }
            }
        }
        return true;
    }

    /** Adjusts position to fit within matrix, returns null if exceeds
     *  bounds.
     * @param pX horizontal position
     * @param pY vertical position
     * @param cm CellMatrix to adjust
     * @return adjusted coordinates [x, y], null if too large
     */
    private int[] adjustToBounds(int pX, int pY, CellMatrix cm) {
        //if out-of-bounds
        if(cm.numRows > numRows || cm.numCols > numCols) {
            return null;
        }
        //adjusts x position
        while (pX + cm.numRows > numRows) {
            pX--;
        }
        while (pX < 0) {
            pX++;
        }

        //adjust y position
        while (pY + cm.numCols > numCols) {
            pY--;
        }
        while (pY < 0) {
            pY++;
        }
        int[] temp = {pX, pY};
        return temp;
    }

    /** Randomly generates starter seed with given
     *  probability.
     *
     * @param probability % chance for cell to be alive
     */
    public void randomSeed(double probability) {
        for (int x = 0; x < matrix.length; x++) {
            for (int y = 0; y < matrix[0].length; y++) {
                if (Math.random() < probability) {
                    matrix[x][y].revive();
                }
            }
        }
    }

    /** Kills all cells. */
    public void genocide() {
        for (int x = 0; x < matrix.length; x++) {
            for (int y = 0; y < matrix[0].length; y++) {
                matrix[x][y].kill();
            }
        }
    }

    /** String representation of CellMatrix
     * @return (W*H)[x,y][x,y]...[x,y]
     */
    public String toString() {
        String temp = "(" + matrix.length + "x" + matrix[0].length + ")";
        for (int x = 0; x < matrix.length; x++) {
            for (int y = 0; y < matrix[0].length; y++) {
                if(matrix[x][y].isAlive()) {
                    temp += "[" + x + "," + y + "]";
                }
            }
        }
        return temp;
    }
}
