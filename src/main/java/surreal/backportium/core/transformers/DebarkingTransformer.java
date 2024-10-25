package surreal.backportium.core.transformers;

import it.unimi.dsi.fastutil.ints.Int2IntMap;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.tree.*;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DebarkingTransformer extends BasicTransformer {

    // Non-abstract BlockLog extending aberrations
    public static byte[] transformBlockLogEx(byte[] basicClass) {
        ClassNode cls = read(basicClass);
        if ((cls.access & ACC_ABSTRACT) != ACC_ABSTRACT && checkIfAbstract(cls) && isSuper(cls, "net/minecraft/block/BlockLog")) {
            for (MethodNode method : cls.methods) {
                if (method.name.equals("<init>") && ((method.access & ACC_PUBLIC) == ACC_PUBLIC || (method.access & ACC_PROTECTED) == ACC_PROTECTED)) {
                    Int2IntMap map = getDescMap(method.desc);
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
                                for (int i : map.keySet()) {
                                    list.add(new VarInsnNode(map.get(i), i));
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
                    break;
                }
            }
        }
        return write(cls, ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES); // COMPUTE_FRAMES???? He fell off.....
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

    /*
    Register ModelBlock to Map<ResourceLocation, ModelBlock> models
    Register VariantList to Map<ModelResourceLocation, VariantList> variants
    Register ModelBlockDefinition Map<ResourceLocation, ModelBlockDefinition> blockDefinitions
    */

    public static byte[] transformModelLoaderRegistry(byte[] basicClass) {
        ClassNode cls = read(basicClass);
        for (MethodNode method : cls.methods) {
            if (method.name.equals("registerVariant")) {
                AbstractInsnNode node = method.instructions.getFirst();
                InsnList list = new InsnList();

                method.instructions.insertBefore(node, list);
                break;
            }
        }
        return write(cls);
    }

//    public static byte[] transformVanillaLoader(byte[] basicClass) {
//        ClassNode cls = read(basicClass);
//        for (MethodNode method : cls.methods) {
//            if (method.name.equals("loadModel")) {
//
//            }
//        }
//    }

    private static boolean checkIfAbstract(ClassNode cls) {
        try {
            return Modifier.isAbstract(Class.forName(cls.superName.replace('/', '.')).getModifiers());
        }
        catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static String createDebarkedClass(ClassNode clsLog, String origMDesc, String mDesc, Int2IntMap descMap) {
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
            if (!descMap.isEmpty()) {
                int count = 0;
                for (int i : descMap.keySet()) {
                    count++;
                    if (count == descMap.size()) break;
                    m.visitVarInsn(descMap.get(i), i);
                }
            }
            m.visitMethodInsn(INVOKESPECIAL, clsLog.name, "<init>", origMDesc, false);
            m.visitVarInsn(ALOAD, 0);
            m.visitVarInsn(ALOAD, descMap.size() + 1);
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
