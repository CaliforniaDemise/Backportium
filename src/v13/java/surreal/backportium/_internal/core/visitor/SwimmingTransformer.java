package surreal.backportium._internal.core.visitor;

import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import surreal.backportium._internal.bytecode.asm.LeClassVisitor;
import surreal.backportium._internal.client.model.ModelBipedSwimming;
import surreal.backportium.api.entity.RiptideEntity;
import surreal.backportium.api.entity.SwimmingEntity;
import surreal.backportium.util.NewMathHelper;

import java.util.function.Function;

// TODO Fix capes
// TODO Crawling and other collision stuff
public final class SwimmingTransformer {

    private static final String HOOKS = "surreal/backportium/_internal/core/visitor/SwimmingTransformer$Hooks";
    private static final String SWIMMING_ENTITY = "surreal/backportium/api/entity/SwimmingEntity";
    private static final String MODEL_BIPED_SWIMMING = "surreal/backportium/_internal/client/model/ModelBipedSwimming";

    public static Function<ClassVisitor, ClassVisitor> visit(String name, String transformedName, byte[] bytes) {
        switch (transformedName) {
            case "net.minecraft.entity.EntityLivingBase": return EntityLivingBaseVisitor::new;
            case "net.minecraft.entity.player.EntityPlayer": return EntityPlayerVisitor::new;
            case "net.minecraft.client.model.ModelBiped": return ModelBipedVisitor::new;
            case "net.minecraft.client.renderer.entity.RenderPlayer": return RenderPlayerVisitor::new;
            case "net.minecraft.client.renderer.EntityRenderer": return EntityRendererVisitor::new;
        }
        return null;
    }

    private static class EntityLivingBaseVisitor extends LeClassVisitor {

        public EntityLivingBaseVisitor(ClassVisitor cv) {
            super(cv);
        }

        @Override
        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            super.visit(version, access, name, signature, superName, getInterfaces(interfaces, SWIMMING_ENTITY));
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
            if (name.equals(getName("onEntityUpdate", "func_70030_z"))) return new OnEntityUpdateVisitor(mv);
            if (name.equals(getName("travel", "func_191986_a"))) return new TravelVisitor(mv);
            return mv;
        }

        @Override
        public void visitEnd() {
            super.visitEnd();
            { // isSwimming
                MethodVisitor mv = super.visitMethod(ACC_PUBLIC, "isSwimming", "()Z", null, null);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitInsn(ICONST_4);
                mv.visitMethodInsn(INVOKEVIRTUAL, "net/minecraft/entity/EntityLivingBase", getName("getFlag", "func_70083_f"), "(I)Z", false);
                mv.visitInsn(IRETURN);
                mv.visitMaxs(1, 0);
            }
            { // setSwimming
                MethodVisitor mv = super.visitMethod(ACC_PUBLIC | ACC_FINAL, "setSwimming", "(Z)V", null, null);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitInsn(ICONST_4);
                mv.visitVarInsn(ILOAD, 1);
                mv.visitMethodInsn(INVOKEVIRTUAL, "net/minecraft/entity/EntityLivingBase", getName("setFlag", "func_70052_a"), "(IZ)V", false);
                mv.visitInsn(RETURN);
                mv.visitMaxs(2, 0);
            }
        }

        private static class OnEntityUpdateVisitor extends MethodVisitor {

            public OnEntityUpdateVisitor(MethodVisitor mv) {
                super(ASM5, mv);
            }

            @Override
            public void visitInsn(int opcode) {
                if (opcode == RETURN) {
                    super.visitVarInsn(ALOAD, 0);
                    super.visitMethodInsn(INVOKESTATIC, HOOKS, "EntityLivingBase$updateSwimming", "(Lnet/minecraft/entity/EntityLivingBase;)V", false);
                }
                super.visitInsn(opcode);
            }
        }

        private static class TravelVisitor extends MethodVisitor {

            private int count = 0;

            public TravelVisitor(MethodVisitor mv) {
                super(ASM5, mv);
            }

