package surreal.backportium.item;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.client.model.ModelLoader;
import surreal.backportium.api.client.model.ModelProvider;
import surreal.backportium.util.RandomHelper;

import java.util.Objects;

/**
 * An Item that adds sub item for every blockstate of the block
 **/
public class ItemBlockSub extends ItemBlock implements ModelProvider {

    public ItemBlockSub(Block block) {
        super(block);
        this.setHasSubtypes(true);
    }

    @Override
    public int getMetadata(int damage) {
        return damage;
    }

    @Override
    public void registerModels() {
        for (IBlockState state : this.block.getBlockState().getValidStates()) {
            String variantIn = RandomHelper.getVariantFromState(state);
            int meta = state.getBlock().getMetaFromState(state);
            ModelLoader.setCustomModelResourceLocation(this, meta, new ModelResourceLocation(Objects.requireNonNull(this.getRegistryName()), variantIn + ",inventory"));
        }
    }
}
