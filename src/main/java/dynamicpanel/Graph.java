package dynamicpanel;

import java.awt.*;
import java.awt.geom.Line2D;
import java.util.ArrayList;

public class Graph extends DynamicItem {
    private String xAxis;
    private String yAxis;
    private final ArrayList<int[]> points;
    private Color lineColor;
    private int lineWidth;

    public Graph(int width, int height) {
        super(width, height);
        points = new ArrayList<>();
    }

    public int numPoints() {
        return points.size();
    }

    public void addPoint(int x, int y) {
        points.add(new int[]{x, y});
    }

    public void removeLast() {
        points.remove(points.size() - 1);
    }

    public void clear() {
        points.clear();
    }

    public double getSlope() {
        int[] pStart = points.get(0);
        int[] pEnd = points.get(points.size() - 1);
        return (double) (pEnd[1] - pStart[1]) / (pEnd[0] - pStart[0]);
    }

    public int[] getPoint(int i) {
        return points.get(i);
    }

    public int maxHeight() {
        int max = 0;
        for(int[] point : points) {
            if(point[1] > max) {
                max = point[1];
            }
        }
        return max;
    }

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

    @Override
    public void draw(Graphics g, int pX, int pY) {
        super.draw(g, pX, pY);
        if(points.size() < 2) {
            return;
        }
        Graphics2D g2 = (Graphics2D) g;

        if(lineWidth > 0) {
            g2.setStroke(new BasicStroke(lineWidth));
        }

        double xScale = 0;
        double yScale = 0;

        if(maxWidth() > 0) {
            xScale = super.getWidth() / (double) maxWidth();
        }
        if(maxHeight() > 0) {
            yScale = super.getHeight() / (double) maxHeight();
        }

        for(int i = 0; i < points.size() - 1; i++) {
            int[] p1 = points.get(i);
            int[] p2 = points.get(i + 1);
            if(lineColor == null) {
                lineColor = Color.white;
            }
            g.setColor(lineColor);
            g2.draw(new Line2D.Double(pX + p1[0] * xScale, pY + super.getHeight() - p1[1] * yScale,
                        pX + p2[0] * xScale, pY + super.getHeight() - p2[1] * yScale));
            g.setColor(Color.darkGray);

        }
    }
}