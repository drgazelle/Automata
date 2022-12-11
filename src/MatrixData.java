import java.util.ArrayList;

public class MatrixData {
    private String name;
    private int[] size;
    private ArrayList<int[]> cells;

    public MatrixData(String name, int[] size, ArrayList<int[]> cells) {
        this.name = name;
        this.size = size;
        this.cells = cells;
    }

    public MatrixData(int[] size, ArrayList<int[]> cells) {
        this.name = "CellMatrix_(" + size[0] + "x" + size[1] + ")";
        this.size = size;
        this.cells = cells;
    }

    public String getName() {
        return name;
    }

    public int[] getSize() {
        return size;
    }

    public ArrayList<int[]> getCells() {
        return cells;
    }

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
