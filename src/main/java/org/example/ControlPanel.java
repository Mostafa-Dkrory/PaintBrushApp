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

public class ControlPanel extends JPanel {
    private final JCheckBox dottedCheckbox;
    private final JCheckBox filledCheckbox;
    private Clip youSavedMe;
    private static Clip saveMe = null;
    private static Clip tapHere = null;
    private static Clip mouseClick = null;

    ControlPanel() {
        youSavedMe = loadSound("src/main/java/org/example/sounds/you-saved-me.wav");
        saveMe = loadSound("src/main/java/org/example/sounds/Save mee.wav");
        tapHere = loadSound("src/main/java/org/example/sounds/john-cena.wav");
        mouseClick = loadSound("src/main/java/org/example/sounds/mouse-click.wav");

        this.setBackground(Color.WHITE);
        // Create buttons for various actions
        this.add(createIconButton("Panel Background Color", "src/main/java/org/example/icons/palette.png", new PaletteButtonListener()));
        this.add(createIconButton("Brush Color", "src/main/java/org/example/icons/penPalette.png", new BrushColorButtonListener()));

        this.add(createIconButton("Line", "src/main/java/org/example/icons/line.png", new LineButtonListener()));
        this.add(createIconButton("Rectangle", "src/main/java/org/example/icons/rec.png", new RectangleButtonListener()));
        this.add(createIconButton("Oval", "src/main/java/org/example/icons/oval.png", new OvalButtonListener()));
        this.add(createIconButton("Brush", "src/main/java/org/example/icons/brush.png", new FreehandButtonListener()));

        dottedCheckbox = new JCheckBox("Dotted");
        filledCheckbox = new JCheckBox("Filled");
        dottedCheckbox.addItemListener(new DottedCheckboxListener());
        filledCheckbox.addItemListener(new FilledCheckboxListener());
        Font checkboxFont = new Font("Arial", Font.BOLD, 24);
        dottedCheckbox.setFont(checkboxFont);
        filledCheckbox.setFont(checkboxFont);
        dottedCheckbox.setBackground(Color.WHITE);
        filledCheckbox.setBackground(Color.WHITE);
        //dottedCheckbox.setPreferredSize(new Dimension(100, 50));
        //filledCheckbox.setPreferredSize(new Dimension(100, 50));


        // Add buttons to the control panel to be organized and take the shape of button not the all screen
        this.add(dottedCheckbox);
        this.add(filledCheckbox);

        this.add(createIconButton("Eraser", "src/main/java/org/example/icons/eraser.png", new EraserButtonListener()));
        this.add(createIconButton("Undo", "src/main/java/org/example/icons/undo.png", new UndoButtonListener()));
        this.add(createIconButton("Clear All", "src/main/java/org/example/icons/clearAll.png", new ClearAllButtonListener()));

        this.add(createIconButton("Save", "src/main/java/org/example/icons/save.png", new SaveButtonListener()));
        this.add(createIconButton("Open", "src/main/java/org/example/icons/open.png", new OpenButtonListener()));
        this.add(createIconButton("Tap Here", "src/main/java/org/example/icons/tapHere.png", new TapHereButtonListener()));

        // Make the main window visible
        setVisible(true);

    }
    private static Clip loadSound(String soundPath) {
        Clip clip = null;
        try {
            File soundFile = new File(soundPath);
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(soundFile);
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            // Log the exception or handle it as needed
            e.printStackTrace();
        }
        return clip;
    }
    private static void playSound(Clip clip) {
        if (clip != null) {
            clip.setMicrosecondPosition(0);
            clip.setFramePosition(0);
            clip.start(); // Play the sound
        } else {
            // Handle the case where the sound couldn't be loaded
            System.out.println("Error loading sound file.");
        }
    }
    public JButton createIconButton(String toolTip, String iconFileName, ActionListener listener) {
        ImageIcon icon = new ImageIcon(iconFileName);
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
    private static class PaletteButtonListener implements ActionListener {

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

    private static class BrushColorButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            playSound(mouseClick);
            // Create a new JColorChooser
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
    private static class LineButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            playSound(mouseClick);
            drawingPanel.setCurrentShape(0);
        }
    }

    // ActionListener for Rectangle button
    private static class RectangleButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            playSound(mouseClick);
            drawingPanel.setCurrentShape(1);
        }
    }

    // ActionListener for Oval button
    private static class OvalButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            playSound(mouseClick);
            drawingPanel.setCurrentShape(2);
        }
    }

    // ActionListener for Freehand button
    private static class FreehandButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            playSound(mouseClick);
            drawingPanel.setCurrentShape(3);
        }
    }

    private static class EraserButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            playSound(mouseClick);
            drawingPanel.setCurrentShape(4); // Set the shape to Eraser
            //drawingPanel.setCurrentColor(Color.WHITE); // Set the color to white (eraser color)

        }
    }

    // ActionListener for Clear All button
    private static class ClearAllButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

            playSound(saveMe);
            drawingPanel.clearAll();

        }
    }

    private static class UndoButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            playSound(mouseClick);
            if (drawingPanel != null) {
                drawingPanel.undo();
            }
        }
    }
    private static class TapHereButtonListener implements ActionListener {

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
