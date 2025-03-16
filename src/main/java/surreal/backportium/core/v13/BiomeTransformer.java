package surreal.backportium.core.v13;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.tree.*;
import surreal.backportium.core.transformers.Transformer;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Adds way to change temperature based on block pos and fixes biome names.
 * This will most likely get changed when I backport new world generation.
 * Might separate biome name fix too.
 **/
// TODO Expand upon it
class BiomeTransformer extends Transformer {

    protected static byte[] transformBiome(byte[] basicClass) {
        ClassNode cls = read(basicClass);
        if (cls.interfaces == null) cls.interfaces = new ArrayList<>();
        cls.interfaces.add("surreal/backportium/api/extension/BiomeExtension");
        { // waterFogColor
            cls.visitField(ACC_PRIVATE, "waterFogColor", "I", null, 0);
        }
        { // getTheSurface - Used specifically for changing ocean surface. Pretty naive approach
            MethodVisitor m = cls.visitMethod(ACC_PUBLIC, "getTheSurface", "(Lnet/minecraft/world/World;Ljava/util/Random;Lnet/minecraft/world/chunk/ChunkPrimer;IID)Lnet/minecraft/block/state/IBlockState;", null, null);
            m.visitFieldInsn(GETSTATIC, "net/minecraft/world/biome/Biome", getName("GRAVEL", "field_185368_d"), "Lnet/minecraft/block/state/IBlockState;");
            m.visitInsn(ARETURN);
        }
        { // getTheTemperature
            MethodVisitor m = cls.visitMethod(ACC_PUBLIC, "getTheTemperature", "(Lnet/minecraft/util/math/BlockPos;)F", null, null);
            m.visitVarInsn(ALOAD, 0);
            m.visitMethodInsn(INVOKEVIRTUAL, cls.name, getName("getDefaultTemperature", "func_185353_n"), "()F", false);
            m.visitInsn(FRETURN);
        }
        { // generateTerrain
            // public final void generateBiomeTerrain(World worldIn, Random rand, ChunkPrimer chunkPrimerIn, int x, int z, double noiseVal)
            MethodVisitor m = cls.visitMethod(ACC_PUBLIC, "generateTerrain", "(Lnet/minecraft/world/World;Ljava/util/Random;Lnet/minecraft/world/chunk/ChunkPrimer;Lnet/minecraft/util/math/BlockPos$MutableBlockPos;IID)V", null, null);
            m.visitInsn(RETURN);
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
            } else if (method.name.equals(getName("getTemperature", "func_180626_a"))) {
                AbstractInsnNode node = method.instructions.getLast();
                while (node.getOpcode() != INVOKEVIRTUAL) node = node.getPrevious();
                InsnList list = new InsnList();
                list.add(new VarInsnNode(ALOAD, 1));
                list.add(new MethodInsnNode(INVOKEVIRTUAL, cls.name, "getTheTemperature", "(Lnet/minecraft/util/math/BlockPos;)F", false));
                method.instructions.insertBefore(node, list);
                method.instructions.remove(node);
            } else if (method.name.equals(getName("generateBiomeTerrain", "func_180628_b"))) {
                {
                    Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                    while (iterator.hasNext()) {
                        AbstractInsnNode node = iterator.next();
                        if (node.getOpcode() == GETSTATIC && ((FieldInsnNode) node).name.equals(getName("GRAVEL", "field_185368_d"))) {
                            InsnList list = new InsnList();
                            list.add(new VarInsnNode(ALOAD, 0));
                            list.add(new VarInsnNode(ALOAD, 1));
                            list.add(new VarInsnNode(ALOAD, 2));
                            list.add(new VarInsnNode(ALOAD, 3));
                            list.add(new VarInsnNode(ILOAD, 4));
                            list.add(new VarInsnNode(ILOAD, 5));
                            list.add(new VarInsnNode(DLOAD, 6));
                            list.add(new MethodInsnNode(INVOKEVIRTUAL, cls.name, "getTheSurface", "(Lnet/minecraft/world/World;Ljava/util/Random;Lnet/minecraft/world/chunk/ChunkPrimer;IID)Lnet/minecraft/block/state/IBlockState;", false));
                            method.instructions.insertBefore(node, list);
                            iterator.remove();
                            break;
                        }
                    }
                }
                AbstractInsnNode node = method.instructions.getLast();
                while (node.getOpcode() != RETURN) node = node.getPrevious();
                InsnList list = new InsnList();
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new VarInsnNode(ALOAD, 1));
                list.add(new VarInsnNode(ALOAD, 2));
                list.add(new VarInsnNode(ALOAD, 3));
                list.add(new VarInsnNode(ALOAD, 15));
                list.add(new VarInsnNode(ILOAD, 4));
                list.add(new VarInsnNode(ILOAD, 5));
                list.add(new VarInsnNode(DLOAD, 6));
                list.add(new MethodInsnNode(INVOKEVIRTUAL, cls.name, "generateTerrain", "(Lnet/minecraft/world/World;Ljava/util/Random;Lnet/minecraft/world/chunk/ChunkPrimer;Lnet/minecraft/util/math/BlockPos$MutableBlockPos;IID)V", false));
                method.instructions.insertBefore(node, list);
            }
            else if (method.name.equals("getWaterColorMultiplier")) {
                AbstractInsnNode node = method.instructions.getLast();
                while (node.getOpcode() != IRETURN) node = node.getPrevious();
                method.instructions.insertBefore(node.getPrevious().getPrevious(), new VarInsnNode(ALOAD, 0));
                method.instructions.insertBefore(node, hook("WaterColor$getWaterColor", "(Lnet/minecraft/world/biome/Biome;I)I"));
            }
            else if (method.name.equals(getName("registerBiomes", "func_185358_q"))) {
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == DUP) {
                        node = iterator.next();
                        if (node instanceof LdcInsnNode) {
                            LdcInsnNode ldc = (LdcInsnNode) node;
                            String str = (String) ldc.cst;
                            if (str.equals("FrozenOcean")) {
//                                ldc.cst = "Legacy Frozen Ocean";
                                continue;
                            }
                            StringBuilder builder = new StringBuilder(str.length());
                            for (int i = 0; i < str.length(); i++) {
                                char c = str.charAt(i);
                                if (Character.isUpperCase(c) && i != 0 && str.charAt(i - 1) != ' ') {
                                    builder.append(' ').append(c);
                                } else builder.append(c);
                            }
                            ldc.cst = builder.toString();
                        }
                    }
                }
                break;
            }
        }
        writeClass(cls);
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
                            list.add(new FieldInsnNode(GETFIELD, cls.name, "waterColor", "I"));
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
        writeClass(cls);
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
            if (method.name.equals(getName("renderFluid", "func_178270_a"))) {
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
                        list.add(hook("WaterColor$getWaterFogColor", "(Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;)Lnet/minecraft/util/math/Vec3d;"));
                        list.add(new VarInsnNode(ASTORE, 16));
                        method.instructions.insert(node, list);
                        break;
                    }
                }
                break;
            }
        }
        return write(cls);
    }

    // TODO Fix this
    // Uses I18n for biome properties biomeName
    protected static byte[] transformCyclopsCore$Name(byte[] basicClass) {
        return basicClass;
    }
}
