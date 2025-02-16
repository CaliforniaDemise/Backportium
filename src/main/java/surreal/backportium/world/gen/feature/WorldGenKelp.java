package surreal.backportium.world.gen.feature;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import surreal.backportium.block.ModBlocks;
import surreal.backportium.block.v13.BlockKelp;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Random;

public class WorldGenKelp extends WorldGenerator {

    private static final IBlockState KELP_STATE = ModBlocks.KELP.getStateFromMeta(1);

    @Override
    @ParametersAreNonnullByDefault
    public boolean generate(World worldIn, Random rand, BlockPos position) {
        if (rand.nextInt(9) > 6) return false;
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int i = 0; i < 32; i++) {
            pos.setPos(position.getX() + rand.nextInt(7) - rand.nextInt(7), position.getY() - 1, position.getZ() + rand.nextInt(7) - rand.nextInt(7));
            IBlockState state;
            for (state = worldIn.getBlockState(pos); (state.getMaterial() == Material.WATER || (!state.isSideSolid(worldIn, pos, EnumFacing.UP) && state.getBlock() != ModBlocks.KELP)) && pos.getY() > 0; state = worldIn.getBlockState(pos)) {
                pos.move(EnumFacing.DOWN);
            }
            if (state.getBlock() == ModBlocks.KELP) continue;
            if (!((BlockKelp) ModBlocks.KELP).canBlockStay(worldIn, pos, KELP_STATE)) continue;
            int kelpY = rand.nextInt(position.getY() - pos.getY()) - 1;
            if (kelpY < 2) continue;
            worldIn.setBlockState(pos.move(EnumFacing.UP), KELP_STATE);
            for (int a = 1; a < kelpY; a++) {
                if (a == kelpY - 1) worldIn.setBlockState(pos.move(EnumFacing.UP), ModBlocks.KELP.getDefaultState());
                else worldIn.setBlockState(pos.move(EnumFacing.UP), KELP_STATE);
            }
        }
        return true;
    }
}
