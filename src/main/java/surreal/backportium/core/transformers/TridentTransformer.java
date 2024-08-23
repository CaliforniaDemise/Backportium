package surreal.backportium.core.transformers;

import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.tree.*;

import java.util.Iterator;
import java.util.Objects;

public class TridentTransformer extends BasicTransformer {

    // Yes Forge, I will cancel RenderPlayerEvent and re-render the entire player model just to move players arm by 180 degrees
    public static byte[] transformModelBiped(byte[] basicClass) {
        ClassNode cls = read(basicClass);
        for (MethodNode method : cls.methods) {
            if (method.name.equals(getName("setRotationAngles", "func_78087_a"))) {
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == RETURN) {
                        String modelBiped = "net/minecraft/client/model/ModelBiped";

                        InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 0));

                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new VarInsnNode(ALOAD, 7));
                        list.add(new MethodInsnNode(INVOKEVIRTUAL, modelBiped, getName("getMainHand", "func_187072_a"), "(Lnet/minecraft/entity/Entity;)Lnet/minecraft/util/EnumHandSide;", false));

                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new VarInsnNode(ALOAD, 7));
                        list.add(new MethodInsnNode(INVOKEVIRTUAL, modelBiped, getName("getMainHand", "func_187072_a"), "(Lnet/minecraft/entity/Entity;)Lnet/minecraft/util/EnumHandSide;", false));
                        list.add(new MethodInsnNode(INVOKEVIRTUAL, modelBiped, getName("getArmForSide", "func_187074_a"), "(Lnet/minecraft/util/EnumHandSide;)Lnet/minecraft/client/model/ModelRenderer;", false));

                        list.add(new VarInsnNode(FLOAD, 1));
                        list.add(new VarInsnNode(FLOAD, 2));
                        list.add(new VarInsnNode(FLOAD, 3));
                        list.add(new VarInsnNode(FLOAD, 4));
                        list.add(new VarInsnNode(FLOAD, 5));
                        list.add(new VarInsnNode(FLOAD, 6));
                        list.add(new VarInsnNode(ALOAD, 7));
                        list.add(hook("ModelBiped$setRotationAngles", "(Lnet/minecraft/client/model/ModelBiped;Lnet/minecraft/util/EnumHandSide;Lnet/minecraft/client/model/ModelRenderer;FFFFFFLnet/minecraft/entity/Entity;)V"));

                        method.instructions.insertBefore(node, list);
                        break;
                    }
                }
                break;
            }
        }

        return write(cls);
    }

    /* The transformers here *most* definitely isn't needed.
     * I guess I can reimplement it instead of extending arrow entity,
     * but I'm too lazy, so instead I will instead pretend extending is justified.
     **/
    public static byte[] transformEntityArrow(byte[] basicClass) {
        ClassNode cls = read(basicClass);
        String hitSound = getName("ENTITY_ARROW_HIT", "field_187731_t");
        for (MethodNode method : cls.methods) {
            if (method.name.equals(getName("onUpdate", "func_70071_h_"))) {
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node instanceof LdcInsnNode && Objects.equals(((LdcInsnNode) node).cst, new Float("0.6"))) {
                        node = node.getNext();
                        method.instructions.remove(node.getPrevious());
                        InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new MethodInsnNode(INVOKEVIRTUAL, cls.name, "getSpeedChangeInWater", "()F", false));
                        method.instructions.insertBefore(node, list);
                        break;
                    }
                }
            }
            else if (method.name.equals(getName("onHit", "func_184549_a"))) {
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                int i = 0;
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == I2F) {
                        method.instructions.insertBefore(node.getPrevious(), new VarInsnNode(ALOAD, 0));
                        method.instructions.insertBefore(node.getPrevious(), new VarInsnNode(ALOAD, 2));
                        method.instructions.insert(node, new MethodInsnNode(INVOKEVIRTUAL, cls.name, "getHitDamage", "(Lnet/minecraft/entity/Entity;F)F", false));
                    }
                    else if (node.getOpcode() == GETSTATIC && ((FieldInsnNode) node).name.equals(hitSound)) {
                        node = node.getNext();
                        method.instructions.remove(node.getPrevious());
                        InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 0));
                        String name = i == 1 ? "getBlockHitSound" : "getEntityHitSound";
                        list.add(new MethodInsnNode(INVOKEVIRTUAL, cls.name, name, "()Lnet/minecraft/util/SoundEvent;", false));
                        method.instructions.insertBefore(node, list);
                        i++;
                        if (i == 2) {
                            break;
                        }
                    }
                    else if (node.getOpcode() == INSTANCEOF && ((TypeInsnNode) node).desc.equals("net/minecraft/entity/monster/EntityEnderman")) {
                        method.instructions.insertBefore(node.getPrevious(), new VarInsnNode(ALOAD, 0));
                        node = node.getNext();
                        method.instructions.remove(node.getPrevious());
                        ((JumpInsnNode) node).setOpcode(IFEQ);
                        method.instructions.insertBefore(node, new MethodInsnNode(INVOKEVIRTUAL, cls.name, "shouldDieAfterHit", "(Lnet/minecraft/entity/Entity;)Z", false));
                    }
                }
                break;
            }
        }
        { // shouldDieAfterHit
            MethodVisitor m_shouldDie = cls.visitMethod(ACC_PROTECTED, "shouldDieAfterHit", "(Lnet/minecraft/entity/Entity;)Z", null, null);
            Label l_con = new Label();
            m_shouldDie.visitVarInsn(ALOAD, 1);
            m_shouldDie.visitTypeInsn(INSTANCEOF, "net/minecraft/entity/monster/EntityEnderman");
            m_shouldDie.visitJumpInsn(IFEQ, l_con);
            m_shouldDie.visitLabel(new Label());
            m_shouldDie.visitInsn(ICONST_0);
            m_shouldDie.visitInsn(IRETURN);
            m_shouldDie.visitLabel(l_con);
            m_shouldDie.visitFrame(F_SAME, 0, null, 0, null);
            m_shouldDie.visitInsn(ICONST_1);
            m_shouldDie.visitInsn(IRETURN);
        }
        { // getSpeedChangeInWater
            MethodVisitor m_speedChange = cls.visitMethod(ACC_PROTECTED, "getSpeedChangeInWater", "()F", null, null);
            m_speedChange.visitLdcInsn(0.6F);
            m_speedChange.visitInsn(FRETURN);
        }
        { // getEntityHitSound
            MethodVisitor m_hitSound = cls.visitMethod(ACC_PROTECTED, "getEntityHitSound", "()Lnet/minecraft/util/SoundEvent;", null, null);
            m_hitSound.visitFieldInsn(GETSTATIC, "net/minecraft/init/SoundEvents", hitSound, "Lnet/minecraft/util/SoundEvent;");
            m_hitSound.visitInsn(ARETURN);
        }
        { // getBlockHitSound
            MethodVisitor m_hitSound = cls.visitMethod(ACC_PROTECTED, "getBlockHitSound", "()Lnet/minecraft/util/SoundEvent;", null, null);
            m_hitSound.visitFieldInsn(GETSTATIC, "net/minecraft/init/SoundEvents", hitSound, "Lnet/minecraft/util/SoundEvent;");
            m_hitSound.visitInsn(ARETURN);
        }
        { // getHitDamage
            MethodVisitor m_hitDamage = cls.visitMethod(ACC_PROTECTED, "getHitDamage", "(Lnet/minecraft/entity/Entity;F)F", null, null);
            m_hitDamage.visitVarInsn(FLOAD, 2);
            m_hitDamage.visitInsn(FRETURN);
        }
        return write(cls);
    }

    // Trident damage source handling
    public static byte[] transformDamageSource(byte[] basicClass) {
        ClassNode cls = read(basicClass);
        for (MethodNode method : cls.methods) {
            if (method.name.equals(getName("causeArrowDamage", "func_76353_a"))) {
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();

                AbstractInsnNode node = iterator.next();
                InsnList list = new InsnList();

                // Create the string
                list.add(new InsnNode(ACONST_NULL));
                list.add(new VarInsnNode(ASTORE, 2));

                // Check if entity is trident
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new TypeInsnNode(INSTANCEOF, "surreal/backportium/entity/v1_13/EntityTrident"));
                LabelNode l_elsecon = new LabelNode();
                list.add(new JumpInsnNode(IFEQ, l_elsecon));
                list.add(new LabelNode());
                list.add(new LdcInsnNode("trident"));
                list.add(new VarInsnNode(ASTORE, 2));
                LabelNode l_con = new LabelNode();
                list.add(new JumpInsnNode(GOTO, l_con));
                list.add(l_elsecon);
                list.add(new FrameNode(F_SAME, 0, null, 0, null));
                list.add(new LdcInsnNode("arrow"));
                list.add(new VarInsnNode(ASTORE, 2));
                list.add(l_con);
                list.add(new FrameNode(F_APPEND, 1, new Object[] { "java/lang/String" }, 0, null));
                method.instructions.insertBefore(node, list);

                while (iterator.hasNext()) {
                    node = iterator.next();
                    if (node instanceof LdcInsnNode && ((LdcInsnNode) node).cst.equals("arrow")) {
                        method.instructions.insert(node, new VarInsnNode(ALOAD, 2));
                        iterator.remove();
                        break;
                    }
                }

                break;
            }
        }
        return write(cls);
    }

    // Add values to living entity to track if it's in riptide effect or not
    public static byte[] transformEntityLivingBase(byte[] basicClass) {
        ClassNode cls = read(basicClass);
        { // If entity is in riptide
            FieldVisitor visitor = cls.visitField(ACC_PROTECTED, "inRiptide", "Z", null, false);
            visitor.visitEnd();
        }
        { // Counts down to zero and sets inRiptide to false on zero. Gets set to 20 when entity gets in riptide
            FieldVisitor visitor = cls.visitField(ACC_PROTECTED, "riptideTime", "I", null, 0);
            visitor.visitEnd();
        }
        for (MethodNode method : cls.methods) {
            if (method.name.equals(getName("onUpdate", ""))) {
                AbstractInsnNode node = method.instructions.getLast();
                while (node.getOpcode() != RETURN) {
                    node = node.getPrevious();
                }
                InsnList list = new InsnList();
                LabelNode l_elsecon = new LabelNode();
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new FieldInsnNode(GETFIELD, cls.name, "riptideTime", "I"));
                list.add(new InsnNode(ICONST_0));
                list.add(new JumpInsnNode(IF_ICMPNE, l_elsecon));
                list.add(new LabelNode());
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new InsnNode(ICONST_0));
                list.add(new FieldInsnNode(PUTFIELD, cls.name, "inRiptide", "Z"));
                LabelNode l_con = new LabelNode();
                list.add(new JumpInsnNode(GOTO, l_con));
                list.add(l_elsecon);
                list.add(new FrameNode(F_SAME, 0, null, 0, null));
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new FieldInsnNode(GETFIELD, cls.name, "riptideTime", "I"));
                list.add(new InsnNode(ICONST_1));
                list.add(new InsnNode(ISUB));
                list.add(new FieldInsnNode(PUTFIELD, cls.name, "riptideTime", "I"));
                list.add(l_con);
                list.add(new FrameNode(F_SAME, 0, null, 0, null));
                method.instructions.insertBefore(node, list);
                break;
            }
        }
        { // handleRiptide. I guess I should handle sound here too?
            MethodVisitor m_riptide = cls.visitMethod(ACC_PUBLIC, "handleRiptide", "(Lnet/minecraft/item/ItemStack;)V", null, null);
            m_riptide.visitFieldInsn(GETSTATIC, "surreal/backportium/enchantment/ModEnchantments", "RIPTIDE", "Lnet/minecraft/enchantment/Enchantment;");
            m_riptide.visitVarInsn(ALOAD, 1);
            m_riptide.visitMethodInsn(INVOKESTATIC, "net/minecraft/enchantment/EnchantmentHelper", getName("getEnchantmentLevel", "func_77506_a"), "(Lnet/minecraft/enchantment/Enchantment;Lnet/minecraft/item/ItemStack;)I", false);
            m_riptide.visitVarInsn(ISTORE, 2);

            m_riptide.visitVarInsn(ALOAD, 0);
            m_riptide.visitMethodInsn(INVOKEVIRTUAL, cls.name, getName("getLookVec", ""), "()Lnet/minecraft/util/math/Vec3d;", false);
            m_riptide.visitVarInsn(ASTORE, 3);

            m_riptide.visitInsn(DCONST_1);
            m_riptide.visitInsn(DCONST_1);
            m_riptide.visitVarInsn(ILOAD, 2);
            m_riptide.visitInsn(I2D);
            m_riptide.visitInsn(DMUL);
            m_riptide.visitInsn(DADD);
            m_riptide.visitVarInsn(DSTORE, 4);

            String motionX = getName("motionX", "field_70159_w");
            m_riptide.visitVarInsn(ALOAD, 0);
            m_riptide.visitInsn(DUP);
            m_riptide.visitFieldInsn(GETFIELD, cls.name, motionX, "D");
            m_riptide.visitVarInsn(DLOAD, 4);
            m_riptide.visitVarInsn(ALOAD, 3);
            m_riptide.visitFieldInsn(GETFIELD, "net/minecraft/util/math/Vec3d", getName("x", "field_72450_a"), "D");
            m_riptide.visitInsn(DMUL);
            m_riptide.visitInsn(DADD);
            m_riptide.visitFieldInsn(PUTFIELD, cls.name, motionX, "D");

            String motionY = getName("motionY", "field_70181_x");
            m_riptide.visitVarInsn(ALOAD, 0);
            m_riptide.visitInsn(DUP);
            m_riptide.visitFieldInsn(GETFIELD, cls.name, motionY, "D");
            m_riptide.visitVarInsn(DLOAD, 4);
            m_riptide.visitVarInsn(ALOAD, 3);
            m_riptide.visitFieldInsn(GETFIELD, "net/minecraft/util/math/Vec3d", getName("y", "field_72448_b"), "D");
            m_riptide.visitInsn(DMUL);
            m_riptide.visitInsn(DADD);
            m_riptide.visitFieldInsn(PUTFIELD, cls.name, motionY, "D");

            String motionZ = getName("motionZ", "field_70179_y");
            m_riptide.visitVarInsn(ALOAD, 0);
            m_riptide.visitInsn(DUP);
            m_riptide.visitFieldInsn(GETFIELD, cls.name, motionZ, "D");
            m_riptide.visitVarInsn(DLOAD, 4);
            m_riptide.visitVarInsn(ALOAD, 3);
            m_riptide.visitFieldInsn(GETFIELD, "net/minecraft/util/math/Vec3d", getName("z", "field_72449_c"), "D");
            m_riptide.visitInsn(DMUL);
            m_riptide.visitInsn(DADD);
            m_riptide.visitFieldInsn(PUTFIELD, cls.name, motionZ, "D");

            m_riptide.visitVarInsn(ALOAD, 0);
            m_riptide.visitInsn(ICONST_1);
            m_riptide.visitFieldInsn(PUTFIELD, cls.name, "inRiptide", "Z");

            m_riptide.visitVarInsn(ALOAD, 0);
            m_riptide.visitIntInsn(BIPUSH, 20);
            m_riptide.visitFieldInsn(PUTFIELD, cls.name, "riptideTime", "I");

            m_riptide.visitInsn(RETURN);
        }
        {
            MethodVisitor m_isRiptide = cls.visitMethod(ACC_PUBLIC, "isInRiptide", "()Z", null, null);
            m_isRiptide.visitVarInsn(ALOAD, 0);
            m_isRiptide.visitFieldInsn(GETFIELD, cls.name, "inRiptide", "Z");
            m_isRiptide.visitInsn(IRETURN);
        }
        {
            MethodVisitor m_getRiptideTime = cls.visitMethod(ACC_PUBLIC, "getRiptideTickLeft", "()I", null, null);
            m_getRiptideTime.visitVarInsn(ALOAD, 0);
            m_getRiptideTime.visitFieldInsn(GETFIELD, cls.name, "riptideTime", "I");
            m_getRiptideTime.visitInsn(IRETURN);
        }
        return write(cls);
    }

    // Add riptide effect to entities
    public static byte[] transformRenderLivingBase(byte[] basicClass) {
        String livingBase = "net/minecraft/entity/EntityLivingBase";
        ClassNode cls = read(basicClass);
        for (MethodNode method : cls.methods) {
            if (method.name.equals(getName("applyRotations", "func_77043_a"))) {
                AbstractInsnNode node = method.instructions.getLast();
                while (node.getOpcode() != RETURN) {
                    node = node.getPrevious();
                }
                InsnList list = new InsnList();
                list.add(new VarInsnNode(ALOAD, 1));
                list.add(new VarInsnNode(ALOAD, 1));
                list.add(new MethodInsnNode(INVOKEVIRTUAL, livingBase, "isInRiptide", "()Z", false));
                list.add(new VarInsnNode(ALOAD, 1));
                list.add(new MethodInsnNode(INVOKEVIRTUAL, livingBase, "getRiptideTickLeft", "()I", false));
                list.add(new VarInsnNode(FLOAD, 4));
                list.add(hook("RenderLivingBase$applyRotations", "(Lnet/minecraft/entity/EntityLivingBase;ZIF)V"));
                method.instructions.insertBefore(node, list);
                break;
            }
        }

        return write(cls);
    }

    // Fix rotation when using Trident with Elytra, maybe make Elytra rendering happen for all living entities
    public static byte[] transformRenderPlayer(byte[] basicClass) {
        String player = "net/minecraft/entity/player/EntityPlayer";
        ClassNode cls = read(basicClass);
        for (MethodNode method : cls.methods) {
            if (method.name.equals(getName("applyRotations", "func_77043_a"))) {
                AbstractInsnNode node = method.instructions.getLast();
                while (node.getOpcode() != RETURN) {
                    node = node.getPrevious();
                }
                InsnList list = new InsnList();
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new VarInsnNode(ALOAD, 1));
                list.add(new VarInsnNode(ALOAD, 1));
                list.add(new MethodInsnNode(INVOKEVIRTUAL, player, "isInRiptide", "()Z", false));
                list.add(new VarInsnNode(ALOAD, 1));
                list.add(new MethodInsnNode(INVOKEVIRTUAL, player, "getRiptideTickLeft", "()I", false));
                list.add(new VarInsnNode(FLOAD, 4));
                list.add(hook("RenderPlayer$fixElytraRotations", "(Lnet/minecraft/entity/player/EntityPlayer;ZIF)V"));
                method.instructions.insertBefore(node, list);
                break;
            }
            else if (method.name.equals("<init>")) {
                String riptideModel = "surreal/backportium/client/model/entity/ModelRiptide";
                String riptideLayer = "surreal/backportium/client/renderer/entity/layer/LayerRiptide";
                AbstractInsnNode node = method.instructions.getLast();
                while (node.getOpcode() != RETURN) {
                    node = node.getPrevious();
                }
                InsnList list = new InsnList();
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new TypeInsnNode(NEW, riptideLayer));
                list.add(new InsnNode(DUP));
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new TypeInsnNode(NEW, riptideModel));
                list.add(new InsnNode(DUP));
                list.add(new MethodInsnNode(INVOKESPECIAL, riptideModel, "<init>", "()V", false));
                list.add(new MethodInsnNode(INVOKESPECIAL, riptideLayer, "<init>", "(Lnet/minecraft/client/renderer/entity/Render;Lnet/minecraft/client/model/ModelBase;)V", false));
                list.add(new MethodInsnNode(INVOKEVIRTUAL, cls.name, getName("addLayer", "func_177094_a"), "(Lnet/minecraft/client/renderer/entity/layers/LayerRenderer;)Z", false));
                method.instructions.insertBefore(node, list);
            }
        }
        return write(cls);
    }

    // TODO Size update doesn't work with Aqua Acrobatics
    public static byte[] transformEntityPlayer(byte[] basicClass) {
        ClassNode cls = read(basicClass);
        for (MethodNode method : cls.methods) {
            if (method.name.equals(getName("updateSize", ""))) {
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();

                AbstractInsnNode node = null;

                while (iterator.hasNext()) {
                    AbstractInsnNode next = iterator.next();

                    if (node == null && next.getOpcode() == ALOAD) {
                        node = next;
                    }

                    if (next.getOpcode() == GOTO) {
                        LabelNode l_goto = ((JumpInsnNode) next).label;
                        LabelNode l_con = new LabelNode();
                        InsnList list = new InsnList();

                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new MethodInsnNode(INVOKEVIRTUAL, cls.name, "isInRiptide", "()Z", false));
                        list.add(new JumpInsnNode(IFEQ, l_con));
                        list.add(new LabelNode());
                        list.add(new LdcInsnNode(0.6F));
                        list.add(new VarInsnNode(FSTORE, 1));
                        list.add(new LdcInsnNode(0.6F));
                        list.add(new VarInsnNode(FSTORE, 2));
                        list.add(new JumpInsnNode(GOTO, l_goto));
                        list.add(l_con);
                        list.add(new FrameNode(F_SAME, 0, null, 0, null));
                        method.instructions.insertBefore(node, list);
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
