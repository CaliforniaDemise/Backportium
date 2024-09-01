package surreal.backportium.core.transformers;

import org.objectweb.asm.tree.*;
import surreal.backportium.api.block.FluidLogged;
import surreal.backportium.core.BPPlugin;

/** Low budget knock-off of Fluidlogged-API with using {@link FluidLogged} **/
public class FluidloggingTransformer extends BasicTransformer {

    public static byte[] transformBlockFluidBase(byte[] basicClass) {
        if (BPPlugin.FLUIDLOGGED) return basicClass;
        ClassNode cls = read(basicClass);
        for (MethodNode method : cls.methods) {
            if (method.name.equals(getName("shouldSideBeRendered", ""))) {
                AbstractInsnNode node = method.instructions.getLast();
                while (node.getOpcode() != IRETURN) node = node.getPrevious();
                InsnList list = new InsnList();
                list.add(new VarInsnNode(ALOAD, 5));
                list.add(hook("BlockFluidBase$renderSide", "(ZLnet/minecraft/block/state/IBlockState;)Z"));
                method.instructions.insertBefore(node, list);
                break;
            }
        }
        return write(cls);
    }

    public static byte[] transformBlockLiquid(byte[] basicClass) {
        if (BPPlugin.FLUIDLOGGED) return basicClass;
        ClassNode cls = read(basicClass);
        for (MethodNode method : cls.methods) {
            if (method.name.equals(getName("shouldSideBeRendered", ""))) {
                AbstractInsnNode node = method.instructions.getLast();
                while (node.getOpcode() != IRETURN) node = node.getPrevious();
                InsnList list = new InsnList();
                list.add(new VarInsnNode(ALOAD, 2));
                list.add(new VarInsnNode(ALOAD, 3));
                list.add(new VarInsnNode(ALOAD, 4));
                list.add(hook("BlockLiquid$renderSide", "(ZLnet/minecraft/world/IBlockAccess;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/EnumFacing;)Z"));
                method.instructions.insertBefore(node, list);
                break;
            }
        }
        return write(cls);
    }
}
