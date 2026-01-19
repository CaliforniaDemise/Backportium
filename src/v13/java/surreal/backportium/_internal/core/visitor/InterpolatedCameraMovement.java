package surreal.backportium._internal.core.visitor;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import surreal.backportium._internal.ConfigValues;
import surreal.backportium._internal.bytecode.asm.LeClassVisitor;
import surreal.backportium.util.NewMathHelper;

import java.util.function.Function;

import static _mod.Constants.V_INTERPOLATED_CAMERA_MOVEMENT;

/**
 * Interpolates eye height changes and allows block overlays to be rendered when player is in water
 */
public final class InterpolatedCameraMovement {

    private static final String HOOKS = V_INTERPOLATED_CAMERA_MOVEMENT + "$Hooks";

    @Nullable
    public static Function<ClassVisitor, ClassVisitor> visit(String name, String transformedName, byte[] bytes) {
        switch (transformedName) {
            case "net.minecraft.client.renderer.EntityRenderer": return EntityRenderer::new;
            case "net.minecraft.entity.player.EntityPlayer": return EntityPlayer::new;
            default: return null;
        }
    }

    private static final class EntityPlayer extends LeClassVisitor {

        public EntityPlayer(ClassVisitor cv) {
            super(cv);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
            if (name.equals(getName("onLivingUpdate", "func_70636_d"))) return new OnLivingUpdate(mv);
            return mv;
        }

        private static final class OnLivingUpdate extends MethodVisitor {

            public OnLivingUpdate(MethodVisitor mv) {
                super(ASM5, mv);
            }

            @Override
            public void visitFieldInsn(int opcode, String owner, String name, String desc) {
                if (opcode == PUTFIELD && name.equals(getName("cameraPitch", "field_70726_aT"))) {
                    super.visitMethodInsn(INVOKESTATIC, HOOKS, "EntityPlayer$getCameraPitch", "(F)F", false);
                }
                super.visitFieldInsn(opcode, owner, name, desc);
            }
        }
    }

    private static final class EntityRenderer extends LeClassVisitor {

        public EntityRenderer(ClassVisitor cv) {
            super(cv);
        }

        @Override
        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            super.visit(version, access, name, signature, superName, interfaces);
            super.visitField(ACC_PRIVATE, "eyeHeight", "F", null, 0.0F);
            super.visitField(ACC_PRIVATE, "previousEyeHeight", "F", null, 0.0F);
            super.visitField(ACC_PRIVATE, "entityEyeHeight", "F", null, 0.0F);
            super.visitField(ACC_PRIVATE, "partialTicks", "F", null, 0.0F);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
            if (name.equals(getName("renderWorldPass", "func_175068_a"))) return new RenderWorldPass(mv);
            if (name.equals(getName("updateRenderer", "func_78464_a"))) return new UpdateRenderer(mv);
            if (name.equals(getName("orientCamera", "func_78467_g"))) return new OrientCamera(mv);
            return mv;
        }

        private static final class UpdateRenderer extends MethodVisitor {

            public UpdateRenderer(MethodVisitor mv) {
                super(ASM5, mv);
            }

            @Override
            public void visitInsn(int opcode) {
                if (opcode == RETURN) {
                    super.visitVarInsn(ALOAD, 0);
                    super.visitVarInsn(ALOAD, 0);
                    super.visitFieldInsn(GETFIELD, "net/minecraft/client/renderer/EntityRenderer", "eyeHeight", "F");
                    super.visitFieldInsn(PUTFIELD, "net/minecraft/client/renderer/EntityRenderer", "previousEyeHeight", "F");
                    super.visitVarInsn(ALOAD, 0);
                    super.visitVarInsn(ALOAD, 0);
                    super.visitFieldInsn(GETFIELD, "net/minecraft/client/renderer/EntityRenderer", "entityEyeHeight", "F");
                    super.visitVarInsn(ALOAD, 0);
                    super.visitFieldInsn(GETFIELD, "net/minecraft/client/renderer/EntityRenderer", "eyeHeight", "F");
                    super.visitInsn(FSUB);
                    super.visitLdcInsn(0.5F);
                    super.visitInsn(FMUL);
                    super.visitVarInsn(ALOAD, 0);
                    super.visitFieldInsn(GETFIELD, "net/minecraft/client/renderer/EntityRenderer", "eyeHeight", "F");
                    super.visitInsn(FADD);
                    super.visitFieldInsn(PUTFIELD, "net/minecraft/client/renderer/EntityRenderer", "eyeHeight", "F");
                }
                super.visitInsn(opcode);
            }
        }

