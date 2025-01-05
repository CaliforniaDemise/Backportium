package surreal.backportium.core.transformers;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.tree.*;

import java.util.Iterator;

// TODO Make riptide a capability and create an API for adding Riptide and Swimming like stuff that can change size and model animations.
// TODO Eating animation
// TODO Fix Quark animations and swimming animation incompatibility
// TODO Fix eye height
/**
 * New swimming, crouching etc. mechanics.
 **/
public class PlayerMoveTransformer extends BasicTransformer {

    public static byte[] transformRenderPlayer(byte[] basicClass) {
       ClassNode cls = read(basicClass);
        for (MethodNode method : cls.methods) {
            if (method.name.equals(getName("renderRightArm", "func_177138_b"))) {
                AbstractInsnNode node = method.instructions.getFirst();
                while (node.getOpcode() != FCONST_1) node = node.getNext();
                InsnList list = new InsnList();
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new MethodInsnNode(INVOKEVIRTUAL, cls.name, getName("getMainModel", "func_177087_b"), "()Lnet/minecraft/client/model/ModelBase;", false));
                list.add(new TypeInsnNode(CHECKCAST, "net/minecraft/client/model/ModelBiped"));
                list.add(new InsnNode(FCONST_0));
                list.add(new MethodInsnNode(INVOKEVIRTUAL, "net/minecraft/client/model/ModelBiped", "setSwimAnimation", "(F)V", false));
                method.instructions.insertBefore(node, list);
            }
            else if (method.name.equals(getName("renderLeftArm", "func_177139_c"))) {
                AbstractInsnNode node = method.instructions.getFirst();
                while (node.getOpcode() != FCONST_1) node = node.getNext();
                InsnList list = new InsnList();
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new MethodInsnNode(INVOKEVIRTUAL, cls.name, getName("getMainModel", "func_177087_b"), "()Lnet/minecraft/client/model/ModelBase;", false));
                list.add(new TypeInsnNode(CHECKCAST, "net/minecraft/client/model/ModelBiped"));
                list.add(new InsnNode(FCONST_0));
                list.add(new MethodInsnNode(INVOKEVIRTUAL, "net/minecraft/client/model/ModelBiped", "setSwimAnimation", "(F)V", false));
                method.instructions.insertBefore(node, list);
            }
            else if (method.name.equals(getName("applyRotations", "func_77043_a"))) {
                AbstractInsnNode node = method.instructions.getLast();
                while (node.getOpcode() != RETURN) node = node.getPrevious();
                InsnList list = new InsnList();
                // TODO MoBends integration
                list.add(new VarInsnNode(ALOAD, 1));
                list.add(new MethodInsnNode(INVOKEVIRTUAL, "net/minecraft/entity/EntityLivingBase", getName("isElytraFlying", "func_184613_cA"), "()Z", false));
                LabelNode l_con_elytra = new LabelNode();
                list.add(new JumpInsnNode(IFNE, l_con_elytra));

                list.add(new InsnNode(FCONST_0));
                list.add(new VarInsnNode(FSTORE, 5));

                list.add(new VarInsnNode(ALOAD, 1));
                list.add(new MethodInsnNode(INVOKEVIRTUAL, "net/minecraft/entity/EntityLivingBase", getName("isInWater", "func_70090_H"), "()Z", false));
                LabelNode l_con_inWaterGoto = new LabelNode();
                LabelNode l_con_inWater = new LabelNode();
                list.add(new JumpInsnNode(IFEQ, l_con_inWater));
                list.add(new LdcInsnNode(-90F));
                list.add(new VarInsnNode(ALOAD, 1));
                list.add(new FieldInsnNode(GETFIELD, "net/minecraft/entity/EntityLivingBase", getName("rotationPitch", "field_70125_A"), "F"));
                list.add(new InsnNode(FSUB));
                list.add(new VarInsnNode(FSTORE, 5));
                list.add(new JumpInsnNode(GOTO, l_con_inWaterGoto));
                list.add(l_con_inWater);
                list.add(new FrameNode(F_SAME, 0, null, 0, null));
                list.add(new LdcInsnNode(-90F));
                list.add(new VarInsnNode(FSTORE, 5));
                list.add(l_con_inWaterGoto);
                list.add(new FrameNode(F_APPEND, 1, new Object[] { FLOAT }, 0, null));

                list.add(new VarInsnNode(ALOAD, 1));
                list.add(new TypeInsnNode(CHECKCAST, "net/minecraft/entity/player/EntityPlayer"));
                list.add(new VarInsnNode(FLOAD, 4));
                list.add(new MethodInsnNode(INVOKEVIRTUAL, "net/minecraft/entity/player/EntityPlayer", "getSwimAnimation", "(F)F", false));
                list.add(new InsnNode(FCONST_0));
                list.add(new VarInsnNode(FLOAD, 5));
                list.add(new MethodInsnNode(INVOKESTATIC, "surreal/backportium/util/RandomHelper", "lerp", "(FFF)F", false));
                list.add(new InsnNode(FCONST_1));
                list.add(new InsnNode(FCONST_0));
                list.add(new InsnNode(FCONST_0));
                list.add(new MethodInsnNode(INVOKESTATIC, "net/minecraft/client/renderer/GlStateManager", getName("rotate", "func_179114_b"), "(FFFF)V", false));

                list.add(new VarInsnNode(ALOAD, 1));
                list.add(new MethodInsnNode(INVOKEVIRTUAL, "net/minecraft/entity/EntityLivingBase", "isSwimming", "()Z", false));
                LabelNode l_con_swimming = new LabelNode();
                list.add(new JumpInsnNode(IFEQ, l_con_swimming));
                list.add(new InsnNode(FCONST_0));
//                list.add(new InsnNode(FCONST_1));
//                list.add(new InsnNode(FNEG));
                list.add(new LdcInsnNode(-0.5F));
//                list.add(new LdcInsnNode(0.3F));
                list.add(new LdcInsnNode(0.3F));
                list.add(new MethodInsnNode(INVOKESTATIC, "net/minecraft/client/renderer/GlStateManager", getName("translate", "func_179109_b"), "(FFF)V", false));
                list.add(l_con_swimming);
                list.add(new FrameNode(F_CHOP, 1, null, 0, null));
                list.add(l_con_elytra);
                list.add(new FrameNode(F_SAME, 0, null, 0, null));
                method.instructions.insertBefore(node, list);
            }
        }
        writeClass(cls);
        return write(cls);
    }

    public static byte[] transformModelBiped(byte[] basicClass) {
        ClassNode cls = read(basicClass);
        { // setSwimAnimation
            MethodVisitor m = cls.visitMethod(ACC_PUBLIC, "setSwimAnimation", "(F)V", null, null);
            m.visitVarInsn(ALOAD, 0);
            m.visitVarInsn(FLOAD, 1);
            m.visitFieldInsn(PUTFIELD, cls.name, "swimAnimation", "F");
            m.visitInsn(RETURN);
        }
        { // setLivingAnimations
            MethodVisitor m = cls.visitMethod(ACC_PUBLIC, getName("setLivingAnimations", "func_78086_a"), "(Lnet/minecraft/entity/EntityLivingBase;FFF)V", null, null);
            m.visitVarInsn(ALOAD, 1);
            m.visitTypeInsn(INSTANCEOF, "net/minecraft/entity/player/EntityPlayer");
            Label l_con = new Label();
            m.visitJumpInsn(IFEQ, l_con);
            m.visitVarInsn(ALOAD, 0);
            m.visitVarInsn(ALOAD, 1);
            m.visitTypeInsn(CHECKCAST, "net/minecraft/entity/player/EntityPlayer");
            m.visitVarInsn(FLOAD, 4);
            m.visitMethodInsn(INVOKEVIRTUAL, "net/minecraft/entity/player/EntityPlayer", "getSwimAnimation", "(F)F", false);
            m.visitFieldInsn(PUTFIELD, cls.name, "swimAnimation", "F");
            m.visitLabel(l_con);
            m.visitFrame(F_SAME, 0, null, 0, null);
            m.visitInsn(RETURN);
        }
        { // swimAnimation
            cls.visitField(ACC_PRIVATE, "swimAnimation", "F", null, 0.0F);
        }
        { // rotLerpRad
            MethodVisitor m = cls.visitMethod(ACC_PRIVATE | ACC_STATIC, "rotLerpRad", "(FFF)F", null, null);
            m.visitVarInsn(FLOAD, 2);
            m.visitVarInsn(FLOAD, 1);
            m.visitInsn(FSUB);
            m.visitFieldInsn(GETSTATIC, "java/lang/Math", "PI", "D");
            m.visitInsn(D2F);
            m.visitInsn(FCONST_2);
            m.visitInsn(FMUL);
            m.visitInsn(FREM);
            m.visitVarInsn(FSTORE, 3);
            m.visitVarInsn(FLOAD, 3);
            m.visitFieldInsn(GETSTATIC, "java/lang/Math", "PI", "D");
            m.visitInsn(D2F);
            m.visitInsn(FNEG);
            Label l_con = new Label();
            m.visitInsn(FCMPG);
            m.visitJumpInsn(IFGE, l_con);
            m.visitVarInsn(FLOAD, 3);
            m.visitFieldInsn(GETSTATIC, "java/lang/Math", "PI", "D");
            m.visitInsn(D2F);
            m.visitInsn(FCONST_2);
            m.visitInsn(FMUL);
            m.visitInsn(FADD);
            m.visitVarInsn(FSTORE, 3);
            m.visitLabel(l_con);
            m.visitFrame(F_APPEND, 1, new Object[] { FLOAT }, 0, null);
            m.visitVarInsn(FLOAD, 3);
            m.visitFieldInsn(GETSTATIC, "java/lang/Math", "PI", "D");
            m.visitInsn(D2F);
            l_con = new Label();
            m.visitInsn(FCMPG);
            m.visitJumpInsn(IFLT, l_con);
            m.visitVarInsn(FLOAD, 3);
            m.visitFieldInsn(GETSTATIC, "java/lang/Math", "PI", "D");
            m.visitInsn(D2F);
            m.visitInsn(FCONST_2);
            m.visitInsn(FMUL);
            m.visitInsn(FSUB);
            m.visitVarInsn(FSTORE, 3);
            m.visitLabel(l_con);
            m.visitFrame(F_SAME, 0, null, 0, null);
            m.visitVarInsn(FLOAD, 1);
            m.visitVarInsn(FLOAD, 0);
            m.visitVarInsn(FLOAD, 3);
            m.visitInsn(FMUL);
            m.visitInsn(FADD);
            m.visitInsn(FRETURN);
        }
        { // getArmAngleSq
            MethodVisitor m = cls.visitMethod(ACC_PRIVATE, "getArmAngleSq", "(F)F", null, null);
            m.visitLdcInsn(-65F);
            m.visitVarInsn(FLOAD, 1);
            m.visitInsn(FMUL);
            m.visitVarInsn(FLOAD, 1);
            m.visitVarInsn(FLOAD, 1);
            m.visitInsn(FMUL);
            m.visitInsn(FADD);
            m.visitInsn(FRETURN);
        }
        for (MethodNode method : cls.methods) {
            if (method.name.equals(getName("render", "func_78088_a"))) {
                AbstractInsnNode node = method.instructions.getFirst();
                while (node.getOpcode() != ALOAD) node = node.getNext();
                InsnList list = new InsnList();
                list.add(new VarInsnNode(ALOAD, 1));
                list.add(new TypeInsnNode(INSTANCEOF, "net/minecraft/entity/EntityLivingBase"));
                LabelNode l_con_living = new LabelNode();
                list.add(new JumpInsnNode(IFEQ, l_con_living));
                list.add(new VarInsnNode(ALOAD, 1));
                list.add(new TypeInsnNode(CHECKCAST, "net/minecraft/entity/EntityLivingBase"));
                list.add(new VarInsnNode(ASTORE, 8));
                list.add(new VarInsnNode(ALOAD, 8));
                list.add(new MethodInsnNode(INVOKEVIRTUAL, "net/minecraft/entity/EntityLivingBase", getName("getTicksElytraFlying", "func_184599_cB"), "()I", false));
                list.add(new InsnNode(ICONST_4));
                list.add(new JumpInsnNode(IF_ICMPGT, l_con_living));
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new FieldInsnNode(GETFIELD, cls.name, "swimAnimation", "F"));
                list.add(new InsnNode(FCONST_0));
                list.add(new InsnNode(FCMPG));
                list.add(new JumpInsnNode(IFLE, l_con_living));
                list.add(new VarInsnNode(ALOAD, 8));
                list.add(new MethodInsnNode(INVOKEVIRTUAL, "net/minecraft/entity/EntityLivingBase", "isSwimming", "()Z", false));
                LabelNode l_con_swimming = new LabelNode();
                list.add(new JumpInsnNode(IFEQ, l_con_swimming));
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new FieldInsnNode(GETFIELD, cls.name, "swimAnimation", "F"));
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new FieldInsnNode(GETFIELD, cls.name, getName("bipedHead", "field_78116_c"), "Lnet/minecraft/client/model/ModelRenderer;"));
                list.add(new FieldInsnNode(GETFIELD, "net/minecraft/client/model/ModelRenderer", getName("rotateAngleX", "field_78795_f"), "F"));
                list.add(new FieldInsnNode(GETSTATIC, "java/lang/Math", "PI", "D"));
                list.add(new InsnNode(DNEG));
                list.add(new InsnNode(D2F));
                list.add(new LdcInsnNode(4F));
                list.add(new InsnNode(FDIV));
                list.add(new LdcInsnNode(0.017453292F));
                list.add(new InsnNode(FDIV));
                list.add(new MethodInsnNode(INVOKESTATIC, cls.name, "rotLerpRad", "(FFF)F", false));
                list.add(new VarInsnNode(FSTORE, 6));
                list.add(new JumpInsnNode(GOTO, l_con_living));
                list.add(l_con_swimming);
                list.add(new FrameNode(F_SAME, 0, null, 0, null));
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new FieldInsnNode(GETFIELD, cls.name, "swimAnimation", "F"));
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new FieldInsnNode(GETFIELD, cls.name, getName("bipedHead", "field_78116_c"), "Lnet/minecraft/client/model/ModelRenderer;"));
                list.add(new FieldInsnNode(GETFIELD, "net/minecraft/client/model/ModelRenderer", getName("rotateAngleX", "rotateAngleX"), "F"));
                list.add(new VarInsnNode(FLOAD, 6));
                list.add(new FieldInsnNode(GETSTATIC, "java/lang/Math", "PI", "D"));
                list.add(new InsnNode(D2F));
                list.add(new LdcInsnNode(180F));
                list.add(new InsnNode(FDIV));
                list.add(new InsnNode(FMUL));
                list.add(new LdcInsnNode(0.017453292F));
                list.add(new InsnNode(FDIV));
                list.add(new MethodInsnNode(INVOKESTATIC, cls.name, "rotLerpRad", "(FFF)F", false));
                list.add(new VarInsnNode(FSTORE, 6));
                list.add(l_con_living);
                list.add(new FrameNode(F_SAME, 0, null, 0, null));
                method.instructions.insertBefore(node, list);
            }
            else if (method.name.equals(getName("setRotationAngles", "func_78087_a"))) {
                { // Swimming Animation
                    int count = 0;
                    AbstractInsnNode node = method.instructions.getLast();
                    do {
                        node = node.getPrevious();
                        if (node.getOpcode() == ALOAD) count++;
                    }
                    while (count != 2);
                    InsnList list = new InsnList();
                    list.add(new VarInsnNode(ALOAD, 0));
                    list.add(new FieldInsnNode(GETFIELD, cls.name, "swimAnimation", "F"));
                    list.add(new InsnNode(FCONST_0));
                    list.add(new InsnNode(FCMPG));
                    LabelNode l_con_anim = new LabelNode();
                    list.add(new JumpInsnNode(IFLE, l_con_anim));
                    list.add(new VarInsnNode(FLOAD, 1));
                    list.add(new LdcInsnNode(26F));
                    list.add(new InsnNode(FREM));
                    list.add(new VarInsnNode(FSTORE, 9));

                    list.add(new InsnNode(FCONST_0));
                    list.add(new VarInsnNode(FSTORE, 10));

                    list.add(new InsnNode(FCONST_0));
                    list.add(new VarInsnNode(FSTORE, 11));

                    list.add(new VarInsnNode(ALOAD, 0));
                    list.add(new FieldInsnNode(GETFIELD, cls.name, getName("swingProgress", "field_78095_p"), "F"));
                    list.add(new InsnNode(FCONST_0));
                    list.add(new InsnNode(FCMPG));
                    LabelNode l_con_swing = new LabelNode();
                    list.add(new JumpInsnNode(IFLE, l_con_swing));
                    LabelNode l_con_else = new LabelNode();

                    list.add(new VarInsnNode(ALOAD, 0));
                    list.add(new VarInsnNode(ALOAD, 7));
                    list.add(new MethodInsnNode(INVOKEVIRTUAL, cls.name, getName("getMainHand", "func_187072_a"), "(Lnet/minecraft/entity/Entity;)Lnet/minecraft/util/EnumHandSide;", false));
                    list.add(new FieldInsnNode(GETSTATIC, "net/minecraft/util/EnumHandSide", "RIGHT", "Lnet/minecraft/util/EnumHandSide;"));
                    LabelNode l_con_hand = new LabelNode();
                    list.add(new JumpInsnNode(IF_ACMPNE, l_con_hand));
                    list.add(new VarInsnNode(ALOAD, 0));
                    list.add(new FieldInsnNode(GETFIELD, cls.name, "swimAnimation", "F"));
                    list.add(new VarInsnNode(FSTORE, 11));
                    list.add(new JumpInsnNode(GOTO, l_con_else));
                    list.add(l_con_hand);
//                    list.add(new FrameNode(F_SAME, 0, null, 0, null));
                    list.add(new VarInsnNode(ALOAD, 0));
                    list.add(new FieldInsnNode(GETFIELD, cls.name, "swimAnimation", "F"));
                    list.add(new VarInsnNode(FSTORE, 10));
                    list.add(new JumpInsnNode(GOTO, l_con_else));
                    list.add(l_con_swing);
//                    list.add(new FrameNode(F_SAME, 0, null, 0, null));
                    list.add(new VarInsnNode(ALOAD, 0));
                    list.add(new FieldInsnNode(GETFIELD, cls.name, "swimAnimation", "F"));
                    list.add(new VarInsnNode(FSTORE, 10));
                    list.add(new VarInsnNode(ALOAD, 0));
                    list.add(new FieldInsnNode(GETFIELD, cls.name, "swimAnimation", "F"));
                    list.add(new VarInsnNode(FSTORE, 11));

                    list.add(l_con_else);
//                    list.add(new FrameNode(F_SAME, 0, null, 0, null));

                    {
                        list.add(new VarInsnNode(FLOAD, 9));
                        list.add(new LdcInsnNode(14F));
                        list.add(new InsnNode(FCMPG));
                        LabelNode l_con_14 = new LabelNode();
                        list.add(new JumpInsnNode(IFGE, l_con_14));
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new FieldInsnNode(GETFIELD, cls.name, getName("bipedLeftArm", "field_178724_i"), "Lnet/minecraft/client/model/ModelRenderer;"));
                        list.add(new VarInsnNode(FLOAD, 11));
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new FieldInsnNode(GETFIELD, cls.name, getName("bipedLeftArm", "field_178724_i"), "Lnet/minecraft/client/model/ModelRenderer;"));
                        list.add(new FieldInsnNode(GETFIELD, "net/minecraft/client/model/ModelRenderer", getName("rotateAngleX", "field_78795_f"), "F"));
                        list.add(new InsnNode(FCONST_0));
                        list.add(new MethodInsnNode(INVOKESTATIC, cls.name, "rotLerpRad", "(FFF)F", false));
                        list.add(new FieldInsnNode(PUTFIELD, "net/minecraft/client/model/ModelRenderer", getName("rotateAngleX", "field_78795_f"), "F"));
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new FieldInsnNode(GETFIELD, cls.name, getName("bipedRightArm", "field_178723_h"), "Lnet/minecraft/client/model/ModelRenderer;"));
                        list.add(new VarInsnNode(FLOAD, 10));
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new FieldInsnNode(GETFIELD, cls.name, getName("bipedRightArm", "field_178723_h"), "Lnet/minecraft/client/model/ModelRenderer;"));
                        list.add(new FieldInsnNode(GETFIELD, "net/minecraft/client/model/ModelRenderer", getName("rotateAngleX", "field_78795_f"), "F"));
                        list.add(new InsnNode(FCONST_0));
                        list.add(new MethodInsnNode(INVOKESTATIC, "surreal/backportium/util/RandomHelper", "lerp", "(FFF)F", false));
                        list.add(new FieldInsnNode(PUTFIELD, "net/minecraft/client/model/ModelRenderer", getName("rotateAngleX", "field_78795_f"), "F"));
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new FieldInsnNode(GETFIELD, cls.name, getName("bipedLeftArm", "field_178724_i"), "Lnet/minecraft/client/model/ModelRenderer;"));
                        list.add(new VarInsnNode(FLOAD, 11));
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new FieldInsnNode(GETFIELD, cls.name, getName("bipedLeftArm", "field_178724_i"), "Lnet/minecraft/client/model/ModelRenderer;"));
                        list.add(new FieldInsnNode(GETFIELD, "net/minecraft/client/model/ModelRenderer", getName("rotateAngleY", "field_78796_g"), "F"));
                        list.add(new FieldInsnNode(GETSTATIC, "java/lang/Math", "PI", "D"));
                        list.add(new InsnNode(D2F));
                        list.add(new MethodInsnNode(INVOKESTATIC, cls.name, "rotLerpRad", "(FFF)F", false));
                        list.add(new FieldInsnNode(PUTFIELD, "net/minecraft/client/model/ModelRenderer", getName("rotateAngleY", "field_78796_g"), "F"));
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new FieldInsnNode(GETFIELD, cls.name, getName("bipedRightArm", "field_178723_h"), "Lnet/minecraft/client/model/ModelRenderer;"));
                        list.add(new VarInsnNode(FLOAD, 10));
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new FieldInsnNode(GETFIELD, cls.name, getName("bipedRightArm", "field_178723_h"), "Lnet/minecraft/client/model/ModelRenderer;"));
                        list.add(new FieldInsnNode(GETFIELD, "net/minecraft/client/model/ModelRenderer", getName("rotateAngleY", "field_78796_g"), "F"));
                        list.add(new FieldInsnNode(GETSTATIC, "java/lang/Math", "PI", "D"));
                        list.add(new InsnNode(D2F));
                        list.add(new MethodInsnNode(INVOKESTATIC, "surreal/backportium/util/RandomHelper", "lerp", "(FFF)F", false));
                        list.add(new FieldInsnNode(PUTFIELD, "net/minecraft/client/model/ModelRenderer", getName("rotateAngleY", "field_78796_g"), "F"));
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new FieldInsnNode(GETFIELD, cls.name, getName("bipedLeftArm", "field_178724_i"), "Lnet/minecraft/client/model/ModelRenderer;"));
                        list.add(new VarInsnNode(FLOAD, 11));
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new FieldInsnNode(GETFIELD, cls.name, getName("bipedLeftArm", "field_178724_i"), "Lnet/minecraft/client/model/ModelRenderer;"));
                        list.add(new FieldInsnNode(GETFIELD, "net/minecraft/client/model/ModelRenderer", getName("rotateAngleZ", "field_78808_h"), "F"));
                        list.add(new FieldInsnNode(GETSTATIC, "java/lang/Math", "PI", "D"));
                        list.add(new InsnNode(D2F));
                        list.add(new LdcInsnNode(1.8707964F));
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new VarInsnNode(FLOAD, 9));
                        list.add(new MethodInsnNode(INVOKEVIRTUAL, cls.name, "getArmAngleSq", "(F)F", false));
                        list.add(new InsnNode(FMUL));
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new LdcInsnNode(14F));
                        list.add(new MethodInsnNode(INVOKEVIRTUAL, cls.name, "getArmAngleSq", "(F)F", false));
                        list.add(new InsnNode(FDIV));
                        list.add(new InsnNode(FADD));
                        list.add(new MethodInsnNode(INVOKESTATIC, cls.name, "rotLerpRad", "(FFF)F", false));
                        list.add(new FieldInsnNode(PUTFIELD, "net/minecraft/client/model/ModelRenderer", getName("rotateAngleZ", "field_78808_h"), "F"));
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new FieldInsnNode(GETFIELD, cls.name, getName("bipedRightArm", "field_178723_h"), "Lnet/minecraft/client/model/ModelRenderer;"));
                        list.add(new VarInsnNode(FLOAD, 10));
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new FieldInsnNode(GETFIELD, cls.name, getName("bipedRightArm", "field_178723_h"), "Lnet/minecraft/client/model/ModelRenderer;"));
                        list.add(new FieldInsnNode(GETFIELD, "net/minecraft/client/model/ModelRenderer", getName("rotateAngleZ", "field_78808_h"), "F"));
                        list.add(new FieldInsnNode(GETSTATIC, "java/lang/Math", "PI", "D"));
                        list.add(new InsnNode(D2F));
                        list.add(new LdcInsnNode(1.8707964F));
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new VarInsnNode(FLOAD, 9));
                        list.add(new MethodInsnNode(INVOKEVIRTUAL, cls.name, "getArmAngleSq", "(F)F", false));
                        list.add(new InsnNode(FMUL));
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new LdcInsnNode(14F));
                        list.add(new MethodInsnNode(INVOKEVIRTUAL, cls.name, "getArmAngleSq", "(F)F", false));
                        list.add(new InsnNode(FDIV));
                        list.add(new InsnNode(FSUB));
                        list.add(new MethodInsnNode(INVOKESTATIC, "surreal/backportium/util/RandomHelper", "lerp", "(FFF)F", false));
                        list.add(new FieldInsnNode(PUTFIELD, "net/minecraft/client/model/ModelRenderer", getName("rotateAngleZ", "field_78808_h"), "F"));
                        LabelNode l_con_rot = new LabelNode();
                        list.add(new JumpInsnNode(GOTO, l_con_rot));
                        list.add(l_con_14);
