package surreal.backportium.core.v13;

import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.tree.*;
import surreal.backportium.core.transformers.Transformer;

import java.util.Iterator;

class TridentTransformer extends Transformer {

    // Yes Forge, I will cancel player rendering and re-render the entire player model just to move players arm by 180 degrees
    protected static byte[] transformModelBiped(byte[] basicClass) {
        ClassNode cls = read(basicClass);
        for (MethodNode method : cls.methods) {
            if (method.name.equals(getName("setRotationAngles", "func_78087_a"))) {
                AbstractInsnNode node = method.instructions.getLast();
                while (node.getOpcode() != RETURN) node = node.getPrevious();
                InsnList list = new InsnList();
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new VarInsnNode(FLOAD, 1));
                list.add(new VarInsnNode(FLOAD, 2));
                list.add(new VarInsnNode(FLOAD, 3));
                list.add(new VarInsnNode(FLOAD, 4));
                list.add(new VarInsnNode(FLOAD, 5));
                list.add(new VarInsnNode(FLOAD, 6));
                list.add(new VarInsnNode(ALOAD, 7));
                list.add(hook("Trident$setRotationAngles", "(Lnet/minecraft/client/model/ModelBiped;FFFFFFLnet/minecraft/entity/Entity;)V"));
                method.instructions.insertBefore(node, list);
                break;
            }
        }
        return write(cls);
    }

    // Add values to living entity to track if it's in riptide effect or not
    protected static byte[] transformEntityLivingBase(byte[] basicClass) {
        ClassNode cls = read(basicClass);
        { // If entity is in riptide
            FieldVisitor visitor = cls.visitField(ACC_PROTECTED, "inRiptide", "Z", null, false);
            visitor.visitEnd();
        }
        { // Counts down to zero and sets inRiptide to false on zero. Gets set to 20 when entity gets in riptide
            FieldVisitor visitor = cls.visitField(ACC_PROTECTED, "riptideTime", "I", null, 0);
            visitor.visitEnd();
        }
        for (MethodNode method : cls.methods) {
            if (method.name.equals(getName("onUpdate", "func_70071_h_"))) {
                AbstractInsnNode node = method.instructions.getLast();
                while (node.getOpcode() != RETURN) node = node.getPrevious();
                InsnList list = new InsnList();
                LabelNode l_elsecon = new LabelNode();
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new FieldInsnNode(GETFIELD, cls.name, "riptideTime", "I"));
                list.add(new InsnNode(ICONST_0));
                list.add(new JumpInsnNode(IF_ICMPNE, l_elsecon));
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new InsnNode(ICONST_0));
                list.add(new FieldInsnNode(PUTFIELD, cls.name, "inRiptide", "Z"));
                LabelNode l_con = new LabelNode();
                list.add(new JumpInsnNode(GOTO, l_con));
                list.add(l_elsecon);
                list.add(new FrameNode(F_SAME, 0, null, 0, null));
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new FieldInsnNode(GETFIELD, cls.name, "riptideTime", "I"));
                list.add(new InsnNode(ICONST_1));
                list.add(new InsnNode(ISUB));
                list.add(new FieldInsnNode(PUTFIELD, cls.name, "riptideTime", "I"));
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new FieldInsnNode(GETFIELD, cls.name, "riptideTime", "I"));
                list.add(hook("Trident$riptideCollision", "(Lnet/minecraft/entity/EntityLivingBase;I)Z"));
                list.add(new FieldInsnNode(PUTFIELD, cls.name, "inRiptide", "Z"));
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new FieldInsnNode(GETFIELD, cls.name, "inRiptide", "Z"));
                LabelNode l_con2 = new LabelNode();
                list.add(new JumpInsnNode(IFNE, l_con2));
                list.add(new LabelNode());
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new InsnNode(ICONST_0));
                list.add(new FieldInsnNode(PUTFIELD, cls.name, "riptideTime", "I"));
                list.add(l_con2);
                list.add(new FrameNode(F_SAME, 0, null, 0, null));
                list.add(l_con);
                list.add(new FrameNode(F_SAME, 0, null, 0, null));
                method.instructions.insertBefore(node, list);
            }
        }
        { // handleRiptide
            MethodVisitor m_riptide = cls.visitMethod(ACC_PUBLIC, "handleRiptide", "(Lnet/minecraft/item/ItemStack;)V", null, null);
            m_riptide.visitFieldInsn(GETSTATIC, "surreal/backportium/enchantment/ModEnchantments", "RIPTIDE", "Lnet/minecraft/enchantment/Enchantment;");
            m_riptide.visitVarInsn(ALOAD, 1);
            m_riptide.visitMethodInsn(INVOKESTATIC, "net/minecraft/enchantment/EnchantmentHelper", getName("getEnchantmentLevel", "func_77506_a"), "(Lnet/minecraft/enchantment/Enchantment;Lnet/minecraft/item/ItemStack;)I", false);
            m_riptide.visitVarInsn(ISTORE, 2);
            m_riptide.visitVarInsn(ALOAD, 0);
            m_riptide.visitMethodInsn(INVOKEVIRTUAL, cls.name, getName("getLookVec", "func_70040_Z"), "()Lnet/minecraft/util/math/Vec3d;", false);
            m_riptide.visitVarInsn(ASTORE, 3);
            m_riptide.visitInsn(DCONST_1);
            m_riptide.visitLdcInsn(0.8D);
            m_riptide.visitVarInsn(ILOAD, 2);
            m_riptide.visitInsn(I2D);
            m_riptide.visitInsn(DMUL);
            m_riptide.visitInsn(DADD);
            m_riptide.visitVarInsn(DSTORE, 4);
            String motionX = getName("motionX", "field_70159_w");
            m_riptide.visitVarInsn(ALOAD, 0);
            m_riptide.visitInsn(DUP);
            m_riptide.visitFieldInsn(GETFIELD, cls.name, motionX, "D");
            m_riptide.visitVarInsn(DLOAD, 4);
            m_riptide.visitVarInsn(ALOAD, 3);
            m_riptide.visitFieldInsn(GETFIELD, "net/minecraft/util/math/Vec3d", getName("x", "field_72450_a"), "D");
            m_riptide.visitInsn(DMUL);
            m_riptide.visitInsn(DADD);
            m_riptide.visitFieldInsn(PUTFIELD, cls.name, motionX, "D");
            String motionY = getName("motionY", "field_70181_x");
            m_riptide.visitVarInsn(ALOAD, 0);
            m_riptide.visitInsn(DUP);
            m_riptide.visitFieldInsn(GETFIELD, cls.name, motionY, "D");
            m_riptide.visitVarInsn(DLOAD, 4);
            m_riptide.visitVarInsn(ALOAD, 3);
            m_riptide.visitFieldInsn(GETFIELD, "net/minecraft/util/math/Vec3d", getName("y", "field_72448_b"), "D");
            m_riptide.visitInsn(DMUL);
            m_riptide.visitInsn(DADD);
            m_riptide.visitFieldInsn(PUTFIELD, cls.name, motionY, "D");
            String motionZ = getName("motionZ", "field_70179_y");
            m_riptide.visitVarInsn(ALOAD, 0);
            m_riptide.visitInsn(DUP);
            m_riptide.visitFieldInsn(GETFIELD, cls.name, motionZ, "D");
            m_riptide.visitVarInsn(DLOAD, 4);
            m_riptide.visitVarInsn(ALOAD, 3);
            m_riptide.visitFieldInsn(GETFIELD, "net/minecraft/util/math/Vec3d", getName("z", "field_72449_c"), "D");
            m_riptide.visitInsn(DMUL);
            m_riptide.visitInsn(DADD);
            m_riptide.visitFieldInsn(PUTFIELD, cls.name, motionZ, "D");
            m_riptide.visitVarInsn(ALOAD, 0);
            m_riptide.visitInsn(ICONST_1);
            m_riptide.visitFieldInsn(PUTFIELD, cls.name, "inRiptide", "Z");
            m_riptide.visitVarInsn(ALOAD, 0);
            m_riptide.visitIntInsn(BIPUSH, 20);
            m_riptide.visitFieldInsn(PUTFIELD, cls.name, "riptideTime", "I");
            m_riptide.visitVarInsn(ILOAD, 2);
            hook(m_riptide, "Trident$getRiptideSound", "(I)Lnet/minecraft/util/SoundEvent;");
            m_riptide.visitVarInsn(ASTORE, 5);
            m_riptide.visitVarInsn(ALOAD, 0);
            m_riptide.visitFieldInsn(GETFIELD, cls.name, getName("world", "field_70170_p"), "Lnet/minecraft/world/World;");
            m_riptide.visitInsn(ACONST_NULL);
            m_riptide.visitVarInsn(ALOAD, 0);
            m_riptide.visitFieldInsn(GETFIELD, cls.name, getName("posX", "field_70165_t"), "D");
            m_riptide.visitVarInsn(ALOAD, 0);
            m_riptide.visitFieldInsn(GETFIELD, cls.name, getName("posY", "field_70163_u"), "D");
            m_riptide.visitVarInsn(ALOAD, 0);
            m_riptide.visitFieldInsn(GETFIELD, cls.name, getName("posZ", "field_70161_v"), "D");
            m_riptide.visitVarInsn(ALOAD, 5);
            m_riptide.visitVarInsn(ALOAD, 0);
            m_riptide.visitMethodInsn(INVOKEVIRTUAL, cls.name, getName("getSoundCategory", "func_184176_by"), "()Lnet/minecraft/util/SoundCategory;", false);
            m_riptide.visitInsn(FCONST_1);
            m_riptide.visitInsn(FCONST_1);
            m_riptide.visitMethodInsn(INVOKEVIRTUAL, "net/minecraft/world/World", getName("playSound", "func_184148_a"), "(Lnet/minecraft/entity/player/EntityPlayer;DDDLnet/minecraft/util/SoundEvent;Lnet/minecraft/util/SoundCategory;FF)V", false);
            m_riptide.visitInsn(RETURN);
        }
        {
            MethodVisitor m_isRiptide = cls.visitMethod(ACC_PUBLIC, "isInRiptide", "()Z", null, null);
            m_isRiptide.visitVarInsn(ALOAD, 0);
            m_isRiptide.visitFieldInsn(GETFIELD, cls.name, "inRiptide", "Z");
            m_isRiptide.visitInsn(IRETURN);
        }
        {
            MethodVisitor m_getRiptideTime = cls.visitMethod(ACC_PUBLIC, "getRiptideTickLeft", "()I", null, null);
            m_getRiptideTime.visitVarInsn(ALOAD, 0);
            m_getRiptideTime.visitFieldInsn(GETFIELD, cls.name, "riptideTime", "I");
            m_getRiptideTime.visitInsn(IRETURN);
        }
        return write(cls);
    }

    // Add riptide effect to entities
    protected static byte[] transformRenderLivingBase(byte[] basicClass) {
        ClassNode cls = read(basicClass);
        for (MethodNode method : cls.methods) {
            if (method.name.equals(getName("applyRotations", "func_77043_a"))) {
                AbstractInsnNode node = method.instructions.getLast();
                while (node.getOpcode() != RETURN) node = node.getPrevious();
                String livingBase = "net/minecraft/entity/EntityLivingBase";
                InsnList list = new InsnList();
                list.add(new VarInsnNode(ALOAD, 1));
                list.add(new MethodInsnNode(INVOKEVIRTUAL, livingBase, "isInRiptide", "()Z", false));
                LabelNode l_con_elytra = new LabelNode();
                list.add(new JumpInsnNode(IFEQ, l_con_elytra));
                list.add(new LdcInsnNode(-90F));
                list.add(new VarInsnNode(ALOAD, 1));
                list.add(new FieldInsnNode(GETFIELD, livingBase, getName("rotationPitch", "field_70125_A"), "F"));
                list.add(new InsnNode(FSUB));
                list.add(new InsnNode(FCONST_1));
                list.add(new InsnNode(FCONST_0));
                list.add(new InsnNode(FCONST_0));
                list.add(new MethodInsnNode(INVOKESTATIC, "net/minecraft/client/renderer/GlStateManager", getName("rotate", "func_179114_b"), "(FFFF)V", false));
                list.add(new VarInsnNode(ALOAD, 1));
                list.add(new VarInsnNode(FLOAD, 4));
                list.add(new MethodInsnNode(INVOKEVIRTUAL, livingBase, getName("getLook", "func_70676_i"), "(F)Lnet/minecraft/util/math/Vec3d;", false));
                list.add(new VarInsnNode(ASTORE, 5));
                list.add(new VarInsnNode(ALOAD, 1));
                list.add(new FieldInsnNode(GETFIELD, livingBase, getName("motionX", "field_70159_w"), "D"));
                list.add(new VarInsnNode(ALOAD, 1));
                list.add(new FieldInsnNode(GETFIELD, livingBase, getName("motionX", "field_70159_w"), "D"));
                list.add(new InsnNode(DMUL));
                list.add(new VarInsnNode(ALOAD, 1));
                list.add(new FieldInsnNode(GETFIELD, livingBase, getName("motionZ", "field_70179_y"), "D"));
                list.add(new VarInsnNode(ALOAD, 1));
                list.add(new FieldInsnNode(GETFIELD, livingBase, getName("motionZ", "field_70179_y"), "D"));
                list.add(new InsnNode(DMUL));
                list.add(new InsnNode(DADD));
                list.add(new VarInsnNode(DSTORE, 6));
                list.add(new VarInsnNode(ALOAD, 5));
                list.add(new FieldInsnNode(GETFIELD, "net/minecraft/util/math/Vec3d", getName("x", "field_72450_a"), "D"));
                list.add(new VarInsnNode(ALOAD, 5));
                list.add(new FieldInsnNode(GETFIELD, "net/minecraft/util/math/Vec3d", getName("x", "field_72450_a"), "D"));
                list.add(new InsnNode(DMUL));
                list.add(new VarInsnNode(ALOAD, 5));
                list.add(new FieldInsnNode(GETFIELD, "net/minecraft/util/math/Vec3d", getName("z", "field_72449_c"), "D"));
                list.add(new VarInsnNode(ALOAD, 5));
                list.add(new FieldInsnNode(GETFIELD, "net/minecraft/util/math/Vec3d", getName("z", "field_72449_c"), "D"));
                list.add(new InsnNode(DMUL));
                list.add(new InsnNode(DADD));
                list.add(new VarInsnNode(DSTORE, 8));
                list.add(new VarInsnNode(DLOAD, 6));
                list.add(new InsnNode(DCONST_0));
                list.add(new InsnNode(DCMPG));
                LabelNode l_con_d0 = new LabelNode();
                list.add(new JumpInsnNode(IFLE, l_con_d0));
                list.add(new VarInsnNode(DLOAD, 8));
                list.add(new InsnNode(DCONST_0));
                list.add(new InsnNode(DCMPG));
                list.add(new JumpInsnNode(IFLE, l_con_d0));
                list.add(new VarInsnNode(ALOAD, 1));
                list.add(new FieldInsnNode(GETFIELD, livingBase, getName("motionX", "field_70159_w"), "D"));
                list.add(new VarInsnNode(ALOAD, 5));
                list.add(new FieldInsnNode(GETFIELD, "net/minecraft/util/math/Vec3d", getName("x", "field_72450_a"), "D"));
                list.add(new InsnNode(DMUL));
                list.add(new VarInsnNode(ALOAD, 1));
                list.add(new FieldInsnNode(GETFIELD, livingBase, getName("motionZ", "field_70179_y"), "D"));
                list.add(new VarInsnNode(ALOAD, 5));
                list.add(new FieldInsnNode(GETFIELD, "net/minecraft/util/math/Vec3d", getName("z", "field_72449_c"), "D"));
                list.add(new InsnNode(DMUL));
                list.add(new InsnNode(DADD));
                list.add(new VarInsnNode(DLOAD, 6));
                list.add(new MethodInsnNode(INVOKESTATIC, "java/lang/Math", "sqrt", "(D)D", false));
                list.add(new VarInsnNode(DLOAD, 8));
                list.add(new MethodInsnNode(INVOKESTATIC, "java/lang/Math", "sqrt", "(D)D", false));
                list.add(new InsnNode(DMUL));
                list.add(new InsnNode(DDIV));
                list.add(new VarInsnNode(DSTORE, 6));
                list.add(new VarInsnNode(ALOAD, 1));
                list.add(new FieldInsnNode(GETFIELD, livingBase, getName("motionX", "field_70159_w"), "D"));
                list.add(new VarInsnNode(ALOAD, 5));
                list.add(new FieldInsnNode(GETFIELD, "net/minecraft/util/math/Vec3d", getName("z", "field_72449_c"), "D"));
                list.add(new InsnNode(DMUL));
                list.add(new VarInsnNode(ALOAD, 1));
                list.add(new FieldInsnNode(GETFIELD, livingBase, getName("motionZ", "field_70179_y"), "D"));
                list.add(new VarInsnNode(ALOAD, 5));
                list.add(new FieldInsnNode(GETFIELD, "net/minecraft/util/math/Vec3d", getName("x", "field_72450_a"), "D"));
                list.add(new InsnNode(DMUL));
                list.add(new InsnNode(DSUB));
                list.add(new VarInsnNode(DSTORE, 8));
                list.add(new VarInsnNode(DLOAD, 8));
                list.add(new MethodInsnNode(INVOKESTATIC, "java/lang/Math", "signum", "(D)D", false));
                list.add(new VarInsnNode(DLOAD, 6));
                list.add(new MethodInsnNode(INVOKESTATIC, "java/lang/Math", "acos", "(D)D", false));
                list.add(new InsnNode(DMUL));
                list.add(new LdcInsnNode(180.0D));
                list.add(new InsnNode(DMUL));
                list.add(new InsnNode(D2F));
                list.add(new LdcInsnNode(Math.PI));
                list.add(new InsnNode(D2F));
                list.add(new InsnNode(FDIV));
                list.add(new InsnNode(FCONST_0));
                list.add(new InsnNode(FCONST_1));
                list.add(new InsnNode(FCONST_0));
                list.add(new MethodInsnNode(INVOKESTATIC, "net/minecraft/client/renderer/GlStateManager", getName("rotate", "func_179114_b"), "(FFFF)V", false));
                list.add(l_con_d0);
                list.add(new FrameNode(F_APPEND, 3, new Object[]{"net/minecraft/util/math/Vec3d", DOUBLE, DOUBLE}, 3, null));
                list.add(new LdcInsnNode(72F));
                list.add(new VarInsnNode(ALOAD, 1));
                list.add(new MethodInsnNode(INVOKEVIRTUAL, livingBase, "getRiptideTickLeft", "()I", false));
                list.add(new InsnNode(I2F));
                list.add(new VarInsnNode(FLOAD, 4));
                list.add(new InsnNode(FSUB));
                list.add(new InsnNode(FCONST_1));
                list.add(new InsnNode(FADD));
                list.add(new InsnNode(FMUL));
                list.add(new InsnNode(FCONST_0));
                list.add(new InsnNode(FCONST_1));
                list.add(new InsnNode(FCONST_0));
                list.add(new MethodInsnNode(INVOKESTATIC, "net/minecraft/client/renderer/GlStateManager", getName("rotate", "func_179114_b"), "(FFFF)V", false));
                list.add(l_con_elytra);
                list.add(new FrameNode(F_CHOP, 3, null, 0, null));
                method.instructions.insertBefore(node, list);
                break;
            }
        }
        return write(cls);
    }

    // Fix rotation when using Trident with Elytra, maybe make Elytra rendering happen for all living entities
    protected static byte[] transformRenderPlayer(byte[] basicClass) {
        ClassNode cls = read(basicClass);
        for (MethodNode method : cls.methods) {
            if (method.name.equals(getName("applyRotations", "func_77043_a"))) {
                AbstractInsnNode node = method.instructions.getLast();
                while (node.getOpcode() != RETURN) node = node.getPrevious();
                String player = "net/minecraft/entity/player/EntityPlayer";
                InsnList list = new InsnList();
                list.add(new VarInsnNode(ALOAD, 1));
                list.add(new MethodInsnNode(INVOKEVIRTUAL, player, getName("isElytraFlying", "func_184613_cA"), "()Z", false));
                LabelNode l_con = new LabelNode();
                list.add(new JumpInsnNode(IFEQ, l_con));
                list.add(new VarInsnNode(ALOAD, 1));
                list.add(new MethodInsnNode(INVOKEVIRTUAL, player, "isInRiptide", "()Z", false));
                list.add(new JumpInsnNode(IFEQ, l_con));
                list.add(new LdcInsnNode(72F));
                list.add(new VarInsnNode(ALOAD, 1));
                list.add(new MethodInsnNode(INVOKEVIRTUAL, player, "getRiptideTickLeft", "()I", false));
                list.add(new InsnNode(I2F));
                list.add(new VarInsnNode(FLOAD, 4));
                list.add(new InsnNode(FSUB));
                list.add(new InsnNode(FCONST_1));
                list.add(new InsnNode(FADD));
                list.add(new InsnNode(FMUL));
                list.add(new InsnNode(FCONST_0));
                list.add(new InsnNode(FCONST_1));
                list.add(new InsnNode(FCONST_0));
                list.add(new MethodInsnNode(INVOKESTATIC, "net/minecraft/client/renderer/GlStateManager", getName("rotate", "func_179114_b"), "(FFFF)V", false));
                list.add(l_con);
                list.add(new FrameNode(F_SAME, 0, null, 0, null));
                method.instructions.insertBefore(node, list);
                break;
            } else if (method.name.equals("<init>")) {
                String riptideModel = "surreal/backportium/client/model/entity/ModelRiptide";
                String riptideLayer = "surreal/backportium/client/renderer/entity/layer/LayerRiptide";
                AbstractInsnNode node = method.instructions.getLast();
                while (node.getOpcode() != RETURN) {
                    node = node.getPrevious();
                }
                InsnList list = new InsnList();
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new TypeInsnNode(NEW, riptideLayer));
                list.add(new InsnNode(DUP));
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new TypeInsnNode(NEW, riptideModel));
                list.add(new InsnNode(DUP));
                list.add(new MethodInsnNode(INVOKESPECIAL, riptideModel, "<init>", "()V", false));
                list.add(new MethodInsnNode(INVOKESPECIAL, riptideLayer, "<init>", "(Lnet/minecraft/client/renderer/entity/Render;Lnet/minecraft/client/model/ModelBase;)V", false));
                list.add(new MethodInsnNode(INVOKEVIRTUAL, cls.name, getName("addLayer", "func_177094_a"), "(Lnet/minecraft/client/renderer/entity/layers/LayerRenderer;)Z", false));
                method.instructions.insertBefore(node, list);
            }
        }
        return write(cls);
    }

    protected static byte[] transformEntityPlayer(byte[] basicClass) {
        ClassNode cls = read(basicClass);
        for (MethodNode method : cls.methods) {
            if (method.name.equals(getName("updateSize", "func_184808_cD"))) {
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();

                AbstractInsnNode node = null;

                while (iterator.hasNext()) {
                    AbstractInsnNode next = iterator.next();

                    if (node == null && next.getOpcode() == ALOAD) {
                        node = next;
                    }

                    if (next.getOpcode() == GOTO) {
                        LabelNode l_goto = ((JumpInsnNode) next).label;
                        LabelNode l_con = new LabelNode();
                        InsnList list = new InsnList();

                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new MethodInsnNode(INVOKEVIRTUAL, cls.name, "isInRiptide", "()Z", false));
                        list.add(new JumpInsnNode(IFEQ, l_con));
                        list.add(new LabelNode());
                        list.add(new LdcInsnNode(0.6F));
                        list.add(new VarInsnNode(FSTORE, 1));
                        list.add(new LdcInsnNode(0.6F));
                        list.add(new VarInsnNode(FSTORE, 2));
                        list.add(new JumpInsnNode(GOTO, l_goto));
                        list.add(l_con);
                        list.add(new FrameNode(F_SAME, 0, null, 0, null));
                        method.instructions.insertBefore(node, list);
                        break;
                    }
                }
            }
        }
        return write(cls);
    }

    protected static byte[] transformEntityPlayerSP(byte[] basicClass) {
        ClassNode cls = read(basicClass);
        for (MethodNode method : cls.methods) {
            if (method.name.equals(getName("isSneaking", "func_70093_af"))) {
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == ILOAD) {
                        LabelNode label = ((JumpInsnNode) node.getNext()).label;
                        InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new MethodInsnNode(INVOKEVIRTUAL, cls.name, "isInRiptide", "()Z", false));
                        list.add(new JumpInsnNode(IFNE, label));
                        method.instructions.insertBefore(node, list);
                        break;
                    }
                }
                break;
            }
        }
        return write(cls);
    }
}
