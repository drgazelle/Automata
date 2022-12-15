import javax.swing.JPanel;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/** Database Class imports and exports
 *  MatrixData from a text document.
 *  Additionally, renders the database
 *  graphically.
 *
 * @author RMizelle
 */
public class Database extends JPanel {
    private final ArrayList<MatrixData> database;
    private File data;

    /** 0-arg constructor implements ArrayList of SparseMatrices
     *  of Cells objects from a text document.
     */
    public Database() {
        database = new ArrayList<>();

        //creates resource folder if necessary
        File directory = new File("resources");
        if (!directory.exists()) {
            System.out.println("New Resources Directory Generated");
            directory.mkdir();
        }
        //checks for data.txt
        try {
            data = new File("resources/data.txt");
            //if data.txt does not exist, instantiates empty Database
            if (data.createNewFile()) {
                System.out.println("New Data File Generated");
            }
            //else data.txt exists, calls import method
            else {
                System.out.println("Accessing Data...");
                if (importData()) {
                    System.out.println("Data Retrieved Successfully");
                }
                else {
                    System.out.println("ERROR: Failed to Retrieve Data");
                }
            }
        }
        catch (IOException e) {
            //Error when generating Database
            System.out.println("ERROR: Could not generate Database");
            e.printStackTrace();
        }
    }

    /** importData method instantiates database using data.txt
     *  @return true if successfully instantiated database, false if error
     */
    private boolean importData() {
        try {
            //creates scanner
            Scanner input = new Scanner(data);
            while (input.hasNextLine()) {
                String line = input.nextLine();
                //splits up data fields
                String[] parts = line.split("##");
                //removes brackets from data fields
                for(int i = 0; i < parts.length; i++) {
                    parts[i] = parts[i].substring(1, parts[i].length() - 1).trim();
                }
                //gets name
                String name = parts[0];

                //get size
                String[] temp = parts[1].split("x");
                int[] size = new int[2];
                size[0] = Integer.parseInt(temp[0].trim());
                size[1] = Integer.parseInt(temp[1].trim());

                //get live cells
                ArrayList<int[]> cells = new ArrayList<>();
                for(int i = 2; i < parts.length; i++) {
                    //new splits String into parts and converts to ints
                    int[] cell = new int[2];
                    temp = parts[i].split(",");
                    cell[0] = Integer.parseInt(temp[0].trim());
                    cell[1] = Integer.parseInt(temp[1].trim());
                    cells.add(cell);
                }
                //adds new MatrixData to database
                database.add(new MatrixData(name, size, cells));
            }
            //closes scanner
            input.close();
        } catch (FileNotFoundException e) {
            System.out.println("ERROR: Couldn't Read File");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /** exportData method takes existing
     *  Database and implements data.txt
     *  @return true if successful, false if error
     */
    public boolean exportDatabase() {
        try {
            FileWriter output = new FileWriter(data);
            for (MatrixData m : database) {
                output.write(m.toString() + "\n");
            }
            output.close();
        }
        catch (IOException e) {
            System.out.println("ERROR: Failure to write data.txt");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /** Adds MatrixData to internal database
     *
     * @param m CellMatrix to be Added
     */
    public void add(MatrixData m) {
        database.add(m);
    }

    /** Removes MatrixData from internal database
     *
     * @param index to be removed
     */
    public MatrixData removeAtIndex(int index) {
        return database.remove(index);
    }

    /** Empties internal Database
     *
     */
    public void wipe() {
        database.clear();
    }

    /** Retrieves MatrixData at index
     *
     * @param index selected
     * @return MatrixData at index
     */
    public MatrixData get(int index) {
        return database.get(index);
    }

    /** Returns length of database
     *
     * @return size of database
     */
    public int databaseSize() {
        return database.size();
    }

    /** Paints Database with Title and dynamic list of elements
     *
     * @param g graphics
     * @param index index of selection
     */
    public void paintDatabase(Graphics g, int index) {
        Graphics2D g2 = (Graphics2D) g;
        //list of menu items

        //font defining aspects
        g2.setFont(MainPanel.mainFont);
        FontMetrics metrics = getFontMetrics(g.getFont());
        int d = metrics.getAscent();

        //position and size variables
        int border = 10;
        int boxHeight = (d + 2) * database.size() + 22; //dynamic box height
        //int boxHeight = AppDriver.HEIGHT - (border * 2); //static box height
        int boxWidth = 125;
        int pX = border;
        int pY = border;

        //box for menu background
        Shape menuBackground = new Rectangle(pX, pY, boxWidth, boxHeight);
        g2.setColor(Color.black);
        g2.fill(menuBackground);
        //OPT: menu border
        //g2.setColor(Color.white);
        //g2.draw(menuBackground);

        //title
        g2.setColor(MainPanel.mainColor);
        g2.setFont(new Font(g2.getFont().getFontName(), Font.PLAIN, 3 * g2.getFont().getSize() / 2));

        FontMetrics metricsTitle = getFontMetrics(g.getFont());
        pY += (metricsTitle.getAscent() / 2) - 3;

        g2.drawString("Database", pX + 5, pY + d);
        pY += (metricsTitle.getAscent() / 2) - 3 + 10;


        //draws index
        if (index > -1) {
            int highlightBorder = 4;
            Shape highlightBox = new Rectangle(pX + highlightBorder, pY + (d + 2) * index + 2, boxWidth - 2 * highlightBorder, d);
            g2.setColor(Color.darkGray);
            g2.fill(highlightBox);
        }

        //resets color
        g2.setColor(Color.white);
        g2.setFont(MainPanel.mainFont);
        for (MatrixData m : database) {
            //loops through items
            g2.drawString(m.getName(), pX + 5, pY + d);
            pY += d + 2;
        }
    }
}
