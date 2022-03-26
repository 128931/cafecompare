package me.nov.cafecompare.swing.listener;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ExitListener extends WindowAdapter {
    private final JFrame frame;

    public ExitListener(JFrame frame) {
        this.frame = frame;
    }

    @Override
    public void windowClosing(WindowEvent we) {
        if (JOptionPane.showConfirmDialog(frame, "Do you really want to exit?", "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            Runtime.getRuntime().exit(0);
        }
    }
}
