package surreal.backportium._internal.core.visitor;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import surreal.backportium._internal.bytecode.asm.LeClassVisitor;
import surreal.backportium.api.world.biome.Overridable;

import java.util.Random;
import java.util.function.Function;

import static _mod.Constants.V_MORE_BIOME_OVERRIDE;
import static _mod.Constants.A_OVERRIDABLE_BIOME;

public final class MoreBiomeOverride extends LeClassVisitor {

    private static final String HOOKS = V_MORE_BIOME_OVERRIDE + "$Hooks";
    private static final String OVERRIDABLE = A_OVERRIDABLE_BIOME;

    private MoreBiomeOverride(ClassVisitor cv) {
        super(cv);
    }

    @Nullable
    public static Function<ClassVisitor, ClassVisitor> getClassVisitor(String name, String transformedName) {
        if (transformedName.equals("net.minecraft.world.biome.Biome")) return MoreBiomeOverride::new;
        return null;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, getInterfaces(interfaces, OVERRIDABLE));
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        if (name.equals(getName("getTemperature", "func_180626_a"))) return new GetTemperature(mv);
        if (name.equals(getName("generateBiomeTerrain", "func_180628_b"))) return new GenerateBiomeTerrain(mv);
        return mv;
    }

    private static final class GetTemperature extends MethodVisitor {

        public GetTemperature(MethodVisitor mv) {
            super(ASM5, mv);
        }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
            if (opcode == INVOKEVIRTUAL && name.equals(getName("getDefaultTemperature", "func_185353_n"))) {
                super.visitVarInsn(ALOAD, 1);
                super.visitMethodInsn(INVOKEVIRTUAL, "net/minecraft/world/biome/Biome", "getDefaultTemperature", "(Lnet/minecraft/util/math/BlockPos;)F", false);
                return;
            }
            super.visitMethodInsn(opcode, owner, name, desc, itf);
        }
    }

    private static final class GenerateBiomeTerrain extends MethodVisitor {

        public GenerateBiomeTerrain(MethodVisitor mv) {
            super(ASM5, mv);
        }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
            if (opcode == INVOKEVIRTUAL && name.equals(getName("setBlockState", "func_177855_a"))) {
                super.visitVarInsn(ALOAD, 0);
                super.visitVarInsn(ALOAD, 1);
                super.visitVarInsn(ALOAD, 2);
                super.visitVarInsn(ILOAD, 4);
                super.visitVarInsn(ILOAD, 5);
                super.visitVarInsn(DLOAD, 6);
                super.visitMethodInsn(INVOKESTATIC, HOOKS, "Biome$setBlockState", "(Lnet/minecraft/world/chunk/ChunkPrimer;IIILnet/minecraft/block/state/IBlockState;Lnet/minecraft/world/biome/Biome;Lnet/minecraft/world/World;Ljava/util/Random;IID)V", false);
                return;
            }
            super.visitMethodInsn(opcode, owner, name, desc, itf);
        }
    }

    @SuppressWarnings("unused")
    public static final class Hooks {

        public static void Biome$setBlockState(ChunkPrimer primer, int x, int y, int z, IBlockState state, Biome biome, World world, Random random, int chunkX, int chunkY, double noiseVal) {
            state = Overridable.cast(biome).getTerrainBlock(world, random, primer, chunkX, chunkY, new BlockPos(x, y, z), noiseVal, state);
            primer.setBlockState(x, y, z, state);
        }

        private Hooks() {}
    }
}
