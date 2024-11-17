package surreal.backportium.core.transformers;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.tree.*;

public class BiomeTransformer extends BasicTransformer {

    public static byte[] transformBiome(byte[] basicClass) {
        ClassNode cls = read(basicClass);
        { // getTheTemperature
            MethodVisitor m = cls.visitMethod(ACC_PUBLIC, "getTheTemperature", "(Lnet/minecraft/util/math/BlockPos;)F", null, null);
            m.visitVarInsn(ALOAD, 0);
            m.visitMethodInsn(INVOKEVIRTUAL, cls.name, getName("getDefaultTemperature", "func_185353_n"), "()F", false);
            m.visitInsn(FRETURN);
        }
        for (MethodNode method : cls.methods) {
            if (method.name.equals(getName("getTemperature", "func_180626_a"))) {
                AbstractInsnNode node = method.instructions.getLast();
                while (node.getOpcode() != INVOKEVIRTUAL) node = node.getPrevious();
                InsnList list = new InsnList();
                list.add(new VarInsnNode(ALOAD, 1));
                list.add(new MethodInsnNode(INVOKEVIRTUAL, cls.name, "getTheTemperature", "(Lnet/minecraft/util/math/BlockPos;)F", false));
                method.instructions.insertBefore(node, list);
                method.instructions.remove(node);
                break;
            }
        }
        return write(cls);
    }
}
