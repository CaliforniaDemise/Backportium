package surreal.backportium._internal.core.visitor;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import surreal.backportium._internal.bytecode.asm.LeClassVisitor;

import java.util.function.Function;

public final class ElytraVisitor {

    public static Function<ClassVisitor, ClassVisitor> visit(String name, String transformedName, byte[] bytes) {
        if (name.equals("net.minecraft.network.NetHandlerPlayServer")) return NetHandlerPlayServerVisitor::new;
        return null;
    }

    private static class NetHandlerPlayServerVisitor extends LeClassVisitor {

        public NetHandlerPlayServerVisitor(ClassVisitor cv) {
            super(cv);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
            if (name.equals(getName("processEntityAction", "func_147357_a"))) return new ProcessEntityActionVisitor(mv);
            return mv;
        }

        private static class ProcessEntityActionVisitor extends MethodVisitor {

            public ProcessEntityActionVisitor(MethodVisitor mv) {
                super(ASM5, mv);
            }

            @Override
            public void visitInsn(int opcode) {
                if (opcode == DCONST_0) {
                    super.visitLdcInsn(0x1.fffffffffffffP+1023);
                    return;
                }
                super.visitInsn(opcode);
            }
        }
    }

    private ElytraVisitor() {}
}
