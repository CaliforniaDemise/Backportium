package surreal.backportium.core.v13;

import org.objectweb.asm.tree.*;
import surreal.backportium.core.transformers.Transformer;

import java.util.Iterator;

public class SlowFallingTransformer extends Transformer {

    public static byte[] transformEntityLivingBase(byte[] basicClass) {
        ClassNode cls = read(basicClass);
        for (MethodNode method : cls.methods) {
            if (method.name.equals(getName("travel", "func_191986_a"))) {
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node instanceof LdcInsnNode && (((LdcInsnNode) node).cst.equals(0.08D) || ((LdcInsnNode) node).cst.equals(-0.08D))) {
                        node = iterator.next();
                        InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(hook("SlowFalling$fallingSpeed", "(DLnet/minecraft/entity/EntityLivingBase;)D"));
                        method.instructions.insertBefore(node, list);
                    }
                }
                break;
            }
        }
        writeClass(cls);
        return write(cls);
    }
}
