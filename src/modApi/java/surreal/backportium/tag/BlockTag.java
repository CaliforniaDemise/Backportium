package surreal.backportium.tag;

import it.unimi.dsi.fastutil.Hash;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import surreal.backportium.util.BlockUtil;

import java.util.Objects;

public class BlockTag extends Tag<IBlockState> {

    public BlockTag() {
        super(BLOCK_STRATEGY);
    }

    public void add(String name, Item item) {
        this.add(name, BlockUtil.getBlockFromItem(item));
    }

    public void add(String name, ItemStack stack) {
        if (stack.getMetadata() == OreDictionary.WILDCARD_VALUE) this.add(name, stack.getItem());
        else this.add(name, BlockUtil.getStateFromStack(stack));
    }

    public void add(String name, Block block) {
        block.getBlockState().getValidStates().forEach(s -> this.add(name, s));
    }

    public void remove(String name, Block block) {
        block.getBlockState().getValidStates().forEach(s -> this.remove(name, s));
    }

    private static final Hash.Strategy<IBlockState> BLOCK_STRATEGY = new Hash.Strategy<IBlockState>() {
        @Override
        public int hashCode(IBlockState state) {
            if (state == null) return 0;
            return Objects.hashCode(state.getBlock());
        }

        @Override
        public boolean equals(IBlockState a, IBlockState b) {
            if (a == null) return b == null;
            if (b == null) return false;
            return a == b;
        }
    };
}
