package dynamicpanel;

import automata.MainPanel;

import java.awt.*;

public class TextBar extends DynamicItem {
    private String text;
    private Color textColor;
    private Font textFont;

    public TextBar(String text, Font font) {
        super();
        this.textFont = font;
        this.text = text;
        super.setDimensions(getWidth(), getHeight());
    }

    @Override
    public int getHeight() {
        return getFontMetrics(textFont).getAscent() - getFontMetrics(textFont).getDescent();
    }

    @Override
    public int getWidth() {
        return getFontMetrics(textFont).stringWidth(text);
    }

    public void setColor(Color c) {
        textColor = c;
    }


    @Override
    public void draw(Graphics g, int pX, int pY) {
        super.draw(g, pX, pY);
        if(textFont != null) {
            g.setFont(textFont);
        }
        if(textColor != null) {
            g.setColor(textColor);
        }
        if(isSelected()) {
            g.setColor(Color.darkGray);
            g.fillRect(pX - 3, pY - 3, getWidth() + 6, getHeight() + 6);
        }
        g.setColor(textColor);
        g.drawString(text, pX, pY + getHeight());
    }
}
