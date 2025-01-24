package surreal.backportium.core.v13;

import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.tree.*;
import surreal.backportium.core.transformers.Transformer;

import java.util.Iterator;

class TridentTransformer extends Transformer {

    // Yes Forge, I will cancel RenderPlayerEvent and re-render the entire player model just to move players arm by 180 degrees
    public static byte[] transformModelBiped(byte[] basicClass) {
        ClassNode cls = read(basicClass);
        for (MethodNode method : cls.methods) {
            if (method.name.equals(getName("setRotationAngles", "func_78087_a"))) {
                AbstractInsnNode node = method.instructions.getLast();
                while (node.getOpcode() != RETURN) node = node.getPrevious();
                InsnList list = new InsnList();
                list.add(new VarInsnNode(ALOAD, 7));
                list.add(new TypeInsnNode(INSTANCEOF, "net/minecraft/entity/EntityLivingBase"));
                LabelNode l_con_living = new LabelNode();
                list.add(new JumpInsnNode(IFEQ, l_con_living));
                list.add(new VarInsnNode(ALOAD, 7));
                list.add(new TypeInsnNode(CHECKCAST, "net/minecraft/entity/EntityLivingBase"));
                list.add(new VarInsnNode(ASTORE, 8));
                list.add(new VarInsnNode(ALOAD, 8));
                list.add(new MethodInsnNode(INVOKEVIRTUAL, "net/minecraft/entity/EntityLivingBase", getName("getActiveItemStack", "func_184607_cu"), "()Lnet/minecraft/item/ItemStack;", false));
                list.add(new VarInsnNode(ASTORE, 9));
                list.add(new VarInsnNode(ALOAD, 8));
                list.add(new MethodInsnNode(INVOKEVIRTUAL, "net/minecraft/entity/EntityLivingBase", getName("getPrimaryHand", "func_184591_cq"), "()Lnet/minecraft/util/EnumHandSide;", false));
                list.add(new VarInsnNode(ASTORE, 10));
                list.add(new VarInsnNode(ALOAD, 8));
                list.add(new MethodInsnNode(INVOKEVIRTUAL, "net/minecraft/entity/EntityLivingBase", getName("getActiveHand", "func_184600_cs"), "()Lnet/minecraft/util/EnumHand;", false));
                list.add(new FieldInsnNode(GETSTATIC, "net/minecraft/util/EnumHand", "MAIN_HAND", "Lnet/minecraft/util/EnumHand;"));
                {
                    LabelNode l_con = new LabelNode();
                    list.add(new JumpInsnNode(IF_ACMPEQ, l_con));
                    list.add(new VarInsnNode(ALOAD, 8));
                    list.add(new MethodInsnNode(INVOKEVIRTUAL, "net/minecraft/entity/EntityLivingBase", getName("getPrimaryHand", "func_184591_cq"), "()Lnet/minecraft/util/EnumHandSide;", false));
                    list.add(new MethodInsnNode(INVOKEVIRTUAL, "net/minecraft/util/EnumHandSide", getName("opposite", "func_188468_a"), "()Lnet/minecraft/util/EnumHandSide;", false));
                    list.add(new VarInsnNode(ASTORE, 10));
                    list.add(l_con);
                    list.add(new FrameNode(F_APPEND, 3, new Object[] { "net/minecraft/entity/EntityLivingBase", "net/minecraft/item/ItemStack", "net/minecraft/util/EnumHandSide" }, 0, null));
                }
                list.add(new VarInsnNode(ALOAD, 9));
                list.add(new MethodInsnNode(INVOKEVIRTUAL, "net/minecraft/item/ItemStack", getName("getItemUseAction", "func_77975_n"), "()Lnet/minecraft/item/EnumAction;", false));
                list.add(new FieldInsnNode(GETSTATIC, "surreal/backportium/Backportium", "SPEAR", "Lnet/minecraft/item/EnumAction;"));
                list.add(new JumpInsnNode(IF_ACMPNE, l_con_living));
                list.add(new FieldInsnNode(GETSTATIC, "surreal/backportium/enchantment/ModEnchantments", "RIPTIDE", "Lnet/minecraft/enchantment/Enchantment;"));
                list.add(new VarInsnNode(ALOAD, 9));
                list.add(new MethodInsnNode(INVOKESTATIC, "net/minecraft/enchantment/EnchantmentHelper", getName("getEnchantmentLevel", "func_77506_a"), "(Lnet/minecraft/enchantment/Enchantment;Lnet/minecraft/item/ItemStack;)I", false));
                list.add(new InsnNode(ICONST_0));
                LabelNode l_con_riptide = new LabelNode();
                list.add(new JumpInsnNode(IF_ICMPEQ, l_con_riptide));
                list.add(new VarInsnNode(ALOAD, 8));
                list.add(new FieldInsnNode(GETFIELD, "net/minecraft/entity/EntityLivingBase", getName("world", "field_70170_p"), "Lnet/minecraft/world/World;"));
                list.add(new VarInsnNode(ALOAD, 8));
                list.add(new MethodInsnNode(INVOKESTATIC, "surreal/backportium/api/helper/RiptideHelper", "canRiptide", "(Lnet/minecraft/world/World;Lnet/minecraft/entity/EntityLivingBase;)Z", false));
                list.add(new JumpInsnNode(IFNE, l_con_riptide));
                list.add(new InsnNode(RETURN));
                list.add(l_con_riptide);
                list.add(new FrameNode(F_SAME, 0, null, 0, null));
                list.add(new VarInsnNode(ALOAD, 10));
                list.add(new FieldInsnNode(GETSTATIC, "net/minecraft/util/EnumHandSide", "RIGHT", "Lnet/minecraft/util/EnumHandSide;"));
                LabelNode l_con = new LabelNode();
                list.add(new JumpInsnNode(IF_ACMPNE, l_con));
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new FieldInsnNode(GETFIELD, cls.name, getName("bipedRightArm", "field_178723_h"), "Lnet/minecraft/client/model/ModelRenderer;"));
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new FieldInsnNode(GETFIELD, cls.name, getName("bipedRightArm", "field_178723_h"), "Lnet/minecraft/client/model/ModelRenderer;"));
                list.add(new FieldInsnNode(GETFIELD, "net/minecraft/client/model/ModelRenderer", getName("rotateAngleX", "field_78795_f"), "F"));
                list.add(new LdcInsnNode(0.5F));
                list.add(new InsnNode(FMUL));
                list.add(new LdcInsnNode((float) 3.14159265358979323846D));
                list.add(new InsnNode(FSUB));
                list.add(new FieldInsnNode(PUTFIELD, "net/minecraft/client/model/ModelRenderer", getName("rotateAngleX", "field_78795_f"), "F"));
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new FieldInsnNode(GETFIELD, cls.name, getName("bipedRightArm", "field_178723_h"), "Lnet/minecraft/client/model/ModelRenderer;"));
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new FieldInsnNode(GETFIELD, cls.name, getName("bipedRightArm", "field_178723_h"), "Lnet/minecraft/client/model/ModelRenderer;"));
                list.add(new FieldInsnNode(GETFIELD, "net/minecraft/client/model/ModelRenderer", getName("rotateAngleZ", "field_78808_h"), "F"));
                list.add(new LdcInsnNode(0.15F));
                list.add(new InsnNode(FSUB));
                list.add(new FieldInsnNode(PUTFIELD, "net/minecraft/client/model/ModelRenderer", getName("rotateAngleZ", "field_78808_h"), "F"));
                list.add(new JumpInsnNode(GOTO, l_con_living));
                list.add(l_con);
                list.add(new FrameNode(F_SAME, 0, null, 0, null));
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new FieldInsnNode(GETFIELD, cls.name, getName("bipedLeftArm", "field_178724_i"), "Lnet/minecraft/client/model/ModelRenderer;"));
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new FieldInsnNode(GETFIELD, cls.name, getName("bipedLeftArm", "field_178724_i"), "Lnet/minecraft/client/model/ModelRenderer;"));
                list.add(new FieldInsnNode(GETFIELD, "net/minecraft/client/model/ModelRenderer", getName("rotateAngleX", "field_78795_f"), "F"));
                list.add(new LdcInsnNode(0.5F));
                list.add(new InsnNode(FMUL));
                list.add(new LdcInsnNode((float) 3.14159265358979323846D));
                list.add(new InsnNode(FSUB));
                list.add(new FieldInsnNode(PUTFIELD, "net/minecraft/client/model/ModelRenderer", getName("rotateAngleX", "field_78795_f"), "F"));
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new FieldInsnNode(GETFIELD, cls.name, getName("bipedLeftArm", "field_178724_i"), "Lnet/minecraft/client/model/ModelRenderer;"));
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new FieldInsnNode(GETFIELD, cls.name, getName("bipedLeftArm", "field_178724_i"), "Lnet/minecraft/client/model/ModelRenderer;"));
                list.add(new FieldInsnNode(GETFIELD, "net/minecraft/client/model/ModelRenderer", getName("rotateAngleZ", "field_78808_h"), "F"));
                list.add(new LdcInsnNode(0.15F));
                list.add(new InsnNode(FADD));
                list.add(new FieldInsnNode(PUTFIELD, "net/minecraft/client/model/ModelRenderer", getName("rotateAngleZ", "field_78808_h"), "F"));
                list.add(l_con_living);
                list.add(new FrameNode(F_SAME, 0, null, 0, null));
                method.instructions.insertBefore(node, list);
                break;
            }
        }
        return write(cls, 3); // Cursed frames
    }

    // Add values to living entity to track if it's in riptide effect or not
    public static byte[] transformEntityLivingBase(byte[] basicClass) {
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
                while (node.getOpcode() != RETURN) {
                    node = node.getPrevious();
                }
                InsnList list = new InsnList();
                LabelNode l_elsecon = new LabelNode();
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new FieldInsnNode(GETFIELD, cls.name, "riptideTime", "I"));
                list.add(new InsnNode(ICONST_0));
                list.add(new JumpInsnNode(IF_ICMPNE, l_elsecon));
                list.add(new LabelNode());
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
                list.add(hook("EntityLivingBase$handleRiptide", "(Lnet/minecraft/entity/EntityLivingBase;I)Z"));
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
        { // handleRiptide. I guess I should handle sound here too?
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
            m_riptide.visitMethodInsn(INVOKESTATIC, "surreal/backportium/api/helper/RiptideHelper", "getSound", "(I)Lnet/minecraft/util/SoundEvent;", false);
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
            m_riptide.visitMethodInsn(INVOKEVIRTUAL, "net/minecraft/world/World", getName("playSound", "func_184133_a"), "(Lnet/minecraft/entity/player/EntityPlayer;DDDLnet/minecraft/util/SoundEvent;Lnet/minecraft/util/SoundCategory;FF)V", false);

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
    public static byte[] transformRenderLivingBase(byte[] basicClass) {
        String livingBase = "net/minecraft/entity/EntityLivingBase";
        ClassNode cls = read(basicClass);
        for (MethodNode method : cls.methods) {
            if (method.name.equals(getName("applyRotations", "func_77043_a"))) {
                AbstractInsnNode node = method.instructions.getLast();
                while (node.getOpcode() != RETURN) {
                    node = node.getPrevious();
                }
                InsnList list = new InsnList();
                list.add(new VarInsnNode(ALOAD, 1));
                list.add(new VarInsnNode(ALOAD, 1));
                list.add(new MethodInsnNode(INVOKEVIRTUAL, livingBase, "isInRiptide", "()Z", false));
                list.add(new VarInsnNode(ALOAD, 1));
                list.add(new MethodInsnNode(INVOKEVIRTUAL, livingBase, "getRiptideTickLeft", "()I", false));
                list.add(new VarInsnNode(FLOAD, 4));
                list.add(clientHook("RenderLivingBase$applyRotations", "(Lnet/minecraft/entity/EntityLivingBase;ZIF)V"));
                method.instructions.insertBefore(node, list);
                break;
            }
        }

        return write(cls);
    }

    // Fix rotation when using Trident with Elytra, maybe make Elytra rendering happen for all living entities
    public static byte[] transformRenderPlayer(byte[] basicClass) {
        String player = "net/minecraft/entity/player/EntityPlayer";
        ClassNode cls = read(basicClass);
        for (MethodNode method : cls.methods) {
            if (method.name.equals(getName("applyRotations", "func_77043_a"))) {
                AbstractInsnNode node = method.instructions.getLast();
                while (node.getOpcode() != RETURN) {
                    node = node.getPrevious();
                }
                InsnList list = new InsnList();
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new VarInsnNode(ALOAD, 1));
                list.add(new VarInsnNode(ALOAD, 1));
                list.add(new MethodInsnNode(INVOKEVIRTUAL, player, "isInRiptide", "()Z", false));
                list.add(new VarInsnNode(ALOAD, 1));
                list.add(new MethodInsnNode(INVOKEVIRTUAL, player, "getRiptideTickLeft", "()I", false));
                list.add(new VarInsnNode(FLOAD, 4));
                list.add(clientHook("RenderPlayer$fixElytraRotations", "(Lnet/minecraft/entity/player/EntityPlayer;ZIF)V"));
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
        writeClass(cls);
        return write(cls);
    }

    public static byte[] transformEntityPlayer(byte[] basicClass) {
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

    public static byte[] transformEntityPlayerSP(byte[] basicClass) {
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
