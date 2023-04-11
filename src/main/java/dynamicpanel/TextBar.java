package dynamicpanel;

import java.awt.*;

public class TextBar extends DynamicItem {
    private String text;
    private Color color;
    private Font font;
    public TextBar(int width, int height, String text) {
        super(width, height);
        this.text = text;
    }

    public void setColor(Color c) {
        this.color = c;
    }

    public void setFont(Font f) {
        this.font = f;
    }

    @Override
    public void draw(Graphics g, int pX, int pY) {
        if(color != null) {
            g.setColor(color);
        }
        if(font != null) {
            g.setFont(font);
        }
        int dY = (getFontMetrics(g.getFont()).getAscent() + super.getHeight()) / 2 + 2;
        g.drawString(text, pX, pY + dY);
    }
}
