package dynamicpanel;

import java.awt.*;
import java.awt.image.BufferedImage;

/** Dynamic Image extends DynamicItem and
 *  can display a buffered image.
 */
public class DynamicImage extends DynamicItem {
    private BufferedImage image;

    /** 3-arg constructor instantiates a DynamicItem
     *  with a buffered image
     * @param width item width
     * @param height item height
     * @param image buffered image
     */
    public DynamicImage(int width, int height, BufferedImage image) {
        super(width, height);
        this.image = image;
    }

    /** Setter method for image and updates dimensions
     *
     * @param image new image
     */
    public void setImage(BufferedImage image) {
        this.image = image;
        if(image == null) {
            super.setDimensions(0, 0);
        }
        else {
            super.setDimensions(image.getWidth(), image.getHeight());
        }
    }

    /** Draws buffered image
     *
     * @param g graphics
     * @param pX start x position
     * @param pY start y position
     */
    @Override
    public void draw(Graphics g, int pX, int pY) {
        super.draw(g, pX, pY);
        g.drawImage(image, pX, pY, null);
    }

    /** Accessor Method for image
     *
     * @return image
     */
    public BufferedImage getImage() {
        return image;
    }
}
