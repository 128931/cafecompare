package me.nov.cafecompare.io;

import org.objectweb.asm.tree.ClassNode;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class Clazz {
    public final JarEntry oldEntry;
    public final Object inputFile;
    public ClassNode node;

    public Clazz(ClassNode node, JarEntry oldEntry, Object inputFile) {
        super();
        this.node = node;
        this.oldEntry = oldEntry;
        this.inputFile = inputFile;
    }

    public InputStream streamOriginal() throws IOException {
        if (inputFile instanceof JarFile) {
            JarFile jf = new JarFile(((JarFile) inputFile).getName());
            return jf.getInputStream(jf.getEntry(oldEntry.getName()));
        }
        return new FileInputStream((File) inputFile);
    }
}
