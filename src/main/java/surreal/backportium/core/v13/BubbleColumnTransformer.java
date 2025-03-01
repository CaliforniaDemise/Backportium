package surreal.backportium.core.v13;

import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;
import surreal.backportium.core.transformers.Transformer;

import java.util.Iterator;

class BubbleColumnTransformer extends Transformer {

    /**
     * Handle ambient and going inside sounds
     **/
    protected static byte[] transformEntityPlayerSP(byte[] basicClass) {
        ClassNode cls = read(basicClass);
        { // inColumn |  0 - None / 1 - Upwards / 2 - Downwards
            FieldVisitor inColum = cls.visitField(ACC_PROTECTED, "inColumn", "I", null, 0);
            inColum.visitEnd();
        }
        { // BP$handleBubbleColumn
            MethodVisitor m = cls.visitMethod(ACC_PRIVATE, "BP$handleBubbleColumn", "(I)I", null, null);
            m.visitVarInsn(ALOAD, 0);
            m.visitFieldInsn(GETFIELD, cls.name, getName("world", "field_70170_p"), "Lnet/minecraft/world/World;");
            m.visitVarInsn(ASTORE, 2);
            m.visitTypeInsn(NEW, "net/minecraft/util/math/BlockPos");
            m.visitInsn(DUP);
            m.visitVarInsn(ALOAD, 0);
            m.visitMethodInsn(INVOKESPECIAL, "net/minecraft/util/math/BlockPos", "<init>", "(Lnet/minecraft/entity/Entity;)V", false);
            m.visitInsn(DCONST_0);
            m.visitVarInsn(ALOAD, 0);
            m.visitMethodInsn(INVOKEVIRTUAL, cls.name, getName("getEyeHeight", "func_70047_e"), "()F", false);
            m.visitInsn(F2D);
            m.visitInsn(DCONST_0);
            m.visitMethodInsn(INVOKEVIRTUAL, "net/minecraft/util/math/BlockPos", getName("add", "func_177963_a"), "(DDD)Lnet/minecraft/util/math/BlockPos;", false);
            m.visitVarInsn(ASTORE, 3);
            m.visitVarInsn(ALOAD, 2);
            m.visitVarInsn(ALOAD, 3);
            m.visitMethodInsn(INVOKEVIRTUAL, "net/minecraft/world/World", getName("getBlockState", "func_180495_p"), "(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/state/IBlockState;", false);
            m.visitVarInsn(ASTORE, 4);
            m.visitVarInsn(ALOAD, 4);
            m.visitMethodInsn(INVOKEINTERFACE, "net/minecraft/block/state/IBlockState", getName("getBlock", "func_177230_c"), "()Lnet/minecraft/block/Block;", true);
            m.visitFieldInsn(GETSTATIC, "surreal/backportium/block/ModBlocks", "BUBBLE_COLUMN", "Lnet/minecraft/block/Block;"); /// {@link surreal.backportium.block.ModBlocks.BUBBLE_COLUMN}
            Label l_con_column = new Label();
            m.visitJumpInsn(IF_ACMPNE, l_con_column);
            m.visitVarInsn(ALOAD, 4);
            m.visitFieldInsn(GETSTATIC, "surreal/backportium/block/v13/BlockBubbleColumn", "DRAG", "Lnet/minecraft/block/properties/PropertyBool;");
            m.visitMethodInsn(INVOKEINTERFACE, "net/minecraft/block/state/IBlockState", getName("getValue", "func_177229_b"), "(Lnet/minecraft/block/properties/IProperty;)Ljava/lang/Comparable;", true);
            m.visitTypeInsn(CHECKCAST, "java/lang/Boolean");
            m.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Boolean", "booleanValue", "()Z", false);
            m.visitVarInsn(ISTORE, 5);
            m.visitVarInsn(ILOAD, 5);
            Label l_con_upwards = new Label();
            m.visitJumpInsn(IFEQ, l_con_upwards);
            {
                m.visitVarInsn(ILOAD, 1);
                m.visitInsn(ICONST_1);
                Label l_con_I = new Label();
                m.visitJumpInsn(IF_ICMPEQ, l_con_I);
                m.visitVarInsn(ALOAD, 2);
                m.visitVarInsn(ALOAD, 0);
                m.visitFieldInsn(GETFIELD, cls.name, getName("posX", "field_70165_t"), "D");
                m.visitVarInsn(ALOAD, 0);
                m.visitFieldInsn(GETFIELD, cls.name, getName("posY", "field_70163_u"), "D");
                m.visitVarInsn(ALOAD, 0);
                m.visitFieldInsn(GETFIELD, cls.name, getName("posZ", "field_70161_v"), "D");
                m.visitFieldInsn(GETSTATIC, "surreal/backportium/sound/ModSounds", "BLOCK_BUBBLE_COLUMN_UPWARDS_INSIDE", "Lnet/minecraft/util/SoundEvent;");
                m.visitFieldInsn(GETSTATIC, "net/minecraft/util/SoundCategory", "BLOCKS", "Lnet/minecraft/util/SoundCategory;");
                m.visitLdcInsn(0.7F);
                m.visitInsn(FCONST_1);
                m.visitInsn(ICONST_0);
                m.visitMethodInsn(INVOKEVIRTUAL, "net/minecraft/world/World", getName("playSound", "func_184134_a"), "(DDDLnet/minecraft/util/SoundEvent;Lnet/minecraft/util/SoundCategory;FFZ)V", false);
                m.visitLabel(l_con_I);
                m.visitFrame(F_APPEND, 3, new Object[]{"net/minecraft/world/World", "net/minecraft/util/math/BlockPos", "net/minecraft/block/state/IBlockState"}, 0, null);
            }
            m.visitInsn(ICONST_1);
            m.visitInsn(IRETURN);
            m.visitLabel(l_con_upwards);
            m.visitFrame(F_APPEND, 1, new Object[]{INTEGER}, 0, null);
            {
                m.visitVarInsn(ILOAD, 1);
                m.visitInsn(ICONST_2);
                Label l_con_I = new Label();
                m.visitJumpInsn(IF_ICMPEQ, l_con_I);
                m.visitVarInsn(ALOAD, 2);
                m.visitVarInsn(ALOAD, 0);
                m.visitFieldInsn(GETFIELD, cls.name, getName("posX", "field_70165_t"), "D");
                m.visitVarInsn(ALOAD, 0);
                m.visitFieldInsn(GETFIELD, cls.name, getName("posY", "field_70163_u"), "D");
                m.visitVarInsn(ALOAD, 0);
                m.visitFieldInsn(GETFIELD, cls.name, getName("posZ", "field_70161_v"), "D");
                m.visitFieldInsn(GETSTATIC, "surreal/backportium/sound/ModSounds", "BLOCK_BUBBLE_COLUMN_UPWARDS_INSIDE", "Lnet/minecraft/util/SoundEvent;");
                m.visitFieldInsn(GETSTATIC, "net/minecraft/util/SoundCategory", "BLOCKS", "Lnet/minecraft/util/SoundCategory;");
                m.visitLdcInsn(0.7F);
                m.visitInsn(FCONST_1);
                m.visitInsn(ICONST_0);
                m.visitMethodInsn(INVOKEVIRTUAL, "net/minecraft/world/World", getName("playSound", "func_184134_a"), "(DDDLnet/minecraft/util/SoundEvent;Lnet/minecraft/util/SoundCategory;FFZ)V", false);
                m.visitLabel(l_con_I);
                m.visitFrame(F_CHOP, 1, null, 0, null);
            }
            m.visitInsn(ICONST_2);
            m.visitInsn(IRETURN);
            m.visitLabel(l_con_column);
            m.visitFrame(F_SAME, 0, null, 0, null);
            m.visitInsn(ICONST_0);
            m.visitInsn(IRETURN);
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
                list.add(new MethodInsnNode(INVOKEVIRTUAL, cls.name, "BP$handleBubbleColumn", "(I)I", false));
                list.add(new FieldInsnNode(PUTFIELD, cls.name, "inColumn", "I"));
                method.instructions.insertBefore(node, list);
            }
        }
        return write(cls);
    }

    /**
     * Make EntityThrowable raytrace liquid blocks.
     **/
    protected static byte[] transformEntityThrowable(byte[] basicClass) {
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

    protected static byte[] transformEntityBoat(byte[] basicClass) {
        ClassNode cls = read(basicClass);
        {
            cls.visitField(ACC_PRIVATE | ACC_STATIC | ACC_FINAL, "BOAT_ROCKING_TICKS", "Lnet/minecraft/network/datasync/DataParameter;", null, null);
            cls.visitField(ACC_PRIVATE, "rocking", "Z", null, false);
            cls.visitField(ACC_PRIVATE, "downwards", "Z", null, false);
            cls.visitField(ACC_PRIVATE, "rockingIntensity", "F", null, 0.0F);
            cls.visitField(ACC_PRIVATE, "rockingAngle", "F", null, 0.0F);
            cls.visitField(ACC_PRIVATE, "prevRockingAngle", "F", null, 0.0F);
        }
        { // getRockingTicks
            MethodVisitor m = cls.visitMethod(ACC_PUBLIC, "getRockingTicks", "()I", null, null);
            m.visitVarInsn(ALOAD, 0);
            m.visitFieldInsn(GETFIELD, cls.name, getName("dataManager", "field_70180_af"), "Lnet/minecraft/network/datasync/EntityDataManager;");
            m.visitFieldInsn(GETSTATIC, cls.name, "BOAT_ROCKING_TICKS", "Lnet/minecraft/network/datasync/DataParameter;");
            m.visitMethodInsn(INVOKEVIRTUAL, "net/minecraft/network/datasync/EntityDataManager", getName("get", "func_187225_a"), "(Lnet/minecraft/network/datasync/DataParameter;)Ljava/lang/Object;", false);
            m.visitTypeInsn(CHECKCAST, "java/lang/Integer");
            m.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I", false);
            m.visitInsn(IRETURN);
        }
        { // setRockingTicks
            MethodVisitor m = cls.visitMethod(ACC_PUBLIC, "setRockingTicks", "(I)V", null, null);
            m.visitVarInsn(ALOAD, 0);
            m.visitFieldInsn(GETFIELD, cls.name, getName("dataManager", "field_70180_af"), "Lnet/minecraft/network/datasync/EntityDataManager;");
            m.visitFieldInsn(GETSTATIC, cls.name, "BOAT_ROCKING_TICKS", "Lnet/minecraft/network/datasync/DataParameter;");
            m.visitVarInsn(ILOAD, 1);
            m.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
            m.visitMethodInsn(INVOKEVIRTUAL, "net/minecraft/network/datasync/EntityDataManager", getName("set", "func_187227_b"), "(Lnet/minecraft/network/datasync/DataParameter;Ljava/lang/Object;)V", false);
            m.visitInsn(RETURN);
        }
        { // getRockingAngle
            MethodVisitor m = cls.visitMethod(ACC_PUBLIC, "getRockingAngle", "(F)F", null, null);
            m.visitVarInsn(ALOAD, 0);
            m.visitFieldInsn(GETFIELD, cls.name, "rockingAngle", "F");
            m.visitVarInsn(ALOAD, 0);
            m.visitFieldInsn(GETFIELD, cls.name, "prevRockingAngle", "F");
            m.visitInsn(FSUB);
            m.visitVarInsn(FLOAD, 1);
            m.visitInsn(FMUL);
            m.visitVarInsn(ALOAD, 0);
            m.visitFieldInsn(GETFIELD, cls.name, "prevRockingAngle", "F");
            m.visitInsn(FADD);
            m.visitInsn(FRETURN);
        }
        {
            MethodVisitor m = cls.visitMethod(ACC_PRIVATE, "BP$inBubbleColumn", "()I", null, null);
            m.visitTypeInsn(NEW, "net/minecraft/util/math/BlockPos");
            m.visitInsn(DUP);
            m.visitVarInsn(ALOAD, 0);
            m.visitMethodInsn(INVOKESPECIAL, "net/minecraft/util/math/BlockPos", "<init>", "(Lnet/minecraft/entity/Entity;)V", false);
            m.visitVarInsn(ASTORE, 1);
            m.visitVarInsn(ALOAD, 0);
            m.visitFieldInsn(GETFIELD, cls.name, getName("world", "field_70170_p"), "Lnet/minecraft/world/World;");
            m.visitVarInsn(ALOAD, 1);
            m.visitMethodInsn(INVOKEVIRTUAL, "net/minecraft/world/World", getName("getBlockState", "func_180495_p"), "(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/state/IBlockState;", false);
            m.visitVarInsn(ASTORE, 2);
            m.visitVarInsn(ALOAD, 2);
            m.visitMethodInsn(INVOKEINTERFACE, "net/minecraft/block/state/IBlockState", getName("getBlock", "func_177230_c"), "()Lnet/minecraft/block/Block;", true);
            m.visitFieldInsn(GETSTATIC, "surreal/backportium/block/ModBlocks", "BUBBLE_COLUMN", "Lnet/minecraft/block/Block;");
            Label l_con_bubbleColumn = new Label();
            m.visitJumpInsn(IF_ACMPNE, l_con_bubbleColumn);
            m.visitVarInsn(ALOAD, 0);
            m.visitFieldInsn(GETFIELD, cls.name, getName("world", "field_70170_p"), "Lnet/minecraft/world/World;");
            m.visitVarInsn(ALOAD, 1);
            m.visitMethodInsn(INVOKEVIRTUAL, "net/minecraft/util/math/BlockPos", getName("up", "func_177984_a"), "()Lnet/minecraft/util/math/BlockPos;", false);
            m.visitMethodInsn(INVOKEVIRTUAL, "net/minecraft/world/World", getName("isAirBlock", "func_175623_d"), "(Lnet/minecraft/util/math/BlockPos;)Z", false);
            m.visitJumpInsn(IFEQ, l_con_bubbleColumn);
            m.visitVarInsn(ALOAD, 2);
            m.visitFieldInsn(GETSTATIC, "surreal/backportium/block/v13/BlockBubbleColumn", "DRAG", "Lnet/minecraft/block/properties/PropertyBool;");
            m.visitMethodInsn(INVOKEINTERFACE, "net/minecraft/block/state/IBlockState", getName("getValue", "func_177229_b"), "(Lnet/minecraft/block/properties/IProperty;)Ljava/lang/Comparable;", true);
            m.visitTypeInsn(CHECKCAST, "java/lang/Boolean");
            m.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Boolean", "booleanValue", "()Z", false);
            Label l_con_upwards = new Label();
            m.visitJumpInsn(IFEQ, l_con_upwards);
            m.visitInsn(ICONST_1);
            m.visitInsn(IRETURN);
            m.visitLabel(l_con_upwards);
            m.visitFrame(F_SAME, 0, null, 0, null);
            m.visitInsn(ICONST_2);
            m.visitInsn(IRETURN);
            m.visitLabel(l_con_bubbleColumn);
            m.visitFrame(F_APPEND, 2, new Object[]{"net/minecraft/util/math/BlockPos", "net/minecraft/block/state/IBlockState"}, 0, null);
            m.visitInsn(ICONST_0);
            m.visitInsn(IRETURN);
        }
        { // BP$anyPlayerRiding
            MethodVisitor m = cls.visitMethod(ACC_PUBLIC, "BP$anyPlayerRiding", "()Z", null, null);
            m.visitInsn(ICONST_0);
            m.visitVarInsn(ISTORE, 1);
            Label l_con_for = new Label();
            m.visitLabel(l_con_for);
            m.visitFrame(F_APPEND, 1, new Object[]{INTEGER}, 0, null);
            m.visitVarInsn(ILOAD, 1);
            m.visitVarInsn(ALOAD, 0);
            m.visitMethodInsn(INVOKEVIRTUAL, cls.name, getName("getPassengers", "func_184188_bt"), "()Ljava/util/List;", false);
            m.visitMethodInsn(INVOKEINTERFACE, "java/util/List", "size", "()I", true);
            Label l_con_num = new Label();
            m.visitJumpInsn(IF_ICMPEQ, l_con_num);

            m.visitVarInsn(ALOAD, 0);
            m.visitMethodInsn(INVOKEVIRTUAL, cls.name, getName("getPassengers", "func_184188_bt"), "()Ljava/util/List;", false);
            m.visitVarInsn(ILOAD, 1);
            m.visitMethodInsn(INVOKEINTERFACE, "java/util/List", "get", "(I)Ljava/lang/Object;", true);
            m.visitTypeInsn(INSTANCEOF, "net/minecraft/entity/player/EntityPlayer");
            Label l_con = new Label();
            m.visitJumpInsn(IFEQ, l_con);
            m.visitInsn(ICONST_1);
            m.visitInsn(IRETURN);
            m.visitLabel(l_con);
            m.visitFrame(F_SAME, 0, null, 0, null);

            m.visitVarInsn(ILOAD, 1);
            m.visitInsn(ICONST_1);
            m.visitInsn(IADD);
            m.visitVarInsn(ISTORE, 1);

            m.visitJumpInsn(GOTO, l_con_for);

            m.visitLabel(l_con_num);
            m.visitFrame(F_SAME, 0, null, 0, null);
            m.visitInsn(ICONST_0);
            m.visitInsn(IRETURN);
        }
        for (MethodNode method : cls.methods) {
            if (method.name.equals(getName("entityInit", "func_70088_a"))) {
                AbstractInsnNode node = method.instructions.getLast();
                while (node.getOpcode() != RETURN) node = node.getPrevious();
                InsnList list = new InsnList();
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new FieldInsnNode(GETFIELD, cls.name, getName("dataManager", "field_70180_af"), "Lnet/minecraft/network/datasync/EntityDataManager;"));
                list.add(new FieldInsnNode(GETSTATIC, cls.name, "BOAT_ROCKING_TICKS", "Lnet/minecraft/network/datasync/DataParameter;"));
                list.add(new InsnNode(ICONST_0));
                list.add(new MethodInsnNode(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false));
                list.add(new MethodInsnNode(INVOKEVIRTUAL, "net/minecraft/network/datasync/EntityDataManager", getName("register", "func_187214_a"), "(Lnet/minecraft/network/datasync/DataParameter;Ljava/lang/Object;)V", false));
                method.instructions.insertBefore(node, list);
            }
            else if (method.name.equals(getName("onUpdate", "func_70071_h_"))) {
                AbstractInsnNode node = method.instructions.getLast();
                while (node.getOpcode() != RETURN) node = node.getPrevious();
                InsnList list = new InsnList();
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new FieldInsnNode(GETFIELD, cls.name, getName("world", "field_70170_p"), "Lnet/minecraft/world/World;"));
                list.add(new FieldInsnNode(GETFIELD, "net/minecraft/world/World", getName("isRemote", "field_72995_K"), "Z"));
                LabelNode l_con_remote = new LabelNode();
                list.add(new JumpInsnNode(IFEQ, l_con_remote));
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new MethodInsnNode(INVOKEVIRTUAL, cls.name, "getRockingTicks", "()I", false));
                list.add(new InsnNode(ICONST_0));
                LabelNode l_con_rockTicks = new LabelNode();
                LabelNode l_con_rockTicks2 = new LabelNode();
                list.add(new JumpInsnNode(IF_ICMPLE, l_con_rockTicks));
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new FieldInsnNode(GETFIELD, cls.name, "rockingIntensity", "F"));
                list.add(new LdcInsnNode(0.05F));
                list.add(new InsnNode(FADD));
                list.add(new FieldInsnNode(PUTFIELD, cls.name, "rockingIntensity", "F"));
                list.add(new JumpInsnNode(GOTO, l_con_rockTicks2));
                list.add(l_con_rockTicks);
                list.add(new FrameNode(F_SAME, 0, null, 0, null));
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new FieldInsnNode(GETFIELD, cls.name, "rockingIntensity", "F"));
                list.add(new LdcInsnNode(-0.1F));
                list.add(new InsnNode(FADD));
                list.add(new FieldInsnNode(PUTFIELD, cls.name, "rockingIntensity", "F"));
                list.add(l_con_rockTicks2);
                list.add(new FrameNode(F_SAME, 0, null, 0, null));
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new FieldInsnNode(GETFIELD, cls.name, "rockingIntensity", "F"));
                list.add(new InsnNode(FCONST_0));
                list.add(new InsnNode(FCONST_1));
                list.add(new MethodInsnNode(INVOKESTATIC, "net/minecraft/util/math/MathHelper", "clamp", "(FFF)F", false));
                list.add(new FieldInsnNode(PUTFIELD, cls.name, "rockingIntensity", "F"));
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new FieldInsnNode(GETFIELD, cls.name, "rockingAngle", "F"));
                list.add(new FieldInsnNode(PUTFIELD, cls.name, "prevRockingAngle", "F"));
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new LdcInsnNode(10.0F));
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new FieldInsnNode(GETFIELD, cls.name, getName("ticksExisted", "field_70173_aa"), "I"));
                list.add(new InsnNode(I2F));
                list.add(new LdcInsnNode(0.5F));
                list.add(new InsnNode(FMUL));
                list.add(new InsnNode(F2D));
                list.add(new MethodInsnNode(INVOKESTATIC, "java/lang/Math", "sin", "(D)D", false));
                list.add(new InsnNode(D2F));
                list.add(new InsnNode(FMUL));
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new FieldInsnNode(GETFIELD, cls.name, "rockingIntensity", "F"));
                list.add(new InsnNode(FMUL));
                list.add(new FieldInsnNode(PUTFIELD, cls.name, "rockingAngle", "F"));
                list.add(l_con_remote);
                list.add(new FrameNode(F_SAME, 0, null, 0, null));
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new FieldInsnNode(GETFIELD, cls.name, "rocking", "Z"));
                LabelNode l_con_nrocking = new LabelNode();
                list.add(new JumpInsnNode(IFNE, l_con_nrocking));
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new InsnNode(ICONST_0));
                list.add(new MethodInsnNode(INVOKEVIRTUAL, cls.name, "setRockingTicks", "(I)V", false));
                list.add(l_con_nrocking);
                list.add(new FrameNode(F_SAME, 0, null, 0, null));
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new MethodInsnNode(INVOKEVIRTUAL, cls.name, "getRockingTicks", "()I", false));
                list.add(new InsnNode(ICONST_0));
                LabelNode l_con_0BT = new LabelNode();
                list.add(new JumpInsnNode(IF_ICMPLE, l_con_0BT));
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new MethodInsnNode(INVOKEVIRTUAL, cls.name, "getRockingTicks", "()I", false));
                list.add(new InsnNode(ICONST_1));
                list.add(new InsnNode(ISUB));
                list.add(new MethodInsnNode(INVOKEVIRTUAL, cls.name, "setRockingTicks", "(I)V", false));
                list.add(new LdcInsnNode(60));
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new MethodInsnNode(INVOKEVIRTUAL, cls.name, "getRockingTicks", "()I", false));
                list.add(new InsnNode(ISUB));
                list.add(new InsnNode(ICONST_1));
                list.add(new InsnNode(ISUB));
                list.add(new InsnNode(ICONST_0));
                LabelNode l_con_0BT2 = new LabelNode();
                list.add(new JumpInsnNode(IF_ICMPLE, l_con_0BT2));
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new MethodInsnNode(INVOKEVIRTUAL, cls.name, "getRockingTicks", "()I", false));
                list.add(new InsnNode(ICONST_0));
                list.add(new JumpInsnNode(IF_ICMPNE, l_con_0BT2));
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new InsnNode(ICONST_0));
                list.add(new MethodInsnNode(INVOKEVIRTUAL, cls.name, "setRockingTicks", "(I)V", false));
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new FieldInsnNode(GETFIELD, cls.name, "downwards", "Z"));
                LabelNode l_con_downwards = new LabelNode();
                list.add(new JumpInsnNode(IFEQ, l_con_downwards));
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new FieldInsnNode(GETFIELD, cls.name, getName("motionY", "field_70181_x"), "D"));
                list.add(new LdcInsnNode(0.7D));
                list.add(new InsnNode(DSUB));
                list.add(new FieldInsnNode(PUTFIELD, cls.name, getName("motionY", "field_70181_x"), "D"));
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new MethodInsnNode(INVOKEVIRTUAL, cls.name, getName("removePassengers", "func_184226_ay"), "()V", false));
                LabelNode l_con_playerRiding = new LabelNode();
                list.add(new JumpInsnNode(GOTO, l_con_0BT2));
                list.add(l_con_downwards);
                list.add(new FrameNode(F_SAME, 0, null, 0, null));
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new MethodInsnNode(INVOKEVIRTUAL, cls.name, "BP$anyPlayerRiding", "()Z", false));
                list.add(new JumpInsnNode(IFEQ, l_con_playerRiding));
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new LdcInsnNode(2.7D));
                list.add(new FieldInsnNode(PUTFIELD, cls.name, getName("motionY", "field_70181_x"), "D"));
                list.add(new JumpInsnNode(GOTO, l_con_0BT2));
                list.add(l_con_playerRiding);
                list.add(new FrameNode(F_SAME, 0, null, 0, null));
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new LdcInsnNode(0.6D));
                list.add(new FieldInsnNode(PUTFIELD, cls.name, getName("motionY", "field_70181_x"), "D"));
                list.add(l_con_0BT2);
                list.add(new FrameNode(F_SAME, 0, null, 0, null));
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new InsnNode(ICONST_0));
                list.add(new FieldInsnNode(PUTFIELD, cls.name, "rocking", "Z"));
                list.add(l_con_0BT);
                list.add(new FrameNode(F_SAME, 0, null, 0, null));
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new MethodInsnNode(INVOKEVIRTUAL, cls.name, "BP$inBubbleColumn", "()I", false));
                list.add(new VarInsnNode(ISTORE, 2));
                list.add(new VarInsnNode(ILOAD, 2));
                list.add(new InsnNode(ICONST_0));
                LabelNode l_con_bubbleColumn = new LabelNode();
                list.add(new JumpInsnNode(IF_ICMPEQ, l_con_bubbleColumn));
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new FieldInsnNode(GETFIELD, cls.name, getName("world", "field_70170_p"), "Lnet/minecraft/world/World;"));
                list.add(new FieldInsnNode(GETFIELD, "net/minecraft/world/World", getName("isRemote", "field_72995_K"), "Z"));
                LabelNode l_con_isRemote = new LabelNode();
                list.add(new JumpInsnNode(IFNE, l_con_isRemote));
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new InsnNode(ICONST_1));
                list.add(new FieldInsnNode(PUTFIELD, cls.name, "rocking", "Z"));
                list.add(new VarInsnNode(ILOAD, 2));
                list.add(new InsnNode(ICONST_2));
                LabelNode l_con_down = new LabelNode();
                list.add(new JumpInsnNode(IF_ICMPNE, l_con_down));
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new InsnNode(ICONST_1));
                list.add(new FieldInsnNode(PUTFIELD, cls.name, "downwards", "Z"));
                list.add(l_con_down);
                list.add(new FrameNode(F_SAME, 0, null, 0, null));
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new MethodInsnNode(INVOKEVIRTUAL, cls.name, "getRockingTicks", "()I", false));
                list.add(new InsnNode(ICONST_0));
                list.add(new JumpInsnNode(IF_ICMPNE, l_con_isRemote));
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new LdcInsnNode(60));
                list.add(new MethodInsnNode(INVOKEVIRTUAL, cls.name, "setRockingTicks", "(I)V", false));
                list.add(l_con_isRemote);
                list.add(new FrameNode(F_SAME, 0, null, 0, null));

                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new FieldInsnNode(GETFIELD, cls.name, getName("world", "field_70170_p"), "Lnet/minecraft/world/World;"));
                list.add(new FieldInsnNode(GETSTATIC, "net/minecraft/util/EnumParticleTypes", "WATER_SPLASH", "Lnet/minecraft/util/EnumParticleTypes;"));
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new FieldInsnNode(GETFIELD, cls.name, getName("posX", "field_70165_t"), "D"));
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new FieldInsnNode(GETFIELD, cls.name, getName("rand", "field_70146_Z"), "Ljava/util/Random;"));
                list.add(new MethodInsnNode(INVOKEVIRTUAL, "java/util/Random", "nextDouble", "()D", false));
                list.add(new InsnNode(DADD));
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new FieldInsnNode(GETFIELD, cls.name, getName("posY", "field_70163_u"), "D"));
                list.add(new LdcInsnNode(0.7D));
                list.add(new InsnNode(DADD));
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new FieldInsnNode(GETFIELD, cls.name, getName("posZ", "field_70161_v"), "D"));
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new FieldInsnNode(GETFIELD, cls.name, getName("rand", "field_70146_Z"), "Ljava/util/Random;"));
                list.add(new MethodInsnNode(INVOKEVIRTUAL, "java/util/Random", "nextDouble", "()D", false));
                list.add(new InsnNode(DADD));
                list.add(new InsnNode(DCONST_0));
                list.add(new InsnNode(DCONST_0));
                list.add(new InsnNode(DCONST_0));
                list.add(new InsnNode(ICONST_0));
                list.add(new IntInsnNode(NEWARRAY, T_INT));
                list.add(new MethodInsnNode(INVOKEVIRTUAL, "net/minecraft/world/World", getName("spawnParticle", "func_175688_a"), "(Lnet/minecraft/util/EnumParticleTypes;DDDDDD[I)V", false));
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new FieldInsnNode(GETFIELD, cls.name, getName("rand", "field_70146_Z"), "Ljava/util/Random;"));
                list.add(new MethodInsnNode(INVOKEVIRTUAL, "java/util/Random", "nextInt", "()I", false));
                list.add(new LdcInsnNode(20));
                list.add(new JumpInsnNode(IF_ICMPNE, l_con_bubbleColumn));

                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new FieldInsnNode(GETFIELD, cls.name, getName("world", "field_70170_p"), "Lnet/minecraft/world/World;"));
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new FieldInsnNode(GETFIELD, cls.name, getName("posX", "field_70165_t"), "D"));
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new FieldInsnNode(GETFIELD, cls.name, getName("posY", "field_70163_u"), "D"));
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new FieldInsnNode(GETFIELD, cls.name, getName("posZ", "field_70161_v"), "D"));
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new MethodInsnNode(INVOKEVIRTUAL, cls.name, getName("getSplashSound", "func_184181_aa"), "()Lnet/minecraft/util/SoundEvent;", false));
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new MethodInsnNode(INVOKEVIRTUAL, cls.name, getName("getSoundCategory", "func_184176_by"), "()Lnet/minecraft/util/SoundCategory;", false));
                list.add(new InsnNode(FCONST_1));
                list.add(new LdcInsnNode(0.4F));
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new FieldInsnNode(GETFIELD, cls.name, getName("rand", "field_70146_Z"), "Ljava/util/Random;"));
                list.add(new MethodInsnNode(INVOKEVIRTUAL, "java/util/Random", "nextFloat", "()F", false));
                list.add(new InsnNode(FMUL));
                list.add(new LdcInsnNode(0.8F));
                list.add(new InsnNode(FADD));
                list.add(new InsnNode(ICONST_0));
                list.add(new MethodInsnNode(INVOKEVIRTUAL, "net/minecraft/world/World", getName("playSound", "func_184134_a"), "(DDDLnet/minecraft/util/SoundEvent;Lnet/minecraft/util/SoundCategory;FFZ)V", false));

                list.add(l_con_bubbleColumn);
                list.add(new FrameNode(F_SAME, 0, null, 0, null));
                method.instructions.insertBefore(node, list);
            }
            else if (method.name.equals("<clinit>")) {
                AbstractInsnNode node = method.instructions.getLast();
                while (node.getOpcode() != RETURN) node = node.getPrevious();
                InsnList list = new InsnList();
                list.add(new LdcInsnNode(Type.getType("Lnet/minecraft/entity/item/EntityBoat;")));
                list.add(new FieldInsnNode(GETSTATIC, "net/minecraft/network/datasync/DataSerializers", "VARINT", "Lnet/minecraft/network/datasync/DataSerializer;"));
                list.add(new MethodInsnNode(INVOKESTATIC, "net/minecraft/network/datasync/EntityDataManager", getName("createKey", "func_187226_a"), "(Ljava/lang/Class;Lnet/minecraft/network/datasync/DataSerializer;)Lnet/minecraft/network/datasync/DataParameter;", false));
                list.add(new FieldInsnNode(PUTSTATIC, cls.name, "BOAT_ROCKING_TICKS", "Lnet/minecraft/network/datasync/DataParameter;"));
                method.instructions.insertBefore(node, list);
            }
        }
        writeClass(cls);
        return write(cls);
    }

    protected static byte[] transformRenderBoat(byte[] basicClass) {
        ClassNode cls = read(basicClass);
        for (MethodNode method : cls.methods) {
            if (method.name.equals(getName("setupRotation", "func_188311_a"))) {
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node instanceof LdcInsnNode && ((LdcInsnNode) node).cst.equals(-1.0F)) {
                        InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 1));
                        list.add(new VarInsnNode(FLOAD, 3));
                        list.add(new MethodInsnNode(INVOKEVIRTUAL, "net/minecraft/entity/item/EntityBoat", "getRockingAngle", "(F)F", false));
                        list.add(new VarInsnNode(FSTORE, 6));
                        list.add(new VarInsnNode(FLOAD, 6));
                        list.add(new InsnNode(FCONST_0));
                        list.add(new MethodInsnNode(INVOKESTATIC, "net/minecraft/util/math/MathHelper", getName("epsilonEquals", "func_180185_a"), "(FF)Z", false));
                        LabelNode l_con_epsilon = new LabelNode();
                        list.add(new JumpInsnNode(IFNE, l_con_epsilon));
                        list.add(new VarInsnNode(FLOAD, 6));
                        list.add(new InsnNode(FCONST_1));
                        list.add(new InsnNode(FCONST_0));
                        list.add(new InsnNode(FCONST_1));
                        list.add(new MethodInsnNode(INVOKESTATIC, "net/minecraft/client/renderer/GlStateManager", getName("rotate", "func_179114_b"), "(FFFF)V", false));
                        list.add(l_con_epsilon);
                        list.add(new FrameNode(F_SAME, 0, null, 0, null));
                        method.instructions.insertBefore(node, list);
                        break;
                    }
                }
                break;
            }
        }
        writeClass(cls);
        return write(cls);
    }

    protected static byte[] transformSoulSand(byte[] basicClass) {
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

    protected static byte[] transformBlockMagma(byte[] basicClass) {
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
