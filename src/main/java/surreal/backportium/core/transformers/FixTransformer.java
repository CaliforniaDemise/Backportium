package surreal.backportium.core.transformers;

import org.objectweb.asm.tree.*;

import java.util.Iterator;

/**
 * Small fixes between versions.
 **/
public class FixTransformer extends BasicTransformer {

    // [MC-130137] - Grass Blocks don't decay underwater
    public static byte[] transformBlockGrass(byte[] basicClass) {
        ClassNode cls = read(basicClass);
        for (MethodNode method : cls.methods) {
            if (method.name.equals(getName("updateTick", "func_180650_b"))) {
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node instanceof FrameNode) {
                        InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 1));
                        list.add(new VarInsnNode(ALOAD, 2));
                        list.add(new MethodInsnNode(INVOKEVIRTUAL, "net/minecraft/util/math/BlockPos", getName("up", "func_177984_a"), "()Lnet/minecraft/util/math/BlockPos;", false));
                        list.add(new MethodInsnNode(INVOKEVIRTUAL, "net/minecraft/world/World", getName("getBlockState", "func_180495_p"), "(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/state/IBlocKState;", false));
                        list.add(new MethodInsnNode(INVOKEINTERFACE, "net/minecraft/block/state/IBlockState", getName("getMaterial", "func_149688_o"), "()Lnet/minecraft/block/material/Material;", true));
                        list.add(new MethodInsnNode(INVOKEVIRTUAL, "net/minecraft/block/material/Material", getName("isLiquid", "func_76224_d"), "()Z", false));
                        LabelNode l_con = new LabelNode();
                        list.add(new JumpInsnNode(IFEQ, l_con));
                        list.add(new VarInsnNode(ALOAD, 1));
                        list.add(new VarInsnNode(ALOAD, 2));
                        list.add(new FieldInsnNode(GETSTATIC, "net/minecraft/init/Blocks", getName("DIRT", "field_150346_d"), "Lnet/minecraft/block/Block;"));
                        list.add(new MethodInsnNode(INVOKEVIRTUAL, "net/minecraft/block/Block", getName("getDefaultState", "func_176223_P"), "()Lnet/minecraft/block/state/IBlockState;", false));
                        list.add(new MethodInsnNode(INVOKEVIRTUAL, "net/minecraft/world/World", getName("setBlockState", "func_175656_a"), "(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;)Z", false));
                        list.add(new InsnNode(RETURN));
                        list.add(l_con);
                        list.add(new FrameNode(F_SAME, 0, null, 0, null));
                        method.instructions.insert(node, list);
                        break;
                    }
                }
                AbstractInsnNode node = method.instructions.getLast();
                while (node.getOpcode() != ICONST_2) node = node.getPrevious();
                node = node.getNext();
                LabelNode l_con = ((JumpInsnNode) node).label;
                InsnList list = new InsnList();
                list.add(new VarInsnNode(ALOAD, 7));
                list.add(new MethodInsnNode(INVOKEINTERFACE, "net/minecraft/block/state/IBlockState", getName("getMaterial", "func_149688_o"), "()Lnet/minecraft/block/material/Material;", true));
                list.add(new MethodInsnNode(INVOKEVIRTUAL, "net/minecraft/block/material/Material", getName("isLiquid", "func_76224_d"), "()Z", false));
                list.add(new JumpInsnNode(IFNE, l_con));
                method.instructions.insert(node, list);
                break;
            }
        }
        return write(cls);
    }
}
