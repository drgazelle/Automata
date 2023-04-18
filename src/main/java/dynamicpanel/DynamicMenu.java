package dynamicpanel;
import automata.MainPanel;

import java.awt.*;

/** DynamicMenu renders a list of strings with
 *  a title that adjusts to the given input
 *
 * @author RMizelle
 */
public class DynamicMenu extends DynamicPanel {
    private int index;
    private String title;
    private String[] items;
    private static Color titleColor, mainColor;
    private Color indexColor;
    private static Font titleFont;

    /** 2-arg constructor instantiates menu with no index
     *
     * @param title menu header
     * @param items display items
     */
    public DynamicMenu(String title, String[] items) {
        this(title, items, 0);
    }

    /** 3-arg constructor instantiates menu with an index
     *
     * @param title menu header
     * @param items display items
     * @param index selection index
     */
    public DynamicMenu(String title, String[] items, int index) {
        this.items = items;
        this.index = index;
        this.title = title;
        setDefaultColors();
    }


    /** Setter Method for title font.
     *
     * @param f font to be used
     */
    public static void setTitleFont(Font f) {
        titleFont = f;
    }

    public void setTitle(String str) {
        title = str;
    }

    public void setItems(String[] items) {
        super.clear();

        for(int i = 0; i < items.length; i++) {
            TextBar item = new TextBar(items[i], MainPanel.mainFont);
            item.setColor(mainColor);
            super.add(item);
        }
    }

    /** Sets default for background and text colors
     *
     */
    public void setDefaultColors() {
        mainColor = Color.WHITE;
        indexColor = Color.darkGray;
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

    /** Sets index color
     *
     * @param c index color
     */
    public void setIndexColor(Color c) {
        indexColor = c;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public void draw(Graphics g, int pX, int pY) {
        if(index > 0) {
            super.select(index);
        }
        super.draw(g, pX, pY);
    }
}
