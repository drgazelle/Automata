package dynamicpanel;

import java.awt.*;
import java.util.ArrayList;

public class DynamicPanel {
    private int border;
    private int spacing;
    private Color BackgroundColor;
    private Color mainColor;
    private Color altColor;
    private ArrayList<DynamicItem> items;

    /** 0-arg constructor instantiates ArrayList of
     *  DynamicItems and calls default settings
     */
    public DynamicPanel() {
        items = new ArrayList<>();
        defaultSettings();
    }

    /** Sets parameters to default */
    private void defaultSettings() {
        border = 5;
        spacing = 5;
        BackgroundColor = Color.black;
        mainColor = Color.white;
        altColor = Color.white;
    }

    /** Add Method for Items ArrayList
     *
     * @param i DynamicItem to be added
     */
    public void add(DynamicItem i) {
        items.add(i);
    }

    /** Returns spacing of DynamicItems
     *
     * @return spacing of DynamicItems
     */
    public int getSpacing() {
        return spacing;
    }

    /** Returns border of DynamicItems
     *
     * @return border size
     */
    public int getBorder() {
        return border;
    }

    /** Calculates the max height
     *  with borders and spacing
     *
     * @return height of DynamicPanel
     */
    public int getHeight() {
        int height = 2 * border;
        for (DynamicItem i : items) {
            height += i.getHeight();
        }
        height += ((items.size() - 1) * spacing);
        return height;
    }

    /** Calculates the max width with
     *  borders
     *
     * @return width of DynamicPanel
     */
    public int getWidth() {
        int width = 0;
        for (DynamicItem i : items) {
            width = Math.max(i.getWidth(), width);
        }
        return width + 2 * border;
    }

    public Color getMainColor() {
        return mainColor;
    }

    public void setMainColor(Color mainColor) {
        this.mainColor = mainColor;
    }

    public Color getAltColor() {
        return altColor;
    }

    public void setAltColor(Color altColor) {
        this.altColor = altColor;
    }

    /** Draw method paints background and all DynamicItems
     *
     * @param g graphics
     * @param pX start x position
     * @param pY start y position
     */
    public void draw(Graphics g, int pX, int pY) {
        int width = getWidth();
        int height = getHeight();

        //Background Box
        g.setColor(BackgroundColor);
        g.fillRect(pX, pY, width, height);

        pX += border;
        pY += border;

        //loops through contained items
        for (DynamicItem i : items) {
            i.draw(g, pX, pY);
            pY += i.getHeight() + spacing;
        }
    }
}
