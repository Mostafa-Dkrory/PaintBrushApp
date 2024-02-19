package org.example;

import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import static org.example.PaintBrushFrame.drawingPanel;

/**
 *
 * @author DKRORY
 */
public final class ControlPanel extends JPanel {

    private final JCheckBox dottedCheckbox;
    private final JCheckBox filledCheckbox;
    private final Clip youSavedMe;
    private final Clip saveMe;
    private final Clip tapHere;
    private final Clip mouseClick;

    ControlPanel() {
        youSavedMe = loadSound("you-saved-me.wav");
        saveMe = loadSound("Save mee.wav");
        tapHere = loadSound("john-cena.wav");
        mouseClick = loadSound("mouse-click.wav");



        // Create buttons for various actions
        dottedCheckbox = new JCheckBox("Dotted");
        filledCheckbox = new JCheckBox("Filled");
        dottedCheckbox.addItemListener(new DottedCheckboxListener());
        filledCheckbox.addItemListener(new FilledCheckboxListener());
        Font checkboxFont = new Font("Arial", Font.BOLD, 24);
        dottedCheckbox.setFont(checkboxFont);
        filledCheckbox.setFont(checkboxFont);
        dottedCheckbox.setBackground(Color.WHITE);
        filledCheckbox.setBackground(Color.WHITE);

        this.add(createIconButton("Panel Background Color", "palette.png", new PaletteButtonListener()));
        this.add(createIconButton("Brush Color", "penPalette.png", new BrushColorButtonListener()));

        this.add(createIconButton("Brush", "pen.png", new FreehandButtonListener()));
        this.add(createIconButton("Line", "line.png", new LineButtonListener()));
        this.add(createIconButton("Rectangle", "rec.png", new RectangleButtonListener()));
        this.add(createIconButton("Oval", "oval.png", new OvalButtonListener()));

        this.add(dottedCheckbox);
        this.add(filledCheckbox);

        this.add(createIconButton("Undo", "ctrlZ.png", new UndoButtonListener()));
        this.add(createIconButton("Eraser", "eraser.png", new EraserButtonListener()));
        this.add(createIconButton("Clear All", "clearAll.png", new ClearAllButtonListener()));

        this.add(createIconButton("Save", "save.png", new SaveButtonListener()));
        this.add(createIconButton("Open", "open.png", new OpenButtonListener()));
        this.add(createIconButton("Tap Here", "tapHere.png", new TapHereButtonListener()));

        this.setBackground(Color.WHITE);
        this.setVisible(true);

    }

    private void playSound(Clip clip) {
        // Handle the case when the sound couldn't be loaded
        if (clip != null) {
            clip.setMicrosecondPosition(0);
            clip.setFramePosition(0);
            clip.start(); // Play the sound
        } else {
            System.out.println("Error loading sound file.");
        }
    }

    private Clip loadSound(String soundPath) {
        Clip clip = null;
        try {
            File soundFile = new File("src/main/java/org/example/sounds/" + soundPath);
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(soundFile);
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            // Log the exception or handle it as needed
            Logger.getLogger(DrawingPanel.class.getName()).severe("Error saving image: " + e.getMessage());
            JOptionPane.showMessageDialog(ControlPanel.this, "Error saving image: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return clip;
    }

    public JButton createIconButton(String toolTip, String iconFileName, ActionListener listener) {
        ImageIcon icon = new ImageIcon("src/main/java/org/example/icons/" + iconFileName);
        JButton button = new JButton(icon);
        button.addActionListener(listener);
        // Set a specific size for the button
        button.setPreferredSize(new Dimension(50, 50));
        // Ensure that the image fits within the button
        Image scaledImage = icon.getImage().getScaledInstance(45, 45, Image.SCALE_SMOOTH);
        button.setIcon(new ImageIcon(scaledImage));
        button.setToolTipText(toolTip);
        button.setBackground(Color.WHITE);
        button.setForeground(Color.WHITE);
        button.setFocusable(false);
        button.setBorderPainted(false);
        return button;
    }

    // ActionListener for Panel Background Color button
    private class PaletteButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            playSound(mouseClick);
            // Show a color dialog and get the selected color
            Color color = JColorChooser.showDialog(new JFrame(), "Select a color", drawingPanel.getBackground());
            // If the user did not cancel the dialog
            if (color != null) {
                // Set the panel background color to the selected color
                drawingPanel.setBackground(color);
            }
        }
    }

