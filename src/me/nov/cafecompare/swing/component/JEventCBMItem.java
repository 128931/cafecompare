package me.nov.cafecompare.swing.component;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.function.Consumer;

public class JEventCBMItem extends JCheckBoxMenuItem implements ActionListener {
    private static final long serialVersionUID = 1L;
    private final Consumer<Boolean> event;

    public JEventCBMItem(String option, Consumer<Boolean> event) {
        super(option);
        this.event = event;
        this.addActionListener(this);

        // to make sure gui settings equal to program settings
        this.setSelected(false);
        this.actionPerformed(null);
    }

    public JEventCBMItem(String option, Consumer<Boolean> event, boolean b) {
        this(option, event);
        this.setSelected(b);
        this.actionPerformed(null);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        event.accept(isSelected());
    }
}
