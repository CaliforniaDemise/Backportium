package surreal.backportium.world.gen.feature.coral;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import surreal.backportium.util.MutBlockPos;

import java.util.Random;

public class WorldGeneratorCoralMushroom extends WorldGeneratorCoral {

    @Override
    protected boolean generateCoral(World world, Random random, BlockPos pos, IBlockState state) {
        int i = random.nextInt(3) + 3;
        int j = random.nextInt(3) + 3;
        int k = random.nextInt(3) + 3;
        int l = random.nextInt(3) + 1;
        MutBlockPos mutable = new MutBlockPos(pos);
        for (int m = 0; m <= j; m++) {
            for (int n = 0; n <= i; n++) {
                for (int o = 0; o <= k; o++) {
                    mutable.setPos(m + pos.getX(), n + pos.getY(), o + pos.getZ());
                    mutable.move(EnumFacing.DOWN, l);
                    if ((m != 0 && m != j || n != 0 && n != i) && (o != 0 && o != k || n != 0 && n != i) && (m != 0 && m != j || o != 0 && o != k) && (m == 0 || m == j || n == 0 || n == i || o == 0 || o == k) && !(random.nextFloat() < 0.1F)) {
                        this.generateCoralPiece(world, random, mutable, state);
                    }
                }
            }
        }

        return true;
    }
}
