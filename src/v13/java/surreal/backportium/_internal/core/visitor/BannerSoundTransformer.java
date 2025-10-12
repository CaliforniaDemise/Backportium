package surreal.backportium._internal.core.visitor;

import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import surreal.backportium._internal.bytecode.asm.LeClassVisitor;

import java.util.function.Function;

/**
 * Fix Banners not having a placing sound.
 **/
public final class BannerSoundTransformer {

    private static final String HOOKS = "surreal/backportium/_internal/core/visitor/BannerSoundTransformer$Hooks";

    public static Function<ClassVisitor, ClassVisitor> visit(String name, String transformedName, byte[] bytes) {
        if (transformedName.equals("net.minecraft.item.ItemBanner")) return ItemBannerVisitor::new;
        return null;
    }

    private static class ItemBannerVisitor extends LeClassVisitor {

        public ItemBannerVisitor(ClassVisitor cv) {
            super(cv);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
            if (name.equals(getName("onItemUse", "func_180614_a"))) return new OnItemUseVisitor(mv);
            return mv;
        }

        private static class OnItemUseVisitor extends MethodVisitor {

            private int count = 0;

            public OnItemUseVisitor(MethodVisitor mv) {
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
    public static class Hooks {
        public static void $playSound(EntityPlayer player, World world, BlockPos pos) {
            IBlockState state = world.getBlockState(pos);
            SoundType soundType = state.getBlock().getSoundType(state, world, pos, player);
            world.playSound(null, pos, soundType.getPlaceSound(), SoundCategory.BLOCKS, (soundType.getVolume() + 1.0F) / 2.0F, soundType.getPitch() * 0.8F);
        }
    }
}
