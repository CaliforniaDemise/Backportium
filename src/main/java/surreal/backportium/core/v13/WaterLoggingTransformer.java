package surreal.backportium.core.v13;

import org.objectweb.asm.tree.*;
import surreal.backportium.core.BPPlugin;
import surreal.backportium.core.transformers.Transformer;
import surreal.backportium.api.block.FluidLogged;

/**
 * A 1.13 waterlogging implementation.
 * Right now it's a low budget knock-off of FluidLogging API
 **/
class WaterLoggingTransformer extends Transformer {

    /**
     * Don't render sides of the liquid if the block is an instance of {@link FluidLogged}
     **/
    protected static byte[] transformBlockFluidBase(byte[] basicClass) {
        if (BPPlugin.FLUIDLOGGED) return basicClass;
        ClassNode cls = read(basicClass);
        for (MethodNode method : cls.methods) {
            if (method.name.equals(getName("shouldSideBeRendered", "func_176225_a"))) {
                AbstractInsnNode node = method.instructions.getLast();
                while (node.getOpcode() != IRETURN) node = node.getPrevious();
                InsnList list = new InsnList();
                LabelNode l_con = new LabelNode();
                list.add(new JumpInsnNode(IFNE, l_con));
                list.add(new InsnNode(ICONST_0));
                list.add(new InsnNode(IRETURN));
                list.add(l_con);
                list.add(new FrameNode(F_SAME, 0, null, 0, null));
                list.add(new VarInsnNode(ALOAD, 5));
                list.add(new MethodInsnNode(INVOKEINTERFACE, "net/minecraft/block/state/IBlockState", getName("getBlock", "func_177230_c"), "()Lnet/minecraft/block/Block;", true));
                list.add(new TypeInsnNode(INSTANCEOF, "surreal/backportium/api/block/FluidLogged"));
                list.add(new InsnNode(IRETURN));
                method.instructions.insertBefore(node, list);
                break;
            }
        }
        return write(cls);
    }

    protected static byte[] transformBlockLiquid(byte[] basicClass) {
        if (BPPlugin.FLUIDLOGGED) return basicClass;
        ClassNode cls = read(basicClass);
        for (MethodNode method : cls.methods) {
            if (method.name.equals(getName("shouldSideBeRendered", "func_176225_a"))) {
                AbstractInsnNode node = method.instructions.getLast();
                while (node.getOpcode() != IRETURN) node = node.getPrevious();
                InsnList list = new InsnList();
                LabelNode l_con = new LabelNode();
                list.add(new JumpInsnNode(IFEQ, l_con));
                list.add(new VarInsnNode(ALOAD, 2));
                list.add(new VarInsnNode(ALOAD, 3));
                list.add(new VarInsnNode(ALOAD, 4));
                list.add(new MethodInsnNode(INVOKEVIRTUAL, "net/minecraft/util/math/BlockPos", "offset", "(Lnet/minecraft/util/EnumFacing;)Lnet/minecraft/util/math/BlockPos;", false));
                list.add(new MethodInsnNode(INVOKEINTERFACE, "net/minecraft/world/IBlockAccess", getName("getBlockState", "func_180495_p"), "(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/state/IBlockState;", true));
                list.add(new MethodInsnNode(INVOKEINTERFACE, "net/minecraft/block/state/IBlockState", getName("getBlock", "func_177230_c"), "()Lnet/minecraft/block/Block;", true));
                list.add(new TypeInsnNode(INSTANCEOF, "surreal/backportium/api/block/FluidLogged"));
                LabelNode l_con_check = new LabelNode();
                list.add(new JumpInsnNode(IFNE, l_con_check));
                list.add(new InsnNode(ICONST_1));
                list.add(new InsnNode(IRETURN));
                list.add(l_con_check);
                list.add(new FrameNode(F_SAME, 0, null, 0, null));
                list.add(new InsnNode(ICONST_0));
                list.add(new InsnNode(IRETURN));
                list.add(l_con);
                list.add(new FrameNode(F_SAME, 0, null, 0, null));
                list.add(new InsnNode(ICONST_1));
                method.instructions.insertBefore(node, list);
                break;
            }
        }
        writeClass(cls);
        return write(cls);
    }
}