//                        list.add(new FrameNode(F_SAME, 0, null, 0, null));
                        list.add(new VarInsnNode(FLOAD, 9));
                        list.add(new LdcInsnNode(22F));
                        list.add(new InsnNode(FCMPG));
                        LabelNode l_con_22 = new LabelNode();
                        list.add(new JumpInsnNode(IFGE, l_con_22));
                        list.add(new VarInsnNode(FLOAD, 9));
                        list.add(new LdcInsnNode(14F));
                        list.add(new InsnNode(FSUB));
                        list.add(new LdcInsnNode(8F));
                        list.add(new InsnNode(FDIV));
                        list.add(new VarInsnNode(FSTORE, 12));
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new FieldInsnNode(GETFIELD, cls.name, getName("bipedLeftArm", "field_178724_i"), "Lnet/minecraft/client/model/ModelRenderer;"));
                        list.add(new VarInsnNode(FLOAD, 11));
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new FieldInsnNode(GETFIELD, cls.name, getName("bipedLeftArm", "field_178724_i"), "Lnet/minecraft/client/model/ModelRenderer;"));
                        list.add(new FieldInsnNode(GETFIELD, "net/minecraft/client/model/ModelRenderer", getName("rotateAngleX", "field_78795_f"), "F"));
                        list.add(new FieldInsnNode(GETSTATIC, "java/lang/Math", "PI", "D"));
                        list.add(new InsnNode(D2F));
                        list.add(new InsnNode(FCONST_2));
                        list.add(new InsnNode(FDIV));
                        list.add(new VarInsnNode(FLOAD, 12));
                        list.add(new InsnNode(FMUL));
                        list.add(new MethodInsnNode(INVOKESTATIC, cls.name, "rotLerpRad", "(FFF)F", false));
                        list.add(new FieldInsnNode(PUTFIELD, "net/minecraft/client/model/ModelRenderer", getName("rotateAngleX", "field_78795_f"), "F"));
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new FieldInsnNode(GETFIELD, cls.name, getName("bipedRightArm", "field_178723_h"), "Lnet/minecraft/client/model/ModelRenderer;"));
                        list.add(new VarInsnNode(FLOAD, 10));
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new FieldInsnNode(GETFIELD, cls.name, getName("bipedRightArm", "field_178723_h"), "Lnet/minecraft/client/model/ModelRenderer;"));
                        list.add(new FieldInsnNode(GETFIELD, "net/minecraft/client/model/ModelRenderer", getName("rotateAngleX", "field_78795_f"), "F"));
                        list.add(new FieldInsnNode(GETSTATIC, "java/lang/Math", "PI", "D"));
                        list.add(new InsnNode(D2F));
                        list.add(new InsnNode(FCONST_2));
                        list.add(new InsnNode(FDIV));
                        list.add(new VarInsnNode(FLOAD, 12));
                        list.add(new InsnNode(FMUL));
                        list.add(new MethodInsnNode(INVOKESTATIC, "surreal/backportium/util/RandomHelper", "lerp", "(FFF)F", false));
                        list.add(new FieldInsnNode(PUTFIELD, "net/minecraft/client/model/ModelRenderer", getName("rotateAngleX", "field_78795_f"), "F"));
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new FieldInsnNode(GETFIELD, cls.name, getName("bipedLeftArm", "field_178724_i"), "Lnet/minecraft/client/model/ModelRenderer;"));
                        list.add(new VarInsnNode(FLOAD, 11));
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new FieldInsnNode(GETFIELD, cls.name, getName("bipedLeftArm", "field_178724_i"), "Lnet/minecraft/client/model/ModelRenderer;"));
                        list.add(new FieldInsnNode(GETFIELD, "net/minecraft/client/model/ModelRenderer", getName("rotateAngleY", "field_78796_g"), "F"));
                        list.add(new FieldInsnNode(GETSTATIC, "java/lang/Math", "PI", "D"));
                        list.add(new InsnNode(D2F));
                        list.add(new MethodInsnNode(INVOKESTATIC, cls.name, "rotLerpRad", "(FFF)F", false));
                        list.add(new FieldInsnNode(PUTFIELD, "net/minecraft/client/model/ModelRenderer", getName("rotateAngleY", "field_78796_g"), "F"));
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new FieldInsnNode(GETFIELD, cls.name, getName("bipedRightArm", "field_178723_h"), "Lnet/minecraft/client/model/ModelRenderer;"));
                        list.add(new VarInsnNode(FLOAD, 10));
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new FieldInsnNode(GETFIELD, cls.name, getName("bipedRightArm", "field_178723_h"), "Lnet/minecraft/client/model/ModelRenderer;"));
                        list.add(new FieldInsnNode(GETFIELD, "net/minecraft/client/model/ModelRenderer", getName("rotateAngleY", "field_78796_g"), "F"));
                        list.add(new FieldInsnNode(GETSTATIC, "java/lang/Math", "PI", "D"));
                        list.add(new InsnNode(D2F));
                        list.add(new MethodInsnNode(INVOKESTATIC, "surreal/backportium/util/RandomHelper", "lerp", "(FFF)F", false));
                        list.add(new FieldInsnNode(PUTFIELD, "net/minecraft/client/model/ModelRenderer", getName("rotateAngleY", "field_78796_g"), "F"));
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new FieldInsnNode(GETFIELD, cls.name, getName("bipedLeftArm", "field_178724_i"), "Lnet/minecraft/client/model/ModelRenderer;"));
                        list.add(new VarInsnNode(FLOAD, 11));
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new FieldInsnNode(GETFIELD, cls.name, getName("bipedLeftArm", "field_178724_i"), "Lnet/minecraft/client/model/ModelRenderer;"));
                        list.add(new FieldInsnNode(GETFIELD, "net/minecraft/client/model/ModelRenderer", getName("rotateAngleZ", "field_78808_h"), "F"));
                        list.add(new LdcInsnNode(5.012389F));
                        list.add(new LdcInsnNode(1.8707964F));
                        list.add(new VarInsnNode(FLOAD, 12));
                        list.add(new InsnNode(FMUL));
                        list.add(new InsnNode(FSUB));
                        list.add(new MethodInsnNode(INVOKESTATIC, cls.name, "rotLerpRad", "(FFF)F", false));
                        list.add(new FieldInsnNode(PUTFIELD, "net/minecraft/client/model/ModelRenderer", getName("rotateAngleZ", "field_78808_h"), "F"));
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new FieldInsnNode(GETFIELD, cls.name, getName("bipedRightArm", "field_178723_h"), "Lnet/minecraft/client/model/ModelRenderer;"));
                        list.add(new VarInsnNode(FLOAD, 10));
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new FieldInsnNode(GETFIELD, cls.name, getName("bipedRightArm", "field_178723_h"), "Lnet/minecraft/client/model/ModelRenderer;"));
                        list.add(new FieldInsnNode(GETFIELD, "net/minecraft/client/model/ModelRenderer", getName("rotateAngleZ", "field_78808_h"), "F"));
                        list.add(new LdcInsnNode(1.2707963F));
                        list.add(new LdcInsnNode(1.8707964F));
                        list.add(new VarInsnNode(FLOAD, 12));
                        list.add(new InsnNode(FMUL));
                        list.add(new InsnNode(FADD));
                        list.add(new MethodInsnNode(INVOKESTATIC, "surreal/backportium/util/RandomHelper", "lerp", "(FFF)F", false));
                        list.add(new FieldInsnNode(PUTFIELD, "net/minecraft/client/model/ModelRenderer", getName("rotateAngleZ", "field_78808_h"), "F"));
                        list.add(new JumpInsnNode(GOTO, l_con_rot));
                        list.add(l_con_22);
