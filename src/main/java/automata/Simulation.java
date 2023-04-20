package automata;

import dynamicpanel.*;

import java.awt.*;

/** Simulation Class records and displays
 *  statistics for each matrix tick.
 */
public class Simulation {
    private CellMatrix matrix;
    private final DynamicPanel panel;
    private final Graph livingGraph;
    private final ProgressBar livingBar;
    private final ProgressBar maxBar;
    private final TextBar deathRate;
    private final TextBar toolTip;
    private int population;
    private int maxLiving;
    private int minLiving;
    private int numText;
    private int numTicks;

    /** 2-arg Constructor instantiates the menu
     *
     * @param width item width
     * @param height item height
     */
    public Simulation(int width, int height) {
        panel = new DynamicPanel();

        livingBar = new ProgressBar(width, height);
        livingBar.setDescription("Living / Max");

        maxBar = new ProgressBar(width, height);
        maxBar.setDescription("Min / Max");
        maxBar.showMin();

        livingGraph = new Graph(width, 2 * height);
        livingGraph.setBackColor(Color.darkGray);
        livingGraph.setDescription("Cells vs Ticks");
        livingGraph.setLineWidth(1);

        deathRate = new TextBar("Cells/Tick: N/A", MainPanel.mainFont, Color.white);
        toolTip = new TextBar("Hover for Details", MainPanel.mainFont, Color.gray);


        panel.addItem(new TextBar("Statistics", MainPanel.titleFont, MainPanel.mainColor));
        panel.addItem(livingBar);
        panel.addItem(maxBar);
        panel.addItem(livingGraph);
        panel.addItem(deathRate);
        panel.addItem(toolTip);
    }

    /** Returns simulation to defaults and clears
     *  previous data
     *
     * @param matrix to observe
     */
    public void reset(CellMatrix matrix) {
        numTicks = 0;
        this.matrix = matrix;
        population = matrix.numLivingCells();

        livingBar.setMax(matrix.getNumRows() * matrix.getNumCols());
        livingBar.setMin(0);

        maxBar.setMax(population);
        maxBar.setMin(population);

        maxLiving = population;
        minLiving = population;

        livingGraph.clear();

        deathRate.setText("Cells/Tick: N/A");
        update(0);
    }

    /** Adds/removes data from tick and updates
     *  menu
     *
     * @param tick current tick
     */
    public void update(int tick) {
        population = matrix.numLivingCells();

        //update absolute max
        if(population > maxLiving) {
            maxLiving = population;
            maxBar.setMax(maxLiving);
        }

        //update absolute
        if(population < minLiving) {
            minLiving = population;
            maxBar.setMin(minLiving);
        }

        //updates progress
        livingBar.setProgress(population);
        maxBar.setProgress(population);

        if(tick >= numTicks) {
            //if progressed
            livingGraph.addPoint(tick, population);
            numTicks++;
        }
        else {
            //if regressed
            livingGraph.removeLast();
            numTicks--;
        }

        if (livingGraph.numPoints() >= 2) {
            //if sufficient data
            double slope = Math.round(livingGraph.getSlope() * 100) / 100.0;
            deathRate.setText("Cells/Tick: " + slope);
        }
        else {
            //else insufficient data
            deathRate.setText("Cells/Tick: N/A");
        }
    }

    /** Height of Panel
     *
     * @return menu height
     */
    public int getBoxHeight() {
        return panel.getHeight();
    }

    /** Width of Panel
     *
     * @return menu width
     */
    public  int getBoxWidth() {
        return panel.getWidth();
    }

    /** Displays tool-tip if hover at
     *  mouse position
     * @param mouseX mouse x coordinate
     * @param mouseY mouse y coordinate
     */
    public void showToolTip(int mouseX, int mouseY) {
        if(panel.isSelected(mouseX, mouseY)) {
            DynamicItem item = (DynamicItem) panel.getItemAt(mouseX, mouseY);
            if (item != null) {
                toolTip.setText(item.toString());
            }
        }
        else {
            toolTip.setText("Hover for Details");
        }
    }


    /** Draws Panel
     *
     * @param g graphics
     * @param pX x position
     * @param pY y position
     */
    public void draw(Graphics g, int pX, int pY) {
        panel.draw(g, pX, pY);
    }
}
