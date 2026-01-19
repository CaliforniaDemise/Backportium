package surreal.backportium._internal.core.visitor;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import surreal.backportium._internal.bytecode.asm.LeClassVisitor;
import surreal.backportium.init.ModBlocks;

import java.util.function.Function;

import static _mod.Constants.V_UNCARVED_PUMPKIN;

public final class UncarvedPumpkin {

    private static final String HOOKS = V_UNCARVED_PUMPKIN + "$Hooks";

    @Nullable
    public static Function<ClassVisitor, ClassVisitor> visit(String name, String transformedName, byte[] bytes) {
        switch (transformedName) {
            case "net.minecraft.block.BlockFence": return cv -> new BlockFenceLike(cv, "func_194142_e");
            case "net.minecraft.block.BlockWall": return cv -> new BlockFenceLike(cv, "func_194143_e");
            case "net.minecraft.block.BlockPane": return cv -> new BlockFenceLike(cv, "func_193394_e");
            case "net.minecraft.block.BlockStem": return BlockStem::new;
            case "net.minecraft.stats.StatList": return StatList::new;
            case "net.minecraft.world.gen.feature.WorldGenPumpkin": return WorldGenPumpkin::new;
            default: return null;
        }
    }

    private static final class BlockFenceLike extends LeClassVisitor {

        private final String srgName;

        public BlockFenceLike(ClassVisitor cv, String srgName) {
            super(cv);
            this.srgName = srgName;
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
            if (name.equals(getName("isExcepBlockForAttachWithPiston", srgName))) return new IsExcepBlockForAttachWithPiston(mv);
            return mv;
        }

        private static final class IsExcepBlockForAttachWithPiston extends MethodVisitor {

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

    private static final class BlockStem extends LeClassVisitor {

        public BlockStem(ClassVisitor cv) {
            super(cv);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
            if (name.equals(getName("getActualState", "func_176221_a")) || name.equals(getName("updateTick", "func_180650_b")))
                return new GetCrop(mv);
            return mv;
        }

        private static final class GetCrop extends MethodVisitor {

            public GetCrop(MethodVisitor mv) {
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
    }

    private static final class StatList extends LeClassVisitor {

        public StatList(ClassVisitor cv) {
            super(cv);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
            if (name.equals(getName("replaceAllSimilarBlocks", "func_75924_a"))) return new ReplaceAllSimilarBlocks(mv);
            return mv;
        }

        private static final class ReplaceAllSimilarBlocks extends MethodVisitor {

            public ReplaceAllSimilarBlocks(MethodVisitor mv) {
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

    private static final class WorldGenPumpkin extends LeClassVisitor {

        public WorldGenPumpkin(ClassVisitor cv) {
            super(cv);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            MethodVisitor mv =  super.visitMethod(access, name, desc, signature, exceptions);
            if (name.equals(getName("generate", "func_180709_b"))) return new Generate(mv);
            return mv;
        }

        private static final class Generate extends MethodVisitor {

            private boolean check = false;

            public Generate(MethodVisitor mv) {
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
    public static final class Hooks {

        public static Block $getPumpkin() {
            return ModBlocks.PUMPKIN;
        }

        public static Block BlockStem$getPumpkin(Block crop) {
            return crop == Blocks.PUMPKIN ? ModBlocks.PUMPKIN : crop;
        }

        public static boolean WorldGenPumpkin$setBlockState(World world, BlockPos pos, IBlockState state, int flags) {
            return world.setBlockState(pos, ModBlocks.PUMPKIN.getDefaultState(), flags);
        }

        private Hooks() {}
    }

    private UncarvedPumpkin() {}
}
