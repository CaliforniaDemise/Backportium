package surreal.backportium.core;

import net.minecraft.launchwrapper.IClassTransformer;
import surreal.backportium.core.transformers.DebarkingTransformer;
import surreal.backportium.core.transformers.FluidloggingTransformer;
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
        if (transformedName.startsWith("surreal.backportium")) return basicClass;
        if (transformedName.startsWith("it.unimi") || transformedName.startsWith("com.google") || transformedName.startsWith("com.ibm") || transformedName.startsWith("com.paulscode")) return basicClass;
        switch (transformedName) {
            // Trident
            case "net.minecraft.client.model.ModelBiped": return TridentTransformer.transformModelBiped(basicClass);
            case "net.minecraft.util.DamageSource": return TridentTransformer.transformDamageSource(basicClass);
            case "net.minecraft.entity.EntityLivingBase": return TridentTransformer.transformEntityLivingBase(basicClass);
            case "net.minecraft.entity.player.EntityPlayer": return TridentTransformer.transformEntityPlayer(basicClass);
            case "net.minecraft.client.renderer.entity.RenderLivingBase": return TridentTransformer.transformRenderLivingBase(basicClass);
            case "net.minecraft.client.renderer.entity.RenderPlayer": return TridentTransformer.transformRenderPlayer(basicClass);
            case "net.minecraft.client.entity.EntityPlayerSP": return TridentTransformer.transformEntityPlayerSP(basicClass);

            // Pumpkin
            case "net.minecraft.block.BlockPane":
            case "net.minecraft.block.BlockWall":
                return PumpkinTransformer.transformBlockFenceLike(basicClass);
            case "net.minecraft.block.BlockStem": return PumpkinTransformer.transformBlockStem(basicClass);
//            case "net.minecraft.stats.StatList":return PumpkinTransformer.transformStatList(basicClass); TODO Find a way without loading ModBlocks early
            case "net.minecraft.world.gen.feature.WorldGenPumpkin": return PumpkinTransformer.transformWorldGenPumpkin(basicClass);

            // Fluidlogging
            case "net.minecraftforge.fluids.BlockFluidBase": return FluidloggingTransformer.transformBlockFluidBase(basicClass);
            case "net.minecraft.block.BlockLiquid": return FluidloggingTransformer.transformBlockLiquid(basicClass);

            // Debarking
            case "net.minecraft.client.renderer.block.statemap.BlockStateMapper": return DebarkingTransformer.transformBlockStateMapper(basicClass);
            case "net.minecraftforge.registries.ForgeRegistry": return DebarkingTransformer.transformForgeRegistry(basicClass);
            case "net.minecraft.block.Block": return DebarkingTransformer.transformBlock(basicClass);
            case "net.minecraft.item.Item": return DebarkingTransformer.transformItem(basicClass);
            case "net.minecraft.item.ItemBlock": return DebarkingTransformer.transformItemBlock(basicClass);
            case "net.minecraftforge.registries.IForgeRegistryEntry$Impl": return DebarkingTransformer.transformForgeRegistryEntry$Impl(basicClass);
        }
        if (!transformedName.startsWith("net.minecraftforge")) {
            boolean bewitchmentCheck = transformedName.equals("com.bewitchment.common.block.util.ModBlockPillar"); // Some mods like Bewitchment likes to create logs without extending BlockLog
            boolean techrebornCheck = transformedName.equals("techreborn.blocks.BlockRubberLog");
            boolean thaumcraftCheck = transformedName.equals("thaumcraft.common.blocks.world.plants.BlockLogsTC");
            String[] toCheck = new String[] { "net/minecraft/block/BlockLog", "com/progwml6/natura/common/block/BlockEnumLog" };
            if (bewitchmentCheck || techrebornCheck || thaumcraftCheck || DebarkingTransformer.checkLogs(basicClass, transformedName, toCheck, false)) return DebarkingTransformer.transformBlockLogEx(basicClass);
        }
        return basicClass;
    }

    public static void classOut(String name, byte[] bytes) {
        File file = new File(BPPlugin.GAME_DIR, "classOut/" + name + ".class");
        file.getParentFile().mkdirs();

        try (OutputStream os = Files.newOutputStream(file.toPath())) {
            os.write(bytes);
        } catch (IOException ignored) {
        }
    }

    private static boolean checkBytes(byte[] basicClass, String name) {
        for (int i = basicClass.length - 1; i >= 0; i--) {
            byte b = basicClass[i];
            int ii = i;
            int g = name.length() - 1;
            while (name.charAt(g) == (char) b) {
                ii--;
                b = basicClass[ii];
                if (g == 0) return true;
                g--;
            }
        }
        return false;
    }
}
