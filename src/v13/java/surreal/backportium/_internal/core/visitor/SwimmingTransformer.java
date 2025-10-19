package surreal.backportium._internal.core.visitor;

import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import surreal.backportium._internal.bytecode.asm.LeClassVisitor;
import surreal.backportium._internal.client.entity.player.ClientPlayerSwimming;
import surreal.backportium._internal.client.model.ModelBipedSwimming;
import surreal.backportium._internal.util.AxisAlignedBBSpliterator;
import surreal.backportium.api.entity.EntityState;
import surreal.backportium.api.entity.EntityWithState;
import surreal.backportium.api.entity.SwimmingEntity;
import surreal.backportium.init.ModEntityStates;
import surreal.backportium.util.NewMathHelper;

import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static net.minecraft.entity.EntityLivingBase.SWIM_SPEED;

// TODO Easy elytra take-off
// TODO Slowdown sneak flying?
// TODO No double tap sprinting
public final class SwimmingTransformer {

    private static final String HOOKS = "surreal/backportium/_internal/core/visitor/SwimmingTransformer$Hooks";
    private static final String SWIMMING_ENTITY = "surreal/backportium/api/entity/SwimmingEntity";
    private static final String MODEL_BIPED_SWIMMING = "surreal/backportium/_internal/client/model/ModelBipedSwimming";
    private static final String CLIENT_PLAYER_SWIMMING = "surreal/backportium/_internal/client/entity/player/ClientPlayerSwimming";

