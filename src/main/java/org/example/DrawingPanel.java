package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 *
 * @author DKRORY
 */
class DrawingPanel extends JPanel {

    // Various properties for the drawing panel
    private final ArrayList<Shape> shapes = new ArrayList<>();
    private final ArrayList<Point> freehandPoints = new ArrayList<>();
    private final ArrayList<Point> erasedPoints = new ArrayList<>();
    private Color currentColor = Color.BLACK;
    private Color lastColor = Color.BLACK;
    private ShapeType currentShape = ShapeType.LINE;
    private boolean drawing = false;
    private boolean erasing = false;
    private Point startPoint;
    private Point endPoint;
    private boolean isDotted = false;
    private boolean isFilled = false;

    // Constructor for the DrawingPanel
    public DrawingPanel() {
        // Set the background color of the drawing panel
        setBackground(Color.WHITE);

        // Add mouse listeners for handling drawing actions
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                startPoint = e.getPoint();
                lastColor = currentColor;

                switch (currentShape) {
                    case FREEHAND -> {
                        drawing = true;
                        freehandPoints.clear();
                        freehandPoints.add(startPoint);
                    }
                    case ERASE -> {
                        erasing = true;
                        erasedPoints.clear();
                        erasedPoints.add(startPoint);
                    }
                    default -> shapes.add(createShape(startPoint, startPoint, currentColor, isDotted, isFilled));
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                endPoint = e.getPoint();

                switch (currentShape) {
                    case FREEHAND -> {
                        drawing = false;
                        shapes.add(new Freehand(freehandPoints, currentColor, isDotted));
                        freehandPoints.clear();  // Clear the points after adding the Freehand shape
                    }
                    case ERASE -> {
                        erasing = false;
                        shapes.add(new Erase(erasedPoints));
                        erasedPoints.clear();
                        currentColor = lastColor;
                    }
                    default -> shapes.add(createShape(startPoint, endPoint, currentColor, isDotted, isFilled));
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
                switch (currentShape) {
                    case FREEHAND -> {
                        if (drawing) {
                            freehandPoints.add(endPoint);
                            repaint();
                        }
                    }
                    case ERASE -> {
                        if (erasing) {
                            erasedPoints.add(endPoint);
                            repaint();
                        }
                    }
                    default -> {
                        // For other shapes --> add and remove the shape while dragging
                        shapes.removeLast();
                        shapes.add(createShape(startPoint, endPoint, currentColor, isDotted, isFilled));
                        repaint();
                    }


                }

            }
        });
    }


    // Method to create a shape based on current properties
    private Shape createShape(Point start, Point end, Color color, boolean dotted, boolean filled) {
        return switch (currentShape) {
            case LINE -> // Line
                    new Line(start, end, color, dotted);
            case RECTANGLE -> // Rectangle
                    new Rectangle(start, end, color, dotted, filled);
            case OVAL -> //Oval
                    new Oval(start, end, color, dotted, filled);
            case ERASE -> //Eraser
                //new Eraser(start, end, getBackground());
                    new Erase(erasedPoints);
            default -> null;
        };
    }

    // Set the current drawing color
    public void setCurrentColor(Color color) {
        currentColor = color;
    }

    public ShapeType getCurrentShape() {
        return currentShape;
    }

    // Set the current drawing shape
    public void setCurrentShape(ShapeType shape) {
        currentShape = shape;
    }

    // Set the dotted property
    public void setDotted(boolean isDotted) {
        this.isDotted = isDotted;
        repaint();
    }

    // Set the filled property
    public void setFilled(boolean isFilled) {
        this.isFilled = isFilled;
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
        if (drawing && currentShape == ShapeType.FREEHAND) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(currentColor);
            if (isDotted) {
                float[] dashPattern = {3, 3};
                g2d.setStroke(new BasicStroke(5, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 2.0f, dashPattern, 0.0f));
            } else {
                g2d.setStroke(new BasicStroke(5.0f));
            }
            for (int i = 1; i < freehandPoints.size(); i++) {
                Point p1 = freehandPoints.get(i - 1);
                Point p2 = freehandPoints.get(i);
                g2d.drawLine(p1.x, p1.y, p2.x, p2.y);
            }
        }
        if (erasing && currentShape == ShapeType.ERASE) {
            Graphics2D g2d = (Graphics2D) g;

            //g2d.setStroke(new BasicStroke());
            g2d.setStroke(new BasicStroke(20.0f));
            for (int i = 1; i < erasedPoints.size(); i++) {
                Point p1 = erasedPoints.get(i - 1);
                Point p2 = erasedPoints.get(i);
                g2d.setColor(g2d.getBackground());
                g2d.drawLine(p1.x, p1.y, p2.x, p2.y);
            }
        }
    }

    // Load an image onto the drawing panel
    public void loadImage(BufferedImage img) {

        shapes.add(new ImageShape(img, new Point(0, 0)));
        repaint();
    }
}