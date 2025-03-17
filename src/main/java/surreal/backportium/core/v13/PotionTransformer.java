package surreal.backportium.core.v13;

import org.objectweb.asm.tree.*;
import surreal.backportium.core.transformers.Transformer;

import java.util.Iterator;

public class PotionTransformer extends Transformer {

    public static byte[] transformEntityLivingBase(byte[] basicClass) {
        ClassNode cls = read(basicClass);
        for (MethodNode method : cls.methods) {
            if (method.name.equals(getName("onEntityUpdate", "func_70030_z"))) {
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == INVOKEVIRTUAL && ((MethodInsnNode) node).name.equals(getName("isPotionActive", "func_70644_a"))) {
                        InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(hook("ConduitPower$isActive", "(ZLnet/minecraft/entity/EntityLivingBase;)Z"));
                        method.instructions.insert(node, list);
                        break;
                    }
                }
            }
            else if (method.name.equals(getName("travel", "func_191986_a"))) {
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

    public static byte[] transformEntityRenderer(byte[] basicClass) {
        ClassNode cls = read(basicClass);
        for (MethodNode method : cls.methods) {
            if (method.name.equals(getName("updateLightmap", "func_78472_g"))) {
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == INVOKEVIRTUAL && ((MethodInsnNode) node).name.equals(getName("isPotionActive", "func_70644_a"))) {
                        method.instructions.insert(node, hook("ConduitPower$isActive", "(Z)Z"));
                        break;
                    }
                }
            }
            else if (method.name.equals(getName("getNightVisionBrightness", "func_180438_a"))) {
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == INVOKEVIRTUAL && ((MethodInsnNode) node).name.equals(getName("getActivePotionEffect", "func_70660_b"))) {
                        InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 1));
                        list.add(hook("ConduitPower$getPotionEffect", "(Lnet/minecraft/potion/PotionEffect;Lnet/minecraft/entity/EntityLivingBase;)Lnet/minecraft/potion/PotionEffect;"));
                        method.instructions.insert(node, list);
                    }
                    else if (node.getOpcode() == ISTORE) {
                        int var = ((VarInsnNode) node).var;
                        InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 1));
                        list.add(new VarInsnNode(ILOAD, var));
                        list.add(hook("ConduitPower$getNightVisionTime", "(Lnet/minecraft/entity/EntityLivingBase;I)I"));
                        list.add(new VarInsnNode(ISTORE, var));
                        method.instructions.insert(node, list);
                        break;
                    }
                }
            }
            else if (method.name.equals(getName("updateFogColor", "func_78466_h"))) {
                AbstractInsnNode node = method.instructions.getLast();
                while (node.getOpcode() != INVOKEVIRTUAL || !((MethodInsnNode) node).name.equals(getName("isPotionActive", "func_70644_a"))) node = node.getPrevious();
                InsnList list = new InsnList();
                list.add(new VarInsnNode(ALOAD, 3));
                list.add(new TypeInsnNode(CHECKCAST, "net/minecraft/entity/EntityLivingBase"));
                list.add(hook("ConduitPower$isActive", "(ZLnet/minecraft/entity/EntityLivingBase;)Z"));
                method.instructions.insert(node, list);
            }
            else if (method.name.equals(getName("setupFog", "func_78468_a"))) {
                AbstractInsnNode node = method.instructions.getLast();
                while (node.getOpcode() != INVOKEVIRTUAL || !((MethodInsnNode) node).name.equals(getName("isPotionActive", "func_70644_a"))) node = node.getPrevious();
                InsnList list = new InsnList();
                list.add(new VarInsnNode(ALOAD, 3));
                list.add(new TypeInsnNode(CHECKCAST, "net/minecraft/entity/EntityLivingBase"));
                list.add(hook("ConduitPower$isActive", "(ZLnet/minecraft/entity/EntityLivingBase;)Z"));
                method.instructions.insert(node, list);
                break;
            }
        }
        writeClass(cls);
        return write(cls);
    }

    public static byte[] transformBlock(byte[] basicClass) {
        ClassNode cls = read(basicClass);
        for (MethodNode method : cls.methods) {
            if (method.name.equals("getFogColor")) {
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == INVOKEVIRTUAL && ((MethodInsnNode) node).name.equals(getName("isPotionActive", "func_70644_a"))) {
                        InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 8));
                        list.add(hook("ConduitPower$isActive", "(ZLnet/minecraft/entity/EntityLivingBase;)Z"));
                        method.instructions.insert(node, list);
                        break;
                    }
                }
                break;
            }
        }
        writeClass(cls);
        return write(cls);
    }
}
