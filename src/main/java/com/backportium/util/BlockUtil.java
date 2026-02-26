package com.backportium.util;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import static net.minecraft.init.Blocks.AIR;

public final class BlockUtil {

    @SuppressWarnings("deprecation")
    public static IBlockState getStateFromStack(ItemStack stack) {
        Block block = ItemUtil.getBlockFromItem(stack.getItem());
        if (block == AIR) return block.getDefaultState();
        return block.getStateFromMeta(stack.getMetadata());
    }

    @NotNull
    public static Item getItemFromBlock(Block block) {
        if (block == AIR) return Items.AIR;
        Item item = Item.getItemFromBlock(block);
        if (item == Items.AIR) item = ForgeRegistries.ITEMS.getValue(block.getRegistryName());
        if (item == null) item = Items.AIR;
        return item;
    }

    private BlockUtil() {}
}
