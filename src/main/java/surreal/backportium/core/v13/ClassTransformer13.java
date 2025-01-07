package surreal.backportium.core.v13;

public class ClassTransformer13 {

    public static byte[] transformClass(String transformedName, byte[] basicClass) {
        switch (transformedName) {

            case "net.minecraft.entity.EntityLivingBase": return BreathingTransformer.transformEntityLivingBase(basicClass);
            case "net.minecraftforge.client.GuiIngameForge": return BreathingTransformer.transformGuiIngameForge(basicClass);

            case "net.minecraft.world.biome.Biome": return BiomeTransformer.transformBiome(basicClass);

            // TODO Better Fluidlogging
            case "net.minecraftforge.fluids.BlockFluidBase": return WaterLoggingTransformer.transformBlockFluidBase(basicClass);
            case "net.minecraft.block.BlockLiquid": return WaterLoggingTransformer.transformBlockLiquid(basicClass);

            // TODO BWM support
            case "net.minecraft.entity.item.EntityItem": return BuoyancyTransformer.transformEntityItem(basicClass);

            case "net.minecraft.block.BlockPane":
            case "net.minecraft.block.BlockWall": return UncarvedPumpkinTransformer.transformBlockFenceLike(basicClass);
            case "net.minecraft.block.BlockStem": return UncarvedPumpkinTransformer.transformBlockStem(basicClass);
            case "net.minecraft.stats.StatList": return UncarvedPumpkinTransformer.transformStatList(basicClass);
            case "net.minecraft.world.gen.feature.WorldGenPumpkin": return UncarvedPumpkinTransformer.transformWorldGenPumpkin(basicClass);

            case "net.minecraft.block.BlockBed": return RandomTransformer.transformBlockBed(basicClass);
        }
        return basicClass;
    }
}
