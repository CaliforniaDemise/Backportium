package com.backportium.registry;

import com.backportium.Tags;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;

public interface Potions {

    void register(Potion potion, ResourceLocation name);

    default void register(Potion potion, String name) {
        register(potion, new ResourceLocation(Tags.MOD_ID, name));
    }
}
