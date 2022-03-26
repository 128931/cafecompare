package me.nov.cafecompare.swing.component;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class JEventRBMItem extends JRadioButtonMenuItem implements ActionListener {
    private static final long serialVersionUID = 1L;
    private final Runnable event;

    public JEventRBMItem(String option, ButtonGroup bg, Runnable event) {
        super(option);
        this.event = event;
        this.addActionListener(this);
        bg.add(this);

        // to make sure gui settings equal to program settings
        this.setSelected(false);
        this.actionPerformed(null);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (this.isSelected()) {
            event.run();
        }
    }
}
