package me.nov.cafecompare.swing.dialog;

import com.github.weisj.darklaf.components.loading.LoadingIndicator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.function.Consumer;

public class ProcessingDialog extends JDialog {
    private static final long serialVersionUID = 1L;
    private final LoadingIndicator li;
    private final Consumer<ProcessingDialog> consumer;
    private JProgressBar pb;
    private Runnable then;
    private Thread action;

    public ProcessingDialog(Component parent, boolean progressBar, Consumer<ProcessingDialog> consumer) {
        this.consumer = consumer;
        this.setLocationRelativeTo(parent);
        this.setLayout(new BorderLayout());
        JPanel cp = new JPanel(new BorderLayout(16, 16));
        cp.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        this.setTitle("Please be patient");
        li = new LoadingIndicator("Processing...");
        li.setRunning(true);
        JPanel center = new JPanel(new FlowLayout(FlowLayout.CENTER));
        center.add(li);
        cp.add(center, BorderLayout.CENTER);
        if (progressBar) {
            cp.add(pb = new JProgressBar(), BorderLayout.SOUTH);
            pb.setStringPainted(false);
        }
        this.add(cp, BorderLayout.CENTER);
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        this.setAlwaysOnTop(true);
        this.setMinimumSize(new Dimension(600, 100));
        this.pack();
        this.addWindowListener(new WindowAdapter() {
            @SuppressWarnings("deprecation")
            @Override
            public void windowClosing(WindowEvent e) {
                if (action.isAlive())
                    action.stop();
                dispose();
            }
        });
    }

    public void publish(float pc) {
        if (pc > pb.getValue())
            pb.setValue((int) pc);
    }

    public void setText(String text) {
        li.setText(text);
    }

    public ProcessingDialog go() {
        SwingUtilities.invokeLater(() -> {
            action = new Thread(() -> {
                this.setVisible(true);
                consumer.accept(ProcessingDialog.this);
                this.dispose();
                this.setVisible(false);
                Toolkit.getDefaultToolkit().beep();
                if (then != null)
                    then.run();
            });
            action.start();
        });
        return this;
    }

    public void then(Runnable then) {
        this.then = then;
    }
}
