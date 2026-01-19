package surreal.backportium._internal.core.visitor;

import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import surreal.backportium._internal.ConfigValues;
import surreal.backportium._internal.bytecode.asm.LeClassVisitor;

import java.util.function.Function;

import static _mod.Constants.V_BETTER_SHOULDER_ENTITIES;

public final class BetterShoulderEntities {

    private static final String HOOKS = V_BETTER_SHOULDER_ENTITIES + "$Hooks";

    @Nullable
    public static Function<ClassVisitor, ClassVisitor> visit(String name, String transformedName, byte[] bytes) {
        if (!ConfigValues.sneakToDropParrots) return null;
        if (transformedName.equals("net.minecraft.entity.player.EntityPlayer")) return EntityPlayer::new;
        return null;
    }

    private static final class EntityPlayer extends LeClassVisitor {

        public EntityPlayer(ClassVisitor cv) {
            super(cv);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
            if (name.equals(getName("onLivingUpdate", "func_70636_d"))) return new OnLivingUpdate(mv);
            if (name.equals(getName("addShoulderEntity", "func_192027_g"))) return new AddShoulderEntity(mv);
            return mv;
        }

        private static final class OnLivingUpdate extends MethodVisitor {

            public OnLivingUpdate(MethodVisitor mv) {
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

        private static final class AddShoulderEntity extends MethodVisitor {

            public AddShoulderEntity(MethodVisitor mv) {
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
    public static final class Hooks {

        public static boolean EntityPlayer$shouldSpawnShoulderEntities(boolean _defaultValue, net.minecraft.entity.player.EntityPlayer player) {
            return !player.world.isRemote && (player.isSneaking() || player.isInWater());
        }

        private Hooks() {}
    }

    private BetterShoulderEntities() {}
}