//                        list.add(new FrameNode(F_SAME, 0, null, 0, null));
                        list.add(new VarInsnNode(FLOAD, 9));
                        list.add(new LdcInsnNode(26F));
                        list.add(new InsnNode(FCMPG));
                        list.add(new JumpInsnNode(IFGE, l_con_rot));
                        list.add(new VarInsnNode(FLOAD, 9));
                        list.add(new LdcInsnNode(22F));
                        list.add(new InsnNode(FSUB));
                        list.add(new LdcInsnNode(4F));
                        list.add(new InsnNode(FDIV));
                        list.add(new VarInsnNode(FSTORE, 12));
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new FieldInsnNode(GETFIELD, cls.name, getName("bipedLeftArm", "field_178724_i"), "Lnet/minecraft/client/model/ModelRenderer;"));
                        list.add(new VarInsnNode(FLOAD, 11));
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new FieldInsnNode(GETFIELD, cls.name, getName("bipedLeftArm", "field_178724_i"), "Lnet/minecraft/client/model/ModelRenderer;"));
                        list.add(new FieldInsnNode(GETFIELD, "net/minecraft/client/model/ModelRenderer", getName("rotateAngleX", "field_78795_f"), "F"));
                        list.add(new FieldInsnNode(GETSTATIC, "java/lang/Math", "PI", "D"));
                        list.add(new InsnNode(D2F));
                        list.add(new InsnNode(FCONST_2));
                        list.add(new InsnNode(FDIV));
                        list.add(new FieldInsnNode(GETSTATIC, "java/lang/Math", "PI", "D"));
                        list.add(new InsnNode(D2F));
                        list.add(new InsnNode(FCONST_2));
                        list.add(new InsnNode(FDIV));
                        list.add(new VarInsnNode(FLOAD, 12));
                        list.add(new InsnNode(FMUL));
                        list.add(new InsnNode(FSUB));
                        list.add(new MethodInsnNode(INVOKESTATIC, cls.name, "rotLerpRad", "(FFF)F", false));
                        list.add(new FieldInsnNode(PUTFIELD, "net/minecraft/client/model/ModelRenderer", getName("rotateAngleX", "field_78795_f"), "F"));
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new FieldInsnNode(GETFIELD, cls.name, getName("bipedRightArm", "field_178723_h"), "Lnet/minecraft/client/model/ModelRenderer;"));
                        list.add(new VarInsnNode(FLOAD, 10));
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new FieldInsnNode(GETFIELD, cls.name, getName("bipedRightArm", "field_178723_h"), "Lnet/minecraft/client/model/ModelRenderer;"));
                        list.add(new FieldInsnNode(GETFIELD, "net/minecraft/client/model/ModelRenderer", getName("rotateAngleX", "field_78795_f"), "F"));
                        list.add(new FieldInsnNode(GETSTATIC, "java/lang/Math", "PI", "D"));
                        list.add(new InsnNode(D2F));
                        list.add(new InsnNode(FCONST_2));
                        list.add(new InsnNode(FDIV));
                        list.add(new FieldInsnNode(GETSTATIC, "java/lang/Math", "PI", "D"));
                        list.add(new InsnNode(D2F));
                        list.add(new InsnNode(FCONST_2));
                        list.add(new InsnNode(FDIV));
                        list.add(new VarInsnNode(FLOAD, 12));
                        list.add(new InsnNode(FMUL));
                        list.add(new InsnNode(FSUB));
                        list.add(new MethodInsnNode(INVOKESTATIC, "surreal/backportium/util/RandomHelper", "lerp", "(FFF)F", false));
                        list.add(new FieldInsnNode(PUTFIELD, "net/minecraft/client/model/ModelRenderer", getName("rotateAngleX", "field_78795_f"), "F"));
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new FieldInsnNode(GETFIELD, cls.name, getName("bipedLeftArm", "field_178724_i"), "Lnet/minecraft/client/model/ModelRenderer;"));
                        list.add(new VarInsnNode(FLOAD, 11));
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new FieldInsnNode(GETFIELD, cls.name, getName("bipedLeftArm", "field_178724_i"), "Lnet/minecraft/client/model/ModelRenderer;"));
                        list.add(new FieldInsnNode(GETFIELD, "net/minecraft/client/model/ModelRenderer", getName("rotateAngleY", "field_78796_g"), "F"));
                        list.add(new FieldInsnNode(GETSTATIC, "java/lang/Math", "PI", "D"));
                        list.add(new InsnNode(D2F));
                        list.add(new MethodInsnNode(INVOKESTATIC, cls.name, "rotLerpRad", "(FFF)F", false));
                        list.add(new FieldInsnNode(PUTFIELD, "net/minecraft/client/model/ModelRenderer", getName("rotateAngleY", "field_78796_g"), "F"));
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new FieldInsnNode(GETFIELD, cls.name, getName("bipedRightArm", "field_178723_h"), "Lnet/minecraft/client/model/ModelRenderer;"));
                        list.add(new VarInsnNode(FLOAD, 10));
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new FieldInsnNode(GETFIELD, cls.name, getName("bipedRightArm", "field_178723_h"), "Lnet/minecraft/client/model/ModelRenderer;"));
                        list.add(new FieldInsnNode(GETFIELD, "net/minecraft/client/model/ModelRenderer", getName("rotateAngleY", "field_78796_g"), "F"));
                        list.add(new FieldInsnNode(GETSTATIC, "java/lang/Math", "PI", "D"));
                        list.add(new InsnNode(D2F));
                        list.add(new MethodInsnNode(INVOKESTATIC, "surreal/backportium/util/RandomHelper", "lerp", "(FFF)F", false));
                        list.add(new FieldInsnNode(PUTFIELD, "net/minecraft/client/model/ModelRenderer", getName("rotateAngleY", "field_78796_g"), "F"));
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new FieldInsnNode(GETFIELD, cls.name, getName("bipedLeftArm", "field_178724_i"), "Lnet/minecraft/client/model/ModelRenderer;"));
                        list.add(new VarInsnNode(FLOAD, 11));
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new FieldInsnNode(GETFIELD, cls.name, getName("bipedLeftArm", "field_178724_i"), "Lnet/minecraft/client/model/ModelRenderer;"));
                        list.add(new FieldInsnNode(GETFIELD, "net/minecraft/client/model/ModelRenderer", getName("rotateAngleZ", "field_78808_h"), "F"));
                        list.add(new FieldInsnNode(GETSTATIC, "java/lang/Math", "PI", "D"));
                        list.add(new InsnNode(D2F));
                        list.add(new MethodInsnNode(INVOKESTATIC, cls.name, "rotLerpRad", "(FFF)F", false));
                        list.add(new FieldInsnNode(PUTFIELD, "net/minecraft/client/model/ModelRenderer", getName("rotateAngleZ", "field_78808_h"), "F"));
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new FieldInsnNode(GETFIELD, cls.name, getName("bipedRightArm", "field_178723_h"), "Lnet/minecraft/client/model/ModelRenderer;"));
                        list.add(new VarInsnNode(FLOAD, 10));
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new FieldInsnNode(GETFIELD, cls.name, getName("bipedRightArm", "field_178723_h"), "Lnet/minecraft/client/model/ModelRenderer;"));
                        list.add(new FieldInsnNode(GETFIELD, "net/minecraft/client/model/ModelRenderer", getName("rotateAngleZ", "field_78808_h"), "F"));
                        list.add(new FieldInsnNode(GETSTATIC, "java/lang/Math", "PI", "D"));
                        list.add(new InsnNode(D2F));
                        list.add(new MethodInsnNode(INVOKESTATIC, "surreal/backportium/util/RandomHelper", "lerp", "(FFF)F", false));
                        list.add(new FieldInsnNode(PUTFIELD, "net/minecraft/client/model/ModelRenderer", getName("rotateAngleZ", "field_78808_h"), "F"));

                        list.add(l_con_rot);
