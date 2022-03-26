package me.nov.cafecompare.swing.panel.tree.renderer;

import javax.swing.*;
import java.awt.*;

public class OverlayIcon implements Icon {

    private final Icon icon;
    private final Icon overlay;

    public OverlayIcon(final Icon icon, final Icon overlay) {
        this.icon = icon;
        this.overlay = overlay;
    }

    @Override
    public void paintIcon(final Component c, final Graphics g, final int x, final int y) {
        icon.paintIcon(c, g, x, y);
        overlay.paintIcon(c, g, x + (icon.getIconWidth() - overlay.getIconWidth()) + 2, y + (icon.getIconHeight() - overlay.getIconHeight()) + 2);
    }

    @Override
    public int getIconWidth() {
        return icon.getIconWidth();
    }

    @Override
    public int getIconHeight() {
        return icon.getIconHeight();
    }
}