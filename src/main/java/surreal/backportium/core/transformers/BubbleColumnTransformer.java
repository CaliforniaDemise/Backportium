package surreal.backportium.core.transformers;

import org.objectweb.asm.tree.*;

import java.util.Iterator;

public class BubbleColumnTransformer extends BasicTransformer {

    /**
     * Make EntityThrowable raytrace liquid blocks.
     **/
    public static byte[] transformEntityThrowable(byte[] basicClass) {
        ClassNode cls = read(basicClass);
        for (MethodNode method : cls.methods) {
            if (method.name.equals(getName("onUpdate", "func_70071_h_"))) {
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == INVOKEVIRTUAL && ((MethodInsnNode) node).name.equals(getName("rayTraceBlocks", "func_147447_a"))) {
                        InsnList list = new InsnList();
                        list.add(new InsnNode(ICONST_0));
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new MethodInsnNode(INVOKESTATIC, "net/minecraft/entity/EntityList", getName("getKey", "func_191301_a"), "(Lnet/minecraft/entity/Entity;)Lnet/minecraft/util/ResourceLocation;", false));
                        list.add(new MethodInsnNode(INVOKEVIRTUAL, "net/minecraft/util/ResourceLocation", getName("getNamespace", "func_110624_b"), "()Ljava/lang/String;", false));
                        list.add(new LdcInsnNode("minecraft"));
                        list.add(new MethodInsnNode(INVOKEVIRTUAL, "java/lang/Object", "equals", "(Ljava/lang/Object;)Z", false));
                        list.add(new InsnNode(ICONST_0));
                        method.instructions.insertBefore(node, list);
                        ((MethodInsnNode) node).desc = "(Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Vec3d;ZZZ)Lnet/minecraft/util/math/RayTraceResult;";
                        break;
                    }
                }
                break;
            }
        }
        return write(cls);
    }
}
