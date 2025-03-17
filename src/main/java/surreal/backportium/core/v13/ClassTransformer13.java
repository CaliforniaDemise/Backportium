package surreal.backportium.core.v13;

public class ClassTransformer13 {

    public static byte[] transformClass(String transformedName, byte[] basicClass) {
        switch (transformedName) {

            case "net.minecraft.entity.EntityLivingBase": return PotionTransformer.transformEntityLivingBase(TridentTransformer.transformEntityLivingBase(PlayerMoveTransformer.transformEntityLivingBase(BreathingTransformer.transformEntityLivingBase(basicClass))));
            case "net.minecraftforge.client.GuiIngameForge": return BreathingTransformer.transformGuiIngameForge(basicClass);

            case "net.minecraft.world.biome.Biome": return BiomeTransformer.transformBiome(basicClass);

            // TODO Implement actual waterlogging -- Waterlogging
            case "net.minecraftforge.fluids.BlockFluidBase": return WaterLoggingTransformer.transformBlockFluidBase(basicClass);
            case "net.minecraft.block.BlockLiquid": return WaterLoggingTransformer.transformBlockLiquid(WaterTransformer.transformBlockLiquid(basicClass));
            case "net.minecraft.world.WorldEntitySpawner": return WaterLoggingTransformer.transformWorldEntitySpawner(basicClass);

            // TODO Add BWM support -- Buoyancy
            case "net.minecraft.entity.item.EntityItem": return BuoyancyTransformer.transformEntityItem(basicClass);

            case "net.minecraft.block.BlockPane":
            case "net.minecraft.block.BlockWall": return UncarvedPumpkinTransformer.transformBlockFenceLike(basicClass);
            case "net.minecraft.block.BlockStem": return UncarvedPumpkinTransformer.transformBlockStem(basicClass);
            case "net.minecraft.stats.StatList": return UncarvedPumpkinTransformer.transformStatList(basicClass);
            case "net.minecraft.world.gen.feature.WorldGenPumpkin": return UncarvedPumpkinTransformer.transformWorldGenPumpkin(basicClass);

            case "net.minecraft.block.BlockBed": return RandomTransformer.transformBlockBed(basicClass);

            case "net.minecraft.client.entity.EntityPlayerSP": return TridentTransformer.transformEntityPlayerSP(BubbleColumnTransformer.transformEntityPlayerSP(basicClass));
            case "net.minecraft.block.BlockSoulSand": return BubbleColumnTransformer.transformSoulSand(basicClass);
            case "net.minecraft.block.BlockMagma": return BubbleColumnTransformer.transformBlockMagma(basicClass);
            case "net.minecraft.entity.projectile.EntityThrowable": return BubbleColumnTransformer.transformEntityThrowable(basicClass);
            case "net.minecraft.entity.item.EntityBoat": return BubbleColumnTransformer.transformEntityBoat(basicClass);
            case "net.minecraft.client.renderer.entity.RenderBoat": return BubbleColumnTransformer.transformRenderBoat(basicClass);

            // TODO Better item listing on search tab etc, fix default shulker box block particle errors, fix iron chests -- Purple Shulker
//            case "net.minecraft.block.BlockShulkerBox": return OurpleShulkerTransformer.transformBlockShulkerBox(basicClass);
//            case "net.minecraft.entity.monster.EntityShulker": return OurpleShulkerTransformer.transformEntityShulker(basicClass);
//            case "net.minecraft.client.renderer.tileentity.TileEntityShulkerBoxRenderer": return OurpleShulkerTransformer.transformTESRShulker(basicClass);
//            case "net.minecraft.client.renderer.entity.RenderShulker": return OurpleShulkerTransformer.transformRenderShulker(basicClass);
//            case "net.minecraft.client.renderer.entity.RenderShulker$HeadLayer": return OurpleShulkerTransformer.transformRenderShulker$HeadLayer(basicClass);
//            case "net.minecraft.init.Blocks": return OurpleShulkerTransformer.transformBlocks(basicClass);
//            case "net.minecraft.client.renderer.BlockModelShapes": return OurpleShulkerTransformer.transformBlockModelShapes(basicClass);
//            case "net.minecraft.client.renderer.RenderItem": return OurpleShulkerTransformer.transformRenderItem(basicClass);
//            case "net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer": return OurpleShulkerTransformer.transformTileEntityItemStackRenderer(basicClass);
            // Iron Chest
//            case "cpw.mods.ironchest.common.blocks.shulker.BlockIronShulkerBox": return OurpleShulkerTransformer.transformBlockIronShulkerBox(basicClass);
//            case "cpw.mods.ironchest.common.items.shulker.ItemIronShulkerBox": return OurpleShulkerTransformer.transformItemIronShulkerBox(basicClass);
//            case "cpw.mods.ironchest.common.core.IronChestBlocks": return OurpleShulkerTransformer.transformIronChestBlocks(basicClass);
//            case "cpw.mods.ironchest.common.core.IronChestBlocks$Registration": return OurpleShulkerTransformer.transformIronChestBlocks$Registration(basicClass);
//            case "cpw.mods.ironchest.common.items.shulker.ItemShulkerBoxChanger": return OurpleShulkerTransformer.transformItemShulkerBoxChanger(basicClass); TODO Unfinished
//            case "cpw.mods.ironchest.client.renderer.shulker.TileEntityIronShulkerBoxRenderer": return OurpleShulkerTransformer.transformTileEntityIronShulkerBoxRenderer(basicClass);

            case "net.minecraft.block.Block": return PotionTransformer.transformBlock(LogTransformer.transformBlock(basicClass)); // TODO Purple Shulker Box
            case "net.minecraft.item.Item": return LogTransformer.transformItem(basicClass); // TODO Purple Shulker Box
            case "net.minecraft.client.renderer.block.statemap.BlockStateMapper": return LogTransformer.transformBlockStateMapper(basicClass);
            case "net.minecraftforge.registries.ForgeRegistry": return LogTransformer.transformForgeRegistry(basicClass);
            case "net.minecraftforge.registries.IForgeRegistryEntry$Impl": return LogTransformer.transformForgeRegistryEntry$Impl(basicClass);
            case "net.minecraft.item.ItemBlock": return LogTransformer.transformItemBlock(basicClass);

            // TODO add crawling -- Player Movement
            case "net.minecraft.client.model.ModelBiped": return TridentTransformer.transformModelBiped(PlayerMoveTransformer.transformModelBiped(basicClass));
            case "net.minecraft.entity.player.EntityPlayer": return TridentTransformer.transformEntityPlayer(PlayerMoveTransformer.transformEntityPlayer(basicClass));
            case "net.minecraft.client.renderer.entity.RenderPlayer": return TridentTransformer.transformRenderPlayer(PlayerMoveTransformer.transformRenderPlayer(basicClass));

            case "net.minecraft.client.renderer.entity.RenderLivingBase": return TridentTransformer.transformRenderLivingBase(basicClass);

            case "net.minecraft.world.biome.Biome$BiomeProperties": return BiomeTransformer.transformBiomeProperties(basicClass);
            case "net.minecraft.world.biome.BiomeColorHelper$3": return BiomeTransformer.transformBiomeColorHelper$WaterColor(basicClass);
            case "net.minecraft.client.renderer.BlockFluidRenderer": return BiomeTransformer.transformBlockFluidRenderer(basicClass);
            case "net.minecraft.client.renderer.ItemRenderer": return BiomeTransformer.transformItemRenderer(basicClass);
            case "net.minecraft.client.renderer.EntityRenderer": return PotionTransformer.transformEntityRenderer(BiomeTransformer.transformEntityRenderer(PlayerMoveTransformer.transformEntityRenderer(basicClass)));
        }
        // TODO Proper Traverse, AoA and DivineRPG support
        if (!transformedName.startsWith("net.minecraftforge") && !transformedName.endsWith("$Debarked")) {
            boolean bewitchmentCheck = transformedName.equals("com.bewitchment.common.block.util.ModBlockPillar"); // Some mods like Bewitchment likes to create logs without extending BlockLog
            boolean techrebornCheck = transformedName.equals("techreborn.blocks.BlockRubberLog");
            boolean thaumcraftCheck = transformedName.equals("thaumcraft.common.blocks.world.plants.BlockLogsTC");
            String[] toCheck = new String[] { "net/minecraft/block/BlockLog", "com/progwml6/natura/common/block/BlockEnumLog" };
            if (bewitchmentCheck || techrebornCheck || thaumcraftCheck || LogTransformer.checkLogs(basicClass, transformedName, toCheck)) return LogTransformer.transformBlockLogEx(basicClass);
        }
        return basicClass;
    }
}