    public static Function<ClassVisitor, ClassVisitor> visit(String name, String transformedName, byte[] bytes) {
        switch (transformedName) {
            case "net.minecraft.entity.EntityLivingBase": return EntityLivingBaseVisitor::new;
            case "net.minecraft.entity.player.EntityPlayer": return EntityPlayerVisitor::new;
            case "net.minecraft.client.entity.EntityPlayerSP": return EntityPlayerSPVisitor::new;
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
            if (name.equals(getName("travel", "func_191986_a"))) return new TravelVisitor(mv);
            return mv;
        }

        private static class TravelVisitor extends MethodVisitor {

            private int getWaterSlowDown_counter = 0;
            private int move_counter = 0;

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
                if (opcode == INVOKEVIRTUAL) {
                    if (name.equals(getName("getWaterSlowDown", "func_189749_co"))) {
                        getWaterSlowDown_counter++;
                        super.visitVarInsn(ALOAD, 0);
                        super.visitMethodInsn(INVOKESTATIC, HOOKS, "EntityLivingBase$getWaterSlowDown", "(FLnet/minecraft/entity/EntityLivingBase;)F", false);
                    }
                    else if (name.equals(getName("move", "func_70091_d"))) {
                        move_counter++;
                        if (move_counter == 4) {
                            super.visitVarInsn(ALOAD, 0);
                            super.visitVarInsn(ALOAD, 0);
                            super.visitVarInsn(ALOAD, 0);
                            super.visitFieldInsn(GETFIELD, "net/minecraft/entity/EntityLivingBase", getName("motionY", "field_70181_x"), "D");
                            super.visitMethodInsn(INVOKESTATIC, HOOKS, "EntityLivingBase$ladderMotion", "(Lnet/minecraft/entity/EntityLivingBase;D)D", false);
                            super.visitFieldInsn(PUTFIELD, "net/minecraft/entity/EntityLivingBase", getName("motionY", "field_70181_x"), "D");
                        }
                    }
                }
            }

            @Override
            public void visitLdcInsn(Object cst) {
                if (getWaterSlowDown_counter == 1 && cst.equals(0.02D)) {
                    getWaterSlowDown_counter++;
                    super.visitInsn(DCONST_0);
                    return;
                }
                super.visitLdcInsn(cst);
            }

            @Override
            public void visitFieldInsn(int opcode, String owner, String name, String desc) {
                super.visitFieldInsn(opcode, owner, name, desc);
                if (getWaterSlowDown_counter == 2 && opcode == PUTFIELD && name.equals(getName("motionY", "field_70181_x"))) {
                    getWaterSlowDown_counter++;
                    super.visitVarInsn(ALOAD, 0);
                    super.visitMethodInsn(INVOKESTATIC, HOOKS, "EntityLivingBase$applyGravity", "(Lnet/minecraft/entity/EntityLivingBase;)V", false);
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
            super.visitField(ACC_PROTECTED, "eyesInWater", "Z", null, false);
            super.visitField(ACC_PRIVATE, "swimAnimation", "F", null, 0.0F);
            super.visitField(ACC_PRIVATE, "lastSwimAnimation", "F", null, 0.0F);
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
            { // updateSwimming
                MethodVisitor mv = super.visitMethod(ACC_PUBLIC, "updateSwimming", "()V", null, null);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitMethodInsn(INVOKESTATIC, HOOKS, "EntityPlayer$updateSwimming", "(Lnet/minecraft/entity/player/EntityPlayer;)V", false);
                mv.visitInsn(RETURN);
                mv.visitMaxs(1, 0);
            }
            { // isSwimming
                MethodVisitor mv = super.visitMethod(ACC_PUBLIC, "isSwimming", "()Z", null, null);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitInsn(ICONST_4);
                mv.visitMethodInsn(INVOKEVIRTUAL, "net/minecraft/entity/player/EntityPlayer", getName("getFlag", "func_70083_f"), "(I)Z", false);
                mv.visitMethodInsn(INVOKESTATIC, HOOKS, "EntityPlayer$isSwimming", "(Lnet/minecraft/entity/player/EntityPlayer;Z)Z", false);
                mv.visitInsn(IRETURN);
                mv.visitMaxs(1, 0);
            }
            { // isActuallySwimming
                MethodVisitor mv = super.visitMethod(ACC_PUBLIC, "isActuallySwimming", "()Z", null, null);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitMethodInsn(INVOKESTATIC, HOOKS, "EntityPlayer$isActuallySwimming", "(Lnet/minecraft/entity/player/EntityPlayer;)Z", false);
                mv.visitInsn(IRETURN);
                mv.visitMaxs(1, 0);
            }
            { // isVisuallySwimming
                MethodVisitor mv = super.visitMethod(ACC_PUBLIC, "isVisuallySwimming", "()Z", null, null);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitMethodInsn(INVOKEVIRTUAL, "net/minecraft/entity/player/EntityPlayer", getName("isInWater", "func_70090_H"), "()Z", false);
                Label l_con = new Label();
                mv.visitJumpInsn(IFNE, l_con);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitMethodInsn(INVOKEVIRTUAL, "net/minecraft/entity/player/EntityPlayer", "isActuallySwimming", "()Z", false);
                mv.visitInsn(IRETURN);
                mv.visitLabel(l_con);
                mv.visitFrame(F_SAME, 0, null, 0, null);
                mv.visitInsn(ICONST_0);
                mv.visitInsn(IRETURN);
                mv.visitMaxs(1, 0);
            }
            { // setSwimming
                MethodVisitor mv = super.visitMethod(ACC_PUBLIC, "setSwimming", "(Z)V", null, null);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitInsn(ICONST_4);
                mv.visitVarInsn(ILOAD, 1);
                mv.visitMethodInsn(INVOKEVIRTUAL, "net/minecraft/entity/player/EntityPlayer", getName("setFlag", "func_70052_a"), "(IZ)V", false);
                mv.visitInsn(RETURN);
                mv.visitMaxs(2, 0);
            }
            { // getSwimAnimation
                MethodVisitor mv = super.visitMethod(ACC_PUBLIC, "getSwimAnimation", "(F)F", null, null);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitFieldInsn(GETFIELD, "net/minecraft/entity/player/EntityPlayer", "swimAnimation", "F");
                mv.visitVarInsn(ALOAD, 0);
                mv.visitFieldInsn(GETFIELD, "net/minecraft/entity/player/EntityPlayer", "lastSwimAnimation", "F");
                mv.visitVarInsn(FLOAD, 1);
                mv.visitMethodInsn(INVOKESTATIC, HOOKS, "EntityPlayer$getSwimAnimation", "(Lnet/minecraft/entity/player/EntityPlayer;FFF)F", false);
                mv.visitInsn(FRETURN);
                mv.visitMaxs(2, 0);
            }
            { // updateSwimAnimation
                MethodVisitor mv = super.visitMethod(ACC_PRIVATE, "updateSwimAnimation", "()V", null, null);
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
                    super.visitVarInsn(ALOAD, 0);
                    super.visitMethodInsn(INVOKEVIRTUAL, "net/minecraft/entity/player/EntityPlayer", "updateSwimming", "()V", false);
                }
                super.visitInsn(opcode);
            }
        }
    }

