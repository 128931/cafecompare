package me.nov.cafecompare.swing.textarea;

import org.apache.commons.io.IOUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.nio.charset.Charset;

public class WelcomePane extends JEditorPane {
    private static final long serialVersionUID = 1L;

    public WelcomePane() {
        this.setContentType("text/html");
        this.setEditable(false);
        try {
            this.setText(IOUtils.toString(WelcomePane.class.getResourceAsStream("/res/welcome.html"), Charset.defaultCharset()));
        } catch (Exception e) {
            e.printStackTrace();
            this.setText(":(");
        }
        this.setFocusable(false);
        this.setOpaque(true);
        putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, true);
        setBorder(new EmptyBorder(16, 16, 16, 16));
    }
}
