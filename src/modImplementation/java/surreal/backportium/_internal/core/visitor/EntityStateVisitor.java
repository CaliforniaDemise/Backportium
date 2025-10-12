package surreal.backportium._internal.core.visitor;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import surreal.backportium._internal.bytecode.asm.LeClassVisitor;
import surreal.backportium._internal.entity.SetSize;
import surreal.backportium.api.entity.EntityState;
import surreal.backportium.api.entity.EntityWithState;
import surreal.backportium.api.entity.RiptideEntity;
import surreal.backportium.api.entity.SwimmingEntity;
import surreal.backportium.event.LivingMoveEvent;

import java.util.function.Function;

import static surreal.backportium.init.ModEntityStates.*;

public final class EntityStateVisitor {

    private static final String HOOKS = "surreal/backportium/_internal/core/visitor/EntityStateVisitor$Hooks";
    private static final String MOVING_ENTITY = "surreal/backportium/api/entity/EntityWithState";
    private static final String SET_SIZE = "surreal/backportium/_internal/entity/SetSize";

    @Nullable
    public static Function<ClassVisitor, ClassVisitor> getClassVisitor(String name, String transformedName) {
        switch (transformedName) {
            case "net.minecraft.entity.Entity": return EntityVisitor::new;
            case "net.minecraft.entity.player.EntityPlayer": return EntityPlayerVisitor::new;
            case "net.minecraft.entity.EntityLivingBase": return EntityLivingBaseVisitor::new;
            case "net.minecraft.client.model.ModelBiped": return ModelBipedVisitor::new;
            case "net.minecraft.client.renderer.entity.RenderLivingBase":
            case "net.minecraft.client.renderer.entity.RenderPlayer": return RenderVisitor::new;
        }
        return null;
    }

    public static class EntityVisitor extends LeClassVisitor {

        public EntityVisitor(ClassVisitor cv) {
            super(cv);
        }

