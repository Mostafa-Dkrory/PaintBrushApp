package org.example;

import javax.swing.*;
import java.awt.*;
/**
 *
 * @author DKRORY
 */

public class PaintBrushFrame extends JFrame {
    protected static DrawingPanel drawingPanel;

    PaintBrushFrame() {
        // Set the title and size of the main window
        this.setTitle("Paint Brush App");
        this.setSize(1300, 750);
        this.setLocationRelativeTo(null);  //centers the frame on the screen.
        this.setExtendedState(JFrame.MAXIMIZED_BOTH); //set the frame to maximize both vertically and horizontally.
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Create a Control Panel and add it to our Frame.
        ControlPanel controlPanel = new ControlPanel();
        controlPanel.setSize(1300, 50);
        this.add(controlPanel, BorderLayout.PAGE_START);
        // Create a Drawing Panel and add it to our Frame.
        drawingPanel = new DrawingPanel();
        drawingPanel.setSize(1300, 700);
        drawingPanel.setBackground(Color.white);
        this.add(drawingPanel, BorderLayout.CENTER);
        this.setVisible(true);


    }
}
