package automata;

import com.google.gson.*;
import dynamicpanel.DynamicItem;
import dynamicpanel.DynamicPanel;
import dynamicpanel.ProgressBar;
import dynamicpanel.TextBar;
import kong.unirest.*;

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
public class Database {
    private final ArrayList<MatrixData> database;
    private File data;
    private int index;
    private int startIndex;
    private final int maxSize;
    private final DynamicPanel databaseMenu;
    private final ProgressBar storageBar;

    /** 0-arg constructor implements ArrayList of SparseMatrices
     *  of Cells objects from a text document.
     */
    public Database(int size) {
        database = new ArrayList<>();
        maxSize = size;

        //Default Index
        index = -1;
        startIndex = 0;

        databaseMenu = new DynamicPanel();
        databaseMenu.addItem(new TextBar("Database", MainPanel.titleFont, MainPanel.mainColor));
        startIndex++;

        //creates resource folder if necessary
        File directory = new File("src/main/resources");
        if (!directory.exists()) {
            System.out.println("New Resources Directory Generated");
            directory.mkdir();
        }
        //checks for data.txt
        try {
            data = new File("src/main/resources/data.txt");
            if (data.createNewFile()) {
                //if data.txt does not exist, instantiates empty Database
                System.out.println("New Data File Generated");
            }
            else {
                //else data.txt exists, calls import method
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

        //Adds storageBar to end
        storageBar = new ProgressBar(databaseMenu.getBorderlessWidth(), 10, 0, maxSize, size());
        storageBar.setColors(Color.darkGray, Color.gray, Color.black);
        databaseMenu.addItem(storageBar);
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
                String[] parts = line.split("////");
                String name = removeShell(parts[0]).trim();
                String[] dimensions = removeShell(parts[1]).split("x");
                int[] size = new int[]{Integer.parseInt(dimensions[0]), Integer.parseInt(dimensions[1])};
                String rleString = removeShell(parts[2]);
                //adds new MatrixData to database
                add(new MatrixData(name, size, rleString));
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

    private String removeShell(String s) {
        return s.substring(1, s.length() - 1);
    }

    /** exportData method takes existing
     *  Database and implements data.txt
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

    /** Resizes and updates progress
     *  of storageBar
     */
    private void updateBar() {
        storageBar.setDimensions(0, storageBar.getHeight());
        storageBar.setDimensions(databaseMenu.getBorderlessWidth(), storageBar.getHeight());
        storageBar.setProgress(size());
    }

    /** Adds MatrixData to internal database
     *
     * @param m CellMatrix to be Added
     */
    public void add(MatrixData m) {
        if(size() < maxSize) {
            database.add(m);
            databaseMenu.addItem(startIndex + size() - 1, new TextBar(m.getTitle(), MainPanel.mainFont, Color.white));
        }
    }

    /** Removes MatrixData from internal database
     *
     */
    public MatrixData deleteIndex() {
        databaseMenu.remove(index + startIndex);
        MatrixData temp = database.remove(index);
        if(index >= size()) {
            index--;
        }
        return temp;
    }

    /** Empties internal Database */
    public void wipe() {
        databaseMenu.removeItemsRange(startIndex, database.size() + startIndex);
        database.clear();
    }

    /** Retrieves MatrixData at index
     *
     * @return MatrixData at index
     */
    public MatrixData get() {
        return database.get(index);
    }

    /** Returns length of database
     *
     * @return numItems of database
     */
    public int size() {
        return database.size();
    }

    /** Increments index with error
     *  checking
     *
     * @return database index
     */
    public int navigateUp() {
        index++;
        if(index >= database.size()) {
            index = 0;
        }
        return index;
    }

    /** Reduces index with error
     *  checking
     *
     * @return database index
     */
    public int navigateDown() {
        index--;
        if(index < 0) {
            index = database.size() - 1;
        }
        return index;
    }

    /** Clears selection */
    public void clearSelection() {
        index = -1;
        databaseMenu.clearSelection();
    }

    /** Returns true if selected
     *
     * @return true if item is selected, false otherwise
     */
    public boolean isSelected() {
        return index > -1;
    }

    public void select(int mouseX, int mouseY) {
        int temp = databaseMenu.getIndexAt(mouseX, mouseY);
        if(temp > 0 && temp <= size()) {
            index = temp - startIndex;
        }
    }

    /** Paints Database with Title and dynamic list of elements
     *
     * @param g graphics
     */
    public void paintDatabase(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        databaseMenu.clearSelection();
        if(isSelected()) {
            databaseMenu.select(index + startIndex);
        }
        updateBar();
        databaseMenu.draw(g2, 10, 10);
    }

    /** Accesses wikicollections api for patterns based of search term
     *
     * <p> Link to API <a href="https://https://rapidapi.com/timjacksonm-1jw8F2hFW3d/api/the-game-of-life">...</a> </p>
     *
     * @param s search term
     */
    public void addFromSearch(String s) {
        System.out.println("\nAccessing RapidAPI...");
        int count = 5;
        HttpResponse<String> response;
        try {
            response = Unirest.get("https://the-game-of-life.p.rapidapi.com/wikicollection/search/title?value=" + s + "&select=%5B%22title%22%2C%22size%22%2C%22rleString%22%5D")
                    .header("X-RapidAPI-Key", "4ce993ab37mshadac634a5fbad3ep1a4c4fjsn041896a40067")
                    .header("X-RapidAPI-Host", "the-game-of-life.p.rapidapi.com")
                    .asString();

            //Access all Aspects
            //HttpResponse<String> response = Unirest.get("https://the-game-of-life.p.rapidapi.com/wikicollection/search/title?value=" + s + "&select=%5B%22author%22%2C%22title%22%2C%22description%22%2C%22size%22%2C%22rleString%22%2C%22date%22%5D&count=" + count)
            //        .header("X-RapidAPI-Key", "4ce993ab37mshadac634a5fbad3ep1a4c4fjsn041896a40067")
            //        .header("X-RapidAPI-Host", "the-game-of-life.p.rapidapi.com")
            //        .asString();

        } catch (UnirestException e) {
            System.out.println("ERROR: Couldn't Access RapidAPI");
            return;
        }

        //error checks connection
        if(response.getStatus() != 200) {
            System.out.println("ERROR: Failed to Access WikiCollections API [Code" + response.getStatus() + "]");
            Unirest.shutDown();
            return;
        }

        System.out.println("Successful Connection [Code " + response.getStatus() + "]");
        Unirest.shutDown();

        Gson gson = new Gson();
        JsonArray elements = JsonParser.parseString(response.getBody()).getAsJsonArray();

        for(JsonElement e : elements) {
            add(gson.fromJson(e.getAsJsonObject(), MatrixData.class));
            if(size() > maxSize) {
                break;
            }
        }
    }

    public void updateTitle(String s) {
        ((TextBar) databaseMenu.getItem(index + 1)).setText(s);
    }
}
