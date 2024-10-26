package surreal.backportium.item.v1_13;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLog;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.oredict.OreDictionary;
import surreal.backportium.api.client.model.ModelProvider;
import surreal.backportium.api.item.OredictProvider;
import surreal.backportium.util.RandomHelper;

import javax.annotation.Nonnull;
import java.util.Objects;

public class ItemBlockDebarkedLog extends ItemBlock implements ModelProvider, OredictProvider {

    private final Block origLog;

    public ItemBlockDebarkedLog(Block block, Block origLog) {
        super(block);
        this.origLog = origLog;
    }

    @Override
    public boolean getHasSubtypes() {
        return this.getOrigItem().getHasSubtypes();
    }

    @Override
    public int getMetadata(int damage) {
        return this.getOrigItem().getMetadata(damage);
    }

    @Nonnull
    @Override
    public String getTranslationKey(@Nonnull ItemStack stack) {
        return this.getOrigItem().getTranslationKey(stack);
    }

    @Nonnull
    @Override
    public String getItemStackDisplayName(@Nonnull ItemStack stack) {
        return I18n.format("tile.backportium.debarked_log", super.getItemStackDisplayName(stack));
    }

    @Override
    public void registerModels() {
        for (IBlockState state : this.block.getBlockState().getValidStates()) {
            String variantIn = RandomHelper.getVariantFromState(state);
            int meta = state.getBlock().getMetaFromState(state);
            if (state.getValue(BlockLog.LOG_AXIS) == BlockLog.EnumAxis.Y) {
                ModelLoader.setCustomModelResourceLocation(this, meta, new ModelResourceLocation(Objects.requireNonNull(this.getRegistryName()), variantIn));
            }
        }
    }

    @Override
    public void registerOreEntries() {
        OreDictionary.registerOre("logWood", this);
    }

    private Item getOrigItem() {
        return RandomHelper.getItemFromBlock(this.origLog);
    }
}
