package surreal.backportium.core.v13;

import org.objectweb.asm.tree.*;
import surreal.backportium.core.transformers.Transformer;

/**
 * Features that are pretty basic to implement and not need a transformer class.
 **/
class RandomTransformer extends Transformer {

    /**
     * Adds custom death message to Nether bed explosion
     **/
    protected static byte[] transformBlockBed(byte[] basicClass) {
        ClassNode cls = read(basicClass);
        for (MethodNode method : cls.methods) {
            if (method.name.equals("onBlockActivated")) {
                AbstractInsnNode node = method.instructions.getLast();
                while (node.getOpcode() != INVOKEVIRTUAL) node = node.getPrevious();
                node = node.getPrevious();
                method.instructions.remove(node.getNext());
                method.instructions.insert(node, new MethodInsnNode(INVOKESPECIAL, "surreal/backportium/api/world/ExplosionGameDesign", "<init>", "(Lnet/minecraft/world/World;Lnet/minecraft/entity/Entity;DDDFII)V", false));
                while (node.getOpcode() != ACONST_NULL) node = node.getPrevious();
                node = node.getPrevious();
                InsnList list = new InsnList();
                list.add(new TypeInsnNode(NEW, "surreal/backportium/api/world/ExplosionGameDesign"));
                list.add(new InsnNode(DUP));
                method.instructions.insertBefore(node, list);
                break;
            }
        }
        return write(cls);
    }
}
