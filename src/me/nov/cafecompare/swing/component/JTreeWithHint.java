package me.nov.cafecompare.swing.component;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;

public class JTreeWithHint extends JTree {
    private static final long serialVersionUID = 1L;
    protected final String hint;

    public JTreeWithHint(String hint) {
        this.hint = hint;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        DefaultMutableTreeNode tn = (DefaultMutableTreeNode) getModel().getRoot();
        ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if (tn.getChildCount() == 0) {
            g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
            g.drawString(hint, getWidth() / 2 - g.getFontMetrics().stringWidth(hint) / 2, getHeight() / 2);
        }
    }
}
