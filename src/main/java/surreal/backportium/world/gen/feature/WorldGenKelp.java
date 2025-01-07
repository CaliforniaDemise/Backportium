package surreal.backportium.world.gen.feature;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import surreal.backportium.block.ModBlocks;
import surreal.backportium.block.v13.BlockKelp;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Random;

public class WorldGenKelp extends WorldGenerator {

    private static final IBlockState KELP_STATE = ModBlocks.KELP.getDefaultState();

    @Override
    @ParametersAreNonnullByDefault
    public boolean generate(World worldIn, Random rand, BlockPos position) {
        if (rand.nextInt(18) != 0) return false;
        for (int i = 0; i < 32; i++) {
            BlockPos blockPos = position.add(rand.nextInt(7) - rand.nextInt(7), -1, rand.nextInt(7) - rand.nextInt(7));
            for (IBlockState state = worldIn.getBlockState(blockPos); state.getMaterial() == Material.WATER && blockPos.getY() > 0; state = worldIn.getBlockState(blockPos)) {
                blockPos = blockPos.down();
            }
            blockPos = blockPos.up();
            if (!((BlockKelp) ModBlocks.KELP).canBlockStay(worldIn, blockPos, KELP_STATE)) continue;
            int kelpY = rand.nextInt(position.getY() - blockPos.getY()); // TODO Can be negative / 0
            worldIn.setBlockState(blockPos, KELP_STATE);
            for (int a = 1; a < kelpY; a++) {
                worldIn.setBlockState(blockPos.add(0, a, 0), KELP_STATE);
            }
        }
        return true;
    }
}
