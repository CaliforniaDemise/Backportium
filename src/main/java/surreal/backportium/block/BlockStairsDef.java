package surreal.backportium.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.state.IBlockState;

public class BlockStairsDef extends BlockStairs {

    public BlockStairsDef(IBlockState modelState) {
        super(modelState);
    }

    public BlockStairsDef(Block block) {
        super(block.getDefaultState());
    }
}
