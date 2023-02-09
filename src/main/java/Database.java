import com.google.gson.*;
import kong.unirest.*;

import javax.swing.JPanel;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import java.util.Scanner;

/** main.Database Class imports and exports
 *  main.MatrixData from a text document.
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
            //if data.txt does not exist, instantiates empty main.Database
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
            //Error when generating main.Database
            System.out.println("ERROR: Could not generate main.Database");
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
                //adds new main.MatrixData to database
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
     *  main.Database and implements data.txt
     *
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

    /** Adds main.MatrixData to internal database
     *
     * @param m main.CellMatrix to be Added
     */
    public void add(MatrixData m) {
        database.add(m);
    }

    /** Removes main.MatrixData from internal database
     *
     * @param index to be removed
     */
    public MatrixData removeAtIndex(int index) {
        return database.remove(index);
    }

    /** Empties internal main.Database */
    public void wipe() {
        database.clear();
    }

    /** Retrieves main.MatrixData at index
     *
     * @param index selected
     * @return main.MatrixData at index
     */
    public MatrixData get(int index) {
        return database.get(index);
    }

    /** Returns length of database
     *
     * @return size of database
     */
    public int sizeDB() {
        return database.size();
    }

    /** Paints main.Database with Title and dynamic list of elements
     *
     * @param g graphics
     * @param index index of selection
     */
    public void paintDatabase(Graphics g, int index) {
        Graphics2D g2 = (Graphics2D) g;
        String[] items = new String[database.size()];
        for (int i = 0; i < database.size(); i++) {
            items[i] = database.get(i).getTitle();
        }
        DynamicMenu databaseMenu = new DynamicMenu("main.Database", items, index);
        databaseMenu.paintMenu(g2, 10, 10);
    }

    /** Accesses wikicollections api for patterns based of search term
     *
     * <p> https://rapidapi.com/timjacksonm-1jw8F2hFW3d/api/the-game-of-life </p>
     *
     * @param s search term
     */
    public void addFromSearch(String s) {

        int count = 5;
        HttpResponse<String> response = Unirest.get("https://the-game-of-life.p.rapidapi.com/wikicollection/search/title?value=" + s + "&select=%5B%22title%22%2C%22size%22%2C%22rleString%22%5D")
                .header("X-RapidAPI-Key", "4ce993ab37mshadac634a5fbad3ep1a4c4fjsn041896a40067")
                .header("X-RapidAPI-Host", "the-game-of-life.p.rapidapi.com")
                .asString();

        //Access all Aspects
        //HttpResponse<String> response = Unirest.get("https://the-game-of-life.p.rapidapi.com/wikicollection/search/title?value=" + s + "&select=%5B%22author%22%2C%22title%22%2C%22description%22%2C%22size%22%2C%22rleString%22%2C%22date%22%5D&count=" + count)
        //        .header("X-RapidAPI-Key", "4ce993ab37mshadac634a5fbad3ep1a4c4fjsn041896a40067")
        //        .header("X-RapidAPI-Host", "the-game-of-life.p.rapidapi.com")
        //        .asString();

        //error checks connection
        if(response.getStatus() != 200) {
            System.out.println("ERROR: Failed to Access WikiCollections API [Code" + response.getStatus() + "]");
            Unirest.shutDown();
            return;
        }
        System.out.println("Successful Connection [Code " + response.getStatus() + "]");
        Unirest.shutDown();

        ArrayList<MatrixData> results = new ArrayList<>();

        Gson gson = new Gson();
        JsonArray elements = JsonParser.parseString(response.getBody()).getAsJsonArray();

        for(JsonElement e : elements) {
            results.add(gson.fromJson(e.getAsJsonObject(), MatrixData.class)); //ARCHIVED UNTIL FUNCTIONAL
        }
        for (int i = 0; i < results.size(); i++) {
            if(results.get(i).convertFromRle()) {
                database.add(results.get(i));
            }
        }
    }
}
