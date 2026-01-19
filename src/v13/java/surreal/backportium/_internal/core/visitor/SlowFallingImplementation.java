package surreal.backportium._internal.core.visitor;

import net.minecraft.entity.EntityLivingBase;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import surreal.backportium._internal.bytecode.asm.LeClassVisitor;
import surreal.backportium.init.ModPotions;

import java.util.Objects;
import java.util.function.Function;

import static _mod.Constants.V_SLOW_FALLING_IMPLEMENTATION;

public final class SlowFallingImplementation {

    private static final String HOOKS = V_SLOW_FALLING_IMPLEMENTATION + "$Hooks";

    @Nullable
    public static Function<ClassVisitor, ClassVisitor> visit(String name, String transformedName, byte[] bytes) {
        if (transformedName.equals("net.minecraft.entity.EntityLivingBase")) return EntityLivingBase::new;
        return null;
    }

    private static final class EntityLivingBase extends LeClassVisitor {

        public EntityLivingBase(ClassVisitor cv) {
            super(cv);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
            if (name.equals(getName("travel", "func_191986_a"))) return new TravelVisitor(mv);
            return mv;
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

    @SuppressWarnings("unused")
    public static final class Hooks {

        public static double SlowFalling$getSpeed(double original, net.minecraft.entity.EntityLivingBase entity) {
            if (entity.motionY <= 0.0 && entity.isPotionActive(ModPotions.SLOW_FALLING)) {
                entity.fallDistance = 0.0F;
                return original / (8.0 + Objects.requireNonNull(entity.getActivePotionEffect(ModPotions.SLOW_FALLING)).getAmplifier());
            }
            return original;
        }

        private Hooks() {}
    }

    private SlowFallingImplementation() {}
}
