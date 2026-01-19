package surreal.backportium.world.biome;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeOcean;
import net.minecraft.world.chunk.ChunkPrimer;
import org.jetbrains.annotations.NotNull;
import surreal.backportium.api.world.biome.Overridable;
import surreal.backportium.api.world.biome.BetterWaterColor;

import java.util.Random;

public class BiomeOceanLukewarm extends BiomeOcean implements Overridable, BetterWaterColor {

    public BiomeOceanLukewarm(BiomeProperties properties) {
        super(properties);
        this.setActualWaterColor(0x45ADF2);
        this.setWaterFogColor(0x041633);
    }

    @Override
    public @NotNull IBlockState getTerrainBlock(World world, Random random, ChunkPrimer primer, int chunkX, int chunkZ, BlockPos pos, double noiseVal, IBlockState defaultState) {
        if (defaultState.getBlock() == Blocks.GRAVEL) return Blocks.SAND.getDefaultState();
        return Overridable.super.getTerrainBlock(world, random, primer, chunkX, chunkZ, pos, noiseVal, defaultState);
    }
}
