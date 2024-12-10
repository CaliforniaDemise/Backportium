package surreal.backportium.item.v1_13;

import binnie.extratrees.wood.EnumETLog;
import binnie.extratrees.wood.EnumShrubLog;
import binnie.extratrees.wood.WoodManager;
import forestry.api.arboriculture.EnumForestryWoodType;
import forestry.api.arboriculture.EnumVanillaWoodType;
import forestry.api.arboriculture.IWoodType;
import forestry.arboriculture.IWoodTyped;
import forestry.arboriculture.WoodHelper;
import forestry.arboriculture.blocks.BlockForestryLog;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import surreal.backportium.util.RandomHelper;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

public class ItemBlockAddLog extends ItemBlock {

    protected final Block origLog;

    public ItemBlockAddLog(Block block, Block origLog) {
        super(block);
        this.origLog = origLog;
        this.setTranslationKey(origLog.getTranslationKey());
    }

    @Override
    public CreativeTabs getCreativeTab() {
        return this.getOrigItem().getCreativeTab();
    }

    @Nonnull
    @Override
    public CreativeTabs[] getCreativeTabs() {
        return this.getOrigItem().getCreativeTabs();
    }

    @Override
    @ParametersAreNonnullByDefault
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        NonNullList<ItemStack> ass = NonNullList.create();
        this.getOrigItem().getSubItems(tab, ass);
        ass.forEach(stack -> {
            ItemStack s = new ItemStack(this, 1, stack.getMetadata());
            s.setTagCompound(stack.getTagCompound());
            items.add(s);
        });
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
    public String getTranslationKey() {
        return this.getOrigItem().getTranslationKey();
    }

    @Nonnull
    @Override
    public String getTranslationKey(@Nonnull ItemStack stack) {
        return this.getOrigItem().getTranslationKey(stack);
    }

    protected Item getOrigItem() {
        return RandomHelper.getItemFromBlock(this.origLog);
    }

    public static class ItemBlockAddLogForestry extends ItemBlockAddLog {

        public ItemBlockAddLogForestry(Block block, Block origLog) {
            super(block, origLog);
        }

        @Nonnull
        @Override
        public String getItemStackDisplayName(@Nonnull ItemStack stack) {
            IWoodType type = this.getWoodType(stack);
            IWoodTyped typed = (BlockForestryLog<?>) this.origLog;
            String displayName = null;
            if (type instanceof EnumVanillaWoodType || type instanceof EnumForestryWoodType) displayName = WoodHelper.getDisplayName(typed, type);
            else if (type instanceof EnumETLog || type instanceof EnumShrubLog) displayName = WoodManager.getDisplayName(typed, type);
            if (displayName == null) displayName = "FORESTRY_NULL";
            String name = "tile.backportium.log_stripped";
            {
                ResourceLocation regName = Objects.requireNonNull(this.block.getRegistryName());
                if (regName.getPath().endsWith("_stripped_bark")) name = "tile.backportium.log_stripped_bark";
                else if (regName.getPath().endsWith("_bark")) name = "tile.backportium.log_bark";
            }
            return I18n.translateToLocalFormatted(name, displayName);
        }

        protected IWoodType getWoodType(ItemStack stack) {
            return ((BlockForestryLog<?>) this.origLog).getWoodType(stack.getMetadata());
        }
    }
}
