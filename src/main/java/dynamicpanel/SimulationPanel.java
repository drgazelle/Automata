package dynamicpanel;

import automata.CellMatrix;
import java.awt.*;

public class SimulationPanel {
    private DynamicPanel panel;
    private ProgressBar current_stats;
    private ProgressBar global_stats;
    private CellMatrix matrix;

    private final int max_population;
    private int current_population;

    public SimulationPanel(CellMatrix matrix) {
        this.matrix = matrix;
        max_population = matrix.getNumCols() * matrix.getNumRows();

        int width = 200;
        int height = 15;
        panel = new DynamicPanel();

        TextBar title_bar = new TextBar(width, 2 * height, "Statistics");
        title_bar.setColor(Color.green);

        current_stats = new ProgressBar(width, height, 0, max_population);
        global_stats = new ProgressBar(height, height, 0, 30, 20);
        panel.add(title_bar);
        panel.add(current_stats);
        panel.add(global_stats);
    }

    public void update() {
        current_stats.setProgress(matrix.population());
    }

    public int getHeight() {
        return panel.getHeight();
    }

    public void draw(Graphics g, int pX, int pY) {
        panel.draw(g, pX, pY);
    }
}
