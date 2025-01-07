package surreal.backportium.item.v13;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import surreal.backportium.api.enums.CoralType;
import surreal.backportium.block.plant.coral.BlockCoral;
import surreal.backportium.item.ItemBlockSub;

import javax.annotation.Nonnull;

public class ItemBlockCoral extends ItemBlockSub {

    public ItemBlockCoral(Block block) {
        super(block);
    }

    @Nonnull
    @Override
    public String getTranslationKey(@Nonnull ItemStack stack) {
        IBlockState state = block.getStateFromMeta(stack.getMetadata());
        CoralType type = state.getValue(BlockCoral.VARIANT);
        boolean alive = state.getValue(BlockCoral.ALIVE);
        String name = super.getTranslationKey();
        if (!alive) name += ".dead";
        return name + "." + type.getName();
    }
}
