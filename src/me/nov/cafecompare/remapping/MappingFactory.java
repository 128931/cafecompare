package me.nov.cafecompare.remapping;

import me.nov.cafecompare.asm.Access;
import me.nov.cafecompare.diff.DiffMath;
import me.nov.cafecompare.io.Clazz;
import me.nov.cafecompare.io.Conversion;
import me.nov.cafecompare.swing.dialog.ProcessingDialog;
import org.objectweb.asm.tree.MethodNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MappingFactory {
    public static final float CLASS_INTERRUPT_CONF = 90;
    public static final float MIN_CLASS_CONF = 25;
    public static final float METH_INTERRUPT_CONF = 95;
    public static final float MIN_METH_CONF = 65;
    public static final float MAX_CONFIDENCE_DEVIATION = 30;
    private final Map<String, String> mappings = new HashMap<>();
    private final Map<String, SuspectedItem<String>> uncalculatedMappings = new HashMap<>();
    private int finished = 0;

    private void putUncalculated(String name, String mapName, float bestConfidence) {
        if (uncalculatedMappings.containsKey(name)) {
            SuspectedItem<String> presentValue = uncalculatedMappings.get(name);
            if (bestConfidence > presentValue.percent) {
                // better match found, update
                presentValue.item = mapName;
                presentValue.percent = bestConfidence;
            }
        } else {
            uncalculatedMappings.put(name, new SuspectedItem<>(mapName, bestConfidence));
        }
    }

    public Map<String, String> get() {
        return mappings;
    }

    public MappingFactory with(String oldName, String newName) {
        mappings.put(oldName, newName);
        return this;
    }

    public MappingFactory remapMethods(Clazz source, Clazz target, ProcessingDialog p) {
        p.setText("Calculating code...");
        HashMap<MethodNode, String> bytecode = new HashMap<>();
        for (MethodNode mn : source.node.methods) {
            bytecode.put(mn, Conversion.textify(mn));
        }
        for (MethodNode mn : target.node.methods) {
            bytecode.put(mn, Conversion.textify(mn));
        }
        p.setText("Comparing methods...");
        uncalculatedMappings.clear();
        List<MethodNode> methods = target.node.methods;
        float size = methods.size();
        for (int i = 0; i < size; i++) {
            MethodNode method = methods.get(i);
            if (method.instructions.size() < 4)
                continue;
            String targetCode = bytecode.get(method);
            MethodNode bestMatch = null;
            float bestConfidence = 0;
            for (MethodNode equivalent : source.node.methods) {
                float confidence = DiffMath.confidencePercent(targetCode, bytecode.get(equivalent));
                if (confidence > bestConfidence) {
                    bestConfidence = confidence;
                    bestMatch = equivalent;
                }
                if (confidence > METH_INTERRUPT_CONF)
                    break;
            }
            p.publish(i / size * 100);
            if (bestConfidence > MIN_METH_CONF) {
                String mapName = target.node.name + "." + method.name + method.desc;
                String name = bestMatch.name;
                putUncalculated(name, mapName, bestConfidence);
            }
        }
        p.setText("Final steps...");
        if (!uncalculatedMappings.isEmpty()) {
            double avgMatch = uncalculatedMappings.values().stream().mapToDouble(s -> s.percent).average().getAsDouble();
            uncalculatedMappings.entrySet().stream().filter(e -> avgMatch - e.getValue().percent < MAX_CONFIDENCE_DEVIATION).forEach(e -> mappings.put(e.getValue().item, e.getKey()));
        }
        return this;
    }

    public MappingFactory remap(List<Clazz> source, List<Clazz> target, ProcessingDialog p) {
        p.setText("Calculating code...");
        Map<Clazz, String> bytecode = new HashMap<>();
        for (Clazz cz : target) {
            bytecode.put(cz, Conversion.textify(cz.node));
        }
        for (Clazz cz : source) {
            bytecode.put(cz, Conversion.textify(cz.node));
        }
        p.setText("Comparing classes... (Estimated remaining time: ?)");

        float size = target.size();

        int index = 0;
        finished = 0;
        uncalculatedMappings.clear();

        int threads = Runtime.getRuntime().availableProcessors();
        ExecutorService service = Executors.newFixedThreadPool(threads);
        new Thread(() -> {
            try {
                Thread.sleep(500); // wait for the threads to initialize
                float checks = 0;
                int totalChange = 0;

                while (finished < size) {
                    int lastFinished = finished;
                    Thread.sleep(500);
                    int change = (finished - lastFinished);
                    checks++;
                    totalChange += change;

                    float avgClassesPerSecond = (totalChange / checks) * 2;
                    float remaining = size - finished;

                    if (avgClassesPerSecond > 0 && finished < size) {
                        int seconds = (int) (remaining / (avgClassesPerSecond));
                        p.setText(String.format("Comparing classes... (Estimated remaining time: %d:%02d:%02d)", seconds / 3600, (seconds % 3600) / 60, seconds % 60));
                    }
                }
            } catch (InterruptedException ignored) {
            }
        }, "time-calculator").start();
        while (index < size) {
            Clazz clazz = target.get(index);
            service.execute(() -> {
                findCommon(new ArrayList<>(source), bytecode, clazz);
                finished++;
                p.publish(finished / size * 100);
            });
            // slot is free, add thread and go
            index++;
        }
        service.shutdown();
        try {
            service.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException ignored) {
        }
        p.setText("Final steps...");
        if (!uncalculatedMappings.isEmpty()) {
            double avgMatch = uncalculatedMappings.values().stream().mapToDouble(s -> s.percent).average().getAsDouble();
            uncalculatedMappings.entrySet().stream().filter(e -> avgMatch - e.getValue().percent < MAX_CONFIDENCE_DEVIATION).forEach(e -> mappings.put(e.getValue().item, e.getKey()));
        }
        return this;
    }

    private void findCommon(List<Clazz> source, Map<Clazz, String> bytecode, Clazz original) {
        String targetCode = bytecode.get(original);
        Clazz bestMatch = null;
        float bestConfidence = 0;
        boolean abstr = Access.isAbstract(original.node.access);
        boolean itf = Access.isInterface(original.node.access);
        int methods = original.node.methods.size();
        int fields = original.node.fields.size();

        source.sort((a, b) -> {
            int adif = (Math.abs(a.node.methods.size() - methods) + 1) * (Math.abs(a.node.fields.size() - fields) + 1);
            int bdif = (Math.abs(b.node.methods.size() - methods) + 1) * (Math.abs(b.node.fields.size() - fields) + 1);
            return Integer.compare(adif, bdif);
        });
        for (Clazz cz : source) {
            if (abstr != Access.isAbstract(cz.node.access))
                continue;
            if (itf != Access.isInterface(cz.node.access))
                continue;

            float confidence = DiffMath.confidencePercent(targetCode, bytecode.get(cz));
            if (confidence > bestConfidence) {
                bestConfidence = confidence;
                bestMatch = cz;
            }
            if (confidence > CLASS_INTERRUPT_CONF)
                break;
        }
        if (bestConfidence > MIN_CLASS_CONF) {
            String name = bestMatch.node.name;
            synchronized (uncalculatedMappings) {
                putUncalculated(name, original.node.name, bestConfidence);
            }
        }
    }
}
