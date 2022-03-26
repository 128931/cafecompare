package me.nov.cafecompare.swing.textarea;

import com.github.weisj.darklaf.LafManager;
import me.nov.cafecompare.io.Clazz;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.Theme;

import java.awt.*;
import java.io.IOException;

public class DecompilerTextArea extends RSyntaxTextArea {
    private static final long serialVersionUID = 1L;
    public Clazz last;

    public DecompilerTextArea() {
        this.setSyntaxEditingStyle("text/java");
        this.setCodeFoldingEnabled(true);
        this.setAntiAliasingEnabled(true);
        this.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        this.setEditable(false);
        String themeName = com.github.weisj.darklaf.theme.Theme.isDark(LafManager.getTheme()) ? "/res/rsta-theme.xml" : "/org/fife/ui/rsyntaxtextarea/themes/eclipse.xml";
        try {
            Theme theme = Theme.load(getClass().getResourceAsStream(themeName));
            theme.apply(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
