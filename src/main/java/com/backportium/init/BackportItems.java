package com.backportium.init;

import com.backportium.Tags;
import com.google.common.base.Preconditions;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

public final class BackportItems {

    public static void init() {}

    @NotNull
    private static Item get(ResourceLocation name) {
        Item item = ForgeRegistries.ITEMS.getValue(name);
        Preconditions.checkNotNull(item);
        return item;
    }

    @NotNull
    private static Item get(String name) {
        return get(new ResourceLocation(Tags.MOD_ID, name));
    }

    private BackportItems() {}
}
