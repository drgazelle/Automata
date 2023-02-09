import javax.swing.JPanel;
import java.awt.*;

public class DynamicMenu extends JPanel {
    private int index;
    private int border;
    private int gap;
    private String title;
    private String[] items;
    private static Color titleColor, mainColor, backgroundColor;
    private static Font mainFont, titleFont;

    /** 2-arg constructor instantiates menu with no index
     *
     * @param title menu header
     * @param items display items
     */
    public DynamicMenu(String title, String[] items) {
        this.title = title;
        this.items = items;
        this.index = -1;
        setDefaultColors();
        setDefaultBorder();
    }

    /** 3-arg constructor instantiates menu with an index
     *
     * @param title menu header
     * @param items display items
     * @param index selection index
     */
    public DynamicMenu(String title, String[] items, int index) {
        this.title = title;
        this.items = items;
        this.index = index;
        setDefaultColors();
        setDefaultBorder();
    }

    /** Setter Method for main font.
     *
     * @param f font to be used
     */
    public static void setMainFont(Font f) {
        mainFont = f;
    }

    /** Setter Method for title font.
     *
     * @param f font to be used
     */
    public static void setTitleFont(Font f) {
        titleFont = f;
    }

    /** Sets default for background and text colors
     *
     */
    public void setDefaultColors() {
        mainColor = Color.WHITE;
        backgroundColor = Color.BLACK;
    }

    /** Sets default menu border and gap */
    private void setDefaultBorder() {
        border = 5;
        gap = 2;
    }

    /** Sets title color.
     *
     * @param c title color
     */
    public static void setTitleColor(Color c) {
        titleColor = c;
    }

    /** Sets text color.
     *
     * @param c text color
     */
    public static void setTextColor(Color c) {
        mainColor = c;
    }

    /** Sets background color.
     *
     * @param c background color
     */
    public static void setBackgroundColor(Color c) {
        backgroundColor = c;
    }

    /** Calculates the width of the menu.
     *
     * @return menu width
     */
    public int getBoxWidth() {
        int boxWidth = getFontMetrics(titleFont).stringWidth(title);
        for(String item : items) {
            boxWidth = Math.max(boxWidth, getFontMetrics(mainFont).stringWidth(item));
        }
        boxWidth += border * 2;

        return boxWidth;
    }

    /** Calculates the menu height.
     *
     * @return menu height
     */
    public int getBoxHeight() {
        return getFontMetrics(titleFont).getAscent() + (2 * gap)
                + (getFontMetrics(mainFont).getAscent() + gap) * (items.length)
                + (border * 2);
    }

    /** Paints a dynamic menu at a given X and Y coordinate
     *
     * @param g graphics pass-through
     * @param pX x position
     * @param pY y position
     */
    public void paintMenu(Graphics g, int pX, int pY) {
        Graphics2D g2 = (Graphics2D) g;

        FontMetrics mainMetrics = getFontMetrics(mainFont);
        FontMetrics titleMetrics = getFontMetrics(titleFont);

        //Defines height of box
        int boxHeight = getBoxHeight();

        //calculates max width
        int boxWidth = getBoxWidth();

        //box for menu background
        Shape menuBackground = new Rectangle(pX, pY, boxWidth, boxHeight);
        g2.setColor(backgroundColor);
        g2.fill(menuBackground);

        //adjusts to border
        pY += border + titleMetrics.getAscent() - gap;
        pX += border;

        //title
        g2.setColor(titleColor);
        g2.setFont(titleFont);
        g2.drawString(title, pX, pY);
        //adjusts y position
        pY += mainMetrics.getAscent() + (2 * gap);

        //resets color and font
        g2.setColor(mainColor);
        g2.setFont(mainFont);

        //loops through items and draws strings according to parameters
        for (int i = 0; i < items.length; i++) {
            //Highlights index
            if(i == index) {
                //highlight x and y position
                int iX = pX - (border / 2);
                int iY = pY - mainMetrics.getAscent() + gap;
                Shape highlightBox = new Rectangle(iX, iY, boxWidth - border, mainMetrics.getAscent());
                g2.setColor(Color.darkGray);
                g2.fill(highlightBox);
                //resets color
                g2.setColor(mainColor);
            }
            //loops through items
            g2.drawString(items[i], pX, pY);
            pY += mainMetrics.getAscent() + gap;
        }
    }
}
