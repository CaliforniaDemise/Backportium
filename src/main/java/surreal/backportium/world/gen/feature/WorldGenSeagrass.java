package surreal.backportium.world.gen.feature;

import net.minecraft.block.BlockBush;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.common.BiomeDictionary;
import surreal.backportium.block.ModBlocks;
import surreal.backportium.block.plant.BlockPlantDouble;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Random;

public class WorldGenSeagrass extends WorldGenerator {

    private static final IBlockState SEAGRASS_STATE = ModBlocks.SEAGRASS.getDefaultState();

    @Override
    @ParametersAreNonnullByDefault
    public boolean generate(World worldIn, Random rand, BlockPos position) {
        if (worldIn.getHeight(position).getY() > position.getY()) return false;
        BlockPos blockPos = position.down();
        for (IBlockState iblockstate = worldIn.getBlockState(blockPos); iblockstate.getMaterial() == Material.WATER && blockPos.getY() > 0; iblockstate = worldIn.getBlockState(blockPos)) {
            blockPos = blockPos.down();
        }
        blockPos = blockPos.up();
        for (int i = 0; i < this.getChance(worldIn, blockPos); ++i) {
            BlockPos blockpos = blockPos.add(rand.nextInt(7) - rand.nextInt(7), 0, rand.nextInt(7) - rand.nextInt(7));
            if (this.isTall(worldIn, blockpos, rand) && ModBlocks.SEAGRASS_DOUBLE.canPlaceBlockAt(worldIn, blockpos)) {
                ((BlockPlantDouble) ModBlocks.SEAGRASS_DOUBLE).place(worldIn, blockpos, SEAGRASS_STATE);
            }
            else if (ModBlocks.SEAGRASS.canPlaceBlockAt(worldIn, blockpos)) {
                worldIn.setBlockState(blockpos, SEAGRASS_STATE, 2);
            }
        }
        return true;
    }

    private int getChance(World world, BlockPos pos) {
        Biome biome = world.getBiome(pos);
        if (BiomeDictionary.hasType(biome, BiomeDictionary.Type.COLD)) return 32;
        else return 48;
        // DEEP WARM 80
        // DEEP COLD 40
    }

    private boolean isTall(World world, BlockPos pos, Random rand) {
        boolean mid = rand.nextBoolean();
        if (mid) return rand.nextFloat() < 0.6F;
        else return rand.nextFloat() < 0.3F;
    }
}
