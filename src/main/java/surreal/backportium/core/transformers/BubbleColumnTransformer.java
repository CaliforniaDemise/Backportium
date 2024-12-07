package surreal.backportium.core.transformers;

import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.tree.*;

import java.util.Iterator;

public class BubbleColumnTransformer extends BasicTransformer {

    /**
     * Handle ambient and going inside sounds
     **/
    public static byte[] transformEntityPlayerSP(ClassNode cls) {
        { // inColumn |  0 - None / 1 - Upwards / 2 -
            FieldVisitor inColum = cls.visitField(ACC_PROTECTED, "inColumn", "I", null, 0);
            inColum.visitEnd();
        }
        for (MethodNode method : cls.methods) {
            if (method.name.equals(getName("onUpdate", "func_70071_h_"))) {
                AbstractInsnNode node = method.instructions.getLast();
                while (node.getOpcode() != RETURN) node = node.getPrevious();
                InsnList list = new InsnList();
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new FieldInsnNode(GETFIELD, cls.name, "inColumn", "I"));
                list.add(clientHook("EntityPlayerSP$handleBubbleColumn", "(Lnet/minecraft/client/entity/EntityPlayerSP;I)I"));
                list.add(new FieldInsnNode(PUTFIELD, cls.name, "inColumn", "I"));
                method.instructions.insertBefore(node, list);
            }
        }
        return write(cls);
    }

    /**
     * Make EntityThrowable raytrace liquid blocks.
     **/
    public static byte[] transformEntityThrowable(byte[] basicClass) {
        ClassNode cls = read(basicClass);
        for (MethodNode method : cls.methods) {
            if (method.name.equals(getName("onUpdate", "func_70071_h_"))) {
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                boolean check = false;
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (!check && node.getOpcode() == ALOAD) {
                        InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new MethodInsnNode(INVOKESTATIC, "net/minecraft/entity/EntityList", getName("getKey", "func_191301_a"), "(Lnet/minecraft/entity/Entity;)Lnet/minecraft/util/ResourceLocation;", false));
                        list.add(new MethodInsnNode(INVOKEVIRTUAL, "net/minecraft/util/ResourceLocation", getName("getNamespace", "func_110624_b"), "()Ljava/lang/String;", false));
                        list.add(new LdcInsnNode("minecraft"));
                        list.add(new MethodInsnNode(INVOKEVIRTUAL, "java/lang/Object", "equals", "(Ljava/lang/Object;)Z", false));
                        LabelNode l_con = new LabelNode();
                        list.add(new JumpInsnNode(IFEQ, l_con));
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new MethodInsnNode(INVOKEVIRTUAL, cls.name, getName("doBlockCollisions", "func_145775_I"), "()V", false));
                        list.add(l_con);
                        list.add(new FrameNode(F_SAME, 0, null, 0, null));
                        method.instructions.insertBefore(node, list);
                        check = true;
                    }
                    if (node.getOpcode() == INVOKEVIRTUAL && ((MethodInsnNode) node).name.equals(getName("rayTraceBlocks", "func_147447_a"))) {
                        InsnList list = new InsnList();
                        list.add(new InsnNode(ICONST_0));
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new MethodInsnNode(INVOKESTATIC, "net/minecraft/entity/EntityList", getName("getKey", "func_191301_a"), "(Lnet/minecraft/entity/Entity;)Lnet/minecraft/util/ResourceLocation;", false));
                        list.add(new MethodInsnNode(INVOKEVIRTUAL, "net/minecraft/util/ResourceLocation", getName("getNamespace", "func_110624_b"), "()Ljava/lang/String;", false));
                        list.add(new LdcInsnNode("minecraft"));
                        list.add(new MethodInsnNode(INVOKEVIRTUAL, "java/lang/Object", "equals", "(Ljava/lang/Object;)Z", false));
                        list.add(new InsnNode(ICONST_0));
                        method.instructions.insertBefore(node, list);
                        ((MethodInsnNode) node).desc = "(Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Vec3d;ZZZ)Lnet/minecraft/util/math/RayTraceResult;";
                        break;
                    }
                }
                break;
            }
        }
        return write(cls);
    }

    public static byte[] transformSoulSand(byte[] basicClass) {
        ClassNode cls = read(basicClass);
        { // placeBubbleColumn
            MethodVisitor m = cls.visitMethod(ACC_PUBLIC | ACC_STATIC, "placeBubbleColumn", "(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)V", null, null);
            m.visitVarInsn(ALOAD, 0);
            m.visitVarInsn(ALOAD, 1);
            m.visitMethodInsn(INVOKEVIRTUAL, "net/minecraft/util/math/BlockPos", getName("up", "func_177984_a"), "()Lnet/minecraft/util/math/BlockPos;", false);
            m.visitMethodInsn(INVOKEVIRTUAL, "net/minecraft/world/World", getName("getBlockState", "func_180495_p"), "(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/state/IBlockState;", false);
            m.visitMethodInsn(INVOKEINTERFACE, "net/minecraft/block/state/IBlockState", getName("getMaterial", "func_185904_a"), "()Lnet/minecraft/block/material/Material;", true);
            m.visitFieldInsn(GETSTATIC, "net/minecraft/block/material/Material", getName("WATER", "field_151586_h"), "Lnet/minecraft/block/material/Material;");
            Label l_con = new Label();
            m.visitJumpInsn(IF_ACMPNE, l_con);
            m.visitVarInsn(ALOAD, 0);
            m.visitVarInsn(ALOAD, 1);
            m.visitMethodInsn(INVOKEVIRTUAL, "net/minecraft/util/math/BlockPos", getName("up", "func_177984_a"), "()Lnet/minecraft/util/math/BlockPos;", false);
            m.visitFieldInsn(GETSTATIC, "surreal/backportium/block/ModBlocks", "BUBBLE_COLUMN", "Lnet/minecraft/block/Block;");
            m.visitInsn(ICONST_1);
            m.visitMethodInsn(INVOKEVIRTUAL, "net/minecraft/block/Block", getName("getStateFromMeta", "func_176203_a"), "(I)Lnet/minecraft/block/state/IBlockState;", false);
            m.visitMethodInsn(INVOKEVIRTUAL, "net/minecraft/world/World", getName("setBlockState", "func_175656_a"), "(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;)Z", false);
            m.visitInsn(RETURN);
            m.visitLabel(l_con);
            m.visitFrame(F_SAME, 0, null, 0, null);
            m.visitInsn(RETURN);
        }
        { // tickRate
            MethodVisitor m = cls.visitMethod(ACC_PUBLIC, getName("tickRate", "func_149738_a"), "(Lnet/minecraft/world/World;)I", null, null);
            m.visitIntInsn(BIPUSH, 20);
            m.visitInsn(IRETURN);
        }
        { // updateTick
            MethodVisitor m = cls.visitMethod(ACC_PUBLIC, getName("updateTick", "func_180650_b"), "(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;Ljava/util/Random;)V", null, null);
            m.visitVarInsn(ALOAD, 1);
            m.visitVarInsn(ALOAD, 2);
            m.visitMethodInsn(INVOKESTATIC, cls.name, "placeBubbleColumn", "(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)V", false);
            m.visitInsn(RETURN);
        }
        { // onBlockAdded
            MethodVisitor m = cls.visitMethod(ACC_PUBLIC, getName("onBlockAdded", "func_176213_c"), "(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;)V", null, null);
            m.visitVarInsn(ALOAD, 1);
            m.visitVarInsn(ALOAD, 2);
            m.visitVarInsn(ALOAD, 0);
            m.visitVarInsn(ALOAD, 0);
            m.visitVarInsn(ALOAD, 1);
            m.visitMethodInsn(INVOKEVIRTUAL, cls.name, getName("tickRate", "func_149738_a"), "(Lnet/minecraft/world/World;)I", false);
            m.visitMethodInsn(INVOKEVIRTUAL, "net/minecraft/world/World", getName("scheduleUpdate", "func_175684_a"), "(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/Block;I)V", false);
            m.visitInsn(RETURN);
        }
        { // neighborChanged
            MethodVisitor m = cls.visitMethod(ACC_PUBLIC, getName("neighborChanged", "func_189540_a"), "(Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/Block;Lnet/minecraft/util/math/BlockPos;)V", null, null);
            m.visitVarInsn(ALOAD, 2);
            m.visitVarInsn(ALOAD, 3);
            m.visitMethodInsn(INVOKEVIRTUAL, "net/minecraft/util/math/BlockPos", getName("up", "func_177984_a"), "()Lnet/minecraft/util/math/BlockPos;", false);
            m.visitMethodInsn(INVOKEVIRTUAL, "net/minecraft/world/World", getName("getBlockState", "func_180495_p"), "(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/state/IBlockState;", false);
            m.visitMethodInsn(INVOKEINTERFACE, "net/minecraft/block/state/IBlockState", getName("getMaterial", "func_185904_a"), "()Lnet/minecraft/block/material/Material;", true);
            m.visitFieldInsn(GETSTATIC, "net/minecraft/block/material/Material", getName("WATER", "field_151586_h"), "Lnet/minecraft/block/material/Material;");
            Label l_con = new Label();
            m.visitJumpInsn(IF_ACMPNE, l_con);
            m.visitVarInsn(ALOAD, 2);
            m.visitVarInsn(ALOAD, 3);
            m.visitVarInsn(ALOAD, 0);
            m.visitVarInsn(ALOAD, 0);
            m.visitVarInsn(ALOAD, 2);
            m.visitMethodInsn(INVOKEVIRTUAL, cls.name, getName("tickRate", "func_149738_a"), "(Lnet/minecraft/world/World;)I", false);
            m.visitMethodInsn(INVOKEVIRTUAL, "net/minecraft/world/World", getName("scheduleUpdate", "func_175684_a"), "(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/Block;I)V", false);
            m.visitLabel(l_con);
            m.visitFrame(F_SAME, 0, null, 0, null);
            m.visitInsn(RETURN);
        }
        return write(cls);
    }

    public static byte[] transformBlockMagma(byte[] basicClass) {
        ClassNode cls = read(basicClass);
        Iterator<MethodNode> methods = cls.methods.iterator();
        while (methods.hasNext()) {
            MethodNode method = methods.next();
            if (method.name.equals("<init>")) {
                AbstractInsnNode node = method.instructions.getLast();
                while (node.getOpcode() != POP) node = node.getPrevious();
                for (int i = 0; i < 4; i++) {
                    node = node.getPrevious();
                    method.instructions.remove(node.getNext());
                }
            }
            else if (method.name.equals(getName("updateTick", "func_180650_b"))) {
                methods.remove();
                break;
            }
        }
        { // placeBubbleColumn
            MethodVisitor m = cls.visitMethod(ACC_PUBLIC | ACC_STATIC, "placeBubbleColumn", "(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)V", null, null);
            m.visitVarInsn(ALOAD, 0);
            m.visitVarInsn(ALOAD, 1);
            m.visitMethodInsn(INVOKEVIRTUAL, "net/minecraft/util/math/BlockPos", getName("up", "func_177984_a"), "()Lnet/minecraft/util/math/BlockPos;", false);
            m.visitMethodInsn(INVOKEVIRTUAL, "net/minecraft/world/World", getName("getBlockState", "func_180495_p"), "(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/state/IBlockState;", false);
            m.visitMethodInsn(INVOKEINTERFACE, "net/minecraft/block/state/IBlockState", getName("getMaterial", "func_185904_a"), "()Lnet/minecraft/block/material/Material;", true);
            m.visitFieldInsn(GETSTATIC, "net/minecraft/block/material/Material", getName("WATER", "field_151586_h"), "Lnet/minecraft/block/material/Material;");
            Label l_con = new Label();
            m.visitJumpInsn(IF_ACMPNE, l_con);
            m.visitVarInsn(ALOAD, 0);
            m.visitVarInsn(ALOAD, 1);
            m.visitMethodInsn(INVOKEVIRTUAL, "net/minecraft/util/math/BlockPos", getName("up", "func_177984_a"), "()Lnet/minecraft/util/math/BlockPos;", false);
            m.visitFieldInsn(GETSTATIC, "surreal/backportium/block/ModBlocks", "BUBBLE_COLUMN", "Lnet/minecraft/block/Block;");
            m.visitMethodInsn(INVOKEVIRTUAL, "net/minecraft/block/Block", getName("getDefaultState", "func_176223_P"), "()Lnet/minecraft/block/state/IBlockState;", false);
            m.visitMethodInsn(INVOKEVIRTUAL, "net/minecraft/world/World", getName("setBlockState", "func_175656_a"), "(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;)Z", false);
            m.visitInsn(RETURN);
            m.visitLabel(l_con);
            m.visitFrame(F_SAME, 0, null, 0, null);
            m.visitInsn(RETURN);
        }
        { // tickRate
            MethodVisitor m = cls.visitMethod(ACC_PUBLIC, getName("tickRate", "func_149738_a"), "(Lnet/minecraft/world/World;)I", null, null);
            m.visitIntInsn(BIPUSH, 20);
            m.visitInsn(IRETURN);
        }
        { // updateTick
            MethodVisitor m = cls.visitMethod(ACC_PUBLIC, getName("updateTick", "func_180650_b"), "(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;Ljava/util/Random;)V", null, null);
            m.visitVarInsn(ALOAD, 1);
            m.visitVarInsn(ALOAD, 2);
            m.visitMethodInsn(INVOKESTATIC, cls.name, "placeBubbleColumn", "(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)V", false);
            m.visitInsn(RETURN);
        }
        { // onBlockAdded
            MethodVisitor m = cls.visitMethod(ACC_PUBLIC, getName("onBlockAdded", "func_176213_c"), "(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;)V", null, null);
            m.visitVarInsn(ALOAD, 1);
            m.visitVarInsn(ALOAD, 2);
            m.visitVarInsn(ALOAD, 0);
            m.visitVarInsn(ALOAD, 0);
            m.visitVarInsn(ALOAD, 1);
            m.visitMethodInsn(INVOKEVIRTUAL, cls.name, getName("tickRate", "func_149738_a"), "(Lnet/minecraft/world/World;)I", false);
            m.visitMethodInsn(INVOKEVIRTUAL, "net/minecraft/world/World", getName("scheduleUpdate", "func_175684_a"), "(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/Block;I)V", false);
            m.visitInsn(RETURN);
        }
        { // neighborChanged
            MethodVisitor m = cls.visitMethod(ACC_PUBLIC, getName("neighborChanged", "func_189540_a"), "(Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/Block;Lnet/minecraft/util/math/BlockPos;)V", null, null);
            m.visitVarInsn(ALOAD, 2);
            m.visitVarInsn(ALOAD, 3);
            m.visitMethodInsn(INVOKEVIRTUAL, "net/minecraft/util/math/BlockPos", getName("up", "func_177984_a"), "()Lnet/minecraft/util/math/BlockPos;", false);
            m.visitMethodInsn(INVOKEVIRTUAL, "net/minecraft/world/World", getName("getBlockState", "func_180495_p"), "(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/state/IBlockState;", false);
            m.visitMethodInsn(INVOKEINTERFACE, "net/minecraft/block/state/IBlockState", getName("getMaterial", "func_185904_a"), "()Lnet/minecraft/block/material/Material;", true);
            m.visitFieldInsn(GETSTATIC, "net/minecraft/block/material/Material", getName("WATER", "field_151586_h"), "Lnet/minecraft/block/material/Material;");
            Label l_con = new Label();
            m.visitJumpInsn(IF_ACMPNE, l_con);
            m.visitVarInsn(ALOAD, 2);
            m.visitVarInsn(ALOAD, 3);
            m.visitVarInsn(ALOAD, 0);
            m.visitVarInsn(ALOAD, 0);
            m.visitVarInsn(ALOAD, 2);
            m.visitMethodInsn(INVOKEVIRTUAL, cls.name, getName("tickRate", "func_149738_a"), "(Lnet/minecraft/world/World;)I", false);
            m.visitMethodInsn(INVOKEVIRTUAL, "net/minecraft/world/World", getName("scheduleUpdate", "func_175684_a"), "(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/Block;I)V", false);
            m.visitLabel(l_con);
            m.visitFrame(F_SAME, 0, null, 0, null);
            m.visitInsn(RETURN);
        }
        return write(cls);
    }
}
