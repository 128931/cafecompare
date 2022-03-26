package me.nov.cafecompare.remapping;

import me.nov.cafecompare.io.Clazz;
import org.objectweb.asm.commons.ClassRemapper;
import org.objectweb.asm.commons.Remapper;
import org.objectweb.asm.tree.ClassNode;

import java.util.List;
import java.util.Map;

public class FullRemapper {
    private final List<Clazz> classes;

    public FullRemapper(List<Clazz> classes) {
        this.classes = classes;
    }

    public void remap(Map<String, String> mappings) {
        for (Clazz original : classes) {
            ClassNode updated = new ClassNode();
            original.node.accept(new ClassRemapper(updated, new Remapper() {
                @Override
                public String map(String internalName) {
                    return mappings.getOrDefault(internalName, internalName);
                }

                @Override
                public String mapMethodName(String owner, String name, String descriptor) {
                    return mappings.getOrDefault(owner + "." + name + descriptor, name);
                }
            }));
            original.node = updated;
        }
    }
}
