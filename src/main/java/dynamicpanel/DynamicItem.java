package dynamicpanel;

import java.awt.*;

/** DynamicItem class creates the objects to
 *  be placed into a DynamicPanel.
 *
 */
public class DynamicItem extends DynamicPanel {
    private int height;
    private int width;
    protected Font font;

    /** 2-arg constructor instantiates a item
     *  with dimensions
     *
     * @param width vertical size
     * @param height horizontal size
     */
    public DynamicItem(int width, int height) {
        setDimensions(width, height);
    }

    /** 0-arg constructor instantiates empty item
     *
     */
    public DynamicItem() {
        height = 0;
        width = 0;
    }

    public void setDimensions(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public void draw(Graphics g, int pX, int pY) {
        super.borderBox = new Rectangle(pX, pY, width, height);
        if(isSelected()) {
            g.setColor(Color.gray);
            g.fillRect(pX, pY, width, height);
        }
    }
}
