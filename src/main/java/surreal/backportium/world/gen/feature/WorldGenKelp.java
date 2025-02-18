package surreal.backportium.world.gen.feature;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import surreal.backportium.block.ModBlocks;
import surreal.backportium.block.v13.BlockKelp;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Random;

public class WorldGenKelp extends WorldGeneratorFoliage {

    public WorldGenKelp(int noiseToCountRatio, double noiseFactor, double noiseOffset) {
        super(noiseToCountRatio, noiseFactor, noiseOffset);
    }

    @SuppressWarnings("deprecation")
    @Override
    @ParametersAreNonnullByDefault
    public boolean generate(World worldIn, Random rand, BlockPos position) {
        IBlockState kelp_state_up = ModBlocks.KELP.getDefaultState();
        IBlockState kelp_state_low = ModBlocks.KELP.getStateFromMeta(1);
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int i = 0; i < this.getCount(rand, position); i++) {
            pos.setPos(position.getX() + rand.nextInt(16), position.getY() - 1, position.getZ() + rand.nextInt(16));
            if (worldIn.getBlockState(pos).getMaterial() != Material.WATER) continue;
            int count = 0;
            IBlockState state;
            for (state = worldIn.getBlockState(pos); (state.getMaterial() == Material.WATER || !state.isSideSolid(worldIn, pos, EnumFacing.UP)) && pos.getY() > 0; state = worldIn.getBlockState(pos)) {
                pos.move(EnumFacing.DOWN);
                ++count;
            }
            int height = Math.min(count - 2, 1 + rand.nextInt(10));
            if (!((BlockKelp) ModBlocks.KELP).canBlockStay(worldIn, pos.move(EnumFacing.UP), kelp_state_up)) continue;
            for (int j = 0; j <= height; ++j) {
                if (j == height) worldIn.setBlockState(pos, kelp_state_up);
                else worldIn.setBlockState(pos, kelp_state_low);
                pos.move(EnumFacing.UP);
            }
        }
        return true;
    }
}
