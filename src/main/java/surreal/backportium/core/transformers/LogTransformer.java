package surreal.backportium.core.transformers;

import it.unimi.dsi.fastutil.ints.IntList;
import org.apache.commons.io.IOUtils;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.tree.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static surreal.backportium.core.BPPlugin.DEBARK;
import static surreal.backportium.core.BPPlugin.DEBARKED_LOGS;
import static surreal.backportium.core.BPPlugin.FUTUREMC;

/**
 * For transforming Log blocks to have stripped and bark variants and also a way to reach that said variants.
 **/
public class LogTransformer extends BasicTransformer {

    private static final Set<String> DO_NOT_TRANSFORM = new HashSet<>();

    static {
        DO_NOT_TRANSFORM.add("com.bewitchment.common.block.util.ModBlockLog");
        DO_NOT_TRANSFORM.add("org.cyclops.cyclopscore.config.configurable.ConfigurableBlockLog");
    }

    public static boolean checkLogs(byte[] cls, String transformedName, String[] superName) {
        if (transformedName.startsWith("backportium.logs")) return false;

        // Mods that already have stripped / debarked logs
        if (transformedName.startsWith("com.sirsquidly.oe")) return false;
        if (transformedName.startsWith("com.globbypotato.rockhounding")) return false;

        // For mods like Debark, FutureMC and Debarked Logs
        if (!shouldRegisterVanillaLogs()) {
            if (transformedName.startsWith("net.minecraft.")) return false;
            if (DEBARK) {
                if (transformedName.startsWith("pl.asie.debark")) return false;
                if (transformedName.startsWith("com.gildedgames.the_aether.")) return false;
                if (transformedName.startsWith("com.teammetallurgy.atum.")) return false;
                if (transformedName.startsWith("com.bewitchment.")) return false;
                if (transformedName.startsWith("binnie.extratrees.")) return false;
                if (transformedName.startsWith("biomesoplenty.")) return false;
                if (transformedName.startsWith("jaredbgreat.climaticbiome.")) return false;
                if (transformedName.startsWith("forestry.")) return false;
                if (transformedName.startsWith("com.progwml6.natura.")) return false;
                if (transformedName.startsWith("vibrantjourneys.")) return false;
                if (transformedName.startsWith("rustic.")) return false;
                if (transformedName.startsWith("net.dries007.tfc.")) return false;
                if (transformedName.startsWith("prospector.traverse.")) return false;
                if (transformedName.startsWith("twilightforest.")) return false;
            }
            if (DEBARKED_LOGS && transformedName.startsWith("debarking.")) return false;
            if (FUTUREMC && transformedName.startsWith("thedarkcolour.futuremc.")) return false;
        }

        if (DO_NOT_TRANSFORM.contains(transformedName)) return false;
        int poolCount = ((cls[9] & 0xFF) | (cls[8] & 0xFF) << 8) - 1;
        int[] constants = new int[poolCount]; // Byte location of constants
        int index = 10;
        for (int i = 0; i < poolCount; i++) {
            constants[i] = index;
            if (cls[index] == DOUBLE || cls[index] == LONG) {
                i++;
                constants[i] = index;
            }
            index = nextConstant(cls, index);
        }
        int accessFlags = ((cls[index + 1] & 0xFF) | (cls[index] & 0xFF) << 8);
        if ((accessFlags & 0x0200) == 0x0200) return false; // interface check
        if ((accessFlags & 0x0400) == 0x0400) return false; // interface check
        int superConstant = ((cls[index + 5] & 0xFF) | (cls[index + 4] & 0xFF) << 8); // superclass' position on constants array
        superConstant = constants[superConstant - 1]; // superclass' position on cls array
        superConstant = ((cls[superConstant + 2] & 0xFF) | (cls[superConstant + 1] & 0xFF) << 8); // superclass' utf8's position
        String str = fromUtf8Const(cls, constants[superConstant - 1]);
        for (String s : superName) {
            if (str.equals(s)) return true;
        }
        if (str.equals("java/lang/Object")) return false;
        try {
            InputStream stream = LogTransformer.class.getClassLoader().getResourceAsStream(str + ".class");
            if (stream == null) return false;
            byte[] bytes = IOUtils.toByteArray(stream);
            stream.close();
            return checkLogs(bytes, transformedName, superName);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Non-abstract BlockLog extending aberrations
    public static byte[] transformBlockLogEx(byte[] basicClass) {
        ClassNode cls = read(basicClass);
        cls.interfaces = new ArrayList<>(cls.interfaces);
        cls.interfaces.add("surreal/backportium/api/block/StrippableLog");
        {
            cls.visitField(ACC_PUBLIC, "stripped", "Lnet/minecraft/block/Block;", null, null);
            cls.visitField(ACC_PUBLIC, "strippedBark", "Lnet/minecraft/block/Block;", null, null);
        }
        { // onStrip
            MethodVisitor m = cls.visitMethod(ACC_PUBLIC, "onStrip", "(Lnet/minecraft/world/World;Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/util/EnumHand;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/util/EnumFacing;Lnet/minecraft/util/math/Vec3d;)Z", null, null);
            m.visitVarInsn(ALOAD, 1);
            m.visitFieldInsn(GETFIELD, "net/minecraft/world/World", getName("isRemote", "field_72995_K"), "Z");
            Label l_con = new Label();
            m.visitJumpInsn(IFNE, l_con);
            m.visitVarInsn(ALOAD, 0);
            m.visitFieldInsn(GETFIELD, cls.name, "stripped", "Lnet/minecraft/block/Block;");
            m.visitJumpInsn(IFNONNULL, l_con);
            m.visitVarInsn(ALOAD, 1);
            m.visitVarInsn(ALOAD, 4);
            m.visitVarInsn(ALOAD, 0);
            m.visitFieldInsn(GETFIELD, cls.name, "stripped", "Lnet/minecraft/block/Block;");
            m.visitVarInsn(ALOAD, 0);
            m.visitVarInsn(ALOAD, 5);
            m.visitMethodInsn(INVOKEVIRTUAL, cls.name, getName("getMetaFromState", "func_176201_c"), "(Lnet/minecraft/block/state/IBlockState;)I", false);
            m.visitMethodInsn(INVOKEVIRTUAL, "net/minecraft/block/Block", getName("getStateFromMeta", "func_176203_a"), "(I)Lnet/minecraft/block/state/IBlockState;", false);
            m.visitMethodInsn(INVOKEVIRTUAL, "net/minecraft/world/World", getName("setBlockState", "func_175656_a"), "(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;)Z", false);
            m.visitInsn(IRETURN);
            m.visitLabel(l_con);
            m.visitFrame(F_SAME, 0, null, 0, null);
            m.visitInsn(ICONST_1);
            m.visitInsn(IRETURN);
        }
        MethodNode initMethod = null;
        boolean createsBlockState = false;
        for (MethodNode method : cls.methods) {
            if ((method.access & ACC_PRIVATE) == ACC_PRIVATE) method.access ^= ACC_PRIVATE;
            if ((method.access & ACC_PUBLIC) != ACC_PUBLIC) method.access |= ACC_PROTECTED;
            if (method.name.equals("<init>")) {
                initMethod = method;
            }
            else if (method.name.equals(getName("createBlockState", "func_180661_e"))) {
                createsBlockState = true;
            }
        }
        if ((cls.access & ACC_ABSTRACT) != ACC_ABSTRACT) {
            if ((cls.access & ACC_PRIVATE) == ACC_PRIVATE) cls.access ^= ACC_PRIVATE;
            else if ((cls.access & ACC_PROTECTED) == ACC_PROTECTED) cls.access ^= ACC_PROTECTED;
            cls.access |= ACC_PUBLIC;
            if ((cls.access & ACC_FINAL) == ACC_FINAL) cls.access ^= ACC_FINAL;
            if (initMethod != null) {
                if ((initMethod.access & ACC_PRIVATE) == ACC_PRIVATE) {
                    initMethod.access ^= ACC_PRIVATE;
                    initMethod.access |= ACC_PUBLIC;
                }
                String clsName = cls.name;
                IntList map = getDescList(initMethod.desc);
                String clsStripped = null;
                String clsBark = null;
                String clsStrippedBark = null;
                clsStripped = createStrippedClass(cls, initMethod.desc, map, createsBlockState);
                if (!clsName.startsWith("de/ellpeck/naturesaura")) {
                    clsBark = createBarkClass(cls, initMethod.desc, map, createsBlockState);
                    clsStrippedBark = createStrippedBarkClass(cls, initMethod.desc, map, createsBlockState);
                }
                if (clsStripped != null) cls.visitInnerClass(clsStripped, cls.name, "Stripped", ACC_PUBLIC | ACC_STATIC);
                if (clsBark != null) cls.visitInnerClass(clsBark, cls.name, "Bark", ACC_PUBLIC | ACC_STATIC);
                if (clsStrippedBark != null) cls.visitInnerClass(clsStrippedBark, cls.name, "StrippedBark", ACC_PUBLIC | ACC_STATIC);
                Iterator<AbstractInsnNode> iterator = initMethod.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == RETURN) {
                        String initDesc;
                        {
                            StringBuilder builder = new StringBuilder();
                            for (int i = 0; i < initMethod.desc.length(); i++) {
                                char c = initMethod.desc.charAt(i);
                                builder.append(c);
                                if (c == '(') {
                                    builder.append("Lnet/minecraft/block/Block;"); // origLog
                                }
                            }
                            initDesc = builder.toString();
                        }
                        InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(hook("Logs$isOriginal", "(Ljava/lang/Object;)Z"));
                        LabelNode l_con = new LabelNode();
                        list.add(new JumpInsnNode(IFEQ, l_con));
                        list.add(new VarInsnNode(ALOAD, 0)); // origLog
                        if (clsStripped != null) {
                            list.add(new VarInsnNode(ALOAD, 0));
                            list.add(new TypeInsnNode(NEW, clsStripped));
                            list.add(new InsnNode(DUP));
                            list.add(new VarInsnNode(ALOAD, 0));
                            if (!map.isEmpty()) {
                                for (int i = 0; i < map.size(); i++) {
                                    list.add(new VarInsnNode(map.getInt(i), i + 1));
                                }
                            }
                            list.add(new MethodInsnNode(INVOKESPECIAL, clsStripped, "<init>", initDesc, false));
                            list.add(new FieldInsnNode(PUTFIELD, cls.name, "stripped", "Lnet/minecraft/block/Block;"));
                        }
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new FieldInsnNode(GETFIELD, cls.name, "stripped", "Lnet/minecraft/block/Block;"));
                        if (clsBark != null) {
                            list.add(new TypeInsnNode(NEW, clsBark));
                            list.add(new InsnNode(DUP));
                            list.add(new VarInsnNode(ALOAD, 0));
                            if (!map.isEmpty()) {
                                for (int i = 0; i < map.size(); i++) {
                                    list.add(new VarInsnNode(map.getInt(i), i + 1));
                                }
                            }
                            list.add(new MethodInsnNode(INVOKESPECIAL, clsBark, "<init>", initDesc, false));
                        }
                        else {
                            list.add(new InsnNode(ACONST_NULL));
                        }
                        if (clsStrippedBark != null) {
                            list.add(new VarInsnNode(ALOAD, 0));
                            list.add(new TypeInsnNode(NEW, clsStrippedBark));
                            list.add(new InsnNode(DUP));
                            list.add(new VarInsnNode(ALOAD, 0));
                            if (!map.isEmpty()) {
                                for (int i = 0; i < map.size(); i++) {
                                    list.add(new VarInsnNode(map.getInt(i), i + 1));
                                }
                            }
                            list.add(new MethodInsnNode(INVOKESPECIAL, clsStrippedBark, "<init>", initDesc, false));
                            list.add(new FieldInsnNode(PUTFIELD, cls.name, "strippedBark", "Lnet/minecraft/block/Block;"));
                        }
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new FieldInsnNode(GETFIELD, cls.name, "strippedBark", "Lnet/minecraft/block/Block;"));
                        list.add(hook("Logs$registerBlocks", "(Lnet/minecraft/block/Block;Lnet/minecraft/block/Block;Lnet/minecraft/block/Block;Lnet/minecraft/block/Block;)V"));
                        list.add(l_con);
                        initMethod.instructions.insertBefore(node, list);
                        break;
                    }
                }
            }
            else {
                String clsStripped = createStrippedClass(cls, "()V", null, createsBlockState);
                String clsBark = createBarkClass(cls, "()V", null, createsBlockState);
                String clsStrippedBark = createStrippedBarkClass(cls, "()V", null, createsBlockState);
                cls.visitInnerClass(clsStripped, cls.name, "Stripped", ACC_PUBLIC | ACC_STATIC);
                cls.visitInnerClass(clsBark, cls.name, "Bark", ACC_PUBLIC | ACC_STATIC);
                cls.visitInnerClass(clsStrippedBark, cls.name, "StrippedBark", ACC_PUBLIC | ACC_STATIC);
                { // <init>
                    MethodVisitor m = cls.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
                    m.visitVarInsn(ALOAD, 0);
                    m.visitMethodInsn(INVOKESPECIAL, cls.name, "<init>", "()V", false);
                    m.visitVarInsn(ALOAD, 0);
                    m.visitMethodInsn(INVOKESTATIC, "surreal/backportium/core/BPHooks", "Logs$isOriginal", "(Ljava/lang/Object;)Z", false);
                    Label l_con = new Label();
                    m.visitJumpInsn(IFEQ, l_con);
                    m.visitVarInsn(ALOAD, 0);
                    m.visitTypeInsn(NEW, clsStripped);
                    m.visitInsn(DUP);
                    m.visitVarInsn(ALOAD, 0);
                    m.visitMethodInsn(INVOKESPECIAL, clsStripped, "<init>", "(Lnet/minecraft/block/Block;)V", false);
                    m.visitTypeInsn(NEW, clsBark);
                    m.visitInsn(DUP);
                    m.visitVarInsn(ALOAD, 0);
                    m.visitMethodInsn(INVOKESPECIAL, clsBark, "<init>", "(Lnet/minecraft/block/Block;)V", false);
                    m.visitTypeInsn(NEW, clsStrippedBark);
                    m.visitInsn(DUP);
                    m.visitVarInsn(ALOAD, 0);
                    m.visitMethodInsn(INVOKESPECIAL, clsStrippedBark, "<init>", "(Lnet/minecraft/block/Block;)V", false);
                    m.visitMethodInsn(INVOKESTATIC, "surreal/backportium/core/BPHooks", "Logs$registerBlocks", "(Lnet/minecraft/block/Block;Lnet/minecraft/block/Block;Lnet/minecraft/block/Block;Lnet/minecraft/block/Block;)V", false);
                    m.visitLabel(l_con);
                    m.visitFrame(F_SAME, 0, null, 0, null);
                    m.visitInsn(RETURN);
                }
            }
        }
        writeClass(cls);
        return write(cls, ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES); // COMPUTE_FRAMES???? He fell off.....
    }

    public static byte[] transformItemBlock(byte[] basicClass) {
        ClassNode cls = read(basicClass);
        for (MethodNode method : cls.methods) {
            if (method.name.equals("<init>")) {
                AbstractInsnNode node = method.instructions.getLast();
                while (node.getOpcode() != RETURN) node = node.getPrevious();
                InsnList list = new InsnList();
                list.add(new VarInsnNode(ALOAD, 1));
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(hook("Logs$registerItem", "(Lnet/minecraft/block/Block;Lnet/minecraft/item/ItemBlock;)V"));
                method.instructions.insertBefore(node, list);
                break;
            }
        }
        { // getItemStackDisplayName
            MethodVisitor m = cls.visitMethod(ACC_PUBLIC, getName("getItemStackDisplayName", "func_77653_i"), "(Lnet/minecraft/item/ItemStack;)Ljava/lang/String;", null, null);
            m.visitVarInsn(ALOAD, 0);
            m.visitVarInsn(ALOAD, 1);
            m.visitMethodInsn(INVOKESPECIAL, cls.superName, getName("getItemStackDisplayName", "func_77653_i"), "(Lnet/minecraft/item/ItemStack;)Ljava/lang/String;", false);
            m.visitVarInsn(ALOAD, 1);
            m.visitMethodInsn(INVOKESTATIC, "surreal/backportium/core/BPHooks", "Logs$getItemStackDisplayName", "(Ljava/lang/String;Lnet/minecraft/item/ItemStack;)Ljava/lang/String;", false);
            m.visitInsn(ARETURN);
        }
        return write(cls);
    }

    /**
     * For registering vanilla additional logs.
     **/
    public static byte[] transformBlock(byte[] basicClass) {
        if (!shouldRegisterVanillaLogs()) return basicClass;
        ClassNode cls = read(basicClass);
        for (int i = cls.methods.size() - 1; i >= 0; i--) {
            MethodNode method = cls.methods.get(i);
            if (method.name.equals(getName("registerBlock", "func_176219_a"))) {
                AbstractInsnNode node = method.instructions.getFirst();
                InsnList list = new InsnList();
                list.add(new FieldInsnNode(GETSTATIC, cls.name, getName("REGISTRY", "field_149771_c"), "Lnet/minecraft/util/registry/RegistryNamespacedDefaultedByKey;"));
                list.add(new VarInsnNode(ALOAD, 1));
                list.add(new VarInsnNode(ALOAD, 2));
                list.add(hook("Logs$registerVanilla", "(Lnet/minecraft/util/registry/RegistryNamespacedDefaultedByKey;Ljava/lang/String;Lnet/minecraft/block/Block;)V"));
                method.instructions.insertBefore(node, list);
                break;
            }
        }
        return write(cls);
    }

    /**
     * For registering vanilla additional logs.
     **/
    public static byte[] transformItem(byte[] basicClass) {
        if (!shouldRegisterVanillaLogs()) return basicClass;
        ClassNode cls = read(basicClass);
        for (int i = cls.methods.size() - 1; i >= 0; i--) {
            MethodNode method = cls.methods.get(i);
            if (method.name.equals(getName("registerItemBlock", "func_179214_a"))) {
                AbstractInsnNode node = method.instructions.getFirst();
                InsnList list = new InsnList();
                list.add(new FieldInsnNode(GETSTATIC, cls.name, getName("REGISTRY", "field_150901_e"), "Lnet/minecraft/util/registry/RegistryNamespaced;"));
                list.add(new FieldInsnNode(GETSTATIC, cls.name, getName("BLOCK_TO_ITEM", "field_179220_a"), "Ljava/util/Map;"));
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new VarInsnNode(ALOAD, 1));
                list.add(hook("Logs$registerVanilla", "(Lnet/minecraft/util/registry/RegistryNamespaced;Ljava/util/Map;Lnet/minecraft/block/Block;Lnet/minecraft/item/Item;)V"));
                method.instructions.insertBefore(node, list);
                break;
            }
        }
        return write(cls);
    }

    public static byte[] transformForgeRegistryEntry$Impl(byte[] basicClass) {
        ClassNode cls = read(basicClass);
        for (MethodNode method : cls.methods) {
            if (method.name.equals("setRegistryName")) {
                AbstractInsnNode node = method.instructions.getLast();
                while (node.getOpcode() != ALOAD || ((VarInsnNode) node).var != 1) node = node.getPrevious();
                method.instructions.insertBefore(node, new VarInsnNode(ALOAD, 0));
                method.instructions.insert(node, hook("Logs$setRegistryNameDeep", "(Lnet/minecraftforge/registries/IForgeRegistryEntry$Impl;Ljava/lang/String;)Ljava/lang/String;"));
                break;
            }
        }
        return write(cls);
    }

    public static byte[] transformASMEventHandler(byte[] basicClass) {
        ClassNode cls = read(basicClass);
        for (MethodNode method : cls.methods) {
            if (method.name.equals("invoke")) {
                AbstractInsnNode node = method.instructions.getLast();
                while (node.getOpcode() != RETURN) node = node.getPrevious();
                InsnList list = new InsnList();
                list.add(new VarInsnNode(ALOAD, 1));
                list.add(hook("Logs$postRegister", "(Lnet/minecraftforge/fml/common/eventhandler/Event;)V"));
                method.instructions.insertBefore(node, list);
                break;
            }
        }
        return write(cls);
    }

    public static byte[] transformBlockStateMapper(byte[] basicClass) {
        ClassNode cls = read(basicClass);
        for (MethodNode method : cls.methods) {
            if (method.name.equals("registerBlockStateMapper")) {
                AbstractInsnNode node = method.instructions.getLast();
                while (node.getOpcode() != RETURN) node = node.getPrevious();
                InsnList list = new InsnList();
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new VarInsnNode(ALOAD, 1));
                list.add(new VarInsnNode(ALOAD, 2));
                list.add(clientHook("Logs$registerBlockStateMapper", "(Lnet/minecraft/client/renderer/block/statemap/BlockStateMapper;Lnet/minecraft/block/Block;Lnet/minecraft/client/renderer/block/statemap/IStateMapper;)V"));
                method.instructions.insertBefore(node, list);
                break;
            }
        }
        return write(cls);
    }

    /**
     * Generates class for stripped log variants.
     * @param cls ClassNode of the original log class.
     * @param initDesc Descriptor of constructor of the class.
     **/
    private static String createStrippedClass(ClassNode cls, String initDesc, IntList descList, boolean hasBlockState) {
        String strippedClsName = "backportium/logs/" + cls.name + "$Stripped";
        ClassNode clsStripped = new ClassNode();
        clsStripped.visit(V1_8, ACC_PUBLIC | ACC_STATIC, strippedClsName, null, cls.name, null);
        { // origLog
            String blockDesc = "Lnet/minecraft/block/Block;";
            clsStripped.visitField(ACC_PUBLIC | ACC_FINAL, "origLog", blockDesc, null, null);
        }
        { // <init>
            String strippedInitDesc;
            {
                StringBuilder builder = new StringBuilder();
                for (int i = 0; i < initDesc.length(); i++) {
                    char c = initDesc.charAt(i);
                    builder.append(c);
                    if (c == '(') {
                        builder.append("Lnet/minecraft/block/Block;"); // origLog
                    }
                }
                strippedInitDesc = builder.toString();
            }
            MethodVisitor m = clsStripped.visitMethod(ACC_PUBLIC, "<init>", strippedInitDesc, null, null);
            m.visitVarInsn(ALOAD, 0);
            m.visitVarInsn(ALOAD, 0);
            boolean hasValues = descList != null && !descList.isEmpty();
            if (hasValues) {
                for (int i = 0; i < descList.size(); i++) {
                    m.visitVarInsn(descList.get(i), i + 2);
                }
            }
            m.visitMethodInsn(INVOKESPECIAL, cls.name, "<init>", initDesc, false);
            m.visitVarInsn(ALOAD, 0);
            m.visitVarInsn(ALOAD, 1);
            m.visitFieldInsn(PUTFIELD, strippedClsName, "origLog", "Lnet/minecraft/block/Block;");
            m.visitInsn(RETURN);
        }
        { // onStrip
            MethodVisitor m = clsStripped.visitMethod(ACC_PUBLIC, "onStrip", "(Lnet/minecraft/world/World;Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/util/EnumHand;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/util/EnumFacing;Lnet/minecraft/util/math/Vec3d;)Z", null, null);
            m.visitInsn(ICONST_0);
            m.visitInsn(IRETURN);
        }
        if (hasBlockState) {
            { // getMetaFromState
                MethodVisitor m = clsStripped.visitMethod(ACC_PUBLIC, getName("getMetaFromState", "func_176201_c"), "(Lnet/minecraft/block/state/IBlockState;)I", null, null);
                m.visitVarInsn(ALOAD, 0);
                m.visitFieldInsn(GETFIELD, strippedClsName, "origLog", "Lnet/minecraft/block/Block;");
                Label l_con = new Label();
                m.visitJumpInsn(IFNULL, l_con);
                m.visitVarInsn(ALOAD, 0);
                m.visitVarInsn(ALOAD, 0);
                m.visitFieldInsn(GETFIELD, strippedClsName, "origLog", "Lnet/minecraft/block/Block;");
                m.visitVarInsn(ALOAD, 1);
                m.visitMethodInsn(INVOKESTATIC, "surreal/backportium/core/BPHooks", "Logs$getMetaFromState", "(Lnet/minecraft/block/Block;Lnet/minecraft/block/Block;Lnet/minecraft/block/state/IBlockState;)I", false);
                m.visitInsn(IRETURN);
                m.visitLabel(l_con);
                m.visitFrame(F_SAME, 0, null, 0, null);
                m.visitVarInsn(ALOAD, 0);
                m.visitVarInsn(ALOAD, 1);
                m.visitMethodInsn(INVOKESPECIAL, cls.name, getName("getMetaFromState", "func_176201_c"), "(Lnet/minecraft/block/state/IBlockState;)I", false);
                m.visitInsn(IRETURN);
            }
            { // getStateFromMeta
                MethodVisitor m = clsStripped.visitMethod(ACC_PUBLIC, getName("getStateFromMeta", "func_176203_a"), "(I)Lnet/minecraft/block/state/IBlockState;", null, null);
                m.visitVarInsn(ALOAD, 0);
                m.visitFieldInsn(GETFIELD, strippedClsName, "origLog", "Lnet/minecraft/block/Block;");
                Label l_con = new Label();
                m.visitJumpInsn(IFNULL, l_con);
                m.visitVarInsn(ALOAD, 0);
                m.visitVarInsn(ALOAD, 0);
                m.visitFieldInsn(GETFIELD, strippedClsName, "origLog", "Lnet/minecraft/block/Block;");
                m.visitVarInsn(ILOAD, 1);
                m.visitMethodInsn(INVOKESTATIC, "surreal/backportium/core/BPHooks", "Logs$getStateFromMeta", "(Lnet/minecraft/block/Block;Lnet/minecraft/block/Block;I)Lnet/minecraft/block/state/IBlockState;", false);
                m.visitInsn(ARETURN);
                m.visitLabel(l_con);
                m.visitFrame(F_SAME, 0, null, 0, null);
                m.visitVarInsn(ALOAD, 0);
                m.visitVarInsn(ILOAD, 1);
                m.visitMethodInsn(INVOKESPECIAL, cls.name, getName("getStateFromMeta", "func_176203_a"), "(I)Lnet/minecraft/block/state/IBlockState;", false);
                m.visitInsn(ARETURN);
            }
        }
        byte[] bytes = write(clsStripped);
        loadNewClass(strippedClsName, bytes);
        return strippedClsName;
    }

    /**
     * Generates class for bark log variants.
     * @param cls ClassNode of the original log class.
     * @param initDesc Descriptor of constructor of the class.
     **/
    private static String createBarkClass(ClassNode cls, String initDesc, IntList descList, boolean hasBlockState) {
        String barkClsName = "backportium/logs/" + cls.name + "$Bark";
        ClassNode clsBark = new ClassNode();
        clsBark.visit(V1_8, ACC_PUBLIC | ACC_STATIC, barkClsName, null, cls.name, null);
        { // origLog | bark
            String blockDesc = "Lnet/minecraft/block/Block;";
            clsBark.visitField(ACC_PUBLIC | ACC_FINAL, "origLog", blockDesc, null, null);
        }
        { // <init>
            String strippedInitDesc;
            {
                StringBuilder builder = new StringBuilder();
                for (int i = 0; i < initDesc.length(); i++) {
                    char c = initDesc.charAt(i);
                    builder.append(c);
                    if (c == '(') {
                        builder.append("Lnet/minecraft/block/Block;"); // origLog
                    }
                }
                strippedInitDesc = builder.toString();
            }
            MethodVisitor m = clsBark.visitMethod(ACC_PUBLIC, "<init>", strippedInitDesc, null, null);
            m.visitVarInsn(ALOAD, 0);
            m.visitVarInsn(ALOAD, 0);
            boolean hasValues = descList != null && !descList.isEmpty();
            if (hasValues) {
                for (int i = 0; i < descList.size(); i++) {
                    m.visitVarInsn(descList.get(i), i + 2);
                }
            }
            m.visitMethodInsn(INVOKESPECIAL, cls.name, "<init>", initDesc, false);
            m.visitVarInsn(ALOAD, 0);
            m.visitVarInsn(ALOAD, 1);
            m.visitFieldInsn(PUTFIELD, barkClsName, "origLog", "Lnet/minecraft/block/Block;");
            m.visitInsn(RETURN);
        }
        { // onStrip
            MethodVisitor m = clsBark.visitMethod(ACC_PUBLIC, "onStrip", "(Lnet/minecraft/world/World;Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/util/EnumHand;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/util/EnumFacing;Lnet/minecraft/util/math/Vec3d;)Z", null, null);
            m.visitVarInsn(ALOAD, 1);
            m.visitFieldInsn(GETFIELD, "net/minecraft/world/World", getName("isRemote", "field_72995_K"), "Z");
            Label l_con = new Label();
            m.visitJumpInsn(IFNE, l_con);
            m.visitVarInsn(ALOAD, 0);
            m.visitFieldInsn(GETFIELD, cls.name, "strippedBark", "Lnet/minecraft/block/Block;");
            m.visitJumpInsn(IFNONNULL, l_con);
            m.visitVarInsn(ALOAD, 1);
            m.visitVarInsn(ALOAD, 4);
            m.visitVarInsn(ALOAD, 0);
            m.visitFieldInsn(GETFIELD, cls.name, "strippedBark", "Lnet/minecraft/block/Block;");
            m.visitVarInsn(ALOAD, 0);
            m.visitVarInsn(ALOAD, 5);
            m.visitMethodInsn(INVOKEVIRTUAL, cls.name, getName("getMetaFromState", "func_176201_c"), "(Lnet/minecraft/block/state/IBlockState;)I", false);
            m.visitMethodInsn(INVOKEVIRTUAL, "net/minecraft/block/Block", getName("getStateFromMeta", "func_176203_a"), "(I)Lnet/minecraft/block/state/IBlockState;", false);
            m.visitMethodInsn(INVOKEVIRTUAL, "net/minecraft/world/World", getName("setBlockState", "func_175656_a"), "(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;)Z", false);
            m.visitInsn(IRETURN);
            m.visitLabel(l_con);
            m.visitFrame(F_SAME, 0, null, 0, null);
            m.visitInsn(ICONST_1);
            m.visitInsn(IRETURN);
        }
        if (hasBlockState) {
            { // getMetaFromState
                MethodVisitor m = clsBark.visitMethod(ACC_PUBLIC, getName("getMetaFromState", "func_176201_c"), "(Lnet/minecraft/block/state/IBlockState;)I", null, null);
                m.visitVarInsn(ALOAD, 0);
                m.visitFieldInsn(GETFIELD, barkClsName, "origLog", "Lnet/minecraft/block/Block;");
                Label l_con = new Label();
                m.visitJumpInsn(IFNULL, l_con);
                m.visitVarInsn(ALOAD, 0);
                m.visitVarInsn(ALOAD, 0);
                m.visitFieldInsn(GETFIELD, barkClsName, "origLog", "Lnet/minecraft/block/Block;");
                m.visitVarInsn(ALOAD, 1);
                m.visitMethodInsn(INVOKESTATIC, "surreal/backportium/core/BPHooks", "Logs$getMetaFromState", "(Lnet/minecraft/block/Block;Lnet/minecraft/block/Block;Lnet/minecraft/block/state/IBlockState;)I", false);
                m.visitInsn(IRETURN);
                m.visitLabel(l_con);
                m.visitFrame(F_SAME, 0, null, 0, null);
                m.visitVarInsn(ALOAD, 0);
                m.visitVarInsn(ALOAD, 1);
                m.visitMethodInsn(INVOKESPECIAL, cls.superName, getName("getMetaFromState", "func_176201_c"), "(Lnet/minecraft/block/state/IBlockState;)I", false);
                m.visitInsn(IRETURN);
            }
            { // getStateFromMeta
                MethodVisitor m = clsBark.visitMethod(ACC_PUBLIC, getName("getStateFromMeta", "func_176203_a"), "(I)Lnet/minecraft/block/state/IBlockState;", null, null);
                m.visitVarInsn(ALOAD, 0);
                m.visitFieldInsn(GETFIELD, barkClsName, "origLog", "Lnet/minecraft/block/Block;");
                Label l_con = new Label();
                m.visitJumpInsn(IFNULL, l_con);
                m.visitVarInsn(ALOAD, 0);
                m.visitVarInsn(ALOAD, 0);
                m.visitFieldInsn(GETFIELD, barkClsName, "origLog", "Lnet/minecraft/block/Block;");
                m.visitVarInsn(ILOAD, 1);
                m.visitMethodInsn(INVOKESTATIC, "surreal/backportium/core/BPHooks", "Logs$getStateFromMeta", "(Lnet/minecraft/block/Block;Lnet/minecraft/block/Block;I)Lnet/minecraft/block/state/IBlockState;", false);
                m.visitInsn(ARETURN);
                m.visitLabel(l_con);
                m.visitFrame(F_SAME, 0, null, 0, null);
                m.visitVarInsn(ALOAD, 0);
                m.visitVarInsn(ILOAD, 1);
                m.visitMethodInsn(INVOKESPECIAL, cls.superName, getName("getStateFromMeta", "func_176203_a"), "(I)Lnet/minecraft/block/state/IBlockState;", false);
                m.visitInsn(ARETURN);
            }
        }
        byte[] bytes = write(clsBark);
        loadNewClass(barkClsName, bytes);
        return barkClsName;
    }

    /**
     * Generates class for stripped bark log variants.
     * @param cls ClassNode of the original log class.
     * @param initDesc Descriptor of constructor of the class.
     **/
    private static String createStrippedBarkClass(ClassNode cls, String initDesc, IntList descList, boolean hasBlockState) {
        String strippedClsName = "backportium/logs/" + cls.name + "$StrippedBark";
        ClassNode clsStripped = new ClassNode();
        clsStripped.visit(V1_8, ACC_PUBLIC | ACC_STATIC, strippedClsName, null, cls.name, null);
        { // origLog | bark
            String blockDesc = "Lnet/minecraft/block/Block;";
            clsStripped.visitField(ACC_PUBLIC | ACC_FINAL, "origLog", blockDesc, null, null);
        }
        { // <init>
            String strippedInitDesc;
            {
                StringBuilder builder = new StringBuilder();
                for (int i = 0; i < initDesc.length(); i++) {
                    char c = initDesc.charAt(i);
                    builder.append(c);
                    if (c == '(') {
                        builder.append("Lnet/minecraft/block/Block;"); // origLog
                    }
                }
                strippedInitDesc = builder.toString();
            }
            MethodVisitor m = clsStripped.visitMethod(ACC_PUBLIC, "<init>", strippedInitDesc, null, null);
            m.visitVarInsn(ALOAD, 0);
            m.visitVarInsn(ALOAD, 0);
            boolean hasValues = descList != null && !descList.isEmpty();
            if (hasValues) {
                for (int i = 0; i < descList.size(); i++) {
                    m.visitVarInsn(descList.get(i), i + 2);
                }
            }
            m.visitMethodInsn(INVOKESPECIAL, cls.name, "<init>", initDesc, false);
            m.visitVarInsn(ALOAD, 0);
            m.visitVarInsn(ALOAD, 1);
            m.visitFieldInsn(PUTFIELD, strippedClsName, "origLog", "Lnet/minecraft/block/Block;");
            m.visitInsn(RETURN);
        }
        { // onStrip
            MethodVisitor m = clsStripped.visitMethod(ACC_PUBLIC, "onStrip", "(Lnet/minecraft/world/World;Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/util/EnumHand;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/util/EnumFacing;Lnet/minecraft/util/math/Vec3d;)Z", null, null);
            m.visitInsn(ICONST_0);
            m.visitInsn(IRETURN);
        }
        if (hasBlockState) {
            { // getMetaFromState
                MethodVisitor m = clsStripped.visitMethod(ACC_PUBLIC, getName("getMetaFromState", "func_176201_c"), "(Lnet/minecraft/block/state/IBlockState;)I", null, null);
                m.visitVarInsn(ALOAD, 0);
                m.visitFieldInsn(GETFIELD, strippedClsName, "origLog", "Lnet/minecraft/block/Block;");
                Label l_con = new Label();
                m.visitJumpInsn(IFNULL, l_con);
                m.visitVarInsn(ALOAD, 0);
                m.visitVarInsn(ALOAD, 0);
                m.visitFieldInsn(GETFIELD, strippedClsName, "origLog", "Lnet/minecraft/block/Block;");
                m.visitVarInsn(ALOAD, 1);
                m.visitMethodInsn(INVOKESTATIC, "surreal/backportium/core/BPHooks", "Logs$getMetaFromState", "(Lnet/minecraft/block/Block;Lnet/minecraft/block/Block;Lnet/minecraft/block/state/IBlockState;)I", false);
                m.visitInsn(IRETURN);
                m.visitLabel(l_con);
                m.visitFrame(F_SAME, 0, null, 0, null);
                m.visitVarInsn(ALOAD, 0);
                m.visitVarInsn(ALOAD, 1);
                m.visitMethodInsn(INVOKESPECIAL, cls.name, getName("getMetaFromState", "func_176201_c"), "(Lnet/minecraft/block/state/IBlockState;)I", false);
                m.visitInsn(IRETURN);
            }
            { // getStateFromMeta
                MethodVisitor m = clsStripped.visitMethod(ACC_PUBLIC, getName("getStateFromMeta", "func_176203_a"), "(I)Lnet/minecraft/block/state/IBlockState;", null, null);
                m.visitVarInsn(ALOAD, 0);
                m.visitFieldInsn(GETFIELD, strippedClsName, "origLog", "Lnet/minecraft/block/Block;");
                Label l_con = new Label();
                m.visitJumpInsn(IFNULL, l_con);
                m.visitVarInsn(ALOAD, 0);
                m.visitVarInsn(ALOAD, 0);
                m.visitFieldInsn(GETFIELD, strippedClsName, "origLog", "Lnet/minecraft/block/Block;");
                m.visitVarInsn(ILOAD, 1);
                m.visitMethodInsn(INVOKESTATIC, "surreal/backportium/core/BPHooks", "Logs$getStateFromMeta", "(Lnet/minecraft/block/Block;Lnet/minecraft/block/Block;I)Lnet/minecraft/block/state/IBlockState;", false);
                m.visitInsn(ARETURN);
                m.visitLabel(l_con);
                m.visitFrame(F_SAME, 0, null, 0, null);
                m.visitVarInsn(ALOAD, 0);
                m.visitVarInsn(ILOAD, 1);
                m.visitMethodInsn(INVOKESPECIAL, cls.name, getName("getStateFromMeta", "func_176203_a"), "(I)Lnet/minecraft/block/state/IBlockState;", false);
                m.visitInsn(ARETURN);
            }
        }
        byte[] bytes = write(clsStripped);
        loadNewClass(strippedClsName, bytes);
        return strippedClsName;
    }

    private static String fromUtf8Const(byte[] cls, int start) {
        int size = ((cls[start + 2] & 0xFF) | (cls[start + 1] & 0xFF) << 8) - 1;
        start += 3;
        StringBuilder builder = new StringBuilder();
        while (size > -1) {
            builder.insert(0, (char) cls[start + size]);
            size--;
        }
        return builder.toString();
    }

    // Return index to jump
    // index is always the start of the constant
    private static int nextConstant(byte[] cls, int index) {
        int type = cls[index];
        switch (type) {
            case UTF8: return index + 3 + (((cls[index + 2] & 0xFF) | (cls[index + 1] & 0xFF) << 8));
            case INTEGER:
            case FLOAT: return index + 5;
            case LONG:
            case DOUBLE: return index + 9;
            case CLASS:
            case STRING: return index + 3;
            case FIELD_REF:
            case METHOD_REF:
            case IFACE_METHOD_REF:
            case NAME_AND_TYPE: return index + 5;
            case METHOD_HANDLE: return index + 4;
            case METHOD_TYPE: return index + 3;
            case INVOKE_DYNAMIC: return index + 5;
            default: return index + 3;
        }
    }

    private static final int
            UTF8 = 1,
            INTEGER = 3,
            FLOAT = 4,
            LONG = 5,
            DOUBLE = 6,
            CLASS = 7,
            STRING = 8,
            FIELD_REF = 9,
            METHOD_REF = 10,
            IFACE_METHOD_REF = 11,
            NAME_AND_TYPE = 12,
            METHOD_HANDLE = 15,
            METHOD_TYPE = 16,
            INVOKE_DYNAMIC = 18;

    private static boolean shouldRegisterVanillaLogs() {
        return !DEBARK && !DEBARKED_LOGS && !FUTUREMC;
    }
}
