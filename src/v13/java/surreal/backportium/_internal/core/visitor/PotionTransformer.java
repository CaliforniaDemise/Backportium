package surreal.backportium._internal.core.visitor;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import surreal.backportium._internal.bytecode.asm.LeClassVisitor;
import surreal.backportium.init.ModPotions;

import java.util.Objects;
import java.util.function.Function;

public final class PotionTransformer {

    private static final String HOOKS = "surreal/backportium/_internal/core/visitor/PotionTransformer$Hooks";

    public static Function<ClassVisitor, ClassVisitor> visit(String name, String transformedName, byte[] bytes) {
        switch (transformedName) {
            case "net.minecraft.entity.EntityLivingBase": return EntityLivingBaseVisitor::new;
            case "net.minecraft.entity.player.EntityPlayer": return EntityPlayerVisitor::new;
            case "net.minecraft.client.renderer.EntityRenderer": return EntityRendererVisitor::new;
            case "net.minecraft.block.Block": return BlockVisitor::new;
        }
        return null;
    }

    private static class EntityLivingBaseVisitor extends LeClassVisitor {

        public EntityLivingBaseVisitor(ClassVisitor cv) {
            super(cv);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
            if (name.equals(getName("onEntityUpdate", "func_70030_z"))) return new OnEntityUpdateVisitor(mv);
            if (name.equals(getName("getArmSwingAnimationEnd", "func_82166_i"))) return new GetArmSwingAnimationEndVisitor(mv);
            if (name.equals(getName("travel", "func_191986_a"))) return new TravelVisitor(mv);
            return mv;
        }

        private static class OnEntityUpdateVisitor extends MethodVisitor {

            public OnEntityUpdateVisitor(MethodVisitor mv) {
                super(ASM5, mv);
            }

            @Override
            public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
                super.visitMethodInsn(opcode, owner, name, desc, itf);
                if (opcode == INVOKEVIRTUAL && name.equals(getName("isPotionActive", "func_70644_a"))) {
                    super.visitVarInsn(ALOAD, 0);
                    super.visitMethodInsn(INVOKESTATIC, HOOKS, "ConduitPower$isActive", "(ZLnet/minecraft/entity/EntityLivingBase;)Z", false);
                }
            }
        }

        private static class GetArmSwingAnimationEndVisitor extends MethodVisitor {

            private int count = 0;

            public GetArmSwingAnimationEndVisitor(MethodVisitor mv) {
                super(ASM5, mv);
            }

