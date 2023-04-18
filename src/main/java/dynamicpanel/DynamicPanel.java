package dynamicpanel;

import java.awt.*;
import java.util.ArrayList;

public class DynamicPanel {
    private int border;
    private int spacing;
    private static Color backColor;
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
        border = 10;
        spacing = 5;
        backColor = Color.black;
    }

    /** Add Method for Items ArrayList
     *
     * @param i DynamicItem to be added
     */
    public void add(DynamicItem i) {
        items.add(i);
    }

    public DynamicItem set(int index, DynamicItem i) {
        return items.set(index, i);
    }

    public void clear() {
        items.clear();
    }

    public int size() {
        return items.size();
    }

    public void add(int i, DynamicItem item) {
        items.add(i, item);
    }

    public void remove(int i) {
        items.remove(i);
    }

    public void removeItemsRange(int startIndex, int endIndex) {
        if (startIndex < 0 || endIndex >= items.size()) {
            return;
        }
        for (int i = startIndex; i < endIndex; i++) {
            items.remove(i);
        }
    }
    public void select(int i) {
        if(i < 0) {
            return;
        }
        items.get(i).setSelected(true);
    }

    public void clearSelection() {
        for(DynamicItem i : items) {
            i.setSelected(false);
        }
    }

    public DynamicItem findItemAt(int mouseX, int mouseY) {
        for(DynamicItem i : items) {
            if(i.getDynamicItem().contains(mouseX, mouseY)) {
                i.setSelected(true);
                return i;
            }
        }
        return null;
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


    /** Draw method paints background and all DynamicItems
     *
     * @param g graphics
     * @param pX start x position
     * @param pY start y position
     */
    public void draw(Graphics g, int pX, int pY) {
        Font font = g.getFont();

        int width = getWidth();
        int height = getHeight();

        //Background Box
        g.setColor(backColor);
        g.fillRect(pX, pY, width, height);

        pX += border;
        pY += border;

        //loops through contained items
        for (DynamicItem i : items) {
            g.setColor(backColor);
            g.setFont(font);
            i.draw(g, pX, pY);
            pY += i.getHeight() + spacing;
        }
    }
}
