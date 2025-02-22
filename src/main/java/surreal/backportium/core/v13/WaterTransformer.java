package surreal.backportium.core.v13;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.tree.ClassNode;
import surreal.backportium.core.transformers.Transformer;

public class WaterTransformer extends Transformer {

    public static byte[] transformBlockLiquid(byte[] basicClass) {
        ClassNode cls = read(basicClass);
        { // getLightOpacity
            MethodVisitor m = cls.visitMethod(ACC_PUBLIC, getName("getLightOpacity", "func_149717_k"), "(Lnet/minecraft/block/state/IBlockState;)I", null, null);
            m.visitInsn(ICONST_1);
            m.visitInsn(IRETURN);
        }
        return write(cls);
    }
}