            @Override
            public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
                super.visitMethodInsn(opcode, owner, name, desc, itf);
                if (opcode == INVOKEVIRTUAL) {
                    if (count < 2 && name.equals(getName("isPotionActive", "func_70644_a"))) {
                        count++;
                        super.visitVarInsn(ALOAD, 0);
                        super.visitMethodInsn(INVOKESTATIC, HOOKS, "ConduitPower$isActive", "(ZLnet/minecraft/entity/EntityLivingBase;)Z", false);
                    }
                    else if (count < 2 && name.equals(getName("getActivePotionEffect", "func_70660_b"))) {
                        count++;
                        super.visitVarInsn(ALOAD, 0);
                        super.visitMethodInsn(INVOKESTATIC, HOOKS, "ConduitPower$getAmplifier", "(Lnet/minecraft/potion/PotionEffect;Lnet/minecraft/entity/EntityLivingBase;)Lnet/minecraft/potion/PotionEffect;", false);
                    }
                }
            }
        }

        private static class TravelVisitor extends MethodVisitor {

            public TravelVisitor(MethodVisitor mv) {
                super(ASM5, mv);
            }

            @Override
            public void visitLdcInsn(Object cst) {
                if (cst.equals(0.08D) || cst.equals(-0.08D)) {
                    super.visitVarInsn(ALOAD, 0);
                    super.visitMethodInsn(INVOKESTATIC, HOOKS, "SlowFalling$getSpeed", "(DLnet/minecraft/entity/EntityLivingBase;)D", false);
                }
                super.visitLdcInsn(cst);
            }
        }
    }

    private static class EntityPlayerVisitor extends LeClassVisitor {

        public EntityPlayerVisitor(ClassVisitor cv) {
            super(cv);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
            if (name.equals(getName("getDigSpeed", "func_184813_a"))) return new GetDigSpeed(mv);
            return mv;
        }

        private static class GetDigSpeed extends MethodVisitor {

            public GetDigSpeed(MethodVisitor mv) {
                super(ASM5, mv);
            }

            @Override
            public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
                super.visitMethodInsn(opcode, owner, name, desc, itf);
                if (opcode == INVOKEVIRTUAL) {
                    if (name.equals(getName("isPotionActive", "func_70644_a"))) {
                        super.visitVarInsn(ALOAD, 0);
                        super.visitMethodInsn(INVOKESTATIC, HOOKS,  "ConduitPower$isActive", "(ZLnet/minecraft/entity/EntityLivingBase;)Z", false);
                    }
                    else if (name.equals(getName("getActivePotionEffect", "func_70660_b"))) {
                        super.visitVarInsn(ALOAD, 0);
                        super.visitMethodInsn(INVOKESTATIC, HOOKS, "ConduitPower$getAmplifier", "(Lnet/minecraft/potion/PotionEffect;Lnet/minecraft/entity/EntityLivingBase;)Lnet/minecraft/potion/PotionEffect;", false);
                    }
                }
            }
        }
    }

    private static class EntityRendererVisitor extends LeClassVisitor {

        public EntityRendererVisitor(ClassVisitor cv) {
            super(cv);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
            if (name.equals(getName("updateLightmap", "func_78472_g"))) return new UpdateLightmapVisitor(mv);
            if (name.equals(getName("getNightVisionBrightness", "func_180438_a"))) return new GetNightVisionBrightnessVisitor(mv);
            if (name.equals(getName("updateFogColor", "func_78466_h"))) return new UpdateFogColorVisitor(mv);
            if (name.equals(getName("setupFog", "func_78468_a"))) return new SetupFogVisitor(mv);
            return mv;
        }

        private static class UpdateLightmapVisitor extends MethodVisitor {

            public UpdateLightmapVisitor(MethodVisitor mv) {
                super(ASM5, mv);
            }

            @Override
            public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
                super.visitMethodInsn(opcode, owner, name, desc, itf);
                if (opcode == INVOKEVIRTUAL && name.equals(getName("isPotionActive", "func_70644_a"))) {
                    super.visitMethodInsn(INVOKESTATIC, HOOKS, "ConduitPower$isActiveClient", "(Z)Z", false);
                }
            }
        }

        private static class GetNightVisionBrightnessVisitor extends MethodVisitor {

            public GetNightVisionBrightnessVisitor(MethodVisitor mv) {
                super(ASM5, mv);
            }

            @Override
            public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
                super.visitMethodInsn(opcode, owner, name, desc, itf);
                if (opcode == INVOKEVIRTUAL && name.equals(getName("getActivePotionEffect", "func_70660_b"))) {
                    super.visitVarInsn(ALOAD, 1);
                    super.visitMethodInsn(INVOKESTATIC, HOOKS, "ConduitPower$getPotionEffect", "(Lnet/minecraft/potion/PotionEffect;Lnet/minecraft/entity/EntityLivingBase;)Lnet/minecraft/potion/PotionEffect;", false);
                }
            }

            @Override
            public void visitVarInsn(int opcode, int var) {
                super.visitVarInsn(opcode, var);
                if (opcode == ISTORE) {
                    super.visitVarInsn(ALOAD, 1);
                    super.visitVarInsn(ILOAD, var);
                    super.visitMethodInsn(INVOKESTATIC, HOOKS, "ConduitPower$getNightVisionTime", "(Lnet/minecraft/entity/EntityLivingBase;I)I", false);
                    super.visitVarInsn(ISTORE, var);
                }
            }
        }

        private static class UpdateFogColorVisitor extends MethodVisitor {

            private int count = 0;

            public UpdateFogColorVisitor(MethodVisitor mv) {
                super(ASM5, mv);
            }

            @Override
            public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
                super.visitMethodInsn(opcode, owner, name, desc, itf);
                if (opcode == INVOKEVIRTUAL && name.equals(getName("isPotionActive", "func_70644_a"))) {
                    count++;
                    if (count == 2) {
                        super.visitVarInsn(ALOAD, 3);
                        super.visitTypeInsn(CHECKCAST, "net/minecraft/entity/EntityLivingBase");
                        super.visitMethodInsn(INVOKESTATIC, HOOKS, "ConduitPower$isActive", "(ZLnet/minecraft/entity/EntityLivingBase;)Z", false);
                    }
                }
            }
        }

        private static class SetupFogVisitor extends MethodVisitor {

            private int count = 0;

            public SetupFogVisitor(MethodVisitor mv) {
                super(ASM5, mv);
            }

            @Override
            public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
                super.visitMethodInsn(opcode, owner, name, desc, itf);
                if (opcode == INVOKEVIRTUAL && name.equals(getName("isPotionActive", "func_70644_a"))) {
                    count++;
                    if (count == 2) {
                        super.visitVarInsn(ALOAD, 3);
                        super.visitTypeInsn(CHECKCAST, "net/minecraft/entity/EntityLivingBase");
                        super.visitMethodInsn(INVOKESTATIC, HOOKS, "ConduitPower$isActive", "(ZLnet/minecraft/entity/EntityLivingBase;)Z", false);
                    }
                }
            }
        }
    }

    private static class BlockVisitor extends LeClassVisitor {

        public BlockVisitor(ClassVisitor cv) {
            super(cv);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
            if (name.equals("getFogColor")) return new GetFogColorVisitor(mv);
            return mv;
        }

        private static class GetFogColorVisitor extends MethodVisitor {

            public GetFogColorVisitor(MethodVisitor mv) {
                super(ASM5, mv);
            }

            @Override
            public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
                super.visitMethodInsn(opcode, owner, name, desc, itf);
                if (opcode == INVOKEVIRTUAL && name.equals(getName("isPotionActive", "func_70644_a"))) {
                    super.visitVarInsn(ALOAD, 8);
                    super.visitMethodInsn(INVOKESTATIC, HOOKS, "ConduitPower$isActive", "(ZLnet/minecraft/entity/EntityLivingBase;)Z", false);
                }
            }
        }
    }

    @SuppressWarnings("unused")
    public static class Hooks {

        public static boolean ConduitPower$isActive(boolean original, EntityLivingBase entity) {
            return original || (entity.isPotionActive(ModPotions.CONDUIT_POWER) && ModPotions.CONDUIT_POWER.shouldApply(entity));
        }


        public static PotionEffect ConduitPower$getAmplifier(@Nullable PotionEffect original, EntityLivingBase entity) {
            PotionEffect conduitPower = entity.getActivePotionEffect(ModPotions.CONDUIT_POWER);
            if (original == null) return conduitPower;
            else if (conduitPower == null) return original;
            else return original.getAmplifier() > conduitPower.getAmplifier() ? original : conduitPower;
        }

        @SideOnly(Side.CLIENT)
        public static boolean ConduitPower$isActiveClient(boolean original) {
            return ConduitPower$isActive(original, Minecraft.getMinecraft().player);
        }

        @SideOnly(Side.CLIENT)
        public static PotionEffect ConduitPower$getPotionEffect(@Nullable PotionEffect effect, EntityLivingBase entity) {
            PotionEffect effect1 = entity.getActivePotionEffect(ModPotions.CONDUIT_POWER);
            if (effect == null) return effect1;
            else if (effect1 == null) return effect;
            else return effect.getDuration() > effect1.getDuration() ? effect : effect1;
        }

        @SideOnly(Side.CLIENT)
        public static int ConduitPower$getNightVisionTime(EntityLivingBase entity, int original) {
            if (entity.isPotionActive(ModPotions.CONDUIT_POWER)) return 201;
            return original;
        }

        public static double SlowFalling$getSpeed(double original, EntityLivingBase entity) {
            if (entity.motionY <= 0.0 && entity.isPotionActive(ModPotions.SLOW_FALLING)) {
                entity.fallDistance = 0.0F;
                return original / (8.0 + Objects.requireNonNull(entity.getActivePotionEffect(ModPotions.SLOW_FALLING)).getAmplifier());
            }
            return original;
        }
    }

    private PotionTransformer() {}
}