    private static class EntityPlayerSPVisitor extends LeClassVisitor {

        public EntityPlayerSPVisitor(ClassVisitor cv) {
            super(cv);
        }

        @Override
        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            super.visit(version, access, name, signature, superName, getInterfaces(interfaces, CLIENT_PLAYER_SWIMMING));
            super.visitField(ACC_PRIVATE, "isCrouching", "Z", null, false);
            super.visitField(ACC_PRIVATE, "bp$sprinting", "Z", null, false);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
            if (name.equals(getName("onUpdateWalkingPlayer", "func_175161_p"))) return new OnUpdateWalkingPlayerVisitor(mv);
            if (name.equals(getName("isSneaking", "func_70093_af"))) return new IsSneakingVisitor(mv);
            if (name.equals(getName("onLivingUpdate", "func_70636_d"))) return new OnLivingUpdateVisitor(mv);
            if (name.equals(getName("pushOutOfBlocks", "func_145771_j"))) return new PushOutOfBlocksVisitor(mv);
            return mv;
        }

        @Override
        public void visitEnd() {
            super.visitEnd();
            { // isActuallySneaking
                MethodVisitor mv = super.visitMethod(ACC_PUBLIC, "isActuallySneaking", "()Z", null, null);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitMethodInsn(INVOKESTATIC, HOOKS, "EntityPlayerSP$isActuallySneaking", "(Lnet/minecraft/client/entity/EntityPlayerSP;)Z", false);
                mv.visitInsn(IRETURN);
                mv.visitMaxs(1, 0);
            }
            { // isForcedDown
                MethodVisitor mv = super.visitMethod(ACC_PUBLIC, "isForcedDown", "()Z", null, null);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitMethodInsn(INVOKESTATIC, HOOKS, "EntityPlayerSP$isForcedDown", "(Lnet/minecraft/client/entity/EntityPlayerSP;)Z", false);
                mv.visitInsn(IRETURN);
                mv.visitMaxs(1, 0);
            }
            { // isUsingSwimmingAnimation
                MethodVisitor mv = super.visitMethod(ACC_PUBLIC, "isUsingSwimmingAnimation", "()Z", null, null);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitMethodInsn(INVOKESTATIC, HOOKS, "EntityPlayerSP$isUsingSwimmingAnimation", "(Lnet/minecraft/client/entity/EntityPlayerSP;)Z", false);
                mv.visitInsn(IRETURN);
                mv.visitMaxs(1, 0);
            }
            { // canSwim
                MethodVisitor mv = super.visitMethod(ACC_PUBLIC, "canSwim", "()Z", null, null);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitFieldInsn(GETFIELD, "net/minecraft/client/entity/EntityPlayerSP", "eyesInWater", "Z");
                mv.visitInsn(IRETURN);
                mv.visitMaxs(1, 0);
            }
            { // isMovingForward
                MethodVisitor mv = super.visitMethod(ACC_PUBLIC, "isMovingForward", "(FF)Z", null, null);
                mv.visitVarInsn(FLOAD, 1);
                mv.visitVarInsn(FLOAD, 2);
                mv.visitMethodInsn(INVOKESTATIC, HOOKS, "EntityPlayerSP$isMovingForward", "(FF)Z", false);
                mv.visitInsn(IRETURN);
                mv.visitMaxs(3, 0);
            }
        }

        private static class OnUpdateWalkingPlayerVisitor extends MethodVisitor {

            public OnUpdateWalkingPlayerVisitor(MethodVisitor mv) {
                super(ASM5, mv);
            }

