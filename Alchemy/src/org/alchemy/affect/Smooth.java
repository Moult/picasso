/*
 * This file is part of the Alchemy project - http://al.chemy.org
 * 
 * Copyright (c) 2007-2009 Karl D.D. Willis
 * 
 * Alchemy is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Alchemy is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Alchemy.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.alchemy.affect;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import org.alchemy.core.*;

/**
 * Smooth Module
 * @author Karl D.D. Willis 
 */
public class Smooth extends AlcModule {

    private boolean mouseDown = false;
    private int spacing = 50;
    private long time;

    public Smooth() {
    }

    @Override
    protected void setup() {
    }

    @Override
    protected void reselect() {
    }

    private void smoothShape(Point currentLoc, int index) {
        AlcShape shape = canvas.shapes.get(index);
        ArrayList<Point2D.Float> points = getPoints(shape.getPath());
        smoothShape(shape, points);
        canvas.redraw(true);
    }

    private ArrayList<Point2D.Float> getPoints(GeneralPath path) {
        PathIterator pathIterator = path.getPathIterator(null);
        float[] points = new float[6];
        ArrayList<Point2D.Float> list = new ArrayList<Point2D.Float>(1000);

        while (!pathIterator.isDone()) {

            switch (pathIterator.currentSegment(points)) {
                case PathIterator.SEG_MOVETO:
                    list.add(new Point2D.Float(points[0], points[1]));
                    break;
                case PathIterator.SEG_LINETO:
                    list.add(new Point2D.Float(points[0], points[1]));
                    break;
                case PathIterator.SEG_QUADTO:
                    list.add(new Point2D.Float(points[2], points[3]));
                    break;
                case PathIterator.SEG_CUBICTO:
                    list.add(new Point2D.Float(points[4], points[5]));
                    break;
            }
            pathIterator.next();
        }
        return list;
    }

    private void smoothShape(AlcShape shape, ArrayList<Point2D.Float> points) {
        shape.setPath(null);
        GeneralPath path = new GeneralPath(GeneralPath.WIND_NON_ZERO, 1000);
        Point2D.Float firstPoint = points.get(0);
        //System.out.println(points.size());
        path.moveTo(firstPoint.x, firstPoint.y);
        for (int i = 0; i < points.size() - 1; i++) {
            if (i == points.size() - 2) {
                Point2D.Float pt = points.get(i);
                path.lineTo(pt.x, pt.y);
            } else {
                Point2D.Float p0 = points.get(i);
                Point2D.Float p1 = points.get(i + 1);
                Point2D.Float p2 = points.get(i + 2);

                float x = p0.x * 0.25F + p1.x * 0.5F + p2.x * 0.25F;
                float y = p0.y * 0.25F + p1.y * 0.5F + p2.y * 0.25F;
                path.lineTo(x, y);
            }
        }
        shape.setPath(path);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (!mouseDown) {
            if (System.currentTimeMillis() - time >= spacing) {
                int firstShape = -1;
                Point pt = e.getPoint();
                // Loop through from the newest shape and find the first one the mouse is over
                for (int i = canvas.shapes.size() - 1; i >= 0; i--) {
                    AlcShape thisShape = canvas.shapes.get(i);
                    if (thisShape.getPath().contains(pt)) {
                        firstShape = i;
                        break;
                    }
                }
                if (firstShape >= 0) {
                    smoothShape(pt, firstShape);
                }
                time = System.currentTimeMillis();
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        mouseDown = true;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        mouseDown = false;
    }
}