        @Override
        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            super.visit(version, access, name, signature, superName, getInterfaces(interfaces, SET_SIZE));
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            if (name.equals(getName("setSize", "func_70105_a"))) access = setAccess(access, ACC_PUBLIC);
            return super.visitMethod(access, name, desc, signature, exceptions);
        }
    }

    public static class EntityPlayerVisitor extends LeClassVisitor {

        public EntityPlayerVisitor(ClassVisitor cv) {
            super(cv);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
            if (name.equals(getName("updateSize", "func_184808_cD"))) return new UpdateSizeVisitor(mv);
            if (name.equals(getName("getEyeHeight", "func_70047_e"))) return new GetEyeHeightVisitor(mv);
            return mv;
        }

        private static class UpdateSizeVisitor extends MethodVisitor {

            public UpdateSizeVisitor(MethodVisitor mv) {
                super(ASM5, mv);
            }

            @Override
            public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
                if (name.equals(getName("setSize", "func_70105_a"))) {
                    super.visitMethodInsn(INVOKESTATIC, HOOKS, "EntityPlayer$setSize", "(Lnet/minecraft/entity/EntityLivingBase;FF)V", false);
                    return;
                }
                super.visitMethodInsn(opcode, owner, name, desc, itf);
            }
        }

        private static class GetEyeHeightVisitor extends MethodVisitor {

            public GetEyeHeightVisitor(MethodVisitor mv) {
                super(ASM5, mv);
            }

            @Override
            public void visitInsn(int opcode) {
                if (opcode == FRETURN) {
                    super.visitVarInsn(ALOAD, 0);
                    super.visitMethodInsn(INVOKESTATIC, HOOKS, "EntityLivingBase$getEyeHeight", "(FLnet/minecraft/entity/EntityLivingBase;)F", false);
                }
                super.visitInsn(opcode);
            }
        }
    }

    public static class EntityLivingBaseVisitor extends LeClassVisitor {

        private boolean hasEyeHeight = false;

        public EntityLivingBaseVisitor(ClassVisitor cv) {
            super(cv);
        }

        @Override
        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            super.visit(version, access, name, signature, superName, getInterfaces(interfaces, MOVING_ENTITY));
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
            if (name.equals(getName("onUpdate", "func_70071_h_"))) return new OnUpdate(mv);
            if (name.equals(getName("getEyeHeight", "func_70047_e"))) {
                hasEyeHeight = true;
                return new GetEyeHeightVisitor(mv);
            }
            return mv;
        }

        @Override
        public void visitEnd() {
            super.visitEnd();
            { // getMove
                MethodVisitor mv = super.visitMethod(ACC_PUBLIC, "getMove", "()Lsurreal/backportium/api/entity/EntityState;", null, null);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitMethodInsn(INVOKESTATIC, HOOKS, "EntityLivingBase$getMove", "(Lnet/minecraft/entity/EntityLivingBase;)Lsurreal/backportium/api/entity/EntityState;", false);
                mv.visitInsn(ARETURN);
                mv.visitMaxs(1, 0);
            }
            if (!hasEyeHeight) { // getEyeHeight
                MethodVisitor mv = super.visitMethod(ACC_PUBLIC, "getEyeHeight", "()F", null, null);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitMethodInsn(INVOKESPECIAL, "net/minecraft/entity/Entity", "getEyeHeight", "()F", false);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitMethodInsn(INVOKESTATIC, HOOKS, "EntityLivingBase$getEyeHeight", "(FLnet/minecraft/entity/EntityLivingBase;)F", false);
                mv.visitInsn(FRETURN);
                mv.visitMaxs(1, 0);
            }
        }

        private static class OnUpdate extends MethodVisitor {

            public OnUpdate(MethodVisitor mv) {
                super(ASM5, mv);
            }

            @Override
            public void visitInsn(int opcode) {
                if (opcode == RETURN) {
                    super.visitVarInsn(ALOAD, 0);
                    super.visitMethodInsn(INVOKESTATIC, HOOKS, "EntityLivingBase$updateSize", "(Lnet/minecraft/entity/EntityLivingBase;)V", false);
                }
                super.visitInsn(opcode);
            }
        }

        private static class GetEyeHeightVisitor extends MethodVisitor {

            public GetEyeHeightVisitor(MethodVisitor mv) {
                super(ASM5, mv);
            }

            @Override
            public void visitInsn(int opcode) {
                if (opcode == FRETURN) {
                    super.visitVarInsn(ALOAD, 0);
                    super.visitMethodInsn(INVOKESTATIC, HOOKS, "EntityLivingBase$getEyeHeight", "(FLnet/minecraft/entity/EntityLivingBase;)F", false);
                }
                super.visitInsn(opcode);
            }
        }
    }

    public static class ModelBipedVisitor extends LeClassVisitor {

        public ModelBipedVisitor(ClassVisitor cv) {
            super(cv);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
            if (name.equals(getName("setRotationAngles", "func_78087_a"))) return new SetRotationAnglesVisitor(mv);
            return mv;
        }

        private static class SetRotationAnglesVisitor extends MethodVisitor {

            public SetRotationAnglesVisitor(MethodVisitor mv) {
                super(ASM5, mv);
            }

            @Override
            public void visitInsn(int opcode) {
                if (opcode == RETURN) {
                    super.visitVarInsn(ALOAD, 0);
                    super.visitVarInsn(FLOAD, 1);
                    super.visitVarInsn(FLOAD, 2);
                    super.visitVarInsn(FLOAD, 3);
                    super.visitVarInsn(FLOAD, 4);
                    super.visitVarInsn(FLOAD, 5);
                    super.visitVarInsn(FLOAD, 6);
                    super.visitVarInsn(ALOAD, 7);
                    super.visitMethodInsn(INVOKESTATIC, HOOKS, "ModelBiped$setRotationAngles", "(Lnet/minecraft/client/model/ModelBiped;FFFFFFLnet/minecraft/entity/Entity;)V", false);
                }
                super.visitInsn(opcode);
            }
        }
    }

    public static class RenderVisitor extends LeClassVisitor {

        private boolean player;

        public RenderVisitor(ClassVisitor cv) {
            super(cv);
        }

        @Override
        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            if (name.equals("net/minecraft/client/renderer/entity/RenderPlayer")) player = true;
            super.visit(version, access, name, signature, superName, interfaces);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
            if (name.equals(getName("applyRotations", "func_77043_a"))) return new ApplyRotationsVisitor(mv, player);
            return mv;
        }

        private static class ApplyRotationsVisitor extends MethodVisitor {

            private final boolean player;

            public ApplyRotationsVisitor(MethodVisitor mv, boolean player) {
                super(ASM5, mv);
                this.player = player;
            }

            @Override
            public void visitInsn(int opcode) {
                if (opcode == RETURN) {
                    super.visitVarInsn(ALOAD, 0);
                    super.visitVarInsn(ALOAD, 1);
                    super.visitVarInsn(FLOAD, 2);
                    super.visitVarInsn(FLOAD, 3);
                    super.visitVarInsn(FLOAD, 4);
                    super.visitInsn(player ? ICONST_1 : ICONST_0);
                    super.visitMethodInsn(INVOKESTATIC, HOOKS, "RenderLiving$applyRotations", "(Lnet/minecraft/client/renderer/entity/RenderLivingBase;Lnet/minecraft/entity/EntityLivingBase;FFFZ)V", false);
                }
                super.visitInsn(opcode);
            }
        }
    }

    @SuppressWarnings("unused")
    public static class Hooks {

        @Nullable
        public static EntityState EntityLivingBase$getMove(EntityLivingBase entity) {
            EntityState move = null;
            if (entity.isPlayerSleeping()) move = SLEEPING;
            if (RiptideEntity.cast(entity).inRiptide()) move = RIPTIDE;
            if (entity.isElytraFlying()) move = FLYING;
            if (entity.isSneaking()) move = SNEAKING;
            if (SwimmingEntity.cast(entity).isSwimming()) move = SWIMMING;
            {
                LivingMoveEvent event = new LivingMoveEvent(entity, move);
                if (MinecraftForge.EVENT_BUS.post(event)) {
                    move = event.getNewMove();
                }
            }
            return move;
        }

        public static void EntityPlayer$setSize(EntityLivingBase entity, float width, float height) {
            EntityState move = EntityWithState.cast(entity).getMove();
            if (move != null) {
                SetSize.cast(entity).setSize(move.getWidth(entity, width), move.getHeight(entity, height));
            }
            else {
                SetSize.cast(entity).setSize(width, height);
            }
        }

        public static void EntityLivingBase$updateSize(EntityLivingBase entity) {
            EntityState move = EntityWithState.cast(entity).getMove();
            if (move != null) {
                SetSize.cast(entity).setSize(move.getWidth(entity, entity.width), move.getHeight(entity, entity.height));
            }
        }

        public static float EntityLivingBase$getEyeHeight(float defaultValue, EntityLivingBase entity) {
            EntityState move = EntityWithState.cast(entity).getMove();
            if (move != null) {
                return move.getEyeHeight(entity, defaultValue);
            }
            return defaultValue;
        }


        @SideOnly(Side.CLIENT)
        public static void ModelBiped$setRotationAngles(ModelBiped model, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
            if (entityIn instanceof EntityLivingBase) {
                EntityLivingBase living = (EntityLivingBase) entityIn;
                EntityState move = EntityWithState.cast(living).getMove();
                if (move != null) {
                    move.applyModelRotations(model, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entityIn);
                }
            }
        }

        @SideOnly(Side.CLIENT)
        public static <T extends EntityLivingBase> void RenderLiving$applyRotations(RenderLivingBase<T> render, T living, float ageInTicks, float rotationYaw, float partialTicks, boolean player) {
            EntityState move = EntityWithState.cast(living).getMove();
            if (move != null) {
                move.applyRenderRotations(render, living, ageInTicks, rotationYaw, partialTicks, player);
            }
        }
    }

    private EntityStateVisitor() {}
}
