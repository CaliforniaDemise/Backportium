package surreal.backportium.world.gen.feature;

import com.google.common.base.Predicate;
import net.minecraft.block.BlockStone;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraftforge.event.terraingen.OreGenEvent;
import net.minecraftforge.event.terraingen.TerrainGen;

import java.util.Random;

public class WorldGenStandardOre1 extends WorldGenMinable {

    private final int count;
    private final int minHeight;
    private final int maxHeight;

    public WorldGenStandardOre1(IBlockState state, int blockCount, int count, int minHeight, int maxHeight) {
        this(state, blockCount, count, minHeight, maxHeight, s -> s.getBlock() == Blocks.STONE && s.getValue(BlockStone.VARIANT).isNatural());
    }

    public WorldGenStandardOre1(IBlockState state, int blockCount, int count, int minHeight, int maxHeight, Predicate<IBlockState> p_i45631_3_) {
        super(state, blockCount, p_i45631_3_);
        this.count = count;
        this.minHeight = minHeight;
        this.maxHeight = maxHeight;
    }

    public void genStandardOre1(World worldIn, Random random, BlockPos pos) {
        if (TerrainGen.generateOre(worldIn, random, this, pos, OreGenEvent.GenerateMinable.EventType.CUSTOM)) {
            int minHeight = this.minHeight;
            int maxHeight = this.maxHeight;
            if (maxHeight < minHeight) {
                int i = minHeight;
                minHeight = maxHeight;
                maxHeight = i;
            }
            else if (maxHeight == minHeight) {
                if (minHeight < 255) {
                    ++maxHeight;
                }
                else {
                    --minHeight;
                }
            }
            for (int j = 0; j < this.count; ++j) {
                BlockPos blockpos = pos.add(random.nextInt(16), random.nextInt(maxHeight - minHeight) + minHeight, random.nextInt(16));
                this.generate(worldIn, random, blockpos);
            }
        }
    }}
