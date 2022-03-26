package me.nov.cafecompare.decompiler;

import java.io.*;
import java.net.URL;
import java.util.*;

import org.apache.commons.io.IOUtils;
import org.benf.cfr.reader.api.*;
import org.benf.cfr.reader.bytecode.analysis.parse.utils.Pair;
import org.objectweb.asm.tree.ClassNode;

import me.nov.cafecompare.io.Conversion;

public class CFRBridge implements IDecompilerBridge {

  public static final HashMap<String, String> options = new HashMap<>();

  static {
    options.put("showversion", "false");
    options.put("hidelongstrings", "true");
    options.put("hideutf", "false");
    options.put("innerclasses", "false");
    options.put("hidelangimports", "true");
    options.put("dumpclasspath", "false");
    options.put("ignoreexceptionsalways", "true");
  }

  public void setAggressive(boolean topsort) {
    options.put("forcetopsort", String.valueOf(topsort));
    options.put("forcetopsortaggress", String.valueOf(topsort));
  }

  private String result;

  public String decompile(String name, byte[] bytes) {
    try {
      this.result = null;
      OutputSinkFactory mySink = new OutputSinkFactory() {
        @Override
        public List<SinkClass> getSupportedSinks(SinkType sinkType, Collection<SinkClass> collection) {
          if (sinkType == SinkType.JAVA && collection.contains(SinkClass.DECOMPILED)) {
            return Arrays.asList(SinkClass.DECOMPILED, SinkClass.STRING);
          } else {
            return Collections.singletonList(SinkClass.STRING);
          }
        }

        @Override
        public <T> Sink<T> getSink(SinkType sinkType, SinkClass sinkClass) {
          if (sinkType == SinkType.JAVA && sinkClass == SinkClass.DECOMPILED) {
            return x -> result = ((SinkReturns.Decompiled) x).getJava().substring(31);
          }
          return ignore -> {
          };
        }
      };
      ClassFileSource source = new ClassFileSource() {
        @Override
        public void informAnalysisRelativePathDetail(String a, String b) {
        }

        @Override
        public String getPossiblyRenamedPath(String path) {
          return path;
        }

        @Override
        public Pair<byte[], String> getClassFileContent(String path) throws IOException {
          String clzName = path.substring(0, path.length() - 6);
          if (clzName.equals(name)) {
            return Pair.make(bytes, clzName);
          }
          URL url = CFRBridge.class.getResource("/" + path);
          if (url != null) {
            return Pair.make(IOUtils.toByteArray(url), path);
          }
          // cfr loads unnecessary classes. normally you should throw a FNF exception here, but this way, no long comment at the top of the code is created
          ClassNode dummy = new ClassNode();
          dummy.name = clzName;
          dummy.version = 52;
          return Pair.make(Conversion.toBytecode0(dummy), clzName);
        }

        @Override
        public Collection<String> addJar(String arg0) {
          throw new RuntimeException();
        }
      };
      CfrDriver cfrDriver = new CfrDriver.Builder().withClassFileSource(source).withOutputSink(mySink).withOptions(options).build();
      cfrDriver.analyse(Collections.singletonList(name));
    } catch (Exception e) {
      e.printStackTrace();
      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw);
      e.printStackTrace(pw);
      return sw.toString();
    }
    if (result == null || result.trim().isEmpty()) {
      result = "No CFR output received";
    }
    return result;
  }
}
