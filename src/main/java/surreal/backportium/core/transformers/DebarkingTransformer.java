package surreal.backportium.core.transformers;

import it.unimi.dsi.fastutil.ints.Int2IntMap;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.tree.*;

import java.lang.reflect.Modifier;
import java.util.Iterator;

public class DebarkingTransformer extends BasicTransformer {

    public static byte[] transformBlockLog(byte[] basicClass) {
        ClassNode cls = read(basicClass);
        { // isDebarked
            MethodVisitor m = cls.visitMethod(ACC_PUBLIC, "isDebarked", "()Z", null, null);
            m.visitInsn(ICONST_0);
            m.visitInsn(IRETURN);
        }
        return write(cls);
    }

    // Non-abstract BlockLog extending aberrations
    public static byte[] transformBlockLogEx(byte[] basicClass) {
        ClassNode cls = read(basicClass);
        if ((cls.access & ACC_ABSTRACT) != ACC_ABSTRACT && checkIfAbstract(cls) && isSuper(cls, "net/minecraft/block/BlockLog")) {
            for (MethodNode method : cls.methods) {
                if (method.name.equals("<init>") && ((method.access & ACC_PUBLIC) == ACC_PUBLIC || (method.access & ACC_PROTECTED) == ACC_PROTECTED)) {
                    Int2IntMap map = getDescMap(method.desc);
                    String clsName = createDebarkedClass(cls, method.desc, map);
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
                                        builder.append("Lnet/minecraft/block/Block;Z"); // isDebarked
                                    }
                                    builder.append(c);
                                }
                                InsnList list = new InsnList();
                                list.add(new VarInsnNode(ALOAD, 0));
                                list.add(new VarInsnNode(ALOAD, 0));
                                list.add(new MethodInsnNode(INVOKEVIRTUAL, cls.name, "isDebarked", "()Z", false));
                                list.add(hook("BlockLog$setRegistryName", builder.toString()));
                                method.instructions.insertBefore(node, list);
                                iterator.remove();
                            }
                        }
                        else if (node.getOpcode() == RETURN) {
                            InsnList list = new InsnList();
                            list.add(new VarInsnNode(ALOAD, 0));
                            list.add(new MethodInsnNode(INVOKEVIRTUAL, cls.name, "isDebarked", "()Z", false));
                            LabelNode l_con = new LabelNode();
                            list.add(new JumpInsnNode(IFNE, l_con));
                            list.add(new TypeInsnNode(NEW, clsName));
                            list.add(new InsnNode(DUP));
                            if (!map.isEmpty()) {
                                for (int i : map.keySet()) {
                                    list.add(new VarInsnNode(map.get(i), i));
                                }
                            }
                            list.add(new MethodInsnNode(INVOKESPECIAL, clsName, "<init>", method.desc, false));
                            list.add(new VarInsnNode(ALOAD, 0));
                            list.add(hook("Debarking$registerBlock", "(Lnet/minecraft/block/Block;Lnet/minecraft/block/Block;)V"));
                            list.add(l_con);
                            method.instructions.insertBefore(node, list);
                            break;
                        }
                    }
                    break;
                }
            }
        }
        return write(cls, ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES); // COMPUTE_FRAMES???? He fell off.....
    }

    public static byte[] transformModelBakery(byte[] basicClass) {
        ClassNode cls = read(basicClass);
        cls.fields.forEach(field -> { if (field.name.equals("blockDefinitions")) { field.access ^= ACC_PRIVATE; field.access |= ACC_PUBLIC; } });
        writeClass(cls);
        return write(cls);
    }

    public static byte[] transformModelLoader(byte[] basicClass) {
        ClassNode cls = read(basicClass);
        for (MethodNode method : cls.methods) {
            if (method.name.equals("setupModelRegistry")) {
                AbstractInsnNode node = method.instructions.getLast();
                while (node.getOpcode() != ALOAD) node = node.getPrevious();
                InsnList list = new InsnList();
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new FieldInsnNode(GETFIELD, cls.name, "blockDefinitions", "Ljava/util/Map;"));
                list.add(hook("ModelBakery$log", "(Ljava/util/Map;)V"));
                method.instructions.insertBefore(node, list);
                break;
            }
        }
        writeClass(cls);
        return write(cls);
    }

    private static boolean checkIfAbstract(ClassNode cls) {
        try {
            return Modifier.isAbstract(Class.forName(cls.superName.replace('/', '.')).getModifiers());
        }
        catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static String createDebarkedClass(ClassNode clsLog, String mDesc, Int2IntMap descMap) {
        ClassNode cls = new ClassNode();

        cls.visit(V1_8, ACC_PUBLIC | ACC_STATIC, clsLog.name + "$Debarked", null, clsLog.name, clsLog.interfaces.toArray(new String[0]));
        { // <init>
            MethodVisitor m = cls.visitMethod(ACC_PUBLIC, "<init>", mDesc, null, null);
            m.visitVarInsn(ALOAD, 0);
            if (!descMap.isEmpty()) {
                for (int i : descMap.keySet()) {
                    m.visitVarInsn(descMap.get(i), i);
                }
            }
            m.visitMethodInsn(INVOKESPECIAL, clsLog.name, "<init>", mDesc, false);
            m.visitInsn(RETURN);
        }
        { // isDebarked
            MethodVisitor m = cls.visitMethod(ACC_PUBLIC, "isDebarked", "()Z", null, null);
            m.visitInsn(ICONST_1);
            m.visitInsn(IRETURN);
        }

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
