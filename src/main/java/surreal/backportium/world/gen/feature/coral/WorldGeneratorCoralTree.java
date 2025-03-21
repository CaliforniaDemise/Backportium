package surreal.backportium.world.gen.feature.coral;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import surreal.backportium.util.MutBlockPos;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class WorldGeneratorCoralTree extends WorldGeneratorCoral {

    @Override
    protected boolean generateCoral(World world, Random random, BlockPos pos, IBlockState state) {
        MutBlockPos mutable = new MutBlockPos(pos);
        int i = random.nextInt(3) + 1;

        for (int j = 0; j < i; j++) {
            if (!this.generateCoralPiece(world, random, mutable, state)) return true;
            mutable.move(EnumFacing.UP);
        }

        BlockPos blockPos = mutable.toImmutable();
        int k = random.nextInt(3) + 2;
        List<EnumFacing> list = Arrays.asList(EnumFacing.HORIZONTALS);
        Collections.shuffle(list, random);
        for (EnumFacing direction : list.subList(0, k)) {
            mutable.setPos(blockPos);
            mutable.move(direction);
            int l = random.nextInt(5) + 2;
            int m = 0;

            for (int n = 0; n < l && this.generateCoralPiece(world, random, mutable, state); n++) {
                m++;
                mutable.move(EnumFacing.UP);
                if (n == 0 || m >= 2 && random.nextFloat() < 0.25F) {
                    mutable.move(direction);
                    m = 0;
                }
            }
        }
        return true;
    }
}
