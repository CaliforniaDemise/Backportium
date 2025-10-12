package surreal.backportium._internal.core.visitor;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import surreal.backportium._internal.bytecode.asm.LeClassVisitor;
import surreal.backportium._internal.enchantment.EnchantmentImpaling;
import surreal.backportium.api.entity.RiptideEntity;
import surreal.backportium.client.entity.render.layer.LayerRiptide;
import surreal.backportium.init.ModEnchantments;

import java.util.List;
import java.util.function.Function;

public final class TridentTransformer {

    private static final String HOOKS = "surreal/backportium/_internal/core/visitor/TridentTransformer$Hooks";
    private static final String RIPTIDE_ENTITY = "surreal/backportium/api/entity/RiptideEntity";

    public static Function<ClassVisitor, ClassVisitor> visit(String name, String transformedName, byte[] bytes) {
        switch (transformedName) {
            case "net.minecraft.entity.EntityLivingBase": return EntityLivingBaseVisitor::new;
            case "net.minecraft.client.renderer.entity.RenderLivingBase": return RenderLivingBaseVisitor::new;
            case "net.minecraft.client.renderer.entity.RenderPlayer": return RenderPlayerVisitor::new;
        }
        return null;
    }

    private static class EntityLivingBaseVisitor extends LeClassVisitor {

        public EntityLivingBaseVisitor(ClassVisitor cv) {
            super(cv);
        }

        @Override
        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            super.visit(version, access, name, signature, superName, getInterfaces(interfaces, RIPTIDE_ENTITY));
            super.visitField(ACC_PRIVATE, "riptideTime", "I", null, 0);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
            if (name.equals(getName("onUpdate", "func_70071_h_"))) return new OnUpdateVisitor(mv);
            return mv;
        }

        private static class OnUpdateVisitor extends MethodVisitor {

            public OnUpdateVisitor(MethodVisitor mv) {
                super(ASM5, mv);
            }

            @Override
            public void visitInsn(int opcode) {
                if (opcode == RETURN) {
                    super.visitVarInsn(ALOAD, 0);
                    super.visitMethodInsn(INVOKESTATIC, HOOKS, "EntityLivingBase$OnUpdate", "(Lnet/minecraft/entity/EntityLivingBase;)V", false);
                }
                super.visitInsn(opcode);
            }
        }

