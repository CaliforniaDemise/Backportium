package surreal.backportium.core.v13;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.tree.*;
import surreal.backportium.core.transformers.Transformer;

import java.util.Iterator;

class OurpleShulkerTransformer extends Transformer {

    // Vanilla
    protected static byte[] transformBlock(byte[] basicClass) {
        ClassNode cls = read(basicClass);
        for (MethodNode method : cls.methods) {
            if (method.name.equals(getName("registerBlocks", "func_149671_p"))) {
                AbstractInsnNode node = method.instructions.getLast();
                while (node.getOpcode() != RETURN) node = node.getPrevious();
                InsnList list = new InsnList();
                list.add(new IntInsnNode(SIPUSH, 253));
                list.add(new LdcInsnNode("shulker_box"));
                list.add(new TypeInsnNode(NEW, "net/minecraft/block/BlockShulkerBox"));
                list.add(new InsnNode(DUP));
                list.add(new InsnNode(ACONST_NULL));
                list.add(new MethodInsnNode(INVOKESPECIAL, "net/minecraft/block/BlockShulkerBox", "<init>", "(Lnet/minecraft/item/EnumDyeColor;)V", false));
                list.add(new InsnNode(FCONST_2));
                list.add(new MethodInsnNode(INVOKEVIRTUAL, "net/minecraft/block/BlockShulkerBox", getName("setHardness", "func_149711_c"), "(F)Lnet/minecraft/block/Block;", false));
                list.add(new FieldInsnNode(GETSTATIC, "net/minecraft/block/SoundType", getName("STONE", "field_185851_d"), "Lnet/minecraft/block/SoundType;"));
                list.add(new MethodInsnNode(INVOKEVIRTUAL, cls.name, getName("setSoundType", "func_149672_a"), "(Lnet/minecraft/block/SoundType;)Lnet/minecraft/block/Block;", false));
                list.add(new LdcInsnNode("shulkerBox"));
                list.add(new MethodInsnNode(INVOKEVIRTUAL, cls.name, getName("setTranslationKey", "func_149663_c"), "(Ljava/lang/String;)Lnet/minecraft/block/Block;", false));
                list.add(new MethodInsnNode(INVOKESTATIC, cls.name, getName("registerBlock", "func_176219_a"), "(ILjava/lang/String;Lnet/minecraft/block/Block;)V", false));
                method.instructions.insertBefore(node, list);
            }
        }
        return write(cls);
    }

