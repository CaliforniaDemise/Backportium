package surreal.backportium._internal.core.visitor;

import net.minecraft.entity.Entity;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import surreal.backportium._internal.bytecode.asm.LeClassVisitor;
import surreal.backportium._internal.world.ExplosionGameDesign;

import java.util.function.Function;

public final class BedExplosionTransformer {

    private static final String HOOKS = "surreal/backportium/_internal/core/visitor/BedExplosionTransformer$Hooks";

    public static Function<ClassVisitor, ClassVisitor> visit(String name, String transformedName, byte[] bytes) {
        if (transformedName.equals("net.minecraft.block.BlockBed")) return BlockBedVisitor::new;
        return null;
    }

    private static class BlockBedVisitor extends LeClassVisitor {

        public BlockBedVisitor(ClassVisitor cv) {
            super(cv);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
            if (name.equals(getName("onBlockActivated", "func_180639_a"))) return new OnBlockActivated(mv);
            return mv;
        }

        private static class OnBlockActivated extends MethodVisitor {

            public OnBlockActivated(MethodVisitor mv) {
                super(ASM5, mv);
            }

            @Override
            public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
                if (opcode == INVOKEVIRTUAL && name.equals(getName("newExplosion", "func_72885_a"))) {
                    super.visitMethodInsn(INVOKESTATIC, HOOKS, "BlockBed$newExplosion", "(Lnet/minecraft/world/World;Lnet/minecraft/entity/Entity;DDDFZZ)Lnet/minecraft/world/Explosion;", false);
                    return;
                }
                super.visitMethodInsn(opcode, owner, name, desc, itf);
            }
        }
    }

    @SuppressWarnings("unused")
    public static class Hooks {

        public static Explosion BlockBed$newExplosion(World world, Entity entity, double x, double y, double z, float strength, boolean causesFire, boolean damagesTerrain) {
            ExplosionGameDesign explosion = new ExplosionGameDesign(world, entity, x, y, z, strength, causesFire, damagesTerrain);
            if (net.minecraftforge.event.ForgeEventFactory.onExplosionStart(world, explosion)) return explosion;
            explosion.doExplosionA();
            explosion.doExplosionB(true);
            return explosion;
        }
    }

    private BedExplosionTransformer() {}
}
