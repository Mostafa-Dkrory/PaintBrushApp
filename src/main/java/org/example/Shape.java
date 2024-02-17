package org.example;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 *
 * @author DKRORY
 */

enum ShapeType {LINE,RECTANGLE,OVAL,FREEHAND,IMAGE,ERASE}

abstract class Shape {
    protected Point start;
    protected Point end;
    protected Color color;
    protected boolean isDotted;
    protected boolean isFilled;

    abstract void draw(Graphics g);
}

// Class representing a line shape
class Line extends Shape {
    // Constructor for the Line class
    public Line(Point start, Point end, Color color, boolean dotted) {
        this.start = start;
        this.end = end;
        this.color = color;
        this.isDotted = dotted;
    }

    // Override method to draw the Line shape on the graphics context
    @Override
    void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(color);

        // Set stroke properties based on whether the shape is dotted or solid
        if (isDotted) {
            float[] dashPattern = {3, 3};
            g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 2.0f, dashPattern, 0.0f));
        } else {
            g2d.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1.0f, null, 0.0f));
        }
        g2d.drawLine(start.x, start.y, end.x, end.y);
    }
}

// Class representing a rectangle shape
class Rectangle extends Shape {
    // Constructor for the Rectangle class
    public Rectangle(Point start, Point end, Color color, boolean dotted, boolean filled) {
        this.start = start;
        this.end = end;
        this.color = color;
        this.isDotted = dotted;
        this.isFilled = filled;
    }

    // Override method to draw the Rectangle shape on the graphics context
    @Override
    void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(color);

        // Set stroke properties based on whether the shape is dotted or solid
        if (isDotted) {
            float[] dashPattern = {3, 3};
            g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 2.0f, dashPattern, 0.0f));
        } else {
            g2d.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1.0f, null, 0.0f));
        }

        int width = Math.abs(end.x - start.x);
        int height = Math.abs(end.y - start.y);
        int x = Math.min(start.x, end.x);
        int y = Math.min(start.y, end.y);

        // Draw either a filled or an outlined rectangle based on the 'filled' property
        if (isFilled) {
            g2d.fillRect(x, y, width, height);
        } else {
            g2d.drawRect(x, y, width, height);
        }
    }
}

// Class representing an oval shape
class Oval extends Shape {

    // Constructor for the Oval class
    public Oval(Point start, Point end, Color color, boolean dotted, boolean filled) {
        this.start = start;
        this.end = end;
        this.color = color;
        this.isDotted = dotted;
        this.isFilled = filled;
    }

    // Override method to draw the Oval shape on the graphics context
    @Override
    void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(color);

        // Set stroke properties based on whether the shape is dotted or solid
        if (isDotted) {
            float[] dashPattern = {3, 3};
            g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 2.0f, dashPattern, 0.0f));
        } else {
            g2d.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1.0f, null, 0.0f));
        }

        int width = Math.abs(end.x - start.x);
        int height = Math.abs(end.y - start.y);
        int x = Math.min(start.x, end.x);
        int y = Math.min(start.y, end.y);

        // Draw either a filled or an outlined oval based on the 'filled' property
        if (isFilled) {
            g2d.fillOval(x, y, width, height);
        } else {
            g2d.drawOval(x, y, width, height);
        }
    }
}

// Class representing an image shape
class ImageShape extends Shape {

    private final BufferedImage image;
    private final Point position;

    // Constructor for the ImageShape class
    public ImageShape(BufferedImage image, Point position) {
        this.image = image;
        this.position = position;
    }

    // Override method to draw the image on the graphics context
    @Override
    void draw(Graphics g) {
        g.drawImage(image, position.x, position.y, null);
    }
}

// Class representing a Freehand shape
class Freehand extends Shape {

    private final ArrayList<Point> points;


    // Constructor for the Freehand class
    public Freehand(ArrayList<Point> points, Color color, boolean dotted) {
        this.points = new ArrayList<>(points);
        this.color = color;
        this.isDotted = dotted;
    }

    // Override method to draw the Freehand shape on the graphics context
    @Override
    void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(color);
        // Set stroke properties based on whether the shape is dotted or solid
        if (isDotted) {
            float[] dashPattern = {3, 3};
            g2d.setStroke(new BasicStroke(5, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 2.0f, dashPattern, 0.0f));
        } else {
            g2d.setStroke(new BasicStroke(5.0f));
        }
        // Draw lines connecting the points to represent the Freehand shape
        for (int i = 1; i < points.size(); i++) {
            Point p1 = points.get(i - 1);
            Point p2 = points.get(i);
            g2d.drawLine(p1.x, p1.y, p2.x, p2.y);
        }
    }
}

class Erase extends Shape {
    private final ArrayList<Point> erasedPoints;

    // Constructor for the Freehand class
    public Erase(ArrayList<Point> points) {
        this.erasedPoints = new ArrayList<>(points);

    }

    // Override method to Erase
    @Override
    void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(PaintBrushFrame.drawingPanel.getBackground());
        // Set stroke properties based on whether the shape is dotted or solid
        g2d.setStroke(new BasicStroke(20.0f));
        // Draw lines connecting the points to represent the Freehand shape
        for (int i = 1; i < erasedPoints.size(); i++) {
            Point p1 = erasedPoints.get(i - 1);
            Point p2 = erasedPoints.get(i);
            g2d.drawLine(p1.x, p1.y, p2.x, p2.y);
        }
    }
}

/*

// Class representing an Eraser shape (extends Rectangle)
class Eraser extends Rectangle {
    // Constructor for the Eraser class
    public Eraser(Point start, Point end, Color color) {
        super(start, end, color, false, true);
    }

    // Override method to draw the Eraser shape on the graphics context
    @Override
    void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(drawingPanel.getBackground());
        super.draw(g2d);


    }
}

*/