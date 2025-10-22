package surreal.backportium.world.biome;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeOcean;
import net.minecraft.world.chunk.ChunkPrimer;
import org.jetbrains.annotations.NotNull;
import surreal.backportium.api.world.biome.Overridable;
import surreal.backportium.api.world.biome.CustomWaterColor;

import java.util.Random;

public class BiomeOceanCold extends BiomeOcean implements Overridable, CustomWaterColor {

    public BiomeOceanCold(BiomeProperties properties) {
        super(properties);
        this.setActualWaterColor(0x3D57D6);
        this.setWaterFogColor(0x050533);
    }

    @Override
    public @NotNull IBlockState getTerrainBlock(World world, Random random, ChunkPrimer primer, int chunkX, int chunkZ, BlockPos pos, double noiseVal, IBlockState defaultState) {
        if (defaultState.getBlock() == Blocks.GRAVEL && noiseVal > 2.7) return Blocks.DIRT.getDefaultState();
        return Overridable.super.getTerrainBlock(world, random, primer, chunkX, chunkZ, pos, noiseVal, defaultState);
    }
}