    // ActionListener for Pen Color button
    private class BrushColorButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            playSound(mouseClick);
            // Show a color dialog and get the selected color
            Color color = JColorChooser.showDialog(new JFrame(), "Select a color", drawingPanel.getBackground());
            // If the user did not cancel the dialog
            if (color != null) {
                // Set the Brush Color to the selected color
                drawingPanel.setCurrentColor(color);
            }
        }
    }

    // ActionListener for Line button
    private class LineButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            playSound(mouseClick);
            drawingPanel.setCurrentShape(ShapeType.LINE);
        }
    }

    // ActionListener for Rectangle button
    private class RectangleButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            playSound(mouseClick);
            drawingPanel.setCurrentShape(ShapeType.RECTANGLE);
        }
    }

    // ActionListener for Oval button
    private class OvalButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            playSound(mouseClick);
            drawingPanel.setCurrentShape(ShapeType.OVAL);
        }
    }

    // ActionListener for Freehand button
    private class FreehandButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            playSound(mouseClick);
            drawingPanel.setCurrentShape(ShapeType.FREEHAND);
        }
    }
    // ActionListener for Eraser button
    private class EraserButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            playSound(mouseClick);
            drawingPanel.setCurrentShape(ShapeType.ERASE); // Set the shape to Eraser
            //drawingPanel.setCurrentColor(Color.WHITE); // Set the color to white (eraser color)
        }
    }

    // ActionListener for Clear All button
    private class ClearAllButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            playSound(saveMe);
            drawingPanel.clearAll();
        }
    }

    // ActionListener for Undo button
    private class UndoButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            playSound(mouseClick);
            if (drawingPanel != null) {
                drawingPanel.undo();
            }
        }
    }

    // ActionListener for Tap Here button
    private class TapHereButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            playSound(tapHere);
        }
    }

    // ActionListener for Save button
    private class SaveButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

            // Use JFileChooser to choose the file to save
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showSaveDialog(ControlPanel.this);

            // If a file is selected, attempt to save the drawing
            if (result == JFileChooser.APPROVE_OPTION) {
                File fileToSave = fileChooser.getSelectedFile();

                try {
                    // Create a BufferedImage and save the drawing to the selected file
                    BufferedImage image = new BufferedImage(drawingPanel.getWidth(), drawingPanel.getHeight(), BufferedImage.TYPE_INT_ARGB);
                    Graphics2D g2d = image.createGraphics();
                    drawingPanel.paint(g2d);
                    g2d.dispose();

                    // Attempt to write the image to the file
                    if (ImageIO.write(image, "png", fileToSave)) {
                        JOptionPane.showMessageDialog(ControlPanel.this, "Drawing saved successfully!");
                        playSound(youSavedMe);
                    } else {
                        throw new IOException("Error saving image: ImageIO.write returned false");
                    }
                } catch (IOException ex) {
                    Logger.getLogger(DrawingPanel.class.getName()).severe("Error saving image: " + ex.getMessage());
                    JOptionPane.showMessageDialog(ControlPanel.this, "Error saving image: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    // Listener for Dotted checkbox
    private class DottedCheckboxListener implements ItemListener {
        @Override
        public void itemStateChanged(ItemEvent e) {
            playSound(mouseClick);
            boolean dotted = (e.getStateChange() == ItemEvent.SELECTED);
            if (dotted) {
                filledCheckbox.setSelected(false);
            }
            drawingPanel.setDotted(dotted);
        }
    }

    // Listener for Filled checkbox
    private class FilledCheckboxListener implements ItemListener {

        @Override
        public void itemStateChanged(ItemEvent e) {
            playSound(mouseClick);
            boolean filled = (e.getStateChange() == ItemEvent.SELECTED);
            if (filled) {
                dottedCheckbox.setSelected(false);
            }
            drawingPanel.setFilled(filled);
        }
    }

    // ActionListener for Open button
    private class OpenButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            playSound(mouseClick);
            // Use JFileChooser to choose the file to open
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showOpenDialog(ControlPanel.this);

            // If a file is selected, attempt to open and load the drawing
            if (result == JFileChooser.APPROVE_OPTION) {
                File fileToOpen = fileChooser.getSelectedFile();

                try {
                    // Read the image from the selected file and load it into the drawing panel
                    BufferedImage img = ImageIO.read(fileToOpen);
                    if (img != null) {
                        drawingPanel.loadImage(img);
                        JOptionPane.showMessageDialog(ControlPanel.this, "Image loaded successfully!");
                    } else {
                        throw new IOException("Error loading image: ImageIO.read returned null");
                    }
                } catch (IOException ex) {
                    Logger.getLogger(DrawingPanel.class.getName()).severe("Error loading image: " + ex.getMessage());
                    JOptionPane.showMessageDialog(ControlPanel.this, "Error loading image: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
}
