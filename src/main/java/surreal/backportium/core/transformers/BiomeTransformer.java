package surreal.backportium.core.transformers;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.tree.*;

import java.util.Iterator;

public class BiomeTransformer extends BasicTransformer {

    // TODO Localize getBiomeName
    public static byte[] transformBiome(byte[] basicClass) {
        ClassNode cls = read(basicClass);
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
            }
            else if (method.name.equals(getName("generateBiomeTerrain", "func_180628_b"))) {
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
                                ldc.cst = "Legacy Frozen Ocean";
                                continue;
                            }
                            StringBuilder builder = new StringBuilder(str.length());
                            for (int i = 0; i < str.length(); i++) {
                                char c = str.charAt(i);
                                if (Character.isUpperCase(c) && i != 0 && str.charAt(i - 1) != ' ') {
                                    builder.append(' ').append(c);
                                }
                                else builder.append(c);
                            }
                            ldc.cst = builder.toString();
                        }
                    }
                }
                break;
            }
        }
        return write(cls);
    }
}
