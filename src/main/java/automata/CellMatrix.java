package automata;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.LinkedList;

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
    private final ArrayList<Cell[][]> buffer;
    private static String ruleString = "B3/S23";
    private static LinkedList<Integer> birth;
    private static LinkedList<Integer> survival;
    private static int bufferMax = 100;
    private static boolean showLegacy = false;

    /**
     * 2-arg constructor instantiates a 2D matrix
     * of Cell objects of numItems numRows by NumCols.
     *
     * @param numR Matrix length
     * @param numC Matrix numCols
     */
    public CellMatrix(int numR, int numC) {
        numRows = numR;
        numCols = numC;

        //instantiates matrix
        matrix = new Cell[numRows][numCols];
        double size = ((double) AppDriver.WIDTH) / numR;
        for (int x = 0; x < numR; x++) {
            for (int y = 0; y < numC; y++) {
                matrix[x][y] = new Cell(x * size, y * size, size);
            }
        }

        //instantiates buffer adding initial state
        buffer = new ArrayList<>();
        buffer.add(matrix);

        //fills rule lists on first run
        if (birth == null || survival == null) {
            fillRuleLists();
        }
    }

    /** Sets status of legacy
     *
     * @param status condition
     */
    public static void setLegacy(boolean status) {
        showLegacy = status;
    }

    /** Accessor Method for Rule
     *
     * @return rule as String
     */
    public static String getRule() {
        return ruleString;
    }

    /** Sets rule and updates lists
     *
     * @param rule correctly formatted rule
     */
    public static void setRule(String rule) {
        ruleString = rule;
        fillRuleLists();
    }

    /** Fills internal rule lists
     *
     */
    private static void fillRuleLists() {
        if(ruleString == null) {
            return;
        }
        String[] parts = ruleString.split("/");

        String b = parts[0];
        String s = parts[1];

        //swaps placement if S/B
        if(parts[0].toUpperCase().contains("S")) {
            b = parts[1];
            s = parts[0];
        }

        birth = fromRuleSet(b.substring(1));
        survival = fromRuleSet(s.substring(1));
    }

    /** Converts String to int[]
     *
     * @param str string to be converted
     * @return int[] of digits
     */
    private static LinkedList<Integer> fromRuleSet(String str) {
        char[] nums = str.toCharArray();
        LinkedList<Integer> temp = new LinkedList<>();
        for (char c : nums) {
            temp.add(Integer.valueOf("" + c));
        }
        return temp;
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
        if(showLegacy) {
            //if showing legacy
            randomColors();
        }
        buffer.clear();
        buffer.add(matrix);
    }

    /** Gives randomColors to each living cell */
    public void randomColors() {
        for (Cell[] cells : matrix) {
            for(Cell cell : cells) {
                cell.setMainColor(new Color((int) (Math.random() * 0x1000000)));
            }
        }
    }

    /** Kills all cells. */
    public void genocide() {
        buffer.clear();
        for (Cell[] cells : matrix) {
            for (Cell c : cells) {
                c.kill();
            }
        }
        buffer.add(matrix);
    }

    public static void setBufferMax(int max) {
        bufferMax = max;
    }

    public int getBufferIndex() {
        return buffer.indexOf(matrix);
    }

    public int getBufferMax() {
        return bufferMax;
    }

    public int getBufferSize() {
        return buffer.size();
    }

    /** Ticks matrix to next generation according to the
     *  rules of the Conway's Game of Life.
     */
    public void tick(boolean wrapEnabled) {
        if(buffer.indexOf(matrix) < buffer.size() - 1) {
            //if already ticked
            matrix = buffer.get(buffer.indexOf(matrix) + 1);
            return;
        }
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
                if (c.isAlive()) {
                    //if alive
                    if(survival.contains(numLiving)) {
                        //if survives
                        g2.matrix[x][y].revive();
                        if (showLegacy) {
                            g2.matrix[x][y].setMainColor(c.getMainColor());
                        }
                    }
                }
                else {
                    //else dead
                    if (birth.contains(numLiving)) {
                        //reproduction
                        g2.matrix[x][y].revive();
                        if(showLegacy) {
                            g2.matrix[x][y].setMainColor(new Color((int) (Math.random() * 0x1000000)));
                        }
                    }
                }
                //else dies to isolation, under-population, or over-population
                if(c.isSpotlit()) {
                    //if cell is spotlit
                    g2.matrix[x][y].spotlight();
                }
            }
        }
        if(buffer.size() == bufferMax) {
            buffer.remove(0);
        }
        //migrates previous to current generation
        matrix = g2.matrix;

        //Adds Matrix to buffer
        buffer.add(g2.matrix);
    }

    public void reset() {
        for(Cell[] cells : matrix) {
            for(Cell cell : cells) {
                cell.setMainColor(new Color(0xc8c8c8));
                cell.setBackColor(new Color(0x141414));
            }
        }
    }

    /** Displays heatMap
     *
     * @param wrapEnabled edge condition
     */
    public void heatMap(boolean wrapEnabled) {
        for (int x = 0; x < numRows; x++) {
            for (int y = 0; y < numCols; y++) {
                Cell c = matrix[x][y];
                c.heatMap(numLivingNeighbors(x, y, wrapEnabled));
            }
        }
    }

    /** Returns matrix to previous state within buffer
     *
     * @return true if within buffer, false otherwise
     */
    public boolean rollback() {
        if(buffer.indexOf(matrix) > 0) {
            matrix = buffer.get(buffer.indexOf(matrix) - 1);
            clearSpotlight();
            return true;
        }
        return false;
    }

    /** Calculates the number
     *  of living cells
     * @return population size
     */
    public int numLivingCells() {
        int living = 0;
        for (Cell[] cells : matrix) {
            for(Cell cell : cells) {
                if(cell.isAlive()) {
                    living++;
                }
            }
        }
        return living;
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
                        //if inside matrix, navigateUp
                        y++;
                    }
                    else {
                        //else skip over
                        continue;
                    }
                }
                if(matrix[x][y].isAlive()) {
                    //if alive, navigateUp
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
                        //if bottom right corner alive, navigateUp
                        num++;
                    }
                }
                if (pY == sideB) {
                    //if bottom left corner
                    if(matrix[sideR][sideT].isAlive()) {
                        //if top right corner alive, navigateUp
                        num++;
                    }
                }
                for (int y = startY; y <= endY; y++) {
                    //loops through opposite side
                    if (matrix[sideR][y].isAlive()) {
                        //if alive, navigateUp
                        num++;
                    }
                }
            }
            else if (pX == sideR) {
                //if right side
                if (pY == sideT) {
                    //if top right corner
                    if (matrix[sideL][sideB].isAlive()) {
                        //if bottom left corner alive, navigateUp
                        num++;
                    }
                }
                if (pY == sideB) {
                    //if bottom right corner
                    if(matrix[sideL][sideT].isAlive()) {
                        //if top left corner alive, navigateUp
                        num++;
                    }
                }
                for (int y = startY; y <= endY; y++) {
                    //loops through opposite side
                    if (matrix[sideL][y].isAlive()) {
                        //if alive, navigateUp
                        num++;
                    }
                }
            }
            if (pY == sideT) {
                for (int x = startX; x <= endX; x++) {
                    //loops through opposite side
                    if (matrix[x][sideB].isAlive()) {
                        //if alive, navigateUp
                        num++;
                    }
                }
            }
            else if (pY == sideB) {
                for (int x = startX; x <= endX; x++) {
                    //loops through opposite side
                    if (matrix[x][sideT].isAlive()) {
                        //if alive, navigateUp
                        num++;
                    }
                }
            }
        }
        return num;
    }

    /** Converts matrix to buffered image excluding
     *  blank rows
      * @param width desired image width
     * @return image of CellMatrix
     */
    public BufferedImage toImage(double width) {
        int numFullCols = numFullCols();

        //calculates scale and height
        double size = width / numRows;
        double height = size * numFullCols;

        //creates a new buffered image and graphics
        BufferedImage image = new BufferedImage((int) width, (int) height, BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D g2 = image.createGraphics();

        //loops through bounded cell matrix and draws image to graphic
        Cell[][] temp = new Cell[numRows][numFullCols];
        for (int x = 0; x < numRows; x++) {
            for (int y = 0; y < numFullCols; y++) {
                temp[x][y] = new Cell(x * size, y * size, size);
                if(matrix[x][y].isAlive()) {
                    temp[x][y].revive();
                }
                temp[x][y].drawCell(g2, false);
            }
        }
        return image;
    }

    /** Calculates the true height of a cell matrix
     *
     * @return number of filled columns
     */
    private int numFullCols() {
        //calculates number of non-blank columns
        int numFullCols = numCols;
        for(int y = numCols - 1; y >= 0; y--) {
            //loops through columns from bottom
            for (int x = 0; x < numRows; x++) {
                if(matrix[x][y].isAlive()) {
                    //ends if non-blank row
                    return numFullCols;
                }
            }
            numFullCols--;
        }
        return numFullCols;
    }

    /**
     * Passes through paintComponent and
     * draws Cell Matrix.
     *
     * @param g        graphics
     * @param showGrid if grid enabled
     */
    public void drawMatrix(Graphics g, boolean showGrid) {
        for (Cell[] row : matrix) {
            for (Cell c : row) {
                c.drawCell(g, showGrid);
            }
        }
    }

    /** removes select from all Cells */
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
                    return new int[]{x, y};
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
        //adjusted coordinates
        pX = coords[0];
        pY = coords[1];

        //loops through new matrix and places to position
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

    /** spotlights cell matrix at coordinate
     *
     * @param pX x position
     * @param pY y position
     * @param cm matrix to select
     */
    public boolean spotlightPlacement(int pX, int pY, CellMatrix cm) {
        int[] coords = adjustToBounds(pX, pY, cm);
        if(coords == null) {
            return false;
        }
        pX = coords[0];
        pY = coords[1];

        //loops through cell matrix and spotlights living cells
        for (int x = pX; x < cm.matrix.length + pX; x++) {
            for (int y = pY; y < cm.matrix[0].length + pY; y++) {
                if(cm.matrix[x - pX][y - pY].isAlive()) {
                    matrix[x][y].spotlight();
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
        return new int[]{pX, pY};
    }

    /** Converts Matrix to MatrixData
     *
     * @return MatrixData representation of CellMatrix
     */
    public MatrixData toMatrixData() {
        return new MatrixData(new int[]{matrix.length, matrix[0].length}, this.toString());
    }

    /** Spotlights are cells within a Rectangle Shape
     *
     * @param rect to search
     */
    public void spotlightAll(Rectangle rect) {
        for (Cell[] row : matrix) {
            for (Cell c : row) {
                if (rect.intersects((Rectangle2D) c.getGridCell())) {
                    c.spotlight();
                }
                else {
                    c.unspotlight();
                }
            }
        }
    }

    public CellMatrix fromSpotlight() {
        int startX = 0;
        int endX = 0;
        int startY = 0;
        int endY = 0;

        for (int x = 0; x < matrix.length; x++) {
            for (int y = 0; y < matrix[0].length; y++) {
                if(getCell(x, y).isSpotlit()) {
                    startX = x;
                    startY = y;
                    break;
                }
            }
        }

        for (int x = matrix.length - 1; x >= 0; x--) {
            for (int y = matrix[0].length - 1; y >= 0; y--) {
                if(getCell(x, y).isSpotlit()) {
                    endX = x;
                    endY = y;
                    break;
                }
            }
        }

        if(endX < startX) {
            int temp = endX;
            endX = startX;
            startX = temp;
        }

        if(endY < startY) {
            int temp = endY;
            endY = startY;
            startY = temp;
        }

        int width = Math.abs(endX - startX);
        int height = Math.abs(endY - startY);

        if (width == 0 || height == 0) {
            return null;
        }

        CellMatrix cm = new CellMatrix(width + 1, height + 1);

        for(int x = startX; x <= endX; x++) {
            for (int y = startY; y <= endY; y++) {
                if(getCell(x, y).isAlive()) {
                    cm.getCell(x - startX, y - startY).revive();
                }
            }
        }
        return cm;
    }

    /** String representation of CellMatrix in RLE format
     * @return (W*H)bo$2bo$3o!
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
            if(c < matrix[0].length -1) {
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
}