        @Override
        public void visitEnd() {
            super.visitEnd();
            { // getRiptideTimeLeft
                MethodVisitor mv = super.visitMethod(ACC_PUBLIC, "getRiptideTimeLeft", "()I", null, null);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitFieldInsn(GETFIELD, "net/minecraft/entity/EntityLivingBase", "riptideTime", "I");
                mv.visitInsn(IRETURN);
                mv.visitMaxs(3, 0);
            }
            { // setRiptideTimeLeft
                MethodVisitor mv = super.visitMethod(ACC_PUBLIC, "setRiptideTimeLeft", "(I)V", null, null);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitVarInsn(ILOAD, 1);
                mv.visitFieldInsn(PUTFIELD, "net/minecraft/entity/EntityLivingBase", "riptideTime", "I");
                mv.visitInsn(RETURN);
                mv.visitMaxs(2, 0);
            }
        }
    }

    private static class RenderLivingBaseVisitor extends LeClassVisitor {

        public RenderLivingBaseVisitor(ClassVisitor cv) {
            super(cv);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
//            if (name.equals(getName("applyRotations", "func_77043_a"))) return new ApplyRotationsVisitor(mv);
            return mv;
        }

        private static class ApplyRotationsVisitor extends MethodVisitor {

            public ApplyRotationsVisitor(MethodVisitor mv) {
                super(ASM5, mv);
            }

            @Override
            public void visitInsn(int opcode) {
                if (opcode == RETURN) {
                    super.visitVarInsn(ALOAD, 1);
                    super.visitVarInsn(FLOAD, 4);
                    super.visitMethodInsn(INVOKESTATIC, HOOKS, "RenderLivingBase$applyRotations", "(Lnet/minecraft/entity/EntityLivingBase;F)V", false);
                }
                super.visitInsn(opcode);
            }
        }
    }

    private static class RenderPlayerVisitor extends LeClassVisitor {

        public RenderPlayerVisitor(ClassVisitor cv) {
            super(cv);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
            if (name.equals("<init>")) return new InitVisitor(mv);
//            if (name.equals(getName("applyRotations", "func_77043_a"))) return new ApplyRotationsVisitor(mv);
            return mv;
        }

        private static class InitVisitor extends MethodVisitor {

            public InitVisitor(MethodVisitor mv) {
                super(ASM5, mv);
            }

            @Override
            public void visitInsn(int opcode) {
                if (opcode == RETURN) {
                    super.visitVarInsn(ALOAD, 0);
                    super.visitMethodInsn(INVOKESTATIC, HOOKS, "RenderPlayer$addRiptideLayer", "(Lnet/minecraft/client/renderer/entity/RenderPlayer;)V", false);
                }
                super.visitInsn(opcode);
            }
        }

        private static class ApplyRotationsVisitor extends MethodVisitor {

            public ApplyRotationsVisitor(MethodVisitor mv) {
                super(ASM5, mv);
            }

            @Override
            public void visitInsn(int opcode) {
                if (opcode == RETURN) {
                    super.visitVarInsn(ALOAD, 1);
                    super.visitVarInsn(FLOAD, 4);
                    super.visitMethodInsn(INVOKESTATIC, HOOKS, "RenderPlayer$fixElytraRotations", "(Lnet/minecraft/entity/EntityLivingBase;F)V", false);
                }
                super.visitInsn(opcode);
            }
        }
    }

    @SuppressWarnings("unused")
    public static class Hooks {

        public static void EntityLivingBase$OnUpdate(EntityLivingBase entity) {
            RiptideEntity riptide = RiptideEntity.cast(entity);
            if (riptide.inRiptide()) {
                if ($handleCollision(entity)) {
                    riptide.setRiptideTimeLeft(0);
                }
                else {
                    riptide.setRiptideTimeLeft(riptide.getRiptideTimeLeft() - 1);
                }
            }
        }

        private static boolean $handleCollision(EntityLivingBase entity) {
            World world = entity.world;
            List<Entity> entities = world.getEntitiesInAABBexcluding(entity, entity.getEntityBoundingBox(), e -> e instanceof EntityLivingBase && e.canBeCollidedWith() && EntitySelectors.NOT_SPECTATING.apply(e));
            if (!entities.isEmpty()) {
                ItemStack stack = entity.getActiveItemStack();
                float add = 0F;
                Entity e = entities.get(0);
                if (!stack.isEmpty() && EnchantmentImpaling.canImpale(e)) {
                    int impaling = EnchantmentHelper.getEnchantmentLevel(ModEnchantments.IMPALING, stack);
                    add = EnchantmentImpaling.handleImpaling(add, impaling);
                }
                e.attackEntityFrom(DamageSource.GENERIC, 8.0F + add);
                entity.motionX = -entity.motionX / 2F;
                entity.motionY = -entity.motionY / 2F;
                entity.motionZ = -entity.motionZ / 2F;
                return true;
            }
            return false;
        }

        @SideOnly(Side.CLIENT)
        public static void RenderLivingBase$applyRotations(EntityLivingBase entity, float partialTicks) {
            RiptideEntity riptide = RiptideEntity.cast(entity);
            if (riptide.inRiptide()) {
                float yRotation = 72F * (riptide.getRiptideTimeLeft() - partialTicks + 1.0F);
                if (!entity.isElytraFlying()) {
                    GlStateManager.rotate(-90.0F - entity.rotationPitch, 1.0F, 0.0F, 0.0F);
                    Vec3d vec3d = entity.getLook(partialTicks);
                    double d0 = entity.motionX * entity.motionX + entity.motionZ * entity.motionZ;
                    double d1 = vec3d.x * vec3d.x + vec3d.z * vec3d.z;
                    if (d0 > 0.0D && d1 > 0.0D) {
                        double d2 = (entity.motionX * vec3d.x + entity.motionZ * vec3d.z) / (Math.sqrt(d0) * Math.sqrt(d1));
                        double d3 = entity.motionX * vec3d.z - entity.motionZ * vec3d.x;
                        GlStateManager.rotate((float) (Math.signum(d3) * Math.acos(d2)) * 180.0F / (float) Math.PI, 0.0F, 1.0F, 0.0F);
                    }
                    GlStateManager.rotate(yRotation, 0, 1, 0);
                }
            }
        }

        @SideOnly(Side.CLIENT)
        public static void RenderPlayer$fixElytraRotations(EntityLivingBase entity, float partialTicks) {
            RiptideEntity riptide = RiptideEntity.cast(entity);
            if (entity.isElytraFlying() && riptide.inRiptide()) {
                float rotate = 72F * (riptide.getRiptideTimeLeft() - partialTicks + 1.0F);
                GlStateManager.rotate(rotate, 0.0F, 1.0F, 0.0F);
            }
        }

        @SuppressWarnings("unchecked")
        @SideOnly(Side.CLIENT)
        public static void RenderPlayer$addRiptideLayer(RenderPlayer render) {
            render.addLayer(new LayerRiptide((Render<EntityLivingBase>) (Object) render));
        }
    }

    private TridentTransformer() {}
}