//                        list.add(new FrameNode(F_CHOP, 1, null, 0, null));
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new FieldInsnNode(GETFIELD, cls.name, getName("bipedLeftLeg", "field_178722_k"), "Lnet/minecraft/client/model/ModelRenderer;"));
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new FieldInsnNode(GETFIELD, cls.name, "swimAnimation", "F"));
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new FieldInsnNode(GETFIELD, cls.name, getName("bipedLeftLeg", "field_178722_k"), "Lnet/minecraft/client/model/ModelRenderer;"));
                        list.add(new FieldInsnNode(GETFIELD, "net/minecraft/client/model/ModelRenderer", getName("rotateAngleX", "field_78795_f"), "F"));
                        list.add(new LdcInsnNode(0.3F));
                        list.add(new VarInsnNode(FLOAD, 1));
                        list.add(new LdcInsnNode(0.33333334F));
                        list.add(new InsnNode(FMUL));
                        list.add(new FieldInsnNode(GETSTATIC, "java/lang/Math", "PI", "D"));
                        list.add(new InsnNode(D2F));
                        list.add(new InsnNode(FADD));
                        list.add(new MethodInsnNode(INVOKESTATIC, "net/minecraft/util/math/MathHelper", getName("cos", "func_76134_b"), "(F)F", false));
                        list.add(new InsnNode(FMUL));
                        list.add(new MethodInsnNode(INVOKESTATIC, "surreal/backportium/util/RandomHelper", "lerp", "(FFF)F", false));
                        list.add(new FieldInsnNode(PUTFIELD, "net/minecraft/client/model/ModelRenderer", getName("rotateAngleX", "field_78795_f"), "F"));

                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new FieldInsnNode(GETFIELD, cls.name, getName("bipedRightLeg", "field_178721_j"), "Lnet/minecraft/client/model/ModelRenderer;"));

                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new FieldInsnNode(GETFIELD, cls.name, "swimAnimation", "F"));

                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new FieldInsnNode(GETFIELD, cls.name, getName("bipedRightLeg", "field_178721_j"), "Lnet/minecraft/client/model/ModelRenderer;"));
                        list.add(new FieldInsnNode(GETFIELD, "net/minecraft/client/model/ModelRenderer", getName("rotateAngleX", "field_78795_f"), "F"));

                        list.add(new LdcInsnNode(0.3F));
                        list.add(new VarInsnNode(FLOAD, 1));
                        list.add(new LdcInsnNode(0.33333334F));
                        list.add(new InsnNode(FMUL));
                        list.add(new MethodInsnNode(INVOKESTATIC, "net/minecraft/util/math/MathHelper", getName("cos", "func_76134_b"), "(F)F", false));
                        list.add(new InsnNode(FMUL));

                        list.add(new MethodInsnNode(INVOKESTATIC, "surreal/backportium/util/RandomHelper", "lerp", "(FFF)F", false));
                        list.add(new FieldInsnNode(PUTFIELD, "net/minecraft/client/model/ModelRenderer", getName("rotateAngleX", "field_78795_f"), "F"));
                    }
                    list.add(l_con_anim);
                    method.instructions.insertBefore(node, list);
                    break;
                }
            }
        }
        writeClass(cls);
        return write(cls, ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
    }

    public static ClassNode transformEntityLivingBase(ClassNode cls) {
        { // isSwimming
            MethodVisitor m = cls.visitMethod(ACC_PUBLIC, "isSwimming", "()Z", null, null);
            m.visitVarInsn(ALOAD, 0);
            m.visitInsn(ICONST_4);
            m.visitMethodInsn(INVOKEVIRTUAL, cls.name, getName("getFlag", "func_70083_f"), "(I)Z", false);
            m.visitInsn(IRETURN);
        }
        { // setSwimming
            MethodVisitor m = cls.visitMethod(ACC_PUBLIC, "setSwimming", "(Z)V", null, null);
            m.visitVarInsn(ALOAD, 0);
            m.visitInsn(ICONST_4);
            m.visitVarInsn(ILOAD, 1);
            m.visitMethodInsn(INVOKEVIRTUAL, cls.name, getName("setFlag", "func_70052_a"), "(IZ)V", false);
            m.visitInsn(RETURN);
        }
        { // getEyeHeight$Post - Used in EntityPlayer, EntityZombie and EntitySkeleton so they handle riptide and swimming eye height change TODO Change TridentTransformer#transformEntityPlayer getEyeHeight part
            MethodVisitor m = cls.visitMethod(ACC_PROTECTED | ACC_STATIC, "getEyeHeight$Post", "(FLnet/minecraft/entity/EntityLivingBase;)F", null, null);
            {
                m.visitVarInsn(ALOAD, 1);
                m.visitMethodInsn(INVOKEVIRTUAL, cls.name, "isSwimming", "()Z", false);
                Label l_con = new Label();
                m.visitJumpInsn(IFEQ, l_con);
                m.visitLdcInsn(0.4F);
                m.visitInsn(FRETURN);
                m.visitLabel(l_con);
                m.visitFrame(F_SAME, 0, null, 0 ,null);
            }
            {
                m.visitVarInsn(ALOAD, 1);
                m.visitMethodInsn(INVOKEVIRTUAL, cls.name, "isInRiptide", "()Z", false);
                Label l_con = new Label();
                m.visitJumpInsn(IFEQ, l_con);
                m.visitLdcInsn(0.4F);
                m.visitInsn(FRETURN);
                m.visitLabel(l_con);
                m.visitFrame(F_SAME, 0, null, 0 ,null);
            }
            m.visitVarInsn(FLOAD, 0);
            m.visitInsn(FRETURN);
        }
        { // _swimSpeed
            MethodVisitor m = cls.visitMethod(ACC_PRIVATE, "_swimSpeed", "(F)F", null, null);
            m.visitVarInsn(ALOAD, 0);
            m.visitMethodInsn(INVOKEVIRTUAL, cls.name, getName("isSprinting", "func_70051_ag"), "()Z", false);
            Label l_con = new Label();
            m.visitJumpInsn(IFEQ, l_con);
            m.visitLdcInsn(0.9F);
            m.visitInsn(FRETURN);
            m.visitLabel(l_con);
            m.visitFrame(F_SAME, 0, null, 0, null);
            m.visitVarInsn(FLOAD, 1);
            m.visitInsn(FRETURN);
        }
        { // _applyGravity
            MethodVisitor m = cls.visitMethod(ACC_PRIVATE, "_applyGravity", "()V", null, null);
            m.visitVarInsn(ALOAD, 0);
            m.visitMethodInsn(INVOKEVIRTUAL, cls.name, "isSwimming", "()Z", false);
            Label l_con = new Label();
            m.visitJumpInsn(IFEQ, l_con);
            m.visitVarInsn(ALOAD, 0);
            m.visitFieldInsn(GETFIELD, cls.name, getName("motionY", "field_70181_x"), "D");
            m.visitInsn(DCONST_0);
            m.visitInsn(DCMPG);
            Label l_con2 = new Label();
            m.visitJumpInsn(IFGT, l_con2);
            m.visitVarInsn(ALOAD, 0);
            m.visitFieldInsn(GETFIELD, cls.name, getName("motionY", "field_70181_x"), "D");
            m.visitLdcInsn(0.005D);
            m.visitInsn(DSUB);
            m.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "abs", "(D)D", false);
            m.visitLdcInsn(0.003D);
            m.visitInsn(DCMPG);
            m.visitJumpInsn(IFLT, l_con2);
            m.visitVarInsn(ALOAD, 0);
            m.visitFieldInsn(GETFIELD, cls.name, getName("motionY", "field_70181_x"), "D");
            m.visitLdcInsn(0.005D);
            m.visitInsn(DSUB);
            m.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "abs", "(D)D", false);
            m.visitLdcInsn(0.003D);
            m.visitInsn(DCMPG);
            m.visitJumpInsn(IFGE, l_con2);
            m.visitVarInsn(ALOAD, 0);
            m.visitVarInsn(ALOAD, 0);
            m.visitFieldInsn(GETFIELD, cls.name, getName("motionY", "field_70181_x"), "D");
            m.visitLdcInsn(0.003D);
            m.visitInsn(DSUB);
            m.visitFieldInsn(PUTFIELD, cls.name, getName("motionY", "field_70181_x"), "D");
            m.visitInsn(RETURN);
            m.visitLabel(l_con2);
            m.visitFrame(F_SAME, 0, null, 0, null);
            m.visitVarInsn(ALOAD, 0);
            m.visitVarInsn(ALOAD, 0);
            m.visitFieldInsn(GETFIELD, cls.name, getName("motionY", "field_70181_x"), "D");
            m.visitLdcInsn(0.005D);
            m.visitInsn(DSUB);
            m.visitFieldInsn(PUTFIELD, cls.name, getName("motionY", "field_70181_x"), "D");
            m.visitInsn(RETURN);
            m.visitLabel(l_con);
            m.visitFrame(F_SAME, 0, null, 0, null);
            m.visitVarInsn(ALOAD, 0);
            m.visitVarInsn(ALOAD, 0);
            m.visitFieldInsn(GETFIELD, cls.name, getName("motionY", "field_70181_x"), "D");
            m.visitLdcInsn(0.02D);
            m.visitInsn(DSUB);
            m.visitFieldInsn(PUTFIELD, cls.name, getName("motionY", "field_70181_x"), "D");
            m.visitInsn(RETURN);
        }
        for (MethodNode method : cls.methods) {
            if (method.name.equals(getName("travel", "func_191986_a"))) {
                int count = 0;
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == INVOKEVIRTUAL && ((MethodInsnNode) node).name.equals(getName("getWaterSlowDown", "func_189749_co"))) {
                        method.instructions.insertBefore(node, new VarInsnNode(ALOAD, 0));
                        method.instructions.insert(node, new MethodInsnNode(INVOKEVIRTUAL, cls.name, "_swimSpeed", "(F)F", false));
                        count++;
                    }
                    else if (count == 1 && node.getOpcode() == INVOKEVIRTUAL && ((MethodInsnNode) node).name.equals(getName("hasNoGravity", "func_189652_ae"))) {
                        node = iterator.next();
                        for (int i = 0; i < 8; i++) {
                            iterator.next();
                            iterator.remove();
                        }
                        InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new MethodInsnNode(INVOKEVIRTUAL, cls.name, "_applyGravity", "()V", false));
                        method.instructions.insert(node, list);
                        count++;
                    }
                }
                break;
            }
        }
        writeClass(cls);
        return cls;
    }

    // TODO Move to EntityLivingBase (for drowned)
    public static byte[] transformEntityPlayer(byte[] basicClass) {
        ClassNode cls = read(basicClass);
        { // _blockCheck
            MethodVisitor m = cls.visitMethod(ACC_PRIVATE, "_blockCheck", "(Lnet/minecraft/block/Block;)Z", null, null);
            m.visitVarInsn(ALOAD, 1);
            m.visitTypeInsn(INSTANCEOF, "net/minecraft/block/BlockLiquid");
            Label l_con = new Label();
            m.visitJumpInsn(IFEQ, l_con);
            m.visitInsn(ICONST_1);
            m.visitInsn(IRETURN);
            m.visitLabel(l_con);
            m.visitFrame(F_SAME, 0, null, 0, null);
            m.visitVarInsn(ALOAD, 1);
            m.visitTypeInsn(INSTANCEOF, "net/minecraftforge/fluids/IFluidBlock");
            m.visitInsn(IRETURN);
        }
        for (MethodNode method : cls.methods) {
            if (method.name.equals("<init>")) {
                AbstractInsnNode node = method.instructions.getLast();
                while (node.getOpcode() != RETURN) node = node.getPrevious();
                InsnList list = new InsnList();
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new MethodInsnNode(INVOKEVIRTUAL, cls.name, getName("getEyeHeight", "func_70047_e"), "()F", false));
                list.add(new FieldInsnNode(PUTFIELD, cls.name, "playerEyeHeight", "F"));
                // TODO Toggleable crawling
                method.instructions.insertBefore(node, list);
            }
            else if (method.name.equals(getName("travel", "func_191986_a"))) {
                AbstractInsnNode node = method.instructions.getFirst();
                while (node.getOpcode() != ALOAD) node = node.getNext();
                InsnList list = new InsnList();
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new MethodInsnNode(INVOKEVIRTUAL, cls.name, "isSwimming", "()Z", false));
                LabelNode l_con_swimming = new LabelNode();
                list.add(new JumpInsnNode(IFEQ, l_con_swimming));
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new MethodInsnNode(INVOKEVIRTUAL, cls.name, getName("isRiding", "func_184218_aH"), "()Z", false));
                list.add(new JumpInsnNode(IFNE, l_con_swimming));
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new MethodInsnNode(INVOKEVIRTUAL, cls.name, getName("getLookVec", "func_70040_Z"), "()Lnet/minecraft/util/math/Vec3d;", false));
                list.add(new FieldInsnNode(GETFIELD, "net/minecraft/util/math/Vec3d", getName("y", "field_72448_b"), "D"));
                list.add(new VarInsnNode(DSTORE, 4));
                list.add(new VarInsnNode(DLOAD, 4));
                list.add(new InsnNode(DCONST_0));
                list.add(new InsnNode(DCMPG));
                LabelNode l_con_yLook = new LabelNode();
                list.add(new JumpInsnNode(IFLE, l_con_yLook));
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new FieldInsnNode(GETFIELD, cls.name, getName("isJumping", "field_70703_bu"), "Z"));
                LabelNode l_con_blockCheck = new LabelNode();
                list.add(new JumpInsnNode(IFEQ, l_con_blockCheck));
                list.add(new JumpInsnNode(GOTO, l_con_yLook));
                list.add(l_con_blockCheck);
                list.add(new FrameNode(F_APPEND, 1, new Object[] { DOUBLE }, 0, null));
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new FieldInsnNode(GETFIELD, cls.name, getName("world", "field_70170_p"), "Lnet/minecraft/world/World;"));
                list.add(new TypeInsnNode(NEW, "net/minecraft/util/math/BlockPos"));
                list.add(new InsnNode(DUP));
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new FieldInsnNode(GETFIELD, cls.name, getName("posX", "field_70165_t"), "D"));
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new FieldInsnNode(GETFIELD, cls.name, getName("posY", "field_70163_u"), "D"));
                list.add(new InsnNode(DCONST_1));
                list.add(new InsnNode(DADD));
                list.add(new LdcInsnNode(0.1D));
                list.add(new InsnNode(DSUB));
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new FieldInsnNode(GETFIELD, cls.name, getName("posZ", "field_70161_v"), "D"));
                list.add(new MethodInsnNode(INVOKESPECIAL, "net/minecraft/util/math/BlockPos", "<init>", "(DDD)V", false));
                list.add(new MethodInsnNode(INVOKEVIRTUAL, "net/minecraft/world/World", getName("getBlockState", "func_180495_p"), "(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/state/IBlockState;", false));
                list.add(new MethodInsnNode(INVOKEINTERFACE, "net/minecraft/block/state/IBlockState", getName("getBlock", "func_177230_c"), "()Lnet/minecraft/block/Block;", true));
                list.add(new MethodInsnNode(INVOKEVIRTUAL, cls.name, "_blockCheck", "(Lnet/minecraft/block/Block;)Z", false));
                list.add(new JumpInsnNode(IFEQ, l_con_swimming));
                list.add(l_con_yLook);
                list.add(new FrameNode(F_SAME, 0, null, 0, null));
                list.add(new VarInsnNode(DLOAD ,4));
                list.add(new LdcInsnNode(-0.2D));
                list.add(new InsnNode(DCMPG));
                LabelNode l_con_large = new LabelNode();
                list.add(new JumpInsnNode(IFGE, l_con_large));
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new VarInsnNode(DLOAD, 4));
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new FieldInsnNode(GETFIELD, cls.name, getName("motionY", "field_70181_x"), "D"));
                list.add(new InsnNode(DSUB));
                list.add(new LdcInsnNode(0.085D));
                list.add(new InsnNode(DMUL));
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new FieldInsnNode(GETFIELD, cls.name, getName("motionY", "field_70181_x"), "D"));
                list.add(new InsnNode(DADD));
                list.add(new FieldInsnNode(PUTFIELD, cls.name, getName("motionY", "field_70181_x"), "D"));
                list.add(new JumpInsnNode(GOTO, l_con_swimming));
                list.add(l_con_large);
                list.add(new FrameNode(F_SAME, 0, null, 0, null));
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new VarInsnNode(DLOAD, 4));
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new FieldInsnNode(GETFIELD, cls.name, getName("motionY", "field_70181_x"), "D"));
                list.add(new InsnNode(DSUB));
                list.add(new LdcInsnNode(0.06D));
                list.add(new InsnNode(DMUL));
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new FieldInsnNode(GETFIELD, cls.name, getName("motionY", "field_70181_x"), "D"));
                list.add(new InsnNode(DADD));
                list.add(new FieldInsnNode(PUTFIELD, cls.name, getName("motionY", "field_70181_x"), "D"));
                list.add(l_con_swimming);
                list.add(new FrameNode(F_CHOP, 1, null, 0, null));
                method.instructions.insertBefore(node, list);
            }
            else if (method.name.equals(getName("updateSize", "func_184808_cD"))) {
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == GETFIELD) {
                        node = node.getPrevious().getPrevious();
                        InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new MethodInsnNode(INVOKEVIRTUAL, cls.name, "isSwimming", "()Z", false));
                        LabelNode l_con = new LabelNode();
                        list.add(new JumpInsnNode(IFEQ, l_con));
                        list.add(new LdcInsnNode(0.6F));
                        list.add(new VarInsnNode(FSTORE, 1));
                        list.add(new LdcInsnNode(0.6F));
                        list.add(new VarInsnNode(FSTORE, 2));
                        list.add(l_con);
                        list.add(new FrameNode(F_SAME, 0, null, 0 ,null));
                        method.instructions.insertBefore(node, list);
                        break;
                    }
                }
            }
            else if (method.name.equals(getName("getEyeHeight", "func_70047_e"))) {
                AbstractInsnNode node = method.instructions.getLast();
                while (node.getOpcode() != FRETURN) node = node.getPrevious();
                InsnList list = new InsnList();
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new MethodInsnNode(INVOKESTATIC, "net/minecraft/entity/EntityLivingBase", "getEyeHeight$Post", "(FLnet/minecraft/entity/EntityLivingBase;)F", false));
                method.instructions.insertBefore(node, list);
                break;
            }
        }
        {
            cls.visitField(ACC_PROTECTED, "eyesInWater", "Z", null, false);
            cls.visitField(ACC_PROTECTED, "eyesInWaterPlayer", "Z", null, false);
            cls.visitField(ACC_PRIVATE, "playerEyeHeight", "F", null, 0.0F);
            cls.visitField(ACC_PRIVATE, "prevEyeHeight", "F", null, 0.0F);
            cls.visitField(ACC_PRIVATE, "swimAnimation", "F", null, 0.0F);
            cls.visitField(ACC_PRIVATE, "lastSwimAnimation", "F", null, 0.0F);
            cls.visitField(ACC_PRIVATE, "timeUnderwater", "F", null, 0.0F);
        }
        { // findEntitySizeScaleFactor
            MethodVisitor m = cls.visitMethod(ACC_PRIVATE, "findEntitySizeScaleFactor", "()F", null, null);
            m.visitInsn(FCONST_1);
            m.visitVarInsn(FSTORE, 1);
            m.visitVarInsn(FLOAD, 1);
            m.visitVarInsn(ALOAD, 0);
            m.visitMethodInsn(INVOKESTATIC, cls.name, "TrinketsAndBaubles$getResizeFactor", "(Lnet/minecraft/entity/player/EntityPlayer;)F", false);
            m.visitInsn(FMUL);
            m.visitVarInsn(FSTORE, 1);
            m.visitVarInsn(FLOAD, 1);
            m.visitVarInsn(ALOAD, 0);
            m.visitMethodInsn(INVOKESTATIC, cls.name, "ChiseledMe$getResizeFactor", "(Lnet/minecraft/entity/player/EntityPlayer;)F", false);
            m.visitInsn(FMUL);
            m.visitVarInsn(FSTORE, 1);
            m.visitVarInsn(FLOAD, 1);
            m.visitInsn(FRETURN);
        }
        { // findEyeScaleFactor
            MethodVisitor m = cls.visitMethod(ACC_PRIVATE, "findEyeScaleFactor", "()F", null, null);
            m.visitInsn(FCONST_1);
            m.visitVarInsn(FSTORE, 1);
            m.visitVarInsn(FLOAD, 1);
            m.visitVarInsn(ALOAD, 0);
            m.visitMethodInsn(INVOKESTATIC, cls.name, "ArtemisLib$getEyeFactor", "(Lnet/minecraft/entity/player/EntityPlayer;)F", false);
            m.visitInsn(FMUL);
            m.visitVarInsn(FSTORE, 1);
            m.visitVarInsn(FLOAD, 1);
            m.visitVarInsn(ALOAD, 0);
            m.visitMethodInsn(INVOKESTATIC, cls.name, "ChiseledMe$getResizeFactor", "(Lnet/minecraft/entity/player/EntityPlayer;)F", false);
            m.visitInsn(FMUL);
            m.visitVarInsn(FSTORE, 1);
            m.visitVarInsn(FLOAD, 1);
            m.visitInsn(FRETURN);
        }
        { // handleEntitySizeScaling
            MethodVisitor m = cls.visitMethod(ACC_PRIVATE, "handleEntitySizeScaling", "(FF)V", null, null);
            m.visitVarInsn(ALOAD, 0);
            m.visitMethodInsn(INVOKEVIRTUAL, cls.name, "findEntitySizeScaleFactor", "()F", false);
            m.visitVarInsn(FSTORE, 3);
            m.visitVarInsn(ALOAD, 0);
            m.visitVarInsn(FLOAD, 2);
            m.visitVarInsn(FLOAD, 3);
            m.visitInsn(FMUL);
            m.visitFieldInsn(PUTFIELD, cls.name, getName("height", "field_70131_O"), "F");
            m.visitVarInsn(ALOAD, 0);
            m.visitVarInsn(FLOAD, 1);
            m.visitVarInsn(FLOAD, 3);
            m.visitInsn(FMUL);
            m.visitFieldInsn(PUTFIELD, cls.name, getName("width", "field_70130_N"), "F");
            m.visitInsn(RETURN);
        }
        { // onEntityUpdate
            String methodName = getName("onEntityUpdate", "func_70030_z");
            MethodVisitor m = cls.visitMethod(ACC_PUBLIC, methodName, "()V", null, null);
            m.visitVarInsn(ALOAD, 0);
            m.visitMethodInsn(INVOKESPECIAL, cls.superName, methodName, "()V", false);
            Label l_con = new Label();
            // TODO Witchery Resurrected transforming integration?;
            m.visitVarInsn(ALOAD, 0);
            m.visitMethodInsn(INVOKEVIRTUAL, cls.name, getName("isInWater", "func_70090_H"), "()Z", false);
            Label l_con_water = new Label();
            m.visitJumpInsn(IFEQ, l_con_water);
            m.visitVarInsn(ALOAD, 0);
            m.visitMethodInsn(INVOKEVIRTUAL, cls.name, getName("isSpectator", "func_175149_v"), "()Z", false);
            Label l_con_spectator = new Label();
            m.visitJumpInsn(IFEQ, l_con_spectator);
            m.visitVarInsn(ALOAD, 0);
            m.visitVarInsn(ALOAD, 0);
            m.visitFieldInsn(GETFIELD, cls.name, "timeUnderwater", "F");
            m.visitLdcInsn(10F);
            m.visitInsn(FADD);
            m.visitInsn(F2I);
            m.visitInsn(ICONST_0);
            m.visitLdcInsn(600);
            m.visitMethodInsn(INVOKESTATIC, "net/minecraft/util/math/MathHelper", getName("clamp", "func_76125_a"), "(III)I", false);
            m.visitInsn(I2F);
            m.visitFieldInsn(PUTFIELD, cls.name, "timeUnderwater", "F");
            m.visitJumpInsn(GOTO, l_con);
            m.visitLabel(l_con_spectator);
            m.visitFrame(F_SAME, 0, null, 0, null);
            m.visitVarInsn(ALOAD, 0);
            m.visitVarInsn(ALOAD, 0);
            m.visitFieldInsn(GETFIELD, cls.name, "timeUnderwater", "F");
            m.visitInsn(FCONST_1);
            m.visitInsn(FADD);
            m.visitInsn(F2I);
            m.visitInsn(ICONST_0);
            m.visitLdcInsn(600);
            m.visitMethodInsn(INVOKESTATIC, "net/minecraft/util/math/MathHelper", getName("clamp", "func_76125_a"), "(III)I", false);
            m.visitInsn(I2F);
            m.visitFieldInsn(PUTFIELD, cls.name, "timeUnderwater", "F");
            m.visitJumpInsn(GOTO, l_con);
            m.visitLabel(l_con_water);
            m.visitFrame(F_SAME, 0, null, 0, null);
            m.visitVarInsn(ALOAD, 0);
            m.visitFieldInsn(GETFIELD, cls.name, "timeUnderwater", "F");
            m.visitInsn(FCONST_0);
            m.visitInsn(FCMPG);
            m.visitJumpInsn(IFLE, l_con);
            m.visitVarInsn(ALOAD, 0);
            m.visitVarInsn(ALOAD, 0);
            m.visitFieldInsn(GETFIELD, cls.name, "timeUnderwater", "F");
            m.visitLdcInsn(10F);
            m.visitInsn(FSUB);
            m.visitInsn(F2I);
            m.visitInsn(ICONST_0);
            m.visitLdcInsn(600);
            m.visitMethodInsn(INVOKESTATIC, "net/minecraft/util/math/MathHelper", getName("clamp", "func_76125_a"), "(III)I", false);
            m.visitInsn(I2F);
            m.visitFieldInsn(PUTFIELD, cls.name, "timeUnderwater", "F");
            m.visitLabel(l_con);
            m.visitFrame(F_SAME, 0, null, 0 ,null);
            m.visitVarInsn(ALOAD, 0);
            m.visitMethodInsn(INVOKEVIRTUAL, cls.name, "updateEyesInWater", "()V", false);
            m.visitVarInsn(ALOAD, 0);
            m.visitMethodInsn(INVOKEVIRTUAL, cls.name, "updateSwimming", "()V", false);
            m.visitVarInsn(ALOAD, 0);
            m.visitMethodInsn(INVOKEVIRTUAL, cls.name, "updateSwimAnimation", "()V", false);
            m.visitInsn(RETURN);
        }
        { // getWaterVision

        }
        { // canForceCrawling

        }
        { // isForcingCrawling

        }
        { // setForcingCrawling

        }
        { // getSwimAnimation
            MethodVisitor m = cls.visitMethod(ACC_PUBLIC, "getSwimAnimation", "(F)F", null, null);
            m.visitVarInsn(FLOAD, 1);
            m.visitVarInsn(ALOAD, 0);
            m.visitFieldInsn(GETFIELD, cls.name, "lastSwimAnimation", "F");
            m.visitVarInsn(ALOAD, 0);
            m.visitFieldInsn(GETFIELD, cls.name, "swimAnimation", "F");
            m.visitMethodInsn(INVOKESTATIC, "surreal/backportium/util/RandomHelper", "lerp", "(FFF)F", false);
            m.visitInsn(FRETURN);
        }
        { // updateSwimAnimation
            MethodVisitor m = cls.visitMethod(ACC_PRIVATE, "updateSwimAnimation", "()V", null, null);
            m.visitVarInsn(ALOAD, 0);
            m.visitVarInsn(ALOAD, 0);
            m.visitFieldInsn(GETFIELD, cls.name, "swimAnimation", "F");
            m.visitFieldInsn(PUTFIELD, cls.name, "lastSwimAnimation", "F");
            m.visitVarInsn(ALOAD, 0);
            m.visitMethodInsn(INVOKEVIRTUAL, cls.name, "isSwimming", "()Z", false);
            Label l_con_swimming = new Label();
            m.visitJumpInsn(IFEQ, l_con_swimming);
            m.visitVarInsn(ALOAD, 0);
            m.visitInsn(FCONST_1);
            m.visitVarInsn(ALOAD, 0);
            m.visitFieldInsn(GETFIELD, cls.name, "swimAnimation", "F");
            m.visitLdcInsn(0.09F);
            m.visitInsn(FADD);
            m.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "min", "(FF)F", false);
            m.visitFieldInsn(PUTFIELD, cls.name, "swimAnimation", "F");
            m.visitInsn(RETURN);
            m.visitLabel(l_con_swimming);
            m.visitFrame(F_SAME, 0, null, 0, null);
            m.visitVarInsn(ALOAD, 0);
            m.visitInsn(FCONST_0);
            m.visitVarInsn(ALOAD, 0);
            m.visitFieldInsn(GETFIELD, cls.name, "swimAnimation", "F");
            m.visitLdcInsn(0.09F);
            m.visitInsn(FSUB);
            m.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "max", "(FF)F", false);
            m.visitFieldInsn(PUTFIELD, cls.name, "swimAnimation", "F");
            m.visitInsn(RETURN);
        }
        { // canSwim
            MethodVisitor m = cls.visitMethod(ACC_PUBLIC, "canSwim", "()Z", null, null);
            m.visitVarInsn(ALOAD, 0);
            m.visitFieldInsn(GETFIELD, cls.name, "eyesInWater", "Z");
            Label l_con = new Label();
            m.visitJumpInsn(IFEQ, l_con);
            m.visitVarInsn(ALOAD, 0);
            m.visitMethodInsn(INVOKEVIRTUAL, cls.name, getName("isInWater", "func_70090_H"), "()Z", false);
            m.visitInsn(IRETURN);
            m.visitLabel(l_con);
            m.visitFrame(F_SAME, 0, null, 0, null);
            m.visitInsn(ICONST_0);
            m.visitInsn(IRETURN);
        }
        { // updateSwimming
            MethodVisitor m = cls.visitMethod(ACC_PUBLIC, "updateSwimming", "()V", null, null);
            m.visitVarInsn(ALOAD, 0);
            m.visitFieldInsn(GETFIELD, cls.name, getName("capabilities", "field_71075_bZ"), "Lnet/minecraft/entity/player/PlayerCapabilities;");
            m.visitFieldInsn(GETFIELD, "net/minecraft/entity/player/PlayerCapabilities", getName("isFlying", "field_75100_b"), "Z");
            Label l_con_flying = new Label();
            m.visitJumpInsn(IFEQ, l_con_flying);
            m.visitVarInsn(ALOAD, 0);
            m.visitInsn(ICONST_0);
            m.visitMethodInsn(INVOKEVIRTUAL, cls.name, "setSwimming", "(Z)V", false);
            m.visitInsn(RETURN);
            m.visitLabel(l_con_flying);
            m.visitFrame(F_SAME, 0, null, 0, null);
            m.visitVarInsn(ALOAD, 0);
            m.visitMethodInsn(INVOKEVIRTUAL, cls.name, "isSwimming", "()Z", false);
            Label l_con_swimming = new Label();
            m.visitJumpInsn(IFEQ, l_con_swimming);
            {
                m.visitVarInsn(ALOAD, 0);
                m.visitMethodInsn(INVOKEVIRTUAL, cls.name, getName("isSprinting", "func_70051_ag"), "()Z", false);
                m.visitJumpInsn(IFEQ, l_con_swimming);
                m.visitVarInsn(ALOAD, 0);
                m.visitMethodInsn(INVOKEVIRTUAL, cls.name, getName("isInWater", "func_70090_H"), "()Z", false);
                m.visitJumpInsn(IFEQ, l_con_swimming);
                m.visitVarInsn(ALOAD, 0);
                m.visitMethodInsn(INVOKEVIRTUAL, cls.name, getName("isRiding", "func_184218_aH"), "()Z", false);
                m.visitJumpInsn(IFNE, l_con_swimming);
                m.visitVarInsn(ALOAD, 0);
                m.visitInsn(ICONST_1);
                m.visitMethodInsn(INVOKEVIRTUAL, cls.name, "setSwimming", "(Z)V", false);
                m.visitInsn(RETURN);
            }
            m.visitLabel(l_con_swimming);
            m.visitFrame(F_SAME, 0, null, 0, null);
            {
                m.visitVarInsn(ALOAD, 0);
                m.visitMethodInsn(INVOKEVIRTUAL, cls.name, getName("isSprinting", "func_70051_ag"), "()Z", false);
                Label l_con_check = new Label();
                m.visitJumpInsn(IFEQ, l_con_check);
                m.visitVarInsn(ALOAD, 0);
                m.visitMethodInsn(INVOKEVIRTUAL, cls.name, "canSwim", "()Z", false);
                m.visitJumpInsn(IFEQ, l_con_check);
                m.visitVarInsn(ALOAD, 0);
                m.visitMethodInsn(INVOKEVIRTUAL, cls.name, getName("isRiding", "func_184218_aH"), "()Z", false);
                m.visitJumpInsn(IFNE, l_con_check);
                m.visitVarInsn(ALOAD, 0);
                m.visitInsn(ICONST_1);
                m.visitMethodInsn(INVOKEVIRTUAL, cls.name, "setSwimming", "(Z)V", false);
                m.visitInsn(RETURN);
                m.visitLabel(l_con_check);
                m.visitFrame(F_SAME, 0, null, 0, null);
            }
            m.visitVarInsn(ALOAD, 0);
            m.visitInsn(ICONST_0);
            m.visitMethodInsn(INVOKEVIRTUAL, cls.name, "setSwimming", "(Z)V", false);
            m.visitInsn(RETURN);
        }
        { // updateEyesInWater
            MethodVisitor m = cls.visitMethod(ACC_PRIVATE, "updateEyesInWater", "()V", null, null);
            m.visitVarInsn(ALOAD, 0);
            m.visitVarInsn(ALOAD, 0);
            m.visitFieldInsn(GETSTATIC, "net/minecraft/block/material/Material", getName("WATER", "field_151586_h"), "Lnet/minecraft/block/material/Material;");
            m.visitMethodInsn(INVOKEVIRTUAL, cls.name, getName("isInsideOfMaterial", "func_70055_a"), "(Lnet/minecraft/block/material/Material;)Z", false);
            m.visitFieldInsn(PUTFIELD, cls.name, "eyesInWater", "Z");
            m.visitInsn(RETURN);
        }
        { // updateEyesInWaterPlayer
            MethodVisitor m = cls.visitMethod(ACC_PRIVATE, "updateEyesInWaterPlayer", "()Z", null, null);
            m.visitVarInsn(ALOAD, 0);
            m.visitVarInsn(ALOAD, 0);
            m.visitFieldInsn(GETSTATIC, "net/minecraft/block/material/Material", getName("WATER", "field_151586_h"), "Lnet/minecraft/block/material/Material;");
            m.visitMethodInsn(INVOKEVIRTUAL, cls.name, getName("isInsideOfMaterial", "func_70055_a"), "(Lnet/minecraft/block/material/Material;)Z", false);
            m.visitFieldInsn(PUTFIELD, cls.name, "eyesInWaterPlayer", "Z");
            m.visitVarInsn(ALOAD, 0);
            m.visitFieldInsn(GETFIELD, cls.name, "eyesInWaterPlayer", "Z");
            m.visitInsn(IRETURN);
        }
        { // recalculateEyeHeight TODO Handle
            MethodVisitor m = cls.visitMethod(ACC_PRIVATE, "recalculateEyeHeight", "()V", null, null);
            m.visitVarInsn(ALOAD, 0);
            m.visitVarInsn(ALOAD, 0);
            m.visitMethodInsn(INVOKEVIRTUAL, cls.name, getName("getEyeHeight", "func_70047_e"), "()F", false);
            m.visitFieldInsn(PUTFIELD, cls.name, "playerEyeHeight", "F");
            m.visitVarInsn(ALOAD, 0);
            m.visitVarInsn(ALOAD, 0);
            m.visitFieldInsn(GETFIELD, cls.name, "eyeHeight", "F");
            m.visitFieldInsn(PUTFIELD, cls.name, "prevEyeHeight", "F");
            m.visitInsn(RETURN);
        }
        { // TrinketsAndBaubles$getResizeFactor
            MethodVisitor m = cls.visitMethod(ACC_PRIVATE | ACC_STATIC, "TrinketsAndBaubles$getResizeFactor", "(Lnet/minecraft/entity/player/EntityPlayer;)F", null, null);
            m.visitLdcInsn("xat");
            m.visitMethodInsn(INVOKESTATIC, "net/minecraftforge/fml/common/Loader", "isModLoaded", "(Ljava/lang/String;)Z", false);
            Label l_con_loaded = new Label();
            m.visitJumpInsn(IFEQ, l_con_loaded);
            m.visitVarInsn(ALOAD, 0);
            m.visitMethodInsn(INVOKESTATIC, "xzeroair/trinkets/capabilities/Capabilities", "getEntityRace", "(Lnet/minecraft/entity/player/EntityPlayer;)Lxzeroair/trinkets/capabilities/race/EntityProperties;", false);
            m.visitVarInsn(ASTORE, 1);
            m.visitVarInsn(ALOAD, 1);
            m.visitJumpInsn(IFNULL, l_con_loaded);
            m.visitVarInsn(ALOAD, 1);
            m.visitMethodInsn(INVOKEVIRTUAL, "xzeroair/trinkets/capabilities/race/EntityProperties", "getSize", "()I", false);
            m.visitInsn(I2F);
            m.visitLdcInsn(100F);
            m.visitInsn(FDIV);
            m.visitInsn(FRETURN);
            m.visitLabel(l_con_loaded);
            m.visitFrame(F_CHOP, 1, null, 0, null);
            m.visitInsn(FCONST_1);
            m.visitInsn(FRETURN);
        }
        { // ChiseledMe$getResizeFactor
            MethodVisitor m = cls.visitMethod(ACC_PRIVATE | ACC_STATIC, "ChiseledMe$getResizeFactor", "(Lnet/minecraft/entity/player/EntityPlayer;)F", null, null);
            m.visitLdcInsn("chiseled_me");
            m.visitMethodInsn(INVOKESTATIC, "net/minecraftforge/fml/common/Loader", "isModLoaded", "(Ljava/lang/String;)Z", false);
            Label l_con_loaded = new Label();
            m.visitJumpInsn(IFEQ, l_con_loaded);
            m.visitVarInsn(ALOAD, 0);
            m.visitTypeInsn(CHECKCAST, "dev/necauqua/mods/cm/api/ISized");
            m.visitMethodInsn(INVOKEINTERFACE, "dev/necauqua/mods/cm/api/ISized", "getSizeCM", "()D", true);
            m.visitInsn(D2F);
            m.visitInsn(FRETURN);
            m.visitLabel(l_con_loaded);
            m.visitFrame(F_SAME, 0, null, 0, null);
            m.visitInsn(FCONST_1);
            m.visitInsn(FRETURN);
        }
        { // ArtemisLib$getEyeFactor
            MethodVisitor m = cls.visitMethod(ACC_PRIVATE | ACC_STATIC, "ArtemisLib$getEyeFactor", "(Lnet/minecraft/entity/player/EntityPlayer;)F", null, null);
            m.visitLdcInsn("artemislib");
            m.visitMethodInsn(INVOKESTATIC, "net/minecraftforge/fml/common/Loader", "isModLoaded", "(Ljava/lang/String;)Z", false);
            Label l_con = new Label();
            m.visitJumpInsn(IFEQ, l_con);
            m.visitVarInsn(ALOAD, 0);
            m.visitMethodInsn(INVOKEVIRTUAL, cls.name, "isSwimming", "()Z", false);
            m.visitJumpInsn(IFEQ, l_con);
            m.visitVarInsn(ALOAD, 0);
            m.visitMethodInsn(INVOKEVIRTUAL, cls.name, getName("getAttributeMap", "func_110140_aT"), "()Lnet/minecraft/entity/ai/attributes/AbstractAttributeMap;", false);
            m.visitFieldInsn(GETSTATIC, "com/artemis/artemislib/util/attributes/ArtemisLibAttributes", "ENTITY_HEIGHT", "Lnet/minecraft/entity/ai/attributes/IAttribute;");
            m.visitMethodInsn(INVOKEVIRTUAL, "net/minecraft/entity/ai/attributes/AbstractAttributeMap", getName("getAttributeInstance", "func_111151_a"), "(Lnet/minecraft/entity/ai/attributes/IAttribute;)Lnet/minecraft/entity/ai/attributes/IAttributeInstance;", false);
            m.visitMethodInsn(INVOKEINTERFACE, "net/minecraft/entity/ai/attributes/IAttributeInstance", getName("getAttributeValue", "func_111126_e"), "()D", true);
            m.visitInsn(D2F);
            m.visitLdcInsn(3F);
            m.visitInsn(FMUL);
            m.visitInsn(FRETURN);
            m.visitLabel(l_con);
            m.visitFrame(F_SAME, 0, null, 0, null);
            m.visitInsn(FCONST_1);
            m.visitInsn(FRETURN);
        }
//        return write(cls);
        writeClass(cls);
        return write(cls);
    }
}
