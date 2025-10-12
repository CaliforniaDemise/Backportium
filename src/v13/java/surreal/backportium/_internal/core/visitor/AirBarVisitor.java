package surreal.backportium._internal.core.visitor;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.util.math.BlockPos;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import surreal.backportium._internal.bytecode.asm.LeClassVisitor;
import surreal.backportium.init.ModBlocks;

public final class AirBarVisitor {

    private static final String HOOKS = "surreal/backportium/_internal/core/visitor/AirBarVisitor$Hooks";

    public static class EntityLivingBaseVisitor extends LeClassVisitor {

        public EntityLivingBaseVisitor(ClassVisitor cv) {
            super(cv);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
            if (name.equals(getName("onEntityUpdate", "func_70030_z"))) return new OnEntityUpdateVisitor(mv);
            return mv;
        }

        private static class OnEntityUpdateVisitor extends MethodVisitor {

            public OnEntityUpdateVisitor(MethodVisitor mv) {
                super(ASM5, mv);
            }

            @Override
            public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
                super.visitMethodInsn(opcode, owner, name, desc, itf);
                if (opcode == INVOKEVIRTUAL && name.equals(getName("isInsideOfMaterial", "func_70055_a"))) {
                    super.visitVarInsn(ALOAD, 0);
                    super.visitMethodInsn(INVOKESTATIC, HOOKS, "EntityLivingBase$shouldDeplete", "(ZLnet/minecraft/entity/EntityLivingBase;)Z", false);
                }
            }

            @Override
            public void visitIntInsn(int opcode, int operand) {
                if (operand == 300) {
                    super.visitVarInsn(ALOAD, 0);
                    super.visitMethodInsn(INVOKESTATIC, HOOKS, "EntityLivingBase$getIncreasedAir", "(Lnet/minecraft/entity/Entity;)I", false);
                   return;
                }
                super.visitIntInsn(opcode, operand);
            }
        }
    }

    public static class GuiIngameForgeVisitor extends LeClassVisitor {

        public GuiIngameForgeVisitor(ClassVisitor cv) {
            super(cv);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
            if (name.equals("renderAir")) return new RenderAirVisitor(mv);
            return mv;
        }

        private static class RenderAirVisitor extends MethodVisitor {

            public RenderAirVisitor(MethodVisitor mv) {
                super(ASM5, mv);
            }

            @Override
            public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
                if (opcode == INVOKEVIRTUAL && name.equals(getName("isInsideOfMaterial", "func_70055_a"))) {
                    super.visitMethodInsn(INVOKESTATIC, HOOKS, "GuiIngameForge$shouldRender", "(Lnet/minecraft/entity/Entity;Lnet/minecraft/block/material/Material;)Z", false);
                    return;
                }
                super.visitMethodInsn(opcode, owner, name, desc, itf);
            }
        }
    }

    @SuppressWarnings("unused")
    public static class Hooks {

        public static int EntityLivingBase$getIncreasedAir(Entity entity) {
            int air = Math.max(entity.getAir(), 0);
            return Math.min(air + 4, 300);
        }

        public static boolean EntityLivingBase$shouldDeplete(boolean insideWater, EntityLivingBase entity) {
            if (entity.canBreatheUnderwater()) return false;
            if (entity.isPotionActive(MobEffects.WATER_BREATHING)) return false;
            if (ModBlocks.BUBBLE_COLUMN == null) return insideWater;
            return entity.world.getBlockState(new BlockPos(entity.posX, entity.posY + (double) entity.getEyeHeight(), entity.posZ)).getBlock() != ModBlocks.BUBBLE_COLUMN && insideWater;
        }

        public static boolean GuiIngameForge$shouldRender(Entity player, Material water) {
            return player.getAir() != 300;
        }
    }

    private AirBarVisitor() {}
}
