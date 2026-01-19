package surreal.backportium._internal.core.visitor;

import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import surreal.backportium._internal.bytecode.asm.LeClassVisitor;

import java.util.function.Function;

import static _mod.Constants.V_FIX_BANNER_SOUND;

/**
 * Fix Banners not having a placing sound.
 **/
public final class FixBannerSound {

    private static final String HOOKS = V_FIX_BANNER_SOUND + "$Hooks";

    @Nullable
    public static Function<ClassVisitor, ClassVisitor> visit(String name, String transformedName, byte[] bytes) {
        if (transformedName.equals("net.minecraft.item.ItemBanner")) return ItemBanner::new;
        return null;
    }

    private static final class ItemBanner extends LeClassVisitor {

        public ItemBanner(ClassVisitor cv) {
            super(cv);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
            if (name.equals(getName("onItemUse", "func_180614_a"))) return new OnItemUse(mv);
            return mv;
        }

        private static final class OnItemUse extends MethodVisitor {

            private int count = 0;

            public OnItemUse(MethodVisitor mv) {
                super(ASM5, mv);
            }

            @Override
            public void visitFieldInsn(int opcode, String owner, String name, String desc) {
                if (opcode == GETSTATIC && name.equals("SUCCESS")) {
                    ++this.count;
                    if (this.count == 2) {
                        super.visitVarInsn(ALOAD, 1);
                        super.visitVarInsn(ALOAD, 2);
                        super.visitVarInsn(ALOAD, 3);
                        super.visitMethodInsn(INVOKESTATIC, HOOKS, "$playSound", "(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)V", false);
                    }
                }
                super.visitFieldInsn(opcode, owner, name, desc);
            }
        }
    }

    @SuppressWarnings("unused")
    public static final class Hooks {

        public static void $playSound(EntityPlayer player, World world, BlockPos pos) {
            IBlockState state = world.getBlockState(pos);
            SoundType soundType = state.getBlock().getSoundType(state, world, pos, player);
            world.playSound(null, pos, soundType.getPlaceSound(), SoundCategory.BLOCKS, (soundType.getVolume() + 1.0F) / 2.0F, soundType.getPitch() * 0.8F);
        }

        private Hooks() {}
    }
}
