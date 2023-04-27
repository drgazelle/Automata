package dynamicpanel;

import java.awt.*;
import java.awt.geom.Line2D;
import java.util.ArrayList;

public class Graph extends DynamicItem {
    private final ArrayList<int[]> points;
    private Color lineColor;
    private int lineWidth;

    /** 2-arg constructor instantiates graph of
     *  a given width and height
     *
     * @param width horizontal distance
     * @param height vertical distance
     */
    public Graph(int width, int height) {
        super(width, height);
        points = new ArrayList<>();
    }

    /** Returns number of points
     *
     * @return size of points array
     */
    public int numPoints() {
        return points.size();
    }

    /** Adds a point at a coordinate
     *
     * @param x coordinate
     * @param y coordinate
     */
    public void addPoint(int x, int y) {
        points.add(new int[]{x, y});
    }

    /** Removes the last point added */
    public void removeLast() {
        points.remove(points.size() - 1);
    }

    /** Clears graph */
    public void clear() {
        points.clear();
    }

    /** Calculates slope from start point
     *  to end point
     *
     * @param start start index, inclusive
     * @param end end index, exclusive
     * @return slope between points
     */
    public double getSlope(int start, int end) {
        int[] pStart = points.get(start);
        int[] pEnd = points.get(end - 1);
        return (double) (pEnd[1] - pStart[1]) / (pEnd[0] - pStart[0]);
    }

    /** Gets point at index
     *
     * @param i index
     * @return point array [x,y]
     */
    public int[] getPoint(int i) {
        return points.get(i);
    }

    /** Calculates the absolute max
     *
     * @return absolute max
     */
    public int maxHeight() {
        int max = 0;
        for(int[] point : points) {
            if(point[1] > max) {
                max = point[1];
            }
        }
        return max;
    }

    /** Returns largest x value
     *
     * @return largest x value
     */
    public int maxWidth() {
        if(points.isEmpty()) {
            return 0;
        }
        return points.get(points.size() - 1)[0];
    }

    public void setLineColor(Color lineColor) {
        this.lineColor = lineColor;
    }

    public void setLineWidth(int lineWidth) {
        this.lineWidth = lineWidth;
    }

    /** Draws the graph within the bounding box
     *
     * @param g graphics
     * @param pX start x position
     * @param pY start y position
     */
    @Override
    public void draw(Graphics g, int pX, int pY) {
        super.draw(g, pX, pY);
        if(points.size() < 2) {
            //returns if too few points
            return;
        }

        //Enables Anti-aliasing
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        //sets line width if valid
        if(lineWidth > 0) {
            g2.setStroke(new BasicStroke(lineWidth));
        }

        double xScale = 0;
        double yScale = 0;
        if(maxWidth() > 0) {
            //if non-zero width
            xScale = (super.getWidth() - lineWidth) / (double) maxWidth();
        }
        if(maxHeight() > 0) {
            //if non-zero height
            yScale = (super.getHeight() - lineWidth) / (double) maxHeight();
        }
        //loops through points
        for(int i = 0; i < points.size() - 1; i++) {
            int[] p1 = points.get(i);
            int[] p2 = points.get(i + 1);
            if(lineColor == null) {
                //if no set line color
                lineColor = Color.white;
            }
            g.setColor(lineColor);
            //draws line between points
            g2.draw(new Line2D.Double(pX + p1[0] * xScale, pY + super.getHeight() - p1[1] * yScale,
                        pX + p2[0] * xScale, pY + super.getHeight() - p2[1] * yScale));

        }
        //disables antialiasing
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_OFF);
    }
}