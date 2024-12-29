package surreal.backportium.core;

import net.minecraft.launchwrapper.IClassTransformer;
import surreal.backportium.core.transformers.*;

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
            case "net.minecraft.client.model.ModelBiped": return TridentTransformer.transformModelBiped(PlayerMoveTransformer.transformModelBiped(basicClass));
            case "net.minecraft.entity.EntityLivingBase": return BreathingTransformer.transformEntityLivingBase(PlayerMoveTransformer.transformEntityLivingBase(TridentTransformer.transformEntityLivingBase(basicClass)));
            case "net.minecraft.entity.player.EntityPlayer": return PlayerMoveTransformer.transformEntityPlayer(TridentTransformer.transformEntityPlayer(basicClass));
            case "net.minecraft.client.renderer.entity.RenderLivingBase": return TridentTransformer.transformRenderLivingBase(basicClass);
            case "net.minecraft.client.renderer.entity.RenderPlayer": return PlayerMoveTransformer.transformRenderPlayer(TridentTransformer.transformRenderLivingBase(basicClass));
            case "net.minecraft.client.entity.EntityPlayerSP": return BubbleColumnTransformer.transformEntityPlayerSP(TridentTransformer.transformEntityPlayerSP(basicClass));

            // Pumpkin
            case "net.minecraft.block.BlockPane":
            case "net.minecraft.block.BlockWall": return PumpkinTransformer.transformBlockFenceLike(basicClass);
            case "net.minecraft.block.BlockStem": return PumpkinTransformer.transformBlockStem(basicClass);
            case "net.minecraft.stats.StatList": return PumpkinTransformer.transformStatList(basicClass);
            case "net.minecraft.world.gen.feature.WorldGenPumpkin": return PumpkinTransformer.transformWorldGenPumpkin(basicClass);

            // Fluidlogging
            case "net.minecraftforge.fluids.BlockFluidBase": return FluidloggingTransformer.transformBlockFluidBase(basicClass);
            case "net.minecraft.block.BlockLiquid": return FluidloggingTransformer.transformBlockLiquid(basicClass);

            // Debarking
            case "net.minecraft.client.renderer.block.statemap.BlockStateMapper": return LogTransformer.transformBlockStateMapper(basicClass);
            case "net.minecraftforge.fml.common.eventhandler.ASMEventHandler": return LogTransformer.transformASMEventHandler(basicClass);
            case "net.minecraftforge.registries.IForgeRegistryEntry$Impl": return LogTransformer.transformForgeRegistryEntry$Impl(basicClass);
            case "net.minecraft.block.Block": return LogTransformer.transformBlock(basicClass);
            case "net.minecraft.item.Item": return LogTransformer.transformItem(basicClass);
            case "net.minecraft.item.ItemBlock": return LogTransformer.transformItemBlock(basicClass);

            // For The Game Players
            case "net.minecraft.block.BlockBed": return IntentionalTransformerDesign.transformBlockBed(basicClass);

            // Biomes
            case "net.minecraft.world.biome.Biome": return BiomeTransformer.transformBiome(basicClass);

            // Item Entity Buoyancy
            case "net.minecraft.entity.item.EntityItem": return BuoyancyTransformer.transformEntityItem(basicClass);

            // Bubble Column
            case "net.minecraft.block.BlockSoulSand": return BubbleColumnTransformer.transformSoulSand(basicClass);
            case "net.minecraft.block.BlockMagma": return BubbleColumnTransformer.transformBlockMagma(basicClass);
            case "net.minecraft.entity.projectile.EntityThrowable": return BubbleColumnTransformer.transformEntityThrowable(basicClass);
            case "net.minecraft.entity.item.EntityBoat": return BubbleColumnTransformer.transformEntityBoat(basicClass);
            case "net.minecraft.client.renderer.entity.RenderBoat": return BubbleColumnTransformer.transformRenderBoat(basicClass);

            // Purple Shulker Box
            case "net.minecraft.block.BlockShulkerBox": return OurpleShulkerTransformer.transformBlockShulkerBox(basicClass);
            case "net.minecraft.entity.monster.EntityShulker": return OurpleShulkerTransformer.transformEntityShulker(basicClass);
            case "net.minecraft.client.renderer.tileentity.TileEntityShulkerBoxRenderer": return OurpleShulkerTransformer.transformTESRShulker(basicClass);
            case "net.minecraft.client.renderer.entity.RenderShulker": return OurpleShulkerTransformer.transformRenderShulker(basicClass);
            case "net.minecraft.client.renderer.entity.RenderShulker$HeadLayer": return OurpleShulkerTransformer.transformRenderShulker$HeadLayer(basicClass);

            // Random Fixes
            case "net.minecraft.block.BlockGrass":
            case "net.minecraft.block.BlockMycelium": return FixTransformer.transformBlockGrass(basicClass);
            case "net.minecraft.network.NetHandlerPlayServer": return FixTransformer.transformNetHandlerPlayServer(basicClass);

            // Breathing
            case "net.minecraftforge.client.GuiIngameForge": return BreathingTransformer.transformGuiIngameForge(basicClass);
        }
        // To Fix: Some AoA and DivineRPG logs are not BlockLogs
        if (!transformedName.startsWith("net.minecraftforge") && !transformedName.endsWith("$Debarked")) {
            boolean bewitchmentCheck = transformedName.equals("com.bewitchment.common.block.util.ModBlockPillar"); // Some mods like Bewitchment likes to create logs without extending BlockLog
            boolean techrebornCheck = transformedName.equals("techreborn.blocks.BlockRubberLog");
            boolean thaumcraftCheck = transformedName.equals("thaumcraft.common.blocks.world.plants.BlockLogsTC");
            String[] toCheck = new String[] { "net/minecraft/block/BlockLog", "com/progwml6/natura/common/block/BlockEnumLog" };
            if (bewitchmentCheck || techrebornCheck || thaumcraftCheck || LogTransformer.checkLogs(basicClass, transformedName, toCheck)) return LogTransformer.transformBlockLogEx(basicClass);
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
