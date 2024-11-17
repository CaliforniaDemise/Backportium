package surreal.backportium.core.transformers;

import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.tree.*;

public class BiomeTransformer extends BasicTransformer {

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
            else if (method.name.equals(getName("generateBiomeTerrain", ""))) {
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
                // INSIDE Y-LOOP //
//                AbstractInsnNode node = method.instructions.getLast();
//                while (!(node instanceof IincInsnNode)) node = node.getPrevious();
//                InsnList list = new InsnList();
//                list.add(new VarInsnNode(ALOAD, 0));
//                list.add(new VarInsnNode(ALOAD, 1));
//                list.add(new VarInsnNode(ALOAD, 2));
//                list.add(new VarInsnNode(ALOAD, 3));
//                list.add(new VarInsnNode(ILOAD, 4));
//                list.add(new VarInsnNode(ILOAD, 16));
//                list.add(new VarInsnNode(ILOAD, 5));
//                list.add(new VarInsnNode(DLOAD, 6));
//                list.add(new MethodInsnNode(INVOKEVIRTUAL, cls.name, "generateTerrain", "(Lnet/minecraft/world/World;Ljava/util/Random;Lnet/minecraft/world/chunk/ChunkPrimer;IIID)V", false));
//                method.instructions.insertBefore(node, list);
//                break;
            }
        }
        writeClass(cls);
        return write(cls);
    }
}