        private static final class OrientCamera extends MethodVisitor {

            private boolean check = false;

            public OrientCamera(MethodVisitor mv) {
                super(ASM5, mv);
            }

            @Override
            public void visitCode() {
                super.visitCode();
                super.visitVarInsn(ALOAD, 0);
                super.visitVarInsn(FLOAD, 1);
                super.visitFieldInsn(PUTFIELD, "net/minecraft/client/renderer/EntityRenderer", "partialTicks", "F");
            }

            @Override
            public void visitVarInsn(int opcode, int var) {
                super.visitVarInsn(opcode, var);
                if (!check && opcode == ASTORE) {
                    check = true;
                    super.visitVarInsn(ALOAD, 0);
                    super.visitVarInsn(ALOAD, 2);
                    super.visitMethodInsn(INVOKEVIRTUAL, "net/minecraft/entity/Entity", getName("getEyeHeight", "func_70047_e"), "()F", false);
                    super.visitFieldInsn(PUTFIELD, "net/minecraft/client/renderer/EntityRenderer", "entityEyeHeight", "F");
                }
            }

            @Override
            public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
                super.visitMethodInsn(opcode, owner, name, desc, itf);
                if (opcode == INVOKEVIRTUAL && name.equals(getName("getEyeHeight", "func_70047_e"))) {
                    super.visitVarInsn(ALOAD, 2);
                    super.visitVarInsn(ALOAD, 0);
                    super.visitFieldInsn(GETFIELD, "net/minecraft/client/renderer/EntityRenderer", "eyeHeight", "F");
                    super.visitVarInsn(ALOAD, 0);
                    super.visitFieldInsn(GETFIELD, "net/minecraft/client/renderer/EntityRenderer", "previousEyeHeight", "F");
                    super.visitVarInsn(ALOAD, 0);
                    super.visitFieldInsn(GETFIELD, "net/minecraft/client/renderer/EntityRenderer", "partialTicks", "F");
                    super.visitMethodInsn(INVOKESTATIC, HOOKS, "EntityRenderer$getEyeHeight", "(FLnet/minecraft/entity/Entity;FFF)F", false);
                }
            }
        }

        private static final class RenderWorldPass extends MethodVisitor {

            public RenderWorldPass(MethodVisitor mv) {
                super(ASM5, mv);
            }

            @Override
            public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
                if (opcode == INVOKEVIRTUAL && name.equals(getName("isInsideOfMaterial", "func_70055_a"))) {
                    super.visitMethodInsn(INVOKESTATIC, HOOKS, "EntityRenderer$empty", "(Lnet/minecraft/entity/Entity;Lnet/minecraft/block/material/Material;)Z", false);
                    return;
                }
                super.visitMethodInsn(opcode, owner, name, desc, itf);
            }
        }
    }

    @SuppressWarnings("unused")
    public static final class Hooks {

        public static boolean EntityRenderer$empty(Entity entity, Material material) {
            return false;
        }

        public static float EntityRenderer$getEyeHeight(float defaultValue, Entity entity, float eyeHeight, float previousEyeHeight, float partialTicks) {
            // Do not apply eye height patch if the camera is not a player, or if Random Patches is installed
//            if (!(entity instanceof EntityPlayer) || IntegrationManager.isRandomPatchesEnabled()) {
//                return eyeHeight;
//            }
            if (!(entity instanceof net.minecraft.entity.player.EntityPlayer)) {
                return defaultValue;
            }
            return NewMathHelper.lerp(partialTicks, previousEyeHeight, eyeHeight);
        }

        public static float EntityPlayer$getCameraPitch(float defaultValue) {
            return ConfigValues.disablePitchBobbing ? 0.0F : defaultValue;
        }

        private Hooks() {}
    }

    private InterpolatedCameraMovement() {}
}
