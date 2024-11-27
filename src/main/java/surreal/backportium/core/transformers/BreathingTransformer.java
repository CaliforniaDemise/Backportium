package surreal.backportium.core.transformers;

import org.objectweb.asm.tree.*;

import java.util.Iterator;

public class BreathingTransformer extends BasicTransformer {

    /**
     * Handle 1.13 entity breathing
     * @param cls Comes from {@link TridentTransformer#transformEntityLivingBase(byte[])}
     **/
    public static byte[] transformEntityLivingBase(ClassNode cls) {
        for (MethodNode method : cls.methods) {
            if (method.name.equals(getName("onEntityUpdate", "func_70030_z"))) {
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                boolean check = false;
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == SIPUSH) {
                        InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new MethodInsnNode(INVOKEVIRTUAL, cls.name, getName("getAir", "func_70086_ai"), "()I", false));
                        list.add(new InsnNode(ICONST_4));
                        list.add(new InsnNode(IADD));
                        list.add(new IntInsnNode(SIPUSH, 300));
                        list.add(new MethodInsnNode(INVOKESTATIC, "java/lang/Math", "min", "(II)I", false));
                        method.instructions.insertBefore(node, list);
                        method.instructions.remove(node);
                        check = true;
                    }
                    else if (check && node.getOpcode() == IFNE) {
                        LabelNode l_con = ((JumpInsnNode) node).label;
                        InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new FieldInsnNode(GETFIELD, cls.name, getName("world", "field_70170_p"), "Lnet/minecraft/world/World;"));
                        list.add(new TypeInsnNode(NEW, "net/minecraft/util/math/BlockPos"));
                        list.add(new InsnNode(DUP));
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new FieldInsnNode(GETFIELD, cls.name, getName("posX", "field_70165_t"), "D"));
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new FieldInsnNode(GETFIELD, cls.name, getName("posY", "field_70163_u"), "D"));
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new MethodInsnNode(INVOKEVIRTUAL, cls.name, getName("getEyeHeight", "func_70047_e"), "()F", false));
                        list.add(new InsnNode(F2D));
                        list.add(new InsnNode(DADD));
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new FieldInsnNode(GETFIELD, cls.name, getName("posZ", "field_70161_v"), "D"));
                        list.add(new MethodInsnNode(INVOKESPECIAL, "net/minecraft/util/math/BlockPos", "<init>", "(DDD)V", false));
                        list.add(new MethodInsnNode(INVOKEVIRTUAL, "net/minecraft/world/World", getName("getBlockState", "func_180495_p"), "(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/state/IBlockState;", false));
                        list.add(new MethodInsnNode(INVOKEINTERFACE, "net/minecraft/block/state/IBlockState", getName("getBlock", "func_177230_c"), "()Lnet/minecraft/block/Block;", true));
                        list.add(new FieldInsnNode(GETSTATIC, "surreal/backportium/block/ModBlocks", "BUBBLE_COLUMN", "Lsurreal/backportium/block/v1_13/BlockBubbleColumn;"));
                        list.add(new JumpInsnNode(IF_ACMPEQ, l_con));
                        method.instructions.insert(node, list);
                        break;
                    }
                }
                break;
            }
        }
        return write(cls);
    }

    public static byte[] transformGuiIngameForge(byte[] basicClass) {
        ClassNode cls = read(basicClass);
        for (MethodNode method : cls.methods) {
            if (method.name.equals("renderAir")) {
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == ALOAD && ((VarInsnNode) node).var == 3) {
                        InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 3));
                        list.add(new MethodInsnNode(INVOKEVIRTUAL, "net/minecraft/entity/player/EntityPlayer", getName("getAir", "func_70086_ai"), "()I", false));
                        list.add(new IntInsnNode(SIPUSH, 300));
                        method.instructions.insertBefore(node, list);
                        for (int i = 0; i < 3; i++) {
                            node = node.getNext();
                            method.instructions.remove(node.getPrevious());
                        }
                        ((JumpInsnNode) node).setOpcode(IF_ICMPEQ);
                        break;
                    }
                }
                break;
            }
        }
        return write(cls);
    }
}
