package surreal.backportium._internal.core.visitor;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import surreal.backportium._internal.bytecode.asm.LeClassVisitor;
import surreal.backportium.util.NewMathHelper;

/**
 * Interpolates eye height changes and allows block overlays to be rendered when player is in water
 */
public final class CameraVisitor {

    private static final String HOOKS = "surreal/backportium/_internal/core/visitor/CameraVisitor$Hooks";

    public static class EntityRendererVisitor extends LeClassVisitor {

        public EntityRendererVisitor(ClassVisitor cv) {
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
            if (name.equals(getName("renderWorldPass", "func_175068_a"))) return new RenderWorldPassVisitor(mv);
            if (name.equals(getName("updateRenderer", "func_78464_a"))) return new UpdateRendererVisitor(mv);
            if (name.equals(getName("orientCamera", "func_78467_g"))) return new OrientCameraVisitor(mv);
            return mv;
        }

        private static class UpdateRendererVisitor extends MethodVisitor {

            public UpdateRendererVisitor(MethodVisitor mv) {
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

        private static class OrientCameraVisitor extends MethodVisitor {

            private boolean check = false;

            public OrientCameraVisitor(MethodVisitor mv) {
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

        private static class RenderWorldPassVisitor extends MethodVisitor {

            public RenderWorldPassVisitor(MethodVisitor mv) {
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
    public static class Hooks {

        public static boolean EntityRenderer$empty(Entity entity, Material material) {
            return false;
        }

        public static float EntityRenderer$getEyeHeight(float defaultValue, Entity entity, float eyeHeight, float previousEyeHeight, float partialTicks) {
            // Do not apply eye height patch if the camera is not a player, or if Random Patches is installed
//            if (!(entity instanceof EntityPlayer) || IntegrationManager.isRandomPatchesEnabled()) {
//                return eyeHeight;
//            }
            if (!(entity instanceof EntityPlayer)) {
                return defaultValue;
            }
            return NewMathHelper.lerp(partialTicks, previousEyeHeight, eyeHeight);
        }
    }

    private CameraVisitor() {}
}
