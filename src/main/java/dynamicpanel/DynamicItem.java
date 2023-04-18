package dynamicpanel;

import javax.swing.*;
import java.awt.*;

/** DynamicItem class creates the objects to
 *  be placed into a DynamicPanel.
 *
 */
public class DynamicItem extends JPanel {
    private int height;
    private int width;
    private boolean selected;
    private Rectangle borderBox;

    public DynamicItem(int width, int height) {
        setDimensions(width, height);
    }

    public DynamicItem() {
    }

    public void setDimensions(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public Shape getDynamicItem() {
        return borderBox;
    }

    public void draw(Graphics g, int pX, int pY) {
        borderBox = new Rectangle(pX, pY, width, height);
        if(isSelected()) {
            g.setColor(Color.gray);
            g.fillRect(pX, pY, width, height);
        }
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isSelected() {
        return selected;
    }
}
