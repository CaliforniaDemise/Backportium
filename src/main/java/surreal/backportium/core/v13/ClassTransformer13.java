package surreal.backportium.core.v13;

public class ClassTransformer13 {

    public static byte[] transformClass(String transformedName, byte[] basicClass) {
        switch (transformedName) {

            case "net.minecraft.entity.EntityLivingBase": return BreathingTransformer.transformEntityLivingBase(basicClass);
            case "net.minecraftforge.client.GuiIngameForge": return BreathingTransformer.transformGuiIngameForge(basicClass);

            case "net.minecraft.world.biome.Biome": return BiomeTransformer.transformBiome(basicClass);

            case "net.minecraftforge.fluids.BlockFluidBase": return WaterLoggingTransformer.transformBlockFluidBase(basicClass);
            case "net.minecraft.block.BlockLiquid": return WaterLoggingTransformer.transformBlockLiquid(basicClass);
        }
        return basicClass;
    }
}
