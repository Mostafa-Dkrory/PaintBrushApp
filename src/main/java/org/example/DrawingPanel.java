package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

class DrawingPanel extends JPanel {

    // Various properties for the drawing panel
    private final ArrayList<Shape> shapes = new ArrayList<>();
    private Color currentColor = Color.BLACK;
    private int currentShape = 0;
    private boolean drawing = false;
    private boolean erasing = false;
    private Point startPoint;
    private Point endPoint;
    private boolean dotted = false;
    private boolean filled = false;
    private final ArrayList<Point> freehandPoints = new ArrayList<>();
    private final ArrayList<Point> erasePoints = new ArrayList<>();

    // Constructor for the DrawingPanel
    public DrawingPanel() {
        // Set the background color of the drawing panel
        setBackground(Color.WHITE);

        // Add mouse listeners for handling drawing actions
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                startPoint = e.getPoint();
                if (currentShape == 3) {
                    drawing = true;
                    freehandPoints.clear();
                    freehandPoints.add(startPoint);
                } else if (currentShape == 5) {
                    erasing = true;
                    erasePoints.clear();
                    erasePoints.add(startPoint);
                } else {
                    shapes.add(createShape(startPoint, startPoint, currentColor, dotted, filled));
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                endPoint = e.getPoint();
                if (currentShape == 3) {
                    drawing = false;
                    shapes.add(new Freehand(freehandPoints, currentColor, dotted));
                    freehandPoints.clear();  // Clear the points after adding the Freehand shape
                } else  if (currentShape == 5) {
                    erasing = false;
                    shapes.add(new Erase(erasePoints, getBackground()));
                    erasePoints.clear();
                    currentColor=Color.BLACK;// Clear the points after adding the Freehand shape
                }else {
                    shapes.add(createShape(startPoint, endPoint, currentColor, dotted, filled));
                }
                repaint();
                startPoint = null;
            }

        });

        // Add mouse motion listener for freehand drawing
        this.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                endPoint = e.getPoint();
                if(currentShape == 3) {
                    if (drawing) {
                        freehandPoints.add(endPoint);
                        repaint();
                    }
                } else if (currentShape == 5) {
                    if (erasing) {
                        erasePoints.add(endPoint);
                        repaint();
                    }
                } else {
                    // For other shapes --> add and remove the shape while dragging
                    shapes.removeLast();
                    shapes.add(createShape(startPoint, endPoint, currentColor, dotted, filled));
                    repaint();
                }
            }
        });
    }


    // Method to create a shape based on current properties
    private Shape createShape(Point start, Point end, Color color, boolean dotted, boolean filled) {
        return switch (currentShape) {
            case 0 -> // Line
                    new Line(start, end, color, dotted);
            case 1 -> // Rectangle
                    new Rectangle(start, end, color, dotted, filled);
            case 2 -> //Oval
                    new Oval(start, end, color, dotted, filled);
            case 4 -> //Eraser
                    new Eraser(start, end, getBackground());
            default -> null;
        };
    }

    // Set the current drawing color
    public void setCurrentColor(Color color) {
        currentColor = color;
    }

    // Set the current drawing shape
    public void setCurrentShape(int shape) {
        currentShape = shape;
    }

    public  int getCurrentShape() {
        return currentShape;
    }

    // Set the dotted property
    public void setDotted(boolean isDotted) {
        dotted = isDotted;
        repaint();
    }

    // Set the filled property
    public void setFilled(boolean isFilled) {
        filled = isFilled;
        repaint();
    }

    // Clear all shapes from the drawing panel
    public void clearAll() {
        shapes.clear();
        repaint();
    }

    // Undo the last drawn shape
    public void undo() {
        if (!shapes.isEmpty()) {
            shapes.removeLast();
            repaint();
        }
    }

    // Override paintComponent to draw shapes on the panel
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (Shape shape : shapes) {
            shape.draw(g);
        }
        if (drawing && currentShape == 3) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(currentColor);
            g2d.setStroke(new BasicStroke());
            for (int i = 1; i < freehandPoints.size(); i++) {
                Point p1 = freehandPoints.get(i - 1);
                Point p2 = freehandPoints.get(i);
                g2d.drawLine(p1.x, p1.y, p2.x, p2.y);
            }
        }
    }

    // Load an image onto the drawing panel
    public void loadImage(BufferedImage img) {

        shapes.add(new ImageShape(img, new Point(0, 0)));
        repaint();
    }

    // Abstract class representing a shape
    abstract static class Shape {

        abstract void draw(Graphics g);
    }

    // Class representing a line shape
    static class Line extends Shape {

        private final Point start;
        private final Point end;
        private final Color color;
        private final boolean dotted;

        // Constructor for the Line class
        public Line(Point start, Point end, Color color, boolean dotted) {
            this.start = start;
            this.end = end;
            this.color = color;
            this.dotted = dotted;
        }

        // Override method to draw the Line shape on the graphics context
        @Override
        void draw(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(color);

            // Set stroke properties based on whether the shape is dotted or solid
            if (dotted) {
                float[] dashPattern = {3, 3};
                g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 2.0f, dashPattern, 0.0f));
            } else {
                g2d.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1.0f, null, 0.0f));
            }
            g2d.drawLine(start.x, start.y, end.x, end.y);
        }
    }

    // Class representing a rectangle shape
    static class Rectangle extends Shape {

        private final Point start;
        private final Point end;
        private final Color color;
        private final boolean dotted;
        private final boolean filled;

        // Constructor for the Rectangle class
        public Rectangle(Point start, Point end, Color color, boolean dotted, boolean filled) {
            this.start = start;
            this.end = end;
            this.color = color;
            this.dotted = dotted;
            this.filled = filled;
        }

        // Override method to draw the Rectangle shape on the graphics context
        @Override
        void draw(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(color);

            // Set stroke properties based on whether the shape is dotted or solid
            if (dotted) {
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
            if (filled) {
                g2d.fillRect(x, y, width, height);
            } else {
                g2d.drawRect(x, y, width, height);
            }
        }
    }

    // Class representing an oval shape
    static class Oval extends Shape {

        private final Point start;
        private final Point end;
        private final Color color;
        private final boolean dotted;
        private final boolean filled;

        // Constructor for the Oval class
        public Oval(Point start, Point end, Color color, boolean dotted, boolean filled) {
            this.start = start;
            this.end = end;
            this.color = color;
            this.dotted = dotted;
            this.filled = filled;
        }

        // Override method to draw the Oval shape on the graphics context
        @Override
        void draw(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(color);

            // Set stroke properties based on whether the shape is dotted or solid
            if (dotted) {
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
            if (filled) {
                g2d.fillOval(x, y, width, height);
            } else {
                g2d.drawOval(x, y, width, height);
            }
        }
    }

    // Class representing an image shape
    static class ImageShape extends Shape {

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
    static class Freehand extends Shape {

        private final ArrayList<Point> points;
        private final Color color;
        private final boolean dotted;

        // Constructor for the Freehand class
        public Freehand(ArrayList<Point> points, Color color, boolean dotted) {
            this.points = new ArrayList<>(points);
            this.color = color;
            this.dotted = dotted;
        }
        // Override method to draw the Freehand shape on the graphics context
        @Override
        void draw(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(color);
            // Set stroke properties based on whether the shape is dotted or solid
            if (dotted) {
                float[] dashPattern = {3, 3};
                g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 2.0f, dashPattern, 0.0f));
            } else {
                g2d.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1.0f, null, 0.0f));
            }
            // Draw lines connecting the points to represent the Freehand shape
            for (int i = 1; i < points.size(); i++) {
                Point p1 = points.get(i - 1);
                Point p2 = points.get(i);
                g2d.drawLine(p1.x, p1.y, p2.x, p2.y);
            }
        }
    }
    static class Erase extends Shape {
        private final ArrayList<Point> erasedPoints;
        private final Color color;
        // Constructor for the Freehand class
        public Erase(ArrayList<Point> points, Color color) {
            this.erasedPoints = new ArrayList<>(points);
            this.color = color;
        }
        // Override method to draw the Freehand shape on the graphics context
        @Override
        void draw(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(color);
            // Set stroke properties based on whether the shape is dotted or solid
            g2d.setStroke(new BasicStroke(20.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1.0f, null, 0.0f));
            // Draw lines connecting the points to represent the Freehand shape
            for (int i = 1; i < erasedPoints.size(); i++) {
                Point p1 = erasedPoints.get(i - 1);
                Point p2 = erasedPoints.get(i );
                g2d.drawLine(p1.x, p1.y, p2.x, p2.y);
            }
        }
    }

    // Class representing an Eraser shape (extends Rectangle)
    static class Eraser extends Rectangle {
        // Constructor for the Eraser class
        public Eraser(Point start, Point end,Color color) {
            super(start, end, color , false, true);
        }

        // Override method to draw the Eraser shape on the graphics context
        @Override
        void draw(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            super.draw(g2d);


        }
    }

}
