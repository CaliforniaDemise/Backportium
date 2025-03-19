package surreal.backportium.core.v13;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.tree.*;
import surreal.backportium.core.transformers.Transformer;

import java.util.Iterator;

/**
 * Adds a way to easily change biome surface and temperature because I'm lazy.
 * It also adds way to translate biomes.
 **/
// TODO Expand upon it
class BiomeTransformer extends Transformer {

    protected static byte[] transformBiome(byte[] basicClass) {
        ClassNode cls = read(basicClass);
        { // translationKey
            cls.visitField(ACC_PRIVATE, "translationKey", "Ljava/lang/String;", null, null);
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
        for (MethodNode method : cls.methods) {
            if (method.name.equals(getName("getTemperature", "func_180626_a"))) {
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
            else if (method.name.equals(getName("getBiomeName", "func_185359_l"))) {
                {
                    Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                    while (iterator.hasNext()) {
                        AbstractInsnNode node = iterator.next();
                        if (node.getOpcode() == ALOAD) {
                            InsnList list = new InsnList();
                            list.add(new VarInsnNode(ALOAD, 0));
                            list.add(new FieldInsnNode(GETFIELD, cls.name, "translationKey", "Ljava/lang/String;"));
                            LabelNode l_con_null = new LabelNode();
                            list.add(new JumpInsnNode(IFNONNULL, l_con_null));
                            list.add(new VarInsnNode(ALOAD, 0));
                            list.add(new VarInsnNode(ALOAD, 0));
                            list.add(hook("Biome$getTranslationKey", "(Lnet/minecraft/world/biome/Biome;)Ljava/lang/String;"));
                            list.add(new FieldInsnNode(PUTFIELD, cls.name, "translationKey", "Ljava/lang/String;"));
                            list.add(l_con_null);
                            list.add(new FrameNode(F_SAME, 0, null, 0, null));
                            method.instructions.insertBefore(node, list);
                            break;
                        }
                    }
                }
                AbstractInsnNode node = method.instructions.getLast();
                while (node.getOpcode() != ARETURN) node = node.getPrevious();
                InsnList list = new InsnList();
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new FieldInsnNode(GETFIELD, cls.name, "translationKey", "Ljava/lang/String;"));
                list.add(hook("Biome$getLocalizedName", "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;"));
                method.instructions.insertBefore(node, list);
                break;
            }
        }
        return write(cls);
    }

    // Uses I18n for biome properties biomeName
    /**
     * kroeser thought doing this would stop me.
     * But he doesn't know Mojang had my back in 2011 and added
     * a way to format to en us regardless of language that's chosen.
     * Not saying that would stop me, but it makes everything easier :+1:
     **/
    protected static byte[] transformConfigurableBiome(byte[] basicClass) {
        ClassNode cls = read(basicClass);
        for (MethodNode method : cls.methods) {
            if (method.name.equals("constructProperties")) {
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == INVOKESTATIC) {
                        method.instructions.insertBefore(node, new MethodInsnNode(INVOKESTATIC, "net/minecraft/util/text/translation/I18n", getName("translateToFallback", "func_150826_b"), "(Ljava/lang/String;)Ljava/lang/String;", false));
                        iterator.remove();
                        break;
                    }
                }
            }
        }
        return basicClass;
    }

    // TODO Change naming to use registry names instead of biome names
    protected static byte[] transformGuiOverlayDebug(byte[] basicClass) {
        ClassNode cls = read(basicClass);
        for (MethodNode method : cls.methods) {
            if (method.name.equals("call")) {
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == INVOKEVIRTUAL && ((MethodInsnNode) node).name.equals(getName("getBiomeName", "func_185359_l"))) {
                        InsnList list = new InsnList();
                        list.add(new MethodInsnNode(INVOKEVIRTUAL, "net/minecraft/world/biome/Biome", "getRegistryName", "()Lnet/minecraft/util/ResourceLocation;", false));
                        list.add(new MethodInsnNode(INVOKEVIRTUAL, "net/minecraft/util/ResourceLocation", "toString", "()Ljava/lang/String;", false));
                        method.instructions.insertBefore(node, list);
                        iterator.remove();
                        break;
                    }
                }
            }
        }
        return write(cls);
    }

    protected static byte[] transformChunkGeneratorSettings$Serializer(byte[] basicClass) {
        ClassNode cls = read(basicClass);
        for (MethodNode method : cls.methods) {
            if (method.name.equals("deserialize")) {
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                int i = 0;
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == BIPUSH) {
                        method.instructions.insertBefore(node, new LdcInsnNode(Integer.MIN_VALUE));
                        iterator.remove();
                    }
                    else if (node.getOpcode() == ICONST_M1) {
                        i++;
                        if (i == 2) {
                            InsnList list = new InsnList();
                            list.add(new VarInsnNode(ALOAD, 5));
                            list.add(new FieldInsnNode(GETFIELD, "net/minecraft/world/gen/ChunkGeneratorSettings$Factory", getName("fixedBiome", "field_177869_G"), "I"));
                            list.add(hook("Buffet$getBiomeId", "(I)I"));
                            method.instructions.insertBefore(node, list);
                            iterator.remove();
                            break;
                        }
                    }
                }
            }
        }
        return write(cls);
    }
}
