package com.backportium.registry;

import com.backportium.Tags;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

public interface Items {

    void register(ResourceLocation name, Item item);

    default void register(String name, Item item) {
        register(new ResourceLocation(Tags.MOD_ID, name), item);
    }
}
