package surreal.backportium.world.gen.feature;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class WorldGenSeaPickle extends WorldGenerator {

    @Override
    public boolean generate(@NotNull World worldIn, @NotNull Random rand, @NotNull BlockPos position) {
        return true;
    }
}
