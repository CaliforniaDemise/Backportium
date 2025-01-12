package surreal.backportium.core.v13;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.tree.*;
import surreal.backportium.core.transformers.Transformer;

import java.util.Iterator;

/**
 * Adds Uncarved Pumpkin
 **/
class UncarvedPumpkinTransformer extends Transformer {

    public static byte[] transformBlockFenceLike(byte[] basicClass) {
        ClassNode cls = read(basicClass);
        for (MethodNode method : cls.methods) {
            if (method.name.equals(getName("isExcepBlockForAttachWithPiston", "func_193394_e"))) {
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == IF_ACMPEQ) {
                        InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(getUncarvedPumpkin());
                        list.add(new JumpInsnNode(IF_ACMPEQ, ((JumpInsnNode) node).label));
                        method.instructions.insert(node, list);
                    }
                }
                break;
            }
        }
        return write(cls);
    }

    public static byte[] transformBlockStem(byte[] basicClass) {
        ClassNode cls = read(basicClass);
        { // BP$getCrop
            MethodVisitor m = cls.visitMethod(ACC_PRIVATE | ACC_STATIC, "BP$getCrop", "(Lnet/minecraft/block/Block;)Lnet/minecraft/block/Block;", null, null);
            m.visitVarInsn(ALOAD, 0);
            m.visitFieldInsn(GETSTATIC, "net/minecraft/init/Blocks", getName("PUMPKIN", ""), "Lnet/minecraft/block/Block;");
            Label l_con = new Label();
            m.visitJumpInsn(IF_ACMPNE, l_con);
            m.visitFieldInsn(GETSTATIC, "surreal/backportium/block/ModBlocks", "UNCARVED_PUMPKIN", "Lnet/minecraft/block/Block;");
            m.visitInsn(ARETURN);
            m.visitLabel(l_con);
            m.visitFrame(F_SAME, 0, null, 0, null);
            m.visitVarInsn(ALOAD, 0);
            m.visitInsn(ARETURN);
        }
        for (MethodNode method : cls.methods) {
            String name = method.name;
            if (name.equals(getName("getActualState", "func_176221_a"))) {
                transformBlockStem$apply(cls, method);
            } else if (name.equals(getName("updateTick", "func_180650_b"))) {
                transformBlockStem$apply(cls, method);
                break;
            }
        }
        return write(cls);
    }

    public static byte[] transformStatList(byte[] basicClass) {
        ClassNode cls = read(basicClass);
        for (MethodNode method : cls.methods) {
            if (method.name.equals(getName("replaceAllSimilarBlocks", "func_75924_a"))) {
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == INVOKESTATIC) {
                        InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new LdcInsnNode("backportium:pumpkin"));
                        list.add(new MethodInsnNode(INVOKESTATIC, "net/minecraft/block/Block", getName("getBlockFromName", "func_149684_b"), "(Ljava/lang/String;)Lnet/minecraft/block/Block;", false));
                        list.add(new FieldInsnNode(GETSTATIC, "net/minecraft/init/Blocks", getName("PUMPKIN", "field_150423_aK"), "Lnet/minecraft/block/Block;"));
                        list.add(new VarInsnNode(ILOAD, 1));
                        list.add(new MethodInsnNode(INVOKESTATIC, cls.name, getName("mergeStatBases", "func_151180_a"), "([Lnet/minecraft/stats/StatBase;Lnet/minecraft/block/Block;Lnet/minecraft/block/Block;Z)V", false));
                        method.instructions.insert(node, list);
                        break;
                    }
                }
            }
        }
        return write(cls);
    }

    public static byte[] transformWorldGenPumpkin(byte[] basicClass) {
        ClassNode cls = read(basicClass);
        for (MethodNode method : cls.methods) {
            if (method.name.equals(getName("generate", "func_180709_b"))) {
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                int i = 0;
                boolean check = false;
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == GETSTATIC) {
                        FieldInsnNode field = (FieldInsnNode) node;
                        if (field.owner.endsWith("/Blocks")) {
                            if (!check) check = true;
                            else {
                                method.instructions.insertBefore(node, getUncarvedPumpkin());
                                iterator.remove();
                            }
                        }
                        else if (field.owner.endsWith("/BlockPumpkin")) i = 5;
                    }
                    if (i > 0) {
                        iterator.remove();
                        i--;
                        if (i == 0) break;
                    }
                }
                break;
            }
        }
        return write(cls);
    }

    private static FieldInsnNode getUncarvedPumpkin() {
        return new FieldInsnNode(GETSTATIC, "surreal/backportium/block/ModBlocks", "UNCARVED_PUMPKIN", "Lnet/minecraft/block/Block;");
    }

    private static void transformBlockStem$apply(ClassNode cls, MethodNode method) {
        Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
        int i = 0;
        while (iterator.hasNext()) {
            AbstractInsnNode node = iterator.next();
            if (node.getOpcode() == GETFIELD) {
                method.instructions.insert(node, new MethodInsnNode(INVOKESTATIC, cls.name, "BP$getCrop", "(Lnet/minecraft/block/Block;)Lnet/minecraft/block/Block;", false));
                i++;
                if (i == 2) return;
            }
        }
    }
}
