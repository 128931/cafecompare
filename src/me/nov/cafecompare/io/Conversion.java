package me.nov.cafecompare.io;

import me.nov.cafecompare.asm.EasyTextifer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.util.TraceClassVisitor;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;

public class Conversion {
    public static byte[] toBytecode(ClassNode cn, boolean useMaxs) {
        try {
            ClassWriter cw = new ClassWriter(useMaxs ? ClassWriter.COMPUTE_MAXS : ClassWriter.COMPUTE_FRAMES);
            cn.accept(cw);
            return cw.toByteArray();
        } catch (Exception e) {
            return toBytecode0(cn);
        }
    }

    public static byte[] toBytecode0(ClassNode cn) {
        ClassWriter cw = new ClassWriter(0);
        cn.accept(cw);
        return cw.toByteArray();
    }

    public static ClassNode toNode(final byte[] bytez) {
        ClassReader cr = new ClassReader(bytez);
        ClassNode cn = new ClassNode();
        try {
            cr.accept(cn, ClassReader.EXPAND_FRAMES);
        } catch (Exception e) {
            try {
                cr.accept(cn, ClassReader.SKIP_FRAMES | ClassReader.SKIP_DEBUG);
            } catch (Exception e2) {
                // e2.printStackTrace();
            }
        }
        cr = null;
        return cn;
    }

    public static String textify(ClassNode cn) {
        StringWriter out = new StringWriter();
        cn.accept(new TraceClassVisitor(null, new EasyTextifer(), new PrintWriter(out)));
        return out.toString();
    }

    public static String textify(MethodNode mn) {
        StringWriter out = new StringWriter();
        TraceClassVisitor tcv = new TraceClassVisitor(null, new EasyTextifer(), new PrintWriter(out));
        mn.accept(tcv);
        tcv.visitEnd();
        return out.toString();
    }

    public static void saveDebugFile(ClassNode cn) {
        try {
            Files.write(new File(cn.name.replace('/', '.') + "-debug.class").toPath(), Conversion.toBytecode0(cn));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
