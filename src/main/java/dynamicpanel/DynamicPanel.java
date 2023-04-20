package dynamicpanel;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class DynamicPanel extends JPanel {
    private int border;
    private int spacing;
    private final ArrayList<DynamicPanel> items;
    protected Rectangle borderBox;

    protected Color backColor;
    protected boolean selected;

    /** 0-arg constructor instantiates ArrayList of
     *  DynamicPanels and calls default settings
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

    public void setBackColor(Color backColor) {
        this.backColor = backColor;
    }

    /** Add Method for Items ArrayList
     *
     * @param i DynamicPanel to be added
     */
    public void addItem(DynamicPanel i) {
        items.add(i);
    }

    public DynamicPanel set(int index, DynamicPanel i) {
        return items.set(index, i);
    }

    public void clear() {
        items.clear();
    }

    public int numItems() {
        return items.size();
    }

    public int indexOf(DynamicPanel i) {
        return items.indexOf(i);
    }

    public void addItem(int i, DynamicPanel item) {
        items.add(i, item);
    }

    public DynamicPanel getItem(int i) {
        return items.get(i);
    }

    public void remove(int i) {
        items.remove(i);
    }

    public void removeItemsRange(int startIndex, int endIndex) {
        if (startIndex < 0 || endIndex >= items.size()) {
            return;
        }
        for (int i = 0; i < endIndex - startIndex; i++) {
            items.remove(startIndex);
        }
    }
    public void select(int i) {
        if(i < 0) {
            return;
        }
        items.get(i).setSelected(true);
    }

    public void clearSelection() {
        for(DynamicPanel i : items) {
            i.setSelected(false);
        }
    }

    /** returns index of item at mouse position
     *
     * @param mouseX horizontal coordinate
     * @param mouseY vertical coordinate
     * @return index of item
     */
    public int getIndexAt(int mouseX, int mouseY) {
        if(isSelected(mouseX, mouseY)) {
            for (DynamicPanel i : items) {
                Shape shape = i.getBorderBox();
                if(shape != null && shape.contains(mouseX, mouseY)) {
                    return items.indexOf(i);
                }
            }
        }
        return -1;
    }

    /** returns item at mouse position
     *
     * @param mouseX horizontal coordinate
     * @param mouseY vertical coordinate
     * @return item, null otherwise
     */
    public DynamicPanel getItemAt(int mouseX, int mouseY) {
        if(isSelected(mouseX, mouseY)) {
            for (DynamicPanel i : items) {
                Shape shape = i.getBorderBox();
                if(shape != null && shape.contains(mouseX, mouseY)) {
                    return i;
                }
            }
        }
        return null;
    }

    /** Checks if mouse is over DynamicPanel
     *
     * @param mouseX horizontal coordinate
     * @param mouseY vertical coordinate
     * @return true if selected, false otherwise
     */
    public boolean isSelected(int mouseX, int mouseY) {
        return (borderBox != null && borderBox.contains(mouseX, mouseY));
    }

    /** Returns spacing of DynamicPanels
     *
     * @return spacing of DynamicPanels
     */
    public int getSpacing() {
        return spacing;
    }

    /** Returns border of DynamicPanels
     *
     * @return border numItems
     */
    public int getPanelBorder() {
        return border;
    }

    /** Calculates the max height
     *  with borders and spacing
     *
     * @return height of DynamicPanel
     */
    public int getHeight() {
        int height = 2 * border;
        for (DynamicPanel i : items) {
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
        for (DynamicPanel i : items) {
            width = Math.max(i.getWidth(), width);
        }
        return width + 2 * border;
    }

    /** Calculates the max width without
     *  borders
     *
     * @return width of the largest item
     */
    public int getBorderlessWidth() {
        return getWidth() - 2 * border;
    }


    /** Draw method paints background and all DynamicPanels
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
        borderBox = new Rectangle(pX, pY, width, height);
        g.fillRect(pX, pY, width, height);

        pX += border;
        pY += border;

        //loops through contained items
        for (DynamicPanel i : items) {
            g.setColor(backColor);
            g.setFont(font);
            i.draw(g, pX, pY);
            pY += i.getHeight() + spacing;
        }
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isSelected() {
        return selected;
    }

    protected Shape getBorderBox() {
        return borderBox;
    }
}
