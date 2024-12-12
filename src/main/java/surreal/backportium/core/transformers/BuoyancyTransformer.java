package surreal.backportium.core.transformers;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.tree.*;

import java.util.Iterator;

// TODO Handle AE2 crystal seeds and BWM buoyancy
public class BuoyancyTransformer extends BasicTransformer {

    public static byte[] transformEntityItem(byte[] basicClass) {
        ClassNode cls = read(basicClass);
        { // applyFloatMotion
            MethodVisitor m = cls.visitMethod(ACC_PRIVATE, "applyFloatMotion", "()V", null, null);
            m.visitVarInsn(ALOAD, 0);
            m.visitFieldInsn(GETFIELD, cls.name, getName("motionY", "field_70181_x"), "D");
            m.visitLdcInsn(0.06D);
            m.visitInsn(DCMPG);
            Label l_con_smaller = new Label();
            m.visitJumpInsn(IFGE, l_con_smaller);
            m.visitVarInsn(ALOAD, 0);
            m.visitVarInsn(ALOAD, 0);
            m.visitFieldInsn(GETFIELD, cls.name, getName("motionY", "field_70181_x"), "D");
            m.visitLdcInsn(5.0E-4D);
            m.visitInsn(DADD);
            m.visitFieldInsn(PUTFIELD, cls.name, getName("motionY", "field_70181_x"), "D");
            m.visitLabel(l_con_smaller);
            m.visitFrame(F_SAME, 0, null, 0, null);
            m.visitVarInsn(ALOAD, 0);
            m.visitVarInsn(ALOAD, 0);
            m.visitFieldInsn(GETFIELD, cls.name, getName("motionX", "field_70159_w"), "D");
            m.visitLdcInsn(0.99D);
            m.visitInsn(DMUL);
            m.visitFieldInsn(PUTFIELD, cls.name, getName("motionX", "field_70159_w"), "D");
            m.visitVarInsn(ALOAD, 0);
            m.visitVarInsn(ALOAD, 0);
            m.visitFieldInsn(GETFIELD, cls.name, getName("motionZ", "field_70179_y"), "D");
            m.visitLdcInsn(0.99D);
            m.visitInsn(DMUL);
            m.visitFieldInsn(PUTFIELD, cls.name, getName("motionZ", "field_70179_y"), "D");
            m.visitInsn(RETURN);
        }
        for (MethodNode method : cls.methods) {
            if (method.name.equals(getName("onUpdate", "func_70071_h_"))) {
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == INVOKEVIRTUAL && ((MethodInsnNode) node).name.equals(getName("hasNoGravity", "func_189652_ae"))) {
                        InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new FieldInsnNode(GETFIELD, cls.name, getName("posY", "field_70163_u"), "D"));
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new MethodInsnNode(INVOKEVIRTUAL, cls.name, getName("getEyeHeight", "func_70047_e"), "()F", false));
                        list.add(new InsnNode(F2D));
                        list.add(new InsnNode(DADD));
                        list.add(new VarInsnNode(DSTORE, 7));
                        list.add(new TypeInsnNode(NEW, "net/minecraft/util/math/BlockPos"));
                        list.add(new InsnNode(DUP));
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new FieldInsnNode(GETFIELD, cls.name, getName("posX", "field_70165_t"), "D"));
                        list.add(new VarInsnNode(DLOAD, 7));
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new FieldInsnNode(GETFIELD, cls.name, getName("posZ", "field_70161_v"), "D"));
                        list.add(new MethodInsnNode(INVOKESPECIAL, "net/minecraft/util/math/BlockPos", "<init>", "(DDD)V", false));
                        list.add(new VarInsnNode(ASTORE, 9));
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new FieldInsnNode(GETFIELD, cls.name, getName("world", "field_70170_p"), "Lnet/minecraft/world/World;"));
                        list.add(new VarInsnNode(ALOAD, 9));
                        list.add(new MethodInsnNode(INVOKEVIRTUAL, "net/minecraft/world/World", getName("getBlockState", "func_180495_p"), "(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/state/IBlockState;", false));
                        list.add(new VarInsnNode(ASTORE, 10));
                        list.add(new VarInsnNode(ALOAD, 10));
                        list.add(new MethodInsnNode(INVOKEINTERFACE, "net/minecraft/block/state/IBlockState", getName("getMaterial", "func_185904_a"), "()Lnet/minecraft/block/material/Material;", true));
                        list.add(new FieldInsnNode(GETSTATIC, "net/minecraft/block/material/Material", getName("WATER", "field_151586_h"), "Lnet/minecraft/block/material/Material;"));
                        LabelNode l_con = new LabelNode();
                        list.add(new JumpInsnNode(IF_ACMPNE, l_con));
                        list.add(new VarInsnNode(ALOAD, 10));
                        list.add(new MethodInsnNode(INVOKEINTERFACE, "net/minecraft/block/state/IBlockState", getName("getBlock", "func_177230_c"), "()Lnet/minecraft/block/Block;", true));
                        list.add(new TypeInsnNode(INSTANCEOF, "net/minecraft/block/BlockLiquid"));
                        list.add(new JumpInsnNode(IFEQ, l_con));
                        list.add(new VarInsnNode(DLOAD, 7));
                        list.add(new InsnNode(D2F));
                        list.add(new VarInsnNode(ALOAD, 9));
                        list.add(new MethodInsnNode(INVOKEVIRTUAL, "net/minecraft/util/math/BlockPos", getName("getY", "func_177956_o"), "()I", false));
                        list.add(new InsnNode(I2F));
                        list.add(new VarInsnNode(ALOAD, 10));
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new FieldInsnNode(GETFIELD, cls.name, getName("world", "field_70170_p"), "Lnet/minecraft/world/World;"));
                        list.add(new VarInsnNode(ALOAD, 9));
                        list.add(new MethodInsnNode(INVOKESTATIC, "net/minecraft/block/BlockLiquid", getName("getBlockLiquidHeight", "func_190973_f"), "(Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/util/math/BlockPos;)F", false));
                        list.add(new InsnNode(FADD));
                        list.add(new InsnNode(FCONST_1));
                        list.add(new LdcInsnNode(9F));
                        list.add(new InsnNode(FDIV));
                        list.add(new InsnNode(FADD));
                        list.add(new InsnNode(FCMPG));
                        list.add(new JumpInsnNode(IFGT, l_con));
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new MethodInsnNode(INVOKEVIRTUAL, cls.name, "applyFloatMotion", "()V", false));
                        list.add(new JumpInsnNode(GOTO, ((JumpInsnNode) node.getNext()).label));
                        list.add(l_con);
                        list.add(new FrameNode(F_SAME, 0, null, 0, null));
                        method.instructions.insertBefore(node.getPrevious(), list);
                    }
                }
            }
        }
        return write(cls, ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
    }
}
