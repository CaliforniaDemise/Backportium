package surreal.backportium.core.transformers;

import it.unimi.dsi.fastutil.ints.IntList;
import org.apache.commons.io.IOUtils;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DebarkingTransformer extends BasicTransformer {

    private static final int
            UTF8 = 1,
            INTEGER = 3,
            FLOAT = 4,
            LONG = 5,
            DOUBLE = 6,
            CLASS = 7,
            STRING = 8,
            FIELD_REF = 9,
            METHOD_REF = 10,
            IFACE_METHOD_REF = 11,
            NAME_AND_TYPE = 12,
            METHOD_HANDLE = 15,
            METHOD_TYPE = 16,
            INVOKE_DYNAMIC = 18;

    public static boolean checkLogs(byte[] cls, String transformedName, String superName) {
        int poolCount = ((cls[9] & 0xFF) | (cls[8] & 0xFF) << 8) - 1;
        int[] constants = new int[poolCount]; // Byte location of constants
        int index = 10;
        for (int i = 0; i < poolCount; i++) {
            constants[i] = index;
            if (cls[index] == DOUBLE || cls[index] == LONG) {
                i++;
                constants[i] = index;
            }
            index = nextConstant(cls, index);
        }
        int accessFlags = ((cls[index + 1] & 0xFF) | (cls[index] & 0xFF) << 8);
        if ((accessFlags & 0x0200) == 0x0200) return false; // interface check
        int classConstant = ((cls[index + 3] & 0xFF) | (cls[index + 2] & 0xFF) << 8);
        classConstant = constants[classConstant - 1];
        classConstant = ((cls[classConstant + 2] & 0xFF) | (cls[classConstant + 1] & 0xFF) << 8);
        String clsName = fromUtf8Const(cls, constants[classConstant - 1]);
        if (clsName.endsWith("$Debarked")) return false;
        int superConstant = ((cls[index + 5] & 0xFF) | (cls[index + 4] & 0xFF) << 8); // superclass' position on constants array
        superConstant = constants[superConstant - 1]; // superclass' position on cls array
        superConstant = ((cls[superConstant + 2] & 0xFF) | (cls[superConstant + 1] & 0xFF) << 8); // superclass' utf8's position
        String str = fromUtf8Const(cls, constants[superConstant - 1]);
        if (str.equals(superName)) return true;
        else {
            if (str.equals("java/lang/Object")) return false;
            try {
                InputStream stream = DebarkingTransformer.class.getClassLoader().getResourceAsStream(str + ".class");
                if (stream == null) {
                    System.err.println("Could not find class named " + str.replace("/", "."));
                    return false;
                }
                byte[] bytes = IOUtils.toByteArray(stream);
                stream.close();
                return checkLogs(bytes, transformedName, superName);
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    // Non-abstract BlockLog extending aberrations
    public static byte[] transformBlockLogEx(byte[] basicClass) {
        ClassNode cls = read(basicClass);
        if ((cls.access & ACC_ABSTRACT) != ACC_ABSTRACT) {
            if ((cls.access & ACC_FINAL) == ACC_FINAL) cls.access ^= ACC_FINAL;
            boolean found = false;
            for (MethodNode method : cls.methods) {
                if (method.name.equals("<init>")) {
                    if ((method.access & ACC_PRIVATE) == ACC_PRIVATE) {
                        method.access ^= ACC_PRIVATE;
                        method.access |= ACC_PUBLIC;
                    }
                    found = true;
                    IntList map = getDescList(method.desc);
                    String debarkedDesc;
                    {
                        StringBuilder builder = new StringBuilder();
                        for (int i = 0; i < method.desc.length(); i++) {
                            char c = method.desc.charAt(i);
                            if (c == ')') {
                                builder.append("Lnet/minecraft/block/Block;");
                            }
                            builder.append(c);
                        }
                        debarkedDesc = builder.toString();
                    }
                    String clsName = createDebarkedClass(cls, method.desc, debarkedDesc, map);
                    cls.visitInnerClass(clsName, cls.name, "Debarked", ACC_PUBLIC | ACC_STATIC);
                    Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                    while (iterator.hasNext()) {
                        AbstractInsnNode node = iterator.next();
                        if (node.getOpcode() == INVOKEVIRTUAL) {
                            MethodInsnNode mInsn = (MethodInsnNode) node;
                            if (mInsn.name.equals("setRegistryName")) {
                                String mDesc = mInsn.desc;
                                StringBuilder builder = new StringBuilder();
                                for (int i = 0; i < mDesc.length(); i++) {
                                    char c = mDesc.charAt(i);
                                    if (c == ')') {
                                        builder.append("Lnet/minecraft/block/Block;");
                                    }
                                    builder.append(c);
                                    if (c == '(') {
                                        if (!mInsn.owner.equals(cls.name)) {
                                            builder.append("Lnet/minecraft/item/Item;");
                                        }
                                    }
                                }
                                InsnList list = new InsnList();
                                list.add(new VarInsnNode(ALOAD, 0));
                                list.add(hook("BlockLog$setRegistryName", builder.toString()));
                                method.instructions.insertBefore(node, list);
                                iterator.remove();
                            }
                        }
                        else if (node.getOpcode() == RETURN) {
                            InsnList list = new InsnList();
                            list.add(new VarInsnNode(ALOAD, 0));
                            list.add(new TypeInsnNode(INSTANCEOF, "surreal/backportium/api/block/DebarkedLog"));
                            LabelNode l_con = new LabelNode();
                            list.add(new JumpInsnNode(IFNE, l_con));
                            list.add(new TypeInsnNode(NEW, clsName));
                            list.add(new InsnNode(DUP));
                            if (!map.isEmpty()) {
                                for (int i = 0; i < map.size(); i++) {
                                    list.add(new VarInsnNode(map.getInt(i), i + 1));
                                }
                            }
                            list.add(new VarInsnNode(ALOAD, 0));
                            list.add(new MethodInsnNode(INVOKESPECIAL, clsName, "<init>", debarkedDesc, false));
                            list.add(new VarInsnNode(ALOAD, 0));
                            list.add(hook("Debarking$registerBlock", "(Lnet/minecraft/block/Block;Lnet/minecraft/block/Block;)V"));
                            list.add(l_con);
                            method.instructions.insertBefore(node, list);
                            break;
                        }
                    }
                }
            }
            if (!found) {
                String clsName = createDebarkedClass(cls, "()V", "(Lnet/minecraft/block/Block;)V", null);
                { // <init>
                    MethodVisitor m = cls.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
                    m.visitVarInsn(ALOAD, 0);
                    m.visitMethodInsn(INVOKESPECIAL, cls.name, "<init>", "()V", false);
                    m.visitVarInsn(ALOAD, 0);
                    m.visitTypeInsn(INSTANCEOF, "surreal/backportium/api/block/DebarkedLog");
                    Label l_con = new Label();
                    m.visitJumpInsn(IFNE, l_con);
                    m.visitTypeInsn(NEW, clsName);
                    m.visitInsn(DUP);
                    m.visitVarInsn(ALOAD, 0);
                    m.visitMethodInsn(INVOKESPECIAL, clsName, "<init>", "(Lnet/minecraft/block/Block;)V", false);
                    m.visitVarInsn(ALOAD, 0);
                    m.visitMethodInsn(INVOKESTATIC, "surreal/backportium/core/BPHooks", "Debarking$registerBlock", "(Lnet/minecraft/block/Block;Lnet/minecraft/block/Block;)V", false);
                    m.visitLabel(l_con);
                    m.visitFrame(F_SAME, 0, null, 0, null);
                    m.visitInsn(RETURN);
                }
            }
        }
        return write(cls, ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES); // COMPUTE_FRAMES???? He fell off.....
    }

    public static byte[] transformItemBlockEx(byte[] basicClass) {
        ClassNode cls = read(basicClass);
        boolean didSomething = false;
        String methodName = getName("getItemStackDisplayName", "func_77653_i");
        for (MethodNode method : cls.methods) {
            if (method.name.equals("<init>")) {
                AbstractInsnNode node = method.instructions.getLast();
                while (node.getOpcode() != RETURN) node = node.getPrevious();
                InsnList list = new InsnList();
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new FieldInsnNode(GETFIELD, cls.name, getName("block", "field_150939_a"), "Lnet/minecraft/block/Block;"));
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(hook("Debarking$registerItem", "(Lnet/minecraft/block/Block;Lnet/minecraft/item/ItemBlock;)V"));
                method.instructions.insertBefore(node, list);
            }
            else if (method.name.equals(methodName)) {
                AbstractInsnNode node = method.instructions.getLast();
                while (node.getOpcode() != ARETURN) node = node.getPrevious();
                InsnList list = new InsnList();
                list.add(new VarInsnNode(ALOAD, 1));
                list.add(hook("Debarking$getItemStackDisplayName", "(Ljava/lang/String;Lnet/minecraft/item/ItemStack;)Ljava/lang/String;"));
                method.instructions.insertBefore(node, list);
                didSomething = true;
                break;
            }
        }
        if (!didSomething) {
            { // <init>
                MethodVisitor m = cls.visitMethod(ACC_PUBLIC, methodName, "(Lnet/minecraft/item/ItemStack;)Ljava/lang/String;", null, null);
                m.visitVarInsn(ALOAD, 0);
                m.visitVarInsn(ALOAD, 1);
                m.visitMethodInsn(INVOKESPECIAL, cls.superName, methodName, "(Lnet/minecraft/item/ItemStack;)Ljava/lang/String;", false);
                m.visitVarInsn(ALOAD, 1);
                m.visitMethodInsn(INVOKESTATIC, "surreal/backportium/core/BPHooks", "Debarking$getItemStackDisplayName", "(Ljava/lang/String;Lnet/minecraft/item/ItemStack;)Ljava/lang/String;", false);
                m.visitInsn(ARETURN);
            }
        }
        return write(cls);
    }

    /**
     * For registering vanilla debarked logs.
     **/
    public static byte[] transformBlock(byte[] basicClass) {
        ClassNode cls = read(basicClass);
        for (int i = cls.methods.size() - 1; i >= 0; i--) {
            MethodNode method = cls.methods.get(i);
            if (method.name.equals(getName("registerBlock", "func_176219_a"))) {
                AbstractInsnNode node = method.instructions.getFirst();
                InsnList list = new InsnList();
                list.add(new FieldInsnNode(GETSTATIC, cls.name, getName("REGISTRY", "field_149771_c"), "Lnet/minecraft/util/registry/RegistryNamespacedDefaultedByKey;"));
                list.add(new VarInsnNode(ALOAD, 1));
                list.add(new VarInsnNode(ALOAD, 2));
                list.add(hook("Debarking$tryRegisteringDebarkedLogVanilla", "(Lnet/minecraft/util/registry/RegistryNamespacedDefaultedByKey;Ljava/lang/String;Lnet/minecraft/block/Block;)V"));
                method.instructions.insertBefore(node, list);
                break;
            }
        }
        return write(cls);
    }

    public static byte[] transformItem(byte[] basicClass) {
        ClassNode cls = read(basicClass);
        for (int i = cls.methods.size() - 1; i >= 0; i--) {
            MethodNode method = cls.methods.get(i);
            if (method.name.equals(getName("registerItemBlock", "func_179214_a"))) {
                AbstractInsnNode node = method.instructions.getFirst();
                InsnList list = new InsnList();
                list.add(new FieldInsnNode(GETSTATIC, cls.name, getName("REGISTRY", "field_150901_e"), "Lnet/minecraft/util/registry/RegistryNamespaced;"));
                list.add(new FieldInsnNode(GETSTATIC, cls.name, getName("BLOCK_TO_ITEM", "field_179220_a"), "Ljava/util/Map;"));
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(hook("Debarking$tryRegisteringDebarkedLogVanilla", "(Lnet/minecraft/util/registry/RegistryNamespaced;Ljava/util/Map;Lnet/minecraft/block/Block;)V"));
                method.instructions.insertBefore(node, list);
                break;
            }
        }
        return write(cls);
    }

    /**
     * Registers the debarked logs that doesn't get registered.
     * For example, Vanilla logs don't register themselves in their constructor.
     * If debarked log doesn't have a registry name it's not registered.
     * If it has a registry name check if it's registered.
     **/
    public static byte[] transformForgeRegistry(byte[] basicClass) {
        ClassNode cls = read(basicClass);
        for (MethodNode method : cls.methods) {
            if (method.name.equals("register")) {
                AbstractInsnNode node = method.instructions.getFirst();
                InsnList list = new InsnList();
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new FieldInsnNode(GETFIELD, cls.name, "superType", "Ljava/lang/Class;"));
                list.add(new LdcInsnNode(Type.getType("Lnet/minecraft/block/Block;")));
                LabelNode l_con = new LabelNode();
                list.add(new JumpInsnNode(IF_ACMPNE, l_con));
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new VarInsnNode(ALOAD, 1));
                list.add(new TypeInsnNode(CHECKCAST, "net/minecraft/block/Block"));
                list.add(hook("Debarking$tryRegisteringDebarkedLog", "(Lnet/minecraftforge/registries/IForgeRegistry;Lnet/minecraft/block/Block;)V"));
                list.add(l_con);
                list.add(new FrameNode(F_SAME, 0, null, 0, null));
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new FieldInsnNode(GETFIELD, cls.name, "superType", "Ljava/lang/Class;"));
                list.add(new LdcInsnNode(Type.getType("Lnet/minecraft/item/Item;")));
                LabelNode l_con1 = new LabelNode();
                list.add(new JumpInsnNode(IF_ACMPNE, l_con1));
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new VarInsnNode(ALOAD, 1));
                list.add(new TypeInsnNode(CHECKCAST, "net/minecraft/item/Item"));
                list.add(hook("Debarking$tryRegisteringDebarkedLog", "(Lnet/minecraftforge/registries/IForgeRegistry;Lnet/minecraft/item/Item;)V"));
                list.add(l_con1);
                list.add(new FrameNode(F_SAME, 0, null, 0, null));
                method.instructions.insertBefore(node, list);
            }
        }
        writeClass(cls);
        return write(cls);
    }

    public static byte[] transformBlockStateMapper(byte[] basicClass) {
        ClassNode cls = read(basicClass);
        for (MethodNode method : cls.methods) {
            if (method.name.equals("registerBlockStateMapper")) {
                AbstractInsnNode node = method.instructions.getLast();
                while (node.getOpcode() != RETURN) node = node.getPrevious();
                InsnList list = new InsnList();
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new VarInsnNode(ALOAD, 1));
                list.add(new VarInsnNode(ALOAD, 2));
                list.add(hook("Debarking$registerBlockStateMapper", "(Lnet/minecraft/client/renderer/block/statemap/BlockStateMapper;Lnet/minecraft/block/Block;Lnet/minecraft/client/renderer/block/statemap/IStateMapper;)V"));
                method.instructions.insertBefore(node, list);
                break;
            }
        }
        return write(cls);
    }

    private static String fromUtf8Const(byte[] cls, int start) {
        int size = cls[start + 1] + cls[start + 2] - 1;
        start += 3;
        StringBuilder builder = new StringBuilder();
        while (size > -1) {
            builder.insert(0, (char) cls[start + size]);
            size--;
        }
        return builder.toString();
    }

    // Return index to jump
    // index is always the start of the constant
    private static int nextConstant(byte[] cls, int index) {
        int type = cls[index];
        switch (type) {
            case UTF8: return index + 3 + (((cls[index + 2] & 0xFF) | (cls[index + 1] & 0xFF) << 8));
            case INTEGER:
            case FLOAT: return index + 5;
            case LONG:
            case DOUBLE: return index + 9;
            case CLASS:
            case STRING: return index + 3;
            case FIELD_REF:
            case METHOD_REF:
            case IFACE_METHOD_REF:
            case NAME_AND_TYPE: return index + 5;
            case METHOD_HANDLE: return index + 4;
            case METHOD_TYPE: return index + 3;
            case INVOKE_DYNAMIC: return index + 5;
            default: return index + 3;
        }
    }

    private static String createDebarkedClass(ClassNode clsLog, String origMDesc, String mDesc, IntList descMap) {
        ClassNode cls = new ClassNode();
        List<String> interfaces = new ArrayList<>(clsLog.interfaces == null ? 1 : cls.interfaces.size() + 1);
        interfaces.add("surreal/backportium/api/block/DebarkedLog");
        cls.visit(V1_8, ACC_PUBLIC | ACC_STATIC, clsLog.name + "$Debarked", null, clsLog.name, interfaces.toArray(new String[0]));
        { // origLog
            cls.visitField(ACC_PUBLIC | ACC_FINAL, "origLog", "Lnet/minecraft/block/Block;", null, null);
        }
        { // <init>
            MethodVisitor m = cls.visitMethod(ACC_PUBLIC, "<init>", mDesc, null, null);
            m.visitVarInsn(ALOAD, 0);
            boolean hasValues = descMap != null && !descMap.isEmpty();
            if (hasValues) {
                for (int i = 0; i < descMap.size(); i++) {
                    m.visitVarInsn(descMap.get(i), i + 1);
                }
            }
            m.visitMethodInsn(INVOKESPECIAL, clsLog.name, "<init>", origMDesc, false);
            m.visitVarInsn(ALOAD, 0);
            m.visitVarInsn(ALOAD, (hasValues ? descMap.size() : 0) + 1);
            m.visitFieldInsn(PUTFIELD, cls.name, "origLog", "Lnet/minecraft/block/Block;");
            m.visitInsn(RETURN);
        }
        { // getOriginal
            MethodVisitor m = cls.visitMethod(ACC_PUBLIC, "getOriginal", "()Lnet/minecraft/block/Block;", null, null);
            m.visitVarInsn(ALOAD, 0);
            m.visitFieldInsn(GETFIELD, cls.name, "origLog", "Lnet/minecraft/block/Block;");
            m.visitInsn(ARETURN);
        }
        writeClass(cls);
        byte[] bytes = write(cls);
        loadNewClass(cls.name, bytes);
        return cls.name;
    }

//    public static byte[] transformBlockLogExtends(byte[] basicClass) {
//    }

//    public static byte[] transformForgeRegistry(byte[] basicClass) {
//        ClassNode cls = read(basicClass);
//        for (MethodNode method : cls.methods) {
//            if (method.name.equals("register")) {
//                AbstractInsnNode node = method.instructions.getLast();
//                while (node.getOpcode() != RETURN) node = node.getPrevious();
//                InsnList list = new InsnList();
//                list.add(new VarInsnNode(ALOAD, 0));
//                list.add(new VarInsnNode(ALOAD, 0));
//                list.add(new FieldInsnNode(GETFIELD, cls.name, "superType", "Ljava/lang/Class;"));
//                list.add(new VarInsnNode(ALOAD, 1));
//                list.add(hook("ForgeRegistry$registerDebarked", "(Lnet/minecraftforge/registries/ForgeRegistry;Ljava/lang/Class;Ljava/lang/Object;)V"));
//                break;
//            }
//        }
//        return write(cls);
//    }
}
