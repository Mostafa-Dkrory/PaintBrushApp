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
    private final ArrayList<Point> freehandPoints = new ArrayList<>();
    private final ArrayList<Point> erasePoints = new ArrayList<>();
    private Color currentColor = Color.BLACK;
    private int currentShape = 0;
    private boolean drawing = false;
    private boolean erasing = false;
    private Point startPoint;
    private Point endPoint;
    private boolean dotted = false;
    private boolean filled = false;

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
                } else if (currentShape == 5) {
                    erasing = false;
                    shapes.add(new Erase(erasePoints, getBackground()));
                    erasePoints.clear();
                    currentColor = Color.BLACK;// Clear the points after adding the Freehand shape
                } else {
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
                if (currentShape == 3) {
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

    public int getCurrentShape() {
        return currentShape;
    }

    // Set the current drawing shape
    public void setCurrentShape(int shape) {
        currentShape = shape;
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
}
