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
    private Color selectionColor;
    protected String description;

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
        this(0, 0);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public void setSelectionColor(Color selectionColor) {
        this.selectionColor = selectionColor;
    }

    @Override
    public void draw(Graphics g, int pX, int pY) {
        Graphics2D g2 = (Graphics2D) g;
        super.borderBox = new Rectangle(pX, pY, width, height);
        if(isSelected()) {
            if(selectionColor == null) {
                selectionColor = Color.gray;
            }
            g2.setColor(selectionColor);
        }
        else {
            g2.setColor(backColor);
        }
        g2.fill(borderBox);
    }

    @Override
    public String toString() {
        if(description == null) {
            return "N/A";
        }
        return description;
    }
}
