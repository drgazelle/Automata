package dynamicpanel;

import java.awt.*;
import java.awt.geom.Rectangle2D;

/** ProgressBar extends DynamicItem by
 *  representing a progress bar that can
 *  be adapted and modified for changes.
 */
public class ProgressBar extends DynamicItem {
    private int max;
    private int min;
    private int progress;
    private Color barColor;
    private Color progColor;
    private Color textColor;

    /** 4-arg constructor instantiates empty
     *  progress bar
     *
     * @param width horizontal numItems
     * @param height vertical numItems
     * @param min minimum value
     * @param max maximum value
     *
     */
    public ProgressBar(int width, int height, int min, int max) {
        this(width, height, min, max, min);
    }

    /** 5-arg constructor instantiates active
     *  progress bar
     *
     * @param width horizontal numItems
     * @param height vertical numItems
     * @param min minimum value
     * @param max maximum value
     * @param progress quantity of completion
     *
     */
    public ProgressBar(int width, int height, int min, int max, int progress) {
        super(width, height);
        this.min = min;
        this.max = max;
        this.progress = progress;
        setDefaultColors();
    }

    private void setDefaultColors() {
        barColor = Color.red;
        progColor = Color.green;
        textColor = Color.black;
    }

    /** Sets progress bar theme
     *
     * @param barColor back color
     * @param progColor front color
     * @param textColor text color
     */
    public void setColors(Color barColor, Color progColor, Color textColor) {
        this.barColor = barColor;
        this.progColor = progColor;
        this.textColor = textColor;
    }

    /** Updates progress
     *
     * @param p progress
     */
    public void setProgress(int p) {
        this.progress =  p;
    }

    // Accessor Methods //

    public int getProgress() {
        return progress;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    /** Draw method paints progress bar with text
     *
     * @param g graphics
     * @param pX horizontal coordinate
     * @param pY vertical coordinate
     */
    @Override
    public void draw(Graphics g, int pX, int pY) {
        Graphics2D g2 = (Graphics2D) g;
        //draws full percent bar
        g2.setColor(barColor);
        g2.fillRect(pX, pY, super.getWidth(), super.getHeight());

        //draws progress bar
        double percent = ((double) super.getWidth() / max) * progress;
        g2.setColor(progColor);
        g2.fill(new Rectangle2D.Double(pX, pY, percent, super.getHeight()));

        //draws progress num
        int dY = (super.getHeight() + getFontMetrics(g2.getFont()).getAscent()) / 2 - 2;
        g2.setColor(textColor);
        g2.drawString(String.valueOf(progress), pX + 5, pY + dY);

        //draws max num
        int textWidth = getFontMetrics(g.getFont()).stringWidth(String.valueOf(max));
        g2.drawString(String.valueOf(max), pX + super.getWidth() - textWidth - 5, pY + dY);
    }
}
