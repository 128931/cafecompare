package me.nov.cafecompare.decompiler;

import me.nov.cafecompare.io.JarIO;
import org.jetbrains.java.decompiler.main.Fernflower;
import org.jetbrains.java.decompiler.main.decompiler.PrintStreamLogger;
import org.jetbrains.java.decompiler.main.extern.IBytecodeProvider;
import org.jetbrains.java.decompiler.main.extern.IResultSaver;

import java.io.*;
import java.util.HashMap;
import java.util.jar.Manifest;

public class FernflowerBridge implements IDecompilerBridge, IBytecodeProvider, IResultSaver {

    public static final HashMap<String, Object> options = new HashMap<>();

    static {
        options.put("rsy", "0");
        options.put("rbr", "0");
        options.put("din", "0");
        options.put("dc4", "1");
        options.put("das", "1");
        options.put("hes", "1");
        options.put("hdc", "1");
        options.put("dgs", "0");
        options.put("ner", "1");
        options.put("den", "1");
        options.put("rgn", "1");
        options.put("lit", "0");
        options.put("asc", "0");
        options.put("bto", "1");
        options.put("nns", "0");
        options.put("uto", "1");
        options.put("udv", "1");
        options.put("rer", "1");
        options.put("fdi", "1");
        options.put("ren", "0");
        options.put("inn", "1");
        options.put("lac", "0");
        options.put("mpm", "5");
    }

    private byte[] bytes;
    private String result;

    @Override
    public void setAggressive(boolean aggressive) {
        options.put("das", aggressive ? "0" : "1");
        options.put("fdi", aggressive ? "0" : "1");
        options.put("rer", aggressive ? "0" : "1");
    }

    public String decompile(String name, byte[] bytez) {
        ByteArrayOutputStream log = new ByteArrayOutputStream();
        try {
            this.result = null;
            this.bytes = bytez;
            Fernflower f = new Fernflower(this, this, options, new PrintStreamLogger(new PrintStream(log)));
            File temp = JarIO.writeTempJar(name, bytez);
            f.addSource(temp);
            f.decompileContext();
        } catch (Exception e) {
            e.printStackTrace();
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            return pw.toString();
        }
        if (result == null || result.trim().isEmpty()) {
            result = "No Fernflower output received\n\nOutput log:\n" + log;
        }
        return result;
    }

    @Override
    public byte[] getBytecode(String externalPath, String internalPath) {
        return this.bytes;
    }

    @Override
    public void saveFolder(String path) {
    }

    @Override
    public void copyFile(String source, String path, String entryName) {
    }

    @Override
    public void saveClassFile(String path, String qualifiedName, String entryName, String content, int[] mapping) {
        this.result = content;
    }

    @Override
    public void createArchive(String path, String archiveName, Manifest manifest) {
    }

    @Override
    public void saveDirEntry(String path, String archiveName, String entryName) {
    }

    @Override
    public void copyEntry(String source, String path, String archiveName, String entry) {
    }

    @Override
    public void saveClassEntry(String path, String archiveName, String qualifiedName, String entryName, String content) {
        this.result = content;
    }

    @Override
    public void closeArchive(String path, String archiveName) {
    }

}
