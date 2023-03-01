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

    public int getNumRows() {
        return numRows;
    }

    public int getNumCols() {
        return numCols;
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
     * @param wrapEnabled edge condition
     *
     * @return number of living neighbors
     */
    private int numLivingNeighbors(int pX, int pY, boolean wrapEnabled) {
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
        StringBuilder sb = new StringBuilder();
        for(int c = 0; c < matrix[0].length; c++) {
            for(int r = 0; r <= endIndex(c); r++) {
                if(endIndex(c) == -1) {
                    //if empty line
                    break;
                }
                int i = 1;
                if (matrix[r][c].isAlive()) {
                    //if living cell
                    while (r < numRows - 1 && matrix[r + 1][c].isAlive()) {
                        //adds range of living cells
                        r++;
                        i++;
                    }
                    if(i > 1) {
                        //adds quantity if more than one
                        sb.append(i);
                    }
                    //labels as living
                    sb.append("o");
                }
                else {
                    //if dead cell
                    while (r < numRows - 1 && !matrix[r + 1][c].isAlive()) {
                        //adds range of dead cells
                        r++;
                        i++;
                    }
                    if(i > 1) {
                        //adds quantity if more than one
                        sb.append(i);
                    }
                    //labels as dead
                    sb.append("b");
                }
            }
            if(c < matrix[0].length - 1) {
                //end line
                sb.append("$");
            }
        }
        //end of matrix
        sb.append("!");
        return sb.toString();
    }

    /** Finds last index of living cell in a row
     *
     * @param c roll of cells
     * @return index of living cell
     */
    private int endIndex(int c) {
        for(int r = matrix.length - 1; r >= 0; r--) {
            if(matrix[r][c].isAlive())
                return r;
        }
        return -1;
    }

    /** Converts RLE to CellMatrix */
    public void fromRLE(String rleString) {
        //reset matrix
        genocide();

        //position variables
        int x = 0;
        int y = 0;

        //Navigates RLE
        char[] rleArr = rleString.toCharArray();
        for(int i = 0; rleArr[i] != '!'; i++) {
            //Finds numerical quantity
            StringBuilder num = new StringBuilder();
            while (Character.isDigit(rleArr[i])) {
                num.append(rleArr[i]);
                i++;
            }
            if (rleArr[i] == '$') {
                //new line
                x = 0;
                y++;
            }
            else if (rleArr[i] == 'b') {
                //if dead cell
                if (num.length() == 0) {
                    //if single dead cell
                    x++;
                } else {
                    //else multiple dead cells
                    x += Integer.parseInt(num.toString());
                }
            }
            else if (rleArr[i] == 'o') {
                //else if alive
                if (num.length() == 0) {
                    //if single dead cell
                    matrix[x][y].revive();
                    x++;
                }
                else {
                    //else multiple dead cells
                    int range = x + Integer.parseInt(num.toString());
                    while (x < range) {
                        matrix[x][y].revive();
                        x++;
                    }
                }
            }
            else {
                //if incorrectly formatted
                genocide();
                System.out.println("ERROR: Failed to import RLE");
                System.out.println("Input Mismatch at (" + x + ", " + y + ") with Character '" + rleArr[i] + "'");
                return;
            }
        }
    }
}