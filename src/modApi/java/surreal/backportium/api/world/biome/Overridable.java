package surreal.backportium.api.world.biome;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;
import org.jetbrains.annotations.NotNull;
import surreal.backportium.api.annotations.Extension;
import surreal.backportium.api.annotations.NotYetImplemented;
import surreal.backportium.api.annotations.Unstable;

import java.util.Random;

/**
 * Expands {@link Biome} class to allow overriding methods such as {@link Biome#getTemperature(BlockPos)} and {@link Biome#generateBiomeTerrain(World, Random, ChunkPrimer, int, int, double)}.
 * - Overridable temperature allows frozen oceans to generate ices in patches.
 * - Overridable terrain blocks allows warm oceans to have sand at bottom or cold oceans to have sand/dirt at bottom.
 * Note: No, overriding {@link Biome#genTerrainBlocks(World, Random, ChunkPrimer, int, int, double)} is too much work for basic changes.
 */
@Extension(Biome.class)
public interface Overridable {

    @NotYetImplemented
    default float getTemperature(IBlockAccess world, Random random, BlockPos pos) { return Float.MIN_VALUE; }

    /**
     * Allows you to change {@link Biome#getDefaultTemperature()} values in {@link Biome#getTemperature(BlockPos)} based on BlockPos.
     * It is used for Frozen Oceans to have sparse ice placements.
     */
    default float getDefaultTemperature(BlockPos pos) { return ((Biome) this).getDefaultTemperature(); }

    /**
     * Runs everytime {@link ChunkPrimer#setBlockState(int, int, int, IBlockState)} runs inside {@link Biome#generateBiomeTerrain(World, Random, ChunkPrimer, int, int, double)}
     * This is used to replace oceans bottom layer for Warm Ocean and Lukewarm Ocean. Can be used to replace ice, bedrock, stone, sand, red sand too
     */
    @Unstable
    @NotNull
    default IBlockState getTerrainBlock(World world, Random random, ChunkPrimer primer, int chunkX, int chunkZ, BlockPos pos, double noiseVal, IBlockState defaultState) { return defaultState; }

    static Overridable cast(Biome biome) { return (Overridable) biome; }
}