            @Override
            public void visitCode() {
                super.visitCode();
                super.visitVarInsn(ALOAD, 0);
                super.visitVarInsn(ALOAD, 0);
                super.visitFieldInsn(GETFIELD, "net/minecraft/entity/EntityLivingBase", getName("isJumping", "field_70703_bu"), "Z");
                super.visitMethodInsn(INVOKESTATIC, HOOKS, "EntityLivingBase$travel", "(Lnet/minecraft/entity/EntityLivingBase;Z)V", false);
            }

            @Override
            public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
                super.visitMethodInsn(opcode, owner, name, desc, itf);
                if (opcode == INVOKEVIRTUAL && name.equals(getName("getWaterSlowDown", "func_189749_co"))) {
                    count++;
                    super.visitVarInsn(ALOAD, 0);
                    super.visitMethodInsn(INVOKESTATIC, HOOKS, "EntityLivingBase$getWaterSlowDown", "(FLnet/minecraft/entity/EntityLivingBase;)F", false);
                }
            }

            @Override
            public void visitLdcInsn(Object cst) {
                super.visitLdcInsn(cst);
                if (count == 1 && cst.equals(0.02D)) {
                    super.visitVarInsn(ALOAD, 0);
                    super.visitMethodInsn(INVOKESTATIC, HOOKS, "EntityLivingBase$applyGravity", "(DLnet/minecraft/entity/EntityLivingBase;)D", false);
                }
            }
        }
    }

    private static class EntityPlayerVisitor extends LeClassVisitor {

        public EntityPlayerVisitor(ClassVisitor cv) {
            super(cv);
        }

        @Override
        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            super.visit(version, access, name, signature, superName, interfaces);
            super.visitField(ACC_PRIVATE, "eyesInWater", "Z", null, false);
            super.visitField(ACC_PRIVATE, "lastSwimAnimation", "F", null, 0.0F);
            super.visitField(ACC_PRIVATE, "swimAnimation", "F", null, 0.0F);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
            if (name.equals(getName("onUpdate", "func_70071_h_"))) return new OnEntityUpdateVisitor(mv);
            return mv;
        }

        @Override
        public void visitEnd() {
            super.visitEnd();
            { // getSwimAnimation
                MethodVisitor mv = super.visitMethod(ACC_PUBLIC, "getSwimAnimation", "(F)F", null, null);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitFieldInsn(GETFIELD, "net/minecraft/entity/player/EntityPlayer", "swimAnimation", "F");
                mv.visitVarInsn(ALOAD, 0);
                mv.visitFieldInsn(GETFIELD, "net/minecraft/entity/player/EntityPlayer", "lastSwimAnimation", "F");
                mv.visitVarInsn(FLOAD, 1);
                mv.visitMethodInsn(INVOKESTATIC, HOOKS, "EntityPlayer$getSwimAnimation", "(FFF)F", false);
                mv.visitInsn(FRETURN);
                mv.visitMaxs(2, 0);
            }
            { // updateSwimAnimation
                MethodVisitor mv = super.visitMethod(ACC_PUBLIC, "updateSwimAnimation", "()V", null, null);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitFieldInsn(GETFIELD, "net/minecraft/entity/player/EntityPlayer", "swimAnimation", "F");
                mv.visitFieldInsn(PUTFIELD, "net/minecraft/entity/player/EntityPlayer", "lastSwimAnimation", "F");
                mv.visitVarInsn(ALOAD, 0);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitFieldInsn(GETFIELD, "net/minecraft/entity/player/EntityPlayer", "swimAnimation", "F");
                mv.visitMethodInsn(INVOKESTATIC, HOOKS, "EntityPlayer$updateSwimmingAnimation", "(Lnet/minecraft/entity/player/EntityPlayer;F)F", false);
                mv.visitFieldInsn(PUTFIELD, "net/minecraft/entity/player/EntityPlayer", "swimAnimation", "F");
                mv.visitInsn(RETURN);
                mv.visitMaxs(1, 0);
            }
            { // canSwim
                MethodVisitor mv = super.visitMethod(ACC_PUBLIC, "canSwim", "()Z", null, null);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitFieldInsn(GETFIELD, "net/minecraft/entity/player/EntityPlayer", "eyesInWater", "Z");
                Label l_con = new Label();
                mv.visitJumpInsn(IFEQ, l_con);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitMethodInsn(INVOKEVIRTUAL, "net/minecraft/entity/player/EntityPlayer", getName("isInWater", "func_70090_H"), "()Z", false);
                mv.visitInsn(IRETURN);
                mv.visitLabel(l_con);
                mv.visitFrame(F_SAME, 0, null, 0, null);
                mv.visitInsn(ICONST_0);
                mv.visitInsn(IRETURN);
                mv.visitMaxs(1, 0);
            }
        }

        private static class OnEntityUpdateVisitor extends MethodVisitor {

            public OnEntityUpdateVisitor(MethodVisitor mv) {
                super(ASM5, mv);
            }

            @Override
            public void visitInsn(int opcode) {
                if (opcode == RETURN) {
                    super.visitVarInsn(ALOAD, 0);
                    super.visitVarInsn(ALOAD, 0);
                    super.visitMethodInsn(INVOKESTATIC, HOOKS, "EntityPlayer$isEyesInWater", "(Lnet/minecraft/entity/player/EntityPlayer;)Z", false);
                    super.visitFieldInsn(PUTFIELD, "net/minecraft/entity/player/EntityPlayer", "eyesInWater", "Z");
                    super.visitVarInsn(ALOAD, 0);
                    super.visitMethodInsn(INVOKEVIRTUAL, "net/minecraft/entity/player/EntityPlayer", "updateSwimAnimation", "()V", false);
                }
                super.visitInsn(opcode);
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
            if (name.equals(getName("applyBobbing", "func_78475_f"))) return new ApplyBobbingVisitor(mv);
            return mv;
        }

        private static class ApplyBobbingVisitor extends MethodVisitor {

            public ApplyBobbingVisitor(MethodVisitor mv) {
                super(ASM5, mv);
            }

            @Override
            public void visitCode() {
                super.visitCode();
                super.visitMethodInsn(INVOKESTATIC, HOOKS, "EntityRenderer$shouldApplyBobbing", "()Z", false);
                Label l_con = new Label();
                super.visitJumpInsn(IFEQ, l_con);
                super.visitInsn(RETURN);
                super.visitLabel(l_con);
                super.visitFrame(F_SAME, 0, null, 0, null);
            }
        }
    }

    // TODO Add a way to change head pitch and stuff to EntityMove
    private static class ModelBipedVisitor extends LeClassVisitor {

        private boolean hasSetLivingAnimations = false;

        public ModelBipedVisitor(ClassVisitor cv) {
            super(cv);
        }

        @Override
        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            super.visit(version, access, name, signature, superName, getInterfaces(interfaces, MODEL_BIPED_SWIMMING));
            super.visitField(ACC_PRIVATE, "bp$swimAnim", "F", null, 0.0F);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
            if (name.equals(getName("setRotationAngles", "func_78087_a"))) return new SetRotationAnglesVisitor(mv);
            if (name.equals(getName("setLivingAnimations", "func_78086_a"))) {
                hasSetLivingAnimations = true;
                return new SetLivingAnimationsVisitor(mv);
            }
            return mv;
        }

        @Override
        public void visitEnd() {
            super.visitEnd();
            { // getSwimAnimation
                MethodVisitor mv = super.visitMethod(ACC_PUBLIC, "getSwimAnimation", "()F", null, null);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitFieldInsn(GETFIELD, "net/minecraft/client/model/ModelBiped", "bp$swimAnim", "F");
                mv.visitInsn(FRETURN);
                mv.visitMaxs(1, 0);
            }
            { // setSwimAnimation
                MethodVisitor mv = super.visitMethod(ACC_PUBLIC, "setSwimAnimation", "(F)V", null, null);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitVarInsn(FLOAD, 1);
                mv.visitFieldInsn(PUTFIELD, "net/minecraft/client/model/ModelBiped", "bp$swimAnim", "F");
                mv.visitInsn(RETURN);
                mv.visitMaxs(2, 0);
            }
            if (!hasSetLivingAnimations) { // setLivingAnimations
                MethodVisitor mv = super.visitMethod(ACC_PUBLIC, getName("setLivingAnimations", "func_78086_a"), "(Lnet/minecraft/entity/EntityLivingBase;FFF)V", null, null);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitVarInsn(ALOAD, 1);
                mv.visitVarInsn(FLOAD, 4);
                mv.visitMethodInsn(INVOKESTATIC, HOOKS, "ModelBiped$setSwimAnimation", "(Lnet/minecraft/client/model/ModelBiped;Lnet/minecraft/entity/EntityLivingBase;F)V", false);
                mv.visitInsn(RETURN);
                mv.visitMaxs(5, 0);
            }
        }

        private static class SetRotationAnglesVisitor extends MethodVisitor {

            public SetRotationAnglesVisitor(MethodVisitor mv) {
                super(ASM5, mv);
            }

            @Override
            public void visitCode() {
                super.visitCode();
                super.visitVarInsn(ALOAD, 0);
                super.visitVarInsn(ALOAD, 7);
                super.visitVarInsn(FLOAD, 5);
                super.visitMethodInsn(INVOKESTATIC, HOOKS, "ModelBiped$getHeadPitch", "(Lnet/minecraft/client/model/ModelBiped;Lnet/minecraft/entity/Entity;F)F", false);
                super.visitVarInsn(FSTORE, 5);
            }
        }

        private static class SetLivingAnimationsVisitor extends MethodVisitor {

            public SetLivingAnimationsVisitor(MethodVisitor mv) {
                super(ASM5, mv);
            }

            @Override
            public void visitInsn(int opcode) {
                if (opcode == RETURN) {
                    super.visitVarInsn(ALOAD, 0);
                    super.visitVarInsn(FLOAD, 4);
                    super.visitMethodInsn(INVOKESTATIC, HOOKS, "ModelBiped$setSwimAnimation", "(Lnet/minecraft/client/model/ModelBiped;F)V", false);
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
            if (name.equals(getName("renderRightArm", "func_177138_b"))
            || name.equals(getName("renderLeftArm", "func_177139_c"))) return new ResetSwimAnimationVisitor(mv);
            return mv;
        }

        private static class ResetSwimAnimationVisitor extends MethodVisitor {

            public ResetSwimAnimationVisitor(MethodVisitor mv) {
                super(ASM5, mv);
            }

            @Override
            public void visitCode() {
                super.visitVarInsn(ALOAD, 0);
                super.visitMethodInsn(INVOKESTATIC, HOOKS, "RenderPlayer$resetSwimAnimation", "(Lnet/minecraft/client/renderer/entity/RenderPlayer;)V", false);
                super.visitCode();
            }
        }
    }

    @SuppressWarnings("unused")
    public static class Hooks {

        // TODO For 1.16
        public static int EntityPlayer$getTimeUnderwater(EntityPlayer player, int timeUnderwater) {
            if (player.isInWater()) {
                int i = player.isSpectator() ? 10 : 1;
                return MathHelper.clamp(timeUnderwater + i, 0, 600);
            }
            else if (timeUnderwater > 0) {
                return MathHelper.clamp(timeUnderwater - 10, 0, 600);
            }
            return timeUnderwater;
        }

        public static void EntityLivingBase$updateSwimming(EntityLivingBase entity) {
            SwimmingEntity swimming = SwimmingEntity.cast(entity);
            if (swimming.canSwim() && !RiptideEntity.cast(entity).inRiptide()) {
                if (entity instanceof EntityPlayer && ((EntityPlayer) entity).capabilities.isFlying) {
                    swimming.setSwimming(false);
                }
                else if (swimming.isSwimming()) {
                    swimming.setSwimming(entity.isSprinting() && entity.isInWater() && !entity.isRiding());
                }
                else {
                    swimming.setSwimming(entity.isSprinting() && swimming.canSwim() && !entity.isRiding());
                }
            }
            else {
                swimming.setSwimming(false);
            }
        }

        public static float EntityLivingBase$getWaterSlowDown(float defaultValue, EntityLivingBase entity) {
            if (entity.isSprinting()) {
                return 0.9F;
            }
            return defaultValue;
        }

        @SideOnly(Side.CLIENT)
        public static float EntityPlayer$getSwimAnimation(float swimAnimation, float lastSwimAnimation, float partialTicks) {
            return NewMathHelper.lerp(partialTicks, lastSwimAnimation, swimAnimation);
        }

        public static boolean EntityPlayer$isEyesInWater(EntityPlayer player) {
            return player.isInsideOfMaterial(Material.WATER);
        }

        public static float EntityPlayer$updateSwimmingAnimation(EntityPlayer player, float swimAnimation) {
            SwimmingEntity entity = SwimmingEntity.cast(player);
            if (entity.isSwimming()) {
                return Math.min(1.0F, swimAnimation + 0.09F);
            }
            else {
                return Math.max(0.0F, swimAnimation - 0.09F);
            }
        }

        public static double EntityLivingBase$applyGravity(double defaultValue, EntityLivingBase entity) {
            SwimmingEntity swimming = SwimmingEntity.cast(entity);
            if (swimming.isSwimming()) {
                if (entity.motionY <= 0.0 && Math.abs(entity.motionY - 0.005) >= 0.003 && Math.abs(entity.motionY - 0.08 / 16.0) < 0.003) {
                    return 0.003;
                }
                else {
                    return 0.08 / 16.0;
                }
            }
            return defaultValue;
        }

        public static void EntityLivingBase$travel(EntityLivingBase entity, boolean isJumping) {
            SwimmingEntity swimming = SwimmingEntity.cast(entity);
            if (swimming.isSwimming() && !entity.isRiding()) {
                double yLook = entity.getLookVec().y;
                IBlockState state = entity.world.getBlockState(new BlockPos(entity.posX, entity.posY + 0.9, entity.posZ));
                if (yLook <= 0.0 || isJumping || state.getBlock() instanceof BlockLiquid || state.getBlock() instanceof IFluidBlock) {
                    double d4 = yLook < -0.2 ? 0.085 : 0.06;
                    entity.motionY += (yLook - entity.motionY) * d4;
                }
            }
        }

        @SideOnly(Side.CLIENT)
        public static float ModelBiped$getHeadPitch(ModelBiped model, Entity entity, float headPitch) {
            if (entity instanceof EntityLivingBase) {
                EntityLivingBase living = (EntityLivingBase) entity;
                SwimmingEntity swimming = SwimmingEntity.cast(living);
                boolean elytra = living.getTicksElytraFlying() > 4;
                float swimAnim = ModelBipedSwimming.cast(model).getSwimAnimation();
                if (!elytra && swimAnim > 0.0F) {
                    if (swimming.isSwimming()) {
                        return NewMathHelper.rotLerpRad(swimAnim, model.bipedHead.rotateAngleX, ((float) -Math.PI / 4.0F)) / 0.017453292F;
                    }
                    else {
                        return NewMathHelper.rotLerpRad(swimAnim, model.bipedHead.rotateAngleX, headPitch * ((float) Math.PI / 180.0F)) / 0.017453292F;
                    }
                }
            }
            return headPitch;
        }

        @SideOnly(Side.CLIENT)
        public static void ModelBiped$setSwimAnimation(ModelBiped model, EntityLivingBase entity, float partialTicks) {
            ModelBipedSwimming.cast(model).setSwimAnimation(SwimmingEntity.cast(entity).getSwimAnimation(partialTicks));
        }

        @SideOnly(Side.CLIENT)
        public static void RenderPlayer$resetSwimAnimation(RenderPlayer render) {
            ModelPlayer model = render.getMainModel();
            ModelBipedSwimming.cast(model).setSwimAnimation(0.0F);
        }

        @SideOnly(Side.CLIENT)
        public static boolean EntityRenderer$shouldApplyBobbing() {
            Entity entity = Minecraft.getMinecraft().getRenderViewEntity();
            return entity instanceof EntityPlayer && SwimmingEntity.cast((EntityPlayer) entity).isSwimming();
        }
    }

    private SwimmingTransformer() {}
}
