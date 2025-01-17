package me.nov.cafecompare.swing;

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.image.BufferedImage;
import java.util.Objects;

public class Utils {
    public static JPanel addTitleAndBorder(String title, Component c) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(title));
        JPanel panel2 = new JPanel(new BorderLayout());
        panel2.add(c, BorderLayout.CENTER);
        panel2.setBorder(BorderFactory.createLoweredBevelBorder());
        panel.add(panel2, BorderLayout.CENTER);
        return panel;
    }

    public static JPanel addBorder(Component c) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(c, BorderLayout.CENTER);
        panel.setBorder(BorderFactory.createLoweredBevelBorder());
        return panel;
    }

    public static void moveTreeItem(JTree tree, int direction) {
        DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
        MutableTreeNode moveNode = (MutableTreeNode) tree.getLastSelectedPathComponent();
        if (moveNode == null)
            return;
        MutableTreeNode parent = (MutableTreeNode) moveNode.getParent();
        if (parent == null)
            return;
        int targetIndex = model.getIndexOfChild(parent, moveNode) + direction;
        if (targetIndex < 0 || targetIndex >= parent.getChildCount())
            return;
        model.removeNodeFromParent(moveNode);
        model.insertNodeInto(moveNode, parent, targetIndex);
        // make the node visible by scroll to it
        TreeNode[] nodes = model.getPathToRoot(moveNode);
        TreePath path = new TreePath(nodes);
        tree.scrollPathToVisible(path);
        // select the newly added node
        tree.setSelectionPath(path);
    }

    public static String getVersion() {
        try {
            return Objects.requireNonNull(Utils.class.getPackage().getImplementationVersion());
        } catch (NullPointerException e) {
            return "(dev)";
        }
    }

    public static Image iconToImage(Icon icon) {
        if (icon instanceof ImageIcon) {
            return ((ImageIcon) icon).getImage();
        } else {
            BufferedImage image = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
            icon.paintIcon(null, image.getGraphics(), 0, 0);
            return image;
        }
    }

    public static JSplitPane setDividerLocation(JSplitPane splitter, final double proportion) {
        if (splitter.isShowing()) {
            if ((splitter.getWidth() > 0) && (splitter.getHeight() > 0)) {
                splitter.setDividerLocation(proportion);
            } else {
                splitter.addComponentListener(new ComponentAdapter() {
                    @Override
                    public void componentResized(ComponentEvent ce) {
                        splitter.removeComponentListener(this);
                        setDividerLocation(splitter, proportion);
                    }
                });
            }
        } else {
            splitter.addHierarchyListener(new HierarchyListener() {
                @Override
                public void hierarchyChanged(HierarchyEvent e) {
                    if (((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0) && splitter.isShowing()) {
                        splitter.removeHierarchyListener(this);
                        setDividerLocation(splitter, proportion);
                    }
                }
            });
        }
        return splitter;
    }
}
