package dynamicpanel;

import java.awt.*;
import java.awt.image.BufferedImage;

public class DynamicImage extends DynamicItem {
    private BufferedImage image;
    public DynamicImage(int width, int height, BufferedImage image) {
        super(width, height);
        this.image = image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
        if(image == null) {
            super.setDimensions(0, 0);
        }
        else {
            super.setDimensions(image.getWidth(), image.getHeight());
        }
    }

    @Override
    public void draw(Graphics g, int pX, int pY) {
        super.draw(g, pX, pY);
        g.drawImage(image, pX, pY, null);
    }
}
