package surreal.backportium.core.transformers;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.*;

import java.util.Iterator;

/**
 * Transformers for Purple Shulker Box
 **/
public class OurpleShulkerTransformer extends BasicTransformer {

    public static byte[] transformEntityShulker(byte[] basicClass) {
        ClassNode cls = read(basicClass);
        for (MethodNode method : cls.methods) {
            if (method.name.equals(getName("entityInit", "func_70088_a"))) {
                AbstractInsnNode node = method.instructions.getLast();
                while (node.getOpcode() != INVOKESTATIC) node = node.getPrevious();
                {
                    AbstractInsnNode n = node.getPrevious();
                    for (int i = 0; i < 3; i++) {
                        n = n.getPrevious();
                        method.instructions.remove(n.getNext());
                    }
                }
                method.instructions.insertBefore(node, new InsnNode(ACONST_NULL));
                method.instructions.remove(node);
            }
            else if (method.name.equals(getName("readEntityFromNBT", "func_70037_a"))) {
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                int count = 0;
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == ALOAD && ((VarInsnNode) node).var == 0) {
                        count++;
                        if (count == 4) {
                            InsnList list = new InsnList();
                            list.add(new VarInsnNode(ALOAD, 1));
                            list.add(new LdcInsnNode("Color"));
                            list.add(new MethodInsnNode(INVOKEVIRTUAL, "net/minecraft/nbt/NBTTagCompound", getName("hasKey", "func_74764_b"), "(Ljava/lang/String;)Z", false));
                            LabelNode l_con = new LabelNode();
                            list.add(new JumpInsnNode(IFEQ, l_con));
                            method.instructions.insertBefore(node, list);
                            while (node.getOpcode() != INVOKEVIRTUAL || !((MethodInsnNode) node).name.equals(getName("set", "func_187227_b"))) node = node.getNext();
                            list.add(l_con);
                            list.add(new FrameNode(F_SAME, 0, null, 0, null));
                            method.instructions.insert(node, list);
                            break;
                        }
                    }
                }
            }
            else if (method.name.equals(getName("writeEntityToNBT", "func_70014_b"))) {
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                int count = 0;
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == ALOAD && ((VarInsnNode) node).var == 1) {
                        count++;
                        if (count == 4) {
                            InsnList list = new InsnList();
                            list.add(new VarInsnNode(ALOAD, 0));
                            list.add(new FieldInsnNode(GETFIELD, cls.name, getName("dataManager", "field_70180_af"), "Lnet/minecraft/network/datasync/EntityDataManager;"));
                            list.add(new FieldInsnNode(GETSTATIC, cls.name, getName("COLOR", "field_190770_bw"), "Lnet/minecraft/network/datasync/DataParameter;"));
                            list.add(new MethodInsnNode(INVOKEVIRTUAL, "net/minecraft/network/datasync/EntityDataManager", getName("get", "func_187225_a"), "(Lnet/minecraft/network/datasync/DataParameter;)Ljava/lang/Object;", false));
                            LabelNode l_con = new LabelNode();
                            list.add(new JumpInsnNode(IFNULL, l_con));
                            method.instructions.insertBefore(node, list);
                            while (node.getOpcode() != INVOKEVIRTUAL || !((MethodInsnNode) node).name.equals(getName("setByte", "func_74774_a"))) node = node.getNext();
                            list.add(l_con);
                            list.add(new FrameNode(F_SAME, 0, null, 0, null));
                            method.instructions.insert(node, list);
                            break;
                        }
                    }
                }
            }
            else if (method.name.equals(getName("getColor", "func_190769_dn")))  {
                AbstractInsnNode node = method.instructions.getFirst();
                while (node.getOpcode() != ALOAD) node = node.getNext();
                InsnList list = new InsnList();
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new FieldInsnNode(GETFIELD, cls.name, getName("dataManager", "field_70180_af"), "Lnet/minecraft/network/datasync/EntityDataManager;"));
                list.add(new FieldInsnNode(GETSTATIC, cls.name, getName("COLOR", "field_190770_bw"), "Lnet/minecraft/network/datasync/DataParameter;"));
                list.add(new MethodInsnNode(INVOKEVIRTUAL, "net/minecraft/network/datasync/EntityDataManager", getName("get", "func_187225_a"), "(Lnet/minecraft/network/datasync/DataParameter;)Ljava/lang/Object;", false));
                LabelNode l_con = new LabelNode();
                list.add(new JumpInsnNode(IFNONNULL, l_con));
                list.add(new InsnNode(ACONST_NULL));
                list.add(new InsnNode(ARETURN));
                list.add(l_con);
                list.add(new FrameNode(F_SAME, 0, null, 0, null));
                method.instructions.insertBefore(node, list);
                break;
            }
        }
        writeClass(cls);
        return write(cls);
    }

    public static byte[] transformBlockShulkerBox(byte[] basicClass) {
        ClassNode cls = read(basicClass);
        for (MethodNode method : cls.methods) {
            if (method.name.equals(getName("getBlockByColor", "func_190952_a"))) {
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == GETSTATIC) {
                        InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 0));
                        LabelNode l_con = new LabelNode();
                        list.add(new JumpInsnNode(IFNONNULL, l_con));
                        list.add(new FieldInsnNode(GETSTATIC, "surreal/backportium/block/ModBlocks", "SHULKER_BOX", "Lnet/minecraft/block/Block;"));
                        list.add(new InsnNode(ARETURN));
                        list.add(l_con);
                        list.add(new FrameNode(F_SAME, 0, null, 0, null));
                        method.instructions.insertBefore(node, list);
                        break;
                    }
                }
            }
            else if (method.name.equals(getName("getColorFromBlock", "func_190954_c"))) {
                AbstractInsnNode node = method.instructions.getLast();
                while (node.getOpcode() != GETSTATIC) node = node.getPrevious();
                method.instructions.insertBefore(node, new InsnNode(ACONST_NULL));
                method.instructions.remove(node);
            }
        }
        writeClass(cls);
        return write(cls);
    }

    public static byte[] transformRenderShulker(byte[] basicClass) {
        ClassNode cls = read(basicClass);
        { // DEFAULT_TEXTURE
            cls.visitField(ACC_PUBLIC | ACC_STATIC | ACC_FINAL, "DEFAULT_TEXTURE", "Lnet/minecraft/util/ResourceLocation;", null, null);
        }
        for (MethodNode method : cls.methods) {
            if (method.name.equals(getName("getEntityTexture", "func_110775_a"))) { // TODO Huh?
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == ALOAD) {
                        InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 1));
                        list.add(new TypeInsnNode(CHECKCAST, "net/minecraft/entity/monster/EntityShulker"));
                        list.add(new MethodInsnNode(INVOKEVIRTUAL, "net/minecraft/entity/monster/EntityShulker", getName("getColor", "func_190769_dn"), "()Lnet/minecraft/item/EnumDyeColor;", false));
                        LabelNode l_con = new LabelNode();
                        list.add(new JumpInsnNode(IFNONNULL, l_con));
                        list.add(new FieldInsnNode(GETSTATIC, cls.name, "DEFAULT_TEXTURE", "Lnet/minecraft/util/ResourceLocation;"));
                        list.add(new InsnNode(ARETURN));
                        list.add(l_con);
                        list.add(new FrameNode(F_SAME, 0, null, 0, null));
                        method.instructions.insertBefore(node, list);
                        break;
                    }
                }
            }
            else if (method.name.equals("<clinit>")) {
                AbstractInsnNode node = method.instructions.getLast();
                while (node.getOpcode() != RETURN) node = node.getPrevious();
                InsnList list = new InsnList();
                list.add(new TypeInsnNode(NEW, "net/minecraft/util/ResourceLocation"));
                list.add(new InsnNode(DUP));
                list.add(new LdcInsnNode("backportium"));
                list.add(new LdcInsnNode("textures/entity/shulker/shulker.png"));
                list.add(new MethodInsnNode(INVOKESPECIAL, "net/minecraft/util/ResourceLocation", "<init>", "(Ljava/lang/String;Ljava/lang/String;)V", false));
                list.add(new FieldInsnNode(PUTSTATIC, cls.name, "DEFAULT_TEXTURE", "Lnet/minecraft/util/ResourceLocation;"));
                method.instructions.insertBefore(node, list);
            }
        }
        return write(cls, ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
    }

    public static byte[] transformRenderShulker$HeadLayer(byte[] basicClass) {
        ClassNode cls = read(basicClass);
        for (MethodNode method : cls.methods) {
            if (method.name.equals(getName("doRenderLayer", "func_177141_a"))) {
                AbstractInsnNode node = method.instructions.getLast();
                while (node.getOpcode() != ALOAD || ((VarInsnNode) node).var != 0) node = node.getPrevious();
                AbstractInsnNode to = node.getNext().getNext().getNext().getNext().getNext().getNext().getNext();
                System.out.println(to);
                LabelNode gotoLabel = new LabelNode();
                InsnList list = new InsnList();
                list.add(new VarInsnNode(ALOAD, 1));
                list.add(new TypeInsnNode(CHECKCAST, "net/minecraft/entity/monster/EntityShulker"));
                list.add(new MethodInsnNode(INVOKEVIRTUAL, "net/minecraft/entity/monster/EntityShulker", getName("getColor", "func_190769_dn"), "()Lnet/minecraft/item/EnumDyeColor;", false));
                LabelNode l_con = new LabelNode();
                list.add(new JumpInsnNode(IFNONNULL, l_con));
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new FieldInsnNode(GETFIELD, cls.name, "this$0", "Lnet/minecraft/client/renderer/entity/RenderShulker;"));
                list.add(new FieldInsnNode(GETSTATIC, "net/minecraft/client/renderer/entity/RenderShulker", "DEFAULT_TEXTURE", "Lnet/minecraft/util/ResourceLocation;"));
                list.add(new MethodInsnNode(INVOKEVIRTUAL, "net/minecraft/client/renderer/entity/RenderShulker", getName("bindTexture", "func_110776_a"), "(Lnet/minecraft/util/ResourceLocation;)V", false));
                list.add(new JumpInsnNode(GOTO, gotoLabel));
                list.add(l_con);
                method.instructions.insertBefore(node, list);
                list.add(gotoLabel);
                method.instructions.insert(to, list);
                break;
            }
        }
        writeClass(cls);
        return write(cls, ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
    }

    public static byte[] transformTESRShulker(byte[] basicClass) {
        ClassNode cls = read(basicClass);
        for (MethodNode method : cls.methods) {
            if (method.name.equals(getName("render", "func_192841_a"))) {
                boolean ass = false;
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == GOTO) {
                        ass = true;
                    }
                    else if (ass && node.getOpcode() == ALOAD) {
                        InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 1));
                        list.add(new MethodInsnNode(INVOKEVIRTUAL, "net/minecraft/tileentity/TileEntityShulkerBox", getName("getColor", "func_190769_dn"), "()Lnet/minecraft/item/EnumDyeColor;", false));
                        LabelNode l_con = new LabelNode();
                        list.add(new JumpInsnNode(IFNONNULL, l_con));
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new FieldInsnNode(GETSTATIC, "net/minecraft/client/renderer/entity/RenderShulker", "DEFAULT_TEXTURE", "Lnet/minecraft/util/ResourceLocation;"));
                        list.add(new MethodInsnNode(INVOKEVIRTUAL, cls.name, getName("bindTexture", "func_147499_a"), "(Lnet/minecraft/util/ResourceLocation;)V", false));
                        AbstractInsnNode n2 = node.getNext();
                        while (!(n2 instanceof LabelNode)) n2 = n2.getNext();
                        list.add(new JumpInsnNode(GOTO, (LabelNode) n2));
                        list.add(l_con);
                        list.add(new FrameNode(F_SAME, 0, null, 0, null));
                        method.instructions.insertBefore(node, list);
                        break;
                    }
                }
            }
        }
        return write(cls);
    }
}
