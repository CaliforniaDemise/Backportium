package surreal.backportium.core.transformers;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.tree.*;

import java.util.Iterator;

public class PumpkinTransformer extends BasicTransformer{

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
        for (MethodNode method : cls.methods) {
            String name = method.name;
            if (name.equals(getName("getActualState", "func_176221_a"))) {
                transformBlockStem$apply(method);
            }
            else if (name.equals(getName("updateTick", "func_180650_b"))) {
               transformBlockStem$apply(method);
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
                        list.add(new VarInsnNode(ALOAD, 0));;
                        list.add(getUncarvedPumpkin());
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
        String mName = getName("generate", "func_180709_b");

        Iterator<MethodNode> iterator = cls.methods.iterator();
        while (iterator.hasNext()) {
            MethodNode m = iterator.next();
            if (m.name.equals(mName)) {
                iterator.remove();
                break;
            }
        }

        {
            MethodVisitor m_generate = cls.visitMethod(ACC_PUBLIC, mName, "(Lnet/minecraft/world/World;Ljava/util/Random;Lnet/minecraft/util/math/BlockPos;)Z", null, null);
            m_generate.visitVarInsn(ALOAD, 1);
            m_generate.visitVarInsn(ALOAD, 2);
            m_generate.visitVarInsn(ALOAD, 3);
            m_generate.visitMethodInsn(INVOKESTATIC, "surreal/backportium/core/transforrmers/BPHooks", "WorldGenPumpkin$generate", "(Lnet/minecraft/world/World;Ljava/util/Random;Lnet/minecraft/util/math/BlockPos;)Z", false);
            m_generate.visitInsn(IRETURN);
        }

        return write(cls);
    }

    private static FieldInsnNode getUncarvedPumpkin() {
        return new FieldInsnNode(GETSTATIC, "surreal/backportium/block/ModBlocks", "UNCARVED_PUMPKIN", "Lsurreal/backportium/block/v1_13/BlockPumpkin;");
    }

    private static void transformBlockStem$apply(MethodNode method) {
        Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
        int i = 0;
        while (iterator.hasNext()) {
            AbstractInsnNode node = iterator.next();
            if (node.getOpcode() == GETFIELD) {
                method.instructions.insert(node, hook("BlockStem$getCrop", "(Lnet/minecraft/block/Block;)Lnet/minecraft/block/Block;"));
                i++;
                if (i == 2) return;
            }
        }
    }
}
