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

    public DynamicItem(int width, int height) {
        setDimensions(width, height);
    }

    private void setDimensions(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public void draw(Graphics g, int pX, int pY) {
        g.fillRect(pX, pY, width, height);
    }
}
