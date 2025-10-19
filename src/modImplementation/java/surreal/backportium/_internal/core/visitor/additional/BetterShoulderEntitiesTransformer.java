package surreal.backportium._internal.core.visitor.additional;

import net.minecraft.entity.player.EntityPlayer;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import surreal.backportium._internal.ConfigValues;
import surreal.backportium._internal.bytecode.asm.LeClassVisitor;

import java.util.function.Function;

public final class BetterShoulderEntitiesTransformer {

    private static final String HOOKS = "surreal/backportium/_internal/core/visitor/additional/BetterShoulderEntitiesTransformer$Hooks";

    public static Function<ClassVisitor, ClassVisitor> visit(String name, String transformedName, byte[] bytes) {
        if (!ConfigValues.sneakToDropParrots) return null;
        if (transformedName.equals("net.minecraft.entity.player.EntityPlayer")) return EntityPlayerVisitor::new;
        return null;
    }

    private static class EntityPlayerVisitor extends LeClassVisitor {

        public EntityPlayerVisitor(ClassVisitor cv) {
            super(cv);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
            if (name.equals(getName("onLivingUpdate", "func_70636_d"))) return new OnLivingUpdateVisitor(mv);
            if (name.equals(getName("addShoulderEntity", "func_192027_g"))) return new AddShoulderEntityVisitor(mv);
            return mv;
        }

        private static class OnLivingUpdateVisitor extends MethodVisitor {

            public OnLivingUpdateVisitor(MethodVisitor mv) {
                super(ASM5, mv);
            }

            @Override
            public void visitFieldInsn(int opcode, String owner, String name, String desc) {
                super.visitFieldInsn(opcode, owner, name, desc);
                if (opcode == GETFIELD && name.equals(getName("isFlying", "field_75100_b"))) {
                    super.visitVarInsn(ALOAD, 0);
                    super.visitMethodInsn(INVOKESTATIC, HOOKS, "EntityPlayer$shouldSpawnShoulderEntities", "(ZLnet/minecraft/entity/player/EntityPlayer;)Z", false);
                }
            }
        }

        private static class AddShoulderEntityVisitor extends MethodVisitor {

            public AddShoulderEntityVisitor(MethodVisitor mv) {
                super(ASM5, mv);
            }

            @Override
            public void visitCode() {
                super.visitCode();
                super.visitVarInsn(ALOAD, 0);
                super.visitMethodInsn(INVOKEVIRTUAL, "net/minecraft/entity/player/EntityPlayer", getName("isSneaking", "func_70093_af"), "()Z", false);
                Label l_con = new Label();
                super.visitJumpInsn(IFEQ, l_con);
                super.visitInsn(ICONST_0);
                super.visitInsn(IRETURN);
                super.visitLabel(l_con);
                super.visitFrame(F_SAME, 0, null, 0, null);
            }
        }
    }

    @SuppressWarnings("unused")
    public static class Hooks {

        public static boolean EntityPlayer$shouldSpawnShoulderEntities(boolean _defaultValue, EntityPlayer player) {
            return !player.world.isRemote && (player.isSneaking() || player.isInWater());
        }
    }

    private BetterShoulderEntitiesTransformer() {}
}
