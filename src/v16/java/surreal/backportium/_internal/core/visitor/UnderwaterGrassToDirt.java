package surreal.backportium._internal.core.visitor;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import surreal.backportium._internal.bytecode.asm.LeClassVisitor;
import surreal.backportium._internal.world.LoggedAccess;

import java.util.function.Function;

import static _mod.Constants.V_UNDERWATER_GRASS_TO_DIRT;

public final class UnderwaterGrassToDirt {

    private static final String HOOKS = V_UNDERWATER_GRASS_TO_DIRT + "$Hooks";

    @Nullable
    public static Function<ClassVisitor, ClassVisitor> visit(String name, String transformedName, byte[] bytes) {
        switch (transformedName) {
            case "net.minecraft.block.BlockGrass":
            case "net.minecraft.block.BlockMycelium": return GrassLike::new;
            default: return null;
        }
    }

    private static final class GrassLike extends LeClassVisitor {

        public GrassLike(ClassVisitor cv) {
            super(cv);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
            if (name.equals(getName("updateTick", "func_180650_b"))) return new UpdateTick(mv);
            return mv;
        }

        private static final class UpdateTick extends MethodVisitor {

            public UpdateTick(MethodVisitor mv) {
                super(ASM5, mv);
            }

            @Override
            public void visitCode() {
                super.visitCode();
                super.visitVarInsn(ALOAD, 1);
                super.visitVarInsn(ALOAD, 2);
                super.visitMethodInsn(INVOKESTATIC, HOOKS, "GrassLike$handleUnderwater", "(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)Z", false);
                Label l_con = new Label();
                super.visitJumpInsn(IFEQ, l_con);
                super.visitInsn(RETURN);
                super.visitLabel(l_con);
                super.visitFrame(F_SAME, 0, null, 0, null);
            }
        }
    }

    @SuppressWarnings("unused")
    public static final class Hooks {

        public static boolean GrassLike$handleUnderwater(World world, BlockPos pos) {
            if (world.isRemote || !world.isAreaLoaded(pos, 3)) {
                return true;
            }
            IBlockState above = world.getBlockState(pos.up());
            if (above.getMaterial().isLiquid() || LoggedAccess.cast(world).getLoggedState(pos.up()).getMaterial().isLiquid()) {
                world.setBlockState(pos, Blocks.DIRT.getDefaultState());
                return true;
            }
            return false;
        }

        private Hooks() {}
    }

    private UnderwaterGrassToDirt() {}
}