    protected static byte[] transformBlockModelShapes(byte[] basicClass) {
        ClassNode cls = read(basicClass);
        for (MethodNode method : cls.methods) {
            if (method.name.equals(getName("getTexture", "func_178122_a"))) {
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == ALOAD && ((VarInsnNode) node).var == 2) {
                        InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 2));
                        list.add(new FieldInsnNode(GETSTATIC, "net/minecraft/init/Blocks", "SHULKER_BOX", "Lnet/minecraft/block/Block;"));
                        LabelNode l_con = new LabelNode();
                        list.add(new JumpInsnNode(IF_ACMPNE, l_con));
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new FieldInsnNode(GETFIELD, cls.name, getName("modelManager", "field_178128_c"), "Lnet/minecraft/client/renderer/block/model/ModelManager;"));
                        list.add(new MethodInsnNode(INVOKEVIRTUAL, "net/minecraft/client/renderer/block/model/ModelManager", getName("getTextureMap", "func_174952_b"), "()Lnet/minecraft/client/renderer/texture/TextureMap;", false));
                        list.add(new LdcInsnNode("minecraft:blocks/shulker_top"));
                        list.add(new MethodInsnNode(INVOKEVIRTUAL, "net/minecraft/client/renderer/texture/TextureMap", getName("getAtlasSprite", "func_110572_b"), "(Ljava/lang/String;)Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;", false));
                        list.add(new InsnNode(ARETURN));
                        list.add(l_con);
                        list.add(new FrameNode(F_SAME, 0, null, 0, null));
                        method.instructions.insertBefore(node, list);
                        break;
                    }
                }
            }
            else if (method.name.equals(getName("registerAllBlocks", "func_178119_d"))) {
                AbstractInsnNode node = method.instructions.getLast();
                while (node.getOpcode() != ALOAD) node = node.getPrevious();
                InsnList list = new InsnList();
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new InsnNode(ICONST_1));
                list.add(new TypeInsnNode(ANEWARRAY, "net/minecraft/block/Block"));
                list.add(new InsnNode(DUP));
                list.add(new InsnNode(ICONST_0));
                list.add(new FieldInsnNode(GETSTATIC, "net/minecraft/init/Blocks", "SHULKER_BOX", "Lnet/minecraft/block/Block;"));
                list.add(new InsnNode(AASTORE));
                list.add(new MethodInsnNode(INVOKEVIRTUAL, cls.name, getName("registerBuiltInBlocks", "func_178123_a"), "([Lnet/minecraft/block/Block;)V", false));
                method.instructions.insertBefore(node, list);
                break;
            }
        }
        writeClass(cls);
        return write(cls);
    }

    protected static byte[] transformTileEntityItemStackRenderer(byte[] basicClass) {
        ClassNode cls = read(basicClass);
        cls.visitField(ACC_PRIVATE | ACC_FINAL, "shulkerBox", "Lnet/minecraft/tileentity/TileEntityShulkerBox;", null, null);
        for (MethodNode method : cls.methods) {
            if (method.name.equals(getName("renderByItem", "func_179022_a")) && method.desc.endsWith("F)V")) {
                boolean check = false;
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == INSTANCEOF && ((TypeInsnNode) node).desc.endsWith("BlockShulkerBox")) check = true;
                    if (check && node.getOpcode() == GETSTATIC) {
                        InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 3));
                        list.add(new MethodInsnNode(INVOKESTATIC, "net/minecraft/block/BlockShulkerBox", getName("getColorFromItem", "func_190955_b"), "(Lnet/minecraft/item/Item;)Lnet/minecraft/item/EnumDyeColor;", false));
                        LabelNode l_con = new LabelNode();
                        list.add(new JumpInsnNode(IFNONNULL, l_con));
                        list.add(new FieldInsnNode(GETSTATIC, "net/minecraft/client/renderer/tileentity/TileEntityRendererDispatcher", getName("instance", "field_147556_a"), "Lnet/minecraft/client/renderer/tileentity/TileEntityRendererDispatcher;"));
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new FieldInsnNode(GETFIELD, cls.name, "shulkerBox", "Lnet/minecraft/tileentity/TileEntityShulkerBox;"));
                        list.add(new InsnNode(DCONST_0));
                        list.add(new InsnNode(DCONST_0));
                        list.add(new InsnNode(DCONST_0));
                        list.add(new InsnNode(FCONST_0));
                        list.add(new VarInsnNode(FLOAD, 2));
                        list.add(new MethodInsnNode(INVOKEVIRTUAL, "net/minecraft/client/renderer/tileentity/TileEntityRendererDispatcher", getName("render", "func_192855_a"), "(Lnet/minecraft/tileentity/TileEntity;DDDFF)V", false));
                        list.add(new InsnNode(RETURN));
                        list.add(l_con);
                        list.add(new FrameNode(F_SAME, 0, null, 0, null));
                        method.instructions.insertBefore(node, list);
                        break;
                    }
                }
            }
            else if (method.name.equals("<init>")) {
                AbstractInsnNode node = method.instructions.getLast();
                while (node.getOpcode() != RETURN) node = node.getPrevious();
                InsnList list = new InsnList();
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new TypeInsnNode(NEW, "net/minecraft/tileentity/TileEntityShulkerBox"));
                list.add(new InsnNode(DUP));
                list.add(new MethodInsnNode(INVOKESPECIAL, "net/minecraft/tileentity/TileEntityShulkerBox", "<init>", "()V", false));
                list.add(new FieldInsnNode(PUTFIELD, cls.name, "shulkerBox", "Lnet/minecraft/tileentity/TileEntityShulkerBox;"));
                method.instructions.insertBefore(node, list);
            }
        }
        return write(cls);
    }

    protected static byte[] transformBlocks(byte[] basicClass) {
        ClassNode cls = read(basicClass);
        cls.visitField(ACC_PUBLIC | ACC_STATIC | ACC_FINAL, "SHULKER_BOX", "Lnet/minecraft/block/Block;", null, null);
        MethodNode method = clinit(cls);
        AbstractInsnNode node = method.instructions.getLast();
        while (node.getOpcode() != RETURN) node = node.getPrevious();
        InsnList list = new InsnList();
        list.add(new LdcInsnNode("shulker_box"));
        list.add(new MethodInsnNode(INVOKESTATIC, "net/minecraft/init/Blocks", getName("getRegisteredBlock", "func_180383_a"), "(Ljava/lang/String;)Lnet/minecraft/block/Block;", false));
        list.add(new FieldInsnNode(PUTSTATIC, "net/minecraft/init/Blocks", "SHULKER_BOX", "Lnet/minecraft/block/Block;"));
        method.instructions.insertBefore(node, list);
        return write(cls);
    }

    protected static byte[] transformItem(byte[] basicClass) {
        ClassNode cls = read(basicClass);
        for (MethodNode method : cls.methods) {
            if (method.name.equals(getName("registerItems", "func_150900_l"))) {
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == GETSTATIC && ((FieldInsnNode) node).name.equals(getName("WHITE_SHULKER_BOX", "field_190977_dl"))) {
                        InsnList list = new InsnList();
                        list.add(new FieldInsnNode(GETSTATIC, "net/minecraft/init/Blocks", "SHULKER_BOX", "Lnet/minecraft/block/Block;"));
                        list.add(new TypeInsnNode(NEW, "net/minecraft/item/ItemShulkerBox"));
                        list.add(new InsnNode(DUP));
                        list.add(new FieldInsnNode(GETSTATIC, "net/minecraft/init/Blocks", "SHULKER_BOX", "Lnet/minecraft/block/Block;"));
                        list.add(new MethodInsnNode(INVOKESPECIAL, "net/minecraft/item/ItemShulkerBox", "<init>", "(Lnet/minecraft/block/Block;)V", false));
                        list.add(new MethodInsnNode(INVOKESTATIC, cls.name, getName("registerItemBlock", "func_179214_a"), "(Lnet/minecraft/block/Block;Lnet/minecraft/item/Item;)V", false));
                        method.instructions.insertBefore(node, list);
                        break;
                    }
                }
                break;
            }
        }
        return write(cls);
    }

    protected static byte[] transformRenderItem(byte[] basicClass) {
        ClassNode cls = read(basicClass);
        for (MethodNode method : cls.methods) {
            if (method.name.equals(getName("registerItems", "func_175041_b"))) {
                AbstractInsnNode node = method.instructions.getLast();
                while (node.getOpcode() != RETURN) node = node.getPrevious();
                InsnList list = new InsnList();
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new FieldInsnNode(GETSTATIC, "net/minecraft/init/Blocks", "SHULKER_BOX", "Lnet/minecraft/block/Block;"));
                list.add(new LdcInsnNode("shulker_box"));
                list.add(new MethodInsnNode(INVOKEVIRTUAL, cls.name, getName("registerBlock", "func_175031_a"), "(Lnet/minecraft/block/Block;Ljava/lang/String;)V", false));
                method.instructions.insertBefore(node, list);
                break;
            }
        }
        return write(cls);
    }

    protected static byte[] transformEntityShulker(byte[] basicClass) {
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
            else if (method.name.equals(getName("getColor", "func_190769_dn"))) {
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
        return write(cls);
    }

    protected static byte[] transformBlockShulkerBox(byte[] basicClass) {
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
        return write(cls);
    }

    protected static byte[] transformRenderShulker(byte[] basicClass) {
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

    protected static byte[] transformRenderShulker$HeadLayer(byte[] basicClass) {
        ClassNode cls = read(basicClass);
        for (MethodNode method : cls.methods) {
            if (method.name.equals(getName("doRenderLayer", "func_177141_a"))) {
                AbstractInsnNode node = method.instructions.getLast();
                while (node.getOpcode() != ALOAD || ((VarInsnNode) node).var != 0) node = node.getPrevious();
                AbstractInsnNode to = node.getNext().getNext().getNext().getNext().getNext().getNext().getNext();
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
        return write(cls, ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
    }

    protected static byte[] transformTESRShulker(byte[] basicClass) {
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

    // Iron Chests
    protected static byte[] transformBlockIronShulkerBox(byte[] basicClass) {
        ClassNode cls = read(basicClass);
        { // getColorName
            MethodVisitor m = cls.visitMethod(ACC_PRIVATE | ACC_STATIC, "getColorName", "(Lnet/minecraft/item/EnumDyeColor;)Ljava/lang/String;", null, null);
            m.visitVarInsn(ALOAD, 0);
            Label l_con = new Label();
            m.visitJumpInsn(IFNULL, l_con);
            m.visitVarInsn(ALOAD, 0);
            m.visitMethodInsn(INVOKEVIRTUAL, "net/minecraft/item/EnumDyeColor", getName("getName", "func_176610_l"), "()Ljava/lang/String;", false);
            m.visitInsn(ARETURN);
            m.visitLabel(l_con);
            m.visitFrame(F_SAME, 0, null, 0, null);
            m.visitLdcInsn("");
            m.visitInsn(ARETURN);
        }
        for (MethodNode method : cls.methods) {
            if (method.name.equals("<init>")) {
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                int count = 0;
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == INVOKEVIRTUAL) {
                        count++;
                        if (count == 3) {
                            method.instructions.insertBefore(node, new MethodInsnNode(INVOKESTATIC, cls.name, "getColorName", "(Lnet/minecraft/item/EnumDyeColor;)Ljava/lang/String;", false));
                            method.instructions.remove(node);
                            break;
                        }
                    }
                }
            }
            else if (method.name.equals("getBlockByColor")) {
                AbstractInsnNode node = method.instructions.getFirst();
                while (node.getOpcode() != GETSTATIC) node = node.getNext();
                InsnList list = new InsnList();
                list.add(new VarInsnNode(ALOAD, 0));
                LabelNode l_con = new LabelNode();
                list.add(new JumpInsnNode(IFNONNULL, l_con));
                list.add(new FieldInsnNode(GETSTATIC, "cpw/mods/ironchest/common/core/IronChestBlocks", "ironShulkerBox", "Lnet/minecraft/block/Block;"));
                list.add(new InsnNode(ARETURN));
                list.add(l_con);
                list.add(new FrameNode(F_SAME, 0, null, 0, null));
                method.instructions.insertBefore(node, list);
            }
            else if (method.name.equals("getColorFromBlock")) {
                AbstractInsnNode node = method.instructions.getLast();
                while (node.getOpcode() != GETSTATIC) node = node.getPrevious();
                method.instructions.insertBefore(node, new InsnNode(ACONST_NULL));
                method.instructions.remove(node);
                break;
            }
        }
        return write(cls);
    }

    protected static byte[] transformItemIronShulkerBox(byte[] basicClass) {
        ClassNode cls = read(basicClass);
        { // getColorName
            MethodVisitor m = cls.visitMethod(ACC_PRIVATE | ACC_STATIC, "getColorName", "(Lnet/minecraft/item/EnumDyeColor;)Ljava/lang/String;", null, null);
            m.visitVarInsn(ALOAD, 0);
            Label l_con = new Label();
            m.visitJumpInsn(IFNULL, l_con);
            m.visitVarInsn(ALOAD, 0);
            m.visitMethodInsn(INVOKEVIRTUAL, "net/minecraft/item/EnumDyeColor", getName("getName", "func_176610_l"), "()Ljava/lang/String;", false);
            m.visitInsn(ARETURN);
            m.visitLabel(l_con);
            m.visitFrame(F_SAME, 0, null, 0, null);
            m.visitLdcInsn("");
            m.visitInsn(ARETURN);
        }
        for (MethodNode method : cls.methods) {
            if (method.name.equals("<init>")) {
                AbstractInsnNode node = method.instructions.getLast();
                while (node.getOpcode() != INVOKEVIRTUAL) node = node.getPrevious();
                method.instructions.insertBefore(node, new MethodInsnNode(INVOKESTATIC, cls.name, "getColorName", "(Lnet/minecraft/item/EnumDyeColor;)Ljava/lang/String;", false));
                method.instructions.remove(node);
                break;
            }
        }
        return write(cls);
    }

    protected static byte[] transformIronChestBlocks(byte[] basicClass) {
        ClassNode cls = read(basicClass);
        { // ironShulkerBox
            cls.visitField(ACC_PUBLIC | ACC_STATIC, "ironShulkerBox", "Lnet/minecraft/block/Block;", null, null);
        }
        return write(cls);
    }

    protected static byte[] transformIronChestBlocks$Registration(byte[] basicClass) {
        ClassNode cls = read(basicClass);
        for (MethodNode method : cls.methods) {
            if (method.name.equals("registerBlocks")) {
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == INVOKEINTERFACE) {
                        InsnList list = new InsnList();
                        list.add(new TypeInsnNode(NEW, "cpw/mods/ironchest/common/blocks/shulker/BlockIronShulkerBox"));
                        list.add(new InsnNode(DUP));
                        list.add(new InsnNode(ACONST_NULL));
                        list.add(new LdcInsnNode("ironchest:iron_shulker_box"));
                        list.add(new MethodInsnNode(INVOKESPECIAL, "cpw/mods/ironchest/common/blocks/shulker/BlockIronShulkerBox", "<init>", "(Lnet/minecraft/item/EnumDyeColor;Ljava/lang/String;)V", false));
                        list.add(new FieldInsnNode(PUTSTATIC, "cpw/mods/ironchest/common/core/IronChestBlocks", "ironShulkerBox", "Lnet/minecraft/block/Block;"));
                        list.add(new VarInsnNode(ALOAD, 1));
                        list.add(new FieldInsnNode(GETSTATIC, "cpw/mods/ironchest/common/core/IronChestBlocks", "ironShulkerBox", "Lnet/minecraft/block/Block;"));
                        list.add(new MethodInsnNode(INVOKEINTERFACE, "net/minecraftforge/registries/IForgeRegistry", "register", "(Lnet/minecraftforge/registries/IForgeRegistryEntry;)V", true));
                        method.instructions.insert(node, list);
                        break;
                    }
                }
            }
            else if (method.name.equals("registerItems")) {
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == INVOKEINTERFACE) {
                        InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 1));
                        list.add(new TypeInsnNode(NEW, "cpw/mods/ironchest/common/items/shulker/ItemIronShulkerBox"));
                        list.add(new InsnNode(DUP));
                        list.add(new LdcInsnNode("ironchest:iron_shulker_box"));
                        list.add(new MethodInsnNode(INVOKESTATIC, "net/minecraft/block/Block", getName("getBlockFromName", "func_149684_b"), "(Ljava/lang/String;)Lnet/minecraft/block/Block;", false));
                        list.add(new InsnNode(ACONST_NULL));
                        list.add(new MethodInsnNode(INVOKESPECIAL, "cpw/mods/ironchest/common/items/shulker/ItemIronShulkerBox", "<init>", "(Lnet/minecraft/block/Block;Lnet/minecraft/item/EnumDyeColor;)V", false));
                        list.add(new MethodInsnNode(INVOKEINTERFACE, "net/minecraftforge/registries/IForgeRegistry", "register", "(Lnet/minecraftforge/registries/IForgeRegistryEntry;)V", true));
                        method.instructions.insert(node, list);
                        break;
                    }
                }
            }
        }
        return write(cls);
    }

    protected static byte[] transformTileEntityIronShulkerBoxRenderer(byte[] basicClass) {
        ClassNode cls = read(basicClass);
        { // getColorName
            MethodVisitor m = cls.visitMethod(ACC_PRIVATE | ACC_STATIC, "getColorName", "(Lnet/minecraft/item/EnumDyeColor;)Ljava/lang/String;", null, null);
            m.visitVarInsn(ALOAD, 0);
            Label l_con = new Label();
            m.visitJumpInsn(IFNULL, l_con);
            m.visitVarInsn(ALOAD, 0);
            m.visitMethodInsn(INVOKEVIRTUAL, "net/minecraft/item/EnumDyeColor", getName("getName", "func_176610_l"), "()Ljava/lang/String;", false);
            m.visitInsn(ARETURN);
            m.visitLabel(l_con);
            m.visitFrame(F_SAME, 0, null, 0, null);
            m.visitLdcInsn("default");
            m.visitInsn(ARETURN);
        }
        for (MethodNode method : cls.methods) {
            if (method.name.equals(getName("render", "func_192841_a"))) {
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                boolean inNew = false;
                int count = 0;
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == NEW) inNew = true;
                    else if (inNew && node.getOpcode() == ALOAD) {
                        method.instructions.remove(node.getNext().getNext());
                        method.instructions.insert(node.getNext(), new MethodInsnNode(INVOKESTATIC, cls.name, "getColorName", "(Lnet/minecraft/item/EnumDyeColor;)Ljava/lang/String;", false));
                        count++;
                        if (count == 2) break;
                    }
                }
                break;
            }
        }
        return write(cls);
    }

    protected static byte[] transformItemShulkerBoxChanger(byte[] basicClass) {
        ClassNode cls = read(basicClass);
        for (MethodNode method : cls.methods) {
            if (method.name.equals("getColorFromTileEntity")) {
                AbstractInsnNode node = method.instructions.getLast();
                while (node.getOpcode() != GETSTATIC) node = node.getPrevious();
                method.instructions.insertBefore(node, new InsnNode(ACONST_NULL));
                method.instructions.remove(node);
            }
            else if (method.name.equals("onItemUseFirst")) {
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == GETSTATIC && ((FieldInsnNode) node).owner.endsWith("EnumDyeColor")) {
                        method.instructions.insertBefore(node, new InsnNode(ACONST_NULL));
                        method.instructions.remove(node);
                    } else if (node.getOpcode() == ASTORE && ((VarInsnNode) node).var == 17) {
                        InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 14));
                        LabelNode l_con = new LabelNode();
                        list.add(new JumpInsnNode(IFNONNULL, l_con));
                        list.add(new VarInsnNode(ALOAD, 11));
                        list.add(new TypeInsnNode(CHECKCAST, "cpw/mods/ironchest/common/tileentity/shulker/TileEntityIronShulkerBox"));
                        list.add(new VarInsnNode(ALOAD, 12));
                        list.add(new MethodInsnNode(INVOKEVIRTUAL, "cpw/mods/ironchest/common/tileentity/shulker/TileEntityIronShulkerBox", "setContents", "(Lnet/minecraft/util/NonNullList;)V", false));
                        list.add(new VarInsnNode(ALOAD, 11));
                        list.add(new TypeInsnNode(CHECKCAST, "cpw/mods/ironchest/common/tileentity/shulker/TileEntityIronShulkerBox"));
                        list.add(new VarInsnNode(ALOAD, 13));
                        list.add(new MethodInsnNode(INVOKEVIRTUAL, "cpw/mods/ironchest/common/tileentity/shulker/TileEntityIronShulkerBox", "setFacing", "(Lnet/minecraft/util/EnumFacing;)V", false));
                        list.add(new VarInsnNode(ALOAD, 2));
                        list.add(new VarInsnNode(ALOAD, 3));
                        list.add(new VarInsnNode(ALOAD, 11));
                        list.add(new MethodInsnNode(INVOKEVIRTUAL, "net/minecraft/world/World", getName("setTileEntity", "func_175690_a"), "(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/tileentity/TileEntity;)V", false));
                        list.add(new VarInsnNode(ALOAD, 2));
                        list.add(new VarInsnNode(ALOAD, 3));
                        list.add(new FieldInsnNode(GETSTATIC, "net/minecraft/init/Blocks", "SHULKER_BOX", "Lnet/minecraft/block/Block;"));
                        list.add(l_con);
                        method.instructions.insert(node, list);
                        break;
                    }
                }
            }
        }
        return write(cls);
    }
}
