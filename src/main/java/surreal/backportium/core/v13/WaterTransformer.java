package surreal.backportium.core.v13;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.tree.*;
import surreal.backportium.core.transformers.Transformer;

import java.util.ArrayList;
import java.util.Iterator;

class WaterTransformer extends Transformer {

    protected static byte[] transformBiome(byte[] basicClass) {
        ClassNode cls = read(basicClass);
        if (cls.interfaces == null) cls.interfaces = new ArrayList<>();
        cls.interfaces.add("surreal/backportium/api/extension/BiomeExtension");
        { // waterFogColor
            cls.visitField(ACC_PRIVATE, "waterFogColor", "I", null, 0);
        }
        { // getWaterFogColor
            MethodVisitor m = cls.visitMethod(ACC_PUBLIC, "getWaterFogColor", "()I", null, null);
            m.visitVarInsn(ALOAD, 0);
            m.visitFieldInsn(GETFIELD, cls.name, "waterFogColor", "I");
            m.visitInsn(IRETURN);
        }
        for (MethodNode method : cls.methods) {
            if (method.name.equals("<init>")) {
                AbstractInsnNode node = method.instructions.getLast();
                while (node.getOpcode() != RETURN) node = node.getPrevious();
                InsnList list = new InsnList();
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new VarInsnNode(ALOAD, 1));
                list.add(new FieldInsnNode(GETFIELD, cls.name + "$BiomeProperties", "waterFogColor", "I"));
                list.add(new FieldInsnNode(PUTFIELD, cls.name, "waterFogColor", "I"));
                method.instructions.insertBefore(node, list);
            }
            else if (method.name.equals("getWaterColorMultiplier")) {
                AbstractInsnNode node = method.instructions.getLast();
                while (node.getOpcode() != IRETURN) node = node.getPrevious();
                method.instructions.insertBefore(node.getPrevious().getPrevious(), new VarInsnNode(ALOAD, 0));
                method.instructions.insertBefore(node, hook("WaterColor$getWaterColor", "(Lnet/minecraft/world/biome/Biome;I)I"));
                break;
            }
        }
        return write(cls);
    }

    protected static byte[] transformBiomeProperties(byte[] basicClass) {
        ClassNode cls = read(basicClass);
        cls.interfaces = new ArrayList<>(1);
        cls.interfaces.add("surreal/backportium/api/extension/BiomePropertiesExtension");
        { // waterFogColor
            cls.visitField(ACC_PROTECTED, "waterFogColor", "I", null, 0);
        }
        for (FieldNode field : cls.fields) {
            if (field.name.equals(getName("waterColor", "field_185417_f"))) {
                field.value = 4159204;
                break;
            }
        }
        for (MethodNode method : cls.methods) {
            if (method.name.equals("<init>")) {
                AbstractInsnNode node = method.instructions.getLast();
                while (node.getOpcode() != RETURN) node = node.getPrevious();
                InsnList list = new InsnList();
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new LdcInsnNode(329011));
                list.add(new FieldInsnNode(PUTFIELD, cls.name, "waterFogColor", "I"));
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new VarInsnNode(ALOAD, 1));
                list.add(hook("WaterColor$defaultWaterColors", "(Lnet/minecraft/world/biome/Biome$BiomeProperties;Ljava/lang/String;)V"));
                method.instructions.insertBefore(node, list);
            }
            else if (method.name.equals(getName("setWaterColor", "func_185402_a"))) {
                {
                    Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                    while (iterator.hasNext()) {
                        AbstractInsnNode node = iterator.next();
                        if (node.getOpcode() == ALOAD) {
                            InsnList list = new InsnList();
                            list.add(new VarInsnNode(ALOAD, 0));
                            list.add(new FieldInsnNode(GETFIELD, cls.name, getName("waterColor", "field_185417_f"), "I"));
                            list.add(new LdcInsnNode(4159204));
                            LabelNode l_con = new LabelNode();
                            list.add(new JumpInsnNode(IF_ICMPEQ, l_con));
                            list.add(new VarInsnNode(ALOAD, 0));
                            list.add(new InsnNode(ARETURN));
                            list.add(l_con);
                            list.add(new FrameNode(F_SAME, 0, null, 0, null));
                            method.instructions.insertBefore(node, list);
                            break;
                        }
                    }
                }
                AbstractInsnNode node = method.instructions.getLast();
                while (node.getOpcode() != PUTFIELD) node = node.getPrevious();
                method.instructions.insertBefore(node, hook("WaterColor$emulateLegacyColor", "(I)I"));
                break;
            }
        }
        { // setWaterFogColor
            MethodVisitor m = cls.visitMethod(ACC_PUBLIC, "setWaterFogColor", "(I)Lnet/minecraft/world/biome/Biome$BiomeProperties;", "(I)TT;", null);
            m.visitVarInsn(ALOAD, 0);
            m.visitVarInsn(ILOAD, 1);
            m.visitFieldInsn(PUTFIELD, cls.name, "waterFogColor", "I");
            m.visitVarInsn(ALOAD, 0);
            m.visitInsn(ARETURN);
        }
        { // setActualWaterColor
            MethodVisitor m = cls.visitMethod(ACC_PUBLIC, "setActualWaterColor", "(I)Lnet/minecraft/world/biome/Biome$BiomeProperties;", "(I)TT;", null);
            m.visitVarInsn(ALOAD, 0);
            m.visitVarInsn(ILOAD, 1);
            m.visitFieldInsn(PUTFIELD, cls.name, getName("waterColor", "field_185417_f"), "I");
            m.visitVarInsn(ALOAD, 0);
            m.visitInsn(ARETURN);
        }
        return write(cls);
    }

    protected static byte[] transformBiomeColorHelper$WaterColor(byte[] basicClass) {
        ClassNode cls = read(basicClass);
        for (MethodNode method : cls.methods) {
            if (method.name.equals(getName("getColorAtPos", "func_180283_a"))) {
                AbstractInsnNode node = method.instructions.getLast();
                while (node.getOpcode() != IRETURN) node = node.getPrevious();
                method.instructions.insertBefore(node.getPrevious().getPrevious(), new VarInsnNode(ALOAD, 1));
                method.instructions.insertBefore(node, hook("WaterColor$getWaterColor", "(Lnet/minecraft/world/biome/Biome;I)I"));
                break;
            }
        }
        return write(cls);
    }

    protected static byte[] transformBlockFluidRenderer(byte[] basicClass) {
        ClassNode cls = read(basicClass);
        for (MethodNode method : cls.methods) {
            if (method.name.equals(getName("initAtlasSprites", "func_178268_a"))) {
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node instanceof LdcInsnNode) {
                        LdcInsnNode ldc = (LdcInsnNode) node;
                        if (ldc.cst.equals("minecraft:blocks/water_still")) ldc.cst = "backportium:blocks/water_still";
                        else if (ldc.cst.equals("minecraft:blocks/water_flow")) {
                            ldc.cst = "backportium:blocks/water_flow";
                            break;
                        }
                    }
                }
            }
            else if (method.name.equals(getName("renderFluid", "func_178270_a"))) {
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                int count = 0;
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node instanceof LdcInsnNode && ((LdcInsnNode) node).cst.equals(0.5F)) {
                        if (count > 0) {
                            int i = count - 1;
                            method.instructions.insertBefore(node, new VarInsnNode(FLOAD, 9 + (i % 3)));
                            method.instructions.remove(node);
                            if (count == 12) break;
                        }
                        count++;
                    }
                }
                break;
            }
        }
        return write(cls);
    }

    protected static byte[] transformTileCrucibleRenderer(byte[] basicClass) {
        ClassNode cls = read(basicClass);
        for (MethodNode method : cls.methods) {
            if (method.name.equals("renderFluid")) {
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == INVOKESTATIC && ((MethodInsnNode) node).owner.endsWith("/Minecraft")) {
                        for (int i = 0; i < 6; i++) {
                            iterator.remove();
                            node = iterator.next();
                        }
                        InsnList list = new InsnList();
                        list.add(new MethodInsnNode(INVOKESTATIC, "net/minecraft/client/Minecraft", getName("getMinecraft", "func_71410_x"), "()Lnet/minecraft/client/Minecraft;", false));
                        list.add(new MethodInsnNode(INVOKEVIRTUAL, "net/minecraft/client/Minecraft", getName("getTextureMapBlocks", "func_147117_R"), "()Lnet/minecraft/client/renderer/texture/TextureMap;", false));
                        list.add(new LdcInsnNode("minecraft:blocks/water_still"));
                        list.add(new MethodInsnNode(INVOKEVIRTUAL, "net/minecraft/client/renderer/texture/TextureMap", getName("getAtlasSprite", "func_110572_b"), "(Ljava/lang/String;)Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;", false));
                        method.instructions.insertBefore(node, list);
                        break;
                    }
                }
            }
        }
        return write(cls);
    }

    protected static byte[] transformItemRenderer(byte[] basicClass) {
        ClassNode cls = read(basicClass);
        for (MethodNode method : cls.methods) {
            if (method.name.equals(getName("renderWaterOverlayTexture", "func_78448_c"))) {
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == INVOKESTATIC && ((MethodInsnNode) node).name.equals(getName("color", "func_179131_c"))) {
                        method.instructions.remove(node.getPrevious());
                        method.instructions.insertBefore(node, new LdcInsnNode(0.1F));
                        break;
                    }
                }
            }
        }
        return write(cls);
    }

    // 1.16 thingy
    protected static byte[] transformEntityPlayer(byte[] basicClass) {
        ClassNode cls = read(basicClass);
        { // getWaterVision
            MethodVisitor m = cls.visitMethod(ACC_PUBLIC, "getWaterVision", "()F", null, null);
            {
                m.visitVarInsn(ALOAD, 0);
                m.visitMethodInsn(INVOKEVIRTUAL, cls.name, getName("isInWater", "func_70090_H"), "()Z", false);
                Label l_con_inWater = new Label();
                m.visitJumpInsn(IFEQ, l_con_inWater);
                m.visitInsn(FCONST_0);
                m.visitInsn(FRETURN);
                m.visitLabel(l_con_inWater);
                m.visitFrame(F_SAME, 0, null, 0, null);
            }
            {
                m.visitVarInsn(ALOAD, 0);
                m.visitFieldInsn(GETFIELD, cls.name, "timeUnderwater", "F");
                m.visitLdcInsn(600F);
                Label l_con_underwater = new Label();
                m.visitInsn(FCMPL);
                m.visitJumpInsn(IFLT, l_con_underwater);
                m.visitInsn(FCONST_1);
                m.visitInsn(FRETURN);
                m.visitLabel(l_con_underwater);
                m.visitFrame(F_SAME, 0, null, 0, null);
            }
            m.visitVarInsn(ALOAD, 0);
            m.visitFieldInsn(GETFIELD, cls.name, "timeUnderwater", "F");
            m.visitLdcInsn(100F);
            m.visitInsn(FDIV);
            m.visitInsn(FCONST_0);
            m.visitInsn(FCONST_1);
            m.visitMethodInsn(INVOKESTATIC, "net/minecraft/util/math/MathHelper", getName("clamp", "func_76131_a"), "(FFF)F", false);
            m.visitLdcInsn(0.6F);
            m.visitInsn(FMUL);
            {
                m.visitVarInsn(ALOAD, 0);
                m.visitFieldInsn(GETFIELD, cls.name, "timeUnderwater", "F");
                m.visitLdcInsn(100F);
                Label l_con_l100 = new Label();
                Label l_goto_l100 = new Label();
                m.visitInsn(FCMPG);
                m.visitJumpInsn(IFGE, l_con_l100);
                m.visitInsn(FCONST_0);
                m.visitJumpInsn(GOTO, l_goto_l100);
                m.visitLabel(l_con_l100);
                m.visitFrame(F_SAME1, 0, null, 1, new Object[] { FLOAT });
                m.visitVarInsn(ALOAD, 0);
                m.visitFieldInsn(GETFIELD, cls.name, "timeUnderwater", "F");
                m.visitLdcInsn(100F);
                m.visitInsn(FSUB);
                m.visitLdcInsn(500F);
                m.visitInsn(FDIV);
                m.visitInsn(FCONST_0);
                m.visitInsn(FCONST_1);
                m.visitMethodInsn(INVOKESTATIC, "net/minecraft/util/math/MathHelper", getName("clamp", "func_76131_a"), "(FFF)F", false);
                m.visitLabel(l_goto_l100);
                m.visitFrame(F_APPEND, 2, new Object[] { FLOAT, FLOAT }, 0, null);
            }
            m.visitLdcInsn(0.39999998F);
            m.visitInsn(FMUL);
            m.visitInsn(FADD);
            m.visitInsn(FRETURN);
        }
        return write(cls, 3);
    }

    protected static byte[] transformEntityRenderer(byte[] basicClass) {
        ClassNode cls = read(basicClass);
        for (MethodNode method : cls.methods) {
            if (method.name.equals(getName("updateFogColor", "func_78466_h"))) {
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == ASTORE && ((VarInsnNode) node).var == 16) {
                        InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 16));
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new FieldInsnNode(GETFIELD, "net/minecraft/client/renderer/EntityRenderer", getName("mc", "field_78531_r"), "Lnet/minecraft/client/Minecraft;"));
                        list.add(new FieldInsnNode(GETFIELD, "net/minecraft/client/Minecraft", getName("world", "field_71441_e"), "Lnet/minecraft/client/multiplayer/WorldClient;"));
                        list.add(new VarInsnNode(ALOAD, 14));
                        list.add(new VarInsnNode(ALOAD, 15));
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new FieldInsnNode(GETFIELD, cls.name, getName("mc", "field_78531_r"), "Lnet/minecraft/client/Minecraft;"));
                        list.add(new FieldInsnNode(GETFIELD, "net/minecraft/client/Minecraft", getName("player", "field_71439_g"), "Lnet/minecraft/client/entity/EntityPlayerSP;"));
                        list.add(new MethodInsnNode(INVOKEVIRTUAL, "net/minecraft/entity/player/EntityPlayer", "getWaterVision", "()F", false));
                        list.add(hook("WaterColor$getWaterFogColor", "(Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;F)Lnet/minecraft/util/math/Vec3d;"));
                        list.add(new VarInsnNode(ASTORE, 16));
                        method.instructions.insert(node, list);
                        break;
                    }
                }
            }
            else if (method.name.equals(getName("setupFog", "func_78468_a"))) {
                int i = 0;
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == INVOKESTATIC && ((MethodInsnNode) node).name.equals(getName("setFogDensity", "func_179095_a"))) {
                        i++;
                        if (i == 4) {
                            InsnList list = new InsnList();
                            list.add(new VarInsnNode(ALOAD, 3));
                            list.add(new VarInsnNode(ALOAD, 3));
                            list.add(new TypeInsnNode(INSTANCEOF, "net/minecraft/entity/player/EntityPlayer"));
                            LabelNode l_con = new LabelNode();
                            LabelNode l_goto = new LabelNode();
                            list.add(new JumpInsnNode(IFEQ, l_con));
                            list.add(new VarInsnNode(ALOAD, 3));
                            list.add(new TypeInsnNode(CHECKCAST, "net/minecraft/entity/player/EntityPlayer"));
                            list.add(new MethodInsnNode(INVOKEVIRTUAL, "net/minecraft/entity/player/EntityPlayer", "getWaterVision", "()F", false));
                            list.add(new JumpInsnNode(GOTO, l_goto));
                            list.add(l_con);
                            list.add(new InsnNode(FCONST_0));
                            list.add(l_goto);
                            list.add(hook("WaterColor$getFogDensity", "(FLnet/minecraft/entity/Entity;F)F"));
                            method.instructions.insertBefore(node, list);
                            break;
                        }
                    }
                }
            }
        }
        return write(cls, 3);
    }

    // TODO Fix this
    // Uses I18n for biome properties biomeName
    protected static byte[] transformCyclopsCore$Name(byte[] basicClass) {
        return basicClass;
    }

    protected static byte[] transformBlockLiquid(byte[] basicClass) {
        ClassNode cls = read(basicClass);
        { // getLightOpacity
            MethodVisitor m = cls.visitMethod(ACC_PUBLIC, getName("getLightOpacity", "func_149717_k"), "(Lnet/minecraft/block/state/IBlockState;)I", null, null);
            m.visitInsn(ICONST_1);
            m.visitInsn(IRETURN);
        }
        return write(cls);
    }
}
