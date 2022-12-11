import java.awt.Graphics;
import java.util.ArrayList;

/** CellMatrix class generates and
 *  modifies a 2D array of Cell
 *  Objects that can generate and modify.
 *
 * @author RMizelle
 */
public class CellMatrix {
    private Cell[][] matrix;
    private int size;

    /** 2-arg constructor instantiates a 2D matrix
     *  of Cell objects of size numRows by Num Columns.
     *
     * @param numRows Matrix length
     * @param numColumns Matrix height
     */
    public CellMatrix(int numRows, int numColumns) {
        matrix = new Cell[numRows][numColumns];
        double size = ((double) AppDriver.WIDTH) / numRows;
        for (int x = 0; x < numRows; x++) {
            for (int y = 0; y < numColumns; y++) {
                matrix[x][y] = new Cell(x * size, y * size, size);
            }
        }
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

    /** removes spotlight from all Cells
     *
     */
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

    /** Converts Matrix to MatrixData
     *
     * @return MatrixData representation of CellMatrix
     */
    public MatrixData toMatrixData() {
        int[] size = {matrix.length, matrix[0].length};
        ArrayList<int[]> cells = new ArrayList<>();
        for (int x = 0; x < matrix.length; x++) {
            for (int y = 0; y < matrix[0].length; y++) {
                if(matrix[x][y].isAlive()) {
                    int[] cell = {x, y};
                    cells.add(cell);
                }
            }
        }
        return new MatrixData(size, cells);
    }
}
