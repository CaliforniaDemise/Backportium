package surreal.backportium._internal.core.visitor;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import surreal.backportium._internal.bytecode.asm.LeClassVisitor;
import surreal.backportium.init.ModBlocks;

import java.util.function.Function;

public final class UncarvedPumpkinTransformer {

    private static final String HOOKS = "surreal/backportium/_internal/core/visitor/UncarvedPumpkinTransformer$Hooks";

    public static Function<ClassVisitor, ClassVisitor> visit(String name, String transformedName, byte[] bytes) {
        switch (transformedName) {
            case "net.minecraft.block.BlockFence": return cv -> new BlockFenceLikeVisitor(cv, "func_194142_e");
            case "net.minecraft.block.BlockWall": return cv -> new BlockFenceLikeVisitor(cv, "func_194143_e");
            case "net.minecraft.block.BlockPane": return cv -> new BlockFenceLikeVisitor(cv, "func_193394_e");
            case "net.minecraft.block.BlockStem": return BlockStemVisitor::new;
            case "net.minecraft.stats.StatList": return StatListVisitor::new;
            case "net.minecraft.world.gen.feature.WorldGenPumpkin": return WorldGenPumpkinVisitor::new;
        }
        return null;
    }

    private static class BlockFenceLikeVisitor extends LeClassVisitor {

        private final String srgName;

        public BlockFenceLikeVisitor(ClassVisitor cv, String srgName) {
            super(cv);
            this.srgName = srgName;
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
            if (name.equals(getName("isExcepBlockForAttachWithPiston", srgName))) return new IsExcepBlockForAttachWithPiston(mv);
            return mv;
        }

        private static class IsExcepBlockForAttachWithPiston extends MethodVisitor {

            public IsExcepBlockForAttachWithPiston(MethodVisitor mv) {
                super(ASM5, mv);
            }

            @Override
            public void visitJumpInsn(int opcode, Label label) {
                super.visitJumpInsn(opcode, label);
                if (opcode == IF_ACMPEQ) {
                    super.visitVarInsn(ALOAD, 0);
                    super.visitMethodInsn(INVOKESTATIC, HOOKS, "$getPumpkin", "()Lnet/minecraft/block/Block;", false);
                    super.visitJumpInsn(IF_ACMPEQ, label);
                }
            }
        }
    }

    private static class BlockStemVisitor extends LeClassVisitor {

        public BlockStemVisitor(ClassVisitor cv) {
            super(cv);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
            if (name.equals(getName("getActualState", "func_176221_a")) || name.equals(getName("updateTick", "func_180650_b")))
                return new GetCropVisitor(mv);
            return mv;
        }

        private static class GetCropVisitor extends MethodVisitor {

            public GetCropVisitor(MethodVisitor mv) {
                super(ASM5, mv);
            }

            @Override
            public void visitFieldInsn(int opcode, String owner, String name, String desc) {
                super.visitFieldInsn(opcode, owner, name, desc);
                if (opcode == GETFIELD && name.equals(getName("crop", "field_149877_a"))) {
                    super.visitMethodInsn(INVOKESTATIC, HOOKS, "BlockStem$getPumpkin", "(Lnet/minecraft/block/Block;)Lnet/minecraft/block/Block;", false);
                }
            }
        }

        private static class GetSeedItemVisitor extends MethodVisitor {

            public GetSeedItemVisitor(MethodVisitor mv) {
                super(ASM5, mv);
            }

            @Override
            public void visitFieldInsn(int opcode, String owner, String name, String desc) {
                if (opcode == GETSTATIC && name.equals(getName("PUMPKIN", "field_150423_aK"))) {
                    super.visitMethodInsn(INVOKESTATIC, HOOKS, "$getPumpkin", "()Lnet/minecraft/block/Block;", false);
                    return;
                }
                super.visitFieldInsn(opcode, owner, name, desc);
            }
        }
    }

    private static class StatListVisitor extends LeClassVisitor {

        public StatListVisitor(ClassVisitor cv) {
            super(cv);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
            if (name.equals(getName("replaceAllSimilarBlocks", "func_75924_a"))) return new ReplaceAllSimilarBlocksVisitor(mv);
            return mv;
        }

        private static class ReplaceAllSimilarBlocksVisitor extends MethodVisitor {

            public ReplaceAllSimilarBlocksVisitor(MethodVisitor mv) {
                super(ASM5, mv);
            }

            @Override
            public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
                super.visitMethodInsn(opcode, owner, name, desc, itf);
                if (opcode == INVOKESTATIC) {
                    super.visitVarInsn(ALOAD, 0);
                    super.visitLdcInsn("backportium:pumpkin");
                    super.visitMethodInsn(INVOKESTATIC, "net/minecraft/block/Block", getName("getBlockFromName", "func_149684_b"), "(Ljava/lang/String;)Lnet/minecraft/block/Block;", false);
                    super.visitFieldInsn(GETSTATIC, "net/minecraft/init/Blocks", getName("PUMPKIN", "field_150423_aK"), "Lnet/minecraft/block/Block;");
                    super.visitVarInsn(ILOAD, 1);
                    super.visitMethodInsn(INVOKESTATIC, "net/minecraft/stats/StatList", getName("mergeStatBases", "func_151180_a"), "([Lnet/minecraft/stats/StatBase;Lnet/minecraft/block/Block;Lnet/minecraft/block/Block;Z)V", false);
                }
            }
        }
    }

    private static class WorldGenPumpkinVisitor extends LeClassVisitor {

        public WorldGenPumpkinVisitor(ClassVisitor cv) {
            super(cv);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            MethodVisitor mv =  super.visitMethod(access, name, desc, signature, exceptions);
            if (name.equals(getName("generate", "func_180709_b"))) return new GenerateVisitor(mv);
            return mv;
        }

        private static class GenerateVisitor extends MethodVisitor {

            private boolean check = false;

            public GenerateVisitor(MethodVisitor mv) {
                super(ASM5, mv);
            }

            @Override
            public void visitFieldInsn(int opcode, String owner, String name, String desc) {
                if (!check && opcode == GETSTATIC && name.equals(getName("PUMPKIN", "field_150423_aK"))) {
                    check = true;
                    super.visitMethodInsn(INVOKESTATIC, HOOKS, "$getPumpkin", "()Lnet/minecraft/block/Block;", false);
                    return;
                }
                super.visitFieldInsn(opcode, owner, name, desc);
            }

            @Override
            public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
                if (opcode == INVOKEVIRTUAL && name.equals(getName("setBlockState", "func_180501_a"))) {
                    super.visitMethodInsn(INVOKESTATIC, HOOKS, "WorldGenPumpkin$setBlockState", "(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;I)Z", false);
                    return;
                }
                super.visitMethodInsn(opcode, owner, name, desc, itf);
            }
        }
    }

    @SuppressWarnings("unused")
    public static class Hooks {

        public static Block $getPumpkin() {
            return ModBlocks.PUMPKIN;
        }

        public static Block BlockStem$getPumpkin(Block crop) {
            return crop == Blocks.PUMPKIN ? ModBlocks.PUMPKIN : crop;
        }

        public static boolean WorldGenPumpkin$setBlockState(World world, BlockPos pos, IBlockState state, int flags) {
            return world.setBlockState(pos, ModBlocks.PUMPKIN.getDefaultState(), flags);
        }
    }

    private UncarvedPumpkinTransformer() {}
}
