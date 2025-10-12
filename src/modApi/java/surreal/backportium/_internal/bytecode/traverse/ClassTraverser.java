package surreal.backportium._internal.bytecode.traverse;

import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;

public class ClassTraverser {

    private static ClassTraverser traverser;

    public static ClassTraverser get() {
        if (traverser == null) traverser = new ClassTraverser();
        return traverser;
    }

    public static void clear() {
        traverser = null;
    }

    private final Map<String, String> superClasses = new HashMap<>(); // class_name -> super_name
    private final Map<String, String[]> interfaces = new HashMap<>(); // class_name -> interfaces

    public boolean alreadyLoaded(String className) {
        return this.superClasses.containsKey(className);
    }

    public boolean doesImplement(String className, String... interfaces) throws IOException {
        byte[] bytes = ClassBytes.getClass(className);
        if (bytes.length == 0) return false;
        return this.doesImplement(bytes, ClassBytes.getConstantJumpTable(bytes), interfaces);
    }

    public void traverse(byte[] bytes, final int[] constantJumpTable, Consumer<String> consumer) {
        try { this.readClass(bytes, constantJumpTable); }
        catch (IOException e) { throw new RuntimeException(e); }
        String className = ClassBytes.getClassName(bytes, constantJumpTable);
        while (true) {
            className = this.superClasses.get(className);
            if (className == null || className.equals("java/lang/Object")) return;
            consumer.accept(className);
        }
    }

    public boolean doesImplement(byte[] bytes, final int[] constantJumpTable, String... interfaces) {
        try { this.readClass(bytes, constantJumpTable); }
        catch (IOException e) { throw new RuntimeException(e); }
        Set<String> set = new HashSet<>();
        Collections.addAll(set, interfaces);
        String className = ClassBytes.getClassName(bytes, constantJumpTable);
        while (true) {
            String[] classInterfaces = this.interfaces.get(className);
            if (classInterfaces != null) {
                for (String str : classInterfaces) {
                    set.remove(str);
                }
                if (set.isEmpty()) return true;
            }
            className = this.superClasses.get(className);
            if (className == null || className.equals("java/lang/Object")) return false;
        }
    }

    public boolean isSuper(String className, String parentClass) throws IOException {
        byte[] bytes = ClassBytes.getClass(className);
        if (bytes.length == 0) return false;
        return this.isSuper(bytes, ClassBytes.getConstantJumpTable(bytes), parentClass);
    }

    public boolean isSuper(byte[] bytes, final int[] constantJumpTable, String superClass) {
        try { this.readClass(bytes, constantJumpTable); }
        catch (IOException e) { throw new RuntimeException(e); }
        String className = ClassBytes.getClassName(bytes, constantJumpTable);
        while (true) {
            String superName = this.superClasses.get(className);
            if (superName == null || superName.equals("java/lang/Object")) return false;
            if (superName.equals(superClass)) return true;
            className = superName;
        }
    }

    private void readClass(byte[] bytes, final int[] constantJumpTable) throws IOException {
        if (bytes.length == 0) return;
        String className = ClassBytes.getClassName(bytes, constantJumpTable);
        if (className.equals("java/lang/Object")) return;
        boolean iface = ClassBytes.isInterface(bytes, constantJumpTable);
        if (!this.superClasses.containsKey(className)) {
            String superName = ClassBytes.getSuperName(bytes, constantJumpTable);
            if (iface) this.interfaces.put(className, new String[] { superName });
            else this.superClasses.put(className, superName);
            byte[] superBytes = ClassBytes.getClass(superName);
            if (superBytes.length == 0) return;
            final int[] superConstJumpTable = ClassBytes.getConstantJumpTable(superBytes);
            this.readClass(superBytes, superConstJumpTable);
        }
        if (!iface && !this.interfaces.containsKey(className)) {
            String[] interfaces = ClassBytes.getInterfaces(bytes, constantJumpTable);
            if (interfaces.length != 0) this.interfaces.put(className, interfaces);
        }
    }
}
