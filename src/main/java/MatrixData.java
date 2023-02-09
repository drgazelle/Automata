import java.util.ArrayList;
import java.util.regex.Pattern;

/** MatrixData class assigns a title to
 *  a CellMatrix's and stores the size
 *  and living cells contents.
 *
 * @author RMizelle
 */
public class MatrixData {
    private String _id;
    private String author;
    private String title;
    private String[] description;
    private String rleString;
    private String date;
    private Size size;
    private ArrayList<int[]> cells;

    /** 3-arg constructor instantiates title, size, and cells.
     *
     * @param name matrix title
     * @param size grid size [x,y]
     * @param cells Arraylist of Living Cell Coordinates [x,y]
     */
    public MatrixData(String name, int[] size, ArrayList<int[]> cells) {
        this.title = name;
        this.size = new Size(size[0], size[1]);
        this.cells = cells;
    }

    /** 2-arg Constructor instantiates size and cells with
     *  default title of "CellMatrix_(W*H)"
     *
     * @param size grid size [x,y]
     * @param cells Arraylist of Living Cell Coordinates [x,y]
     */
    public MatrixData(int[] size, ArrayList<int[]> cells) {
        this.title = "CellMatrix_(" + size[0] + "x" + size[1] + ")";
        this.size = new Size(size[0], size[1]);
        this.cells = cells;
    }

    /** Setter Method for Name
     *
     * @param title to be assigned
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /** Accessor Method for Name
     *
     * @return Name of Matrix
     */
    public String getTitle() {
        return title;
    }

    /** Accessor Method for Size
     *
     * @return Grid Size [x,y]
     */
    public int[] getSize() {
        int[] temp = {size.getX(), size.getY()};
        return temp;
    }

    /** Accessor Method for Cells Arraylist
     *
     * @return ArrayList of living Cells coordinates
     */
    public ArrayList<int[]> getCells() {
        return cells;
    }

    /** Converts Matrix Data to CellMatrix
     *
     * @return CellMatrix of size with all cells revived
     */
    public CellMatrix toCellMatrix() {
        CellMatrix temp = new CellMatrix(size.getX(), size.getY());
        for (int[] cell : cells) {
            temp.getCell(cell[1], cell[0]).revive();
        }
        return temp;
    }

    @Override
    public String toString() {
        String temp = "\"" + title + "\"";
        temp += "##(" + size.getX() + "x" + size.getY() + ")";
        for (int[] cell : cells) {
            temp += "##[" + cell[0] + "," + cell[1] + "]";
        }
        return temp;
    }

    /** Converts internal rleString into an array of Cell coordinates
     *
     * @return true if valid rle, false otherwise
     */
    public boolean convertFromRle() {
        if (rleString == null || rleString.isEmpty()) {
            return false;
        }
        cells = new ArrayList<>();
        String[] lines = rleString.substring(0, rleString.length() - 1).trim().split(Pattern.quote("$"));
        //loops through lines
        for(int x = 0; x < lines.length; x++) {
            char[] line = lines[x].toCharArray();
            String rangeStr = "";
            int rangeNum;
            int y = 0;
            for(int i = 0; i < line.length; i++) {
                //loops through line
                if(line[i] == 'b' || line[i] == 'o') {
                    if(rangeStr.isEmpty()) {
                        //if no digits
                        rangeNum = 1;
                    }
                    else {
                        //adds digits
                        rangeNum = Integer.valueOf(rangeStr);
                    }

                    if(line[i] == 'o') {
                        //if alive
                        addRange(rangeNum, x, y);
                    }
                    y += rangeNum;
                    //adds distance
                    rangeStr = "";
                    //resets range
                }
                else {
                    rangeStr += line[i];
                }
            }
        }
        return true;
    }

    /** Adds range of cell at a given x and y coordinate
     *
     * @param r range
     * @param x start position
     * @param y start postion
     */
    private void addRange(int r, int x, int y) {
        for (int i = 0; i < r; i++) {
            cells.add(new int[]{x, y + i});
        }
    }
}

/** Size object for API integration */
class Size {
    private int x;
    private int y;

    /** 2-arg constructor instantiates width and height
     *
     * @param x width
     * @param y height
     */
    public Size(int x, int y) {
        this.x = x;
        this.y = y;
    }

    //// Accessor Methods ////

    public int getY() {
        return y;
    }

    public int getX() {
        return x;
    }
}
