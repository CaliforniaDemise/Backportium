package surreal.backportium.core;

import net.minecraft.launchwrapper.IClassTransformer;
import surreal.backportium.core.transformers.PumpkinTransformer;
import surreal.backportium.core.transformers.TridentTransformer;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;

@SuppressWarnings("unused")
public class BPTransformer implements IClassTransformer {

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (basicClass == null) return null;
        switch (transformedName) {
//            case "net.minecraft.block.BlockButton": return transformBlockButton(basicClass);
            // Trident
            case "net.minecraft.client.model.ModelBiped" : return TridentTransformer.transformModelBiped(basicClass);
            case "net.minecraft.entity.projectile.EntityArrow": return TridentTransformer.transformEntityArrow(basicClass);
            case "net.minecraft.util.DamageSource": return TridentTransformer.transformDamageSource(basicClass);
            case "net.minecraft.entity.EntityLivingBase": return TridentTransformer.transformEntityLivingBase(basicClass);
            case "net.minecraft.entity.player.EntityPlayer": return TridentTransformer.transformEntityPlayer(basicClass);
            case "net.minecraft.client.renderer.entity.RenderLivingBase": return TridentTransformer.transformRenderLivingBase(basicClass);
            case "net.minecraft.client.renderer.entity.RenderPlayer": return TridentTransformer.transformRenderPlayer(basicClass);

            // Pumpkin
            case "net.minecraft.block.BlockPane":
            case "net.minecraft.block.BlockWall":
                return PumpkinTransformer.transformBlockFenceLike(basicClass);
            case "net.minecraft.block.BlockStem": return PumpkinTransformer.transformBlockStem(basicClass);
//            case "net.minecraft.stats.StatList": return PumpkinTransformer.transformStatList(basicClass); TODO Find a way without loading ModBlocks early
            case "net.minecraft.world.gen.feature.WorldGenPumpkin": return PumpkinTransformer.transformWorldGenPumpkin(basicClass);
        }
        return basicClass;
    }



    public static void classOut(String name, byte[] bytes) {
        File file = new File(BPPlugin.GAME_DIR, "classOut/" + name + ".class");
        file.getParentFile().mkdirs();

        try (OutputStream os = Files.newOutputStream(file.toPath())) {
            os.write(bytes);
        }
        catch (IOException ignored) {}
    }



//    private byte[] transformBlockButton(byte[] basicClass) {
//        ClassReader reader = new ClassReader(basicClass);
//        ClassNode cls = new ClassNode();
//        reader.accept(cls, 0);
//
//        cls.superName = "net/minecraft/block/BlockHorizontal";
//
//        { // FACE property
//            cls.visitField(ACC_PUBLIC | ACC_STATIC | ACC_FINAL, "FACE", "Lnet/minecraft/block/properties/PropertyEnum;", null, null);
//        }
//
//        for (MethodNode method : cls.methods) {
//            if (method.name.equals("<init>")) {
//                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
//
//                int i = 0;
//
//                while (iterator.hasNext()) {
//                    AbstractInsnNode node = iterator.next();
//                    if (node.getOpcode() == GETSTATIC) {
//                        if (i == 1) {
//                            InsnList list = new InsnList();
//                            list.add(new FieldInsnNode(GETSTATIC, "net/minecraft/block/BlockButton", "FACE", "Lnet/minecraft/block/properties/PropertyEnum;"));
//                            list.add(new FieldInsnNode(GETSTATIC, "surreal/backportium/api/enums/ButtonFace", "WALL", "Lsurreal/backportium/enums/ButtonFace;"));
//                            list.add(new MethodInsnNode(INVOKEINTERFACE, "net/minecraft/block/state/IBlockState", "withProperty", "(Lnet/minecraft/block/properties/IProperty;Ljava/lang/Comparable;)Lnet/minecraft/block/state/IBlockState;", true));
//                            method.instructions.insertBefore(node, list);
//                            break;
//                        }
//                        i++;
//                    }
//                }
//            }
//            else if (method.name.equals("<clinit>")) {
//                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
//
//                while (iterator.hasNext()) {
//                    AbstractInsnNode node = iterator.next();
//                    if (node.getOpcode() == RETURN) {
//                        InsnList list = new InsnList();
//                        list.add(new LdcInsnNode("face"));
//                        list.add(new LdcInsnNode(Type.getType("Lsurreal/backportium/api/enums/ButtonFace;")));
//                        list.add(new MethodInsnNode(INVOKESTATIC, "net/minecraft/block/properties/PropertyEnum", "create", "(Ljava/lang/String;Ljava/lang/Class;)Lnet/minecraft/block/properties/PropertyEnum;", false));
//                        list.add(new FieldInsnNode(PUTSTATIC, "net/minecraft/block/BlockButton", "FACE", "Lnet/minecraft/block/properties/PropertyEnum;"));
//                        method.instructions.insertBefore(node, list);
//                    }
//                }
//            }
//        }
//
//        classOut(cls.name, basicClass);
//
//        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
//        cls.accept(writer);
//
//        classOut(cls.name + "_changed", writer.toByteArray());
//
//        return basicClass;
//    }
}
