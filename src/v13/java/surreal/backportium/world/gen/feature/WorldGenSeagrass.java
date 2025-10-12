package surreal.backportium.world.gen.feature;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.common.BiomeDictionary;
import surreal.backportium.block.BlockPlant;
import surreal.backportium.init.ModBlocks;
import surreal.backportium.world.biome.BiomeOceanWarm;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Random;

public class WorldGenSeagrass extends WorldGenerator {

    private static final IBlockState SEAGRASS_STATE = ModBlocks.SEAGRASS.getDefaultState();

    @Override
    @ParametersAreNonnullByDefault
    public boolean generate(World worldIn, Random rand, BlockPos position) {
        if (worldIn.getHeight(position).getY() > position.getY()) return false;
        // SIMPLE
//        if (rand.nextFloat() < 1.0F / 10F) {
//            BlockPos.MutableBlockPos pos = new MutBlockPos();
//            pos.setPos(position);
//            IBlockState state;
//            while ((state = worldIn.getBlockState(pos.move(EnumFacing.DOWN))).getMaterial() == Material.WATER) {}
//            if (state.getBlock() == Blocks.STONE && worldIn.getBlockState(position).getMaterial() == Material.WATER && worldIn.getBlockState(position.up()).getMaterial() == Material.WATER) {
//
//            }
//        }
        // OTHER TYPES
        BlockPos.MutableBlockPos blockPos = new BlockPos.MutableBlockPos();
        for (int i = 0; i < this.getChance(worldIn, blockPos); ++i) {
            blockPos.setPos(position.getX() + rand.nextInt(16), position.getY() - 1, position.getZ() + rand.nextInt(16));
            for (IBlockState iblockstate = worldIn.getBlockState(blockPos); iblockstate.getMaterial() == Material.WATER && blockPos.getY() > 0; iblockstate = worldIn.getBlockState(blockPos)) {
                blockPos.move(EnumFacing.DOWN);
            }
            blockPos.move(EnumFacing.UP);
            if (this.isTall(worldIn, blockPos, rand) && ((BlockPlant) ModBlocks.SEAGRASS).canGrow(worldIn, blockPos, worldIn.getBlockState(blockPos), worldIn.isRemote)) {
                ((BlockPlant) ModBlocks.SEAGRASS).grow(worldIn, rand, blockPos, SEAGRASS_STATE);
            }
            else if (ModBlocks.SEAGRASS.canPlaceBlockAt(worldIn, blockPos)) {
                worldIn.setBlockState(blockPos, SEAGRASS_STATE, 2);
            }
        }
        return true;
    }

    private int getChance(World world, BlockPos pos) {
        Biome biome = world.getBiome(pos);
        if (biome instanceof BiomeOceanWarm) return 80;
        if (BiomeDictionary.hasType(biome, BiomeDictionary.Type.COLD)) {
            if (biome.getHeightVariation() <= -1.8F) return 40;
            return 32;
        }
        if (BiomeDictionary.hasType(biome, BiomeDictionary.Type.SWAMP)) return 48;
        return 48; // RIVER | NORMAL
    }

    private boolean isTall(World world, BlockPos pos, Random rand) {
        Biome biome = world.getBiome(pos);
        if (BiomeDictionary.hasType(biome, BiomeDictionary.Type.RIVER)) return rand.nextFloat() < 0.4F;
        else if (BiomeDictionary.hasType(biome, BiomeDictionary.Type.SWAMP)) return rand.nextFloat() < 0.6F;
        else if (BiomeDictionary.hasType(biome, BiomeDictionary.Type.OCEAN) && biome.getBaseHeight() <= -1.8F) return rand.nextFloat() < 0.8F;
        else return rand.nextFloat() < 0.3F;
    }
}