            @Override
            public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
                if (opcode == INVOKEVIRTUAL && name.equals(getName("isSneaking", "func_70093_af"))) {
                    super.visitMethodInsn(INVOKEVIRTUAL, owner, "isActuallySneaking", "()Z", false);
                    return;
                }
                super.visitMethodInsn(opcode, owner, name, desc, itf);
            }
        }

        private static class IsSneakingVisitor extends MethodVisitor {

            public IsSneakingVisitor(MethodVisitor mv) {
                super(ASM5, mv);
            }

            @Override
            public void visitCode() {
                super.visitCode();
                super.visitInsn(ICONST_1);
                Label l_con = new Label();
                super.visitJumpInsn(IFEQ, l_con);
                super.visitVarInsn(ALOAD, 0);
                super.visitFieldInsn(GETFIELD, "net/minecraft/client/entity/EntityPlayerSP", "isCrouching", "Z");
                super.visitInsn(IRETURN);
                super.visitLabel(l_con);
                super.visitFrame(F_SAME, 0, null, 0, null);
                super.visitVarInsn(ALOAD, 0);
            }
        }

        private static class OnLivingUpdateVisitor extends MethodVisitor {

            public OnLivingUpdateVisitor(MethodVisitor mv) {
                super(ASM5, mv);
            }

            @Override
            public void visitCode() {
                super.visitCode();
                super.visitVarInsn(ALOAD, 0);
                super.visitVarInsn(ALOAD, 0);
                super.visitMethodInsn(INVOKEVIRTUAL, "net/minecraft/client/entity/EntityPlayerSP", getName("isSprinting", "func_70051_ag"), "()Z", false);
                super.visitFieldInsn(PUTFIELD, "net/minecraft/client/entity/EntityPlayerSP", "bp$sprinting", "Z");
//                super.visitVarInsn(ALOAD, 0);
//                super.visitVarInsn(ALOAD, 0);
//                super.visitVarInsn(ALOAD, 0);
//                super.visitFieldInsn(GETFIELD, "net/minecraft/client/entity/EntityPlayerSP", getName("sprintToggleTimer", "field_71156_d"), "I");
//                super.visitMethodInsn(INVOKESTATIC, HOOKS, "EntityPlayerSP$updateSprintToggleTimer", "(Lnet/minecraft/client/player/EntityPlayerSP;I)I", false);
//                super.visitFieldInsn(PUTFIELD, "net/minecraft/client/entity/EntityPlayerSP", getName("sprintToggleTimer", "field_71156_d"), "I");
            }

            @Override
            public void visitFieldInsn(int opcode, String owner, String name, String desc) {
                super.visitFieldInsn(opcode, owner, name, desc);
                if (opcode == PUTFIELD && name.equals(getName("wasFallFlying", "field_189813_ct"))) {
                    super.visitVarInsn(ALOAD, 0);
                    super.visitMethodInsn(INVOKESTATIC, HOOKS, "EntityPlayerSP$updatePlayerMoveState", "(Lnet/minecraft/client/entity/EntityPlayerSP;)V", false);
                    super.visitVarInsn(ALOAD, 0);
                    super.visitVarInsn(ALOAD, 0);
                    super.visitMethodInsn(INVOKESTATIC, HOOKS, "EntityPlayerSP$isCrouching", "(Lnet/minecraft/client/entity/EntityPlayerSP;)Z", false);
                    super.visitFieldInsn(PUTFIELD, "net/minecraft/client/entity/EntityPlayerSP", "isCrouching", "Z");
                    super.visitVarInsn(ALOAD, 0);
                    super.visitVarInsn(ALOAD, 0);
                    super.visitFieldInsn(GETFIELD, "net/minecraft/client/entity/EntityPlayerSP", "bp$sprinting", "Z");
                    super.visitMethodInsn(INVOKEVIRTUAL, "net/minecraft/client/entity/EntityPlayerSP", getName("setSprinting", "func_70031_b"), "(Z)V", false);
                    super.visitVarInsn(ALOAD, 0);
                    super.visitVarInsn(ALOAD, 0);
                    super.visitFieldInsn(GETFIELD, "net/minecraft/client/entity/EntityPlayerSP", getName("sprintToggleTimer", "field_71156_d"), "I");
                    super.visitMethodInsn(INVOKESTATIC, HOOKS, "EntityPlayerSP$handleSprinting", "(Lnet/minecraft/client/entity/EntityPlayerSP;I)V", false);
                    super.visitVarInsn(ALOAD, 0);
                    super.visitMethodInsn(INVOKESTATIC, HOOKS, "EntityPlayerSP$handleWaterSneaking", "(Lnet/minecraft/client/entity/EntityPlayerSP;)V", false);
                }
            }
        }

        private static class PushOutOfBlocksVisitor extends MethodVisitor {

            public PushOutOfBlocksVisitor(MethodVisitor mv) {
                super(ASM5, mv);
            }

            @Override
            public void visitCode() {
                super.visitCode();
                super.visitVarInsn(ALOAD, 0);
                super.visitVarInsn(DLOAD, 1);
                super.visitVarInsn(DLOAD, 3);
                super.visitVarInsn(DLOAD, 5);
                super.visitMethodInsn(INVOKESTATIC, HOOKS, "EntityPlayerSP$pushOutOfBlocks", "(Lnet/minecraft/client/entity/EntityPlayerSP;DDD)Z", false);
                Label l_con = new Label();
                super.visitJumpInsn(IFEQ, l_con);
                super.visitInsn(ICONST_0);
                super.visitInsn(IRETURN);
                super.visitLabel(l_con);
                super.visitFrame(F_SAME, 0, null, 0, null);
            }
        }
    }

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
                super.visitJumpInsn(IFNE, l_con);
                super.visitInsn(RETURN);
                super.visitLabel(l_con);
                super.visitFrame(F_SAME, 0, null, 0, null);
            }
        }
    }

    @SuppressWarnings("unused")
    public static class Hooks {

        public static void EntityLivingBase$travel(net.minecraft.entity.EntityLivingBase entity, boolean isJumping) {
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

        public static void EntityLivingBase$applyGravity(EntityLivingBase entity) {
            if (!SwimmingEntity.cast(entity).isSwimming()) {
                if (entity.motionY <= 0.0 && Math.abs(entity.motionY - 0.005) >= 0.003 && Math.abs(entity.motionY - 0.08 / 16.0) < 0.003) {
                    entity.motionY = -0.003;
                } else {
                    entity.motionY -= 0.08 / 16.0;
                }
            }
        }

        public static float EntityLivingBase$getWaterSlowDown(float defaultValue, EntityLivingBase entity) {
            if (SwimmingEntity.cast(entity).isSwimming()) {
                return 0.9F;
            }
            return defaultValue;
        }

        public static double EntityLivingBase$ladderMotion(net.minecraft.entity.EntityLivingBase entity, double defaultValue) {
            if (entity.collidedHorizontally && entity.isOnLadder()) return 0.2;
            return defaultValue;
        }

        public static void EntityPlayer$updateSwimming(EntityPlayer player) {
            SwimmingEntity swimming = SwimmingEntity.cast(player);
            if (player.capabilities.isFlying) {
                swimming.setSwimming(false);
            }
            else if (swimming.isSwimming()) {
                swimming.setSwimming(player.isSprinting() && player.isInWater() && !player.isRiding());
            }
            else {
                swimming.setSwimming(player.isSprinting() && swimming.canSwim() && !player.isRiding());
            }
        }

        public static boolean EntityPlayer$isSwimming(EntityPlayer player, boolean swimFlag) {
            SwimmingEntity swimming = SwimmingEntity.cast(player);
            return !player.capabilities.isFlying && !player.isSpectator() && swimFlag;
        }

        public static boolean EntityPlayer$isActuallySwimming(EntityPlayer player) {
            EntityState state = EntityWithState.cast(player).getState();
            boolean isFlying = !player.isElytraFlying() && state == ModEntityStates.FLYING;
            return state == ModEntityStates.CRAWLING || isFlying;
        }

        public static float EntityPlayer$getSwimAnimation(EntityPlayer player, float swimAnimation, float lastSwimAnimation, float partialTicks) {
            return NewMathHelper.lerp(partialTicks, lastSwimAnimation, swimAnimation);
        }

        public static boolean EntityPlayer$isEyesInWater(EntityPlayer player) {
            return player.isInsideOfMaterial(Material.WATER);
        }

        public static float EntityPlayer$updateSwimmingAnimation(EntityPlayer player, float swimAnimation) {
            SwimmingEntity entity = SwimmingEntity.cast(player);
            if (entity.isActuallySwimming()) {
                return Math.min(1.0F, swimAnimation + 0.09F);
            }
            else {
                return Math.max(0.0F, swimAnimation - 0.09F);
            }
        }

        public static boolean EntityPlayerSP$isActuallySneaking(EntityPlayerSP player) {
            return player.movementInput != null && player.movementInput.sneak;
        }

        public static boolean EntityPlayerSP$isForcedDown(EntityPlayerSP player) {
            SwimmingEntity swimming = SwimmingEntity.cast(player);
            return !player.capabilities.isFlying ? player.isSneaking() || swimming.isVisuallySwimming() : swimming.isActuallySwimming();
        }

        public static boolean EntityPlayerSP$isUsingSwimmingAnimation(EntityPlayerSP player) {
            SwimmingEntity swimming = SwimmingEntity.cast(player);
            ClientPlayerSwimming playerSwimming = ClientPlayerSwimming.cast(player);
            if (swimming.canSwim()) {
                return playerSwimming.isMovingForward(player.movementInput.moveForward, player.movementInput.moveStrafe);
            }
            /*
                if (ConfigHandler.MovementConfig.sidewaysSprinting) {
                    return moveForward >= 0.8F || Math.abs(moveStrafe) > 0.8F;
                }
             */
            return player.movementInput.moveForward >= 0.8F;
        }

        public static boolean EntityPlayerSP$isMovingForward(float moveForward, float moveStrafe) {
            if (moveForward > 1.0E-5F) {
                return true;
            }
            /*else if (ConfigHandler.MovementConfig.sidewaysSwimming) {
                return Math.abs(moveStrafe) > 1.0E-5F;
            }*/
            return false;
        }

        public static boolean EntityPlayerSP$pushOutOfBlocks(EntityPlayerSP player, double x, double y, double z) {
            if (!player.noClip) {
                BlockPos blockpos = new BlockPos(x, player.posY, z);
                if (shouldBlockPushPlayer(player, blockpos)) {
                    double d0 = x - blockpos.getX();
                    double d1 = z - blockpos.getZ();
                    EnumFacing direction = null;
                    double d2 = Double.MAX_VALUE;
                    EnumFacing[] xzPlane = new EnumFacing[]{EnumFacing.WEST, EnumFacing.EAST, EnumFacing.NORTH, EnumFacing.SOUTH};
                    for (EnumFacing direction1 : xzPlane) {
                        EnumFacing.Axis axis = direction1.getAxis();
                        double d3 = axis == EnumFacing.Axis.X ? d0 : axis == EnumFacing.Axis.Z ? d1 : 0.0;
                        double d4 = direction1.getAxisDirection() == EnumFacing.AxisDirection.POSITIVE ? 1.0 - d3 : d3;
                        if (d4 < d2 && !shouldBlockPushPlayer(player, blockpos.offset(direction1))) {
                            d2 = d4;
                            direction = direction1;
                        }
                    }
                    if (direction != null) {
                        if (direction.getAxis() == EnumFacing.Axis.X) {
                            player.motionX = 0.1 * direction.getDirectionVec().getX();
                        } else {
                            player.motionZ = 0.1 * direction.getDirectionVec().getZ();
                        }
                    }
                }
                return true;
            }
            return false;
        }

        public static void EntityPlayerSP$updatePlayerMoveState(EntityPlayerSP player) {
            ClientPlayerSwimming playerSwimming = ClientPlayerSwimming.cast(player);
            if (!player.movementInput.sneak && playerSwimming.isForcedDown()) {
                player.movementInput.moveStrafe = (float) ((double) player.movementInput.moveStrafe * 0.3);
                player.movementInput.moveForward = (float) ((double) player.movementInput.moveForward * 0.3);
            }
            if (player.movementInput.sneak && !playerSwimming.isForcedDown()) {
                player.movementInput.moveStrafe = (float) ((double) player.movementInput.moveStrafe / 0.3);
                player.movementInput.moveForward = (float) ((double) player.movementInput.moveForward / 0.3);
            }
        }

        public static int EntityPlayerSP$updateSprintToggleTimer(EntityPlayerSP player, int defaultValue) {
            if (player.movementInput.sneak) {
                return 0;
            }
            if (defaultValue > 0) {
                return defaultValue - 1;
            }
            if (player.isHandActive() && !player.isRiding()) {
                return 0;
            }
            return defaultValue;
        }

        private static boolean shouldBlockPushPlayer(EntityPlayerSP player, BlockPos pos) {
            double minY = player.getEntityBoundingBox().minY;
            double maxY = player.getEntityBoundingBox().maxY;
            AxisAlignedBB aabb = new AxisAlignedBB(pos.getX(), minY, pos.getZ(), pos.getX() + 1.0, maxY, pos.getZ() + 1.0);
            // don't use IBlockState#causesSuffocation as it works differently in newer versions
            return !isAxisAlignedBBNotClear(player.world, player, aabb.shrink(1.0E-7));
        }

        private static boolean isAxisAlignedBBNotClear(World world, @Nullable Entity entity, AxisAlignedBB aabb) {
            return createAxisAlignedBBStream(world, entity, aabb).allMatch(Objects::isNull);
        }

        private static Stream<AxisAlignedBB> createAxisAlignedBBStream(World world, @Nullable Entity entity, AxisAlignedBB aabb) {
            return StreamSupport.stream(new AxisAlignedBBSpliterator(world, entity, aabb), false);
        }

        public static boolean EntityPlayerSP$isCrouching(EntityPlayerSP player) {
            final boolean cantStand = !ModEntityStates.STANDING.isStateClear(player);
            if ((!player.capabilities.isFlying || !cantStand) && player.getTicksElytraFlying() <= 4) {
                if (!SwimmingEntity.cast(player).isSwimming() && (player.onGround || !player.isInWater())) {
                    if (!player.isOnLadder() && ModEntityStates.SNEAKING.isStateClear(player) || player.noClip) {
                        return player.movementInput.sneak || !player.isPlayerSleeping() && cantStand;
                    }
                }
            }
            return false;
        }

        public static void EntityPlayerSP$handleWaterSneaking(EntityPlayerSP player) {
            if (player.isInWater() && player.movementInput.sneak && !player.capabilities.isFlying) {
                player.motionY -= 0.03999999910593033 * player.getEntityAttribute(SWIM_SPEED).getAttributeValue();
            }
        }

        public static void EntityPlayerSP$handleSprinting(EntityPlayerSP player, int sprintToggleTimer) {
            final boolean isSaturated = (float) player.getFoodStats().getFoodLevel() > 6.0F || player.capabilities.allowFlying;
            ClientPlayerSwimming playerSwimming = ClientPlayerSwimming.cast(player);
            boolean wasSneaking = player.movementInput.sneak;
            boolean wasSwimming = playerSwimming.isUsingSwimmingAnimation();
            boolean isSprintingEnvironment = player.onGround || playerSwimming.canSwim() || player.capabilities.isFlying;
            if (isSprintingEnvironment && !wasSneaking && !wasSwimming && playerSwimming.isUsingSwimmingAnimation() && !player.isSprinting() && isSaturated && !player.isHandActive() && !player.isPotionActive(MobEffects.BLINDNESS)) {
                if (sprintToggleTimer <= 0 && !Minecraft.getMinecraft().gameSettings.keyBindSprint.isKeyDown()) {
//                    this.sprintToggleTimer = ConfigValues.noDoubleTapSprinting ? 0 : 7;
                } else {
                    player.setSprinting(true);
                }
            }
            if (!player.isSprinting() && (!player.isInWater() || playerSwimming.canSwim()) && playerSwimming.isUsingSwimmingAnimation() && isSaturated && !player.isHandActive() && !player.isPotionActive(MobEffects.BLINDNESS) && Minecraft.getMinecraft().gameSettings.keyBindSprint.isKeyDown()) {
                player.setSprinting(true);
            }

            if (player.isSprinting()) {
                boolean isNotMoving = !playerSwimming.isMovingForward(player.movementInput.moveForward, player.movementInput.moveStrafe) || !isSaturated;
                // don't stop sprint flying when breaching water surface
                boolean hasCollided = isNotMoving || player.collidedHorizontally || player.isInWater() && !playerSwimming.canSwim() && !player.capabilities.isFlying;
                if (SwimmingEntity.cast(player).isSwimming()) {
                    if (!player.movementInput.sneak && isNotMoving || !player.isInWater()) {
                        player.setSprinting(false);
                    }
                } else if (hasCollided) {
                    player.setSprinting(false);
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
            return !(entity instanceof EntityPlayer) || !SwimmingEntity.cast((EntityPlayer) entity).isSwimming();
        }
    }

    private SwimmingTransformer() {}
}
