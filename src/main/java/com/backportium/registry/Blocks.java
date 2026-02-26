package com.backportium.registry;

import com.backportium.Tags;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public interface Blocks {

    void registerItem(ResourceLocation name, Block block, @Nullable Function<Block, Item> itemFunction);

    default void registerItem(String name, Block block, @Nullable Function<Block, Item> itemFunction) {
        registerItem(new ResourceLocation(Tags.MOD_ID, name), block, itemFunction);
    }

    default void registerItem(String name, Block block) {
        registerItem(name, block, ItemBlock::new);
    }

    default void register(ResourceLocation name, Block block) {
        registerItem(name, block, null);
    }

    default void register(String name, Block block) {
        register(new ResourceLocation(Tags.MOD_ID, name), block);
    }
}
