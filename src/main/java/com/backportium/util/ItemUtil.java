package com.backportium.util;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlockSpecial;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import static net.minecraft.init.Blocks.AIR;

public final class ItemUtil {

    @NotNull
    public static Block getBlockFromItem(Item item) {
        Block block = Block.getBlockFromItem(item);
        if (block != AIR) return block;
        if (item instanceof ItemBlockSpecial) {
            ItemBlockSpecial itemBlock = (ItemBlockSpecial) item;
            return itemBlock.getBlock();
        }
        block = ForgeRegistries.BLOCKS.getValue(item.getRegistryName());
        if (block == null) block = AIR;
        return block;
    }

    private ItemUtil() {}
}
