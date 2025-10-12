package surreal.backportium._internal.core.visitor;

import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import surreal.backportium._internal.bytecode.asm.LeClassVisitor;

public final class BuoyancyVisitor {

    private static final String HOOKS = "surreal/backportium/_internal/core/visitor/BuoyancyVisitor$Hooks";
    private static final String MOD_LIST = "surreal/backportium/integration/ModList";

    public static class EntityItemVisitor extends LeClassVisitor {

        public EntityItemVisitor(ClassVisitor cv) {
            super(cv);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
            if (name.equals(getName("onUpdate", "func_70071_h_"))) return new OnUpdateVisitor(mv);
            return mv;
        }

        @Override
        public void visitEnd() {
            super.visitEnd();
            final String thisClass = "net/minecraft/entity/item/EntityItem";
            { // additionalY
                MethodVisitor mv = super.visitMethod(ACC_PRIVATE, "BP$additionalY", "()D", null, null);
                mv.visitFieldInsn(GETSTATIC, MOD_LIST, "AE2", "Z");
                Label l_con = new Label();
                mv.visitJumpInsn(IFEQ, l_con);
                mv.visitMethodInsn(INVOKESTATIC, "appeng/core/AEConfig", "instance", "()Lappeng/core/AEConfig;", false);
                mv.visitFieldInsn(GETSTATIC, "appeng/core/features/AEFeature", "IN_WORLD_PURIFICATION", "Lappeng/core/features/AEFeature;");
                mv.visitMethodInsn(INVOKEVIRTUAL, "appeng/core/AEConfig", "isFeatureEnabled", "(Lappeng/core/features/AEFeature;)Z", false);
                mv.visitJumpInsn(IFEQ, l_con);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitMethodInsn(INVOKEVIRTUAL, thisClass, getName("getItem", "func_92059_d"), "()Lnet/minecraft/item/ItemStack;", false);
                mv.visitMethodInsn(INVOKEVIRTUAL, "net/minecraft/item/ItemStack", getName("getItem", "func_77973_b"), "()Lnet/minecraft/item/Item;", false);
                mv.visitTypeInsn(INSTANCEOF, "appeng/api/implementations/items/IGrowableCrystal");
                mv.visitJumpInsn(IFEQ, l_con);
                mv.visitLdcInsn(0.25D);
                mv.visitInsn(DRETURN);
                mv.visitLabel(l_con);
                mv.visitFrame(F_SAME, 0, null, 0, null);
                mv.visitInsn(DCONST_0);
                mv.visitInsn(DRETURN);
                mv.visitMaxs(1, 0);
            }
        }

        private static class OnUpdateVisitor extends MethodVisitor {

            public OnUpdateVisitor(MethodVisitor mv) {
                super(ASM5, mv);
            }

            @Override
            public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
                final String thisClass = "net/minecraft/entity/item/EntityItem";
                if (opcode == INVOKEVIRTUAL && name.equals(getName("hasNoGravity", "func_189652_ae"))) {
                    super.visitVarInsn(ALOAD, 0);
                    super.visitMethodInsn(INVOKEVIRTUAL, thisClass, "BP$additionalY", "()D", false);
                    super.visitMethodInsn(INVOKESTATIC, HOOKS, "EntityItem$hasNoGravity", "(Lnet/minecraft/entity/Entity;D)Z", false);
                    return;
                }
                super.visitMethodInsn(opcode, owner, name, desc, itf);
            }
        }
    }

    @SuppressWarnings("unused")
    public static class Hooks {

        private static boolean $isBuoyant(Entity entity) {
            return true;
        }

        public static boolean EntityItem$hasNoGravity(Entity entity, double additionalY) {
            if ($isBuoyant(entity)) {
                double eyePosition = entity.posY + (double) entity.getEyeHeight();
                BlockPos eyeBlockPos = new BlockPos(entity.posX, eyePosition, entity.posZ);
                IBlockState state = entity.world.getBlockState(eyeBlockPos);
                if(state.getMaterial() == Material.WATER && state.getBlock() instanceof BlockLiquid) {
                    float thresholdHeight = eyeBlockPos.getY() + BlockLiquid.getBlockLiquidHeight(state, entity.world, eyeBlockPos) + (1.0F / 9.0F);
                    if(eyePosition < thresholdHeight) {
                        if (entity.motionY < 0.06) {
                            entity.motionY += 5.0E-4;
                        }
                        entity.motionX *= 0.99;
                        entity.motionZ *= 0.99;
                        return true;
                    }
                }
            }
            return entity.hasNoGravity();
        }
    }

    private BuoyancyVisitor() {}
}
