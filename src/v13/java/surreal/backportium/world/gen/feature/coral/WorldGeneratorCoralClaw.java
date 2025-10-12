package surreal.backportium.world.gen.feature.coral;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class WorldGeneratorCoralClaw extends WorldGeneratorCoral {

    @Override
    protected boolean generateCoral(World world, Random random, BlockPos pos, IBlockState state) {
        if (!this.generateCoralPiece(world, random, pos, state)) return false;
        else {
            EnumFacing direction = EnumFacing.HORIZONTALS[random.nextInt(4)];
            int i = random.nextInt(2) + 2;
            List<EnumFacing> list = new ArrayList<>(3);
            list.add(direction);
            list.add(direction.rotateY());
            list.add(direction.rotateYCCW());
            Collections.shuffle(list, random);
            for (EnumFacing direction2 : list.subList(0, i)) {
                BlockPos.MutableBlockPos mutPos = new BlockPos.MutableBlockPos(pos);
                int j = random.nextInt(2) + 1;
                mutPos.move(direction2);
                int k;
                EnumFacing direction3;
                if (direction2 == direction) {
                    direction3 = direction;
                    k = random.nextInt(3) + 2;
                } else {
                    mutPos.move(EnumFacing.UP);
                    EnumFacing[] directions = new EnumFacing[] { direction2, EnumFacing.UP };
                    direction3 = directions[random.nextInt(2)];
                    k = random.nextInt(3) + 3;
                }
                for (int l = 0; l < j && this.generateCoralPiece(world, random, mutPos, state); l++) {
                    mutPos.move(direction3);
                }
                mutPos.move(direction3.getOpposite());
                mutPos.move(EnumFacing.UP);
                for (int l = 0; l < k; l++) {
                    mutPos.move(direction);
                    if (!this.generateCoralPiece(world, random, mutPos, state)) break;
                    if (random.nextFloat() < 0.25F) mutPos.move(EnumFacing.UP);
                }
            }
            return true;
        }
    }
}