package surreal.backportium.world.biome;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeOcean;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraftforge.common.BiomeDictionary;
import org.jetbrains.annotations.NotNull;
import surreal.backportium._internal.world.biome.BiomeTypeProvider;
import surreal.backportium.api.biome.Overridable;
import surreal.backportium.api.world.biome.CustomWaterColor;

import java.util.Random;

public class BiomeOceanWarm extends BiomeOcean implements Overridable, CustomWaterColor, BiomeTypeProvider {

    public BiomeOceanWarm(BiomeProperties properties) {
        super(properties);
        this.setActualWaterColor(0x43D5EE);
        this.setWaterFogColor(0x041F33);
    }

    @Override
    public @NotNull IBlockState getTerrainBlock(World world, Random random, ChunkPrimer primer, int chunkX, int chunkZ, BlockPos pos, double noiseVal, IBlockState defaultState) {
        if (defaultState.getBlock() == Blocks.GRAVEL) return Blocks.SAND.getDefaultState();
        return Overridable.super.getTerrainBlock(world, random, primer, chunkX, chunkZ, pos, noiseVal, defaultState);
    }

    @Override
    public void addTypes() {
        BiomeDictionary.addTypes(this, BiomeDictionary.Type.HOT);
    }
}
