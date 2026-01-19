package surreal.backportium._internal.core.visitor;

import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import surreal.backportium._internal.bytecode.asm.LeClassVisitor;

import java.util.function.Function;

public final class EasyElytraTakeOff {

    @Nullable
    public static Function<ClassVisitor, ClassVisitor> visit(String name, String transformedName, byte[] bytes) {
        if (name.equals("net.minecraft.network.NetHandlerPlayServer")) return NetHandlerPlayServer::new;
        return null;
    }

    private static final class NetHandlerPlayServer extends LeClassVisitor {

        public NetHandlerPlayServer(ClassVisitor cv) {
            super(cv);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
            if (name.equals(getName("processEntityAction", "func_147357_a"))) return new ProcessEntityAction(mv);
            return mv;
        }

        private static final class ProcessEntityAction extends MethodVisitor {

            public ProcessEntityAction(MethodVisitor mv) {
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

    private EasyElytraTakeOff() {}
}
