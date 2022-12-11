import java.util.ArrayList;

/** MatrixData class assigns a name to
 *  a CellMatrix's and stores the size
 *  and living cells contents.
 *
 * @author RMizelle
 */
public class MatrixData {
    private String name;
    private int[] size;
    private ArrayList<int[]> cells;

    /** 3-arg constructor instantiates name, size, and cells.
     *
     * @param name matrix name
     * @param size grid size [x,y]
     * @param cells Arraylist of Living Cell Coordinates [x,y]
     */
    public MatrixData(String name, int[] size, ArrayList<int[]> cells) {
        this.name = name;
        this.size = size;
        this.cells = cells;
    }

    /** 2-arg Constructor instantiates size and cells with
     *  default name of "CellMatrix_(W*H)"
     *
     * @param size grid size [x,y]
     * @param cells Arraylist of Living Cell Coordinates [x,y]
     */
    public MatrixData(int[] size, ArrayList<int[]> cells) {
        this.name = "CellMatrix_(" + size[0] + "x" + size[1] + ")";
        this.size = size;
        this.cells = cells;
    }

    /** Setter Method for Name
     *
     * @param name to be assigned
     */
    public void setName(String name) {
        this.name = name;
    }

    /** Accessor Method for Name
     *
     * @return Name of Matrix
     */
    public String getName() {
        return name;
    }

    /** Accessor Method for Size
     *
     * @return Grid Size [x,y]
     */
    public int[] getSize() {
        return size;
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
        CellMatrix temp = new CellMatrix(size[0], size[1]);
        for (int[] cell : cells) {
            temp.getCell(cell[0], cell[1]).revive();
        }
        return temp;
    }

    @Override
    public String toString() {
        String temp = "\"" + name + "\" ";
        temp += "(" + size[0] + "x" + size[1] + ") ";
        for (int[] cell : cells) {
            temp += "[" + cell[0] + "," + cell[1] + "] ";
        }
        return temp;
    }
}
