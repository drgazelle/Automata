package automata;

import java.awt.image.BufferedImage;

/** MatrixData class assigns a title to
 *  a CellMatrix's and stores the numItems
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
    private String rule;
    private String date;
    private Size size;

    /** 4-arg constructor instantiates title, numItems, rule, and cells.
     *
     * @param rule rule
     * @param name matrix title
     * @param size grid numItems [x,y]
     * @param rleString Encoded Cell Coordinates
     */
    public MatrixData(String rule, String name, int[] size, String rleString) {
        this.rule = rule;
        this.title = name;
        this.size = new Size(size[0], size[1]);
        this.rleString = rleString;
    }

    /** 3-arg constructor instantiates title, numItems, and cells.
     *
     * @param name matrix title
     * @param size grid numItems [x,y]
     * @param rleString Encoded Cell Coordinates
     */
    public MatrixData(String name, int[] size, String rleString) {
        this("B2/S23", name, size, rleString);
    }

    /** 2-arg Constructor instantiates numItems and cells with
     *  default title of "CellMatrix_(W*H)"
     *
     * @param size grid numItems [x,y]
     * @param rleString encoded Cell Coordinates
     */
    public MatrixData(int[] size, String rleString) {
        this("CellMatrix_(" + size[0] + "x" + size[1] + ")", size, rleString);
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
        return new int[]{size.getX(), size.getY()};
    }

    /** Converts Matrix Data to CellMatrix
     *
     * @return CellMatrix of numItems with all cells revived
     */
    public CellMatrix toCellMatrix() {
        CellMatrix matrix = new CellMatrix(size.getX(), size.getY());

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
                    matrix.getCell(x, y).revive();
                    x++;
                }
                else {
                    //else multiple dead cells
                    int range = x + Integer.parseInt(num.toString());
                    while (x < range) {
                        matrix.getCell(x, y).revive();
                        x++;
                    }
                }
            }
        }
        return matrix;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("#N " + title + "\n");
        sb.append("x = " + size.getX() + ", y = " + size.getY());
        sb.append(", rule = ");
        if(rule == null) {
            sb.append("B2/S23");
        }
        else {
            sb.append(rule);
        }
        sb.append("\n");
        char[] temp = rleString.toCharArray();
        for(int i = 0; i < rleString.length(); i++) {
            if(i > 0 && i % 60 == 0) {
                sb.append("\n");
            }
            sb.append(temp[i]);
        }
        return sb.toString();
    }

    public BufferedImage toImage(double width) {
        return toCellMatrix().toImage(width);
    }

    public String getRleString() {
        return rleString;
    }

    public void setRleString(String rle) {
        rleString = rle;
    }

    public String getRule() {
        return rule;
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

    @Override
    public String toString() {
        return "X:" + x + "\tY: " + y;
    }
}