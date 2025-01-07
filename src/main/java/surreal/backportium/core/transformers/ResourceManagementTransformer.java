package surreal.backportium.core.transformers;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;

import java.util.Iterator;

public class ResourceManagementTransformer extends Transformer {

    public static byte[] transformFMLClientHandler(byte[] basicClass) {
        ClassNode cls = read(basicClass);
        { // addPackToList
            MethodVisitor m = cls.visitMethod(ACC_PUBLIC, "addPackToList", "(Lnet/minecraft/client/resources/IResourcePack;)V", null, null);
            m.visitVarInsn(ALOAD, 0);
            m.visitFieldInsn(GETFIELD, cls.name, "resourcePackList", "Ljava/util/List;");
            m.visitVarInsn(ALOAD, 1);
            m.visitMethodInsn(INVOKEINTERFACE, "java/util/List", "add", "(Ljava/lang/Object;)Z", true);
            m.visitInsn(RETURN);
        }
        for (MethodNode method : cls.methods) {
            if (method.name.equals("addModAsResource")) {
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                int count = 0;
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == GETFIELD) {
                        count++;
                        if (count == 2) {
                            method.instructions.remove(node.getPrevious());
                            method.instructions.remove(node.getNext().getNext());
                            method.instructions.remove(node.getNext());
                            InsnList list = new InsnList();

                            method.instructions.remove(node);
                            break;
                        }
                    }
                }
                break;
            }
        }
        return write(cls);
    }
}
